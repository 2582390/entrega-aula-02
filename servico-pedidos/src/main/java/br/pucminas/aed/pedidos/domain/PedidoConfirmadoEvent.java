package br.pucminas.aed.pedidos.domain;

import java.time.Instant;
import java.util.List;

/**
 * Evento de dominio: Pedido Confirmado
 * 
 * Classe imutavel explicita — campos private final, sem setter, copia defensiva
 * de colecao. Nao usamos record: o objetivo e que os mecanismos fiquem a vista.
 * 
 * Identidade propria: um eventoId distinto do id da entidade de negocio (pedidoId).
 * 
 * Datas em ISO-8601, nunca epoch. O contrato e o JSON no fio.
 */
public class PedidoConfirmadoEvent {

	private final String eventoId;
	private final String pedidoId;
	private final String cliente;
	private final List<ItemDoPedidoVO> itens;
	private final Double total;
	private final Instant timestamp;

	public PedidoConfirmadoEvent(
		String eventoId,
		String pedidoId, 
		String cliente,
		List<ItemDoPedidoVO> itens,
		Double total,
		Instant timestamp
	) {
		this.eventoId = eventoId;
		this.pedidoId = pedidoId;
		this.cliente = cliente;
		this.itens = itens != null ? List.copyOf(itens) : List.of();
		this.total = total;
		this.timestamp = timestamp;
	}

	public String getEventoId() {
		return eventoId;
	}

	public String getPedidoId() {
		return pedidoId;
	}

	public String getCliente() {
		return cliente;
	}

	public List<ItemDoPedidoVO> getItens() {
		return itens;
	}

	public Double getTotal() {
		return total;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return "PedidoConfirmadoEvent{" +
			"eventoId='" + eventoId + '\'' +
			", pedidoId='" + pedidoId + '\'' +
			", cliente='" + cliente + '\'' +
			", itens=" + itens +
			", total=" + total +
			", timestamp=" + timestamp +
			'}';
	}
}
