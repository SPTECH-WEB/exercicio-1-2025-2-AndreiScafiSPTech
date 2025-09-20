package school.sptech.prova_ac1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository repository;

    @GetMapping
    public ResponseEntity<List<Usuario>> buscarTodos() {
        try {
            List<Usuario> usuarios = repository.findAll();
            if (usuarios.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<Usuario> criar(@RequestBody Usuario usuario) {
        try {
            boolean cpfExiste = repository.existsByCpf(usuario.getCpf());
            boolean emailExiste = repository.existsByEmail(usuario.getEmail());

            if (cpfExiste || emailExiste) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            Usuario novoUsuario = repository.save(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable("id") Integer id) {
        try {
            return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        try{
            if (repository.existsById(id)) {
                repository.deleteById(id);
                return ResponseEntity.noContent().build(); // 204 No Content
            } else {
                return ResponseEntity.notFound().build(); // 404 Not Found
            }
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/filtro-data")
    public ResponseEntity<List<Usuario>> buscarPorDataNascimento(LocalDate nascimento) {
        try {
            List<Usuario> usuarios = repository.findByDataNascimentoAfter(nascimento);
            if (usuarios.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Integer id, @RequestBody Usuario usuario) {
        try {
            Optional<Usuario> existente = repository.findById(id);
            if (existente.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Optional<Usuario> emailDuplicado = repository.findByEmail(usuario.getEmail());
            if (emailDuplicado.isPresent() && !emailDuplicado.get().getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            Optional<Usuario> cpfDuplicado = repository.findByCpf(usuario.getCpf());
            if (cpfDuplicado.isPresent() && !cpfDuplicado.get().getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            Usuario atual = existente.get();
            atual.setNome(usuario.getNome());
            atual.setEmail(usuario.getEmail());
            atual.setCpf(usuario.getCpf());
            atual.setSenha(usuario.getSenha());
            atual.setDataNascimento(usuario.getDataNascimento());

            Usuario atualizado = repository.save(atual);
            return ResponseEntity.ok(atualizado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
