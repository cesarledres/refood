package br.com.fiap.refood.service;

import br.com.fiap.refood.dto.response.ItemPedidoResponse;
import br.com.fiap.refood.model.ItemPedido;
import br.com.fiap.refood.repository.ItemPedidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemPedidoService {

    private final ItemPedidoRepository itemPedidoRepository;

    public ItemPedidoService(ItemPedidoRepository itemPedidoRepository) {
        this.itemPedidoRepository = itemPedidoRepository;
    }

    public List<ItemPedidoResponse> listarPorPedido(Long idPedido) {

        return itemPedidoRepository.findByPedidoId(idPedido)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ItemPedidoResponse> listarPorProduto(Long idProduto) {

        return itemPedidoRepository.findByProdutoId(idProduto)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ItemPedidoResponse toResponse(ItemPedido item) {

        return new ItemPedidoResponse(
                item.getId(),
                item.getProduto().getId(),
                item.getProduto().getNome(),
                item.getQuantidade(),
                item.getValorUnitario(),
                item.getValorSubtotal()
        );
    }
}