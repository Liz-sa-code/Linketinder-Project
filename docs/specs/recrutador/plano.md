# Plano: Recrutador e Match Anônimo

**Status:** concluído e validado; pronto para entrega

Este plano implementa somente o escopo aprovado em
[spec.md](./spec.md). Os status abaixo registram o andamento real; não marque
uma tarefa como concluída antes de validar seus critérios.

## Restrições de execução

- Implementar uma tarefa por vez e atualizar seu status para `em andamento`
  antes de começar e para `concluída` somente depois da validação.
- Cada tarefa de implementação deve atualizar obrigatoriamente
  `CHANGELOG.md`, em `Não lançado`, e registrar o andamento neste plano.
- Não versionar credenciais, arquivos `.env` ou segredos. Usar dados fictícios
  em exemplos e testes.
- Manter o anonimato permanente no backend e nas consultas, não somente na
  interface. Não retornar identidade nem após o match.
- Não criar cadastro ou integração para formação e experiência. Não definir
  neste trabalho regras de interesses repetidos/cancelados, vagas encerradas ou
  desativação/desvinculação de Recrutadores.
- Não fazer commits sem autorização explícita.

## Tarefas

### 1. Preparar o schema e os modelos do domínio

- **Status:** concluída
- **Camadas:** `database`, `model`
- **Arquivos previstos:**
  - `database/schema.sql`
  - `src/main/groovy/model/Recrutador.groovy`
  - modelos de permissão por vaga, interesse e match em
    `src/main/groovy/model/`
  - `CHANGELOG.md`
  - este `plano.md`
- **Trabalho:** adicionar a persistência de Recrutador, permissões explícitas
  por Recrutador/vaga, interesses contextualizados pela vaga e match por
  candidato/vaga/Recrutador/empresa. Incluir os vínculos e restrições de
  integridade necessários. Corrigir somente as vírgulas finais inválidas em
  `candidatos` e `empresas` que impedem a aplicação atual do schema. Incluir
  armazenamento de hash de senha para os atores que precisam autenticar, nunca
  senha em texto puro.
- **Dependências:** nenhuma.
- **Validação:** aplicar o schema em banco de teste PostgreSQL; verificar
  chaves estrangeiras, integridade empresa-vaga-Recrutador e unicidade do
  match pela combinação aprovada. Confirmar que não foi adicionada modelagem
  para cadastro ou integração de formação/experiência.
- **Resultado:** adicionados os modelos Groovy de Recrutador, permissão,
  interesse de candidato, interesse de Recrutador e match anônimo. O schema
  agora contém as relações e chaves compostas para garantir a associação do
  Recrutador e da vaga à mesma empresa; candidatos e empresas têm coluna
  `senha_hash`, e Recrutadores exigem essa coluna. Corrigidas somente as
  vírgulas finais inválidas em `candidatos` e `empresas`.
- **Validação executada:** `./gradlew test` passou. `database/schema.sql` e
  `database/inserts.sql` foram aplicados com sucesso em PostgreSQL 18
  temporário.

### 2. Implementar persistência JDBC

- **Status:** concluída
- **Camadas:** `repository`, `database`
- **Arquivos previstos:**
  - novos repositórios e interfaces em `src/main/groovy/repository/` para
    Recrutador, permissões, interesses e matches
  - `src/main/groovy/repository/CandidatoRepository.groovy`
  - testes correspondentes em `src/test/groovy/repository/`
  - `src/main/groovy/repository/MatchRepository.groovy`
  - `CHANGELOG.md`
  - este `plano.md`
- **Trabalho:** persistir e consultar os registros do domínio usando JDBC e os
  padrões existentes de `ConnectionProvider`. Criar consultas específicas
  para os fluxos anônimos que selecionem somente os campos profissionais
  permitidos; não reutilizar consultas que retornam PII.
- **Dependências:** tarefa 1.
- **Validação:** testes de repositório cobrem gravação e leitura dos vínculos
  aprovados; consultas de perfil anônimo não selecionam nome, sobrenome,
  e-mail, CPF, nascimento, país ou CEP.
- **Resultado:** persistência JDBC para Recrutador, permissões,
  interesses, matches e consultas anônimas implementada. As consultas de perfil
  e match retornam competências, descrição e formação já existente, sem
  selecionar campos identificáveis. Schema e `inserts.sql` foram aplicados em
  PostgreSQL temporário; a execução ponta a ponta validou perfil, formação,
  interesse, permissão e match.

