# Instruções para agentes

Leia este arquivo antes de investigar ou alterar o repositório. Ele descreve o
estado observado do projeto, não substitui os requisitos aprovados de cada
tarefa e não autoriza alterar funcionalidades por conta própria.

## Visão geral

Linketinder é uma plataforma acadêmica de conexão entre candidatos e empresas,
baseada em competências e oportunidades de trabalho. O repositório contém um
backend Groovy com persistência PostgreSQL/JDBC e uma aplicação frontend
TypeScript. O frontend atualmente mantém seus próprios dados demonstrativos no
`localStorage`; não está integrado ao backend.

## Stack tecnológica

- Backend: Groovy, executado e gerenciado com Gradle.
- Persistência principal: PostgreSQL via JDBC; o código também inclui driver e
provedor MySQL, mas `Main` seleciona PostgreSQL.
- Testes backend: Spock sobre a plataforma JUnit.
- Frontend: TypeScript e Vite. O `package.json` declara Chart.js, lucide-react e
Recharts como dependências, e o plugin React para Vite. O ponto de entrada
atual é `frontend/src/main.ts`, escrito em TypeScript sem React; `App.tsx`
permanece como tela de template.
- API REST: servidor baseado em `com.sun.net.httpserver.HttpServer`, na porta
8080, com endpoints POST para cadastro de candidatos, empresas e vagas. Ela
usa os controllers/services/repositories do backend.

## Arquitetura e estrutura

O backend está em `src/main/groovy/`:

- `model`: modelos de domínio (`Candidato`, `Empresa`, `Vaga`,
  `Competencia`, `Pessoa` e outros).
- `repository`: contratos e implementações de acesso JDBC aos dados.
- `service`: validações básicas de entrada e delegação para repositories.
- `controller`: delegação entre handlers/menu e services.
- `view`: menu interativo de terminal.
- `database`: fábrica e provedores de conexão.
- `api/handler`: servidor HTTP e handlers JSON para os endpoints existentes.

O fluxo normal é `view` ou `api/handler` -> `controller` -> `service` ->
`repository` -> `database`/JDBC. A montagem dessas dependências ocorre em
`Main.groovy`. O frontend vive em `frontend/`, e hoje funciona separadamente,
com estado no armazenamento local do navegador.

Padrões confirmados no código: Repository, injeção de dependência dos
provedores nos repositories e Factory/Singleton em `ConnectionFactory`.
Não presuma a existência de uma Facade `BancoDeDados`: ela é mencionada no
README, mas não está presente no código atual.

## Domínio e funcionalidades atuais

- **Candidato:** pessoa com nome, e-mail, documento, estado, CEP e descrição;
  o modelo também tem idade e competências. O backend oferece CRUD e
  associação de competências. A persistência atual usa nome/sobrenome,
  nascimento, e-mail, CPF, país, CEP e descrição.
- **Empresa:** pessoa com nome, e-mail e documento, com país e lista de
  competências no modelo. O backend oferece CRUD de dados empresariais.
- **Vaga:** no modelo Groovy tem título, descrição, localidade, salário,
  empresa e competências. Na persistência/backend os campos são empresa,
  nome, descrição, cidade, estado e ativa; existe associação de competências.
- **Competência:** entidade com ID e nome. O backend oferece CRUD e associa
  competências existentes a candidatos e vagas.

No banco, as tabelas relacionais `candidato_competencia` e `vaga_competencia`
representam as associações de competências. A tabela `vagas` referencia
`empresas`.

Distinga sempre as três superfícies:

- **Backend Groovy:** CRUD de candidato, empresa, vaga e competência,
  associações de competências e cadastro por API REST. Não há curtidas nem
  matches implementados no backend.
- **Somente scripts SQL:** `schema.sql` declara tabelas de formação e de
  curtidas; `queries.sql` contém exemplos de inserção de curtidas e consulta
  que combina registros de curtida. Isso não equivale a fluxo ou regra de
  negócio implementada na aplicação.
- **Somente frontend:** telas e dados demonstrativos para candidatos,
  empresas, vagas, competências/formação e exibição visual de anonimato. O
  estado fica no `localStorage`, sem integração efetiva com o banco/backend.

Não derive regras definitivas de match, desfazer curtida, escopo de curtida ou
revelação de identidade a partir dos scripts de exemplo. Pergunte quando uma
tarefa depender dessas decisões.

## PRINCÍPIO DO ANONIMATO — REGRA INVIOLÁVEL

