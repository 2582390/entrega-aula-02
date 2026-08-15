package br.pucminas.aed.pedidos.service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Repository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.pucminas.aed.pedidos.domain.PedidoConfirmadoEvent;

/**
 * Publisher do evento de dominio: PedidoConfirmado
 * 
 * Publica com envelope CloudEvents 1.0 em modo binario: os quatro atributos
 * obrigatorios (ce_specversion, ce_id, ce_source, ce_type) nos cabecalhos,
 * mais ce_time. O corpo da mensagem e o JSON do evento.
 * 
 * Type e versionado: dominio.entidade.fato.v1
 * 
 * A chave de particao e o pedidoId: a menor unidade cuja ordem o negocio exige.
 */
@Repository
public class PedidoPublisher {

	private static final Logger log = LoggerFactory.getLogger(PedidoPublisher.class);
	private static final String TOPIC = "pedidos-confirmados";
	private static final String CLOUDEVENTS_VERSION = "1.0";
	private static final String EVENT_TYPE = "pedidos.pedido.confirmado.v1";
	private static final String EVENT_SOURCE = "pedidos-service";

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;

	public PedidoPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
	}

	public void publicar(PedidoConfirmadoEvent evento) {
		try {
			// Serializar o evento para JSON
			String eventJson = objectMapper.writeValueAsString(evento);

			// Construir o instante para ce_time em ISO-8601
			String ceTime = DateTimeFormatter.ISO_INSTANT
				.format(evento.getTimestamp().atOffset(ZoneOffset.UTC));

			// Construir a mensagem com headers CloudEvents (modo binario)
			Message<String> message = MessageBuilder
				.withPayload(eventJson)
				.setHeader("ce_specversion", CLOUDEVENTS_VERSION)
				.setHeader("ce_id", evento.getEventoId())
				.setHeader("ce_source", EVENT_SOURCE)
				.setHeader("ce_type", EVENT_TYPE)
				.setHeader("ce_time", ceTime)
				.setHeader(KafkaHeaders.TOPIC, TOPIC)
				.setHeader(KafkaHeaders.MESSAGE_KEY, evento.getPedidoId())
				.build();

			// Enviar: o retorno tem dono
			kafkaTemplate.send(message)
				.whenComplete((result, ex) -> {
					if (ex != null) {
						log.error("Erro ao publicar pedido {}: {}", evento.getPedidoId(), ex.getMessage());
						throw new RuntimeException("Falha na publicacao do evento", ex);
					} else {
						log.info("Evento publicado com sucesso. Particao: {}, Offset: {}",
							result.getRecordMetadata().partition(),
							result.getRecordMetadata().offset());
					}
				});

		} catch (JsonProcessingException e) {
			log.error("Erro ao serializar evento: {}", e.getMessage());
			throw new RuntimeException("Falha na serializacao do evento", e);
		}
	}
}
