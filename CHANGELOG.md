# Changelog

Registro das mudanças notáveis do Linketinder, seguindo o formato Keep a
Changelog.

## [Não lançado]

### Adicionado

- `AGENTS.md`: instruções para agentes sobre o projeto, sua arquitetura e o
  Princípio do Anonimato.
- `skills/feature-com-spec/SKILL.md`: fluxo adaptado para especificar, revisar,
  planejar, implementar e validar funcionalidades.
- Cadastro e autenticação de Recrutadores vinculados a empresas, com permissões
  explícitas por vaga.
- Registro de interesses de candidatos e Recrutadores e persistência de matches
  anônimos por candidato, vaga e Recrutador.
- Endpoints autenticados para cadastro, permissões, perfis profissionais
  anônimos, interesses e consulta de matches.
- Interface web conectada à API para cadastro, autenticação, vagas, permissões,
  interesses e matches.
- Hash de senhas PBKDF2-HMAC-SHA256 com salt individual.

### Alterado

- Consultas e projeções de perfil e match incluem somente dados profissionais
  permitidos, incluindo formação já persistida quando disponível.
- Frontend deixa de usar dados demonstrativos em `localStorage` nos fluxos
  implementados.
- API limitada ao loopback e respostas com `Cache-Control: no-store`.

### Corrigido

- Sintaxe SQL das tabelas `candidatos` e `empresas` para permitir aplicar o
  schema PostgreSQL.
- Escopo da consulta de candidatos para incluir quem demonstrou interesse em
  qualquer vaga da empresa do Recrutador, sem dispensar permissão específica
  para analisar uma vaga ou registrar interesse nela.

### Removido
