package br.com.fiap.refood.service;

import br.com.fiap.refood.dto.request.ProdutoRequest;
import br.com.fiap.refood.dto.response.ProdutoResponse;
import br.com.fiap.refood.model.Estabelecimento;
import br.com.fiap.refood.model.Produto;
import br.com.fiap.refood.repository.EstabelecimentoRepository;
import br.com.fiap.refood.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;

    public ProdutoService(
            ProdutoRepository produtoRepository,
            EstabelecimentoRepository estabelecimentoRepository) {
        this.produtoRepository = produtoRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
    }

    @Transactional
    public ProdutoResponse cadastrar(ProdutoRequest dto) {

        validarPrecos(dto);

        Estabelecimento estabelecimento = estabelecimentoRepository
                .findById(dto.estabelecimentoId())
                .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado."));

        Produto produto = new Produto();
        produto.setEstabelecimento(estabelecimento);
        produto.setNome(dto.nome());
        produto.setCategoria(dto.categoria());
        produto.setPrecoOriginal(dto.precoOriginal());
        produto.setPrecoPromocional(dto.precoPromocional());
        produto.setQuantidadeEstoque(dto.quantidadeEstoque());
        produto.setDataValidade(dto.dataValidade());
        produto.setImagem(dto.imagem());

        return toResponse(produtoRepository.save(produto));
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listarTodos() {
        return produtoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(Long id) {
        return toResponse(buscarProduto(id));
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listarPorEstabelecimento(Long estabelecimentoId) {
        return produtoRepository.findByEstabelecimentoId(estabelecimentoId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ProdutoResponse atualizar(Long id, ProdutoRequest dto) {

        validarPrecos(dto);

        Produto produto = buscarProduto(id);

        Estabelecimento estabelecimento = estabelecimentoRepository
                .findById(dto.estabelecimentoId())
                .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado."));

        produto.setEstabelecimento(estabelecimento);
        produto.setNome(dto.nome());
        produto.setCategoria(dto.categoria());
        produto.setPrecoOriginal(dto.precoOriginal());
        produto.setPrecoPromocional(dto.precoPromocional());
        produto.setQuantidadeEstoque(dto.quantidadeEstoque());
        produto.setDataValidade(dto.dataValidade());
        produto.setImagem(dto.imagem());

        return toResponse(produtoRepository.save(produto));
    }

    @Transactional
    public void excluir(Long id) {
        produtoRepository.delete(buscarProduto(id));
    }

    private void validarPrecos(ProdutoRequest dto) {
        if (dto.precoPromocional().compareTo(dto.precoOriginal()) >= 0) {
            throw new RuntimeException(
                    "O preço promocional deve ser menor que o preço original."
            );
        }
    }

    private Produto buscarProduto(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado."));
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