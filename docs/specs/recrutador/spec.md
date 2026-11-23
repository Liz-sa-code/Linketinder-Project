# Especificação: Recrutador e Match Anônimo

**Status:** versão final para revisão

**Escopo:** Recrutador, interesses candidato-vaga e match anônimo

Esta especificação registra regras aprovadas pelo usuário e diferencia o
comportamento existente do que ainda precisa ser implementado. Pendências não
devem ser tratadas como requisitos aprovados.

## 1. Contexto e objetivo

O Linketinder conecta candidatos e empresas a oportunidades com base em
competências. Esta funcionalidade adicionará o perfil de Recrutador vinculado
a uma empresa e um fluxo em que interesses mútuos de candidato e Recrutador
criam um match associado ao candidato, à vaga e à empresa responsável.

O anonimato é permanente. Nem a criação do match nem qualquer etapa posterior
revela ao outro participante dados de identidade do candidato ou do Recrutador.
O match representa exclusivamente o interesse mútuo entre candidato, vaga e
Recrutador.

## 2. Estado atual do projeto

### Implementado no backend Groovy

- CRUD de candidatos, empresas, vagas e competências, com persistência JDBC.
- Associação de competências a candidatos e vagas.
- Vaga vinculada a empresa.
- A API REST registra candidatos, empresas e vagas.

### Existente apenas em scripts SQL

- `database/schema.sql` declara `curtidas_candidato_vaga` e
  `curtidas_empresa_candidato`.
- `database/queries.sql` contém exemplos que combinam essas curtidas e retornam
  nomes. Não há fluxo correspondente no backend, tabela persistida de match,
  ou interesses de Recrutador.
- `formacoes` e `candidato_formacao` existem no schema, mas não há integração
  correspondente nos repositórios Groovy. O frontend também contém dados
  demonstrativos de formação, sem integração com o backend.
- Não há modelo ou integração de experiência no backend.

As tabelas de curtida atuais não implementam as regras aprovadas: a curtida
empresarial identifica uma empresa, não o Recrutador que manifestou interesse.

### Existente apenas no frontend

O frontend usa dados em `localStorage`, sem integração com a API. A interface
mascara visualmente alguns dados, mas os registros locais incluem dados
identificáveis; essa ocultação não é controle de acesso nem garantia de
anonimato. O fluxo funcional de interesses e matches ainda não existe.

### Não implementado

Não existem modelo, persistência, serviço, controlador ou endpoint de
Recrutador, autenticação/autorização de usuários, interesses implementados na
aplicação ou matches persistidos.

## 3. Regras de negócio aprovadas

### 3.1 Recrutador e permissões

1. Um Recrutador possui nome, área de recrutamento, descrição, e-mail, CPF,
   data de nascimento e senha.
2. Cada Recrutador está vinculado a uma empresa.
3. O Recrutador pode analisar candidatos e vagas dentro das permissões da
   empresa.
4. O Recrutador só pode consultar candidatos que demonstraram interesse em
   pelo menos uma vaga da sua empresa. A permissão explícita por vaga continua
   obrigatória para analisar uma vaga ou registrar nela o interesse do
   Recrutador.

### 3.2 Interesses e match

1. O candidato pode demonstrar interesse em uma vaga.
2. O Recrutador vinculado à empresa responsável pela vaga pode demonstrar
   interesse no candidato.
3. O interesse mútuo correspondente ao mesmo candidato e à vaga da empresa do
   Recrutador gera um match.
4. O match representa somente o interesse mútuo e é associado ao candidato, à
   vaga, ao Recrutador e à empresa responsável.

### 3.3 Anonimato permanente

1. Antes e depois do match, os dados profissionais do candidato visíveis ao
   Recrutador limitam-se a:
   - competências;
   - dados de formação acadêmica já disponíveis no sistema;
   - descrição profissional;
   - dados de experiência já disponíveis no sistema.
2. Formação e experiência são exibidas somente quando já existem dados
   disponíveis. Esta etapa não adiciona cadastros, fontes ou integrações para
   essas informações.
3. Nome, e-mail, CPF, data de nascimento, CEP e qualquer dado de identidade
   equivalente do candidato ou do Recrutador nunca são revelados ao outro
   participante, inclusive após o match.
