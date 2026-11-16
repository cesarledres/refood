package br.com.fiap.refood.model;

import br.com.fiap.refood.model.enums.TipoPagamento;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Pagamento {
    private Long id;
    private Pedido pedido;
    private TipoPagamento tipoPagamento;
    private Double valorPagamento;
    private LocalDateTime dataPagamento;
}