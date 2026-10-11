
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
                            senha_hash TEXT NOT NULL
);

CREATE TABLE empresas (
                          id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          nome VARCHAR(150) NOT NULL,
                          cnpj VARCHAR(18) NOT NULL UNIQUE,
                          email VARCHAR(150) NOT NULL UNIQUE,
                          descricao TEXT,
                          pais VARCHAR(100) NOT NULL,
                          cep VARCHAR(15) NOT NULL,
                          senha_hash TEXT NOT NULL
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
                       ativa BOOLEAN NOT NULL DEFAULT TRUE
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