4. O match não altera quais dados profissionais podem ser exibidos e não inicia
   nenhum fluxo de consentimento, autorização de revelação ou revogação de
   identidade.

## 4. Modelo de dados

O modelo abaixo descreve entidades e relações exigidas; não define ainda um
schema SQL ou nomes definitivos de colunas.

| Conceito | Dados/relação exigidos | Situação atual |
|---|---|---|
| Recrutador | Nome, área de recrutamento, descrição, e-mail, CPF, data de nascimento, senha e vínculo obrigatório a uma empresa | Não existe no modelo nem no schema |
| Candidato | Perfil profissional com competências, formação e experiência somente quando os dados já estão disponíveis, além da descrição profissional existente | Candidato, competências e `Pessoa.descricao` existem; formação aparece parcialmente no SQL e nos dados demonstrativos do frontend; experiência não está integrada ao backend. Não serão criados cadastros ou integrações novos |
| Vaga | Oportunidade pertencente à empresa responsável | Modelo e persistência existentes |
| Permissão de análise | Relação explícita entre Recrutador e vaga, concedida ou retirada por representante/administrador da empresa | Não existe; precisa ser implementada |
| Interesse do candidato | Relação entre candidato e vaga | Tabela SQL existente; sem operação Groovy/API |
| Interesse do Recrutador | Relação entre o Recrutador e o candidato, com vínculo à empresa e contexto da vaga correspondente | Não existe; a tabela empresa-candidato não identifica o Recrutador |
| Match | Um registro por candidato, vaga e Recrutador participante, associado também à empresa responsável | Não existe; precisa ser persistido e ligado aos interesses correspondentes |

Cada match é identificado pela combinação de candidato, vaga e Recrutador. O
match também registra a empresa responsável pela vaga. A empresa vinculada ao
Recrutador deve ser a mesma empresa responsável pela vaga. O match representa
somente os interesses mútuos correspondentes; não contém estado de consentimento
nem mecanismo de divulgação de identidade.

As permissões de análise devem ser representáveis por Recrutador e vaga. A
permissão para analisar uma vaga não é herdada automaticamente de outras vagas
da empresa. Um representante/administrador da empresa pode conceder ou retirar
cada permissão. Uma empresa pode ter vários Recrutadores.

Um candidato que demonstrou interesse em qualquer vaga da empresa do
Recrutador é elegível para consulta, mesmo que essa vaga específica não esteja
autorizada para aquele Recrutador. A permissão explícita continua obrigatória
para analisar a vaga e registrar nela o interesse do Recrutador. A única relação
candidato-vaga considerada é o interesse explícito do candidato.

Formação e experiência só são exibidas quando já estiverem disponíveis no
sistema. Não serão criados cadastros nem integrações para preencher dados
ausentes nesta etapa.

A persistência deve garantir integridade entre os participantes, vaga,
empresa, permissões e interesses que formam o match. A forma física das tabelas
e restrições será detalhada no plano de implementação.

## 5. Fluxos principais

### 5.1 Cadastro e acesso do Recrutador

1. Cadastrar Recrutador com os campos aprovados e vinculá-lo a uma empresa.
2. Autenticar e identificar o Recrutador.
3. Consultar somente vagas explicitamente autorizadas para aquele Recrutador.
4. Consultar candidatos que tenham demonstrado interesse em qualquer vaga da
   empresa do Recrutador, retornando exclusivamente as informações profissionais
   permitidas.

A forma de autenticação será proposta como decisão técnica no plano; não foi
definida uma restrição de negócio adicional. O escopo inclui cadastrar a conta
do Recrutador, sem fluxo separado de ativação ou desativação.

### 5.2 Interesse e criação do match

1. O candidato registra interesse em uma vaga.
2. O Recrutador autorizado, vinculado à empresa responsável por essa vaga,
   registra interesse no mesmo candidato.
3. O sistema identifica os interesses correspondentes e cria um match único
   para o candidato, a vaga e o Recrutador, associado também à empresa.
4. A criação e a consulta do match mantêm ambas as identidades ocultas.

