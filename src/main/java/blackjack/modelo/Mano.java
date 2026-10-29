package blackjack.modelo;

/** Mano como lista enlazada simple de cartas. */
public class Mano {
    private static class Nodo {
        Carta carta; Nodo sig;
        Nodo(Carta c) { carta = c; }
    }

    private Nodo cabeza, cola;
    private int tamano = 0;

    public void agregar(Carta c) {
        Nodo n = new Nodo(c);
        if (cabeza == null) cabeza = cola = n;
        else { cola.sig = n; cola = n; }
        tamano++;
    }

    public void vaciar() { cabeza = cola = null; tamano = 0; }
    public int tamano() { return tamano; }

    public Carta[] getCartas() {
        Carta[] r = new Carta[tamano];
        int i = 0;
        for (Nodo n = cabeza; n != null; n = n.sig) r[i++] = n.carta;
        return r;
    }

    /** Ases como 1 u 11 para el mayor puntaje sin pasar de 21; si todo se pasa, ases = 1. */
    public int calcularPuntaje() {
        int suma = 0, ases = 0;
        for (Nodo n = cabeza; n != null; n = n.sig) {
            suma += n.carta.getRango().getValor();
            if (n.carta.esAs()) ases++;
        }
        if (ases > 0 && suma + 10 <= 21) suma += 10;
        return suma;
    }

    /** As + carta de valor 10 en las dos cartas iniciales. */
    public boolean esBlackjack() {
        if (tamano != 2) return false;
        Carta a = cabeza.carta, b = cola.carta;
        return (a.esAs() && b.getRango().getValor() == 10)
            || (b.esAs() && a.getRango().getValor() == 10);
    }

    public boolean esVeintiuno() { return calcularPuntaje() == 21 && !esBlackjack(); }

    /** Dos cartas: una figura (J, Q, K) y un 4, sin importar palo ni orden. */
    public boolean esVeinteYMedio() {
        if (tamano != 2) return false;
        Carta a = cabeza.carta, b = cola.carta;
        return (a.getRango().esFigura() && b.getRango() == Rango.CUATRO)
            || (b.getRango().esFigura() && a.getRango() == Rango.CUATRO);
    }

    public boolean sePaso() { return calcularPuntaje() > 21; }

    /** Texto de la jugada especial ("" si no hay). */
    public String describirJugada() {
        if (esBlackjack()) return "Blackjack";
        if (esVeinteYMedio()) return "Veinte y medio";
        if (sePaso()) return "Pasarse";
        if (esVeintiuno()) return "Veintiuno";
        return "";
    }
}
