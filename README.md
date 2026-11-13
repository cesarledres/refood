
# 🌱 SustentGame

## 📌 Sobre o Projeto

O **SustentGame** é um projeto acadêmico desenvolvido durante o **Challenge FIAP**, em parceria com a empresa **SoulUp**, com foco em sustentabilidade e gamificação.

O objetivo da aplicação é incentivar a realização de ações sustentáveis por meio de um sistema de pontuação e classificação de usuários.

O sistema foi desenvolvido como uma **API RESTful utilizando Java e Spring Boot**, permitindo o gerenciamento de usuários, cadastro e validação de vídeos, atribuição de pontos e consulta ao ranking.

A aplicação utiliza **Spring Data JPA e Hibernate** para persistência dos dados no **Oracle Database**, seguindo uma arquitetura em camadas para organizar as responsabilidades do sistema.

---

## 🚀 Funcionalidades

### 👤 Gerenciamento de Usuários

- Cadastro de usuários
- Consulta de usuários cadastrados
- Busca de usuários por ID
- Atualização de informações
- Exclusão de usuários

### 🎥 Gerenciamento de Vídeos

- Cadastro de vídeos vinculados aos usuários
- Consulta de vídeos cadastrados
- Validação simulada de vídeos relacionados a ações sustentáveis, utilizando uma lógica de aprovação ou rejeição aleatória para fins acadêmicos
- Verificação de vídeos já validados

### 🏆 Gamificação e Ranking

- Atribuição de pontos por ações sustentáveis
- Atualização da pontuação dos usuários
- Classificação dos participantes por pontuação
- Exibição dos cinco primeiros colocados
- Verificação de acesso a recompensas conforme a classificação

---

## 🛠️ Tecnologias Utilizadas

- Java
- Spring Boot
- Spring Web / Spring MVC
- Spring Data JPA
- Hibernate
- Oracle Database
- Maven
- IntelliJ IDEA
- Git e GitHub

---

## 🏗️ Arquitetura do Projeto

O SustentGame utiliza uma arquitetura em camadas, separando as responsabilidades de cada componente.

```text
Cliente HTTP
     |
     v
 Controller
     |
     v
   Service
     |
     v
 Repository
     |
     v
Oracle Database
```

---

## 🌐 API RESTful

A aplicação utiliza os métodos HTTP para realizar operações sobre os recursos do sistema.

| Método | Descrição |
|---|---|
| GET | Consultar recursos |
| POST | Cadastrar recursos |
| PUT | Atualizar recursos |
| DELETE | Excluir recursos |

### Principais Recursos

A API disponibiliza operações relacionadas a:

- **Usuários:** cadastro, consulta, atualização e exclusão.
- **Vídeos:** cadastro, consulta e validação.
- **Ranking:** classificação dos usuários de acordo com a pontuação.

> As rotas específicas devem ser consultadas nos Controllers da aplicação.

### Exemplo de Requisição

Consulta de usuário por identificador:

```http
GET /usuario/1
```

Exemplo ilustrativo de resposta JSON:

```json
{
  "id": 1,
  "nome": "Usuario Exemplo",
  "email": "usuario@email.com",
  "pontos": 100
}
```

> Os campos e caminhos apresentados são exemplos e devem ser ajustados conforme os DTOs e endpoints implementados.

---

## ⚠️ Tratamento de Exceções

A aplicação utiliza os recursos de tratamento de exceções do Spring Boot para identificar erros e retornar respostas HTTP adequadas.

Entre as situações previstas estão:

- Usuários ou vídeos não encontrados
- Dados inválidos
- Violações de regras de negócio
- Conflitos durante operações

### Principais Códigos HTTP

| Código | Significado |
|---|---|
| 200 OK | Requisição realizada com sucesso |
| 201 Created | Recurso criado |
| 204 No Content | Operação concluída sem conteúdo |
| 400 Bad Request | Requisição inválida |
| 404 Not Found | Recurso não encontrado |
| 409 Conflict | Conflito com uma regra de negócio |
| 500 Internal Server Error | Erro interno do servidor |

---

## ⚙️ Como Executar o Projeto

### Pré-requisitos

Para executar a aplicação localmente, é necessário possuir:

- JDK compatível com a versão configurada no `pom.xml` (Java 21)
- Maven ou Maven Wrapper
- Oracle Database acessível
- Git

### 1. Clonar o Repositório

