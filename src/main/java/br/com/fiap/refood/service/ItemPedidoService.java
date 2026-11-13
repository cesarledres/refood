package br.com.fiap.refood.service;

import br.com.fiap.refood.dao.ItemPedidoDAO;
import br.com.fiap.refood.dao.ProdutoDAO;
import br.com.fiap.refood.dto.response.ItemPedidoResponse;
import br.com.fiap.refood.exception.RecursoNaoEncontradoException;
import br.com.fiap.refood.model.ItemPedido;
import br.com.fiap.refood.model.Produto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class ItemPedidoService {

    private final ItemPedidoDAO itemPedidoDAO;
    private final ProdutoDAO produtoDAO;
    private final ModelMapper modelMapper;

    public ItemPedidoService(
            ItemPedidoDAO itemPedidoDAO,
            ProdutoDAO produtoDAO,
            ModelMapper modelMapper) {
        this.itemPedidoDAO = itemPedidoDAO;
        this.produtoDAO = produtoDAO;
        this.modelMapper = modelMapper;
    }

    public List<ItemPedidoResponse> listarPorPedido(Long pedidoId) {
        try {
            List<ItemPedido> itens =
                    itemPedidoDAO.listarPorPedido(pedidoId);

            return itens.stream()
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
                throw new RecursoNaoEncontradoException("Item do pedido não encontrado.");
            }

            return toResponse(item);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar item do pedido.", e);
        }
    }

    private ItemPedidoResponse toResponse(ItemPedido item) {
        try {
            Produto produto = produtoDAO.buscarPorId(item.getProduto().getId());

            if (produto == null) {
                throw new RecursoNaoEncontradoException("Produto não encontrado.");
            }

            item.setProduto(produto);

            return modelMapper.map(item, ItemPedidoResponse.class);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao converter item do pedido.", e);
        }
    }
}