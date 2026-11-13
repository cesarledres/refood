package br.com.fiap.refood.model;

import br.com.fiap.refood.model.enums.TipoUsuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_USUARIO")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USUARIO")
    private Long id;

    @Column(name = "NM_USUARIO", nullable = false, length = 100)
    private String nome;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "SENHA", nullable = false, length = 255)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_USUARIO", nullable = false, length = 20)
    private TipoUsuario tipoUsuario;

    @OneToOne(mappedBy = "usuario")
    private Estabelecimento estabelecimento;

    @OneToMany(mappedBy = "usuario")
    private List<Pedido> pedidos = new ArrayList<>();
}