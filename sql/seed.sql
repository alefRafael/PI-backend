USE atividades_complementares;

-- Dados de teste para o backend.
-- Rode este arquivo depois do schema.sql.

INSERT INTO curso (nome, carga_horaria_total)
SELECT 'Engenharia de Software', 200
WHERE NOT EXISTS (SELECT 1 FROM curso WHERE nome = 'Engenharia de Software');

INSERT INTO curso (nome, carga_horaria_total)
SELECT 'Ciência da Computação', 200
WHERE NOT EXISTS (SELECT 1 FROM curso WHERE nome = 'Ciência da Computação');

INSERT INTO curso (nome, carga_horaria_total)
SELECT 'Sistemas de Informação', 180
WHERE NOT EXISTS (SELECT 1 FROM curso WHERE nome = 'Sistemas de Informação');

INSERT INTO curso (nome, carga_horaria_total)
SELECT 'Design Digital', 160
WHERE NOT EXISTS (SELECT 1 FROM curso WHERE nome = 'Design Digital');

INSERT INTO categoria_atividade (nome, descricao)
SELECT 'Ensino', 'Cursos, oficinas e formações complementares.'
WHERE NOT EXISTS (SELECT 1 FROM categoria_atividade WHERE nome = 'Ensino');

INSERT INTO categoria_atividade (nome, descricao)
SELECT 'Eventos', 'Palestras, seminários, congressos e hackathons.'
WHERE NOT EXISTS (SELECT 1 FROM categoria_atividade WHERE nome = 'Eventos');

INSERT INTO categoria_atividade (nome, descricao)
SELECT 'Pesquisa', 'Projetos de pesquisa e iniciação científica.'
WHERE NOT EXISTS (SELECT 1 FROM categoria_atividade WHERE nome = 'Pesquisa');

-- Coordenador padrão para registrar avaliações.
INSERT INTO usuario (email, telefone, perfil, senha, nome)
SELECT 'coordenador@faculdade.com', NULL, 'COORDENADOR', 'senha_temporaria', 'Coordenador PI'
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'coordenador@faculdade.com');

INSERT INTO coordenador (id_usuario)
SELECT id FROM usuario WHERE email = 'coordenador@faculdade.com'
ON DUPLICATE KEY UPDATE id_usuario = id_usuario;

-- Alunos de exemplo. Estes substituem os dados fixos que antes ficavam direto no HTML.
INSERT INTO usuario (email, telefone, perfil, senha, nome)
SELECT 'joao@email.com', NULL, 'ALUNO', 'senha_temporaria', 'João Silva'
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'joao@email.com');

INSERT INTO aluno (id_usuario, matricula, data_ingresso)
SELECT id, '20241001', '2024-02-01' FROM usuario WHERE email = 'joao@email.com'
ON DUPLICATE KEY UPDATE matricula = VALUES(matricula);

INSERT INTO usuario (email, telefone, perfil, senha, nome)
SELECT 'maria@email.com', NULL, 'ALUNO', 'senha_temporaria', 'Maria Santos'
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'maria@email.com');

INSERT INTO aluno (id_usuario, matricula, data_ingresso)
SELECT id, '20241002', '2024-02-01' FROM usuario WHERE email = 'maria@email.com'
ON DUPLICATE KEY UPDATE matricula = VALUES(matricula);

INSERT INTO usuario (email, telefone, perfil, senha, nome)
SELECT 'pedro@email.com', NULL, 'ALUNO', 'senha_temporaria', 'Pedro Costa'
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'pedro@email.com');

INSERT INTO aluno (id_usuario, matricula, data_ingresso)
SELECT id, '20241003', '2024-02-01' FROM usuario WHERE email = 'pedro@email.com'
ON DUPLICATE KEY UPDATE matricula = VALUES(matricula);

-- Matrículas em cursos.
INSERT IGNORE INTO matricula_curso (id_aluno, id_curso)
SELECT u.id, c.id_curso
FROM usuario u
JOIN curso c ON c.nome = 'Engenharia de Software'
WHERE u.email = 'joao@email.com';

INSERT IGNORE INTO matricula_curso (id_aluno, id_curso)
SELECT u.id, c.id_curso
FROM usuario u
JOIN curso c ON c.nome = 'Ciência da Computação'
WHERE u.email = 'maria@email.com';

INSERT IGNORE INTO matricula_curso (id_aluno, id_curso)
SELECT u.id, c.id_curso
FROM usuario u
JOIN curso c ON c.nome = 'Sistemas de Informação'
WHERE u.email = 'pedro@email.com';

-- Solicitações de exemplo. Estes substituem as linhas fixas da tela de solicitações.
INSERT INTO atividade (descricao, carga_horaria, titulo, data_envio, data_realizacao, status, id_curso, id_aluno, id_categoria)
SELECT 'Curso introdutório de Python.', 20, 'Curso de Python', '2024-01-15', '2024-01-10', 'Pendente', c.id_curso, u.id, cat.id_categoria
FROM usuario u
JOIN curso c ON c.nome = 'Engenharia de Software'
JOIN categoria_atividade cat ON cat.nome = 'Ensino'
WHERE u.email = 'joao@email.com'
  AND NOT EXISTS (SELECT 1 FROM atividade WHERE titulo = 'Curso de Python' AND id_aluno = u.id);

INSERT INTO atividade (descricao, carga_horaria, titulo, data_envio, data_realizacao, status, id_curso, id_aluno, id_categoria)
SELECT 'Palestra sobre inteligência artificial.', 4, 'Palestra IA', '2024-02-10', '2024-02-08', 'Pendente', c.id_curso, u.id, cat.id_categoria
FROM usuario u
JOIN curso c ON c.nome = 'Ciência da Computação'
JOIN categoria_atividade cat ON cat.nome = 'Eventos'
WHERE u.email = 'maria@email.com'
  AND NOT EXISTS (SELECT 1 FROM atividade WHERE titulo = 'Palestra IA' AND id_aluno = u.id);

INSERT INTO atividade (descricao, carga_horaria, titulo, data_envio, data_realizacao, status, id_curso, id_aluno, id_categoria)
SELECT 'Participação em hackathon.', 8, 'Hackathon', '2024-02-15', '2024-02-14', 'Pendente', c.id_curso, u.id, cat.id_categoria
FROM usuario u
JOIN curso c ON c.nome = 'Sistemas de Informação'
JOIN categoria_atividade cat ON cat.nome = 'Eventos'
WHERE u.email = 'pedro@email.com'
  AND NOT EXISTS (SELECT 1 FROM atividade WHERE titulo = 'Hackathon' AND id_aluno = u.id);
