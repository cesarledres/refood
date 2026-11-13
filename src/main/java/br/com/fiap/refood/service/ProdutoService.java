package br.com.fiap.refood.service;

import br.com.fiap.refood.dao.EstabelecimentoDAO;
import br.com.fiap.refood.dao.ProdutoDAO;
import br.com.fiap.refood.dto.request.ProdutoRequest;
import br.com.fiap.refood.dto.response.ProdutoResponse;
import br.com.fiap.refood.model.Estabelecimento;
import br.com.fiap.refood.model.Produto;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoDAO produtoDAO;
    private final EstabelecimentoDAO estabelecimentoDAO;

    public ProdutoService(
            ProdutoDAO produtoDAO,
            EstabelecimentoDAO estabelecimentoDAO) {
        this.produtoDAO = produtoDAO;
        this.estabelecimentoDAO = estabelecimentoDAO;
    }

    public ProdutoResponse cadastrar(ProdutoRequest dto) {
        try {
            Estabelecimento estabelecimento =
                    estabelecimentoDAO.buscarPorId(dto.estabelecimentoId());

            if (estabelecimento == null) {
                throw new RuntimeException("Estabelecimento não encontrado.");
            }

            validarPrecos(dto.precoOriginal(), dto.precoPromocional());

            if (dto.quantidadeEstoque() <= 0) {
                throw new RuntimeException("A quantidade em estoque deve ser maior que zero.");
            }

            if (dto.dataValidade().isBefore(LocalDate.now())) {
                throw new RuntimeException("Não é possível cadastrar um produto vencido.");
            }

            Produto produto = new Produto();
            produto.setEstabelecimento(estabelecimento);
            produto.setNome(dto.nome());
            produto.setCategoria(dto.categoria());
            produto.setPrecoOriginal(dto.precoOriginal());
            produto.setPrecoPromocional(dto.precoPromocional());
            produto.setQuantidadeEstoque(dto.quantidadeEstoque());
            produto.setDataValidade(dto.dataValidade());
            produto.setImagem(dto.imagem());

            produtoDAO.inserir(produto);

            return toResponse(produto);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar produto.", e);
        }
    }

    public List<ProdutoResponse> listarTodos() {
        try {
            return produtoDAO.listarTodos()
                    .stream()
                    .map(this::toResponse)
                    .toList();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos.", e);
        }
    }

    public List<ProdutoResponse> listarPorEstabelecimento(Long estabelecimentoId) {
        try {
            Estabelecimento estabelecimento =
                    estabelecimentoDAO.buscarPorId(estabelecimentoId);

            if (estabelecimento == null) {
                throw new RuntimeException("Estabelecimento não encontrado.");
            }

            return produtoDAO.listarTodos()
                    .stream()
                    .filter(p -> p.getEstabelecimento().getId().equals(estabelecimentoId))
                    .map(this::toResponse)
                    .toList();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos do estabelecimento.", e);
        }
    }

    public ProdutoResponse buscarPorId(Long id) {
        try {
            Produto produto = produtoDAO.buscarPorId(id);

            if (produto == null) {
                throw new RuntimeException("Produto não encontrado.");
            }

            return toResponse(produto);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto.", e);
        }
    }

    public ProdutoResponse atualizar(Long id, ProdutoRequest dto) {
        try {
            Produto produto = produtoDAO.buscarPorId(id);

            if (produto == null) {
                throw new RuntimeException("Produto não encontrado.");
            }

            Estabelecimento estabelecimento =
                    estabelecimentoDAO.buscarPorId(dto.estabelecimentoId());

            if (estabelecimento == null) {
                throw new RuntimeException("Estabelecimento não encontrado.");
            }

            validarPrecos(dto.precoOriginal(), dto.precoPromocional());

            if (dto.quantidadeEstoque() < 0) {
                throw new RuntimeException("A quantidade em estoque não pode ser negativa.");
            }

            if (dto.dataValidade().isBefore(LocalDate.now())) {
                throw new RuntimeException("Não é possível cadastrar um produto vencido.");
            }

            produto.setEstabelecimento(estabelecimento);
            produto.setNome(dto.nome());
            produto.setCategoria(dto.categoria());
            produto.setPrecoOriginal(dto.precoOriginal());
            produto.setPrecoPromocional(dto.precoPromocional());
            produto.setQuantidadeEstoque(dto.quantidadeEstoque());
            produto.setDataValidade(dto.dataValidade());
            produto.setImagem(dto.imagem());

            produtoDAO.atualizar(produto);

            return toResponse(produto);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar produto.", e);
        }
    }

    public void excluir(Long id) {
        try {
            Produto produto = produtoDAO.buscarPorId(id);

            if (produto == null) {
                throw new RuntimeException("Produto não encontrado.");
            }

            produtoDAO.excluir(id);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir produto.", e);
        }
    }

    private void validarPrecos(Double precoOriginal, Double precoPromocional) {
        if (precoOriginal == null || precoOriginal <= 0) {
            throw new RuntimeException("O preço original deve ser maior que zero.");
        }

        if (precoPromocional == null || precoPromocional <= 0) {
            throw new RuntimeException("O preço promocional deve ser maior que zero.");
        }

        if (precoPromocional >= precoOriginal) {
            throw new RuntimeException("O preço promocional deve ser menor que o original.");
        }
    }

    private ProdutoResponse toResponse(Produto produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getEstabelecimento().getId(),
                produto.getNome(),
                produto.getCategoria(),
                produto.getPrecoOriginal(),
                produto.getPrecoPromocional(),
                produto.getQuantidadeEstoque(),
                produto.getDataValidade(),
                produto.getImagem()
        );
    }
}