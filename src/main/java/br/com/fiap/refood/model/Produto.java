package br.com.fiap.refood.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Produto {
    private Long id;
    private Estabelecimento estabelecimento;
    private String nome;
    private String categoria;
    private Double precoOriginal;
    private Double precoPromocional;
    private Integer quantidadeEstoque;
    private LocalDate dataValidade;
    private String imagem;
    private List<ItemPedido> itensPedido = new ArrayList<>();
}