### 5.3 Consulta do match sob anonimato permanente

1. O candidato e o Recrutador consultam a existência e os dados profissionais
   permitidos do match; formação e experiência só são incluídas se já estiverem
   disponíveis no sistema.
2. A consulta não retorna dados de identidade de nenhum dos participantes.
3. Não há ação, endpoint ou estado de consentimento para revelar ou revogar
   identidades.
4. Dados de formação ou experiência ausentes não são cadastrados nem integrados
   por este fluxo.

## 6. Requisitos funcionais

- **RF-01 — Cadastro:** permitir cadastrar Recrutador com os campos aprovados e
  vínculo a uma empresa.
- **RF-02 — Permissões:** limitar a consulta de vagas às permissões explícitas
  do Recrutador para cada vaga e limitar candidatos aos critérios definidos na
  seção 3.1.
- **RF-03 — Perfil profissional anônimo:** antes e depois do match, retornar ao
  Recrutador somente competências, formação, descrição profissional e
  experiência que já estejam disponíveis no sistema. A descrição reutiliza o
  campo `Pessoa.descricao`; não criar novos campos, cadastros ou integrações
  para esses dados nesta etapa.
- **RF-04 — Interesse do candidato:** registrar interesse de um candidato em
  uma vaga.
- **RF-05 — Interesse do Recrutador:** registrar interesse de um Recrutador em
  um candidato, no contexto de uma vaga autorizada da empresa à qual está
  vinculado.
- **RF-06 — Match:** criar um match por candidato, vaga e Recrutador quando os
  interesses correspondentes forem mútuos, associado também à empresa
  responsável pela vaga.
- **RF-07 — Anonimato permanente:** manter candidato e Recrutador anônimos um
  para o outro antes e depois do match.
- **RF-08 — Ausência de revelação:** não oferecer consentimento, autorização,
  revogação ou endpoint que libere dados de identidade entre os participantes.
- **RF-09 — Respostas protegidas:** não retornar dados de identidade do
  candidato ou do Recrutador ao outro participante por consultas, endpoints,
  respostas da API ou qualquer outro fluxo, inclusive após o match.

## 7. Requisitos não funcionais

- **RNF-01 — Anonimato por padrão:** aplicar a restrição no backend e na camada
  de acesso a dados; ocultação na interface não é suficiente.
- **RNF-02 — Escopo de acesso:** validar no servidor a identidade do Recrutador
  e seu vínculo empresarial em toda operação. Exigir permissão explícita para
  consultar/analisar uma vaga e registrar interesse de Recrutador naquela vaga.
  A listagem de candidatos exige interesse em alguma vaga da empresa. Derivar
  o ator autenticado e seu escopo no servidor, sem confiar em identificadores
  de empresa, Recrutador ou candidato enviados pelo cliente como prova de
  autorização.
- **RNF-03 — Minimização de dados:** consultas, endpoints e DTOs usados para
  exibir um participante ao outro devem selecionar e serializar somente os
  campos profissionais permitidos, antes e depois do match. Não incluir dados
  de identidade em respostas da API, logs, erros, payloads ou objetos de perfil
  retornados por repositórios para esse fluxo.
- **RNF-04 — Proteção de credenciais:** a senha não deve ser exposta em
  respostas, logs ou dados de perfil. A solução técnica de autenticação e
  armazenamento seguro será proposta no plano; senha em texto puro não é
  aceitável.
- **RNF-05 — Integridade:** interesses e match devem permanecer vinculados às
  entidades correspondentes; o match não deve criar um fluxo de divulgação de
  identidade.
- **RNF-06 — Testabilidade:** adicionar testes automatizados para permissões,
  correspondência de interesses, anonimato permanente e não exposição de dados
  pessoais.
- **RNF-07 — Compatibilidade:** mudanças nos fluxos atuais devem ser explícitas
  e testadas; não ampliar exposição de dados dos candidatos em funcionalidades
  existentes.

## 8. Regras de anonimato permanente e proteção de dados

- O anonimato é permanente: nem o candidato nem o Recrutador podem obter dados
  de identidade um do outro, antes ou depois do match.
