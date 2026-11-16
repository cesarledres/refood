package br.com.fiap.refood.model;

import br.com.fiap.refood.model.enums.TipoUsuario;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Usuario {
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private TipoUsuario tipoUsuario;
    private Estabelecimento estabelecimento;
    private List<Pedido> pedidos = new ArrayList<>();
}