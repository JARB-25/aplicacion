package rb.lms;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.Materia;
import model.Rol;
import model.Usuario;

public class MenuController {

    @FXML private Label  welcomeLabel;
    @FXML private Label  rolLabel;
    @FXML private Label  materiaLabel;

    @FXML private Button btnGestionMaterias;

    // Estudiante
    @FXML private Button btnVerTareas;
    @FXML private Button btnEntregar;
    @FXML private Button btnNotas;

    // Profesor
    @FXML private Button btnCrearTarea;
    @FXML private Button btnCalificar;

    // Ambos
    @FXML private Button btnEisenhower;
    @FXML private Button btnHistorial;

    private Usuario usuario;

    // ── Inicialización desde exterior ─────────────────────────────────────────

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        AppState.setUsuarioActual(usuario);
        refrescarUI();
    }

    private void refrescarUI() {
        welcomeLabel.setText("Bienvenido: " + usuario.getEmail());
        rolLabel.setText("Rol: " + (usuario.getRol() == Rol.ESTUDIANTE ? "Estudiante" : "Profesor"));

        Materia materia = AppState.getMateriaActual();
        if (materia != null) {
            materiaLabel.setText("📚 Materia activa: " + materia.getNombre());
            materiaLabel.setVisible(true);
            materiaLabel.setManaged(true);
        } else {
            materiaLabel.setText("⚠  Sin materia seleccionada — ve a Gestionar materias");
            materiaLabel.setVisible(true);
            materiaLabel.setManaged(true);
        }

        boolean esEstudiante = usuario.getRol() == Rol.ESTUDIANTE;
        boolean tieneMateria = materia != null;

        // Botones estudiante
        setVisible(btnVerTareas,  esEstudiante && tieneMateria);
        setVisible(btnEntregar,   esEstudiante && tieneMateria);
        setVisible(btnNotas,      esEstudiante && tieneMateria);

        // Botones profesor
        setVisible(btnCrearTarea, !esEstudiante && tieneMateria);
        setVisible(btnCalificar,  !esEstudiante && tieneMateria);

        // Ambos — solo si hay materia
        setVisible(btnEisenhower, tieneMateria);
        setVisible(btnHistorial,  true);         // historial siempre accesible
        setVisible(btnGestionMaterias, true);
    }

    // ── Navegación ────────────────────────────────────────────────────────────

    @FXML private void irAGestionMaterias() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("materias.fxml"));
        Parent root = loader.load();
        MateriasController ctrl = loader.getController();
        ctrl.setUsuario(usuario);
        App.setScene(root);
    }

    @FXML private void irAVerTareas()  throws IOException { abrirSecundario(SecondaryController.Modo.VER_TAREAS); }
    @FXML private void irAEntregar()   throws IOException { abrirSecundario(SecondaryController.Modo.ENTREGAR); }
    @FXML private void irANotas()      throws IOException { abrirSecundario(SecondaryController.Modo.VER_NOTAS); }
    @FXML private void irACrearTarea() throws IOException { abrirSecundario(SecondaryController.Modo.CREAR_TAREA); }
    @FXML private void irACalificar()  throws IOException { abrirSecundario(SecondaryController.Modo.CALIFICAR); }

    @FXML
    private void irAEisenhower() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("heisenhower.fxml"));
        Parent root = loader.load();
        EisenhowerController ctrl = loader.getController();
        ctrl.setUsuario(usuario);
        App.setScene(root);
    }

    @FXML
    private void irAHistorial() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("historial.fxml"));
        Parent root = loader.load();
        HistorialController ctrl = loader.getController();
        ctrl.setUsuario(usuario);
        App.setScene(root);
    }

    @FXML
    private void cerrarSesion() {
        try {
            AppState.cerrarSesion();
            App.setRoot("primary");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void abrirSecundario(SecondaryController.Modo modo) throws IOException {
        if (AppState.getMateriaActual() == null) return;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("secondary.fxml"));
        Parent root = loader.load();
        SecondaryController ctrl = loader.getController();
        ctrl.setUsuario(usuario, modo);
        App.setScene(root);
    }

    private void setVisible(Button btn, boolean visible) {
        btn.setVisible(visible);
        btn.setManaged(visible);
    }
}