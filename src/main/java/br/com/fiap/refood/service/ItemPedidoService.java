package br.com.fiap.refood.service;

import br.com.fiap.refood.dao.ItemPedidoDAO;
import br.com.fiap.refood.dao.ProdutoDAO;
import br.com.fiap.refood.dto.response.ItemPedidoResponse;
import br.com.fiap.refood.model.ItemPedido;
import br.com.fiap.refood.model.Produto;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class ItemPedidoService {

    private final ItemPedidoDAO itemPedidoDAO;
    private final ProdutoDAO produtoDAO;

    public ItemPedidoService(
            ItemPedidoDAO itemPedidoDAO,
            ProdutoDAO produtoDAO) {
        this.itemPedidoDAO = itemPedidoDAO;
        this.produtoDAO = produtoDAO;
    }

    public List<ItemPedidoResponse> listarPorPedido(Long pedidoId) {
        try {
            return itemPedidoDAO.listarPorPedido(pedidoId)
                    .stream()
                    .map(this::toResponse)
                    .toList();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar itens do pedido.", e);
        }
    }

    public ItemPedidoResponse buscarPorId(Long id) {
        try {
            ItemPedido item = itemPedidoDAO.buscarPorId(id);

            if (item == null) {
                throw new RuntimeException("Item do pedido não encontrado.");
            }

            return toResponse(item);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar item do pedido.", e);
        }
    }

    private ItemPedidoResponse toResponse(ItemPedido item) {
        try {
            Produto produto = produtoDAO.buscarPorId(
                    item.getProduto().getId()
            );

            if (produto == null) {
                throw new RuntimeException("Produto do item não encontrado.");
            }

            return new ItemPedidoResponse(
                    item.getId(),
                    produto.getId(),
                    produto.getNome(),
                    item.getQuantidade(),
                    item.getValorUnitario(),
                    item.getValorSubtotal()
            );

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao converter item do pedido.", e);
        }
    }
}