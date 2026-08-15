## Aula 02

Interação 1
- Pedido: "Como nomear o evento para descrever o fato de que um pedido foi confirmado?"
- Resposta da ferramenta: sugeriu `OrderConfirmedEvent` e uso de `record` em Java para simplicidade.
- Aceito: a sugestão de nome descrevendo o fato.
- Recusado: uso de `record` — razão técnica: decidimos não usar `record` para manter visibilidade explícita dos mecanismos de imutabilidade (campos `private final`, cópia defensiva), como exige o roteiro.

Interação 2
- Pedido: "Qual o formato de data usar no payload para compatibilidade entre linguagens?"
- Resposta da ferramenta: sugeriu epoch milliseconds.
- Aceito: recomendação de ser explícito sobre formato.
- Recusado: epoch — razão técnica: recusamos epoch porque quebra interoperabilidade humana e difere do `ce_time` no cabeçalho; optamos por ISO-8601 no corpo e configuramos `ObjectMapper` para isso.

Interação 3
- Pedido: "Devo incluir a classe Java do evento nos headers para facilitar desserialização no consumidor?"
- Resposta da ferramenta: sugeriu incluir um header com o tipo Java.
- Aceito: considerar metadados adicionais quando necessário.
- Recusado: incluir nome de classe Java nos headers — razão técnica: isso vicia o contrato no fornecedor e quebra interoperabilidade; usamos CloudEvents `ce_type` versionado em vez disso.
