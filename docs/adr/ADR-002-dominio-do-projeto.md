# ADR-002 — Domínio do projeto

🔗 **Repositório GitHub:** https://github.com/2582390/entrega-aula-02

## Status
Aceita · 2026-08-16 · Equipe 01

## Contexto
Escolhemos trabalhar com o processo de confirmação de pedidos em uma loja online. O domínio foi trazido a partir da demonstração existente no repositório e adaptado para respeitar os requisitos da atividade da Aula 02: um processo real, com regra de negócio, integração externa, compensação e reprocessamento.

## Decisão
Processo escolhido: confirmação de pedido do gatilho (pedido criado) até o desfecho (pedido confirmado e estoque reservado).

Como atende cada um dos quatro critérios:
- ponto de decisão com regra de negócio: a decisão de confirmar ou recusar a reserva com base na disponibilidade do estoque ocorre no fluxo do pedido;
- sistema externo: o serviço de estoque/ERP é o componente externo representado pelo `servico-estoque`, que pode ficar indisponível ou falhar em alguma operação;
- caminho de exceção com compensação: se a confirmação do pedido ou a reserva de estoque falhar, há necessidade de compensação/estorno e notificação do cliente;
- algo que valha a pena reprocessar: auditoria, relatórios e reconciliação de confirmações e reservas de estoque são cenários naturais de reprocessamento.

## Alternativas consideradas
- Ledger de pagamentos: recusado porque amplia o escopo com integrações financeiras que não são necessárias para provar o domínio da aula 02;
- roteamento em tempo real de entregas: recusado porque exige mais integrações e não agrega ao objetivo central da demonstração de idempotência e integração assíncrona;
- cadastro de cliente simplificado: recusado porque não produz um ponto de decisão ou compensação relevante para o problema estudado.

## Consequências aceitas
- O projeto fica concentrado em um domínio de negócio pequeno, mas suficientemente rico para demonstrar eventos, idempotência e compensação;
- o contrato da mensageria exige atenção a CloudEvents, serialização neutra e tolerância a reprocessamento;
- parte do comportamento de negócio fica fora do escopo nesta etapa, como regras de pagamento, logística e CQRS/Event Sourcing das aulas seguintes;
- a decisão reduz o conjunto de integrações, mas também limita o alcance funcional e aumentará a necessidade de disciplina na modelagem das próximas etapas.
