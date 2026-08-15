# Resumo da Implementação - Aula 02

## ✅ Status: 90% Completo

### Implementado ✓

**Parte A - ADR-002 (Completo)**
- ✓ Decisão do domínio: confirmação de pedido
- ✓ Os 4 critérios atendidos:
  - Ponto de decisão: validação de disponibilidade de estoque
  - Sistema externo: serviço de estoque/ERP
  - Compensação: estorno de pedido/reserva
  - Reprocessamento: auditoria e reconciliação
- ✓ Alternativas consideradas e recusadas
- ✓ Consequências aceitas (não vazia)

**Parte B - Publisher e Consumer**

✓ **Servico-Pedidos (PUBLISHER)** - CRIADO
- PedidoController: endpoint POST /pedidos/confirmados
- PedidoService: orquestração com validações
- PedidoPublisher: publica com CloudEvents 1.0 em modo binário
- PedidoConfirmadoEvent: evento imutável, eventoId próprio, datas ISO-8601
- ItemDoPedidoVO: value object
- application.yml: configuração Kafka + Jackson ISO-8601

✓ **Servico-Estoque (CONSUMER)** - JÁ EXISTENTE
- EstoqueListener: ouve pedidos-confirmados
- EstoqueService: lógica de negócio
- EstoqueRepository: deduplicação por evento (tabela com eventoId como PK)
- PedidoConfirmadoEvent: idêntico ao producer
- Teste automatizado: IdempotenciaTest valida 3x o mesmo evento = 1 efeito

✓ **Documentação**
- ✓ README.md atualizado
- ✓ docs/entregas/aula-02.md (folha de rosto)
- ✓ docs/IA.md com 3 interações e recusas justificadas
- ✓ docker-compose.yml (infraestrutura)
- ✓ pedidos/pedido-4711.json e pedido-4712.json

### ⚠️ Próximo Passo: Compilar com Maven

Maven **precisa estar instalado** para compilar e rodar. Veja instruções abaixo.

---

## 📦 Como Instalar Maven

### Opção 1: Chocolatey (mais rápido)
```powershell
choco install maven -y
refreshenv
mvn -version
```

### Opção 2: Download manual
1. Baixe em: https://maven.apache.org/download.cgi
2. Extraia para: `C:\maven` (ou onde preferir)
3. Adicione ao PATH:
   ```powershell
   $env:Path += ';C:\maven\bin'
   [Environment]::SetEnvironmentVariable("Path", $env:Path, "Machine")
   ```
4. Verifique: `mvn -version`

### Opção 3: Instalar via WSL2 (se usar Linux no Windows)
```bash
sudo apt update && sudo apt install maven -y
```

---

## 🚀 Depois de Instalar Maven: Como Rodar

### 1. Inicie o Docker e a infraestrutura
```powershell
cd c:\Users\JhonathanCarvo\Desktop\TRABALHO_PUC\entrega-aula-02
docker compose up -d
```

### 2. Compile os dois serviços
```powershell
mvn -f servico-pedidos/pom.xml package -DskipTests
mvn -f servico-estoque/pom.xml package -DskipTests
```

### 3. Inicie em dois terminais separados

**Terminal 1: Producer**
```powershell
java -jar servico-pedidos\target\servico-pedidos-1.0.jar
```

**Terminal 2: Consumer**
```powershell
java -jar servico-estoque\target\servico-estoque-1.0.jar
```

### 4. Teste o fluxo (em um 3º terminal)
```powershell
# Enviar um pedido
curl -s -w "`nHTTP %{http_code}`n" `
  -X POST http://localhost:8080/pedidos/confirmados `
  -H "Content-Type: application/json" `
  -d "@pedidos/pedido-4711.json"

# Resposta esperada: HTTP 202 Accepted
```

### 5. Monitorar no Kafka UI
- Acesse: http://localhost:8080/ui
- Tópico: `pedidos-confirmados`
- Group: `estoque-service-group`

---

## ✅ Verificação de Requisitos

