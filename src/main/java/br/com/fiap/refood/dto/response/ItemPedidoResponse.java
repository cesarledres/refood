package br.com.fiap.refood.dto.response;

public record ItemPedidoResponse(
        Long id,
        Long produtoId,
        String nomeProduto,
        Integer quantidade,
        Double valorUnitario,
        Double valorSubtotal
) {}