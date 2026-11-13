package br.com.fiap.refood.service;

import br.com.fiap.refood.dto.request.PagamentoRequest;
import br.com.fiap.refood.dto.response.PagamentoResponse;
import br.com.fiap.refood.model.Pagamento;
import br.com.fiap.refood.model.Pedido;
import br.com.fiap.refood.repository.PagamentoRepository;
import br.com.fiap.refood.repository.PedidoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;

    public PagamentoService(
            PagamentoRepository pagamentoRepository,
            PedidoRepository pedidoRepository) {

        this.pagamentoRepository = pagamentoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public PagamentoResponse cadastrar(PagamentoRequest dto) {

        Pedido pedido = pedidoRepository.findById(dto.pedidoId())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado."));

        if (pagamentoRepository.findByPedidoId(pedido.getId()).isPresent()) {
            throw new RuntimeException("Este pedido já possui um pagamento cadastrado.");
        }

        Pagamento pagamento = new Pagamento();

        pagamento.setPedido(pedido);
        pagamento.setTipoPagamento(dto.tipoPagamento());
        pagamento.setValorPagamento(pedido.getValorTotal());
        pagamento.setDataPagamento(LocalDateTime.now());

        pagamentoRepository.save(pagamento);

        return toResponse(pagamento);
    }

    public List<PagamentoResponse> listarTodos() {

        return pagamentoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PagamentoResponse buscarPorId(Long id) {

        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado."));

        return toResponse(pagamento);
    }

    public PagamentoResponse buscarPorPedido(Long idPedido) {

        Pagamento pagamento = pagamentoRepository.findByPedidoId(idPedido)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado para este pedido."));

        return toResponse(pagamento);
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