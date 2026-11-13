package br.com.fiap.refood.service;

import br.com.fiap.refood.dao.*;
import br.com.fiap.refood.dto.request.*;
import br.com.fiap.refood.dto.response.*;
import br.com.fiap.refood.model.*;
import br.com.fiap.refood.model.enums.StatusPedido;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoDAO pedidoDAO;
    private final UsuarioDAO usuarioDAO;
    private final EstabelecimentoDAO estabelecimentoDAO;
    private final ProdutoDAO produtoDAO;
    private final ItemPedidoDAO itemPedidoDAO;
    private final ModelMapper modelMapper;

    public PedidoService(
            PedidoDAO pedidoDAO,
            UsuarioDAO usuarioDAO,
            EstabelecimentoDAO estabelecimentoDAO,
            ProdutoDAO produtoDAO,
            ItemPedidoDAO itemPedidoDAO,
            ModelMapper modelMapper) {

        this.pedidoDAO = pedidoDAO;
        this.usuarioDAO = usuarioDAO;
        this.estabelecimentoDAO = estabelecimentoDAO;
        this.produtoDAO = produtoDAO;
        this.itemPedidoDAO = itemPedidoDAO;
        this.modelMapper = modelMapper;
    }

    public PedidoResponse cadastrar(PedidoRequest dto) {
        try {
            Usuario usuario = usuarioDAO.buscarPorId(dto.usuarioId());

            if (usuario == null) {
                throw new RuntimeException("Usuário não encontrado.");
            }

            Estabelecimento estabelecimento =
                    estabelecimentoDAO.buscarPorId(dto.estabelecimentoId());

            if (estabelecimento == null) {
                throw new RuntimeException("Estabelecimento não encontrado.");
            }

            if (dto.itens() == null || dto.itens().isEmpty()) {
                throw new RuntimeException(
                        "O pedido precisa ter pelo menos um item.");
            }

            Pedido pedido = modelMapper.map(dto, Pedido.class);

            pedido.setUsuario(usuario);
            pedido.setEstabelecimento(estabelecimento);
            pedido.setDataPedido(LocalDateTime.now());
            pedido.setStatus(StatusPedido.PENDENTE);
            pedido.setValorTotal(0.0);

            pedidoDAO.inserir(pedido);

            double valorTotal = 0.0;
            List<ItemPedido> itens = new ArrayList<>();

            for (ItemPedidoRequest itemDTO : dto.itens()) {

                Produto produto =
                        produtoDAO.buscarPorId(itemDTO.produtoId());

                if (produto == null) {
                    throw new RuntimeException(
                            "Produto não encontrado: " + itemDTO.produtoId());
                }

                if (!produto.getEstabelecimento().getId()
                        .equals(dto.estabelecimentoId())) {
                    throw new RuntimeException(
                            "Todos os produtos devem pertencer ao estabelecimento do pedido.");
                }

                if (produto.getDataValidade().isBefore(LocalDate.now())) {
                    throw new RuntimeException(
                            "O produto " + produto.getNome() + " está vencido.");
                }

                if (itemDTO.quantidade() == null
                        || itemDTO.quantidade() <= 0) {
                    throw new RuntimeException(
                            "A quantidade deve ser maior que zero.");
                }

                if (produto.getQuantidadeEstoque() < itemDTO.quantidade()) {
                    throw new RuntimeException(
                            "Estoque insuficiente para " + produto.getNome());
                }

                double valorUnitario = produto.getPrecoPromocional();
                double valorSubtotal = valorUnitario * itemDTO.quantidade();

                ItemPedido item =
                        modelMapper.map(itemDTO, ItemPedido.class);

                item.setPedido(pedido);
                item.setProduto(produto);
                item.setValorUnitario(valorUnitario);
                item.setValorSubtotal(valorSubtotal);

                itemPedidoDAO.inserir(item);

                produto.setQuantidadeEstoque(
                        produto.getQuantidadeEstoque()
                                - itemDTO.quantidade()
                );

                produtoDAO.atualizar(produto);

                valorTotal += valorSubtotal;
                itens.add(item);
            }

            pedido.setItens(itens);
            pedido.setValorTotal(valorTotal);

            pedidoDAO.atualizar(pedido);

            return modelMapper.map(pedido, PedidoResponse.class);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar pedido.", e);
        }
    }

    public List<PedidoResponse> listarTodos() {
        try {
            return pedidoDAO.listarTodos().stream()
                    .map(this::toResponse)
                    .toList();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos.", e);
        }
    }

    public PedidoResponse buscarPorId(Long id) {
        try {
            Pedido pedido = pedidoDAO.buscarPorId(id);

            if (pedido == null) {
                throw new RuntimeException("Pedido não encontrado.");
            }

            return toResponse(pedido);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pedido.", e);
        }
    }

    public List<PedidoResponse> listarPorUsuario(Long usuarioId) {
        try {
            Usuario usuario = usuarioDAO.buscarPorId(usuarioId);

            if (usuario == null) {
                throw new RuntimeException("Usuário não encontrado.");
            }

            return pedidoDAO.listarTodos().stream()
                    .filter(p -> p.getUsuario().getId().equals(usuarioId))
                    .map(this::toResponse)
                    .toList();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos do usuário.", e);
        }
    }

    public List<PedidoResponse> listarPorEstabelecimento(Long estabelecimentoId) {
        try {
            Estabelecimento estabelecimento =
                    estabelecimentoDAO.buscarPorId(estabelecimentoId);

            if (estabelecimento == null) {
                throw new RuntimeException("Estabelecimento não encontrado.");
            }

            return pedidoDAO.listarTodos().stream()
                    .filter(p -> p.getEstabelecimento().getId()
                            .equals(estabelecimentoId))
                    .map(this::toResponse)
                    .toList();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erro ao listar pedidos do estabelecimento.", e);
        }
    }

    public PedidoResponse concluir(Long id) {
        try {
            Pedido pedido = pedidoDAO.buscarPorId(id);

            if (pedido == null) {
                throw new RuntimeException("Pedido não encontrado.");
            }

            pedido.setStatus(StatusPedido.CONCLUIDO);
            pedidoDAO.atualizar(pedido);

            return toResponse(pedido);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao concluir pedido.", e);
        }
    }

    private PedidoResponse toResponse(Pedido pedido) {
        try {
            List<ItemPedido> itens =
                    itemPedidoDAO.listarPorPedido(pedido.getId());

            for (ItemPedido item : itens) {
                Produto produto =
                        produtoDAO.buscarPorId(item.getProduto().getId());

                item.setProduto(produto);
            }

            pedido.setItens(itens);

            return modelMapper.map(pedido, PedidoResponse.class);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao converter pedido.", e);
        }
    }
}