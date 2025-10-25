# Fixed Income Fanout Exchange MQ

Este projeto demonstra a implementação de **Fanout Exchange** para broadcast de mensagens para múltiplos consumers simultâneos usando RabbitMQ em um contexto de renda fixa.

## 📋 Sobre o Projeto

Este é o **Exercício 4** da série de aprendizado de RabbitMQ, focado em Fanout Exchange com broadcast de eventos.

**O que você aprenderá:**
- ✅ Fanout Exchange (broadcast para todos)
- ✅ Múltiplos consumers independentes
- ✅ Mesmo evento para destinatários diferentes
- ✅ Filas exclusivas por consumer
- ✅ Padrão de notificação em tempo real
- ✅ Escalabilidade horizontal de consumers

## 🏗️ Arquitetura

```
Producer API (8080) → RabbitMQ Fanout Exchange → Múltiplos Consumers (8081+)
                           ↓
                    rf-events.fanout
                           │
        ┌──────────────────┼──────────────────┬───────────────────┐
        │                  │                  │                   │
   rate.queue         pricing.queue     notifications.queue   audit.queue
        │                  │                  │                   │
        ↓                  ↓                  ↓                   ↓
   RateConsumer    PricingConsumer   NotificationsConsumer  AuditConsumer
```

## 📦 Estrutura do Projeto

O projeto utiliza **Maven Multi-Module**, separando Producer e Consumer em módulos independentes:

```
fixed-income-fanout-exchange-mq/
├── pom.xml (POM Pai)
├── producer/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/
└── consumer/
    ├── pom.xml
    ├── Dockerfile
    └── src/main/java/
```

**Vantagens:**
- Deploy independente de Producer e Consumer
- Escalabilidade horizontal isolada
- Cada módulo com suas próprias dependências
- Simula arquitetura de microserviços

## 🛠️ Tecnologias

- Java 21
- Spring Boot 3.5.7
- Spring AMQP
- RabbitMQ 3
- Docker & Docker Compose
- Maven Multi-Module
- Lombok
- Jackson

## ✅ Pré-requisitos

- Docker e Docker Compose instalados
- Java 21 (para desenvolvimento local)
- Maven 3.9+ (para desenvolvimento local)
- Portas 8080, 8081, 8082, 8083, 8084, 5672 e 15672 disponíveis

## 🚀 Como Executar

### Com Docker Compose (recomendado)

```bash
# Clonar o repositório
git clone https://github.com/iagoomes/fixed-income-fanout-exchange-mq.git
cd fixed-income-fanout-exchange-mq

# Buildar e iniciar todos os serviços
docker-compose up --build

# Ou em background
docker-compose up -d --build

# Ver logs
docker-compose logs -f

# Ver logs de um serviço específico
docker-compose logs -f producer-api
docker-compose logs -f rate-consumer
docker-compose logs -f pricing-consumer

# Parar os serviços
docker-compose down
```

### Em Desenvolvimento Local

```bash
# Terminal 1 - RabbitMQ
docker-compose up rabbitmq

# Terminal 2 - Build do projeto
mvn clean install

# Terminal 3 - Producer
cd producer
mvn spring-boot:run

# Terminal 4+ - Consumers (cada um em um terminal)
cd consumer
mvn spring-boot:run -Dspring-boot.run.arguments="--consumer.type=rate"
mvn spring-boot:run -Dspring-boot.run.arguments="--consumer.type=pricing"
mvn spring-boot:run -Dspring-boot.run.arguments="--consumer.type=notifications"
mvn spring-boot:run -Dspring-boot.run.arguments="--consumer.type=audit"
```

## 📁 Estrutura de Código

### Producer

```
producer/src/main/java/br/com/iagoomes/producer/
├── ProducerApplication.java
├── application/
│   ├── controller/
│   │   └── FixedIncomeEventController.java
│   └── service/
│       └── FixedIncomeEventService.java
├── domain/dto/
│   └── FixedIncomeEventDto.java
└── infra/
    ├── config/
    │   └── RabbitMQFanoutConfig.java
    └── mqprovider/producer/
        └── FixedIncomeEventProducer.java
```

