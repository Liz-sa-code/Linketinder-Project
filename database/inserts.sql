
INSERT INTO candidatos
(nome, sobrenome, data_nascimento, email, cpf,
 pais, cep, descricao, senha_hash)
VALUES
    ('Sandubinha', 'Silva', '2000-05-12',
     'sandubinha@example.com', '111.111.111-11',
     'Brasil', '45000-000', 'Desenvolvedor em formação',
     'HASH_FICTICIO_1'),

    ('Ana', 'Souza', '1998-03-20',
     'ana@example.com', '222.222.222-22',
     'Brasil', '40000-000', 'Desenvolvedora backend',
     'HASH_FICTICIO_2'),

    ('Carlos', 'Lima', '1995-11-08',
     'carlos@example.com', '333.333.333-33',
     'Brasil', '41000-000', 'Analista de sistemas',
     'HASH_FICTICIO_3'),

    ('Marina', 'Costa', '2001-07-15',
     'marina@example.com', '444.444.444-44',
     'Brasil', '44000-000', 'Desenvolvedora frontend',
     'HASH_FICTICIO_4'),

    ('Pedro', 'Alves', '1997-01-25',
     'pedro@example.com', '555.555.555-55',
     'Brasil', '43000-000', 'Engenheiro de software',
     'HASH_FICTICIO_5');

INSERT INTO empresas
(nome, cnpj, email, descricao, pais, cep, senha_hash)
VALUES
    ('Pastelsoft', '11.111.111/0001-11',
     'contato@pastelsoft.example',
     'Desenvolvimento de sistemas ERP',
     'Brasil', '45000-000', 'HASH_EMPRESA_1'),

    ('TechBahia', '22.222.222/0001-22',
     'contato@techbahia.example',
     'Soluções tecnológicas',
     'Brasil', '40000-000', 'HASH_EMPRESA_2'),

    ('CodeMais', '33.333.333/0001-33',
     'contato@codemais.example',
     'Desenvolvimento de software',
     'Brasil', '41000-000', 'HASH_EMPRESA_3'),

    ('NuvemDev', '44.444.444/0001-44',
     'contato@nuvemdev.example',
     'Sistemas em nuvem',
     'Brasil', '44000-000', 'HASH_EMPRESA_4'),

    ('InovaSoft', '55.555.555/0001-55',
     'contato@inovasoft.example',
     'Consultoria e inovação',
     'Brasil', '43000-000', 'HASH_EMPRESA_5');

INSERT INTO competencias (nome)
VALUES
    ('Python'),
    ('Java'),
    ('Groovy'),
    ('Angular'),
    ('PostgreSQL'),
    ('Spring Framework'),
    ('TypeScript'),
    ('SQL'),
    ('Git'),
    ('Comunicação');

INSERT INTO vagas
(empresa_id, nome, descricao, cidade, estado)
VALUES
    (1, 'Desenvolvedor Backend',
     'Desenvolvimento de APIs e sistemas ERP',
     'Vitória da Conquista', 'BA'),

    (1, 'Desenvolvedor Frontend',
     'Desenvolvimento de interfaces web',
     'Salvador', 'BA'),

    (2, 'Analista de Dados',
     'Consultas SQL e análise de dados',
     'Salvador', 'BA'),

    (3, 'Desenvolvedor Java',
     'Desenvolvimento de aplicações Java',
     'Feira de Santana', 'BA'),

    (4, 'Engenheiro de Software',
     'Desenvolvimento de sistemas em nuvem',
     'São Paulo', 'SP');

-- Competências dos candidatos
INSERT INTO candidato_competencia
(candidato_id, competencia_id)
VALUES
    (1, 1), (1, 2), (1, 5),
    (2, 2), (2, 3), (2, 6),
    (3, 5), (3, 8), (3, 9),
    (4, 4), (4, 7), (4, 10),
    (5, 1), (5, 2), (5, 9);

-- Competências exigidas pelas vagas
INSERT INTO vaga_competencia
(vaga_id, competencia_id)
VALUES
    (1, 2), (1, 3), (1, 5),
    (2, 4), (2, 7),
    (3, 5), (3, 8),
    (4, 2), (4, 6),
    (5, 1), (5, 2), (5, 9);

INSERT INTO formacoes (nome)
VALUES
    ('Ciência da Computação'),
    ('Sistemas de Informação'),
    ('Engenharia de Software'),
    ('Análise e Desenvolvimento de Sistemas');

INSERT INTO candidato_formacao
(candidato_id, formacao_id, instituicao, nivel)
VALUES
    (1, 1, 'Universidade fictícia', 'Graduação em andamento'),
    (2, 2, 'Universidade fictícia', 'Graduação'),
    (3, 4, 'Faculdade fictícia', 'Tecnólogo'),
    (4, 1, 'Universidade fictícia', 'Graduação'),
    (5, 3, 'Universidade fictícia', 'Graduação');
