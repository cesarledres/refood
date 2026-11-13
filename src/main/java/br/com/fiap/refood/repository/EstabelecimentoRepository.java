package br.com.fiap.refood.repository;

import br.com.fiap.refood.model.Estabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstabelecimentoRepository extends JpaRepository<Estabelecimento, Long> {

    Optional<Estabelecimento> findByCnpj(String cnpj);

    Optional<Estabelecimento> findByUsuarioId(Long idUsuario);
}