- Nome, e-mail, CPF, data de nascimento, CEP e quaisquer dados pessoais ou
  identificadores equivalentes não podem ser retornados ao outro participante
  por consultas, endpoints, respostas da API, erros, logs ou frontend.
- A proteção deve existir no backend e na camada de acesso a dados. Ocultar ou
  substituir valores visualmente no frontend não é suficiente.
- As consultas de perfil devem selecionar somente competências, formação
  acadêmica quando disponível, descrição profissional e experiências quando
  disponíveis. Não selecionar PII para removê-la depois na serialização.
- Endpoints, controllers, services, repositories, consultas SQL e payloads devem
  preservar esse limite em todos os estados do match.
- A autorização de acesso a vagas e candidatos continua sendo validada no
  backend pelo Recrutador autenticado, sua empresa, as permissões por vaga e a
  regra aprovada de interesse do candidato.
- Nenhum consentimento, autorização ou revogação de identidade pode ser criado
  para contornar ou suspender o anonimato permanente.
- O frontend não deve receber ou armazenar dados de identidade para exibir
  candidatos ou Recrutadores ao outro participante.

## 9. Critérios de aceitação

- [ ] O cadastro persiste todos os campos aprovados do Recrutador e associa-o a
      uma empresa.
- [ ] Um representante/administrador da empresa pode conceder ou retirar a
      permissão de um Recrutador para uma vaga da mesma empresa.
- [ ] O Recrutador não pode analisar uma vaga sem permissão explícita para ela.
- [ ] A listagem inclui candidatos com interesse em qualquer vaga da empresa do
      Recrutador, mesmo que essa vaga específica não esteja autorizada para ele;
      não inclui candidatos sem interesse da empresa ou ligados somente a vagas
      de outras empresas.
- [ ] A autorização de cada consulta é validada no backend com base na empresa,
      nas permissões da vaga e na elegibilidade do candidato.
- [ ] Antes e depois do match, as respostas de candidato contêm somente
      competências, dados de formação e experiência já disponíveis e descrição
      profissional.
- [ ] Dados de formação ou experiência ausentes não causam novos cadastros,
      fontes ou integrações nesta etapa.
- [ ] Nome, e-mail, CPF, data de nascimento, CEP e outros dados identificáveis
      nunca aparecem em consultas de perfil, endpoints, respostas da API, logs
      ou frontend apresentados ao outro participante.
- [ ] Interesse do candidato sozinho não cria match.
- [ ] Interesse do Recrutador sozinho não cria match.
- [ ] Interesses correspondentes do candidato e do Recrutador da empresa da vaga
      criam um match único por candidato, vaga e Recrutador, associado à
      empresa responsável pela vaga.
- [ ] Dois Recrutadores da mesma empresa que correspondam aos interesses do
      mesmo candidato e vaga têm matches distintos, cada um ligado ao respectivo
      Recrutador.
- [ ] A criação e a consulta do match não alteram a projeção profissional
      permitida nem revelam identidade.
- [ ] Não existem fluxo, endpoint ou estado de consentimento, autorização ou
      revogação para revelar identidade.
- [ ] Testes automatizados verificam acesso entre empresas, tentativa de
      acesso a vagas não autorizadas e exposição de dados pessoais via API,
      consultas, logs e frontend.
- [ ] Testes e implementação não introduzem regras de cardinalidade ou ciclo de
      vida fora do escopo atual.

Nenhum critério depende de uma decisão de negócio pendente. As decisões
técnicas de autenticação e persistência devem ser detalhadas no plano e
validadas sem ampliar as regras aprovadas.

## 10. Impactos na arquitetura atual

As camadas abaixo existem, mas as capacidades descritas nesta especificação
ainda precisam ser implementadas:

- **`model`:** criar o modelo de Recrutador, representar permissões explícitas
  por Recrutador/vaga, interesses, match por candidato/vaga/Recrutador e
  empresa responsável.
- **`repository`:** adicionar persistência JDBC e consultas com escopo de
  Recrutador/vaga; criar projeções profissionais anônimas. As consultas atuais
  de candidatos retornam nome, sobrenome, nascimento, e-mail, CPF, país e CEP,
  portanto não podem ser reutilizadas sem proteção.
