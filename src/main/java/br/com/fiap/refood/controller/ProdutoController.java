package br.com.fiap.refood.controller;

import br.com.fiap.refood.dto.request.ProdutoRequest;
import br.com.fiap.refood.dto.response.ProdutoResponse;
import br.com.fiap.refood.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produto")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    public ResponseEntity<ProdutoResponse> cadastrar(
            @RequestBody @Valid ProdutoRequest dto) {

        ProdutoResponse produto = produtoService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(produto);
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> listarTodos() {

        return ResponseEntity.ok(produtoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @GetMapping("/estabelecimento/{idEstabelecimento}")
    public ResponseEntity<List<ProdutoResponse>> listarPorEstabelecimento(
            @PathVariable Long idEstabelecimento) {

        return ResponseEntity.ok(
                produtoService.listarPorEstabelecimento(idEstabelecimento)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid ProdutoRequest dto) {

        return ResponseEntity.ok(produtoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        produtoService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}