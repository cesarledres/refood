package br.com.fiap.refood.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_ITEM_PEDIDO")
@Getter
@Setter
@NoArgsConstructor
public class ItemPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ITEM")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_PEDIDO", nullable = false)
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "ID_PRODUTO", nullable = false)
    private Produto produto;

    @Column(name = "QT_PRODUTO", nullable = false)
    private Integer quantidade;

    @Column(name = "VL_UNITARIO", nullable = false, precision = 10)
    private Double valorUnitario;

    @Column(name = "VL_SUBTOTAL", nullable = false, precision = 10)
    private Double valorSubtotal;
}