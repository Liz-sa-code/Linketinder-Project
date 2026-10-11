# Linketinder

## Autora

**Liz Soares**

## Sobre o projeto

O **Linketinder** é um projeto desenvolvido em **Groovy** como parte do **ZG-Hero Project**.

A proposta do projeto é criar uma aplicação inspirada no funcionamento de plataformas como **LinkedIn** e **Tinder**, aproximando candidatos e empresas de acordo com suas informações e competências.

Nesta versão, o sistema possui candidatos e empresas pré-cadastrados e permite visualizar essas informações através de um menu no terminal.

## Tecnologias utilizadas

- Groovy: desenvolvimento do backend.
- PostgreSQL: armazenamento e gerenciamento dos dados.
- JDBC: comunicação entre a aplicação e o banco de dados.
- Gradle: gerenciamento do projeto e execução dos testes.
- Spock: estrutura de testes automatizados, quando configurada no projeto.
- TypeScript: desenvolvimento do frontend.
- Vite: ferramenta de desenvolvimento e execução do frontend.
- Git e GitHub: versionamento e hospedagem do código-fonte.

## Funcionalidades

O backend possui operações de cadastro, consulta, atualização e exclusão (CRUD) para as principais entidades do sistema.

-- Candidatos
- Cadastro de candidatos.
- Listagem dos candidatos cadastrados.
- Atualização de informações.
- Exclusão de candidatos.
- Associação de competências aos candidatos.
- Os dados incluem nome, sobrenome, data de nascimento, e-mail, CPF, país, CEP, descrição e senha armazenada no campo destinado ao hash.

-- Empresas
- Cadastro de empresas.
- Listagem de empresas.
- Atualização de informações.
- Exclusão de empresas.
- Os dados incluem nome, CNPJ, e-mail, descrição, país, CEP e campo destinado ao hash da senha.

--Competências
- Cadastro de competências.
- Listagem das competências disponíveis.
- Atualização de competências.
- Exclusão de competências.

-- Vagas
- Cadastro de vagas vinculadas a empresas.
- Listagem de vagas.
- Atualização de informações das vagas.
- Exclusão de vagas.

-- Associação de competências exigidas.
- Banco de dados
- O sistema utiliza PostgreSQL, com acesso realizado diretamente por JDBC, sem utilizar JPA ou Hibernate.
- O esquema relacional está definido em database/schema.sql, enquanto os dados iniciais estão organizados em database/inserts.sql.
- O arquivo database/queries.sql reúne consultas SQL utilizadas no projeto.
## Rodar no terminal

1. Clonar o repositório

No terminal, execute:

git clone https://github.com/Liz-sa-code/Linketinder-Project.git
2. Entrar na pasta do projeto

   cd Linketinder-Project

3. Entrar na pasta dos arquivos Groovy
    
   cd src/main
4. Executar o programa

Execute:

groovy Main.groovy

Após executar o comando, o menu do Linketinder será exibido:

===== LINKETINDER =====

1 - Listar candidatos

2 - Listar empresas

0 - Sair

Escolha uma opção:

Digite uma das opções disponíveis e pressione Enter.