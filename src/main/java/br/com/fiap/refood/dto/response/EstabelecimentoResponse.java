package br.com.fiap.refood.dto.response;

public record EstabelecimentoResponse(
        Long id,
        Long usuarioId,
        String nome,
        String cnpj,
        String endereco,
        String cep,
        String telefone,
        String descricao
) {}