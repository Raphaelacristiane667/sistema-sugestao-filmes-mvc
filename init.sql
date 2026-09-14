CREATE DATABASE IF NOT EXISTS mvc_java CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mvc_java;

DROP TABLE IF EXISTS avaliacoes;
DROP TABLE IF EXISTS categorias;
DROP TABLE IF EXISTS filmes;

CREATE TABLE filmes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    ano_lancamento INT NOT NULL
);

CREATE TABLE categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

CREATE TABLE avaliacoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    filme_id INT NOT NULL,
    categoria_id INT NOT NULL,
    nota INT NOT NULL,
    CONSTRAINT fk_avaliacao_filme FOREIGN KEY (filme_id) REFERENCES filmes(id),
    CONSTRAINT fk_avaliacao_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

INSERT INTO filmes (titulo, ano_lancamento) VALUES
('Matrix', 1999), ('O Senhor dos Anéis', 2001), ('Toy Story', 1995), ('Cidade de Deus', 2002), ('Interestelar', 2014);
INSERT INTO categorias (nome) VALUES ('Ação'), ('Aventura'), ('Animação'), ('Drama'), ('Ficção Científica');
INSERT INTO avaliacoes (filme_id, categoria_id, nota) VALUES
(1,5,9), (2,2,10), (3,3,8), (4,4,9), (5,5,10);
