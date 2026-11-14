
package br.com.fiap.refood.dao;

import br.com.fiap.refood.model.Pagamento;
import br.com.fiap.refood.model.Pedido;
import br.com.fiap.refood.model.enums.TipoPagamento;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PagamentoDAO {

    private final DataSource dataSource;

    public PagamentoDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void inserir(Pagamento pagamento) throws SQLException {

        String sql = """
                INSERT INTO TB_PAGAMENTO
                (ID_PEDIDO, TP_PAGAMENTO, VL_PAGAMENTO, DT_PAGAMENTO)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     sql,
                     new String[]{"ID_PAGAMENTO"}
             )) {

            stmt.setLong(1, pagamento.getPedido().getId());
            stmt.setString(2, pagamento.getTipoPagamento().name());
            stmt.setDouble(3, pagamento.getValorPagamento());

            if (pagamento.getDataPagamento() != null) {
                stmt.setTimestamp(
                        4, Timestamp.valueOf(pagamento.getDataPagamento())
                );
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    pagamento.setId(rs.getLong(1));
                }
            }
        }
    }

    public Pagamento buscarPorId(Long id) throws SQLException {

        String sql = """
                SELECT * FROM TB_PAGAMENTO
                WHERE ID_PAGAMENTO = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearPagamento(rs);
                }
            }
        }

        return null;
    }

    public Pagamento buscarPorPedido(Long idPedido) throws SQLException {

        String sql = """
                SELECT * FROM TB_PAGAMENTO
                WHERE ID_PEDIDO = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idPedido);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearPagamento(rs);
                }
            }
        }

        return null;
    }

    public List<Pagamento> listarTodos() throws SQLException {

        String sql = "SELECT * FROM TB_PAGAMENTO";

        List<Pagamento> pagamentos = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                pagamentos.add(mapearPagamento(rs));
            }
        }

        return pagamentos;
    }

    public void atualizar(Pagamento pagamento) throws SQLException {

        String sql = """
                UPDATE TB_PAGAMENTO
                SET ID_PEDIDO = ?,
                    TP_PAGAMENTO = ?,
                    VL_PAGAMENTO = ?,
                    DT_PAGAMENTO = ?
                WHERE ID_PAGAMENTO = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, pagamento.getPedido().getId());
            stmt.setString(2, pagamento.getTipoPagamento().name());
            stmt.setDouble(3, pagamento.getValorPagamento());

            if (pagamento.getDataPagamento() != null) {
                stmt.setTimestamp(
                        4, Timestamp.valueOf(pagamento.getDataPagamento())
                );
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }

            stmt.setLong(5, pagamento.getId());

            stmt.executeUpdate();
        }
    }

    public void excluir(Long id) throws SQLException {

        String sql = """
                DELETE FROM TB_PAGAMENTO
                WHERE ID_PAGAMENTO = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    private Pagamento mapearPagamento(ResultSet rs) throws SQLException {

        Pagamento pagamento = new Pagamento();

        Pedido pedido = new Pedido();
        pedido.setId(rs.getLong("ID_PEDIDO"));

        Timestamp data = rs.getTimestamp("DT_PAGAMENTO");

        pagamento.setId(rs.getLong("ID_PAGAMENTO"));
        pagamento.setPedido(pedido);

        pagamento.setTipoPagamento(
                TipoPagamento.valueOf(rs.getString("TP_PAGAMENTO"))
        );

        pagamento.setValorPagamento(rs.getDouble("VL_PAGAMENTO"));

        if (data != null) {
            pagamento.setDataPagamento(data.toLocalDateTime());
        }

        return pagamento;
    }
}
