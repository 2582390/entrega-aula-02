package br.pucminas.aed.pedidos.controller;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.pucminas.aed.pedidos.domain.ItemDoPedidoVO;
import br.pucminas.aed.pedidos.domain.PedidoConfirmadoEvent;
import br.pucminas.aed.pedidos.service.PedidoService;

/**
 * Controller que dispara a confirmacao de pedido.
 * 
 * A API HTTP responde 202 (Accepted) quando o efeito ainda nao aconteceu —
 * o evento foi aceitoe vai ser processado, mas a confirmacao e assincrona.
 */
@RestController
@RequestMapping("/pedidos")
public class PedidoController {

	private static final Logger log = LoggerFactory.getLogger(PedidoController.class);
	private final PedidoService pedidoService;

	public PedidoController(PedidoService pedidoService) {
		this.pedidoService = pedidoService;
	}

	@PostMapping("/confirmados")
	public ResponseEntity<Void> confirmarPedido(@RequestBody PedidoRequest request) {
		try {
			log.info("Requisicao para confirmar pedido: {}", request.getPedidoId());

			// Construir o evento a partir da requisicao
			List<ItemDoPedidoVO> itens = request.getItens().stream()
				.map(item -> new ItemDoPedidoVO(item.getSku(), item.getQuantidade(), item.getPreco()))
				.toList();

			PedidoConfirmadoEvent evento = new PedidoConfirmadoEvent(
				UUID.randomUUID().toString(),
				request.getPedidoId(),
				request.getCliente(),
				itens,
				request.getTotal(),
				Instant.now()
			);

			// Delegar ao servico
			pedidoService.confirmarPedido(evento);

			// Responder 202: o evento foi aceito e sera processado de forma assincrona
			return ResponseEntity.status(HttpStatus.ACCEPTED).build();
		} catch (Exception e) {
			log.error("Erro ao confirmar pedido: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	// DTO para a requisicao
	public static class PedidoRequest {
		private String pedidoId;
		private String cliente;
		private List<ItemRequest> itens;
		private Double total;

		public String getPedidoId() {
			return pedidoId;
		}

		public void setPedidoId(String pedidoId) {
			this.pedidoId = pedidoId;
		}

		public String getCliente() {
			return cliente;
		}

		public void setCliente(String cliente) {
			this.cliente = cliente;
		}

		public List<ItemRequest> getItens() {
			return itens;
		}

		public void setItens(List<ItemRequest> itens) {
			this.itens = itens;
		}

		public Double getTotal() {
			return total;
		}

		public void setTotal(Double total) {
			this.total = total;
		}
	}

	public static class ItemRequest {
		private String sku;
		private Integer quantidade;
		private Double preco;

		public String getSku() {
			return sku;
		}

		public void setSku(String sku) {
			this.sku = sku;
		}

		public Integer getQuantidade() {
			return quantidade;
		}

		public void setQuantidade(Integer quantidade) {
			this.quantidade = quantidade;
		}

		public Double getPreco() {
			return preco;
		}

		public void setPreco(Double preco) {
			this.preco = preco;
		}
	}
}
