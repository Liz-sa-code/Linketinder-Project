ALTER TABLE public.candidatos
    ADD COLUMN IF NOT EXISTS senha_hash VARCHAR(255);

ALTER TABLE public.empresas
    ADD COLUMN IF NOT EXISTS senha_hash VARCHAR(255);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conrelid = 'public.vagas'::regclass
          AND conname = 'vagas_id_empresa_id_key'
    ) THEN
        ALTER TABLE public.vagas
            ADD CONSTRAINT vagas_id_empresa_id_key UNIQUE (id, empresa_id);
    END IF;
END;
$$;

CREATE TABLE IF NOT EXISTS public.recrutadores (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id BIGINT NOT NULL
        REFERENCES public.empresas(id) ON DELETE CASCADE,
    nome VARCHAR(150) NOT NULL,
    area_recrutamento VARCHAR(150) NOT NULL,
    descricao TEXT NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    data_nascimento DATE NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    UNIQUE (id, empresa_id)
);

CREATE TABLE IF NOT EXISTS public.permissoes_recrutador_vaga (
    recrutador_id BIGINT NOT NULL,
    vaga_id BIGINT NOT NULL,
    empresa_id BIGINT NOT NULL,
    criada_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (recrutador_id, vaga_id),
    FOREIGN KEY (recrutador_id, empresa_id)
        REFERENCES public.recrutadores(id, empresa_id) ON DELETE CASCADE,
    FOREIGN KEY (vaga_id, empresa_id)
        REFERENCES public.vagas(id, empresa_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.interesses_recrutador_candidato (
    recrutador_id BIGINT NOT NULL,
    candidato_id BIGINT NOT NULL
        REFERENCES public.candidatos(id) ON DELETE CASCADE,
    vaga_id BIGINT NOT NULL,
    empresa_id BIGINT NOT NULL,
    criada_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (recrutador_id, candidato_id, vaga_id),
    UNIQUE (recrutador_id, candidato_id, vaga_id, empresa_id),
    FOREIGN KEY (recrutador_id, empresa_id)
        REFERENCES public.recrutadores(id, empresa_id) ON DELETE CASCADE,
    FOREIGN KEY (vaga_id, empresa_id)
        REFERENCES public.vagas(id, empresa_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.matches_anonimos (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    candidato_id BIGINT NOT NULL,
    vaga_id BIGINT NOT NULL,
    recrutador_id BIGINT NOT NULL,
    empresa_id BIGINT NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (candidato_id, vaga_id, recrutador_id),
    FOREIGN KEY (candidato_id, vaga_id)
        REFERENCES public.curtidas_candidato_vaga(candidato_id, vaga_id)
        ON DELETE CASCADE,
    FOREIGN KEY (recrutador_id, candidato_id, vaga_id, empresa_id)
        REFERENCES public.interesses_recrutador_candidato(
            recrutador_id, candidato_id, vaga_id, empresa_id
        )
        ON DELETE CASCADE
);
