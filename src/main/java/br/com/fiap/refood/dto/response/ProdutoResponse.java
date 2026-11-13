package br.com.fiap.refood.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProdutoResponse(
        Long id,
        Long estabelecimentoId,
        String nome,
        String categoria,
        Double precoOriginal,
        Double precoPromocional,
        Integer quantidadeEstoque,
        LocalDate dataValidade,
        String imagem
) {}