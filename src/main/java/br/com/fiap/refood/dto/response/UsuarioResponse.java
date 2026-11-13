package br.com.fiap.refood.dto.response;

import br.com.fiap.refood.model.enums.TipoUsuario;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        TipoUsuario tipoUsuario
) {}