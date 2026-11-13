
package br.com.fiap.refood.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProdutoRequest(

        @NotNull
        Long estabelecimentoId,

        @NotBlank
        @Size(max = 120)
        String nome,

        @NotBlank
        @Size(max = 50)
        String categoria,

        @NotNull
        @DecimalMin("0.01")
        Double precoOriginal,

        @NotNull
        @DecimalMin("0.01")
        Double precoPromocional,

        @NotNull
        @PositiveOrZero
        Integer quantidadeEstoque,

        @NotNull
        LocalDate dataValidade,

        @Size(max = 500)
        String imagem

) {}
