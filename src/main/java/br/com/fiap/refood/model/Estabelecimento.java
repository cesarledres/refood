package br.com.fiap.refood.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_ESTABELECIMENTO")
@Getter
@Setter
@NoArgsConstructor
public class Estabelecimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ESTABELECIMENTO")
    private Long id;

    @OneToOne
    @JoinColumn(name = "ID_USUARIO", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "NM_ESTABELECIMENTO", nullable = false, length = 120)
    private String nome;

    @Column(name = "CNPJ", nullable = false, unique = true, length = 18)
    private String cnpj;

    @Column(name = "DS_ENDERECO", nullable = false, length = 255)
    private String endereco;

    @Column(name = "NR_CEP", length = 10)
    private String cep;

    @Column(name = "NR_TELEFONE", length = 20)
    private String telefone;

    @Column(name = "DS_DESCRICAO", length = 500)
    private String descricao;

    @OneToMany(mappedBy = "estabelecimento")
    private List<Produto> produtos = new ArrayList<>();

    @OneToMany(mappedBy = "estabelecimento")
    private List<Pedido> pedidos = new ArrayList<>();
}