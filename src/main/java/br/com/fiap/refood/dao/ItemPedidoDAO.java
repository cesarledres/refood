
package br.com.fiap.refood.dao;

import br.com.fiap.refood.model.ItemPedido;
import br.com.fiap.refood.model.Pedido;
import br.com.fiap.refood.model.Produto;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ItemPedidoDAO {

    private final DataSource dataSource;

    public ItemPedidoDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void inserir(ItemPedido item) throws SQLException {

        String sql = """
                INSERT INTO TB_ITEM_PEDIDO
                (ID_PEDIDO, ID_PRODUTO, QT_PRODUTO,
                 VL_UNITARIO, VL_SUBTOTAL)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, item.getPedido().getId());
            stmt.setLong(2, item.getProduto().getId());
            stmt.setInt(3, item.getQuantidade());
            stmt.setDouble(4, item.getValorUnitario());
            stmt.setDouble(5, item.getValorSubtotal());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    item.setId(rs.getLong(1));
                }
            }
        }
    }

    public ItemPedido buscarPorId(Long id) throws SQLException {

        String sql = """
                SELECT * FROM TB_ITEM_PEDIDO
                WHERE ID_ITEM = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearItem(rs);
                }
            }
        }

        return null;
    }

    public List<ItemPedido> listarTodos() throws SQLException {

        String sql = "SELECT * FROM TB_ITEM_PEDIDO";

        List<ItemPedido> itens = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                itens.add(mapearItem(rs));
            }
        }

        return itens;
    }

    public List<ItemPedido> listarPorPedido(Long idPedido)
            throws SQLException {

        String sql = """
                SELECT * FROM TB_ITEM_PEDIDO
                WHERE ID_PEDIDO = ?
                """;

        List<ItemPedido> itens = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idPedido);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    itens.add(mapearItem(rs));
                }
            }
        }

        return itens;
    }

    public void atualizar(ItemPedido item) throws SQLException {

        String sql = """
                UPDATE TB_ITEM_PEDIDO
                SET ID_PEDIDO = ?,
                    ID_PRODUTO = ?,
                    QT_PRODUTO = ?,
                    VL_UNITARIO = ?,
                    VL_SUBTOTAL = ?
                WHERE ID_ITEM = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, item.getPedido().getId());
            stmt.setLong(2, item.getProduto().getId());
            stmt.setInt(3, item.getQuantidade());
            stmt.setDouble(4, item.getValorUnitario());
            stmt.setDouble(5, item.getValorSubtotal());
            stmt.setLong(6, item.getId());

            stmt.executeUpdate();
        }
    }

    public void excluir(Long id) throws SQLException {

        String sql = """
                DELETE FROM TB_ITEM_PEDIDO
                WHERE ID_ITEM = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    private ItemPedido mapearItem(ResultSet rs) throws SQLException {

        ItemPedido item = new ItemPedido();

        Pedido pedido = new Pedido();
        pedido.setId(rs.getLong("ID_PEDIDO"));

        Produto produto = new Produto();
        produto.setId(rs.getLong("ID_PRODUTO"));

        item.setId(rs.getLong("ID_ITEM"));
        item.setPedido(pedido);
        item.setProduto(produto);
        item.setQuantidade(rs.getInt("QT_PRODUTO"));
        item.setValorUnitario(rs.getDouble("VL_UNITARIO"));
        item.setValorSubtotal(rs.getDouble("VL_SUBTOTAL"));

        return item;
    }
}
