package br.com.fiap.refood.service;

import br.com.fiap.refood.dao.UsuarioDAO;
import br.com.fiap.refood.dto.request.UsuarioRequest;
import br.com.fiap.refood.dto.response.UsuarioResponse;
import br.com.fiap.refood.model.Usuario;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioDAO usuarioDAO;
    private final ModelMapper modelMapper;

    public UsuarioService(UsuarioDAO usuarioDAO, ModelMapper modelMapper) {
        this.usuarioDAO = usuarioDAO;
        this.modelMapper = modelMapper;
    }

    public UsuarioResponse cadastrar(UsuarioRequest dto) {
        try {
            boolean emailExiste = usuarioDAO.listarTodos().stream()
                    .anyMatch(u -> u.getEmail().equalsIgnoreCase(dto.email()));

            if (emailExiste) {
                throw new RuntimeException("E-mail já cadastrado.");
            }

            Usuario usuario = modelMapper.map(dto, Usuario.class);
            usuario.setSenha(dto.senha());

            usuarioDAO.inserir(usuario);

            return modelMapper.map(usuario, UsuarioResponse.class);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar usuário.", e);
        }
    }

    public List<UsuarioResponse> listarTodos() {
        try {
            return usuarioDAO.listarTodos().stream()
                    .map(usuario -> modelMapper.map(usuario, UsuarioResponse.class))
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

            return modelMapper.map(usuario, UsuarioResponse.class);

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

            Usuario atualizado = modelMapper.map(dto, Usuario.class);
            atualizado.setId(id);
            atualizado.setSenha(dto.senha());

            usuarioDAO.atualizar(atualizado);

            return modelMapper.map(atualizado, UsuarioResponse.class);

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
}