SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- ------------------------------------------------------------
-- Schema
-- ------------------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `atividades_complementares`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `atividades_complementares`;

-- ============================================================
-- Tabela: usuario
-- Entidade pai da hierarquia (generalização)
-- ============================================================
CREATE TABLE IF NOT EXISTS `usuario` (
  `id`        INT          NOT NULL AUTO_INCREMENT,
  `email`     VARCHAR(150) NOT NULL,
  `telefone`  VARCHAR(20)  NULL,
  `perfil`    VARCHAR(50)  NULL,
  `senha`     VARCHAR(255) NOT NULL,
  `nome`      VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uq_usuario_email` (`email`)
) ENGINE = InnoDB;

-- ============================================================
-- Tabela: admin
-- Especialização de usuario
-- ============================================================
CREATE TABLE IF NOT EXISTS `admin` (
  `id_usuario`  INT         NOT NULL,
  `tipo_admin`  VARCHAR(50) NULL,
  PRIMARY KEY (`id_usuario`),
  CONSTRAINT `fk_admin_usuario`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `usuario` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ============================================================
-- Tabela: coordenador
-- Especialização de usuario
-- ============================================================
CREATE TABLE IF NOT EXISTS `coordenador` (
  `id_usuario` INT NOT NULL,
  PRIMARY KEY (`id_usuario`),
  CONSTRAINT `fk_coordenador_usuario`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `usuario` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ============================================================
-- Tabela: aluno
-- Especialização de usuario
-- ============================================================
CREATE TABLE IF NOT EXISTS `aluno` (
  `id_usuario`    INT         NOT NULL,
  `matricula`     VARCHAR(20) NOT NULL,
  `data_ingresso` DATE        NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE INDEX `uq_aluno_matricula` (`matricula`),
  CONSTRAINT `fk_aluno_usuario`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `usuario` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ============================================================
-- Tabela: curso
-- ============================================================
CREATE TABLE IF NOT EXISTS `curso` (
  `id_curso`           INT          NOT NULL AUTO_INCREMENT,
  `nome`               VARCHAR(100) NOT NULL,
  `carga_horaria_total` INT         NOT NULL DEFAULT 0,
  PRIMARY KEY (`id_curso`)
) ENGINE = InnoDB;

-- ============================================================
-- Tabela: regra_curso
-- Relacionamento: curso possui regra_curso (1,1)
-- ============================================================
CREATE TABLE IF NOT EXISTS `regra_curso` (
  `id_regra`          INT NOT NULL AUTO_INCREMENT,
  `id_curso`          INT NOT NULL,
  `carga_horaria_min` INT NOT NULL DEFAULT 0,
  `carga_horaria_max` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id_regra`),
  UNIQUE INDEX `uq_regra_curso_id_curso` (`id_curso`),
  CONSTRAINT `fk_regra_curso_curso`
    FOREIGN KEY (`id_curso`)
    REFERENCES `curso` (`id_curso`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ============================================================
-- Tabela: categoria_atividade
-- ============================================================
CREATE TABLE IF NOT EXISTS `categoria_atividade` (
  `id_categoria` INT          NOT NULL AUTO_INCREMENT,
  `descricao`    TEXT         NULL,
  `nome`         VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id_categoria`)
) ENGINE = InnoDB;

-- ============================================================
-- Tabela: regra_curso_categoria  (relacionamento "define")
-- regra_curso (1,n) define (0,n) categoria_atividade
-- ============================================================
CREATE TABLE IF NOT EXISTS `regra_curso_categoria` (
  `id_regra`      INT NOT NULL,
  `id_categoria`  INT NOT NULL,
  PRIMARY KEY (`id_regra`, `id_categoria`),
  CONSTRAINT `fk_rcc_regra`
    FOREIGN KEY (`id_regra`)
    REFERENCES `regra_curso` (`id_regra`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_rcc_categoria`
    FOREIGN KEY (`id_categoria`)
    REFERENCES `categoria_atividade` (`id_categoria`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ============================================================
-- Tabela: atividade
-- ============================================================
CREATE TABLE IF NOT EXISTS `atividade` (
  `id_atividade`    INT          NOT NULL AUTO_INCREMENT,
  `descricao`       TEXT         NULL,
  `carga_horaria`   INT          NOT NULL DEFAULT 0,
  `titulo`          VARCHAR(150) NOT NULL,
  `data_envio`      DATE         NULL,
  `data_realizacao` DATE         NULL,
  `status`          VARCHAR(30)  NOT NULL DEFAULT 'pendente',
  -- FK para curso (pertence a) - (1,1) do lado atividade
  `id_curso`        INT          NOT NULL,
  -- FK para aluno (submete) - (1,n) do lado atividade
  `id_aluno`        INT          NOT NULL,
  -- FK para categoria (classifica) - (1,n) do lado atividade
  `id_categoria`    INT          NOT NULL,
  PRIMARY KEY (`id_atividade`),
  INDEX `idx_atividade_curso`     (`id_curso`),
  INDEX `idx_atividade_aluno`     (`id_aluno`),
  INDEX `idx_atividade_categoria` (`id_categoria`),
  CONSTRAINT `fk_atividade_curso`
    FOREIGN KEY (`id_curso`)
    REFERENCES `curso` (`id_curso`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_atividade_aluno`
    FOREIGN KEY (`id_aluno`)
    REFERENCES `aluno` (`id_usuario`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_atividade_categoria`
    FOREIGN KEY (`id_categoria`)
    REFERENCES `categoria_atividade` (`id_categoria`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ============================================================
-- Tabela: comprovante
-- atividade (1,n) possui comprovante
-- ============================================================
CREATE TABLE IF NOT EXISTS `comprovante` (
  `id_comprovante` INT          NOT NULL AUTO_INCREMENT,
  `id_atividade`   INT          NOT NULL,
  `tipo_arquivo`   VARCHAR(50)  NULL,
  `tamanho`        BIGINT       NULL,
  `arquivo`        LONGBLOB     NULL,
  PRIMARY KEY (`id_comprovante`),
  INDEX `idx_comprovante_atividade` (`id_atividade`),
  CONSTRAINT `fk_comprovante_atividade`
    FOREIGN KEY (`id_atividade`)
    REFERENCES `atividade` (`id_atividade`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ============================================================
-- Tabela: avaliacao
-- ============================================================
CREATE TABLE IF NOT EXISTS `avaliacao` (
  `id_avaliacao`        INT          NOT NULL AUTO_INCREMENT,
  `carga_horaria_validada` INT       NOT NULL DEFAULT 0,
  `status_avaliacao`    VARCHAR(30)  NOT NULL DEFAULT 'pendente',
  `observacao`          TEXT         NULL,
  `data_avaliacao`      DATE         NULL,
  -- FK para atividade (recebe) - (0,1) do lado atividade → (1,1) do lado avaliacao
  `id_atividade`        INT          NOT NULL,
  -- FK para coordenador (avalia) - (0,n) coordenador avalia (1,n) avaliacao
  `id_coordenador`      INT          NOT NULL,
  PRIMARY KEY (`id_avaliacao`),
  UNIQUE INDEX `uq_avaliacao_atividade` (`id_atividade`),
  INDEX `idx_avaliacao_coordenador` (`id_coordenador`),
  CONSTRAINT `fk_avaliacao_atividade`
    FOREIGN KEY (`id_atividade`)
    REFERENCES `atividade` (`id_atividade`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_avaliacao_coordenador`
    FOREIGN KEY (`id_coordenador`)
    REFERENCES `coordenador` (`id_usuario`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ============================================================
-- Tabela: matricula_curso  (relacionamento "matricula-se")
-- aluno (0,n) matricula-se em (0,n) curso
-- Coordenador (0,n) gerencia (0,n) curso
-- ============================================================
CREATE TABLE IF NOT EXISTS `matricula_curso` (
  `id_aluno`  INT NOT NULL,
  `id_curso`  INT NOT NULL,
  PRIMARY KEY (`id_aluno`, `id_curso`),
  CONSTRAINT `fk_matricula_aluno`
    FOREIGN KEY (`id_aluno`)
    REFERENCES `aluno` (`id_usuario`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_matricula_curso`
    FOREIGN KEY (`id_curso`)
    REFERENCES `curso` (`id_curso`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ============================================================
-- Tabela: coordenador_curso  (relacionamento "gerencia")
-- coordenador (0,n) gerencia (0,n) curso
-- ============================================================
CREATE TABLE IF NOT EXISTS `coordenador_curso` (
  `id_coordenador` INT NOT NULL,
  `id_curso`       INT NOT NULL,
  PRIMARY KEY (`id_coordenador`, `id_curso`),
  CONSTRAINT `fk_coord_curso_coordenador`
    FOREIGN KEY (`id_coordenador`)
    REFERENCES `coordenador` (`id_usuario`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_coord_curso_curso`
    FOREIGN KEY (`id_curso`)
    REFERENCES `curso` (`id_curso`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ============================================================
-- Restaurar configurações originais
-- ============================================================
SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;