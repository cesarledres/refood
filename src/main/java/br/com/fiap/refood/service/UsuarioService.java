package br.com.fiap.refood.service;

import br.com.fiap.refood.dto.request.UsuarioRequest;
import br.com.fiap.refood.dto.response.UsuarioResponse;
import br.com.fiap.refood.model.Usuario;
import br.com.fiap.refood.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public UsuarioResponse cadastrar(UsuarioRequest dto) {

        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("E-mail já cadastrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(dto.senha());
        usuario.setTipoUsuario(dto.tipoUsuario());

        return toResponse(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return toResponse(buscarUsuario(id));
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest dto) {

        Usuario usuario = buscarUsuario(id);

        if (!usuario.getEmail().equals(dto.email())
                && usuarioRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("E-mail já cadastrado.");
        }

        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(dto.senha());
        usuario.setTipoUsuario(dto.tipoUsuario());

        return toResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public void excluir(Long id) {
        Usuario usuario = buscarUsuario(id);
        usuarioRepository.delete(usuario);
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipoUsuario()
        );
    }
}