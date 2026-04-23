package rb.lms;

import java.time.LocalDate;
import java.util.List;

import model.Materia;
import model.Rol;
import model.Tarea;
import model.Usuario;
import repository.HistorialRepository;
import repository.UsuarioRepository;
import service.AuthService;
import service.MateriaService;
import service.TareaService;

public final class AppState {

    public static final UsuarioRepository usuarioRepository = new UsuarioRepository();
    public static final HistorialRepository historialRepository = new HistorialRepository();

    public static final AuthService authService = new AuthService();
    public static final MateriaService materiaService = new MateriaService();
    public static final TareaService tareaService = new TareaService();

    private static boolean initialized;
    private static Materia materiaActual;
    private static Usuario profesorDemo;
    private static Usuario usuarioActual;

    static {
        authService.setRepo(usuarioRepository);
        authService.setHistorialRepo(historialRepository);
        tareaService.setHistorialRepo(historialRepository);
    }

    private AppState() {
    }

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        profesorDemo = usuarioRepository.buscar("profe@poligran.edu.co");
        if (profesorDemo == null) {
            profesorDemo = authService.registrar("profe@poligran.edu.co", "Password1!", Rol.PROFESOR);
        }

        materiaActual = materiaService.buscar("Programacion 1", profesorDemo.getEmail());
        if (materiaActual == null) {
            materiaActual = materiaService.crearMateria("Programacion 1", profesorDemo);
        }

        List<Tarea> tareasGuardadas = tareaService.obtenerTareasGuardadas(materiaActual.getNombre());
        for (Tarea tarea : tareasGuardadas) {
            boolean existe = materiaActual.getTareas().stream()
                .anyMatch(t -> t.getTitulo().equals(tarea.getTitulo()));
            if (!existe) {
                materiaActual.agregarTarea(tarea);
            }
        }

        initialized = true;
    }

    public static Usuario registrarUsuario(String email, String password, Rol rol) {
        initialize();

        Usuario usuario = authService.registrar(email, password, rol);
        if (rol == Rol.ESTUDIANTE && materiaActual != null) {
            try {
                materiaService.inscribirEstudiante(materiaActual, usuario);
            } catch (RuntimeException ignored) {
            }
        }
        return usuario;
    }

    public static Materia getMateriaActual() {
        initialize();
        return materiaActual;
    }

    public static void setMateriaActual(Materia materia) {
        materiaActual = materia;
        if (materiaActual == null) {
            return;
        }

        List<Tarea> tareasGuardadas = tareaService.obtenerTareasGuardadas(materiaActual.getNombre());
        for (Tarea tarea : tareasGuardadas) {
            boolean existe = materiaActual.getTareas().stream()
                .anyMatch(t -> t.getTitulo().equals(tarea.getTitulo()));
            if (!existe) {
                materiaActual.agregarTarea(tarea);
            }
        }
    }

    public static Usuario getProfesorDemo() {
        initialize();
        return profesorDemo;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static void setUsuarioActual(Usuario usuario) {
        initialize();
        usuarioActual = usuario;

        if (usuario == null) {
            return;
        }

        if (usuario.getRol() == Rol.ESTUDIANTE) {
            List<Materia> materias = materiaService.getMateriasDeEstudiante(usuario);
            if (materiaActual != null && materias.contains(materiaActual)) {
                setMateriaActual(materiaActual);
            } else if (!materias.isEmpty()) {
                setMateriaActual(materias.get(0));
            } else if (materiaActual != null) {
                try {
                    materiaService.inscribirEstudiante(materiaActual, usuario);
                } catch (RuntimeException ignored) {
                }
            }
        } else {
            List<Materia> materias = materiaService.getMateriasPorProfesor(usuario);
            if (materiaActual != null && materias.contains(materiaActual)) {
                setMateriaActual(materiaActual);
            } else if (!materias.isEmpty()) {
                setMateriaActual(materias.get(0));
            }
        }
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }

    public static void resetear() {
        initialized = false;
        materiaActual = null;
        profesorDemo = null;
        usuarioActual = null;
    }

    public static LocalDate fechaLimitePorDefecto() {
        return LocalDate.now().plusDays(7);
    }
}
