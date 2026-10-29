package blackjack.controlador;

import blackjack.modelo.Carta;
import blackjack.modelo.Mano;
import blackjack.modelo.Partida;
import blackjack.modelo.Ronda;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador: atiende los eventos, pide las operaciones al modelo (Partida) y actualiza la vista.
 * No calcula puntajes, resultados ni pagos: eso lo hace el modelo.
 */
public class JuegoController {

    @FXML private Label lblSaldo;
    @FXML private Label lblApuesta;
    @FXML private Label lblPuntajeJugador;
    @FXML private Label lblPuntajeComputador;
    @FXML private Label lblEstadoComputador;
    @FXML private Label lblMensaje;
    @FXML private Label lblError;
    @FXML private HBox cartasJugador;
    @FXML private HBox cartasComputador;
    @FXML private TextField txtApuesta;
    @FXML private Button btnIniciar;
    @FXML private Button btnPedir;
    @FXML private Button btnPlantarse;
    @FXML private Button btnReglas;
    @FXML private Button btnCerrar;

    private static final double ANCHO_CARTA = 100;
    private static final double MS_ENTRE_CARTAS = 700;

    private final Partida partida = new Partida();
    private final Map<String, Image> imagenes = new HashMap<>();
    /** true mientras se muestra la secuencia del turno del computador: todas las acciones de juego quedan bloqueadas. */
    private boolean turnoComputadorEnCurso = false;

    @FXML
    private void initialize() {
        actualizar();
    }

    // ------------------------------------------------------------------ eventos

    @FXML
    private void onIniciar() {
        if (turnoComputadorEnCurso || !partida.puedeIniciar()) return;
        String error = partida.iniciarRonda(txtApuesta.getText());
        if (error != null) {
            lblError.setText(error);
            return;                      // no inicia, no reparte, no cambia el saldo
        }
        lblError.setText("");
        txtApuesta.clear();
        despuesDeAccion();
    }

    @FXML
    private void onPedir() {
        if (turnoComputadorEnCurso || !partida.puedePedir()) return;
        partida.pedirCarta();
        despuesDeAccion();
    }

    @FXML
    private void onPlantarse() {
        if (turnoComputadorEnCurso || !partida.puedePlantarse()) return;
        // Se deshabilita de inmediato: cualquier activación adicional se ignora (HU05.5).
        btnPedir.setDisable(true);
        btnPlantarse.setDisable(true);
        partida.plantarse();
        despuesDeAccion();
    }

    @FXML
    private void onReglas() {
        // La ventana de reglas es modal y no toca el modelo: la partida queda como estaba (HU11.3).
        TextArea area = new TextArea(Reglas.TEXTO);
        area.setEditable(false);
        area.setWrapText(true);
        area.setPrefSize(620, 460);

        Alert dialogo = new Alert(Alert.AlertType.INFORMATION);
        dialogo.initOwner(lblMensaje.getScene().getWindow());
        dialogo.setTitle("Reglas");
        dialogo.setHeaderText("Reglas de 21 Black Jack (esta adaptación)");
        dialogo.getDialogPane().setContent(area);
        dialogo.setResizable(true);
        dialogo.showAndWait();
    }

    @FXML
    private void onCerrar() {
        confirmarCierre();
    }

    /** Usado por el botón «Cerrar juego» y por la X de la ventana (ver Main). */
    public void confirmarCierre() {
        ButtonType si = new ButtonType("Sí, cerrar", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert dialogo = new Alert(Alert.AlertType.CONFIRMATION, "", si, no);
        dialogo.initOwner(lblMensaje.getScene().getWindow());
        dialogo.setTitle("Cerrar juego");
        dialogo.setHeaderText("¿Seguro que quieres cerrar el juego?");
        dialogo.setContentText("La partida actual (saldo, apuesta y cartas) no se guarda.");
        Optional<ButtonType> respuesta = dialogo.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == si) {
            Platform.exit();
        }
        // Si cancela no se toca nada: la partida continúa exactamente igual (HU12.2).
    }

    // ------------------------------------------------------------------ presentación

    /** Tras iniciar, pedir o plantarse: si el computador jugó, se muestra su turno como secuencia breve. */
    private void despuesDeAccion() {
        Ronda ronda = partida.getRonda();
        boolean computadorJugo = ronda != null && ronda.estaTerminada()
                && ronda.getManoComputador().tamano() > 0;
        if (computadorJugo) {
            animarTurnoComputador(ronda);
        } else {
            actualizar();
        }
    }

