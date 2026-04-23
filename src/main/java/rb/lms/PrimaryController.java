package rb.lms;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.Rol;
import model.Usuario;
import service.AuthService;

public class PrimaryController {

    private final AuthService authService = AppState.authService;

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisibleField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField confirmPasswordVisibleField;
    @FXML private CheckBox mostrarPassword;
    @FXML private ComboBox<String> rolComboBox;
    @FXML private Label mensajeLabel;

    @FXML
    public void initialize() {
        // Configurar ComboBox de rol
        rolComboBox.getItems().addAll("Estudiante", "Profesor");
        rolComboBox.setValue("Estudiante");

        // Bind campos de contraseña visible <-> oculta
        passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());
        confirmPasswordVisibleField.textProperty().bindBidirectional(confirmPasswordField.textProperty());

        // Bind managed a visible para que no ocupen espacio cuando están ocultos
        passwordField.managedProperty().bind(passwordField.visibleProperty());
        passwordVisibleField.managedProperty().bind(passwordVisibleField.visibleProperty());
        confirmPasswordField.managedProperty().bind(confirmPasswordField.visibleProperty());
        confirmPasswordVisibleField.managedProperty().bind(confirmPasswordVisibleField.visibleProperty());

        // Alternar visibilidad con el checkbox
        passwordField.visibleProperty().bind(mostrarPassword.selectedProperty().not());
        passwordVisibleField.visibleProperty().bind(mostrarPassword.selectedProperty());
        confirmPasswordField.visibleProperty().bind(mostrarPassword.selectedProperty().not());
        confirmPasswordVisibleField.visibleProperty().bind(mostrarPassword.selectedProperty());

        mensajeLabel.setText("");
    }

    @FXML
    private void registrar() {
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String password = obtenerPassword();
        String confirmPassword = obtenerConfirmPassword();
        String rolSeleccionado = rolComboBox.getValue();

        try {
            if (!password.equals(confirmPassword)) {
                mostrarError("Las contraseñas no coinciden");
                return;
            }

            if (authService.existeUsuario(email)) {
                mostrarError("El usuario ya existe");
                return;
            }

            Rol rol = "Profesor".equals(rolSeleccionado) ? Rol.PROFESOR : Rol.ESTUDIANTE;
            Usuario usuario = AppState.registrarUsuario(email, password, rol);
            AppState.setUsuarioActual(usuario);

            mensajeLabel.setStyle("-fx-text-fill: green;");
            mensajeLabel.setText("Usuario registrado correctamente. Ya puedes iniciar sesión.");
            limpiarCampos();
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void login() {
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String password = obtenerPassword();

        try {
            AppState.initialize();
            Usuario usuario = authService.login(email, password);
            AppState.setUsuarioActual(usuario);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
            Parent root = loader.load();

            MenuController controller = loader.getController();
            controller.setUsuario(usuario);

            App.setScene(root);
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    private String obtenerPassword() {
        if (mostrarPassword != null && mostrarPassword.isSelected() && passwordVisibleField != null) {
            return passwordVisibleField.getText();
        }
        return passwordField == null ? "" : passwordField.getText();
    }

    private String obtenerConfirmPassword() {
        if (mostrarPassword != null && mostrarPassword.isSelected() && confirmPasswordVisibleField != null) {
            return confirmPasswordVisibleField.getText();
        }
        return confirmPasswordField == null ? "" : confirmPasswordField.getText();
    }

    private void mostrarError(String msg) {
        mensajeLabel.setStyle("-fx-text-fill: red;");
        mensajeLabel.setText(msg);
    }

    private void limpiarCampos() {
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        mostrarPassword.setSelected(false);
    }
}
