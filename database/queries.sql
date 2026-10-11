
SELECT
    c.id,
    c.nome,
    c.sobrenome,
    co.nome AS competencia
FROM candidatos c
         JOIN candidato_competencia cc
              ON cc.candidato_id = c.id
         JOIN competencias co
              ON co.id = cc.competencia_id
ORDER BY c.id, co.nome;

SELECT
    v.nome AS vaga,
    e.nome AS empresa,
    v.cidade,
    v.estado,
    co.nome AS competencia
FROM vagas v
         JOIN empresas e ON e.id = v.empresa_id
         LEFT JOIN vaga_competencia vc ON vc.vaga_id = v.id
         LEFT JOIN competencias co ON co.id = vc.competencia_id
ORDER BY v.id, co.nome;

SELECT
    c.nome || ' ' || c.sobrenome AS candidato,
    v.nome AS vaga,
    e.nome AS empresa,
    COUNT(*) AS competencias_compativeis
FROM candidatos c
         JOIN candidato_competencia cc
              ON cc.candidato_id = c.id
         JOIN vaga_competencia vc
              ON vc.competencia_id = cc.competencia_id
         JOIN vagas v ON v.id = vc.vaga_id
         JOIN empresas e ON e.id = v.empresa_id
GROUP BY c.id, c.nome, c.sobrenome,
         v.id, v.nome, e.nome
ORDER BY competencias_compativeis DESC;

SELECT
    c.id AS codigo_candidato,
    co.nome AS competencia,
    f.nome AS formacao,
    cf.nivel
FROM candidatos c
         LEFT JOIN candidato_competencia cc
                   ON cc.candidato_id = c.id
         LEFT JOIN competencias co
                   ON co.id = cc.competencia_id
         LEFT JOIN candidato_formacao cf
                   ON cf.candidato_id = c.id
         LEFT JOIN formacoes f
                   ON f.id = cf.formacao_id
ORDER BY c.id;

INSERT INTO curtidas_candidato_vaga
(candidato_id, vaga_id)
VALUES (1, 1)
    ON CONFLICT DO NOTHING;

INSERT INTO curtidas_empresa_candidato
(empresa_id, candidato_id)
VALUES (1, 1)
    ON CONFLICT DO NOTHING;

SELECT
    c.nome || ' ' || c.sobrenome AS candidato,
    v.nome AS vaga,
    e.nome AS empresa
FROM curtidas_candidato_vaga cv
         JOIN vagas v ON v.id = cv.vaga_id
         JOIN empresas e ON e.id = v.empresa_id
         JOIN curtidas_empresa_candidato ce
              ON ce.empresa_id = e.id
                  AND ce.candidato_id = cv.candidato_id
         JOIN candidatos c ON c.id = cv.candidato_id
WHERE cv.candidato_id = 1
  AND cv.vaga_id = 1;
