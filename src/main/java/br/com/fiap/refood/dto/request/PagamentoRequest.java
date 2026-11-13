
package br.com.fiap.refood.dto.request;

import br.com.fiap.refood.model.enums.TipoPagamento;
import jakarta.validation.constraints.NotNull;

public record PagamentoRequest(

        @NotNull
        Long pedidoId,

        @NotNull
        TipoPagamento tipoPagamento

) {}