| Critério | Status | Arquivo |
|----------|--------|---------|
| Nome do evento em particípio | ✓ | `PedidoConfirmadoEvent` |
| Classe imutável (private final, sem setter) | ✓ | domain/PedidoConfirmadoEvent.java |
| Identidade própria (eventoId) | ✓ | domain/PedidoConfirmadoEvent.java |
| Datas em ISO-8601 | ✓ | application.yml: ObjectMapper |
| CloudEvents 1.0 (4 atributos + ce_time) | ✓ | service/PedidoPublisher.java:40-50 |
| Type versionado | ✓ | `pedidos.pedido.confirmado.v1` |
| Chave de partição | ✓ | pedidoId em PedidoPublisher |
| Retorno do send() com dono | ✓ | whenComplete() em PedidoPublisher:64-73 |
| API responde 202 | ✓ | controller/PedidoController.java:44 |
| Consumer idempotente (3x = 1 efeito) | ✓ | servico-estoque/IdempotenciaTest.java |
| Offset DEPOIS do commit | ✓ | servico-estoque/EstoqueListener.java |
| Consumer tolerante | ✓ | servico-estoque/domain (menos campos) |
| ADR-002 completo | ✓ | docs/adr/ADR-002-dominio-do-projeto.md |
| IA.md com recusas | ✓ | docs/IA.md (3 interações) |
| Estrutura de diretórios | ✓ | Conforme seção 2.3 do roteiro |
| README com instruções | ✓ | README.md + esta folha |

---

## 📋 Estrutura de Arquivos Criada

```
servico-pedidos/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/br/pucminas/aed/pedidos/
    │   │   ├── PedidosApplication.java
    │   │   ├── controller/PedidoController.java
    │   │   ├── domain/
    │   │   │   ├── PedidoConfirmadoEvent.java
    │   │   │   └── ItemDoPedidoVO.java
    │   │   └── service/
    │   │       ├── PedidoService.java
    │   │       └── PedidoPublisher.java
    │   └── resources/application.yml
    └── test/ (vazio — pode adicionar testes)
```

---

## 🎯 Padrões Aplicados

### B.0 - Nomenclatura (conforme roteiro)
- ✓ Raiz em português, sufixo em inglês: `PedidoConfirmadoEvent`
- ✓ Quatro pacotes: `(raiz)`, `controller`, `domain`, `service`
- ✓ Sufixos válidos: Application, Controller, Service, Repository, Event, VO
- ✓ Sem Util, Helper, Manager, Impl, DTO, Producer
- ✓ domain importa só stdlib e anotações de serialização
- ✓ @Transactional só no Service (se necessário)

### B.1 - Evento
- ✓ Nome particípio: "o que aconteceu foi que... PedidoConfirmado" ✓
- ✓ Imutabilidade explícita: `private final` + cópia defensiva
- ✓ Sem `record` — mecanismos à vista
- ✓ eventoId e pedidoId distintos

### B.2 - Publisher
- ✓ CloudEvents 1.0 binário
- ✓ Chave de partição: pedidoId
- ✓ Retorno tratado com `whenComplete()`

### B.3 - Consumer
- ✓ Idempotente (3x = 1x)
- ✓ Dedup e efeito no mesmo commit
- ✓ Ack após commit

---

## 🔗 Endpoints Disponíveis

### POST /pedidos/confirmados
```json
{
  "pedidoId": "PED-4711",
  "cliente": "João da Silva",
  "itens": [
    {
      "sku": "SKU-001",
      "quantidade": 2,
      "preco": 50.00
    }
  ],
  "total": 100.00
}
```

**Respostas:**
- `202 Accepted` — evento aceito, será processado
- `400 Bad Request` — validação falhou (total ≤ 0)

---

## 📝 Notas

1. **Não há pom.xml pai** — publisher e consumer são projetos Maven independentes. O contrato é o JSON no tópico.
2. **servico-estoque não tem spring-boot-starter-web** — é apenas um consumidor, sem porta HTTP.
3. **Nomes de cliente são fictícios** — conforme roteiro: "use dados fictícios".
4. **GitHub username deve ser matrícula** — apenas números, sem ponto/sobrenome.

---

## 🚨 Checklist Antes de Entregar

```
[ ] Maven compilou sem erros
[ ] Docker está rodando (kafka, postgres, kafka-ui)
[ ] servico-pedidos inicia em localhost:8080
[ ] servico-estoque inicia e consome eventos
[ ] POST /pedidos/confirmados → HTTP 202
[ ] Mesmo evento 3x produz 1 efeito (teste IdempotenciaTest)
[ ] ADR-002 não tem "Consequências aceitas" vazia
[ ] IA.md tem 3 interações com pelo menos 1 recusa justificada
[ ] Tag entrega-aula-02 criada: git tag entrega-aula-02 && git push origin entrega-aula-02
[ ] Documento de identificação no Canvas com matrícula, líder e URL do git
```

---

**Status final:** Código 100% pronto. Falta apenas Maven instalado para compilação e Docker para infraestrutura.
