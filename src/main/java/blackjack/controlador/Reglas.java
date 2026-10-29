package blackjack.controlador;

/** Texto de la ventana «Ver reglas». Debe coincidir con lo implementado en el modelo (HU11.5). */
public final class Reglas {
    private Reglas() { }

    public static final String TEXTO = """
OBJETIVO
Obtener un puntaje mayor que el del computador sin superar 21. Se juega con un mazo de 52 cartas sin comodines, compartido por el jugador y el computador. Cada ronda empieza con el mazo completo y mezclado, y una carta no se reparte dos veces en la misma ronda.

VALOR DE LAS CARTAS
- Del 2 al 10: su valor numérico.
- J, Q y K: 10 puntos.
- As: 1 u 11 puntos. El programa elige el valor que da el mayor puntaje sin pasar de 21; si todas las combinaciones se pasan, todos los ases valen 1.
- El palo no cambia el puntaje.

APUESTAS Y SALDO
- Empiezas con 1.000 fichas ficticias. El computador es la banca y no tiene saldo.
- Antes de cada ronda apuestas un número entero, múltiplo de 10, de al menos 10 fichas y que no supere tu saldo.
- La apuesta se descuenta al comenzar la ronda y no se puede cambiar durante ella.
- Con menos de 10 fichas ya no puedes apostar.

PAGOS (la apuesta ya fue descontada al iniciar)
- Derrota: no se acredita nada.
- Empate: se devuelve la apuesta.
- Victoria ordinaria: se acredita el doble de la apuesta (devuelve la apuesta y ganas una cantidad igual).
- Victoria con Blackjack: se acredita 2,5 veces la apuesta (devuelve la apuesta y ganas 1,5 veces su valor).
Ejemplo con apuesta de 100 y saldo de 1.000: pierdes y quedas con 900; empatas y quedas con 1.000; ganas y quedas con 1.100; ganas con Blackjack y quedas con 1.150.

DESARROLLO DE LA RONDA
1. Recibes dos cartas y se muestra tu puntaje. El computador espera, sin cartas.
2. Puedes pedir cartas o plantarte.
3. Si llegas a 21, tu turno termina solo y juega el computador. Si superas 21, pierdes de inmediato y el computador no juega.
4. Cuando te plantas, el computador recibe dos cartas y juega solo.
5. Al final se determina el resultado y el saldo se actualiza una sola vez.

JUGADAS ESPECIALES
- Blackjack: un as y una carta de valor 10 (10, J, Q o K) en las dos cartas iniciales. Un 21 de tres o más cartas no es Blackjack. Supera a cualquier mano que no sea Blackjack.
- Veintiuno: 21 puntos sin cumplir la condición de Blackjack. Se compara con la mano del rival; no gana automáticamente frente a un Blackjack.
- Veinte y medio: las dos primeras cartas son una figura (J, Q o K) y un 4 (suman 14), sin importar el palo ni el orden. Se avisa con un mensaje y aún puedes pedir carta o plantarte (si pides carta, ya no cuenta como Veinte y medio). No da pago adicional. Al comparar, le gana a cualquier mano de 20 puntos o menos; pierde contra 21 y contra Blackjack. Si ambos lo tienen, hay empate.
- Pasarse: puntaje mayor que 21 después de ajustar los ases. Quien se pasa pierde.

QUIÉN GANA
- Si te pasas, pierdes. Si no te pasas y el computador se pasa, ganas.
- Si ambos tienen Blackjack, hay empate. Si solo uno lo tiene, gana ese participante.
- Sin Blackjack ni Veinte y medio, gana el mayor puntaje; con puntajes iguales hay empate.
- Si tienes Blackjack, solo se revisa si el computador también tiene Blackjack con sus dos cartas iniciales: si es así empatan; si no, ganas.
- Si no tienes Blackjack y el computador lo consigue con sus dos cartas iniciales, gana el computador aunque tengas 21.

JUEGO DEL COMPUTADOR
- Recibe dos cartas al comenzar su turno y reconoce un Blackjack inicial.
- Pide carta mientras su puntaje sea menor que 17.
- Se planta con cualquier puntaje entre 17 y 21 (incluido 17 con un as contado como 11).
- Pierde si supera 21.
- Sus decisiones dependen solo de su propia mano (si recibe J y 4, suma 14 y también pide carta).

MENSAJES Y CONTROLES
- Pedir carta y Plantarse solo funcionan durante tu turno.
- No puedes iniciar otra ronda ni cambiar la apuesta mientras la ronda esté en curso.
- Consultar las reglas no altera la partida.
- Cerrar el juego (botón o X de la ventana) pide confirmación; si cancelas, la partida sigue igual. La partida no se guarda al cerrar.
""";
}
