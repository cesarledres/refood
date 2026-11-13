package br.com.fiap.refood.service;

import br.com.fiap.refood.dao.PagamentoDAO;
import br.com.fiap.refood.dao.PedidoDAO;
import br.com.fiap.refood.dto.request.PagamentoRequest;
import br.com.fiap.refood.dto.response.PagamentoResponse;
import br.com.fiap.refood.exception.RecursoNaoEncontradoException;
import br.com.fiap.refood.exception.RegraNegocioException;
import br.com.fiap.refood.model.Pagamento;
import br.com.fiap.refood.model.Pedido;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagamentoService {

    private final PagamentoDAO pagamentoDAO;
    private final PedidoDAO pedidoDAO;
    private final ModelMapper modelMapper;

    public PagamentoService(
            PagamentoDAO pagamentoDAO,
            PedidoDAO pedidoDAO,
            ModelMapper modelMapper) {
        this.pagamentoDAO = pagamentoDAO;
        this.pedidoDAO = pedidoDAO;
        this.modelMapper = modelMapper;
    }

    public PagamentoResponse cadastrar(PagamentoRequest dto) {
        try {
            Pedido pedido = pedidoDAO.buscarPorId(dto.pedidoId());

            if (pedido == null) {
                throw new RecursoNaoEncontradoException("Pedido não encontrado.");
            }

            Pagamento pagamentoExistente =
                    pagamentoDAO.buscarPorPedido(dto.pedidoId());

            if (pagamentoExistente != null) {
                throw new RegraNegocioException("Este pedido já possui um pagamento.");
            }

            Pagamento pagamento = modelMapper.map(dto, Pagamento.class);

            pagamento.setPedido(pedido);
            pagamento.setValorPagamento(pedido.getValorTotal());
            pagamento.setDataPagamento(LocalDateTime.now());

            pagamentoDAO.inserir(pagamento);

            return modelMapper.map(pagamento, PagamentoResponse.class);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar pagamento.", e);
        }
    }

    public List<PagamentoResponse> listarTodos() {
        try {
            return pagamentoDAO.listarTodos().stream()
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
                throw new RecursoNaoEncontradoException("Pagamento não encontrado.");
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
                throw new RegraNegocioException("Pagamento não encontrado para este pedido.");
            }

            return toResponse(pagamento);

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erro ao buscar pagamento do pedido.", e);
        }
    }

    private PagamentoResponse toResponse(Pagamento pagamento) {
        return modelMapper.map(pagamento, PagamentoResponse.class);
    }
}