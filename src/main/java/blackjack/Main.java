package blackjack;

import blackjack.controlador.JuegoController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Punto de entrada: carga la vista FXML y pide confirmación al cerrar con la X (HU12). */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("vista/Juego.fxml"));
        Parent raiz = loader.load();
        JuegoController controlador = loader.getController();

        stage.setTitle("21 Black Jack - Duelo contra el computador");
        stage.setScene(new Scene(raiz));
        stage.setMinWidth(900);
        stage.setMinHeight(760);

        // La X de la ventana no cierra directamente: se consume el evento y se pide confirmación.
        stage.setOnCloseRequest(evento -> {
            evento.consume();
            controlador.confirmarCierre();
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
