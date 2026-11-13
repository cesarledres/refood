package br.com.fiap.refood.service;

import br.com.fiap.refood.dao.PagamentoDAO;
import br.com.fiap.refood.dao.PedidoDAO;
import br.com.fiap.refood.dto.request.PagamentoRequest;
import br.com.fiap.refood.dto.response.PagamentoResponse;
import br.com.fiap.refood.model.Pagamento;
import br.com.fiap.refood.model.Pedido;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagamentoService {

    private final PagamentoDAO pagamentoDAO;
    private final PedidoDAO pedidoDAO;

    public PagamentoService(
            PagamentoDAO pagamentoDAO,
            PedidoDAO pedidoDAO) {
        this.pagamentoDAO = pagamentoDAO;
        this.pedidoDAO = pedidoDAO;
    }

    public PagamentoResponse cadastrar(PagamentoRequest dto) {
        try {
            Pedido pedido = pedidoDAO.buscarPorId(dto.pedidoId());

            if (pedido == null) {
                throw new RuntimeException("Pedido não encontrado.");
            }

            Pagamento pagamentoExistente =
                    pagamentoDAO.buscarPorPedido(dto.pedidoId());

            if (pagamentoExistente != null) {
                throw new RuntimeException("Este pedido já possui um pagamento.");
            }

            Pagamento pagamento = new Pagamento();
            pagamento.setPedido(pedido);
            pagamento.setTipoPagamento(dto.tipoPagamento());
            pagamento.setValorPagamento(pedido.getValorTotal());
            pagamento.setDataPagamento(LocalDateTime.now());

            pagamentoDAO.inserir(pagamento);

            return toResponse(pagamento);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar pagamento.", e);
        }
    }

    public List<PagamentoResponse> listarTodos() {
        try {
            return pagamentoDAO.listarTodos()
                    .stream()
                    .map(this::toResponse)
                    .toList();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pagamentos.", e);
        }
    }

    public PagamentoResponse buscarPorId(Long id) {
        try {
            Pagamento pagamento = pagamentoDAO.buscarPorId(id);

            if (pagamento == null) {
                throw new RuntimeException("Pagamento não encontrado.");
            }

            return toResponse(pagamento);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pagamento.", e);
        }
    }

    public PagamentoResponse buscarPorPedido(Long pedidoId) {
        try {
            Pagamento pagamento = pagamentoDAO.buscarPorPedido(pedidoId);

            if (pagamento == null) {
                throw new RuntimeException("Pagamento não encontrado para este pedido.");
            }

            return toResponse(pagamento);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pagamento do pedido.", e);
        }
    }

    private PagamentoResponse toResponse(Pagamento pagamento) {
        return new PagamentoResponse(
                pagamento.getId(),
                pagamento.getPedido().getId(),
                pagamento.getTipoPagamento(),
                pagamento.getValorPagamento(),
                pagamento.getDataPagamento()
        );
    }
}