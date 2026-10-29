package blackjack.modelo;

import java.util.Random;

/** Mazo de 52 cartas en un arreglo. Se reparte avanzando un índice (sin repetir). */
public class Mazo {
    private final Carta[] cartas = new Carta[52];
    private int siguiente = 0;

    /** Mazo completo y mezclado. */
    public Mazo() {
        int n = 0;
        for (Palo p : Palo.values())
            for (Rango r : Rango.values())
                cartas[n++] = new Carta(r, p);
        barajar();
    }

    private Mazo(boolean vacio) { }

    /** Para pruebas: las primeras cartas salen en el orden dado; el resto se completa sin repetir. */
    public static Mazo conOrden(Carta... orden) {
        Mazo m = new Mazo(true);
        int n = 0;
        for (Carta c : orden) m.cartas[n++] = c;
        for (Palo p : Palo.values())
            for (Rango r : Rango.values()) {
                Carta c = new Carta(r, p);
                boolean ya = false;
                for (Carta o : orden) if (o.equals(c)) ya = true;
                if (!ya) m.cartas[n++] = c;
            }
        return m;
    }

    private void barajar() {
        Random rnd = new Random();
        for (int i = cartas.length - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            Carta t = cartas[i]; cartas[i] = cartas[j]; cartas[j] = t;
        }
        siguiente = 0;
    }

    public Carta repartir() {
        if (siguiente >= cartas.length) throw new IllegalStateException("El mazo no tiene más cartas");
        return cartas[siguiente++];
    }

    public int restantes() { return cartas.length - siguiente; }
}
