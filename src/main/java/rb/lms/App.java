package rb.lms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Stage primaryStage;
    private static final double WIDTH = 640;
    private static final double HEIGHT = 520;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        primaryStage.setTitle("LMS - Sistema de Gestión de Aprendizaje");
        primaryStage.setMinWidth(WIDTH);
        primaryStage.setMinHeight(HEIGHT);
        primaryStage.setResizable(false);

        Parent root = loadFXML("primary");
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /** Cambia el contenido de la ventana SIN cambiar su tamaño */
    public static void setRoot(String fxml) throws IOException {
        primaryStage.getScene().setRoot(loadFXML(fxml));
    }

    /** Cambia la escena pasando el controlador ya configurado */
    public static void setScene(Parent root) {
        primaryStage.getScene().setRoot(root);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static <T> T loadFXMLWithController(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        loader.load();
        return loader.getController();
    }

    public static void main(String[] args) {
        launch();
    }
}