- **`service`:** validar campos e permissões, coordenar os interesses e a
  criação do match, sem fluxo de divulgação de identidade.
- **`controller` e `api/handler`:** expor os fluxos necessários com autenticação
  e autorização. A API atual tem somente rotas POST de cadastro e não oferece
  autenticação. Será necessário identificar com segurança o candidato,
  Recrutador ou representante da empresa que executa cada ação.
- **`database`:** criar/alterar somente o schema necessário ao Recrutador,
  permissões, interesses e match conforme o modelo aprovado. Não adicionar
  cadastros ou integrações de formação/experiência nesta etapa. Os scripts
  existentes de curtidas são apenas SQL e não identificam o Recrutador.
- **`frontend`:** integrar o fluxo com a API e não receber/armazenar dados
  identificáveis para uma tela anônima. O `localStorage` atual e a ocultação
  visual não oferecem segurança.
- **`testes`:** cobrir regras de acesso, match e anonimato permanente em todos
  os fluxos afetados. Os testes atuais não cobrem esses comportamentos.

## 11. Fora do escopo

- Implementação de código nesta etapa de revisão; ela começa somente após a
  aprovação da especificação e do plano.
- Rankings, pontuação de compatibilidade, recomendação automática ou critérios
  de seleção por competências além de exibir os dados profissionais aprovados.
- Mensagens, chat, notificações ou contato automático após o match.
- Novos cadastros ou integrações de formação e experiência; serão usados
  somente os dados que já estiverem disponíveis no sistema.
- Regras para interesses repetidos ou cancelados, encerramento de vaga e
  desativação ou desvinculação de Recrutadores. Esses comportamentos ficam fora
  do escopo atual e serão definidos futuramente.
- Fluxo separado de ativação, desativação ou remoção de conta do Recrutador.
- Qualquer mecanismo para revelar dados de identidade ao outro participante,
  inclusive após o match.
- Refatorações não necessárias dos CRUDs atuais.

## 12. Decisões tomadas e dúvidas pendentes

### Decisões aprovadas

- Campos do Recrutador e vínculo a uma empresa.
- O acesso do Recrutador às vagas é explícito por Recrutador e vaga.
- Um representante/administrador da empresa concede ou retira permissões
  explícitas de análise de vaga; uma empresa pode ter vários Recrutadores.
- Um candidato que demonstrou interesse em qualquer vaga da empresa é elegível
  para consulta, mesmo se aquela vaga não estiver autorizada para o Recrutador.
- A única relação candidato-vaga considerada para elegibilidade é o interesse
  explícito do candidato.
- Direção dos interesses: candidato → vaga e Recrutador da empresa responsável
  → candidato.
- Interesse mútuo correspondente gera um match por candidato, vaga e
  Recrutador, associado à empresa responsável.
- Dados profissionais permitidos antes do match e dados identificáveis que
  permanecem ocultos.
- O anonimato é permanente, inclusive depois da criação do match; nenhum dado
  de identidade de candidato ou Recrutador é revelado ao outro participante.
- O match representa somente o interesse mútuo; não há consentimento ou
  autorização para revelar identidade.
- Formação e experiência são exibidas somente quando os dados já estão
  disponíveis; esta etapa não cria cadastros nem integrações para esses dados.
- A descrição profissional reutiliza o campo `descricao` já existente no perfil
  do candidato.
- Regras para interesses repetidos/cancelados, vagas encerradas e
  desativação/desvinculação de Recrutadores ficam fora do escopo atual e serão
  definidas futuramente.
- O cadastro do Recrutador está no escopo; não há fluxo separado de ativação ou
  desativação de conta nesta entrega.
- A autenticação será proposta como decisão técnica no plano, sem requisito de
  negócio adicional informado.

### Dúvidas pendentes

Nenhuma decisão de negócio pendente bloqueia o escopo mínimo desta
especificação. Os detalhes técnicos de autenticação e armazenamento seguro da
senha serão definidos no plano de implementação.

O plano está sujeito à revisão e aprovação junto com esta especificação. A
implementação só começa depois dessa aprovação.