### Consumer

```
consumer/src/main/java/br/com/iagoomes/consumer/
├── ConsumerApplication.java
├── domain/dto/
│   └── FixedIncomeEventDto.java
└── infra/
    ├── config/
    │   └── RabbitMQFanoutConfig.java
    └── mqprovider/consumer/
        ├── RateConsumer.java
        ├── PricingConsumer.java
        ├── NotificationsConsumer.java
        └── AuditConsumer.java
```

## 📡 Endpoints da API

### Producer API (http://localhost:8080)

**Health Check**
```
GET /api/v1/events/health
```

**Enviar Evento de Taxa de Juros**
```bash
POST /api/v1/events/rate-change
Content-Type: application/json

{
  "eventType": "rate_change",
  "productType": "CDB",
  "oldRate": 12.5,
  "newRate": 13.0,
  "effectiveDate": "2025-10-24",
  "reason": "Selic aumentou"
}
```

**Enviar Evento de Precificação**
```bash
POST /api/v1/events/pricing-update
Content-Type: application/json

{
  "eventType": "pricing_update",
  "productType": "LCI",
  "newPrice": 98.75,
  "oldPrice": 98.50,
  "timestamp": "2025-10-24T15:30:00"
}
```

**Enviar Evento Genérico**
```bash
POST /api/v1/events/broadcast
Content-Type: application/json

{
  "eventType": "vencimento_proximos_dias",
  "description": "Vários ativos vencem nos próximos 7 dias",
  "severity": "HIGH"
}
```

**Status de Resposta:** 202 Accepted

## 📚 Fanout Exchange

### O que é Fanout Exchange?

**Fanout Exchange** é um tipo de exchange do RabbitMQ que roteia mensagens para **TODAS as filas vinculadas**, independentemente da routing key.

Diferente do Direct Exchange (exato) ou Topic Exchange (padrão), o Fanout Exchange simplesmente:
1. Recebe uma mensagem
2. Cria uma cópia para CADA fila vinculada
3. Envia para todas simultaneamente

### Quando Usar?

- 📢 Broadcasts / Notificações (ex: Taxa mudou!)
- 📊 Eventos que múltiplos sistemas precisam processar
- 🔔 Alertas em tempo real
- 📝 Auditoria centralizada
- 🎯 Padrão pub/sub simples

### Como Funciona

```
1 Evento é publicado no Fanout Exchange
                    ↓
        Exchange cria 3 CÓPIAS
                    ↓
    ┌────────────┬────────────┬────────────┐
    ↓            ↓            ↓            ↓
Queue1       Queue2       Queue3       Queue4
    ↓            ↓            ↓            ↓
Consumer1   Consumer2    Consumer3    Consumer4

✅ TODOS recebem a mesma mensagem!
```

### Configuração

No arquivo `RabbitMQFanoutConfig.java`:

```java
@Bean
public FanoutExchange fixedIncomeFanoutExchange() {
    return new FanoutExchange("rf-events.fanout", true, false);
}

@Bean
public Queue rateQueue() {
    return new Queue("rf.rate.queue", true);
}

@Bean
public Binding rateBinding() {
    return BindingBuilder.bind(rateQueue())
        .to(fixedIncomeFanoutExchange());
    // Note: Sem routing key! Fanout envia para TODAS
}
```

### Diferença: Direct vs Topic vs Fanout

