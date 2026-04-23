package rb.lms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.Materia;
import model.Rol;
import model.Usuario;

/**
 * FIX 3: Navegar a la vista de detalle de una materia.
 *
 * PROBLEMA ORIGINAL:
 *   - El botón "Seleccionar" solo cambiaba AppState.materiaActual, sin navegar a ningún lugar.
 *   - No había forma de "entrar" a una materia para ver sus tareas, estudiantes, etc.
 *   - El flujo esperado era: lista de materias → seleccionar → ver detalle/tareas de esa materia.
 *
 * SOLUCIÓN:
 *   - Se renombra "Seleccionar" a "Entrar a materia" (cambiar en materias.fxml también).
 *   - Al hacer clic, se establece la materia activa Y se navega al menú principal,
 *     que ya muestra los botones correctos según el rol (ver tareas, crear tarea, etc.).
 *   - Esto crea el flujo lógico: Login → Materias → [Entrar] → Menú con contexto de materia.
 *
 * CAMBIO EN materias.fxml REQUERIDO:
 *   Cambiar el texto del botón btnSeleccionar:
 *     <Button fx:id="btnSeleccionar" text="Entrar a materia" onAction="#entrarAMateria" />
 *   (el método se renombra de seleccionarMateria a entrarAMateria)
 */
public class MateriasController {

    @FXML private Label subtituloLabel;
    @FXML private ListView<String> listaMaterias;
    @FXML private Label materiaSelLabel;
    @FXML private Label formLabel;
    @FXML private TextField nombreField;
    @FXML private Button btnCrear;
    @FXML private Button btnRenombrar;
    @FXML private Button btnEliminar;
    @FXML private Button btnSeleccionar;   // renombrar texto a "Entrar a materia" en FXML
    @FXML private Button btnInscribirse;
    @FXML private Label mensajeLabel;

    private Usuario usuario;
    private List<Materia> materiasVisibles = new ArrayList<>();
    private Materia materiaSeleccionada;

    @FXML
    public void initialize() {
        listaMaterias.getSelectionModel().selectedIndexProperty().addListener((obs, oldValue, newValue) -> {
            int index = newValue == null ? -1 : newValue.intValue();
            if (index >= 0 && index < materiasVisibles.size()) {
                materiaSeleccionada = materiasVisibles.get(index);
                materiaSelLabel.setText("Seleccionada: " + materiaSeleccionada.getNombre());
            } else {
                materiaSeleccionada = null;
                materiaSelLabel.setText("(ninguna seleccionada)");
            }
        });
        mensajeLabel.setText("");
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        refrescarVista();
    }

