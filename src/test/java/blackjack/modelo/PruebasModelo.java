package blackjack.modelo;

import static blackjack.modelo.Palo.*;
import static blackjack.modelo.Rango.*;

/** Pruebas sin JUnit: java blackjack.modelo.PruebasModelo */
public class PruebasModelo {
    static int fallos = 0;

    static Carta c(Rango r, Palo p) { return new Carta(r, p); }
    static Mano mano(Carta... cs) { Mano m = new Mano(); for (Carta x : cs) m.agregar(x); return m; }

    static void igual(String prueba, Object esperado, Object obtenido) {
        boolean ok = esperado.equals(obtenido);
        if (!ok) fallos++;
        System.out.println((ok ? "CUMPLE " : "FALLA  ") + prueba + " | esperado=" + esperado + " obtenido=" + obtenido);
    }

    public static void main(String[] args) {
        // Un as
        Mano m = mano(c(AS, PICAS), c(NUEVE, PICAS));
        igual("As + 9", 20, m.calcularPuntaje());
        m.agregar(c(CINCO, PICAS));
        igual("As + 9 + 5", 15, m.calcularPuntaje());

        // Varios ases
        igual("As+As+9", 21, mano(c(AS, PICAS), c(AS, CORAZONES), c(NUEVE, PICAS)).calcularPuntaje());
        igual("As+As+K", 12, mano(c(AS, PICAS), c(AS, CORAZONES), c(K, PICAS)).calcularPuntaje());
        Mano pasada = mano(c(AS, PICAS), c(AS, CORAZONES), c(K, PICAS), c(K, CORAZONES));
        igual("As+As+K+K puntaje", 22, pasada.calcularPuntaje());
        igual("As+As+K+K se pasa", true, pasada.sePaso());

        // Jugadas
        igual("As+10 Blackjack", "Blackjack", mano(c(AS, PICAS), c(DIEZ, PICAS)).describirJugada());
        igual("7+7+7 Veintiuno", "Veintiuno", mano(c(SIETE, PICAS), c(SIETE, CORAZONES), c(SIETE, TREBOLES)).describirJugada());
        igual("J+4 Veinte y medio", "Veinte y medio", mano(c(J, PICAS), c(CUATRO, CORAZONES)).describirJugada());
        igual("As+K+5 no es Blackjack", false, mano(c(AS, PICAS), c(K, PICAS), c(CINCO, PICAS)).esBlackjack());

        // Prioridad: jugador 21 en 3 cartas vs computador As+10 -> gana computador
        Ronda r = new Ronda(Mazo.conOrden(c(CINCO, CORAZONES), c(SEIS, CORAZONES), c(DIEZ, CORAZONES),
                                          c(AS, PICAS), c(K, PICAS)));
        r.iniciar(); r.pedirCarta();
        igual("Prioridad", Resultado.DERROTA, r.getResultado());

        // Blackjack doble
        r = new Ronda(Mazo.conOrden(c(AS, CORAZONES), c(K, CORAZONES), c(AS, PICAS), c(K, PICAS)));
        r.iniciar();
        igual("Blackjack doble", Resultado.EMPATE, r.getResultado());

        // Blackjack del jugador gana con pago especial
        r = new Ronda(Mazo.conOrden(c(AS, CORAZONES), c(K, CORAZONES), c(DIEZ, PICAS), c(NUEVE, PICAS)));
        r.iniciar();
        igual("Victoria con Blackjack", Resultado.VICTORIA_BLACKJACK, r.getResultado());
        igual("Computador no pide con BJ del jugador", 2, r.getManoComputador().tamano());

        // Jugador se pasa: computador sin cartas
        r = new Ronda(Mazo.conOrden(c(K, CORAZONES), c(NUEVE, CORAZONES), c(CINCO, CORAZONES)));
        r.iniciar(); r.pedirCarta();
        igual("Jugador se pasa", Resultado.DERROTA, r.getResultado());
        igual("Computador sin cartas", 0, r.getManoComputador().tamano());

        // Umbral rival
        r = new Ronda(Mazo.conOrden(c(DIEZ, CORAZONES), c(OCHO, CORAZONES), c(DIEZ, PICAS), c(SEIS, PICAS), c(DOS, PICAS)));
        r.iniciar(); r.plantarse();
        igual("Rival con 16 pide", 3, r.getManoComputador().tamano());
        r = new Ronda(Mazo.conOrden(c(DIEZ, CORAZONES), c(OCHO, CORAZONES), c(DIEZ, PICAS), c(SIETE, PICAS)));
        r.iniciar(); r.plantarse();
        igual("Rival 10+7 se planta", 2, r.getManoComputador().tamano());
        r = new Ronda(Mazo.conOrden(c(DIEZ, CORAZONES), c(OCHO, CORAZONES), c(AS, PICAS), c(SEIS, PICAS)));
        r.iniciar(); r.plantarse();
        igual("Rival As+6 (17) se planta", 2, r.getManoComputador().tamano());

        // Comparación
        r = new Ronda(Mazo.conOrden(c(DIEZ, CORAZONES), c(OCHO, CORAZONES), c(DIEZ, PICAS), c(OCHO, PICAS)));
        r.iniciar(); r.plantarse();
        igual("18 vs 18", Resultado.EMPATE, r.getResultado());
        r = new Ronda(Mazo.conOrden(c(DIEZ, CORAZONES), c(DIEZ, DIAMANTES), c(DIEZ, PICAS), c(SEIS, PICAS), c(SEIS, TREBOLES)));
        r.iniciar(); r.plantarse();
        igual("20 vs rival 22", Resultado.VICTORIA, r.getResultado());

        // Veinte y medio: gana a 20, pierde contra 21
        r = new Ronda(Mazo.conOrden(c(J, CORAZONES), c(CUATRO, CORAZONES), c(DIEZ, PICAS), c(DIEZ, TREBOLES)));
        r.iniciar(); r.plantarse();
        igual("Veinte y medio vs 20", Resultado.VICTORIA, r.getResultado());
        r = new Ronda(Mazo.conOrden(c(J, CORAZONES), c(CUATRO, CORAZONES), c(SIETE, PICAS), c(SIETE, TREBOLES), c(SIETE, DIAMANTES)));
        r.iniciar(); r.plantarse();
        igual("Veinte y medio vs 21", Resultado.DERROTA, r.getResultado());

        // Integridad: acciones fuera de turno y 52 cartas sin repetir
        r = new Ronda(Mazo.conOrden(c(K, CORAZONES), c(NUEVE, CORAZONES), c(CINCO, CORAZONES)));
        r.pedirCarta(); r.plantarse();
        igual("Acciones antes de iniciar se ignoran", 0, r.getManoJugador().tamano());
        r.iniciar(); r.plantarse(); r.pedirCarta(); r.plantarse();
        igual("Pedir tras terminar se ignora", 2, r.getManoJugador().tamano());

        Mazo mz = new Mazo();
        java.util.HashSet<Carta> vistas = new java.util.HashSet<>();
        for (int i = 0; i < 52; i++) vistas.add(mz.repartir());
        igual("52 cartas distintas", 52, vistas.size());

        System.out.println(fallos == 0 ? "\nTODAS LAS PRUEBAS CUMPLEN" : "\nFALLOS: " + fallos);
    }
}
