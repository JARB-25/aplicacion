package rb.lms;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.Entrega;
import model.EstadoTarea;
import model.Materia;
import model.Rol;
import model.Tarea;
import model.Usuario;
import service.MateriaService;
import service.TareaService;

public class SecondaryController {

    public enum Modo {
        VER_TAREAS, ENTREGAR, VER_NOTAS, CREAR_TAREA, CALIFICAR
    }

    @FXML private Label tituloPanel;
    @FXML private Label subtituloPanel;
    @FXML private ListView<String> listaItems;
    @FXML private Label mensajeLabel;
    @FXML private TextArea contenidoArea;
    @FXML private javafx.scene.layout.HBox archivoBox;
    @FXML private TextField archivoField;
    @FXML private javafx.scene.layout.HBox calificarBox;
    @FXML private TextField notaField;
    @FXML private TextField comentarioField;
    @FXML private TextArea resultadoArea;
    @FXML private TextField tituloField;
    @FXML private Button btnAccion;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;

    private MateriaService materiaS;
    private TareaService tareaS;
    private Usuario usuarioLogeado;
    private Modo modoActual;
    private List<Tarea> tareasPendientes;
    private Tarea tareaEnEdicion;

    @FXML
    public void initialize() {
        materiaS = AppState.materiaService;
        tareaS = AppState.tareaService;
        mensajeLabel.setText("");
    }

    public void setUsuario(Usuario usuario, Modo modo) {
        this.usuarioLogeado = usuario;
        this.modoActual = modo;
        configurarModo();
        cargarDatos();
    }

    public void setUsuario(Usuario usuario) {
        setUsuario(usuario, Modo.VER_TAREAS);
    }

    private void configurarModo() {
        ocultar(tituloField);
        ocultar(contenidoArea);
        ocultar(archivoBox);
        ocultar(calificarBox);
        ocultar(resultadoArea);
        ocultar(btnAccion);
        ocultar(btnEditar);
        ocultar(btnEliminar);
        subtituloPanel.setText("");
        tareaEnEdicion = null;

        switch (modoActual) {
            case VER_TAREAS:
                tituloPanel.setText("Mis tareas pendientes");
                subtituloPanel.setText("Tareas que aún no has entregado");
                break;
            case ENTREGAR:
                tituloPanel.setText("Entregar tarea");
                subtituloPanel.setText("Selecciona una tarea, escribe tu respuesta y entrega");
                mostrar(contenidoArea);
                mostrar(archivoBox);
                mostrar(btnAccion);
                btnAccion.setText("Entregar tarea seleccionada");
                break;
            case VER_NOTAS:
                tituloPanel.setText("Mis notas");
                subtituloPanel.setText("Entregas calificadas");
                mostrar(resultadoArea);
                break;
            case CREAR_TAREA:
                tituloPanel.setText("Crear nueva tarea");
                subtituloPanel.setText("La fecha límite se establece en 7 días");
                mostrar(tituloField);
                mostrar(contenidoArea);
                mostrar(btnAccion);
                mostrar(btnEditar);
                mostrar(btnEliminar);
                btnAccion.setText("Crear tarea");
                break;
            case CALIFICAR:
                tituloPanel.setText("Calificar entregas");
                subtituloPanel.setText("Selecciona una entrega, ingresa nota (0.0–5.0) y guarda");
                mostrar(calificarBox);
                mostrar(btnAccion);
                btnAccion.setText("Guardar calificación");
                break;
            default:
                break;
        }
    }

    private void cargarDatos() {
        listaItems.getItems().clear();
        mensajeLabel.setText("");
        Materia materia = AppState.getMateriaActual();

        switch (modoActual) {
            case VER_TAREAS:
            case ENTREGAR:
                tareasPendientes = tareaS.obtenerTareasPendientes(usuarioLogeado, materia);
                if (tareasPendientes.isEmpty()) {
                	listaItems.getItems().add("No tienes tareas pendientes");
                } else {
                    for (Tarea tarea : tareasPendientes) {
                        listaItems.getItems().add("Tarea: " + tarea.getTitulo());
                    }
                }
                break;
            case VER_NOTAS:
                StringBuilder sb = new StringBuilder();
                List<Entrega> calificadas = tareaS.obtenerEntregasCalificadas(usuarioLogeado);
                if (calificadas.isEmpty()) {
                	resultadoArea.setText("No hay notas disponibles aun.");
                } else {
                    for (Entrega entrega : calificadas) {
                        sb.append(entrega.getTarea().getTitulo())
                            .append(": ")
                            .append(String.format("%.1f", entrega.getNota()))
                            .append("\n");
                    }
                    resultadoArea.setText(sb.toString());
                }
                break;
            case CREAR_TAREA:
                for (Tarea tarea : materia.getTareas()) {
                	listaItems.getItems().add("Tarea: " + tarea.getTitulo());
                }
                if (materia.getTareas().isEmpty()) {
                	listaItems.getItems().add("(No hay tareas creadas aun)");
                }
                break;
            case CALIFICAR:
                List<Entrega> entregas = tareaS.getEntregas();
                if (entregas.isEmpty()) {
                	listaItems.getItems().add("(No hay entregas registradas)");
                } else {
                    for (Entrega entrega : entregas) {
                        String estado = entrega.getEstado() == EstadoTarea.CALIFICADO
                            ? "Nota: " + String.format("%.1f", entrega.getNota())
                            : "Pendiente";
                        listaItems.getItems().add(
                            entrega.getEstudiante().getEmail() + " - "
                                + entrega.getTarea().getTitulo() + " [" + estado + "]"
                        );
                    }
                }
                break;
            default:
                break;
        }
    }

