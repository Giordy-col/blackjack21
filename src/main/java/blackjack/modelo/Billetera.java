package blackjack.modelo;

/**
 * Saldo de fichas del jugador, validación de apuestas y liquidación (HU01, HU02, HU09).
 * El computador es la banca y no tiene saldo propio.
 */
public class Billetera {
    public static final int SALDO_INICIAL = 1000;
    public static final int APUESTA_MINIMA = 10;
    public static final int MULTIPLO = 10;

    private int saldo = SALDO_INICIAL;

    public int getSaldo() { return saldo; }

    /** Hay fichas para al menos la apuesta mínima (HU02 2.5 / HU10 10.4). */
    public boolean puedeApostar() { return saldo >= APUESTA_MINIMA; }

    /**
     * Valida el texto escrito en el campo de apuesta. No modifica el saldo.
     * @return null si la apuesta es válida; si no, un mensaje que explica cómo corregirla.
     */
    public String validarApuesta(String texto) {
        if (!puedeApostar())
            return "No tienes fichas suficientes para otra ronda (mínimo " + APUESTA_MINIMA + ").";
        if (texto == null || texto.trim().isEmpty())
            return "Ingresa una apuesta para comenzar.";
        String t = texto.trim();

        if (t.matches("-?\\d+[.,]\\d*") || t.matches("-?[.,]\\d+"))
            return "La apuesta debe ser un número entero, sin decimales, puntos ni comas.";
        if (!t.matches("-?\\d+"))
            return "La apuesta debe ser un número entero. Escribe solo dígitos (por ejemplo, 50).";
        if (t.startsWith("-"))
            return "La apuesta no puede ser negativa. Escribe un múltiplo de " + MULTIPLO
                    + " desde " + APUESTA_MINIMA + ".";

        // Solo dígitos: un número muy largo siempre supera el saldo (evita desbordar un int).
        String digitos = t.replaceFirst("^0+(?=\\d)", "");
        if (digitos.length() > 9)
            return "La apuesta supera tu saldo de " + saldo + " fichas.";
        int apuesta = Integer.parseInt(digitos);

        if (apuesta == 0)
            return "La apuesta no puede ser cero. Escribe un múltiplo de " + MULTIPLO
                    + " desde " + APUESTA_MINIMA + ".";
        if (apuesta < APUESTA_MINIMA)
            return "La apuesta mínima es de " + APUESTA_MINIMA + " fichas.";
        if (apuesta % MULTIPLO != 0)
            return "La apuesta debe ser múltiplo de " + MULTIPLO + " (por ejemplo, 10, 20, 30...).";
        if (apuesta > saldo)
            return "La apuesta supera tu saldo de " + saldo + " fichas.";
        return null;
    }

    /** Convierte un texto ya validado en la cantidad apostada. */
    public int parsearApuesta(String texto) {
        if (validarApuesta(texto) != null)
            throw new IllegalArgumentException("Apuesta no válida: " + texto);
        return Integer.parseInt(texto.trim().replaceFirst("^0+(?=\\d)", ""));
    }

    /** Descuenta la apuesta al comenzar la ronda. */
    public void descontar(int apuesta) {
        if (apuesta < APUESTA_MINIMA || apuesta % MULTIPLO != 0 || apuesta > saldo)
            throw new IllegalArgumentException("No se puede descontar " + apuesta + " con saldo " + saldo);
        saldo -= apuesta;
    }

    /** Fichas que se acreditan al terminar la ronda (la apuesta ya fue descontada). */
    public static int pago(int apuesta, Resultado resultado) {
        switch (resultado) {
            case DERROTA:            return 0;
            case EMPATE:             return apuesta;
            case VICTORIA:           return apuesta * 2;
            case VICTORIA_BLACKJACK: return apuesta * 5 / 2;   // apuesta + 1,5 veces; la apuesta es múltiplo de 10
            default: throw new IllegalArgumentException("Resultado desconocido");
        }
    }

    /** Acredita el pago de la ronda. Quien la llama garantiza que se haga una sola vez por ronda. */
    public int liquidar(int apuesta, Resultado resultado) {
        int p = pago(apuesta, resultado);
        saldo += p;
        return p;
    }
}
