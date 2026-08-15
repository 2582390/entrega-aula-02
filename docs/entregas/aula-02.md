# Folha de rosto — Entrega Aula 02

## O que foi feito nesta etapa
- ADR-002: definição do domínio em [docs/adr/ADR-002-dominio-do-projeto.md](docs/adr/ADR-002-dominio-do-projeto.md).
- Implementação do publisher/consumer no domínio de confirmação de pedido, com CloudEvents nos cabeçalhos e payload em JSON neutro.
- Consumer idempotente com deduplicação por `eventoId` e teste automatizado de repetição de mensagens.

## Onde está cada coisa
- ADR: [docs/adr/ADR-002-dominio-do-projeto.md](docs/adr/ADR-002-dominio-do-projeto.md)
- Registro de uso de IA: [docs/IA.md](docs/IA.md)
- Folha de rosto: [docs/entregas/aula-02.md](docs/entregas/aula-02.md)
- Producer: [servico-pedidos](../../servico-pedidos)
- Consumer: [servico-estoque](../../servico-estoque)

## Como rodar
1) Suba a infraestrutura local com Docker Compose:

```powershell
docker compose up -d
```

2) Compile os módulos:

```powershell
mvn -f servico-pedidos/pom.xml package -DskipTests
mvn -f servico-estoque/pom.xml package -DskipTests
```

3) Suba os serviços em terminais separados:

```powershell
java -jar servico-pedidos/target/servico-pedidos-1.0.jar
java -jar servico-estoque/target/servico-estoque-1.0.jar
```

4) Envie um pedido de exemplo:

```powershell
curl.exe -s -w "`nHTTP %{http_code}`n" -X POST localhost:8080/pedidos/confirmados -H "Content-Type: application/json" -d "@pedidos/pedido-4711.json"
```

## Quem fez o quê
- Líder: preencher nome e matrícula
- Integrante 01: preencher nome e matrícula
- Integrante 02: preencher nome e matrícula
- Integrante 03: preencher nome e matrícula
- Integrante 04: preencher nome e matrícula
- Integrante 05: preencher nome e matrícula
- Integrante 06: preencher nome e matrícula

## Observações
- Atualizar os nomes e matrículas antes da entrega pública.
- A tag recomendada para marcar a entrega é `entrega-aula-02`.
- O repositório público deve conter a URL final do GitHub do projeto.