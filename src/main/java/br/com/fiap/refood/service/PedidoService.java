package br.com.fiap.refood.service;

import br.com.fiap.refood.dao.EstabelecimentoDAO;
import br.com.fiap.refood.dao.ItemPedidoDAO;
import br.com.fiap.refood.dao.PedidoDAO;
import br.com.fiap.refood.dao.ProdutoDAO;
import br.com.fiap.refood.dao.UsuarioDAO;
import br.com.fiap.refood.dto.request.ItemPedidoRequest;
import br.com.fiap.refood.dto.request.PedidoRequest;
import br.com.fiap.refood.dto.response.ItemPedidoResponse;
import br.com.fiap.refood.dto.response.PedidoResponse;
import br.com.fiap.refood.model.*;
import br.com.fiap.refood.model.enums.StatusPedido;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoDAO pedidoDAO;
    private final UsuarioDAO usuarioDAO;
    private final EstabelecimentoDAO estabelecimentoDAO;
    private final ProdutoDAO produtoDAO;
    private final ItemPedidoDAO itemPedidoDAO;

    public PedidoService(
            PedidoDAO pedidoDAO,
            UsuarioDAO usuarioDAO,
            EstabelecimentoDAO estabelecimentoDAO,
            ProdutoDAO produtoDAO,
            ItemPedidoDAO itemPedidoDAO) {

        this.pedidoDAO = pedidoDAO;
        this.usuarioDAO = usuarioDAO;
        this.estabelecimentoDAO = estabelecimentoDAO;
        this.produtoDAO = produtoDAO;
        this.itemPedidoDAO = itemPedidoDAO;
    }

    public PedidoResponse cadastrar(PedidoRequest dto) {
        try {
            Usuario usuario = usuarioDAO.buscarPorId(dto.usuarioId());

            if (usuario == null) {
                throw new RuntimeException("Usuário não encontrado.");
            }

            Estabelecimento estabelecimento =
                    estabelecimentoDAO.buscarPorId(dto.estabelecimentoId());

            if (estabelecimento == null) {
                throw new RuntimeException("Estabelecimento não encontrado.");
            }

            if (dto.itens() == null || dto.itens().isEmpty()) {
                throw new RuntimeException("O pedido precisa ter pelo menos um item.");
            }

            Pedido pedido = new Pedido();
            pedido.setUsuario(usuario);
            pedido.setEstabelecimento(estabelecimento);
            pedido.setDataPedido(LocalDateTime.now());
            pedido.setStatus(StatusPedido.PENDENTE);
            pedido.setValorTotal(0.0);

            pedidoDAO.inserir(pedido);

            double valorTotal = 0.0;
            List<ItemPedidoResponse> itensResponse = new ArrayList<>();

            for (ItemPedidoRequest itemDTO : dto.itens()) {

                Produto produto = produtoDAO.buscarPorId(itemDTO.produtoId());

                if (produto == null) {
                    throw new RuntimeException("Produto não encontrado: " + itemDTO.produtoId());
                }

                if (!produto.getEstabelecimento().getId()
                        .equals(dto.estabelecimentoId())) {
                    throw new RuntimeException(
                            "Todos os produtos devem pertencer ao estabelecimento do pedido.");
                }

                if (produto.getDataValidade().isBefore(LocalDate.now())) {
                    throw new RuntimeException(
                            "O produto " + produto.getNome() + " está vencido.");
                }

                if (itemDTO.quantidade() == null || itemDTO.quantidade() <= 0) {
                    throw new RuntimeException("A quantidade deve ser maior que zero.");
                }

                if (produto.getQuantidadeEstoque() < itemDTO.quantidade()) {
                    throw new RuntimeException(
                            "Estoque insuficiente para o produto " + produto.getNome());
                }

                double valorUnitario = produto.getPrecoPromocional();
                double valorSubtotal = valorUnitario * itemDTO.quantidade();

                ItemPedido item = new ItemPedido();
                item.setPedido(pedido);
                item.setProduto(produto);
                item.setQuantidade(itemDTO.quantidade());
                item.setValorUnitario(valorUnitario);
                item.setValorSubtotal(valorSubtotal);

                itemPedidoDAO.inserir(item);

                produto.setQuantidadeEstoque(
                        produto.getQuantidadeEstoque() - itemDTO.quantidade()
                );

                produtoDAO.atualizar(produto);

                valorTotal += valorSubtotal;

                itensResponse.add(new ItemPedidoResponse(
                        item.getId(),
                        produto.getId(),
                        produto.getNome(),
                        item.getQuantidade(),
                        item.getValorUnitario(),
                        item.getValorSubtotal()
                ));
            }

            pedido.setValorTotal(valorTotal);
            pedidoDAO.atualizar(pedido);

            return new PedidoResponse(
                    pedido.getId(),
                    usuario.getId(),
                    estabelecimento.getId(),
                    pedido.getDataPedido(),
                    pedido.getValorTotal(),
                    pedido.getStatus(),
                    itensResponse
            );

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar pedido.", e);
        }
    }

    public List<PedidoResponse> listarTodos() {
        try {
            return pedidoDAO.listarTodos()
                    .stream()
                    .map(this::toResponse)
                    .toList();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos.", e);
        }
    }

    public PedidoResponse buscarPorId(Long id) {
        try {
            Pedido pedido = pedidoDAO.buscarPorId(id);

            if (pedido == null) {
                throw new RuntimeException("Pedido não encontrado.");
            }

            return toResponse(pedido);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pedido.", e);
        }
    }

    public List<PedidoResponse> listarPorUsuario(Long usuarioId) {
        try {
            Usuario usuario = usuarioDAO.buscarPorId(usuarioId);

            if (usuario == null) {
                throw new RuntimeException("Usuário não encontrado.");
            }

            return pedidoDAO.listarTodos()
                    .stream()
                    .filter(p -> p.getUsuario().getId().equals(usuarioId))
                    .map(this::toResponse)
                    .toList();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos do usuário.", e);
        }
    }

    public List<PedidoResponse> listarPorEstabelecimento(Long estabelecimentoId) {
        try {
            Estabelecimento estabelecimento =
                    estabelecimentoDAO.buscarPorId(estabelecimentoId);

            if (estabelecimento == null) {
                throw new RuntimeException("Estabelecimento não encontrado.");
            }

            return pedidoDAO.listarTodos()
                    .stream()
                    .filter(p -> p.getEstabelecimento().getId().equals(estabelecimentoId))
                    .map(this::toResponse)
                    .toList();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos do estabelecimento.", e);
        }
    }

    public PedidoResponse concluir(Long id) {
        try {
            Pedido pedido = pedidoDAO.buscarPorId(id);

            if (pedido == null) {
                throw new RuntimeException("Pedido não encontrado.");
            }

            pedido.setStatus(StatusPedido.CONCLUIDO);
            pedidoDAO.atualizar(pedido);

            return toResponse(pedido);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao concluir pedido.", e);
        }
    }

    private PedidoResponse toResponse(Pedido pedido) {
        try {
            List<ItemPedidoResponse> itens = itemPedidoDAO.listarPorPedido(pedido.getId())
                    .stream()
                    .map(item -> {
                        try {
                            Produto produto = produtoDAO.buscarPorId(
                                    item.getProduto().getId()
                            );

                            return new ItemPedidoResponse(
                                    item.getId(),
                                    produto.getId(),
                                    produto.getNome(),
                                    item.getQuantidade(),
                                    item.getValorUnitario(),
                                    item.getValorSubtotal()
                            );

                        } catch (SQLException e) {
                            throw new RuntimeException("Erro ao buscar produto do pedido.", e);
                        }
                    })
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
        }catch (SQLException e) {
        throw new RuntimeException ("Erro ao converter pedido.", e);
    }
    }
}