```bash
git clone https://github.com/cesarledres/challenge-java-sustent-game.git
```

Acesse a pasta do projeto:

```bash
cd sustentGame
```

### 2. Configurar o Banco de Dados

Configure as propriedades de conexão com o Oracle Database no arquivo `application.properties` ou por meio de variáveis de ambiente.

Exemplo de configuração utilizando variáveis de ambiente:

```properties
spring.datasource.url=${ORACLE_URL}
spring.datasource.username=${ORACLE_USERNAME}
spring.datasource.password=${ORACLE_PASSWORD}
```

As variáveis devem conter as informações de conexão com o banco Oracle.

### 3. Executar a Aplicação

Utilizando o Maven Wrapper no Windows:

```bash
mvnw.cmd spring-boot:run
```

Ou, caso o Maven esteja instalado:

```bash
mvn spring-boot:run
```

Após a inicialização, a API estará disponível localmente no endereço:

`http://localhost:8081`

Para acessar a versão publicada da API, utilize a URL disponibilizada pelo Render.

---

## 🌐 Deploy

A API SustentGame está publicada na plataforma **Render**, permitindo o acesso aos seus endpoints por meio de uma URL pública, sem a necessidade de executar a aplicação localmente.

A API está integrada ao **Oracle Database**, possibilitando a realização de operações de cadastro, consulta, atualização e exclusão de dados por meio de requisições HTTP.

Para utilizar a API, basta informar a URL pública como endereço base das requisições em ferramentas como Postman ou Insomnia.

### Endpoints da API

**URL Base:** `https://challenge-java-sustent-game.onrender.com`

### 👤 Usuários

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/usuario` | Lista todos os usuários |
| GET | `/usuario/{id}` | Busca um usuário pelo ID |
| POST | `/usuario` | Cadastra um novo usuário |
| PUT | `/usuario/{id}` | Atualiza os dados de um usuário |
| DELETE | `/usuario/{id}` | Exclui um usuário |

**Exemplo de JSON para cadastro:**

```json
{
  "nome": "João Silva",
  "email": "joaosilva@email.com",
  "pontos": 0
}
```

### 🎥 Vídeos

| Método | Endpoint | Descrição |
|--------|---|---|
| GET    | `/video` | Lista todos os vídeos cadastrados |
| GET    | `/video/{id}` | Busca um vídeo pelo ID |
| POST   | `/video` | Cadastra um vídeo vinculado a um usuário |
| PUT    | `/video/{id}` | Atualiza os dados de um vídeo |
| DELETE | `/video/{id}` | Exclui um vídeo cadastrado |
| PATCH  | `/video/{id}/validar` | Realiza a validação simulada de um vídeo |

A validação utiliza uma lógica de decisão aleatória para simular a aprovação ou rejeição de vídeos relacionados a ações sustentáveis.

### 🏆 Ranking

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/ranking` | Consulta a classificação dos usuários |

### 📡 Como Utilizar

Para realizar uma requisição, basta combinar a URL base da API com o endpoint desejado.

**Exemplo:**

```http
GET https://challenge-java-sustent-game.onrender.com/usuario
```

Para operações de cadastro ou atualização, utilize o método HTTP correspondente e envie os dados no formato JSON.

**Cabeçalho:**

```http
Content-Type: application/json
```

> **Observação:** os endpoints e exemplos apresentados devem corresponder às rotas e aos DTOs implementados na aplicação.


---

## 🧠 Conceitos Aplicados

Durante o desenvolvimento do projeto, foram aplicados conceitos como:

- Programação Orientada a Objetos
- Encapsulamento
- Desenvolvimento de APIs RESTful
- Spring MVC
- Arquitetura em camadas
- Padrão Repository
- Operações CRUD
- Injeção de dependências
- Mapeamento objeto-relacional
- Persistência de dados
- Validação de regras de negócio
- Tratamento de exceções
- Versionamento com Git

---

## 🎯 Objetivo Acadêmico

O SustentGame tem como objetivo aplicar conhecimentos adquiridos durante o curso de **Análise e Desenvolvimento de Sistemas da FIAP**, integrando programação Java, desenvolvimento de APIs RESTful, persistência de dados e organização de software.

Além de atender à proposta do Challenge em parceria com a SoulUp, o projeto busca demonstrar a aplicação prática de conceitos de desenvolvimento backend e arquitetura de sistemas.# refood
