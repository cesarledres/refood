package br.com.fiap.refood.dto.response;

import br.com.fiap.refood.model.enums.StatusPedido;

import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        Long usuarioId,
        Long estabelecimentoId,
        LocalDateTime dataPedido,
        Double valorTotal,
        StatusPedido status,
        List<ItemPedidoResponse> itens
) {}