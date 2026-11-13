package br.com.fiap.refood.controller;

import br.com.fiap.refood.dto.request.PedidoRequest;
import br.com.fiap.refood.dto.response.PedidoResponse;
import br.com.fiap.refood.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pedido")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> cadastrar(
            @RequestBody @Valid PedidoRequest dto) {

        PedidoResponse pedido = pedidoService.cadastrar(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(pedido.id())
                .toUri();

        return ResponseEntity.created(location).body(pedido);
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> listarTodos() {

        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<PedidoResponse>> listarPorUsuario(
            @PathVariable Long idUsuario) {

        return ResponseEntity.ok(
                pedidoService.listarPorUsuario(idUsuario)
        );
    }

    @GetMapping("/estabelecimento/{idEstabelecimento}")
    public ResponseEntity<List<PedidoResponse>> listarPorEstabelecimento(
            @PathVariable Long idEstabelecimento) {

        return ResponseEntity.ok(
                pedidoService.listarPorEstabelecimento(idEstabelecimento)
        );
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<PedidoResponse> concluir(
            @PathVariable Long id) {

        return ResponseEntity.ok(pedidoService.concluir(id));
    }
}