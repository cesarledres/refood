package br.com.fiap.refood.model;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class ItemPedido {
    private Long id;
    private Pedido pedido;
    private Produto produto;
    private Integer quantidade;
    private Double valorUnitario;
    private Double valorSubtotal;
}