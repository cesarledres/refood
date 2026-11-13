package br.com.fiap.refood.service;

import br.com.fiap.refood.dao.UsuarioDAO;
import br.com.fiap.refood.dto.request.UsuarioRequest;
import br.com.fiap.refood.dto.response.UsuarioResponse;
import br.com.fiap.refood.model.Usuario;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public UsuarioResponse cadastrar(UsuarioRequest dto) {
        try {
            boolean emailExiste = usuarioDAO.listarTodos().stream()
                    .anyMatch(u -> u.getEmail().equalsIgnoreCase(dto.email()));

            if (emailExiste) {
                throw new RuntimeException("E-mail já cadastrado.");
            }

            Usuario usuario = new Usuario();
            usuario.setNome(dto.nome());
            usuario.setEmail(dto.email());
            usuario.setSenha(dto.senha());
            usuario.setTipoUsuario(dto.tipoUsuario());

            usuarioDAO.inserir(usuario);

            return toResponse(usuario);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar usuário.", e);
        }
    }

    public List<UsuarioResponse> listarTodos() {
        try {
            return usuarioDAO.listarTodos()
                    .stream()
                    .map(this::toResponse)
                    .toList();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários.", e);
        }
    }

    public UsuarioResponse buscarPorId(Long id) {
        try {
            Usuario usuario = usuarioDAO.buscarPorId(id);

            if (usuario == null) {
                throw new RuntimeException("Usuário não encontrado.");
            }

            return toResponse(usuario);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário.", e);
        }
    }

    public UsuarioResponse atualizar(Long id, UsuarioRequest dto) {
        try {
            Usuario usuario = usuarioDAO.buscarPorId(id);

            if (usuario == null) {
                throw new RuntimeException("Usuário não encontrado.");
            }

            boolean emailExiste = usuarioDAO.listarTodos().stream()
                    .anyMatch(u -> !u.getId().equals(id)
                            && u.getEmail().equalsIgnoreCase(dto.email()));

            if (emailExiste) {
                throw new RuntimeException("E-mail já cadastrado.");
            }

            usuario.setNome(dto.nome());
            usuario.setEmail(dto.email());
            usuario.setSenha(dto.senha());
            usuario.setTipoUsuario(dto.tipoUsuario());

            usuarioDAO.atualizar(usuario);

            return toResponse(usuario);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar usuário.", e);
        }
    }

    public void excluir(Long id) {
        try {
            Usuario usuario = usuarioDAO.buscarPorId(id);

            if (usuario == null) {
                throw new RuntimeException("Usuário não encontrado.");
            }

            usuarioDAO.excluir(id);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir usuário.", e);
        }
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipoUsuario()
        );
    }
}