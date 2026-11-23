-- =====================================================================
-- Amigo Secreto - Base de datos (MySQL 8)
-- Crea la base de datos amigosecreto, sus tablas y los datos iniciales.
-- Ejecuta el script completo. No lo modifiques.
-- =====================================================================

DROP DATABASE IF EXISTS amigosecreto;
CREATE DATABASE amigosecreto CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
USE amigosecreto;

-- Catálogo de tipos
CREATE TABLE tipo_participante (
    id_tipo      INT          NOT NULL,
    descripcion  VARCHAR(30)  NOT NULL,
    CONSTRAINT pk_tipo_participante PRIMARY KEY (id_tipo),
    CONSTRAINT uq_tipo_participante_descripcion UNIQUE (descripcion)
);

-- Registros.
-- trabajador_activo se usa solo para Adulto; nombre_tutor se usa solo para Niño.
-- id_amigo_secreto es el participante al que le toca regalar (NULL = sin sorteo).
-- Si se elimina un participante, quien debía regalarle queda sin amigo secreto.
CREATE TABLE participante (
    id                 INT           NOT NULL AUTO_INCREMENT,
    id_tipo            INT           NOT NULL,
    nombre             VARCHAR(80)   NOT NULL,
    edad               INT           NOT NULL,
    sugerencia_regalo  VARCHAR(120)  NULL,
    trabajador_activo  BOOLEAN       NULL,
    nombre_tutor       VARCHAR(80)   NULL,
    id_amigo_secreto   INT           NULL,
    CONSTRAINT pk_participante PRIMARY KEY (id),
    CONSTRAINT fk_participante_tipo FOREIGN KEY (id_tipo) REFERENCES tipo_participante (id_tipo),
    CONSTRAINT fk_participante_amigo FOREIGN KEY (id_amigo_secreto) REFERENCES participante (id)
        ON DELETE SET NULL,
    CONSTRAINT ck_participante_edad CHECK (edad BETWEEN 0 AND 120)
);

-- Datos iniciales
INSERT INTO tipo_participante (id_tipo, descripcion) VALUES (1, 'Adulto');
INSERT INTO tipo_participante (id_tipo, descripcion) VALUES (2, 'Niño');
