package br.com.fiap.refood.dto.response;

import br.com.fiap.refood.model.enums.TipoPagamento;

import java.time.LocalDateTime;

public record PagamentoResponse(
        Long id,
        Long pedidoId,
        TipoPagamento tipoPagamento,
        Double valorPagamento,
        LocalDateTime dataPagamento
) {}