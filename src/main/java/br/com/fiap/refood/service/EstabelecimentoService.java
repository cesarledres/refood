package br.com.fiap.refood.service;

import br.com.fiap.refood.dao.EstabelecimentoDAO;
import br.com.fiap.refood.dao.UsuarioDAO;
import br.com.fiap.refood.dto.request.EstabelecimentoRequest;
import br.com.fiap.refood.dto.response.EstabelecimentoResponse;
import br.com.fiap.refood.model.Estabelecimento;
import br.com.fiap.refood.model.Usuario;
import br.com.fiap.refood.model.enums.TipoUsuario;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class EstabelecimentoService {

    private final EstabelecimentoDAO estabelecimentoDAO;
    private final UsuarioDAO usuarioDAO;

    public EstabelecimentoService(
            EstabelecimentoDAO estabelecimentoDAO,
            UsuarioDAO usuarioDAO) {
        this.estabelecimentoDAO = estabelecimentoDAO;
        this.usuarioDAO = usuarioDAO;
    }

    public EstabelecimentoResponse cadastrar(EstabelecimentoRequest dto) {
        try {
            Usuario usuario = usuarioDAO.buscarPorId(dto.usuarioId());

            if (usuario == null) {
                throw new RuntimeException("Usuário não encontrado.");
            }

            if (usuario.getTipoUsuario() != TipoUsuario.COMERCIANTE) {
                throw new RuntimeException("Apenas comerciantes podem cadastrar estabelecimentos.");
            }

            boolean jaPossuiEstabelecimento = estabelecimentoDAO.listarTodos().stream()
                    .anyMatch(e -> e.getUsuario().getId().equals(dto.usuarioId()));

            if (jaPossuiEstabelecimento) {
                throw new RuntimeException("Este usuário já possui um estabelecimento.");
            }

            boolean cnpjExiste = estabelecimentoDAO.listarTodos().stream()
                    .anyMatch(e -> e.getCnpj().equals(dto.cnpj()));

            if (cnpjExiste) {
                throw new RuntimeException("CNPJ já cadastrado.");
            }

            Estabelecimento estabelecimento = new Estabelecimento();
            estabelecimento.setUsuario(usuario);
            estabelecimento.setNome(dto.nome());
            estabelecimento.setCnpj(dto.cnpj());
            estabelecimento.setEndereco(dto.endereco());
            estabelecimento.setCep(dto.cep());
            estabelecimento.setTelefone(dto.telefone());
            estabelecimento.setDescricao(dto.descricao());

            estabelecimentoDAO.inserir(estabelecimento);

            return toResponse(estabelecimento);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar estabelecimento.", e);
        }
    }

    public List<EstabelecimentoResponse> listarTodos() {
        try {
            return estabelecimentoDAO.listarTodos()
                    .stream()
                    .map(this::toResponse)
                    .toList();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar estabelecimentos.", e);
        }
    }

    public EstabelecimentoResponse buscarPorId(Long id) {
        try {
            Estabelecimento estabelecimento = estabelecimentoDAO.buscarPorId(id);

            if (estabelecimento == null) {
                throw new RuntimeException("Estabelecimento não encontrado.");
            }

            return toResponse(estabelecimento);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar estabelecimento.", e);
        }
    }

    public EstabelecimentoResponse atualizar(Long id, EstabelecimentoRequest dto) {
        try {
            Estabelecimento estabelecimento = estabelecimentoDAO.buscarPorId(id);

            if (estabelecimento == null) {
                throw new RuntimeException("Estabelecimento não encontrado.");
            }

            Usuario usuario = usuarioDAO.buscarPorId(dto.usuarioId());

            if (usuario == null) {
                throw new RuntimeException("Usuário não encontrado.");
            }

            if (usuario.getTipoUsuario() != TipoUsuario.COMERCIANTE) {
                throw new RuntimeException("O usuário informado não é comerciante.");
            }

            boolean cnpjExiste = estabelecimentoDAO.listarTodos().stream()
                    .anyMatch(e -> !e.getId().equals(id)
                            && e.getCnpj().equals(dto.cnpj()));

            if (cnpjExiste) {
                throw new RuntimeException("CNPJ já cadastrado.");
            }

            estabelecimento.setUsuario(usuario);
            estabelecimento.setNome(dto.nome());
            estabelecimento.setCnpj(dto.cnpj());
            estabelecimento.setEndereco(dto.endereco());
            estabelecimento.setCep(dto.cep());
            estabelecimento.setTelefone(dto.telefone());
            estabelecimento.setDescricao(dto.descricao());

            estabelecimentoDAO.atualizar(estabelecimento);

            return toResponse(estabelecimento);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar estabelecimento.", e);
        }
    }

    public void excluir(Long id) {
        try {
            Estabelecimento estabelecimento = estabelecimentoDAO.buscarPorId(id);

            if (estabelecimento == null) {
                throw new RuntimeException("Estabelecimento não encontrado.");
            }

            estabelecimentoDAO.excluir(id);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir estabelecimento.", e);
        }
    }

    private EstabelecimentoResponse toResponse(Estabelecimento estabelecimento) {
        return new EstabelecimentoResponse(
                estabelecimento.getId(),
                estabelecimento.getUsuario().getId(),
                estabelecimento.getNome(),
                estabelecimento.getCnpj(),
                estabelecimento.getEndereco(),
                estabelecimento.getCep(),
                estabelecimento.getTelefone(),
                estabelecimento.getDescricao()
        );
    }
}