package br.com.fiap.refood.repository;

import br.com.fiap.refood.model.Pedido;
import br.com.fiap.refood.model.enums.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByUsuarioId(Long idUsuario);

    List<Pedido> findByEstabelecimentoId(Long idEstabelecimento);

    List<Pedido> findByStatus(StatusPedido status);
}