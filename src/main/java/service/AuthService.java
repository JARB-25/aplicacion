package service;

import model.HistorialEntrada;
import model.Rol;
import model.Usuario;
import repository.HistorialRepository;
import repository.UsuarioRepository;
import util.EmailValidator;
import util.PasswordValidator;

/**
 * Servicio de autenticación.
 * Usa el repositorio compartido inyectado desde AppState.
 * Registra eventos de login y registro en el historial.
 */
public class AuthService {

    private UsuarioRepository repo;
    private HistorialRepository historialRepo;

    public void setRepo(UsuarioRepository repo) {
        this.repo = repo;
    }

    public void setHistorialRepo(HistorialRepository repo) {
        this.historialRepo = repo;
    }

    public Usuario registrar(String email, String password, Rol rol) {
        validarRepositorio();

        if (!EmailValidator.esValido(email)) {
            throw new RuntimeException("Email institucional inválido (debe ser @poligran.edu.co)");
        }
        if (!PasswordValidator.esValido(password)) {
            throw new RuntimeException("La contraseña debe tener al menos 4 caracteres");
        }
        if (repo.buscar(email) != null) {
            throw new RuntimeException("El usuario ya existe");
        }

        Usuario usuario = new Usuario(email, password, rol);
        repo.guardar(usuario);

        if (historialRepo != null) {
            historialRepo.registrar(HistorialEntrada.TipoEvento.REGISTRO, email,
                "Nuevo usuario registrado como " + rol.name());
        }

        return usuario;
    }

    public boolean existeUsuario(String email) {
        validarRepositorio();
        return repo.buscar(email) != null;
    }

    public Usuario login(String email, String password) {
        validarRepositorio();
        Usuario usuario = repo.buscar(email);

        if (usuario == null || !usuario.getPasswordHash().equals(password)) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        if (historialRepo != null) {
            historialRepo.registrar(HistorialEntrada.TipoEvento.LOGIN, email, "Inicio de sesión");
        }

        return usuario;
    }

    private void validarRepositorio() {
        if (repo == null) throw new IllegalStateException("Repositorio no configurado en AuthService");
    }
}