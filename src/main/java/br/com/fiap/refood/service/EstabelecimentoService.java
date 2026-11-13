package br.com.fiap.refood.service;

import br.com.fiap.refood.dto.request.EstabelecimentoRequest;
import br.com.fiap.refood.dto.response.EstabelecimentoResponse;
import br.com.fiap.refood.model.Estabelecimento;
import br.com.fiap.refood.model.Usuario;
import br.com.fiap.refood.model.enums.TipoUsuario;
import br.com.fiap.refood.repository.EstabelecimentoRepository;
import br.com.fiap.refood.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EstabelecimentoService {

    private final EstabelecimentoRepository estabelecimentoRepository;
    private final UsuarioRepository usuarioRepository;

    public EstabelecimentoService(
            EstabelecimentoRepository estabelecimentoRepository,
            UsuarioRepository usuarioRepository) {
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public EstabelecimentoResponse cadastrar(EstabelecimentoRequest dto) {

        Optional<Usuario> usuarioOptional = usuarioRepository.findById(dto.usuarioId());

        if (usuarioOptional.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado.");
        }

        Usuario usuario = usuarioOptional.get();

        if (usuario.getTipoUsuario() != TipoUsuario.COMERCIANTE) {
            throw new RuntimeException("O usuário precisa ser um comerciante.");
        }

        if (estabelecimentoRepository.findByUsuarioId(usuario.getId()).isPresent()) {
            throw new RuntimeException("Este usuário já possui um estabelecimento.");
        }

        Estabelecimento estabelecimento = new Estabelecimento();
        estabelecimento.setUsuario(usuario);
        estabelecimento.setNome(dto.nome());
        estabelecimento.setCnpj(dto.cnpj());
        estabelecimento.setEndereco(dto.endereco());
        estabelecimento.setCep(dto.cep());
        estabelecimento.setTelefone(dto.telefone());
        estabelecimento.setDescricao(dto.descricao());

        return toResponse(estabelecimentoRepository.save(estabelecimento));
    }

    @Transactional(readOnly = true)
    public List<EstabelecimentoResponse> listarTodos() {
        return estabelecimentoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EstabelecimentoResponse buscarPorId(Long id) {
        return toResponse(buscarEstabelecimento(id));
    }

    @Transactional
    public EstabelecimentoResponse atualizar(
            Long id, EstabelecimentoRequest dto) {

        Estabelecimento estabelecimento = buscarEstabelecimento(id);

        estabelecimento.setNome(dto.nome());
        estabelecimento.setCnpj(dto.cnpj());
        estabelecimento.setEndereco(dto.endereco());
        estabelecimento.setCep(dto.cep());
        estabelecimento.setTelefone(dto.telefone());
        estabelecimento.setDescricao(dto.descricao());

        return toResponse(estabelecimentoRepository.save(estabelecimento));
    }

    @Transactional
    public void excluir(Long id) {
        estabelecimentoRepository.delete(buscarEstabelecimento(id));
    }

    private Estabelecimento buscarEstabelecimento(Long id) {
        return estabelecimentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado."));
    }

    private EstabelecimentoResponse toResponse(Estabelecimento estabelecimento) {
        return new EstabelecimentoResponse(
                estabelecimento.getId(),
                estabelecimento.getUsuario().getId(),
                estabelecimento.getNome(),
                estabelecimento.getCnpj(),
                estabelecimento.getEndereco(),
                estabelecimento.getCep(),
                estabelecimento.getTelefone(),
                estabelecimento.getDescricao()
        );
    }
}