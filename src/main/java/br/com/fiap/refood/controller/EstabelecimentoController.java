package br.com.fiap.refood.controller;

import br.com.fiap.refood.dto.request.EstabelecimentoRequest;
import br.com.fiap.refood.dto.response.EstabelecimentoResponse;
import br.com.fiap.refood.service.EstabelecimentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/estabelecimento")
public class EstabelecimentoController {

    private final EstabelecimentoService estabelecimentoService;

    public EstabelecimentoController(
            EstabelecimentoService estabelecimentoService) {
        this.estabelecimentoService = estabelecimentoService;
    }

    @PostMapping
    public ResponseEntity<EstabelecimentoResponse> cadastrar(
            @RequestBody @Valid EstabelecimentoRequest dto) {

        EstabelecimentoResponse estabelecimento =
                estabelecimentoService.cadastrar(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(estabelecimento.id())
                .toUri();

        return ResponseEntity.created(location).body(estabelecimento);
    }

    @GetMapping
    public ResponseEntity<List<EstabelecimentoResponse>> listarTodos() {

        return ResponseEntity.ok(estabelecimentoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstabelecimentoResponse> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(estabelecimentoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstabelecimentoResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid EstabelecimentoRequest dto) {

        return ResponseEntity.ok(estabelecimentoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        estabelecimentoService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}