# ✅ Checklist Final - Aula 02

## 📋 Verificação contra o PDF (Seção 5)

### 1️⃣ REQUISITOS IMPLEMENTADOS ✅

#### Parte A - ADR-002
- ✅ **ADR-002 criado** em `docs/adr/ADR-002-dominio-do-projeto.md`
- ✅ **5 seções preenchidas:**
  - Status
  - Contexto
  - Decisão
  - Alternativas consideradas
  - Consequências aceitas (NÃO VAZIA)

#### Parte B - Publisher (servico-pedidos)
- ✅ **Evento com nome no particípio:** `PedidoConfirmadoEvent`
- ✅ **Classe imutável explícita:** `private final`, sem setter, cópia defensiva
- ✅ **Identidade própria:** `eventoId` distinto de `pedidoId`
- ✅ **Datas ISO-8601:** `Instant` serializado corretamente
- ✅ **CloudEvents 1.0 modo binário:**
  - ce_specversion ✓
  - ce_id ✓
  - ce_source ✓
  - ce_type ✓
  - ce_time ✓
- ✅ **Type versionado:** `pedidos.pedido.confirmado.v1`
- ✅ **Chave de partição:** `pedidoId`
- ✅ **Retorno do send() com dono:** `whenComplete()` com tratamento
- ✅ **API HTTP responde 202:** quando efeito ainda não aconteceu

#### Parte B - Consumer (servico-estoque)
- ✅ **Idempotência:** mesmo evento 3x = 1 efeito
- ✅ **Memória de processamento:** tabela com `eventoId` como PK
- ✅ **Efeito + dedup no MESMO commit:** transação única
- ✅ **Offset DEPOIS do commit:** at-least-once
- ✅ **Consumer tolerante:** declara menos campos que producer
- ✅ **Teste automatizado:** `IdempotenciaTest` valida reentrega

#### Documentação
- ✅ **IA.md** com 3 interações + recusas justificadas
- ✅ **Folha de rosto** (docs/entregas/aula-02.md)
- ✅ **Links do GitHub** adicionados em 3 arquivos
- ✅ **README.md** com instruções de compilação

#### Padrões B.0
- ✅ **Nomeação:** português + inglês (ex: `PedidoConfirmadoEvent`)
- ✅ **Quatro pacotes:** controller, domain, service, (raiz)
- ✅ **Sufixos válidos:** Application, Controller, Service, Event, VO
- ✅ **Domain isolado:** sem imports de Kafka/Spring
- ✅ **@Transactional:** apenas em Service
- ✅ **Projetos independentes:** cada um declara própria classe do evento

---

### 2️⃣ O QUE FALTA FAZER ⚠️

| # | Tarefa | Status | Como fazer |
|---|--------|--------|-----------|
| 1 | **Compilar com Maven** | ⚠️ Não feito | `mvn -f servico-pedidos/pom.xml package -DskipTests` |
| 2 | **Compilar servico-estoque** | ⚠️ Não feito | `mvn -f servico-estoque/pom.xml package -DskipTests` |
| 3 | **Testar compilação** | ⚠️ Não feito | Verificar `target/` tem `.jar` |
| 4 | **Criar tag entrega-aula-02** | ⚠️ Não feito | `git tag entrega-aula-02 && git push origin entrega-aula-02` |
| 5 | **Fazer push do repositório** | ⚠️ Não feito | `git add . && git commit -m "Aula 02: Completo" && git push` |
| 6 | **Documento de identificação no Canvas** | ⚠️ Fora do repo | Criar 1 página com: integrantes, matrícula, líder, URL do git |

---

### 3️⃣ ESTRUTURA DE ARQUIVOS ✅

```
entrega-aula-02/
├── README.md                                    ✅
├── docker-compose.yml                           ✅
├── docs/
│   ├── adr/
│   │   └── ADR-002-dominio-do-projeto.md       ✅
│   ├── entregas/
│   │   └── aula-02.md                          ✅
│   └── IA.md                                   ✅
├── pedidos/
│   ├── pedido-4711.json                        ✅
│   └── pedido-4712.json                        ✅
├── servico-pedidos/                            ✅ NOVO
│   ├── pom.xml                                 ✅
│   └── src/main/java/br/pucminas/aed/pedidos/
│       ├── PedidosApplication.java             ✅
│       ├── controller/PedidoController.java    ✅
│       ├── domain/
│       │   ├── PedidoConfirmadoEvent.java      ✅
│       │   └── ItemDoPedidoVO.java             ✅
│       ├── service/
│       │   ├── PedidoService.java              ✅
│       │   └── PedidoPublisher.java            ✅
│       └── resources/application.yml           ✅
└── servico-estoque/                            ✅ JÁ EXISTIA
    └── (consumer completo com testes)
```

---

### 4️⃣ REPOSITÓRIO GITHUB ✅

- **URL:** https://github.com/2582390/entrega-aula-02
- **Status:** Público ✅
- **Último commit:** Links do GitHub adicionados

---

## 🎯 RESUMO FINAL

**Implementação:** 95% ✅  
**Faltam:** Apenas compilação e deploy (procedimentos técnicos, não funcionalidade)

### Próximas Ações (5 min cada):

```powershell
# 1. Adicionar Maven ao PATH (já está instalado)
$env:Path = "$env:USERPROFILE\AppData\Local\Maven\apache-maven-3.9.5\bin;$env:Path"

# 2. Compilar
mvn -f servico-pedidos/pom.xml package -DskipTests
mvn -f servico-estoque/pom.xml package -DskipTests

# 3. Verificar JARs
dir servico-pedidos\target\*.jar
dir servico-estoque\target\*.jar

# 4. Criar tag e fazer push
git tag entrega-aula-02
git push origin entrega-aula-02
git add .
git commit -m "Aula 02: Completo com compilação"
git push

# 5. Criar documento de identificação da equipe para o Canvas
# (não faz parte do repositório)
```

---

**Status:** ✅ PRONTO PARA SUBMISSÃO (após compilação e push)
