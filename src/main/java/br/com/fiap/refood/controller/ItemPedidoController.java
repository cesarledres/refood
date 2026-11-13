package br.com.fiap.refood.controller;

import br.com.fiap.refood.dto.response.ItemPedidoResponse;
import br.com.fiap.refood.service.ItemPedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/itens-pedido")
public class ItemPedidoController {

    private final ItemPedidoService itemPedidoService;

    public ItemPedidoController(ItemPedidoService itemPedidoService) {
        this.itemPedidoService = itemPedidoService;
    }

    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<List<ItemPedidoResponse>> listarPorPedido(
            @PathVariable Long idPedido) {

        return ResponseEntity.ok(
                itemPedidoService.listarPorPedido(idPedido)
        );
    }

    @GetMapping("/produto/{idProduto}")
    public ResponseEntity<List<ItemPedidoResponse>> listarPorProduto(
            @PathVariable Long idProduto) {

        return ResponseEntity.ok(
                itemPedidoService.listarPorProduto(idProduto)
        );
    }
}