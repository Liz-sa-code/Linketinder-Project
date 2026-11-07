import type { Candidato, Empresa, Vaga } from "./type";

const CHAVES = {
    candidatos: "linketinder_candidatos",
    empresas: "linketinder_empresas",
    vagas: "linketinder_vagas"
};

// Dados iniciais

const candidatosIniciais: Candidato[] = [
    {
        id: 1,
        nome: "Ana",
        sobrenome: "Souza",
        data_nascimento: "2002-05-15",
        email: "ana@example.com",
        cpf: "000.000.000-00",
        pais: "Brasil",
        cep: "45000-000",
        descricao: "Interesse em desenvolvimento web.",
        formacao: "Sistemas de Informação",
        competencias: ["TypeScript", "JavaScript", "HTML", "CSS"]
    },
    {
        id: 2,
        nome: "Bruno",
        sobrenome: "Lima",
        data_nascimento: "1999-08-20",
        email: "bruno@example.com",
        cpf: "111.111.111-11",
        pais: "Brasil",
        cep: "40000-000",
        descricao: "Interesse em backend e dados.",
        formacao: "Ciência da Computação",
        competencias: ["Python", "Java", "SQL"]
    },
    {
        id: 3,
        nome: "Carla",
        sobrenome: "Mendes",
        data_nascimento: "2001-03-10",
        email: "carla@example.com",
        cpf: "222.222.222-22",
        pais: "Brasil",
        cep: "01000-000",
        descricao: "Desenvolvimento de sistemas.",
        formacao: "Engenharia de Software",
        competencias: ["Java", "SQL", "Docker"]
    }
];

const empresasIniciais: Empresa[] = [
    {
        id: 1,
        nome: "Paçoca Tech",
        email: "rh@pacocatech.example",
        cnpj: "00.000.000/0001-00",
        pais: "Brasil",
        cep: "40000-000",
        descricao: "Empresa demonstrativa de tecnologia."
    }
];

const vagasIniciais: Vaga[] = [
    {
        id: 1,
        empresa_id: 1,
        nome: "Desenvolvedor Frontend Júnior",
        cidade: "Remoto",
        estado: "BA",
        descricao: "Desenvolvimento de interfaces web.",
        ativa: true,
        competencias: ["TypeScript", "JavaScript", "CSS"]
    },
    {
        id: 2,
        empresa_id: 1,
        nome: "Desenvolvedor Backend Júnior",
        cidade: "Salvador",
        estado: "BA",
        descricao: "Desenvolvimento de APIs e serviços.",
        ativa: true,
        competencias: ["Java", "SQL"]
    },
    {
        id: 3,
        empresa_id: 1,
        nome: "Estágio em Dados",
        cidade: "Remoto",
        estado: "BA",
        descricao: "Análise e preparação de dados.",
        ativa: true,
        competencias: ["Python", "SQL"]
    }
];

// Funções auxiliares

function ler<T>(chave: string, padrao: T[]): T[] {
    const salvo = localStorage.getItem(chave);

    if (salvo !== null) {
        try {
            return JSON.parse(salvo) as T[];
        } catch {
            localStorage.removeItem(chave);
        }
    }

    localStorage.setItem(chave, JSON.stringify(padrao));

    return padrao;
}

function salvar<T>(chave: string, dados: T[]): void {
    localStorage.setItem(chave, JSON.stringify(dados));
}

// Candidatos

export function listarCandidatos(): Candidato[] {
    return ler(CHAVES.candidatos, candidatosIniciais);
}

export function criarCandidato(candidato: Candidato): void {
    const candidatos = listarCandidatos();

    salvar(CHAVES.candidatos, [
        ...candidatos,
        candidato
    ]);
}

export function excluirCandidato(id: number): void {
    const candidatos = listarCandidatos();

    salvar(
        CHAVES.candidatos,
        candidatos.filter(candidato => candidato.id !== id)
    );
}

// Empresas

export function listarEmpresas(): Empresa[] {
    return ler(CHAVES.empresas, empresasIniciais);
}

export function criarEmpresa(empresa: Empresa): void {
    const empresas = listarEmpresas();

    salvar(CHAVES.empresas, [
        ...empresas,
        empresa
    ]);
}

export function excluirEmpresa(id: number): void {
    const empresas = listarEmpresas();

    salvar(
        CHAVES.empresas,
        empresas.filter(empresa => empresa.id !== id)
    );

    const vagas = listarVagas();

    salvar(
        CHAVES.vagas,
        vagas.filter(vaga => vaga.empresa_id !== id)
    );
}

// Vagas

export function listarVagas(): Vaga[] {
    return ler(CHAVES.vagas, vagasIniciais);
}

export function criarVaga(vaga: Vaga): void {
    const vagas = listarVagas();

    salvar(CHAVES.vagas, [
        ...vagas,
        vaga
    ]);
}

export function excluirVaga(id: number): void {
    const vagas = listarVagas();

    salvar(
        CHAVES.vagas,
        vagas.filter(vaga => vaga.id !== id)
    );
}