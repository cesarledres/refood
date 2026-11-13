
package br.com.fiap.refood.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PedidoRequest(

        @NotNull
        Long usuarioId,

        @NotNull
        Long estabelecimentoId,

        @NotEmpty
        List<@Valid ItemPedidoRequest> itens

) {}
