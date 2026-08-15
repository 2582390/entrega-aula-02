package br.pucminas.aed.pedidos.domain;

public class ItemDoPedidoVO {
  private final String sku;
  private final Integer quantidade;
  private final Double preco;

  public ItemDoPedidoVO(String sku, Integer quantidade, Double preco) {
    this.sku = sku;
    this.quantidade = quantidade;
    this.preco = preco;
  }

  public String getSku() {
    return sku;
  }

  public Integer getQuantidade() {
    return quantidade;
  }

  public Double getPreco() {
    return preco;
  }

  @Override
  public String toString() {
    return "ItemDoPedidoVO{" +
        "sku='" + sku + '\'' +
        ", quantidade=" + quantidade +
        ", preco=" + preco +
        '}';
  }
}
