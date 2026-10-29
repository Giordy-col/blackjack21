package blackjack.modelo;

/**
 * Une la Billetera (dinero) con la Ronda (cartas). Es lo único que usa el controlador.
 * Garantiza: la apuesta se descuenta una vez al iniciar y el resultado se liquida una sola vez.
 */
public class Partida {
    private final Billetera billetera = new Billetera();
    private Ronda ronda;              // null antes de la primera ronda
    private int apuesta = 0;          // apuesta de la ronda actual (0 si no hay ronda)
    private boolean liquidada = false;
    private int pagoUltimaRonda = 0;

    public int getSaldo() { return billetera.getSaldo(); }
    public int getApuesta() { return apuesta; }
    public Ronda getRonda() { return ronda; }
    public int getPagoUltimaRonda() { return pagoUltimaRonda; }

    /** Hay una ronda en curso (turno del jugador). */
    public boolean rondaActiva() { return ronda != null && !ronda.estaTerminada(); }
    public boolean haySaldoParaApostar() { return billetera.puedeApostar(); }
    public boolean puedeIniciar() { return !rondaActiva() && haySaldoParaApostar(); }
    public boolean puedePedir() { return ronda != null && ronda.esTurnoJugador(); }
    public boolean puedePlantarse() { return ronda != null && ronda.esTurnoJugador(); }

    /** Validación de la apuesta sin efectos (para mostrar mensajes). */
    public String validarApuesta(String texto) { return billetera.validarApuesta(texto); }

    /**
     * Inicia una ronda nueva con la apuesta escrita.
     * @return null si se inició; si no, el mensaje de error (no cambia nada).
     */
    public String iniciarRonda(String textoApuesta) {
        return iniciarRonda(textoApuesta, new Mazo());
    }

    /** Igual que iniciarRonda(String) pero con un mazo dado (para pruebas con cartas controladas). */
    public String iniciarRonda(String textoApuesta, Mazo mazo) {
        if (rondaActiva()) return "Ya hay una ronda en curso. Termínala antes de iniciar otra.";
        String error = billetera.validarApuesta(textoApuesta);
        if (error != null) return error;

        apuesta = billetera.parsearApuesta(textoApuesta);
        billetera.descontar(apuesta);
        liquidada = false;
        pagoUltimaRonda = 0;
        ronda = new Ronda(mazo);
        ronda.iniciar();
        liquidarSiTerminada();   // por si el jugador ya terminó con 21 / Blackjack
        return null;
    }

    public void pedirCarta() {
        if (!puedePedir()) return;
        ronda.pedirCarta();
        liquidarSiTerminada();
    }

    public void plantarse() {
        if (!puedePlantarse()) return;
        ronda.plantarse();
        liquidarSiTerminada();
    }

    private void liquidarSiTerminada() {
        if (ronda != null && ronda.estaTerminada() && !liquidada) {
            pagoUltimaRonda = billetera.liquidar(apuesta, ronda.getResultado());
            liquidada = true;
        }
    }

    /** Resultado neto de la ronda: fichas ganadas (+) o perdidas (-) respecto a antes de apostar. */
    public int getNetoUltimaRonda() { return pagoUltimaRonda - apuesta; }
}
