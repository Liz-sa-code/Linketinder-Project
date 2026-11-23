
CREATE TABLE candidatos (
                            id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            nome VARCHAR(100) NOT NULL,
                            sobrenome VARCHAR(100) NOT NULL,
                            data_nascimento DATE NOT NULL,
                            email VARCHAR(150) NOT NULL UNIQUE,
                            cpf VARCHAR(14) NOT NULL UNIQUE,
                            pais VARCHAR(100) NOT NULL,
                            cep VARCHAR(15) NOT NULL,
                            descricao TEXT,
                            senha_hash VARCHAR(255)

);

CREATE TABLE empresas (
                          id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          nome VARCHAR(150) NOT NULL,
                          cnpj VARCHAR(18) NOT NULL UNIQUE,
                          email VARCHAR(150) NOT NULL UNIQUE,
                          descricao TEXT,
                          pais VARCHAR(100) NOT NULL,
                          cep VARCHAR(15) NOT NULL,
                          senha_hash VARCHAR(255)
);

CREATE TABLE competencias (
                              id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                              nome VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE vagas (
                       id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       empresa_id BIGINT NOT NULL
                           REFERENCES empresas(id) ON DELETE CASCADE,
                       nome VARCHAR(150) NOT NULL,
                       descricao TEXT NOT NULL,
                       cidade VARCHAR(100) NOT NULL,
                       estado VARCHAR(100) NOT NULL,
                       ativa BOOLEAN NOT NULL DEFAULT TRUE,
                       UNIQUE (id, empresa_id)
);

CREATE TABLE recrutadores (
                              id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                              empresa_id BIGINT NOT NULL
                                  REFERENCES empresas(id) ON DELETE CASCADE,
                              nome VARCHAR(150) NOT NULL,
                              area_recrutamento VARCHAR(150) NOT NULL,
                              descricao TEXT NOT NULL,
                              email VARCHAR(150) NOT NULL UNIQUE,
                              cpf VARCHAR(14) NOT NULL UNIQUE,
                              data_nascimento DATE NOT NULL,
                              senha_hash VARCHAR(255) NOT NULL,
                              UNIQUE (id, empresa_id)
);

CREATE TABLE candidato_competencia (
                                       candidato_id BIGINT NOT NULL
                                           REFERENCES candidatos(id) ON DELETE CASCADE,
                                       competencia_id BIGINT NOT NULL
                                           REFERENCES competencias(id) ON DELETE CASCADE,
                                       PRIMARY KEY (candidato_id, competencia_id)
);

CREATE TABLE vaga_competencia (
                                  vaga_id BIGINT NOT NULL
                                      REFERENCES vagas(id) ON DELETE CASCADE,
                                  competencia_id BIGINT NOT NULL
                                      REFERENCES competencias(id) ON DELETE CASCADE,
                                  PRIMARY KEY (vaga_id, competencia_id)
);

CREATE TABLE formacoes (
                           id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                           nome VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE candidato_formacao (
                                    candidato_id BIGINT NOT NULL
                                        REFERENCES candidatos(id) ON DELETE CASCADE,
                                    formacao_id BIGINT NOT NULL
                                        REFERENCES formacoes(id) ON DELETE CASCADE,
                                    instituicao VARCHAR(150),
                                    nivel VARCHAR(100),
                                    PRIMARY KEY (candidato_id, formacao_id)
);

-- Curtidas de candidatos em vagas
CREATE TABLE curtidas_candidato_vaga (
                                         candidato_id BIGINT NOT NULL
                                             REFERENCES candidatos(id) ON DELETE CASCADE,
                                         vaga_id BIGINT NOT NULL
                                             REFERENCES vagas(id) ON DELETE CASCADE,
                                         criada_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         PRIMARY KEY (candidato_id, vaga_id)
);

-- Curtidas de empresas em candidatos
CREATE TABLE curtidas_empresa_candidato (
                                            empresa_id BIGINT NOT NULL
                                                REFERENCES empresas(id) ON DELETE CASCADE,
                                            candidato_id BIGINT NOT NULL
                                                REFERENCES candidatos(id) ON DELETE CASCADE,
                                            criada_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                            PRIMARY KEY (empresa_id, candidato_id)
);

CREATE TABLE permissoes_recrutador_vaga (
                                            recrutador_id BIGINT NOT NULL,
                                            vaga_id BIGINT NOT NULL,
                                            empresa_id BIGINT NOT NULL,
                                            criada_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                            PRIMARY KEY (recrutador_id, vaga_id),
                                            FOREIGN KEY (recrutador_id, empresa_id)
                                                REFERENCES recrutadores(id, empresa_id)
                                                ON DELETE CASCADE,
                                            FOREIGN KEY (vaga_id, empresa_id)
                                                REFERENCES vagas(id, empresa_id)
                                                ON DELETE CASCADE
);

CREATE TABLE interesses_recrutador_candidato (
                                                 recrutador_id BIGINT NOT NULL,
                                                 candidato_id BIGINT NOT NULL
                                                     REFERENCES candidatos(id) ON DELETE CASCADE,
                                                 vaga_id BIGINT NOT NULL,
                                                 empresa_id BIGINT NOT NULL,
                                                 criada_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                 PRIMARY KEY (recrutador_id, candidato_id, vaga_id),
                                                 UNIQUE (recrutador_id, candidato_id, vaga_id, empresa_id),
                                                 FOREIGN KEY (recrutador_id, empresa_id)
                                                     REFERENCES recrutadores(id, empresa_id)
                                                     ON DELETE CASCADE,
                                                 FOREIGN KEY (vaga_id, empresa_id)
                                                     REFERENCES vagas(id, empresa_id)
                                                     ON DELETE CASCADE
);

CREATE TABLE matches_anonimos (
                                 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                 candidato_id BIGINT NOT NULL,
                                 vaga_id BIGINT NOT NULL,
                                 recrutador_id BIGINT NOT NULL,
                                 empresa_id BIGINT NOT NULL,
                                 criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 UNIQUE (candidato_id, vaga_id, recrutador_id),
                                 FOREIGN KEY (candidato_id, vaga_id)
                                     REFERENCES curtidas_candidato_vaga(candidato_id, vaga_id)
                                     ON DELETE CASCADE,
                                 FOREIGN KEY (
                                     recrutador_id,
                                     candidato_id,
                                     vaga_id,
                                     empresa_id
                                 )
                                     REFERENCES interesses_recrutador_candidato(
                                         recrutador_id,
                                         candidato_id,
                                         vaga_id,
                                         empresa_id
                                     )
                                     ON DELETE CASCADE
);
