package br.com.fiap.refood.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_PRODUTO")
@Getter
@Setter
@NoArgsConstructor
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PRODUTO")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_ESTABELECIMENTO", nullable = false)
    private Estabelecimento estabelecimento;

    @Column(name = "NM_PRODUTO", nullable = false, length = 120)
    private String nome;

    @Column(name = "DS_CATEGORIA", nullable = false, length = 50)
    private String categoria;

    @Column(name = "VL_PRECO_ORIGINAL", nullable = false, precision = 10)
    private Double precoOriginal;

    @Column(name = "VL_PRECO_PROMOCIONAL", nullable = false, precision = 10)
    private Double precoPromocional;

    @Column(name = "QT_ESTOQUE", nullable = false)
    private Integer quantidadeEstoque;

    @Column(name = "DT_VALIDADE", nullable = false)
    private LocalDate dataValidade;

    @Column(name = "DS_IMAGEM", length = 500)
    private String imagem;

    @OneToMany(mappedBy = "produto")
    private List<ItemPedido> itensPedido = new ArrayList<>();
}