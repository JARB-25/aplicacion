package rb.lms;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import model.HistorialEntrada;
import model.Usuario;
import repository.HistorialRepository;

public class HistorialController {

    @FXML private Label tituloLabel;
    @FXML private TextArea historialArea;
    @FXML private Label mensajeLabel;

    private Usuario usuarioLogeado;
    private final HistorialRepository historialRepo = AppState.historialRepository;

    @FXML
    public void initialize() {
        mensajeLabel.setText("");
    }

    public void setUsuario(Usuario usuario) {
        this.usuarioLogeado = usuario;
        cargarHistorial();
    }

    private void cargarHistorial() {
        List<HistorialEntrada> entradas = historialRepo.obtenerDeUsuario(usuarioLogeado.getEmail());
        if (entradas.isEmpty()) {
            historialArea.setText("No hay actividad registrada.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (HistorialEntrada e : entradas) {
            sb.append(e.toString()).append("\n");
        }
        historialArea.setText(sb.toString());
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
            mensajeLabel.setStyle("-fx-text-fill: red;");
            mensajeLabel.setText("Error al volver: " + e.getMessage());
        }
    }
}