    /** Muestra las cartas del computador una por una y al final el resultado y el saldo. */
    private void animarTurnoComputador(Ronda ronda) {
        turnoComputadorEnCurso = true;
        Carta[] cartas = ronda.getManoComputador().getCartas();
        Mano parcial = new Mano();

        // Estado visible mientras el computador juega: el jugador ya no puede actuar.
        lblSaldo.setText("Saldo: " + (partida.getSaldo() - partida.getPagoUltimaRonda()) + " fichas");
        lblApuesta.setText("Apuesta: " + partida.getApuesta() + " fichas");
        mostrarMano(cartasJugador, ronda.getManoJugador());
        lblPuntajeJugador.setText(textoPuntaje(ronda.getManoJugador()));
        cartasComputador.getChildren().clear();
        lblPuntajeComputador.setText("Puntaje: —");
        lblEstadoComputador.setText("El computador está jugando…");
        lblMensaje.setText("Terminó tu turno con " + ronda.getManoJugador().calcularPuntaje()
                + ". Turno del computador…");
        lblError.setText("");
        txtApuesta.setDisable(true);
        btnIniciar.setDisable(true);
        btnPedir.setDisable(true);
        btnPlantarse.setDisable(true);

        Timeline secuencia = new Timeline();
        for (int i = 0; i < cartas.length; i++) {
            final Carta carta = cartas[i];
            secuencia.getKeyFrames().add(new KeyFrame(Duration.millis(MS_ENTRE_CARTAS * (i + 1)), e -> {
                parcial.agregar(carta);
                cartasComputador.getChildren().add(crearVistaCarta(carta));
                lblPuntajeComputador.setText(textoPuntaje(parcial));
            }));
        }
        secuencia.getKeyFrames().add(new KeyFrame(Duration.millis(MS_ENTRE_CARTAS * (cartas.length + 1)), e -> {
            turnoComputadorEnCurso = false;
            actualizar();
        }));
        secuencia.play();
    }

    /** Pone en pantalla exactamente el estado actual del modelo (HU01, HU03, HU09, HU10). */
    private void actualizar() {
        Ronda ronda = partida.getRonda();

        lblSaldo.setText("Saldo: " + partida.getSaldo() + " fichas");
        lblApuesta.setText(ronda == null ? "Apuesta: —" : "Apuesta: " + partida.getApuesta() + " fichas");

        mostrarMano(cartasJugador, ronda == null ? null : ronda.getManoJugador());
        mostrarMano(cartasComputador, ronda == null ? null : ronda.getManoComputador());
        lblPuntajeJugador.setText(ronda == null ? "Puntaje: —" : textoPuntaje(ronda.getManoJugador()));
        lblPuntajeComputador.setText(ronda == null ? "Puntaje: —" : textoPuntaje(ronda.getManoComputador()));
        lblEstadoComputador.setText(textoEstadoComputador(ronda));

        lblMensaje.setText(textoMensaje(ronda));

        boolean puedeIniciar = partida.puedeIniciar();
        txtApuesta.setDisable(!puedeIniciar);
        btnIniciar.setDisable(!puedeIniciar);
        btnPedir.setDisable(!partida.puedePedir());
        btnPlantarse.setDisable(!partida.puedePlantarse());
        btnReglas.setDisable(false);
        btnCerrar.setDisable(false);

        if (!partida.rondaActiva() && !partida.haySaldoParaApostar()) {
            lblError.setText("No hay fichas suficientes para otra ronda.");
        }
        if (puedeIniciar) {
            txtApuesta.requestFocus();
        }
    }

    private String textoMensaje(Ronda ronda) {
        if (ronda == null) {
            return "Ingresa una apuesta para comenzar.";
        }
        if (ronda.estaTerminada()) {
            StringBuilder sb = new StringBuilder(ronda.getMensajeResultado()).append("  ");
            int neto = partida.getNetoUltimaRonda();
            if (neto > 0) sb.append("Ganas ").append(neto).append(" fichas.");
            else if (neto == 0) sb.append("Se te devuelve la apuesta de ").append(partida.getApuesta()).append(" fichas.");
            else sb.append("Pierdes ").append(-neto).append(" fichas.");
            if (partida.haySaldoParaApostar()) {
                sb.append(" Ingresa una nueva apuesta para jugar otra ronda.");
            } else {
                sb.append(" No tienes fichas suficientes: no es posible continuar apostando.");
            }
            return sb.toString();
        }
        String estado = ronda.getMensajeEstado();
        return estado.contains("Pide carta") ? estado : estado + " Pide carta o plántate.";
    }

    private String textoEstadoComputador(Ronda ronda) {
        if (ronda == null) return "Aún no hay ronda.";
        if (ronda.getManoComputador().tamano() > 0) return "";
        return ronda.estaTerminada() ? "El computador no jugó en esta ronda." : "Espera su turno, sin cartas.";
    }

    private String textoPuntaje(Mano mano) {
        if (mano.tamano() == 0) return "Puntaje: —";
        String jugada = mano.describirJugada();
        return "Puntaje: " + mano.calcularPuntaje() + (jugada.isEmpty() ? "" : "  ·  " + jugada);
    }

    private void mostrarMano(HBox zona, Mano mano) {
        zona.getChildren().clear();
        if (mano == null) return;
        for (Carta carta : mano.getCartas()) {
            zona.getChildren().add(crearVistaCarta(carta));
        }
    }

    private Node crearVistaCarta(Carta carta) {
        ImageView vista = new ImageView(cargarImagen(carta.getNombreImagen()));
        vista.setFitWidth(ANCHO_CARTA);
        vista.setPreserveRatio(true);
        vista.setSmooth(true);
        return vista;
    }

    private Image cargarImagen(String nombre) {
        Image imagen = imagenes.get(nombre);
        if (imagen == null) {
            InputStream flujo = getClass().getResourceAsStream("/blackjack/imagenes/" + nombre + ".png");
            if (flujo == null) {
                throw new IllegalStateException("Falta la imagen de la carta: " + nombre + ".png");
            }
            imagen = new Image(flujo);
            imagenes.put(nombre, imagen);
        }
        return imagen;
    }
}
