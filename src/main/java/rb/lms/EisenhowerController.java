package rb.lms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import model.Materia;
import model.Rol;
import model.Tarea;
import model.Usuario;
import service.TareaService;

/**
 * FIX 2: El estudiante puede mover tareas en la matriz de Eisenhower
 *         sin alterar la clasificación oficial del profesor.
 *
 * PROBLEMA ORIGINAL:
 *   - mover() lanzaba "Solo el profesor puede reclasificar tareas" para estudiantes.
 *   - Los botones Q1-Q4 estaban deshabilitados para el estudiante.
 *   - El estudiante no podía organizar su propia vista.
 *
 * SOLUCIÓN:
 *   - Se introduce un mapa local (overridesEstudiante) que guarda la posición
 *     personalizada de cada tarea SOLO para la sesión del estudiante.
 *   - El profesor sigue siendo el único que persiste cambios reales (tareaRepo).
 *   - Los botones se habilitan para todos; el comportamiento depende del rol.
 *   - Una etiqueta "(personalizado)" aparece junto a tareas que el estudiante movió.
 */
public class EisenhowerController {

    @FXML private ListView<String> listQ1;
    @FXML private ListView<String> listQ2;
    @FXML private ListView<String> listQ3;
    @FXML private ListView<String> listQ4;
    @FXML private Label mensajeLabel;
    @FXML private Label tituloSelLabel;
    @FXML private Button btnMoverQ1;
    @FXML private Button btnMoverQ2;
    @FXML private Button btnMoverQ3;
    @FXML private Button btnMoverQ4;

    private TareaService tareaS;
    private Usuario usuarioLogeado;
    private Materia materia;
    private Tarea tareaSeleccionada;

    /**
     * Overrides locales del estudiante: titulo → cuadrante (1-4).
     * No se persiste; es solo para la sesión actual.
     */
    private final Map<String, Integer> overridesEstudiante = new HashMap<>();

    @FXML
    public void initialize() {
        tareaS = AppState.tareaService;
        mensajeLabel.setText("");

        listQ1.getSelectionModel().selectedIndexProperty().addListener((obs, o, n) -> {
            if (n.intValue() >= 0) seleccionarDeLista(listQ1, getTareasQ(1), n.intValue());
        });
        listQ2.getSelectionModel().selectedIndexProperty().addListener((obs, o, n) -> {
            if (n.intValue() >= 0) seleccionarDeLista(listQ2, getTareasQ(2), n.intValue());
        });
        listQ3.getSelectionModel().selectedIndexProperty().addListener((obs, o, n) -> {
            if (n.intValue() >= 0) seleccionarDeLista(listQ3, getTareasQ(3), n.intValue());
        });
        listQ4.getSelectionModel().selectedIndexProperty().addListener((obs, o, n) -> {
            if (n.intValue() >= 0) seleccionarDeLista(listQ4, getTareasQ(4), n.intValue());
        });
    }

    public void setUsuario(Usuario usuario) {
        this.usuarioLogeado = usuario;
        this.materia = AppState.getMateriaActual();

        // FIX: botones siempre habilitados — tanto profesor como estudiante pueden mover
        btnMoverQ1.setDisable(false);
        btnMoverQ2.setDisable(false);
        btnMoverQ3.setDisable(false);
        btnMoverQ4.setDisable(false);

        cargarDatos();
    }

    private void cargarDatos() {
        listQ1.getItems().clear();
        listQ2.getItems().clear();
        listQ3.getItems().clear();
        listQ4.getItems().clear();
        tareaSeleccionada = null;
        tituloSelLabel.setText("(ninguna tarea seleccionada)");

        List<Tarea> tareas = tareaS.obtenerTareasOrdenadasEisenhower(materia);
        for (Tarea t : tareas) {
            int q = cuadranteEfectivo(t);
            boolean personalizado = overridesEstudiante.containsKey(t.getTitulo());
            String item = t.getTitulo() + " · " + t.etiquetaEstado()
                        + (personalizado ? " (personalizado)" : "");

            if      (q == 1) listQ1.getItems().add(item);
            else if (q == 2) listQ2.getItems().add(item);
            else if (q == 3) listQ3.getItems().add(item);
            else             listQ4.getItems().add(item);
        }
    }

