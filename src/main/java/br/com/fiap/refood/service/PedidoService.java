package br.com.fiap.refood.service;

import br.com.fiap.refood.dto.request.ItemPedidoRequest;
import br.com.fiap.refood.dto.request.PedidoRequest;
import br.com.fiap.refood.dto.response.ItemPedidoResponse;
import br.com.fiap.refood.dto.response.PedidoResponse;
import br.com.fiap.refood.model.*;
import br.com.fiap.refood.model.enums.StatusPedido;
import br.com.fiap.refood.repository.EstabelecimentoRepository;
import br.com.fiap.refood.repository.PedidoRepository;
import br.com.fiap.refood.repository.ProdutoRepository;
import br.com.fiap.refood.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            UsuarioRepository usuarioRepository,
            EstabelecimentoRepository estabelecimentoRepository,
            ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public PedidoResponse cadastrar(PedidoRequest dto) {

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Estabelecimento estabelecimento = estabelecimentoRepository
                .findById(dto.estabelecimentoId())
                .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado."));

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setEstabelecimento(estabelecimento);
        pedido.setStatus(StatusPedido.PENDENTE);

        double valorTotal = 0.0;
        List<ItemPedido> itens = new ArrayList<>();

        for (ItemPedidoRequest itemDTO : dto.itens()) {

            Produto produto = produtoRepository.findById(itemDTO.produtoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado."));

            if (!produto.getEstabelecimento().getId()
                    .equals(estabelecimento.getId())) {
                throw new RuntimeException(
                        "O produto não pertence ao estabelecimento informado."
                );
            }

            if (produto.getQuantidadeEstoque() < itemDTO.quantidade()) {
                throw new RuntimeException(
                        "Estoque insuficiente para o produto: " + produto.getNome()
                );
            }

            Double valorUnitario = produto.getPrecoPromocional();

            double valorSubtotal = valorUnitario * itemDTO.quantidade();

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(itemDTO.quantidade());
            item.setValorUnitario(valorUnitario);
            item.setValorSubtotal(valorSubtotal);

            itens.add(item);
            valorTotal += valorSubtotal;

            produto.setQuantidadeEstoque(
                    produto.getQuantidadeEstoque() - itemDTO.quantidade()
            );
        }

        pedido.setItens(itens);
        pedido.setValorTotal(valorTotal);

        return toResponse(pedidoRepository.save(pedido));
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarTodos() {
        return pedidoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarPorId(Long id) {
        return toResponse(buscarPedido(id));
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarPorEstabelecimento(Long estabelecimentoId) {
        return pedidoRepository.findByEstabelecimentoId(estabelecimentoId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PedidoResponse concluir(Long id) {

        Pedido pedido = buscarPedido(id);

        if (pedido.getStatus() == StatusPedido.CONCLUIDO) {
            throw new RuntimeException("Este pedido já foi concluído.");
        }

        pedido.setStatus(StatusPedido.CONCLUIDO);

        return toResponse(pedidoRepository.save(pedido));
    }

    private Pedido buscarPedido(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado."));
    }

    private PedidoResponse toResponse(Pedido pedido) {

        List<ItemPedidoResponse> itens = pedido.getItens()
                .stream()
                .map(item -> new ItemPedidoResponse(
                        item.getId(),
                        item.getProduto().getId(),
                        item.getProduto().getNome(),
                        item.getQuantidade(),
                        item.getValorUnitario(),
                        item.getValorSubtotal()
                ))
                .toList();

        return new PedidoResponse(
                pedido.getId(),
                pedido.getUsuario().getId(),
                pedido.getEstabelecimento().getId(),
                pedido.getDataPedido(),
                pedido.getValorTotal(),
                pedido.getStatus(),
                itens
        );
    }
}