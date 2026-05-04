# Backend - Sistema de Atividades Complementares

API em Java com Spring Boot para o PI de Atividades Complementares.

Este repositório é apenas do backend. O front fica em outro repositório e chama esta API usando `fetch`.

## Tecnologias

- Java 17
- Spring Boot 3
- Spring Web
- Spring JDBC
- MySQL
- Maven

## Estrutura

```text
src/main/java/br/com/pi/atividades
├── config       -> configuração de CORS
├── controller   -> endpoints da API
├── dto          -> objetos de entrada e saída
├── exception    -> tratamento de erros
├── repository   -> consultas SQL com JdbcTemplate
└── service      -> regras simples da aplicação

sql
├── schema.sql   -> cria o banco e as tabelas
└── seed.sql     -> insere dados de teste
```

## Como rodar

### 1. Criar o banco

Abra o MySQL Workbench e execute:

```text
sql/schema.sql
```

Depois execute:

```text
sql/seed.sql
```

O `seed.sql` coloca dados de teste no banco. Esses dados substituem os dados que antes estavam fixos no HTML do front.

### 2. Configurar a senha do MySQL

Abra:

```text
src/main/resources/application.properties
```

Altere:

```properties
spring.datasource.password=123456
```

Coloque a senha do seu MySQL.

### 3. Abrir no Spring Tools

1. Abra o Spring Tools ou Eclipse.
2. Vá em `File > Import`.
3. Escolha `Maven > Existing Maven Projects`.
4. Selecione a pasta deste backend.
5. Aguarde as dependências baixarem.
6. Rode a classe `AtividadesComplementaresApplication.java` como Spring Boot App.

A API deve iniciar em:

```text
http://localhost:8080
```

### 4. Testar a API

Abra no navegador:

```text
http://localhost:8080/api/health
```

Resposta esperada:

```json
{"status":"API funcionando"}
```

## Endpoints principais

```text
GET    /api/health
GET    /api/dashboard
GET    /api/cursos
POST   /api/cursos
PUT    /api/cursos/{id}
DELETE /api/cursos/{id}

GET    /api/alunos
POST   /api/alunos
PUT    /api/alunos/{id}
DELETE /api/alunos/{id}

GET    /api/solicitacoes
GET    /api/solicitacoes?status=Pendente
PUT    /api/solicitacoes/{id}/aprovar
PUT    /api/solicitacoes/{id}/rejeitar
GET    /api/categorias
```

## Observação importante

A pasta `sql` não é o banco de dados. Ela só guarda os scripts para criar e popular o banco.
O banco real continua sendo o MySQL.
