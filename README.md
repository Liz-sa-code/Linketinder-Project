# Linketinder

## Autora

**Liz Soares**

## Sobre o projeto

O **Linketinder** é um projeto desenvolvido em **Groovy** como parte do **ZG-Hero Project**.

A proposta do projeto é criar uma aplicação inspirada no funcionamento de plataformas como **LinkedIn** e **Tinder**, aproximando candidatos e empresas de acordo com suas informações e competências.

Nesta versão, o sistema possui candidatos e empresas pré-cadastrados e permite visualizar essas informações através de um menu no terminal.

## Tecnologias 

### Frontend
- TypeScript: tipagem e organização do código.
- Vite: servidor de desenvolvimento e build.
- HTML5: estrutura da página.
- CSS3: estilos e responsividade.
- Chart.js: gráfico de barras de competências.
- LocalStorage: persistência local dos dados de demonstração.

## Banco de daods
- PostgreSQL 18: Sistema gerenciador de banco de dados relacional (SGBD).
- SQL: Linguagem utilizada para criação das tabelas, inserção, consulta e manipulação de dados.
- dbdiagram.io: Ferramenta utilizada para elaboração do Diagrama Entidade-Relacionamento (DER).
## Funcionalidades

O sistema possui:

- Listagem de candidatos;
- Listagem de empresas;
- Exibição dos dados dos candidatos;
- Exibição dos dados das empresas;
- Menu de interação no terminal;
- Candidatos pré-cadastrados;
- Empresas pré-cadastradas;
- Utilização de listas para armazenamento dos dados

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