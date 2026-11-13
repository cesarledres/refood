
package br.com.fiap.refood.dao;

import br.com.fiap.refood.model.Pedido;
import br.com.fiap.refood.model.Usuario;
import br.com.fiap.refood.model.Estabelecimento;
import br.com.fiap.refood.model.enums.StatusPedido;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PedidoDAO {

    private final DataSource dataSource;

    public PedidoDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void inserir(Pedido pedido) throws SQLException {

        String sql = """
                INSERT INTO TB_PEDIDO
                (ID_USUARIO, ID_ESTABELECIMENTO, DT_PEDIDO,
                 VL_TOTAL, ST_PEDIDO)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, pedido.getUsuario().getId());
            stmt.setLong(2, pedido.getEstabelecimento().getId());

            stmt.setTimestamp(
                    3, Timestamp.valueOf(pedido.getDataPedido())
            );

            stmt.setDouble(4, pedido.getValorTotal());
            stmt.setString(5, pedido.getStatus().name());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    pedido.setId(rs.getLong(1));
                }
            }
        }
    }

    public Pedido buscarPorId(Long id) throws SQLException {

        String sql = """
                SELECT * FROM TB_PEDIDO
                WHERE ID_PEDIDO = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearPedido(rs);
                }
            }
        }

        return null;
    }

    public List<Pedido> listarTodos() throws SQLException {

        String sql = "SELECT * FROM TB_PEDIDO";

        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                pedidos.add(mapearPedido(rs));
            }
        }

        return pedidos;
    }

    public void atualizar(Pedido pedido) throws SQLException {

        String sql = """
                UPDATE TB_PEDIDO
                SET ID_USUARIO = ?,
                    ID_ESTABELECIMENTO = ?,
                    DT_PEDIDO = ?,
                    VL_TOTAL = ?,
                    ST_PEDIDO = ?
                WHERE ID_PEDIDO = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, pedido.getUsuario().getId());
            stmt.setLong(2, pedido.getEstabelecimento().getId());

            stmt.setTimestamp(
                    3, Timestamp.valueOf(pedido.getDataPedido())
            );

            stmt.setDouble(4, pedido.getValorTotal());
            stmt.setString(5, pedido.getStatus().name());
            stmt.setLong(6, pedido.getId());

            stmt.executeUpdate();
        }
    }

    public void excluir(Long id) throws SQLException {

        String sql = """
                DELETE FROM TB_PEDIDO
                WHERE ID_PEDIDO = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    private Pedido mapearPedido(ResultSet rs) throws SQLException {

        Pedido pedido = new Pedido();

        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("ID_USUARIO"));

        Estabelecimento estabelecimento = new Estabelecimento();
        estabelecimento.setId(rs.getLong("ID_ESTABELECIMENTO"));

        pedido.setId(rs.getLong("ID_PEDIDO"));
        pedido.setUsuario(usuario);
        pedido.setEstabelecimento(estabelecimento);

        pedido.setDataPedido(
                rs.getTimestamp("DT_PEDIDO").toLocalDateTime()
        );

        pedido.setValorTotal(rs.getDouble("VL_TOTAL"));

        pedido.setStatus(
                StatusPedido.valueOf(rs.getString("ST_PEDIDO"))
        );

        return pedido;
    }
}