    /**
     * Cuadrante efectivo: usa el override del estudiante si existe,
     * si no usa la clasificación real de la tarea.
     */
    private int cuadranteEfectivo(Tarea t) {
        if (overridesEstudiante.containsKey(t.getTitulo())) {
            return overridesEstudiante.get(t.getTitulo());
        }
        return cuadranteReal(t);
    }

    private int cuadranteReal(Tarea t) {
        if (t.isUrgente() && t.isImportante())   return 1;
        if (!t.isUrgente() && t.isImportante())  return 2;
        if (t.isUrgente() && !t.isImportante())  return 3;
        return 4;
    }

    private List<Tarea> getTareasQ(int q) {
        List<Tarea> todas = tareaS.obtenerTareasOrdenadasEisenhower(materia);
        List<Tarea> resultado = new ArrayList<>();
        for (Tarea t : todas) if (cuadranteEfectivo(t) == q) resultado.add(t);
        return resultado;
    }

    private void seleccionarDeLista(ListView<String> lista, List<Tarea> tareas, int index) {
        if (lista != listQ1) listQ1.getSelectionModel().clearSelection();
        if (lista != listQ2) listQ2.getSelectionModel().clearSelection();
        if (lista != listQ3) listQ3.getSelectionModel().clearSelection();
        if (lista != listQ4) listQ4.getSelectionModel().clearSelection();

        if (index < tareas.size()) {
            tareaSeleccionada = tareas.get(index);
            tituloSelLabel.setText("Seleccionada: " + tareaSeleccionada.getTitulo()
                + "  (" + tareaSeleccionada.cuadranteEisenhower() + ")");
            mensajeLabel.setText("");
        }
    }

    @FXML private void moverQ1() { mover(1, true, true);   }
    @FXML private void moverQ2() { mover(2, false, true);  }
    @FXML private void moverQ3() { mover(3, true, false);  }
    @FXML private void moverQ4() { mover(4, false, false); }

    /**
     * FIX: 
     *   - PROFESOR → persiste el cambio real (como antes).
     *   - ESTUDIANTE → guarda solo en overridesEstudiante (sesión local).
     */
    private void mover(int cuadrante, boolean urgente, boolean importante) {
        if (tareaSeleccionada == null) {
            mostrarError("Selecciona primero una tarea de algún cuadrante");
            return;
        }

        if (usuarioLogeado.getRol() == Rol.PROFESOR) {
            // Profesor: cambio real y persistente
            try {
                tareaS.clasificarEisenhower(tareaSeleccionada, urgente, importante, usuarioLogeado);
                mostrarExito("\"" + tareaSeleccionada.getTitulo() + "\" movida a "
                    + tareaSeleccionada.cuadranteEisenhower() + " (guardado)");
            } catch (Exception e) {
                mostrarError(e.getMessage());
                return;
            }
        } else {
            // Estudiante: override local solo para esta sesión
            overridesEstudiante.put(tareaSeleccionada.getTitulo(), cuadrante);
            mostrarExito("\"" + tareaSeleccionada.getTitulo() + "\" reorganizada localmente en Q" + cuadrante);
        }

        cargarDatos();
    }

    @FXML
    private void volver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
            Parent root = loader.load();
            MenuController controller = loader.getController();
            controller.setUsuario(usuarioLogeado);
            App.setScene(root);
        } catch (IOException e) {
            mostrarError("Error al volver: " + e.getMessage());
        }
    }

    private void mostrarError(String msg) {
        mensajeLabel.setStyle("-fx-text-fill: red;");
        mensajeLabel.setText(msg);
    }

    private void mostrarExito(String msg) {
        mensajeLabel.setStyle("-fx-text-fill: green;");
        mensajeLabel.setText(msg);
    }
}