Antes do match, candidatos e empresas não podem ter acesso a dados pessoais
identificáveis uns dos outros. Recrutadores também devem visualizar somente
dados profissionais dos candidatos antes do match, como competências, formação
e experiência. A avaliação deve usar somente informações profissionais
pertinentes.

Nome, CPF, e-mail pessoal, telefone, endereço, data de nascimento e dados
equivalentes não podem ser expostos antes do match. Nunca enfraqueça nem
contorne essa proteção. Ocultar campos visualmente no frontend não é suficiente:
o anonimato deve ser aplicado na camada de acesso aos dados e no backend.

Antes de implementar ou revisar qualquer fluxo relacionado a perfis, vagas,
curtidas ou matches:

1. Verifique handlers, endpoints, controllers, services, repositories,
   consultas SQL, payloads, logs e frontend quanto à exposição de identidade.
2. Não retorne dados identificáveis por API antes de uma autorização definida.
   Não introduza CPF, data de nascimento, e-mail pessoal ou outros dados
   sensíveis de candidatos em novas funcionalidades.
3. Depois do match, libere somente os dados expressamente permitidos por uma
   regra de negócio aprovada; não deduza quais dados devem ser liberados.
4. Inclua verificações de anonimato na implementação, nos testes e no code
   review.
5. Se uma decisão puder comprometer o anonimato, interrompa e pergunte antes
   de implementar.

O código atual ainda retorna dados identificáveis em consultas Groovy de
candidatos e os apresenta no menu. O frontend mascara alguns dados na
apresentação, mas mantém dados identificáveis em seu estado local. Trate isso
como estado existente a ser considerado com cuidado, nunca como garantia de
anonimato nem como autorização para ampliar a exposição.

## Banco de dados

Os scripts SQL ficam em `database/`:

- `schema.sql`: definição das tabelas e relações.
- `inserts.sql`: dados de exemplo.
- `queries.sql`: consultas de exemplo, incluindo consultas de competências,
  formação, curtidas e combinação de curtidas.

O backend usa PostgreSQL/JDBC. A conexão principal requer as variáveis de
ambiente `DB_URL`, `DB_USER` e `DB_PASSWORD`. O repositório não define tarefa
Gradle nem script próprio para aplicar SQL. Com `psql` instalado e um banco já
criado, os comandos abaixo são modelos: substitua os valores pelos dados do
ambiente e execute a carga inicial na ordem indicada.

```bash
psql -h <host> -U <usuario> -d <banco> -f database/schema.sql
psql -h <host> -U <usuario> -d <banco> -f database/inserts.sql
```

Confirme o esquema e os scripts antes de executá-los: o estado atual tem
vírgulas finais após colunas nas declarações de `candidatos` e `empresas` em
`schema.sql`, e `inserts.sql` referencia `senha_hash`, coluna ausente desse
schema. Há também divergências entre alguns campos do modelo e as tabelas.
Não corrija nem oculte essas inconsistências sem escopo aprovado.

## Como executar e testar

Na raiz do repositório:

```bash
./gradlew clean test
```

Na pasta `frontend/`:

```bash
npm run build
```

Com o PostgreSQL configurado por `DB_URL`, `DB_USER` e `DB_PASSWORD`, o
`application` do Gradle aponta para `Main`. Para iniciar o menu, use
`./gradlew run`; para iniciar o servidor REST, `Main` aceita o argumento
`api`, executado como `./gradlew run --args=api`. A API escuta na porta 8080.

## Convenções para agentes

- Leia este `AGENTS.md` antes de trabalhar e investigue o código e a
  documentação relevantes antes de propor ou fazer alterações.
- Siga a sequência **spec -> plano -> implementação**. Trabalhe uma tarefa de
  cada vez e marque o andamento no plano da tarefa; não crie arquivos de plano
  adicionais sem solicitação.
- Evite refatorações fora do escopo e preserve todas as alterações locais do
  usuário. Nunca reverta alterações que não fez.
- Nunca invente requisitos ou regras de negócio. Pergunte quando faltar uma
  decisão necessária, especialmente se puder afetar anonimato ou privacidade.
- Use dados fictícios em exemplos, fixtures e testes.
- Execute os testes e validações pertinentes e reporte com clareza comandos,
  resultados e limitações.
- Atualize `CHANGELOG.md` obrigatoriamente em cada tarefa de implementação.
- Nunca versione credenciais, arquivos `.env` ou outros segredos.
- Faça commits pequenos e claros somente quando houver autorização explícita.
