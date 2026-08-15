# Equipe 01 · Projeto de Confirmação de Pedido

**AED — Arquitetura Reativa e Event-Driven · PUC Minas / IEC**

Este repositório implementa o domínio de confirmação de pedido em um fluxo event-driven com Kafka, com um producer e um consumer idempotente em Java/Spring Boot.

## Domínio escolhido

Processo de confirmação de pedido de e-commerce, em que um pedido é recebido, validado e publicado como evento de domínio para reserva de estoque. O consumidor registra o evento como processado e aplica a reserva somente uma vez, mesmo quando a mesma mensagem chega repetidas vezes.

## Estrutura do projeto

- [docs/adr/ADR-002-dominio-do-projeto.md](docs/adr/ADR-002-dominio-do-projeto.md) — decisão do domínio e consequências aceitas
- [docs/IA.md](docs/IA.md) — registro de uso de IA com recusa justificada
- [docs/entregas/aula-02.md](docs/entregas/aula-02.md) — folha de rosto da entrega
- [servico-pedidos](servico-pedidos) — publisher/producer do domínio
- [servico-estoque](servico-estoque) — consumer idempotente

## Pré-requisitos

- JDK 21
- Maven 3.9+
- Docker Desktop ou Docker Engine

## Como subir o projeto

Na raiz do repositório:

```bash
docker compose up -d
```

Em seguida, compile os módulos:

```bash
mvn -f servico-pedidos/pom.xml package -DskipTests
mvn -f servico-estoque/pom.xml package -DskipTests
```

Suba o producer e o consumer em terminais separados:

```bash
java -jar servico-pedidos/target/servico-pedidos-1.0.jar
java -jar servico-estoque/target/servico-estoque-1.0.jar
```

Teste o fluxo HTTP de confirmação de pedido:

```powershell
curl.exe -s -w "`nHTTP %{http_code}`n" -X POST localhost:8080/pedidos/confirmados -H "Content-Type: application/json" -d "@pedidos/pedido-4711.json"
```

## Observações da entrega

- O evento é publicado como fato do domínio, não como comando.
- O contrato do Kafka usa CloudEvents em cabeçalhos e payload em JSON neutro.
- O consumidor deduplica por `eventoId` e confirma o offset somente após o commit.
- O comportamento idempotente é validado por teste automatizado.

## Dados da equipe

- Líder: preencher nome e matrícula
- Integrantes: preencher nomes e matrículas
- Repositório: preencher a URL pública do GitHub

> Este README deve ser atualizado com os dados finais da equipe antes da entrega oficial no repositório público e no Canvas.
