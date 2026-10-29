package blackjack.modelo;

public enum Rango {
    AS(1, "A"), DOS(2, "2"), TRES(3, "3"), CUATRO(4, "4"), CINCO(5, "5"), SEIS(6, "6"),
    SIETE(7, "7"), OCHO(8, "8"), NUEVE(9, "9"), DIEZ(10, "10"),
    J(10, "J"), Q(10, "Q"), K(10, "K");

    private final int valor;      // el As vale 1 aquí; el ajuste a 11 lo hace Mano
    private final String simbolo;

    Rango(int valor, String simbolo) { this.valor = valor; this.simbolo = simbolo; }

    public int getValor() { return valor; }
    public String getSimbolo() { return simbolo; }
    public boolean esFigura() { return this == J || this == Q || this == K; }
}