### 3. Adicionar autenticação e autorização por escopo

- **Status:** concluída
- **Camadas:** `service`, `api/handler`, `database`
- **Arquivos previstos:**
  - serviços de autenticação e autorização em
    `src/main/groovy/service/`
  - componentes de principal/autenticação em
    `src/main/groovy/api/`
  - serviços e repositórios de `Candidato`, `Empresa` e `Recrutador`
    necessários ao login
  - `src/test/groovy/service/`
  - `CHANGELOG.md`
  - este `plano.md`
- **Trabalho:** estabelecer um principal autenticado para candidato,
  Recrutador e representante/administrador da empresa, usando hashes de senha e
  sem expor credenciais. Derivar do principal os IDs usados para cada ação. O
  representante/administrador só administra Recrutadores e permissões da
  própria empresa; o Recrutador só opera no vínculo da sua conta. Atribuição
  de empresa ao Recrutador deve ser validada no servidor, sem aceitar o ID
  enviado pelo cliente como prova de autorização.
- **Dependências:** tarefas 1 e 2.
- **Validação:** testes demonstram rejeição de solicitações não autenticadas,
  acesso entre empresas e tentativa de usar IDs de outro ator; nenhum teste,
  log ou resposta contém senha ou hash.
- **Resultado:** autenticação por token em memória e hash
  PBKDF2-HMAC-SHA256 implementados; operações derivam empresa e ator da sessão.
  Testes cobrem hashing e referências de perfil isoladas por sessão. A
  execução ponta a ponta confirmou rejeição de vaga não autorizada e tentativa
  de concessão de permissão entre empresas.

### 4. Implementar consultas anônimas e interesse do candidato

- **Status:** concluída
- **Camadas:** `repository`, `service`, `model`
- **Arquivos previstos:**
  - `src/main/groovy/repository/CandidatoRepository.groovy`
  - serviços de candidato, vaga e permissão em
    `src/main/groovy/service/`
  - DTO/projeção profissional anônima em `src/main/groovy/model/` ou no
    pacote equivalente já adotado pelo projeto
  - testes em `src/test/groovy/repository/` e
    `src/test/groovy/service/`
  - `CHANGELOG.md`
  - este `plano.md`
- **Trabalho:** listar para o Recrutador apenas candidatos que demonstraram
  interesse em alguma vaga da sua empresa, com perfil exclusivamente
  profissional. Exigir permissão específica para analisar uma vaga. Registrar
  o interesse do candidato na vaga autenticada, sem confiar em IDs de candidato
  recebidos do cliente. Exibir formação ou experiência somente se já estiverem
  disponíveis por um fluxo existente.
- **Dependências:** tarefas 2 e 3.
- **Validação:** testes cobrem elegibilidade do candidato, isolamento entre
  empresas, vaga sem permissão e ausência de dados identificáveis no resultado
  da consulta e no registro de interesse.
- **Resultado:** candidatos elegíveis são filtrados por interesse em
  qualquer vaga da empresa; a projeção inclui competências, descrição e
  formação existente. Interesses de candidato usam a identidade da sessão. A
  consulta de perfil expõe apenas referência opaca e dados profissionais pela
  API. O fluxo foi exercitado com PostgreSQL temporário.

### 5. Implementar interesse do Recrutador e criação do match

- **Status:** concluída
- **Camadas:** `repository`, `service`, `model`
- **Arquivos previstos:**
  - repositórios e modelos criados nas tarefas 1 e 2
  - novo serviço de match/interesse em `src/main/groovy/service/`
  - testes em `src/test/groovy/service/`
  - `CHANGELOG.md`
  - este `plano.md`
- **Trabalho:** permitir que um Recrutador autenticado e autorizado demonstre
  interesse no candidato no contexto da vaga da sua empresa. Criar e persistir
  o match quando os dois interesses aprovados corresponderem ao mesmo
  candidato e vaga. Manter o Recrutador específico como participante do match.
- **Dependências:** tarefas 2, 3 e 4.
- **Validação:** testes provam que cada interesse isolado não cria match; o
  interesse mútuo cria match ligado ao candidato, vaga, Recrutador e empresa;
  Recrutadores diferentes produzem matches distintos; a projeção do match
  continua anônima. Não testar nem definir comportamento de cancelamento ou
  repetição fora do que exige a integridade aprovada.