    @FXML
    private void crearMateria() {
        if (usuario.getRol() != Rol.PROFESOR) {
            mostrarError("Solo profesores pueden crear materias");
            return;
        }
        String nombre = nombreField.getText() == null ? "" : nombreField.getText().trim();
        if (nombre.isEmpty()) {
            mostrarError("Escribe el nombre de la materia");
            return;
        }
        try {
            Materia materia = AppState.materiaService.crearMateria(nombre, usuario);
            AppState.setMateriaActual(materia);
            nombreField.clear();
            mostrarExito("Materia creada correctamente");
            refrescarVista();
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void renombrarMateria() {
        if (materiaSeleccionada == null) {
            mostrarError("Selecciona una materia");
            return;
        }
        String nuevoNombre = nombreField.getText() == null ? "" : nombreField.getText().trim();
        if (nuevoNombre.isEmpty()) {
            mostrarError("Escribe el nuevo nombre");
            return;
        }
        try {
            AppState.materiaService.renombrarMateria(materiaSeleccionada, nuevoNombre, usuario);
            if (materiaSeleccionada.equals(AppState.getMateriaActual())) {
                AppState.setMateriaActual(materiaSeleccionada);
            }
            nombreField.clear();
            mostrarExito("Materia renombrada");
            refrescarVista();
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void eliminarMateria() {
        if (materiaSeleccionada == null) {
            mostrarError("Selecciona una materia");
            return;
        }
        try {
            boolean eraActual = materiaSeleccionada.equals(AppState.getMateriaActual());
            AppState.materiaService.eliminarMateria(materiaSeleccionada, usuario);
            if (eraActual) {
                AppState.setMateriaActual(null);
            }
            materiaSeleccionada = null;
            mostrarExito("Materia eliminada");
            refrescarVista();
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * FIX: Antes solo cambiaba materiaActual sin ir a ningún lado.
     * Ahora: establece la materia activa y navega al menú principal,
     * donde el usuario ya puede operar con esa materia (ver tareas, crear, etc.).
     *
     * RENOMBRAR en materias.fxml:
     *   onAction="#entrarAMateria"  (antes era seleccionarMateria)
     *   text="Entrar a materia"     (antes era "Seleccionar")
     */
    @FXML
    private void entrarAMateria() {
        if (materiaSeleccionada == null) {
            mostrarError("Selecciona una materia");
            return;
        }

        if (usuario.getRol() == Rol.ESTUDIANTE && !materiaSeleccionada.getEstudiantes().contains(usuario)) {
            mostrarError("Primero debes inscribirte en la materia");
            return;
        }

        if (usuario.getRol() == Rol.PROFESOR && !materiaSeleccionada.getProfesor().equals(usuario)) {
            mostrarError("Solo puedes entrar a materias que te pertenezcan");
            return;
        }

        // Establecer materia activa
        AppState.setMateriaActual(materiaSeleccionada);

        // Navegar al menú principal con el contexto de la materia
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
            Parent root = loader.load();
            MenuController ctrl = loader.getController();
            ctrl.setUsuario(usuario);
            App.setScene(root);
        } catch (IOException e) {
            mostrarError("Error al entrar a la materia: " + e.getMessage());
        }
    }

    /**
     * Compatibilidad: si en el FXML aún apunta a seleccionarMateria,
     * redirige a entrarAMateria.
     */
    @FXML
    private void seleccionarMateria() {
        entrarAMateria();
    }

    @FXML
    private void inscribirse() {
        if (materiaSeleccionada == null) {
            mostrarError("Selecciona una materia");
            return;
        }
        if (usuario.getRol() != Rol.ESTUDIANTE) {
            mostrarError("Solo estudiantes pueden inscribirse");
            return;
        }
        try {
            AppState.materiaService.inscribirEstudiante(materiaSeleccionada, usuario);
            AppState.setMateriaActual(materiaSeleccionada);
            mostrarExito("Inscripción realizada — ahora puedes entrar a la materia");
            refrescarVista();
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void volver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
            Parent root = loader.load();
            MenuController controller = loader.getController();
            controller.setUsuario(usuario);
            App.setScene(root);
        } catch (IOException e) {
            mostrarError("Error al volver: " + e.getMessage());
        }
    }

    private void refrescarVista() {
        listaMaterias.getItems().clear();
        materiaSeleccionada = null;
        materiaSelLabel.setText("(ninguna seleccionada)");

        boolean esProfesor = usuario.getRol() == Rol.PROFESOR;
        if (esProfesor) {
            subtituloLabel.setText("Administra tus materias. Entra a una para trabajar con ella.");
            formLabel.setText("Nueva materia:");
            materiasVisibles = AppState.materiaService.getMateriasPorProfesor(usuario);
        } else {
            subtituloLabel.setText("Inscríbete en una materia y entra a verla.");
            formLabel.setText("Nombre de materia:");
            materiasVisibles = AppState.materiaService.getMaterias();
        }

        for (Materia materia : materiasVisibles) {
            String texto = materia.getNombre();
            if (!esProfesor) {
                boolean inscrito = materia.getEstudiantes().contains(usuario);
                texto += " — " + materia.getProfesor().getEmail()
                       + (inscrito ? " ✔ inscrito" : "");
            }
            if (materia.equals(AppState.getMateriaActual())) {
                texto += " [activa]";
            }
            listaMaterias.getItems().add(texto);
        }

        setVisible(btnCrear, esProfesor);
        setVisible(btnRenombrar, esProfesor);
        setVisible(btnEliminar, esProfesor);
        setVisible(btnInscribirse, !esProfesor);
        setVisible(btnSeleccionar, true);
    }

    private void setVisible(Button button, boolean visible) {
        button.setVisible(visible);
        button.setManaged(visible);
    }

    private void mostrarError(String mensaje) {
        mensajeLabel.setStyle("-fx-text-fill: red;");
        mensajeLabel.setText(mensaje);
    }

    private void mostrarExito(String mensaje) {
        mensajeLabel.setStyle("-fx-text-fill: green;");
        mensajeLabel.setText(mensaje);
    }
}
