package blackjack.modelo;

public class Carta {
    private final Rango rango;
    private final Palo palo;

    public Carta(Rango rango, Palo palo) { this.rango = rango; this.palo = palo; }

    public Rango getRango() { return rango; }
    public Palo getPalo() { return palo; }
    public boolean esAs() { return rango == Rango.AS; }

    /** Nombre sugerido para la imagen, p. ej. "A_PICAS" -> A_PICAS.png */
    public String getNombreImagen() { return rango.getSimbolo() + "_" + palo.name(); }

    @Override public boolean equals(Object o) {
        return o instanceof Carta && ((Carta) o).rango == rango && ((Carta) o).palo == palo;
    }
    @Override public int hashCode() { return rango.ordinal() * 4 + palo.ordinal(); }
    @Override public String toString() { return rango.getSimbolo() + " de " + palo; }
}
