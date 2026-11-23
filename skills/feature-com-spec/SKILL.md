# Skill: feature-com-spec

> Versão adaptada para o Linketinder. A skill original não foi encontrada nos
> arquivos disponíveis nem no histórico Git do repositório.

## Objetivo

Definir um processo rastreável para especificar, planejar, implementar e validar
uma funcionalidade sem inventar regras de negócio, ampliar o escopo ou
comprometer o Princípio do Anonimato.

## Quando usar

Use esta skill antes de implementar uma nova funcionalidade ou alteração
significativa que exija requisitos, decisões de negócio ou coordenação entre
camadas. Para correções pequenas e claramente especificadas, siga a tarefa e o
`AGENTS.md` sem criar documentação desnecessária.

## Perguntas antes da especificação

Antes de escrever a spec, investigue o comportamento e as estruturas
relacionadas no projeto. Se houver decisões de negócio indefinidas, faça
perguntas ao usuário e aguarde respostas antes de redigir a spec. Não transforme
suposições em requisitos. Registre decisões confirmadas e dúvidas pendentes
separadamente.

Interrompa e pergunte especialmente quando a decisão puder afetar privacidade,
dados revelados antes ou depois de um match, autorização, recrutadores ou o
Princípio do Anonimato.

## Artefatos e caminhos

Para cada funcionalidade, use uma pasta com nome curto em kebab-case:

- Especificação: `docs/specs/<nome-da-feature>/spec.md`
- Plano: `docs/specs/<nome-da-feature>/plano.md`

Não crie uma spec ou plano para o Recrutador até que isso seja solicitado e
autorizado. Não crie documentação fora do escopo da tarefa.

## Conteúdo obrigatório da spec

O arquivo `spec.md` deve conter estas seções:

1. **Contexto e objetivo:** problema, motivação e resultado esperado.
2. **Regras de negócio:** somente regras aprovadas, com exceções e limites
   relevantes. Indique explicitamente o que permanece indefinido.
3. **Modelo de dados:** entidades, campos, relações e migrações quando
   aplicável; caso contrário, registre que não há alteração de modelo prevista.
4. **Critérios de aceite:** resultados observáveis e verificáveis.
5. **Impactos nas funcionalidades existentes:** fluxos, camadas, contratos,
   dados ou comportamento que podem mudar, e compatibilidade esperada.
6. **Fora do escopo:** mudanças relacionadas que não serão feitas nesta tarefa.
7. **Princípio do Anonimato:** dados profissionais necessários, dados
   identificáveis que devem permanecer protegidos, participantes autorizados e
   condições de eventual revelação. Após o match, listar apenas dados cuja
   liberação tenha sido expressamente aprovada; não presuma essa lista.
8. **Decisões tomadas e dúvidas pendentes:** distinguir decisões confirmadas
   das perguntas ainda sem resposta.

## Revisão e aprovação

Apresente a spec para revisão antes de planejar a implementação. Confirme com o
usuário a aprovação e resolva as dúvidas pendentes que bloqueiem o trabalho.
Não inicie o plano nem a implementação antes dessa aprovação. Se houver
mudanças pedidas, atualize a spec e reapresente-a para aprovação.

## Conteúdo obrigatório do plano

Depois da aprovação da spec, crie `plano.md` na mesma pasta. Divida o trabalho
em tarefas pequenas, numeradas e executáveis incrementalmente. Cada tarefa deve
informar:

- número, descrição e status (`pendente`, `em andamento` ou `concluída`);
- arquivos previstos e camadas afetadas, como `model`, `repository`,
  `service`, `controller`, `view`, `api/handler`, `database` ou `frontend`;
- resultado esperado e critério de validação específico;
- testes relevantes e dependências entre tarefas, quando existirem.

Não inclua tarefas especulativas nem refatorações fora da spec. Atualize o
status no plano ao iniciar e concluir cada tarefa de implementação.

## Método obrigatório

Siga esta ordem, sem pular etapas:

**spec → revisão/aprovação → plano → implementação incremental → testes →
commits**

- **Spec:** investigue o projeto, esclareça requisitos e registre o escopo.
- **Revisão/aprovação:** aguarde aprovação explícita da spec.
- **Plano:** decomponha o trabalho aprovado em tarefas pequenas e verificáveis.
- **Implementação incremental:** faça uma tarefa do plano por vez, preserve
  alterações locais e evite mudanças fora do escopo.
- **Testes:** execute verificações pertinentes e compare os resultados com a
  spec e os critérios de aceite.
- **Commits:** faça commits pequenos e claros somente quando houver autorização
  explícita. Sem autorização, encerre após a validação e reporte o estado.

Cada tarefa de implementação deve obrigatoriamente atualizar `CHANGELOG.md` e
marcar seu andamento no `plano.md`. Se `CHANGELOG.md` ainda não existir ou sua
criação não estiver autorizada, peça autorização antes de iniciar a
implementação; não deixe a atualização para depois nem crie o arquivo fora do
escopo aprovado.

## Checklist obrigatório de validação

Antes de considerar a funcionalidade concluída, verifique e registre no plano:

- [ ] A implementação atende à spec aprovada e a todos os critérios de aceite.
- [ ] O Princípio do Anonimato foi preservado em cada fluxo e payload afetado.
- [ ] As funcionalidades existentes e seus contratos foram preservados ou as
      mudanças compatíveis foram aprovadas e documentadas.
- [ ] Os testes automatizados pertinentes foram executados; novos
      comportamentos têm testes adequados.
- [ ] Segurança e validação de dados foram verificadas nas entradas, camadas de
      acesso, respostas, logs e superfícies afetadas.

Para mudanças que envolvam candidatos, empresas, recrutadores, perfis, vagas ou
matches, inspecione handlers, endpoints, controllers, services, repositories,
SQL, payloads, logs e frontend. Ocultar dados na interface não garante
anonimato: a proteção precisa ocorrer também no backend e na camada de acesso
aos dados. Não exponha dados identificáveis antes de autorização aprovada.

## Convenções de execução

- Leia `AGENTS.md` e investigue o código relacionado antes de escrever a spec.
- Trabalhe uma tarefa de cada vez e mantenha o plano atualizado.
- Use dados fictícios em exemplos, fixtures e testes.
- Não versione credenciais, arquivos `.env` ou outros segredos.
- Reporte comandos, resultados, limitações e dúvidas sem alegar validações que
  não foram executadas.
