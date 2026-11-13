
package br.com.fiap.refood.config;

import br.com.fiap.refood.dto.response.*;
import br.com.fiap.refood.model.*;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(
                        org.modelmapper.config.Configuration.AccessLevel.PRIVATE
                );

        Converter<Usuario, UsuarioResponse> usuarioConverter = context -> {
            Usuario u = context.getSource();

            return new UsuarioResponse(
                    u.getId(),
                    u.getNome(),
                    u.getEmail(),
                    u.getTipoUsuario()
            );
        };

        mapper.addConverter(usuarioConverter);

        Converter<Estabelecimento, EstabelecimentoResponse> estabelecimentoConverter =
                context -> {
                    Estabelecimento e = context.getSource();

                    return new EstabelecimentoResponse(
                            e.getId(),
                            e.getUsuario().getId(),
                            e.getNome(),
                            e.getCnpj(),
                            e.getEndereco(),
                            e.getCep(),
                            e.getTelefone(),
                            e.getDescricao()
                    );
                };

        mapper.addConverter(estabelecimentoConverter);

        Converter<Produto, ProdutoResponse> produtoConverter = context -> {
            Produto p = context.getSource();

            return new ProdutoResponse(
                    p.getId(),
                    p.getEstabelecimento().getId(),
                    p.getNome(),
                    p.getCategoria(),
                    p.getPrecoOriginal(),
                    p.getPrecoPromocional(),
                    p.getQuantidadeEstoque(),
                    p.getDataValidade(),
                    p.getImagem()
            );
        };

        mapper.addConverter(produtoConverter);

        Converter<ItemPedido, ItemPedidoResponse> itemConverter = context -> {
            ItemPedido item = context.getSource();

            return new ItemPedidoResponse(
                    item.getId(),
                    item.getProduto().getId(),
                    item.getProduto().getNome(),
                    item.getQuantidade(),
                    item.getValorUnitario(),
                    item.getValorSubtotal()
            );
        };

        mapper.addConverter(itemConverter);

        Converter<Pedido, PedidoResponse> pedidoConverter = context -> {
            Pedido p = context.getSource();

            List<ItemPedidoResponse> itens = p.getItens() == null
                    ? List.of()
                    : p.getItens().stream()
                      .map(item -> mapper.map(item, ItemPedidoResponse.class))
                      .toList();

            return new PedidoResponse(
                    p.getId(),
                    p.getUsuario().getId(),
                    p.getEstabelecimento().getId(),
                    p.getDataPedido(),
                    p.getValorTotal(),
                    p.getStatus(),
                    itens
            );
        };

        mapper.addConverter(pedidoConverter);

        Converter<Pagamento, PagamentoResponse> pagamentoConverter = context -> {
            Pagamento p = context.getSource();

            return new PagamentoResponse(
                    p.getId(),
                    p.getPedido().getId(),
                    p.getTipoPagamento(),
                    p.getValorPagamento(),
                    p.getDataPagamento()
            );
        };

        mapper.addConverter(pagamentoConverter);

        return mapper;
    }
}
