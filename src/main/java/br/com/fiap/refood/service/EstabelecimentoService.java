package br.com.fiap.refood.service;

import br.com.fiap.refood.dao.EstabelecimentoDAO;
import br.com.fiap.refood.dao.UsuarioDAO;
import br.com.fiap.refood.dto.request.EstabelecimentoRequest;
import br.com.fiap.refood.dto.response.EstabelecimentoResponse;
import br.com.fiap.refood.exception.RecursoNaoEncontradoException;
import br.com.fiap.refood.exception.RegraNegocioException;
import br.com.fiap.refood.model.Estabelecimento;
import br.com.fiap.refood.model.Usuario;
import br.com.fiap.refood.model.enums.TipoUsuario;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class EstabelecimentoService {

    private final EstabelecimentoDAO estabelecimentoDAO;
    private final UsuarioDAO usuarioDAO;
    private final ModelMapper modelMapper;

    public EstabelecimentoService(
            EstabelecimentoDAO estabelecimentoDAO,
            UsuarioDAO usuarioDAO,
            ModelMapper modelMapper) {
        this.estabelecimentoDAO = estabelecimentoDAO;
        this.usuarioDAO = usuarioDAO;
        this.modelMapper = modelMapper;
    }

    public EstabelecimentoResponse cadastrar(EstabelecimentoRequest dto) {
        try {
            Usuario usuario = usuarioDAO.buscarPorId(dto.usuarioId());

            if (usuario == null) {
                throw new RecursoNaoEncontradoException("Usuário não encontrado.");
            }

            if (usuario.getTipoUsuario() != TipoUsuario.COMERCIANTE) {
                throw new RegraNegocioException(
                        "Apenas comerciantes podem cadastrar estabelecimentos.");
            }

            boolean usuarioJaPossui = estabelecimentoDAO.listarTodos().stream()
                    .anyMatch(e -> e.getUsuario().getId().equals(dto.usuarioId()));

            if (usuarioJaPossui) {
                throw new RegraNegocioException(
                        "Este usuário já possui um estabelecimento.");
            }

            boolean cnpjExiste = estabelecimentoDAO.listarTodos().stream()
                    .anyMatch(e -> e.getCnpj().equals(dto.cnpj()));

            if (cnpjExiste) {
                throw new RegraNegocioException("CNPJ já cadastrado.");
            }

            Estabelecimento estabelecimento =
                    modelMapper.map(dto, Estabelecimento.class);

            estabelecimento.setUsuario(usuario);

            estabelecimentoDAO.inserir(estabelecimento);

            return modelMapper.map(
                    estabelecimento, EstabelecimentoResponse.class);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar estabelecimento.", e);
        }
    }

    public List<EstabelecimentoResponse> listarTodos() {
        try {
            return estabelecimentoDAO.listarTodos().stream()
                    .map(e -> modelMapper.map(e, EstabelecimentoResponse.class))
                    .toList();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar estabelecimentos.", e);
        }
    }

    public EstabelecimentoResponse buscarPorId(Long id) {
        try {
            Estabelecimento estabelecimento =
                    estabelecimentoDAO.buscarPorId(id);

            if (estabelecimento == null) {
                throw new RecursoNaoEncontradoException("Estabelecimento não encontrado.");
            }

            return modelMapper.map(
                    estabelecimento, EstabelecimentoResponse.class);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar estabelecimento.", e);
        }
    }

    public EstabelecimentoResponse atualizar(
            Long id, EstabelecimentoRequest dto) {
        try {
            Estabelecimento estabelecimento =
                    estabelecimentoDAO.buscarPorId(id);

            if (estabelecimento == null) {
                throw new RecursoNaoEncontradoException("Estabelecimento não encontrado.");
            }

            Usuario usuario = usuarioDAO.buscarPorId(dto.usuarioId());

            if (usuario == null) {
                throw new RecursoNaoEncontradoException("Usuário não encontrado.");
            }

            if (usuario.getTipoUsuario() != TipoUsuario.COMERCIANTE) {
                throw new RegraNegocioException("O usuário não é comerciante.");
            }

            boolean cnpjExiste = estabelecimentoDAO.listarTodos().stream()
                    .anyMatch(e -> !e.getId().equals(id)
                            && e.getCnpj().equals(dto.cnpj()));

            if (cnpjExiste) {
                throw new RegraNegocioException("CNPJ já cadastrado.");
            }

            Estabelecimento atualizado =
                    modelMapper.map(dto, Estabelecimento.class);

            atualizado.setId(id);
            atualizado.setUsuario(usuario);

            estabelecimentoDAO.atualizar(atualizado);

            return modelMapper.map(
                    atualizado, EstabelecimentoResponse.class);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar estabelecimento.", e);
        }
    }

    public void excluir(Long id) {
        try {
            Estabelecimento estabelecimento =
                    estabelecimentoDAO.buscarPorId(id);

            if (estabelecimento == null) {
                throw new RecursoNaoEncontradoException("Estabelecimento não encontrado.");
            }

            estabelecimentoDAO.excluir(id);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir estabelecimento.", e);
        }
    }
}