- **Resultado:** o repositório valida a permissão da vaga, o vínculo com
  a empresa e o interesse prévio do candidato, então persiste o interesse do
  Recrutador e cria o match em transação. As projeções de match são anônimas.
  O fluxo mútuo e as projeções para candidato e Recrutador foram exercitados
  contra PostgreSQL temporário.

### 6. Expor os fluxos pela API REST

- **Status:** concluída
- **Camadas:** `controller`, `api/handler`
- **Arquivos previstos:**
  - `src/main/groovy/api/handler/ApiServer.groovy`
  - handlers e controllers necessários em
    `src/main/groovy/api/handler/` e
    `src/main/groovy/controller/`
  - `src/main/groovy/Main.groovy`
  - testes de API em `src/test/groovy/`
  - `CHANGELOG.md`
  - este `plano.md`
- **Trabalho:** conectar autenticação, cadastro do Recrutador, gestão de
  permissões, consulta profissional anônima, interesses e consulta de matches
  aos serviços. As respostas destinadas a participantes devem usar somente
  projeções anônimas. Não criar endpoints de revelação de identidade.
- **Dependências:** tarefas 3, 4 e 5.
- **Validação:** testes de handler/API confirmam autenticação e autorização,
  payloads profissionais permitidos, rejeição de acesso indevido e ausência de
  PII em respostas, erros e logs.
- **Resultado:** rotas implementadas para cadastro e login, vagas,
  Recrutadores, permissões, interesses e matches. Há teste HTTP de perfil
  anônimo verificando que identidade não chega à resposta. O fluxo HTTP
  completo foi exercitado contra PostgreSQL temporário, incluindo escopos
  entre empresas e vagas.

### 7. Integrar o fluxo mínimo no frontend

- **Status:** concluída
- **Camadas:** `frontend`
- **Arquivos previstos:**
  - `frontend/src/main.ts`
  - `frontend/src/type.ts`
  - `frontend/src/data.ts`, somente se necessário para retirar o fluxo novo do
    armazenamento local demonstrativo
  - testes de frontend, se já houver estrutura compatível
  - `CHANGELOG.md`
  - este `plano.md`
- **Trabalho:** integrar cadastro/acesso necessário, permissões, consulta
  anônima, manifestação de interesses e consulta do match com a API. Manter
  fora do armazenamento e das telas destinadas ao outro participante qualquer
  dado identificável. Não tratar ocultação visual nem IDs do navegador como
  controle de acesso.
- **Dependências:** tarefa 6.
- **Validação:** `npm run build` passa; verificações da interface confirmam que
  os fluxos usam respostas da API e não exibem nem armazenam dados pessoais do
  outro participante.
- **Resultado:** formulários e painéis usam a API; a interface não
  utiliza `localStorage` e apresenta competências, descrição e formação
  disponível sem dados de identidade. `npm run build` passou.

### 8. Executar validação integrada da entrega

- **Status:** concluída
- **Camadas:** todas as camadas afetadas
- **Arquivos previstos:**
  - testes de backend e frontend criados/alterados nas tarefas anteriores
  - `CHANGELOG.md`
  - este `plano.md`
- **Trabalho:** executar a suíte pertinente, verificar o comportamento completo
  e marcar esta tarefa e as tarefas validadas como concluídas. Registrar no
  changelog somente as alterações efetivamente implementadas.
- **Dependências:** tarefas 1 a 7.
- **Validação:** executar `./gradlew clean test` na raiz e `npm run build` em
  `frontend/`; aplicar o schema e validar scripts SQL em banco de teste quando
  disponível. Registrar resultados e limitações sem declarar verificações não
  executadas.
- **Resultado:** `./gradlew clean test` passou e `cd frontend && npm
  run build` passou. A suíte cobre hash, isolamento de referências por sessão,
  projeção anônima de serviço e resposta HTTP sem dados identificáveis. Schema,
  dados de exemplo e fluxo ponta a ponta foram validados em PostgreSQL
  temporário.

## Checklist de validação da funcionalidade

- [x] A implementação atende à especificação aprovada e aos critérios de
      aceitação.
- [x] O Princípio do Anonimato permanente foi verificado nos fluxos e
      payload, inclusive depois do match.
- [x] As funcionalidades e contratos existentes foram preservados ou as
      mudanças compatíveis foram aprovadas e documentadas.
- [x] Os testes automatizados pertinentes foram executados; os novos
      comportamentos têm testes adequados.
- [x] Segurança e validação de dados foram verificadas nas entradas,
      autenticação, autorização, consultas, respostas, logs e frontend.
