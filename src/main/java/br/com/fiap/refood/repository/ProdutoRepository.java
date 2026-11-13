package br.com.fiap.refood.repository;

import br.com.fiap.refood.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByCategoria(String categoria);

    List<Produto> findByEstabelecimentoId(Long idEstabelecimento);

    List<Produto> findByNomeContainingIgnoreCase(String nome);
}