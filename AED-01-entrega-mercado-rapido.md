# Entrega Aula 01 — Mercado Rápido

## B.1 Mural finalizado

### Caminho principal

[Cliente] → (FinalizarCompra) → {Pedido} → **PedidoRecebido**
  → política: sempre que `PedidoRecebido`, então `AutorizarPagamento`

[Gateway de pagamento] → **PagamentoAutorizado**
  → política: sempre que `PagamentoAutorizado`, então `ReservarEstoque`

{Estoque} → **EstoqueReservado**
  → política: sempre que `EstoqueReservado`, então `ConfirmarPedido`

{Pedido} → **PedidoConfirmado**
  → [Notificação] → `NotificacaoEnviada`

### Caminho de exceção

{Pagamento} → **PagamentoRecusado**
  → política: sempre que `PagamentoRecusado`, então `LiberarReserva`

{Estoque} → **ReservaLiberada**
  → política: sempre que `ReservaLiberada`, então `PedidoCancelado`

{Pedido} → **PedidoCancelado**

### Hotspots

- ! Quem cancela o pedido se o gateway demorar mais de 30 segundos?
- ! Estorno é evento ou comando?
- ! A análise antifraude bloqueia a confirmação ou roda em paralelo?

### Fronteiras sugeridas

- `Pedido`: recebe o pedido, marca fluxo e publica confirmação.
- `Pagamento`: autoriza ou recusa o pagamento externo.
- `Estoque`: reserva, libera ou expira a reserva do item.
- `Entrega`: acionamento do entregador e rota em tempo real.

Cada fronteira agrupa o que muda junto e pelo mesmo motivo.

---

## B.2 Diário de eventos

### Evento 1

Nome do evento
PedidoRecebido

Agregado produtor
Pedido — é ele que decide que o cliente finalizou a compra e que o pedido pode entrar no fluxo.

Consumidores previstos
Pagamento, Estoque, Notificação. Eles precisam saber que o pedido existe e qual é seu conteúdo.

Carga mínima
`eventoId`, `ocorridoEm`, `pedidoId`, `clienteId`, `valorTotal`, `itens[sku, quantidade]`.

Por que existe
Inicia o processo de compra e garante que os demais domínios possam reagir ao pedido recebido.

Frequência estimada
Alta (~8 mil por dia).

---

### Evento 2

Nome do evento
PagamentoAutorizado

Agregado produtor
Pagamento — é quem decide que a transação foi aprovada pelo gateway.

Consumidores previstos
Estoque, Pedido, Notificação. Eles precisam saber que o pedido pode seguir adiante.

Carga mínima
`eventoId`, `ocorridoEm`, `pedidoId`, `autorizacaoId`, `valor`, `status`.

Por que existe
Informa que o pagamento foi aprovado e libera a próxima etapa do processo.

Frequência estimada
Média (~3 mil por dia).

---

### Evento 3

Nome do evento
PagamentoRecusado

Agregado produtor
Pagamento — é quem decide que a transação foi rejeitada.

Consumidores previstos
Pedido, Estoque, Notificação. Precisam saber que o pedido não deve avançar e que reservas podem ser liberadas.

Carga mínima
`eventoId`, `ocorridoEm`, `pedidoId`, `motivo`.

Por que existe
Indica que a compra falhou e aciona o fluxo de exceção adequado.

Frequência estimada
Baixa (~200 por dia).

---

### Evento 4

Nome do evento
EstoqueReservado

Agregado produtor
Estoque — é quem confirma a reserva física do item para o pedido.

Consumidores previstos
Pedido, Notificação. Eles precisam saber que o estoque está garantido.

Carga mínima
`eventoId`, `ocorridoEm`, `pedidoId`, `sku`, `quantidade`, `localizacao`.

Por que existe
Confirma a reserva do item e permite que o pedido continue no processo.

Frequência estimada
Média (~3 mil por dia).

---

### Evento 5

Nome do evento
ReservaLiberada

Agregado produtor
Estoque — é quem decide liberar a reserva de um item.

Consumidores previstos
Pedido. Hoje mais ninguém.

