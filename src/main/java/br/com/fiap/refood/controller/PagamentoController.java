package br.com.fiap.refood.controller;

import br.com.fiap.refood.dto.request.PagamentoRequest;
import br.com.fiap.refood.dto.response.PagamentoResponse;
import br.com.fiap.refood.service.PagamentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagamento")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping
    public ResponseEntity<PagamentoResponse> cadastrar(
            @RequestBody @Valid PagamentoRequest dto) {

        PagamentoResponse pagamento = pagamentoService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagamento);
    }

    @GetMapping
    public ResponseEntity<List<PagamentoResponse>> listarTodos() {

        return ResponseEntity.ok(pagamentoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagamentoResponse> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(pagamentoService.buscarPorId(id));
    }

    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<PagamentoResponse> buscarPorPedido(
            @PathVariable Long idPedido) {

        return ResponseEntity.ok(
                pagamentoService.buscarPorPedido(idPedido)
        );
    }
}