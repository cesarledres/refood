
package br.com.fiap.refood.dao;

import br.com.fiap.refood.model.Estabelecimento;
import br.com.fiap.refood.model.Usuario;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EstabelecimentoDAO {

    private final DataSource dataSource;

    public EstabelecimentoDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void inserir(Estabelecimento estabelecimento) throws SQLException {

        String sql = """
                INSERT INTO TB_ESTABELECIMENTO
                (ID_USUARIO, NM_ESTABELECIMENTO, CNPJ,
                 DS_ENDERECO, NR_CEP, NR_TELEFONE, DS_DESCRICAO)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     sql,
                     new String[]{"ID_ESTABELECIMENTO"}
             )) {

            stmt.setLong(1, estabelecimento.getUsuario().getId());
            stmt.setString(2, estabelecimento.getNome());
            stmt.setString(3, estabelecimento.getCnpj());
            stmt.setString(4, estabelecimento.getEndereco());
            stmt.setString(5, estabelecimento.getCep());
            stmt.setString(6, estabelecimento.getTelefone());
            stmt.setString(7, estabelecimento.getDescricao());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    estabelecimento.setId(rs.getLong(1));
                }
            }
        }
    }

    public Estabelecimento buscarPorId(Long id) throws SQLException {

        String sql = """
                SELECT * FROM TB_ESTABELECIMENTO
                WHERE ID_ESTABELECIMENTO = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearEstabelecimento(rs);
                }
            }
        }

        return null;
    }

    public List<Estabelecimento> listarTodos() throws SQLException {

        String sql = "SELECT * FROM TB_ESTABELECIMENTO";

        List<Estabelecimento> estabelecimentos = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                estabelecimentos.add(mapearEstabelecimento(rs));
            }
        }

        return estabelecimentos;
    }

    public void atualizar(Estabelecimento estabelecimento) throws SQLException {

        String sql = """
                UPDATE TB_ESTABELECIMENTO
                SET ID_USUARIO = ?,
                    NM_ESTABELECIMENTO = ?,
                    CNPJ = ?,
                    DS_ENDERECO = ?,
                    NR_CEP = ?,
                    NR_TELEFONE = ?,
                    DS_DESCRICAO = ?
                WHERE ID_ESTABELECIMENTO = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, estabelecimento.getUsuario().getId());
            stmt.setString(2, estabelecimento.getNome());
            stmt.setString(3, estabelecimento.getCnpj());
            stmt.setString(4, estabelecimento.getEndereco());
            stmt.setString(5, estabelecimento.getCep());
            stmt.setString(6, estabelecimento.getTelefone());
            stmt.setString(7, estabelecimento.getDescricao());
            stmt.setLong(8, estabelecimento.getId());

            stmt.executeUpdate();
        }
    }

    public void excluir(Long id) throws SQLException {

        String sql = """
                DELETE FROM TB_ESTABELECIMENTO
                WHERE ID_ESTABELECIMENTO = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    private Estabelecimento mapearEstabelecimento(ResultSet rs)
            throws SQLException {

        Estabelecimento estabelecimento = new Estabelecimento();

        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("ID_USUARIO"));

        estabelecimento.setId(rs.getLong("ID_ESTABELECIMENTO"));
        estabelecimento.setUsuario(usuario);
        estabelecimento.setNome(rs.getString("NM_ESTABELECIMENTO"));
        estabelecimento.setCnpj(rs.getString("CNPJ"));
        estabelecimento.setEndereco(rs.getString("DS_ENDERECO"));
        estabelecimento.setCep(rs.getString("NR_CEP"));
        estabelecimento.setTelefone(rs.getString("NR_TELEFONE"));
        estabelecimento.setDescricao(rs.getString("DS_DESCRICAO"));

        return estabelecimento;
    }
}