Carga mínima
`eventoId`, `ocorridoEm`, `pedidoId`, `sku`, `quantidade`, `motivo`.

Por que existe
Registra que a reserva foi liberada após uma exceção ou cancelamento.

Frequência estimada
Baixa (~150 por dia).

---

### Evento 6

Nome do evento
ReservaExpirada

Agregado produtor
Estoque — é quem controla o prazo da reserva.

Consumidores previstos
Pedido. O estoque precisa voltar a ficar disponível se o pagamento não vier.

Carga mínima
`eventoId`, `ocorridoEm`, `pedidoId`, `itens[sku, quantidade]`.

Por que existe
Evita que estoque continue bloqueado por reservas sem pagamento.

Frequência estimada
Baixa (~200 por dia).

---

### Evento 7

Nome do evento
PedidoConfirmado

Agregado produtor
Pedido — é quem decide que o pedido está definitivamente confirmado.

Consumidores previstos
Estoque, Notificação. Eles usam o fato para aplicar o efeito de negócio e avisar o cliente.

Carga mínima
`eventoId`, `ocorridoEm`, `pedidoId`, `clienteId`, `valorTotal`, `moeda`, `itens[sku, quantidade]`.

Por que existe
Afirma que o pedido foi confirmado e autoriza os serviços interessados a aplicar seus efeitos.

Frequência estimada
Alta (~8 mil por dia).

---

### Evento 8

Nome do evento
PedidoCancelado

Agregado produtor
Pedido — é quem decide que o pedido não pode ser concluído.

Consumidores previstos
Estoque, Notificação. Eles precisam saber que o pedido foi cancelado para reverter reservas e avisar o cliente.

Carga mínima
`eventoId`, `ocorridoEm`, `pedidoId`, `motivo`.

Por que existe
Finaliza o fluxo quando o pedido não pode ser completado.

Frequência estimada
Baixa (~100 por dia).

---

### Observações sobre carga mínima

- `eventoId` é necessário para deduplicação de eventos.
- `ocorridoEm` registra quando o fato aconteceu no domínio, não quando o broker recebeu.
- `pedidoId` identifica o agregado e permite ordenação por chave.
- `itens` inclui `sku` e `quantidade`, que são necessários para aplicar reserva no estoque.

---

## B.3 Envelope completo de um evento

### Evento escolhido

`PedidoConfirmado`

### Modo binário (como no projeto da disciplina)

Cabeçalhos da mensagem:

- `ce_specversion: 1.0`
- `ce_id: 11111111-1111-1111-1111-111111111111`
- `ce_source: /loja/servico-pedidos`
- `ce_type: loja.pedido.confirmado.v1`
- `ce_time: 2026-08-03T22:14:07.512Z`

Corpo da mensagem:

```json
{
  "eventoId": "11111111-1111-1111-1111-111111111111",
  "ocorridoEm": "2026-08-03T22:14:07.512Z",
  "pedidoId": "4711",
  "clienteId": "c-9931",
  "valorTotal": 289.90,
  "moeda": "BRL",
  "itens": [
    { "sku": "TEC-002", "quantidade": 1, "preco": 289.90 }
  ]
}
```

### Modo estruturado

```json
{
  "specversion": "1.0",
  "id": "11111111-1111-1111-1111-111111111111",
  "source": "/loja/servico-pedidos",
  "type": "loja.pedido.confirmado.v1",
  "time": "2026-08-03T22:14:07.512Z",
  "subject": "pedido/4711",
  "datacontenttype": "application/json",
  "data": {
    "eventoId": "11111111-1111-1111-1111-111111111111",
    "ocorridoEm": "2026-08-03T22:14:07.512Z",
    "pedidoId": "4711",
    "clienteId": "c-9931",
    "valorTotal": 289.90,
    "moeda": "BRL",
    "itens": [
      { "sku": "TEC-002", "quantidade": 1, "preco": 289.90 }
    ]
  }
}
```

### Justificativa

- O modo binário é o mais adequado para mensageria Kafka porque separa os atributos do envelope dos dados do payload.
- O consumidor pode deduplicar lendo apenas `ce_id`, sem precisar desserializar a carga.
- O modo estruturado é útil para HTTP e arquivos, mas o projeto da disciplina usa mensageria e, por isso, o binário é o modo operacional.

