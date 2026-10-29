package blackjack.modelo;

/** Una ronda completa: reparto, turno del jugador, turno automático del computador y resultado. */
public class Ronda {
    public enum Estado { SIN_INICIAR, TURNO_JUGADOR, TERMINADA }

    private final Mazo mazo;
    private final Mano jugador = new Mano();
    private final Mano computador = new Mano();
    private Estado estado = Estado.SIN_INICIAR;
    private Resultado resultado;
    private String mensajeResultado = "";
    private String mensajeEstado = "Ingresa una apuesta para comenzar.";

    /** Ronda normal: mazo nuevo y mezclado. Crear una Ronda nueva por cada ronda de juego. */
    public Ronda() { this(new Mazo()); }

    /** Para pruebas con mazo controlado. */
    public Ronda(Mazo mazo) { this.mazo = mazo; }

    public void iniciar() {
        if (estado != Estado.SIN_INICIAR) return;
        jugador.agregar(mazo.repartir());
        jugador.agregar(mazo.repartir());
        estado = Estado.TURNO_JUGADOR;
        String jugada = jugador.describirJugada();
        mensajeEstado = "Tu turno. Tienes " + jugador.calcularPuntaje() + "."
                + (jugada.isEmpty() ? "" : " ¡" + jugada + "!");
        if (jugador.calcularPuntaje() == 21) turnoComputador();
    }

    public void pedirCarta() {
        if (estado != Estado.TURNO_JUGADOR) return;
        jugador.agregar(mazo.repartir());
        int p = jugador.calcularPuntaje();
        if (p > 21) {
            terminar(Resultado.DERROTA, "Te pasaste con " + p + ". Pierdes la ronda.");
        } else if (p == 21) {
            turnoComputador();
        } else {
            mensajeEstado = "Tienes " + p + ". Pide carta o plántate.";
        }
    }

    public void plantarse() {
        if (estado != Estado.TURNO_JUGADOR) return;
        turnoComputador();
    }

    private void turnoComputador() {
        mensajeEstado = "Turno del computador.";
        computador.agregar(mazo.repartir());
        computador.agregar(mazo.repartir());
        // Con Blackjack del jugador o del computador la ronda se resuelve solo con las dos cartas iniciales.
        if (!jugador.esBlackjack() && !computador.esBlackjack()) {
            while (computador.calcularPuntaje() < 17) computador.agregar(mazo.repartir());
        }
        resolver();
    }

    private void resolver() {
        int pj = jugador.calcularPuntaje(), pc = computador.calcularPuntaje();
        boolean bj = jugador.esBlackjack(), bc = computador.esBlackjack();

        if (bj && bc)      terminar(Resultado.EMPATE, "Ambos tienen Blackjack. Empate.");
        else if (bj)       terminar(Resultado.VICTORIA_BLACKJACK, "¡Blackjack! Ganas; el computador no tiene Blackjack.");
        else if (bc)       terminar(Resultado.DERROTA, "El computador tiene Blackjack. Pierdes.");
        else if (computador.sePaso())
                           terminar(Resultado.VICTORIA, "El computador se pasó con " + pc + ". ¡Ganas!");
        else {
            boolean vj = jugador.esVeinteYMedio(), vc = computador.esVeinteYMedio();
            if (vj && vc)      terminar(Resultado.EMPATE, "Ambos tienen Veinte y medio. Empate.");
            else if (vj)       terminar(pc == 21 ? Resultado.DERROTA : Resultado.VICTORIA,
                                   pc == 21 ? "Tu Veinte y medio pierde contra el 21 del computador."
                                            : "Tu Veinte y medio supera el " + pc + " del computador. ¡Ganas!");
            else if (vc)       terminar(pj == 21 ? Resultado.VICTORIA : Resultado.DERROTA,
                                   pj == 21 ? "Tu 21 supera el Veinte y medio del computador. ¡Ganas!"
                                            : "El Veinte y medio del computador supera tu " + pj + ". Pierdes.");
            else if (pj > pc)  terminar(Resultado.VICTORIA, "Ganas " + pj + " a " + pc + ".");
            else if (pj < pc)  terminar(Resultado.DERROTA, "Pierdes " + pj + " a " + pc + ".");
            else               terminar(Resultado.EMPATE, "Empate a " + pj + ".");
        }
    }

    private void terminar(Resultado r, String msg) {
        resultado = r;
        mensajeResultado = msg;
        mensajeEstado = msg;
        estado = Estado.TERMINADA;
    }

    public boolean estaActiva() { return estado == Estado.TURNO_JUGADOR; }
    public boolean esTurnoJugador() { return estado == Estado.TURNO_JUGADOR; }
    public boolean estaTerminada() { return estado == Estado.TERMINADA; }
    public Estado getEstado() { return estado; }
    public Mano getManoJugador() { return jugador; }
    public Mano getManoComputador() { return computador; }
    public Resultado getResultado() { return resultado; }
    public String getMensajeResultado() { return mensajeResultado; }
    public String getMensajeEstado() { return mensajeEstado; }
}
