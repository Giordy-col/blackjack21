package blackjack.modelo;

import static blackjack.modelo.Palo.*;
import static blackjack.modelo.Rango.*;

/** Pruebas de Persona 2 (sin JUnit): java blackjack.modelo.PruebasBilletera */
public class PruebasBilletera {
    static int fallos = 0;

    static Carta c(Rango r, Palo p) { return new Carta(r, p); }

    static void igual(String prueba, Object esperado, Object obtenido) {
        boolean ok = esperado == null ? obtenido == null : esperado.equals(obtenido);
        if (!ok) fallos++;
        System.out.println((ok ? "CUMPLE " : "FALLA  ") + prueba + " | esperado=" + esperado + " obtenido=" + obtenido);
    }

    static void rechaza(String prueba, Billetera b, String texto) {
        String msg = b.validarApuesta(texto);
        boolean ok = msg != null && !msg.isEmpty() && b.getSaldo() == Billetera.SALDO_INICIAL;
        if (!ok) fallos++;
        System.out.println((ok ? "CUMPLE " : "FALLA  ") + prueba + " | entrada=\"" + texto + "\" mensaje=" + msg);
    }

    /** Juega una ronda con mazo controlado: jugador recibe las dos primeras, computador las dos siguientes. */
    static Partida jugar(String apuesta, boolean plantar, Carta... orden) {
        Partida p = new Partida();
        String err = p.iniciarRonda(apuesta, Mazo.conOrden(orden));
        if (err != null) throw new IllegalStateException(err);
        if (plantar) p.plantarse();
        return p;
    }

