# Linketinder

## Autora

**Liz Soares**

## Sobre o projeto

O **Linketinder** é um projeto desenvolvido em **Groovy** como parte do **ZG-Hero Project**.

A proposta é criar uma aplicação inspirada no funcionamento de plataformas como **LinkedIn** e **Tinder**, aproximando candidatos e empresas de acordo com suas informações e competências.

O sistema possui operações de cadastro, consulta, atualização e exclusão de candidatos, empresas, vagas e competências, além de uma interface de interação pelo terminal e um frontend desenvolvido com TypeScript.

## Tecnologias utilizadas

- **Groovy:** desenvolvimento do backend.
- **PostgreSQL:** armazenamento e gerenciamento dos dados.
- **JDBC:** comunicação entre a aplicação e o banco de dados.
- **Gradle:** gerenciamento do projeto e execução dos testes.
- **Spock:** estrutura de testes automatizados.
- **TypeScript:** desenvolvimento do frontend.
- **Vite:** ferramenta de desenvolvimento e execução do frontend.
- **Chart.js:** criação de gráficos.
- **Git e GitHub:** versionamento e hospedagem do código-fonte.

## Funcionalidades

### Candidatos

- Cadastro de candidatos.
- Listagem dos candidatos cadastrados.
- Consulta de candidatos por ID.
- Atualização de informações.
- Exclusão de candidatos.
- Associação de competências aos candidatos.

Os dados incluem nome, sobrenome, data de nascimento, e-mail, CPF, país, CEP, descrição e senha armazenada em formato de hash.

### Empresas

- Cadastro de empresas.
- Listagem de empresas.
- Consulta de empresas por ID.
- Atualização de informações.
- Exclusão de empresas.

Os dados incluem nome, CNPJ, e-mail, descrição, país, CEP e senha armazenada em formato de hash.

### Competências

- Cadastro de competências.
- Listagem das competências disponíveis.
- Consulta por ID.
- Atualização de competências.
- Exclusão de competências.

### Vagas

- Cadastro de vagas vinculadas a empresas.
- Listagem de vagas.
- Consulta por ID.
- Atualização de informações das vagas.
- Exclusão de vagas.
- Associação de competências exigidas.

## Banco de dados

O sistema utiliza **PostgreSQL**, com acesso realizado diretamente por JDBC, sem utilizar JPA ou Hibernate.

O esquema relacional está definido em `database/schema.sql`, enquanto os dados iniciais estão organizados em `database/inserts.sql`.

O arquivo `database/queries.sql` reúne consultas SQL utilizadas no projeto.

As configurações de conexão são realizadas por meio das variáveis de ambiente:

- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`

## Padrões de Projeto (Design Patterns)

Nesta etapa do projeto, foi realizada uma refatoração com o objetivo de aplicar padrões de projeto, melhorar a organização do código e reduzir o acoplamento entre as classes.

### 1. Factory

**Classe:** `database/ConnectionFactory.groovy`

O padrão Factory foi utilizado para centralizar a criação dos provedores de conexão com o banco de dados.

A classe `ConnectionFactory` possui o método `criar()`, responsável por selecionar e instanciar a implementação de `ConnectionProvider` de acordo com o banco de dados informado.

Atualmente, a implementação disponível é a conexão com PostgreSQL, por meio da classe `ConexaoBanco`.

Essa abordagem facilita a inclusão de novos provedores de conexão futuramente, sem exigir alterações na lógica dos repositórios.

### 2. Singleton

**Classe:** `database/ConnectionFactory.groovy`

O padrão Singleton foi aplicado à classe `ConnectionFactory`, garantindo que exista apenas uma instância da fábrica durante a execução da aplicação.

Para isso, foi utilizado um atributo estático que armazena a instância única, além de um construtor privado e um método de acesso chamado `getInstancia()`.

Essa implementação centraliza o acesso à fábrica de conexões e evita a criação de múltiplas instâncias dela.

**Observação:** o Singleton é aplicado à fábrica, não à conexão JDBC propriamente dita. Cada operação pode obter sua própria conexão por meio do provedor.

### 3. Facade

**Classe:** `BancoDeDados.groovy`

O padrão Facade foi utilizado para simplificar o acesso às operações de persistência do sistema.

A classe `BancoDeDados` funciona como uma interface única entre o menu da aplicação e os diferentes repositórios.

Ela disponibiliza operações para candidatos, empresas, vagas e competências, delegando a execução aos respectivos repositórios.

Dessa forma, o menu não precisa conhecer os detalhes de implementação de cada repositório, reduzindo o acoplamento entre a interface e a camada de persistência.

### 4. Repository

**Pacote:** `repository`

O padrão Repository foi aplicado às classes:

- `CandidatoRepository`
- `EmpresaRepository`
- `VagaRepository`
- `CompetenciaRepository`

Cada repositório concentra as operações de acesso e manipulação dos dados de sua respectiva entidade.

As classes implementam interfaces próprias, separando os contratos de acesso aos dados de suas implementações.

Essa organização facilita a manutenção, reutilização e evolução da camada de persistência.

### 5. Injeção de Dependência

A injeção de dependência foi utilizada para fornecer o `ConnectionProvider` aos repositórios por meio de seus construtores.

Assim, os repositórios não precisam criar diretamente suas conexões ou depender de uma implementação específica.

A classe `Main` utiliza a `ConnectionFactory` para obter o provedor e o repassa aos repositórios.

Essa abordagem reduz o acoplamento e facilita a substituição de implementações e a realização de testes.

## Testes automatizados

O projeto utiliza **Spock** para a implementação de testes automatizados.

Foram incluídos testes para verificar o funcionamento da `ConnectionFactory`, incluindo:

- Retorno da mesma instância da fábrica.
- Criação de um provedor PostgreSQL.
- Rejeição de bancos de dados não suportados.

Para executar a suíte de testes, utilize:

```bash
./gradlew clean test
```

A execução deve apresentar `BUILD SUCCESSFUL` quando todos os testes forem concluídos com sucesso.

## Como executar o projeto

### 1. Clonar o repositório

```bash
git clone https://github.com/Liz-sa-code/Linketinder-Project.git
```

### 2. Entrar na pasta do projeto

```bash
cd Linketinder-Project
```

### 3. Configurar o banco de dados

Crie o banco PostgreSQL e execute os arquivos SQL:

- `database/schema.sql`
- `database/inserts.sql`

Configure as variáveis de ambiente `DB_URL`, `DB_USER` e `DB_PASSWORD` com os dados da sua instalação do PostgreSQL.

### 4. Executar os testes

```bash
./gradlew clean test
```

### 5. Executar a aplicação

A aplicação possui uma interface de interação pelo terminal. A execução deve ser realizada conforme a configuração de execução do projeto Groovy.

## Frontend

O frontend passou por uma reorganização para utilizar TypeScript com Vite, mantendo a implementação principal em TypeScript sem depender da estrutura React do template inicial.

As principais alterações foram:

- Organização das interfaces de dados em `type.ts`.
- Centralização das operações de cadastro, listagem e exclusão em `data.ts`.
- Implementação da navegação entre as telas em `main.ts`.
- Separação da estilização em `style.css`.
- Criação de telas para candidatos e empresas.
- Implementação de formulários para cadastro de candidatos, empresas e vagas.
- Inclusão de visualização de competências e formação dos candidatos.
- Implementação de gráfico com Chart.js para apresentar a distribuição de competências.

Atualmente, os dados utilizados pelo frontend são armazenados no `localStorage` do navegador. Essa implementação permite testar as funcionalidades da interface, mas não representa uma integração definitiva com o banco PostgreSQL.