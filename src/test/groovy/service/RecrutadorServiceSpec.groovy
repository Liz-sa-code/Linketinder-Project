package service

import repository.CandidatoInterfaceRepository
import repository.EmpresaRepositoryInterface
import repository.MatchRepository
import repository.RecrutadorRepositoryInterface
import repository.VagaRepositoryInterface
import database.ConnectionProvider
import security.AuthPrincipal
import security.AuthSessionStore
import spock.lang.Specification

class RecrutadorServiceSpec extends Specification {

    def "lists only anonymous candidates scoped to the recruiter's company and permissions"() {
        given:
        CandidatoInterfaceRepository candidatos = Mock()
        EmpresaRepositoryInterface empresas = Mock()
        RecrutadorRepositoryInterface recrutadores = Mock()
        VagaRepositoryInterface vagas = Mock()
        MatchRepository matches = new MatchRepository(Mock(ConnectionProvider))
        AuthSessionStore sessoes = new AuthSessionStore()
        AuthService auth = new AuthService(
                candidatos,
                empresas,
                recrutadores,
                sessoes
        )
        AuthPrincipal principal = new AuthPrincipal(
                'recrutador',
                12L,
                3L
        )
        String token = sessoes.criar(principal)
        RecrutadorService service = new RecrutadorService(
                recrutadores,
                candidatos,
                vagas,
                matches,
                auth
        )

        when:
        List<Map> perfis = service.listarCandidatosAnonimos(principal, token)

        then:
        1 * candidatos.listarPerfisAnonimosPorEmpresa(3L) >> [[
                candidatoId: 99L,
                descricao: 'Experiência profissional',
                competencias: ['Groovy'],
                formacoes: ['Ciência da Computação — Universidade fictícia — Graduação']
        ]]
        perfis.size() == 1
        perfis.first().keySet() == ['referencia', 'descricao', 'competencias', 'formacoes'] as Set
        perfis.first().descricao == 'Experiência profissional'
        perfis.first().competencias == ['Groovy']
        perfis.first().formacoes == ['Ciência da Computação — Universidade fictícia — Graduação']
        !perfis.first().containsKey('candidatoId')
    }

    def "does not allow a recruiter to resolve another recruiter's profile reference"() {
        given:
        AuthSessionStore sessoes = new AuthSessionStore()
        String tokenA = sessoes.criar(new AuthPrincipal('recrutador', 1L, 10L))
        String tokenB = sessoes.criar(new AuthPrincipal('recrutador', 2L, 10L))
        String referencia = sessoes.criarReferenciaCandidato(tokenA, 42L)

        expect:
        sessoes.resolverReferenciaCandidato(tokenA, referencia) == 42L
        sessoes.resolverReferenciaCandidato(tokenB, referencia) == null
    }
}
