package br.com.fiap.refood.service;

import br.com.fiap.refood.dao.EstabelecimentoDAO;
import br.com.fiap.refood.dao.ProdutoDAO;
import br.com.fiap.refood.dto.request.ProdutoRequest;
import br.com.fiap.refood.dto.response.ProdutoResponse;
import br.com.fiap.refood.exception.RecursoNaoEncontradoException;
import br.com.fiap.refood.exception.RegraNegocioException;
import br.com.fiap.refood.model.Estabelecimento;
import br.com.fiap.refood.model.Produto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoDAO produtoDAO;
    private final EstabelecimentoDAO estabelecimentoDAO;
    private final ModelMapper modelMapper;

    public ProdutoService(
            ProdutoDAO produtoDAO,
            EstabelecimentoDAO estabelecimentoDAO,
            ModelMapper modelMapper) {
        this.produtoDAO = produtoDAO;
        this.estabelecimentoDAO = estabelecimentoDAO;
        this.modelMapper = modelMapper;
    }

    public ProdutoResponse cadastrar(ProdutoRequest dto) {
        try {
            Estabelecimento estabelecimento =
                    estabelecimentoDAO.buscarPorId(dto.estabelecimentoId());

            if (estabelecimento == null) {
                throw new RecursoNaoEncontradoException("Estabelecimento não encontrado.");
            }

            validarProduto(dto);

            Produto produto = modelMapper.map(dto, Produto.class);
            produto.setEstabelecimento(estabelecimento);

            produtoDAO.inserir(produto);

            return modelMapper.map(produto, ProdutoResponse.class);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar produto.", e);
        }
    }

    public List<ProdutoResponse> listarTodos() {
        try {
            return produtoDAO.listarTodos().stream()
                    .map(p -> modelMapper.map(p, ProdutoResponse.class))
                    .toList();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos.", e);
        }
    }

    public List<ProdutoResponse> listarPorEstabelecimento(Long estabelecimentoId) {
        try {
            Estabelecimento estabelecimento = estabelecimentoDAO.buscarPorId(estabelecimentoId);

            if (estabelecimento == null) {
                throw new RecursoNaoEncontradoException("Estabelecimento não encontrado.");
            }

            return produtoDAO.listarTodos().stream()
                    .filter(p -> p.getEstabelecimento().getId()
                            .equals(estabelecimentoId))
                    .map(p -> modelMapper.map(p, ProdutoResponse.class))
                    .toList();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erro ao listar produtos do estabelecimento.", e);
        }
    }

    public ProdutoResponse buscarPorId(Long id) {
        try {
            Produto produto = produtoDAO.buscarPorId(id);

            if (produto == null) {
                throw new RecursoNaoEncontradoException("Produto não encontrado.");
            }

            return modelMapper.map(produto, ProdutoResponse.class);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto.", e);
        }
    }

    public ProdutoResponse atualizar(Long id, ProdutoRequest dto) {
        try {
            Produto produto = produtoDAO.buscarPorId(id);

            if (produto == null) {
                throw new RecursoNaoEncontradoException("Produto não encontrado.");
            }

            Estabelecimento estabelecimento = estabelecimentoDAO.buscarPorId(dto.estabelecimentoId());

            if (estabelecimento == null) {
                throw new RecursoNaoEncontradoException("Estabelecimento não encontrado.");
            }

            validarProduto(dto);

            Produto atualizado = modelMapper.map(dto, Produto.class);
            atualizado.setId(id);
            atualizado.setEstabelecimento(estabelecimento);

            produtoDAO.atualizar(atualizado);

            return modelMapper.map(atualizado, ProdutoResponse.class);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar produto.", e);
        }
    }

    public void excluir(Long id) {
        try {
            Produto produto = produtoDAO.buscarPorId(id);

            if (produto == null) {
                throw new RecursoNaoEncontradoException("Produto não encontrado.");
            }

            if (!produto.getItensPedido().isEmpty()) {
                throw new RegraNegocioException(
                        "Não é possível excluir um produto associado a pedidos."
                );
            }

            produtoDAO.excluir(id);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir produto.", e);
        }
    }

    private void validarProduto(ProdutoRequest dto) {
        if (dto.precoOriginal() == null || dto.precoOriginal() <= 0) {
            throw new RegraNegocioException("O preço original deve ser maior que zero.");
        }

        if (dto.precoPromocional() == null || dto.precoPromocional() <= 0) {
            throw new RegraNegocioException("O preço promocional deve ser maior que zero.");
        }

        if (dto.precoPromocional() >= dto.precoOriginal()) {
            throw new RegraNegocioException("O preço promocional deve ser menor que o original.");
        }

        if (dto.quantidadeEstoque() == null || dto.quantidadeEstoque() < 0) {
            throw new RegraNegocioException("Quantidade em estoque inválida.");
        }

        if (dto.dataValidade() == null || dto.dataValidade().isBefore(LocalDate.now())) {
            throw new RegraNegocioException("A data de validade é inválida.");
        }
    }
}