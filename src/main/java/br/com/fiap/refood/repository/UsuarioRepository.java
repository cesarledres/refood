package br.com.fiap.refood.repository;

import br.com.fiap.refood.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmail(String email);

    Usuario findByEmail(String email);
}