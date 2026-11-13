
package br.com.fiap.refood.dao;

import br.com.fiap.refood.model.Produto;
import br.com.fiap.refood.model.Estabelecimento;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProdutoDAO {

    private final DataSource dataSource;

    public ProdutoDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void inserir(Produto produto) throws SQLException {

        String sql = """
                INSERT INTO TB_PRODUTO
                (ID_ESTABELECIMENTO, NM_PRODUTO, DS_CATEGORIA,
                 VL_PRECO_ORIGINAL, VL_PRECO_PROMOCIONAL,
                 QT_ESTOQUE, DT_VALIDADE, DS_IMAGEM)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, produto.getEstabelecimento().getId());
            stmt.setString(2, produto.getNome());
            stmt.setString(3, produto.getCategoria());
            stmt.setDouble(4, produto.getPrecoOriginal());
            stmt.setDouble(5, produto.getPrecoPromocional());
            stmt.setInt(6, produto.getQuantidadeEstoque());
            stmt.setDate(7, Date.valueOf(produto.getDataValidade()));
            stmt.setString(8, produto.getImagem());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    produto.setId(rs.getLong(1));
                }
            }
        }
    }

    public Produto buscarPorId(Long id) throws SQLException {

        String sql = """
                SELECT * FROM TB_PRODUTO
                WHERE ID_PRODUTO = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProduto(rs);
                }
            }
        }

        return null;
    }

    public List<Produto> listarTodos() throws SQLException {

        String sql = "SELECT * FROM TB_PRODUTO";

        List<Produto> produtos = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                produtos.add(mapearProduto(rs));
            }
        }

        return produtos;
    }

    public void atualizar(Produto produto) throws SQLException {

        String sql = """
                UPDATE TB_PRODUTO
                SET ID_ESTABELECIMENTO = ?,
                    NM_PRODUTO = ?,
                    DS_CATEGORIA = ?,
                    VL_PRECO_ORIGINAL = ?,
                    VL_PRECO_PROMOCIONAL = ?,
                    QT_ESTOQUE = ?,
                    DT_VALIDADE = ?,
                    DS_IMAGEM = ?
                WHERE ID_PRODUTO = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, produto.getEstabelecimento().getId());
            stmt.setString(2, produto.getNome());
            stmt.setString(3, produto.getCategoria());
            stmt.setDouble(4, produto.getPrecoOriginal());
            stmt.setDouble(5, produto.getPrecoPromocional());
            stmt.setInt(6, produto.getQuantidadeEstoque());
            stmt.setDate(7, Date.valueOf(produto.getDataValidade()));
            stmt.setString(8, produto.getImagem());
            stmt.setLong(9, produto.getId());

            stmt.executeUpdate();
        }
    }

    public void excluir(Long id) throws SQLException {

        String sql = """
                DELETE FROM TB_PRODUTO
                WHERE ID_PRODUTO = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    private Produto mapearProduto(ResultSet rs) throws SQLException {

        Produto produto = new Produto();

        Estabelecimento estabelecimento = new Estabelecimento();
        estabelecimento.setId(rs.getLong("ID_ESTABELECIMENTO"));

        produto.setId(rs.getLong("ID_PRODUTO"));
        produto.setEstabelecimento(estabelecimento);
        produto.setNome(rs.getString("NM_PRODUTO"));
        produto.setCategoria(rs.getString("DS_CATEGORIA"));
        produto.setPrecoOriginal(rs.getDouble("VL_PRECO_ORIGINAL"));
        produto.setPrecoPromocional(rs.getDouble("VL_PRECO_PROMOCIONAL"));
        produto.setQuantidadeEstoque(rs.getInt("QT_ESTOQUE"));
        produto.setDataValidade(
                rs.getDate("DT_VALIDADE").toLocalDate()
        );
        produto.setImagem(rs.getString("DS_IMAGEM"));

        return produto;
    }
}
