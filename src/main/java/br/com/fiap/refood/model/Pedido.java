package br.com.fiap.refood.model;

import br.com.fiap.refood.model.enums.StatusPedido;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_PEDIDO")
@Getter
@Setter
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PEDIDO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ESTABELECIMENTO", nullable = false)
    private Estabelecimento estabelecimento;

    @Column(name = "DT_PEDIDO", nullable = false)
    private LocalDateTime dataPedido;

    @Column(name = "VL_TOTAL", nullable = false, precision = 10, scale = 2)
    private Double valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "ST_PEDIDO", nullable = false, length = 20)
    private StatusPedido status = StatusPedido.PENDENTE;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Pagamento pagamento;

    @PrePersist
    public void prePersist() {
        if (dataPedido == null) {
            dataPedido = LocalDateTime.now();
        }
    }
}