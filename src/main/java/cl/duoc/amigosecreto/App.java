package cl.duoc.amigosecreto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Carga la ventana principal definida en main-view.fxml.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("view/main-view.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Amigo Secreto — Registro de participantes");
        stage.setScene(scene);
        stage.show();
    }
}