    public static void main(String[] args) {
        // --- HU01: estado inicial
        Partida inicial = new Partida();
        igual("Saldo inicial", 1000, inicial.getSaldo());
        igual("Sin apuesta antes de la primera ronda", 0, inicial.getApuesta());
        igual("Sin ronda antes de empezar", null, inicial.getRonda());
        igual("Pedir y plantarse deshabilitados", false, inicial.puedePedir() || inicial.puedePlantarse());
        igual("Se puede iniciar", true, inicial.puedeIniciar());

        // --- HU02: apuestas incorrectas (vacío, texto, 15, 0, -10, 10,5, mayor al saldo...)
        Billetera b = new Billetera();
        rechaza("Apuesta vacía", b, "");
        rechaza("Apuesta solo espacios", b, "   ");
        rechaza("Apuesta nula", b, null);
        rechaza("Texto", b, "abc");
        rechaza("Texto con número", b, "10 fichas");
        rechaza("No múltiplo de 10", b, "15");
        rechaza("Cero", b, "0");
        rechaza("Negativo", b, "-10");
        rechaza("Decimal con coma", b, "10,5");
        rechaza("Decimal con punto", b, "10.5");
        rechaza("Menor de 10", b, "5");
        rechaza("Mayor al saldo", b, "1010");
        rechaza("Número enorme", b, "99999999999999999999");
        igual("Apuesta 10 válida", null, b.validarApuesta("10"));
        igual("Apuesta 100 válida", null, b.validarApuesta("100"));
        igual("Apuesta con espacios válida", null, b.validarApuesta("  50 "));
        igual("Validar no descuenta saldo", 1000, b.getSaldo());

        // Rechazo desde la partida: no inicia ronda, no reparte, no cambia saldo
        Partida p0 = new Partida();
        String err = p0.iniciarRonda("15");
        igual("Rechazo informa error", true, err != null);
        igual("Rechazo no inicia ronda", null, p0.getRonda());
        igual("Rechazo no cambia saldo", 1000, p0.getSaldo());

        // --- Límite: saldo 1000, apuesta 1000
        Partida pl = jugar("1000", false, c(K, CORAZONES), c(NUEVE, CORAZONES), c(DIEZ, PICAS), c(SIETE, PICAS));
        igual("Límite: saldo durante la ronda es 0", 0, pl.getSaldo());
        igual("Límite: apuesta vigente 1000", 1000, pl.getApuesta());
        igual("Límite: ronda activa", true, pl.rondaActiva());

        // --- HU09: pagos con saldo 1000 y apuesta 100
        // Derrota: jugador 10+8 (18) vs computador 10+9 (19)
        Partida pd = jugar("100", true, c(DIEZ, CORAZONES), c(OCHO, CORAZONES), c(DIEZ, PICAS), c(NUEVE, PICAS));
        igual("Pago derrota -> 900", 900, pd.getSaldo());
        // Empate 18 vs 18
        Partida pe = jugar("100", true, c(DIEZ, CORAZONES), c(OCHO, CORAZONES), c(DIEZ, PICAS), c(OCHO, PICAS));
        igual("Pago empate -> 1000", 1000, pe.getSaldo());
        // Victoria ordinaria 20 vs 18
        Partida pv = jugar("100", true, c(DIEZ, CORAZONES), c(DIEZ, DIAMANTES), c(DIEZ, PICAS), c(OCHO, PICAS));
        igual("Pago victoria ordinaria -> 1100", 1100, pv.getSaldo());
        // Victoria con Blackjack
        Partida pb = jugar("100", false, c(AS, CORAZONES), c(K, CORAZONES), c(DIEZ, PICAS), c(NUEVE, PICAS));
        igual("Pago victoria con Blackjack -> 1150", 1150, pb.getSaldo());
        // Empate con Blackjack devuelve la apuesta
        Partida pbb = jugar("100", false, c(AS, CORAZONES), c(K, CORAZONES), c(AS, PICAS), c(K, PICAS));
        igual("Empate con Blackjack -> 1000", 1000, pbb.getSaldo());
        // 21 sin Blackjack recibe pago ordinario: jugador 7+7+7, computador 10+9
        Partida p21 = new Partida();
        p21.iniciarRonda("100", Mazo.conOrden(c(SIETE, CORAZONES), c(SIETE, DIAMANTES), c(SIETE, PICAS),
                c(DIEZ, PICAS), c(NUEVE, PICAS)));
        p21.pedirCarta();
        igual("21 no Blackjack paga ordinario -> 1100", 1100, p21.getSaldo());
        // Jugador se pasa
        Partida pp = new Partida();
        pp.iniciarRonda("100", Mazo.conOrden(c(K, CORAZONES), c(NUEVE, CORAZONES), c(CINCO, CORAZONES)));
        pp.pedirCarta();
        igual("Jugador se pasa -> 900", 900, pp.getSaldo());
        igual("Neto al perder", -100, pp.getNetoUltimaRonda());
        igual("Neto Blackjack", 150, pb.getNetoUltimaRonda());

        // Pago aplicado una sola vez: repetir acciones no cambia el saldo
        pv.plantarse(); pv.plantarse(); pv.pedirCarta(); pv.pedirCarta();
        igual("Repetir acciones no paga de nuevo", 1100, pv.getSaldo());
        igual("Ronda terminada no está activa", false, pv.rondaActiva());

        // --- HU03.6: no se puede iniciar otra ronda mientras la actual esté activa
        Partida pa = new Partida();
        pa.iniciarRonda("100", Mazo.conOrden(c(DIEZ, CORAZONES), c(SEIS, CORAZONES), c(DIEZ, PICAS), c(SIETE, PICAS)));
        String again = pa.iniciarRonda("100");
        igual("Segunda activación se rechaza", true, again != null);
        igual("Segunda activación no descuenta otra vez", 900, pa.getSaldo());
        igual("Apuesta fija durante la ronda", 100, pa.getApuesta());

        // --- Acciones fuera de turno
        Partida pf = new Partida();
        pf.pedirCarta(); pf.plantarse();
        igual("Pedir antes de iniciar no hace nada", null, pf.getRonda());

        // --- HU10: otra ronda conserva el saldo (no se restablecen las 1000 fichas)
        Partida pc = jugar("100", true, c(DIEZ, CORAZONES), c(OCHO, CORAZONES), c(DIEZ, PICAS), c(NUEVE, PICAS));
        igual("Saldo tras perder", 900, pc.getSaldo());
        igual("Se puede iniciar otra ronda", true, pc.puedeIniciar());
        igual("Apuesta nueva se valida contra 900", true, pc.validarApuesta("950") != null);
        igual("Apuesta 900 válida (igual al saldo)", null, pc.validarApuesta("900"));
        pc.iniciarRonda("100");
        igual("Nueva ronda descuenta sobre el saldo actualizado", 800, pc.getSaldo());
        igual("Nueva ronda: manos nuevas, jugador con 2 cartas", true,
                pc.getRonda().getManoJugador().tamano() >= 2);
        igual("Nueva ronda: computador sin cartas", 0, pc.getRonda().getManoComputador().tamano());

        // --- Saldo menor de 10: no se puede apostar
        Partida ps = new Partida();
        // Se pierde todo apostando 1000 y perdiendo 18 vs 19
        ps.iniciarRonda("1000", Mazo.conOrden(c(DIEZ, CORAZONES), c(OCHO, CORAZONES), c(DIEZ, PICAS), c(NUEVE, PICAS)));
        ps.plantarse();
        igual("Saldo 0 tras perder todo", 0, ps.getSaldo());
        igual("Sin saldo no hay para apostar", false, ps.haySaldoParaApostar());
        igual("Sin saldo no se puede iniciar", false, ps.puedeIniciar());
        String sinFichas = ps.iniciarRonda("10");
        igual("Sin saldo informa fichas insuficientes", true, sinFichas != null && sinFichas.contains("fichas suficientes"));

        System.out.println(fallos == 0 ? "\nTODAS LAS PRUEBAS CUMPLEN" : "\nFALLOS: " + fallos);
    }
}