| Exchange | Routing | Caso de Uso | Binding |
|----------|---------|-----------|---------|
| **Direct** | Exato | 1 mensagem → 1 queue | routing key exacta |
| **Topic** | Padrão | 1 mensagem → múltiplas queues por padrão | wildcards (* e #) |
| **Fanout** | Broadcast | 1 mensagem → TODAS as queues | SEM routing key |

## 🔄 Fluxo de Processamento

### Producer

- Expõe endpoint REST para enviar eventos
- Publica mensagem no Fanout Exchange (sem routing key)
- Não sabe quantos consumers existem
- Não sabe quem vai processar

### Consumer

- Escuta sua fila específica
- Recebe a mesma mensagem que outros consumers
- Processa independentemente
- Cada um tem seu próprio processamento

### Exemplo: Taxa de Juros Muda

```
1. Taxa Selic aumenta
   ↓
2. POST /api/v1/events/rate-change
   ↓
3. Producer publica no Fanout Exchange
   ↓
4. Exchange cria 4 CÓPIAS
   ↓
5. Rate Consumer → Atualiza BD de taxas
6. Pricing Consumer → Recalcula preços
7. Notifications Consumer → Envia emails/SMS
8. Audit Consumer → Registra no log
   ↓
9. Todos processam em PARALELO! ⚡
```

## 💡 Exemplos de Uso

### Exemplo 1: Mudança de Taxa

```bash
curl -X POST http://localhost:8080/api/v1/events/rate-change \
  -H "Content-Type: application/json" \
  -d '{
    "eventType": "rate_change",
    "productType": "CDB",
    "oldRate": 12.5,
    "newRate": 13.0,
    "effectiveDate": "2025-10-24",
    "reason": "Selic aumentou"
  }'
```

**Logs esperados (simultâneamente):**

```
rate-consumer | 🏠 [RATE CONSUMER] Taxa atualizada: CDB 12.5% → 13.0%
pricing-consumer | 💰 [PRICING CONSUMER] Recalculando preços...
notifications-consumer | 📧 [NOTIFICATIONS] Enviando alertas...
audit-consumer | 📝 [AUDIT] Evento registrado
```

### Exemplo 2: Múltiplos Eventos

```bash
# Evento 1
curl -X POST http://localhost:8080/api/v1/events/rate-change \
  -H "Content-Type: application/json" \
  -d '{"eventType": "rate_change", "productType": "CDB", ...}'

# Evento 2 (enquanto evento 1 está sendo processado)
curl -X POST http://localhost:8080/api/v1/events/pricing-update \
  -H "Content-Type: application/json" \
  -d '{"eventType": "pricing_update", "productType": "LCI", ...}'

# Evento 3
curl -X POST http://localhost:8080/api/v1/events/broadcast \
  -H "Content-Type: application/json" \
  -d '{"eventType": "vencimento_proximos_dias", ...}'
```

**Resultado:** Todos os 3 eventos propagam para TODOS os 4 consumers em tempo real! 🚀

## 📊 Monitoramento

### Ver logs do Consumer

```bash
docker-compose logs -f rate-consumer
docker-compose logs -f pricing-consumer
docker-compose logs -f notifications-consumer
docker-compose logs -f audit-consumer
```

### RabbitMQ Management UI

Acesse: [http://localhost:15672](http://localhost:15672)

**Credenciais:**
- Username: `guest`
- Password: `guest`

**O que você pode ver:**

1. **Exchanges**
   - Vá em Exchanges → `rf-events.fanout`
   - Veja que é tipo "fanout"
   - Veja todos os 4 bindings (sem routing key)

2. **Queues**
   - Vá em Queues
   - Veja as 4 filas:
     - `rf.rate.queue`
     - `rf.pricing.queue`
     - `rf.notifications.queue`
     - `rf.audit.queue`
   - Veja quantas mensagens cada uma tem
   - Use "Get Messages" para visualizar

3. **Connections**
   - Veja as conexões ativas do Producer e Consumers

## ⚙️ Configuração

### application.yml

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest

fixed-income:
  fanout:
    exchange:
      name: rf-events.fanout
      durable: true
    queues:
      rate:
        name: rf.rate.queue
        durable: true
      pricing:
        name: rf.pricing.queue
        durable: true
      notifications:
        name: rf.notifications.queue
        durable: true
      audit:
        name: rf.audit.queue
        durable: true
```

### Variáveis de Ambiente

Você pode sobrescrever as configurações via variáveis de ambiente:

```bash
SPRING_RABBITMQ_HOST=rabbitmq
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=guest
SPRING_RABBITMQ_PASSWORD=guest
```

## 🔧 Troubleshooting

### Verificar se as portas estão em uso

```bash
lsof -i :8080
lsof -i :8081
lsof -i :8082
lsof -i :8083
lsof -i :8084
lsof -i :5672
lsof -i :15672
```

### Parar containers antigos

```bash
docker-compose down -v
```

### Verificar logs do Producer

```bash
docker-compose logs -f producer-api
```

### Verificar conexão com RabbitMQ

- Acesse [http://localhost:15672](http://localhost:15672)
- Vá em Connections
- Deve haver uma conexão do Producer e múltiplas dos Consumers

### Consumers não estão recebendo mensagens

**Verificar:**
- Todos os consumers estão rodando?
  ```bash
  docker-compose ps
  ```
- Queues foram criadas?
  - Acesse [http://localhost:15672](http://localhost:15672) → Queues
  - Deve existir as 4 filas
- Exchange é do tipo fanout?
  - Acesse Exchanges → `rf-events.fanout`
  - Type deve ser "fanout"
- Bindings estão corretos?
  - Veja se há 4 bindings (um por queue)

**Possíveis causas:**
- Consumer travado (verificar logs)
- Erro na desserialização JSON
- Exchange não é fanout
- Bindings não existem

**Solução:**
```bash
# Reiniciar tudo
docker-compose down -v
docker-compose up --build
```

## 💡 Casos de Uso Reais

### 1. Taxa Selic Muda
```
Taxa Selic 11.5% → 11.75%
  ↓
Fanout para:
- Recalcular todas as precificações
- Notificar clientes
- Atualizar dashboards
- Registrar em auditoria
```

### 2. Evento de Liquidez
```
Mercado fecha crise de liquidez
  ↓
Fanout para:
- Congelar novas operações
- Alertar compliance
- Notificar tesouraria
- Logar evento crítico
```

### 3. Correção de Erro
```
Descoberto erro em processamento de vencimento
  ↓
Fanout para:
- Compensar transações
- Notificar clientes afetados
- Auditar correção
- Alertar risco
```

## 🎓 O que Você Aprendeu

✅ Fanout Exchange para broadcasts
✅ Múltiplos consumers processando simultaneamente
✅ Padrão pub/sub com RabbitMQ
✅ Escalabilidade horizontal de consumers
✅ Diferenças: Direct vs Topic vs Fanout
✅ Casos de uso reais em renda fixa

## 📚 Série de Exercícios

- **Exercício 1:** [registry-cdb-basic-concepts-mq](https://github.com/iagoomes/registry-cdb-basic-concepts-mq) - Fluxo básico
- **Exercício 2:** [registry-cdb-dlx-retry-mq](https://github.com/iagoomes/registry-cdb-dlx-retry-mq) - DLX e Retry
- **Exercício 3:** [fixed-income-topic-routing-mq](https://github.com/iagoomes/fixed-income-topic-routing-mq) - Topic Exchange
- **Exercício 4:** [fixed-income-fanout-exchange-mq](https://github.com/iagoomes/fixed-income-fanout-exchange-mq) - Fanout Exchange ← VOCÊ ESTÁ AQUI

**Próximos:**
- Exercício 5: Priority Queues
- Exercício 6: TTL (Time To Live)
- Exercício 7: Headers Exchange
- Exercício 8: Prefetch e Controle de Fluxo

## 👨‍💻 Autor

**Iago Gomes**
- GitHub: [@iagoomes](https://github.com/iagoomes)
- LinkedIn: [Iago Gomes](https://www.linkedin.com/in/deviagogomes)

⭐ Se este projeto te ajudou, deixe uma estrela no repositório!
