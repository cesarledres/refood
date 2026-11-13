package br.com.fiap.refood.dao;

import br.com.fiap.refood.model.Usuario;
import br.com.fiap.refood.model.enums.TipoUsuario;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UsuarioDAO {

    private final DataSource dataSource;

    public UsuarioDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void inserir(Usuario usuario) throws SQLException {

        String sql = """
                INSERT INTO TB_USUARIO
                (NM_USUARIO, EMAIL, SENHA, TIPO_USUARIO)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     sql,
                     new String[]{"ID_USUARIO"}
             )) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getTipoUsuario().name());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setId(rs.getLong(1));
                }
            }
        }
    }

    public Usuario buscarPorId(Long id) throws SQLException {

        String sql = """
                SELECT * FROM TB_USUARIO
                WHERE ID_USUARIO = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        }

        return null;
    }

    public List<Usuario> listarTodos() throws SQLException {

        String sql = "SELECT * FROM TB_USUARIO";

        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        }

        return usuarios;
    }

    public void atualizar(Usuario usuario) throws SQLException {

        String sql = """
                UPDATE TB_USUARIO
                SET NM_USUARIO = ?,
                    EMAIL = ?,
                    SENHA = ?,
                    TIPO_USUARIO = ?
                WHERE ID_USUARIO = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getTipoUsuario().name());
            stmt.setLong(5, usuario.getId());

            stmt.executeUpdate();
        }
    }

    public void excluir(Long id) throws SQLException {

        String sql = """
                DELETE FROM TB_USUARIO
                WHERE ID_USUARIO = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {

        Usuario usuario = new Usuario();

        usuario.setId(rs.getLong("ID_USUARIO"));
        usuario.setNome(rs.getString("NM_USUARIO"));
        usuario.setEmail(rs.getString("EMAIL"));
        usuario.setSenha(rs.getString("SENHA"));
        usuario.setTipoUsuario(
                TipoUsuario.valueOf(rs.getString("TIPO_USUARIO"))
        );

        return usuario;
    }
}
