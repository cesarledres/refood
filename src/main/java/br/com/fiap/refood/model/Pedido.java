package br.com.fiap.refood.model;

import br.com.fiap.refood.model.enums.StatusPedido;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Pedido {
    private Long id;
    private Usuario usuario;
    private Estabelecimento estabelecimento;
    private LocalDateTime dataPedido = LocalDateTime.now();;
    private Double valorTotal;
    private StatusPedido status = StatusPedido.PENDENTE;
    private List<ItemPedido> itens = new ArrayList<>();
    private Pagamento pagamento;
}