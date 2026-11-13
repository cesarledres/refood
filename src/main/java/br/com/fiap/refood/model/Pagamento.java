package br.com.fiap.refood.model;

import br.com.fiap.refood.model.enums.TipoPagamento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_PAGAMENTO")
@Getter
@Setter
@NoArgsConstructor
public class Pagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PAGAMENTO")
    private Long id;

    @OneToOne
    @JoinColumn(name = "ID_PEDIDO", nullable = false, unique = true)
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "TP_PAGAMENTO", nullable = false, length = 30)
    private TipoPagamento tipoPagamento;

    @Column(name = "VL_PAGAMENTO", nullable = false, precision = 10)
    private Double valorPagamento;

    @Column(name = "DT_PAGAMENTO")
    private LocalDateTime dataPagamento;
}