---

## B.4 ADR-001 — Comunicação por eventos no processo Mercado Rápido

### Status

Aceito

### Contexto

O processo envolve cliente, gateway de pagamento, estoque e notificação. O pedido é recebido no aplicativo, o pagamento é autorizado por um gateway externo e o estoque é reservado pela loja física. O domínio exige desacoplamento entre o serviço que afirma o fato (`servico-pedidos`) e o serviço que aplica o efeito no estoque (`servico-estoque`). Há também consumo eventual por outros interessados, como painel de pedidos e notificação.

### Decisão

Usaremos comunicação orientada a eventos entre `servico-pedidos` e `servico-estoque` usando um tópico Kafka. O produtor publica fatos (`PedidoConfirmado`) em um barramento, e os consumidores interessados leem esse evento de forma assíncrona.

### Alternativas consideradas

- Chamada síncrona HTTP ponto a ponto — descartada porque cria acoplamento temporal e torna o fluxo dependente da disponibilidade imediata do serviço de estoque.
- Orquestrador central — descartado porque introduz um ponto único de falha e mantém um controle excessivo do processo, reduzindo o desacoplamento entre os serviços.

### Consequências aceitas

- Consistência eventual entre o pedido confirmado e a reserva de estoque.
- Necessidade de ter consumidores idempotentes e um mecanismo de deduplicação de eventos (`eventoId`).
- Complexidade maior de depuração distribuída e necessidade de observabilidade de eventos.
- Necessidade de versionamento do contrato do evento e cuidado com evolução de payload.

### Como saberemos que erramos

- Se o tempo entre a publicação do evento e a aplicação do estoque se tornar demasiado alto para o negócio.
- Se a falta de idempotência causar diferenças frequentes de estoque duplicado ou reprocessamentos indevidos.
- Se a equipe não conseguir rastrear com facilidade o caminho do evento entre produtor e consumidor.

---

## B.5 Registro de uso de IA

Este documento foi elaborado no contexto da disciplina com apoio de IA para estruturar e redigir o conteúdo do exercício. O uso de IA foi limitado à formatação, revisão e organização das respostas, e as decisões técnicas e de negócio foram supervisionadas e validadas pelo autor.

A IA foi recusada para qualquer ação que exigisse execução de comandos não autorizados, acesso direto a sistemas do usuário ou geração automática de conteúdo sem validação humana.

---

## Checklist final

- [x] Eventos no passado e no vocabulário do negócio
- [x] Nenhum evento CRUD
- [x] Nenhum comando disfarçado de evento
- [x] Caminho de exceção presente
- [x] Pelo menos dois hotspots
- [x] Pelo menos duas políticas escritas no formato “sempre que X, então Y”
- [x] Cada evento do diário tem frase de negócio
- [x] Carga mínima de cada evento justificada
- [x] Envelope com `specversion`, `id`, `source`, `type` e `time`
- [x] Envelope nos dois modos: estruturado e binário
- [x] ADR com consequências aceitas
- [x] ADR com métrica ou sintoma para saber que erramos
- [x] Registro de uso de IA com recusa justificada

---

## Uso da aplicação para validação

### Comandos úteis

```powershell
cd "C:\Users\JhonathanCarvo\Desktop\ARQ REATIVA\AED - Aula 01 - Codigo da Demonstracao\demo-kafka-idempotencia"
docker compose up -d
java -jar servico-pedidos/target/servico-pedidos-1.0.jar
java -jar servico-estoque/target/servico-estoque-1.0.jar
curl.exe -X POST http://localhost:8080/pedidos/confirmados -H "Content-Type: application/json" -d "@pedidos/pedido-4711.json"
docker compose exec postgres psql -U aed -d aed -c "select * from estoque; select count(*) from evento_processado;"
```

### O que confirmar

- o `POST /pedidos/confirmados` retorna `202`
- o estoque cai apenas uma vez
- `evento_processado` registra apenas 1 evento processado
