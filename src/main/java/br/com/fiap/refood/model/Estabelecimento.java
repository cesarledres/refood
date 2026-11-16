package br.com.fiap.refood.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Estabelecimento {
    private Long id;
    private Usuario usuario;
    private String nome;
    private String cnpj;
    private String endereco;
    private String cep;
    private String telefone;
    private String descricao;
    private List<Produto> produtos = new ArrayList<>();
    private List<Pedido> pedidos = new ArrayList<>();
}