    @FXML
    private void ejecutarAccion() {
        mensajeLabel.setText("");
        switch (modoActual) {
            case ENTREGAR:
                entregarTarea();
                break;
            case CREAR_TAREA:
                crearTarea();
                break;
            case CALIFICAR:
                calificarTarea();
                break;
            default:
                break;
        }
    }

    private void crearTarea() {
        if (usuarioLogeado == null || usuarioLogeado.getRol() != Rol.PROFESOR) {
            mostrarError("Solo profesores pueden crear tareas");
            return;
        }

        String titulo = tituloField.getText();
        if (titulo == null || titulo.isBlank()) {
            mostrarError("Escribe un titulo para la tarea");
            return;
        }

        String descripcion = contenidoArea.getText() == null ? "" : contenidoArea.getText();
        if (tareaEnEdicion == null) {
            tareaS.crearTarea(titulo, descripcion, AppState.getMateriaActual(), usuarioLogeado, LocalDate.now().plusDays(7));
            mostrarExito("Tarea \"" + titulo + "\" creada exitosamente");
        } else {
            tareaS.actualizarTarea(tareaEnEdicion, titulo, descripcion, usuarioLogeado, AppState.getMateriaActual());
            mostrarExito("Tarea \"" + titulo + "\" actualizada");
        }
        limpiarFormularioTarea();
        cargarDatos();
    }

    @FXML
    private void editarTarea() {
        if (modoActual != Modo.CREAR_TAREA) {
            return;
        }

        Materia materia = AppState.getMateriaActual();
        int index = listaItems.getSelectionModel().getSelectedIndex();
        if (index < 0 || index >= materia.getTareas().size()) {
            mostrarError("Selecciona una tarea");
            return;
        }

        tareaEnEdicion = materia.getTareas().get(index);
        tituloField.setText(tareaEnEdicion.getTitulo());
        contenidoArea.setText(tareaEnEdicion.getDescripcion());
        btnAccion.setText("Guardar cambios");
        mostrarExito("Editando tarea seleccionada");
    }

    @FXML
    private void eliminarTarea() {
        if (modoActual != Modo.CREAR_TAREA) {
            return;
        }

        Materia materia = AppState.getMateriaActual();
        int index = listaItems.getSelectionModel().getSelectedIndex();
        if (index < 0 || index >= materia.getTareas().size()) {
            mostrarError("Selecciona una tarea");
            return;
        }

        Tarea tarea = materia.getTareas().get(index);
        tareaS.eliminarTarea(tarea, usuarioLogeado, materia);
        if (tarea.equals(tareaEnEdicion)) {
            limpiarFormularioTarea();
        }
        mostrarExito("Tarea eliminada");
        cargarDatos();
    }

    private void entregarTarea() {
        if (usuarioLogeado == null || usuarioLogeado.getRol() != Rol.ESTUDIANTE) {
            mostrarError("Solo estudiantes pueden entregar tareas");
            return;
        }

        int index = listaItems.getSelectionModel().getSelectedIndex();
        if (index == -1 || tareasPendientes == null || tareasPendientes.isEmpty()) {
            mostrarError("Selecciona una tarea de la lista");
            return;
        }

        if (index >= tareasPendientes.size()) {
            mostrarError("Seleccion invalida");
            return;
        }

        Tarea tareaSeleccionada = tareasPendientes.get(index);
        Materia materia = AppState.getMateriaActual();

        try {
        	String contenido = contenidoArea.getText() == null ? "" : contenidoArea.getText();
        	String archivo = archivoField.getText() == null ? "" : archivoField.getText().trim();
        	tareaS.entregarTarea(usuarioLogeado, materia, tareaSeleccionada, contenido, archivo);
        	contenidoArea.clear();
        	archivoField.clear();
            mostrarExito("Tarea \"" + tareaSeleccionada.getTitulo() + "\" entregada exitosamente");
            cargarDatos();
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    private void calificarTarea() {
        if (usuarioLogeado == null || usuarioLogeado.getRol() != Rol.PROFESOR) {
            mostrarError("Solo profesores pueden calificar");
            return;
        }

        int index = listaItems.getSelectionModel().getSelectedIndex();
        List<Entrega> entregas = tareaS.getEntregas();

        if (index == -1 || entregas.isEmpty()) {
            mostrarError("Selecciona una entrega de la lista");
            return;
        }

        if (index >= entregas.size()) {
            mostrarError("Seleccion invalida");
            return;
        }

        String notaTexto = notaField.getText() == null ? "" : notaField.getText().trim();
        if (notaTexto.isEmpty()) {
            mostrarError("Ingresa la nota antes de calificar");
            return;
        }

        try {
            double nota = Double.parseDouble(notaTexto.replace(",", "."));
            Entrega entrega = entregas.get(index);
            String comentario = comentarioField.getText() == null ? "" : comentarioField.getText().trim();
            tareaS.calificarTarea(usuarioLogeado, AppState.getMateriaActual(), entrega, nota, comentario);
            comentarioField.clear();
            notaField.clear();
            mostrarExito("Calificacion guardada: " + String.format("%.1f", nota));
            cargarDatos();
        } catch (NumberFormatException e) {
            mostrarError("Ingresa un numero valido (ej: 4.5)");
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

    private void limpiarFormularioTarea() {
        tareaEnEdicion = null;
        contenidoArea.clear();
        tituloField.clear();
        btnAccion.setText("Crear tarea");
    }

    private void mostrar(javafx.scene.Node node) {
        node.setVisible(true);
        node.setManaged(true);
    }

    private void ocultar(javafx.scene.Node node) {
        node.setVisible(false);
        node.setManaged(false);
    }
}
