package br.com.fiap.refood.dto.request;

import br.com.fiap.refood.model.enums.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(

        @NotBlank
        @Size(max = 100)
        String nome,

        @NotBlank
        @Email
        @Size(max = 150)
        String email,

        @NotBlank
        @Size(min = 6, max = 255)
        String senha,

        @NotNull
        TipoUsuario tipoUsuario

) {
}