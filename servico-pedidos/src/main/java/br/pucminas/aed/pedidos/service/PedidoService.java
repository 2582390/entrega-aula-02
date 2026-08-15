package br.pucminas.aed.pedidos.service;

import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import br.pucminas.aed.pedidos.domain.PedidoConfirmadoEvent;

/**
 * Servico de aplicacao: orquestra a confirmacao de pedido e a publicacao
 * do evento de dominio.
 * 
 * A transacao comeca e termina aqui. O listener Kafka e um adaptador de
 * entrada: ele recebe, delega no servico, trata excecoes e confirma offset.
 */
@Service
public class PedidoService {

	private static final Logger log = LoggerFactory.getLogger(PedidoService.class);
	private final PedidoPublisher pedidoPublisher;

	public PedidoService(PedidoPublisher pedidoPublisher) {
		this.pedidoPublisher = pedidoPublisher;
	}

	public void confirmarPedido(PedidoConfirmadoEvent evento) {
		log.info("Confirmando pedido: {}", evento.getPedidoId());
		
		// A validacao de negocio aconteceria aqui.
		// Por enquanto, a regra de negocio e simples: aceitar se total > 0.
		if (evento.getTotal() <= 0) {
			throw new IllegalArgumentException("Total do pedido deve ser maior que zero");
		}

		// Publicar o evento. O retorno do send() tem dono: eh tratado na
		// classe PedidoPublisher.
		pedidoPublisher.publicar(evento);
		log.info("Pedido {} publicado com sucesso", evento.getPedidoId());
	}
}
