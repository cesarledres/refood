
package br.com.fiap.refood.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EstabelecimentoRequest(

        @NotNull
        Long usuarioId,

        @NotBlank
        @Size(max = 120)
        String nome,

        @NotBlank
        @Size(max = 18)
        String cnpj,

        @NotBlank
        @Size(max = 255)
        String endereco,

        @Size(max = 10)
        String cep,

        @Size(max = 20)
        String telefone,

        @Size(max = 500)
        String descricao

) {}
