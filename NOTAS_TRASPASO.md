# Notas de traspaso – Persona 1 (modelo)

## Hecho (paquete `blackjack.modelo`)
- `Palo`, `Rango`, `Carta`, `Mazo` (arreglo de 52, barajado, sin repetir), `Mano` (lista enlazada), `Resultado` (enum), `Ronda`.
- `PruebasModelo`: cubre Un as, Varios ases, Jugadas, Prioridad, Blackjack doble, Jugador se pasa, Umbral rival, Comparación, Integridad. Todas cumplen.

## Cómo usarlo desde el controlador
- Por cada ronda: `Ronda ronda = new Ronda(); ronda.iniciar();` (mazo nuevo y mezclado; el saldo NO vive aquí).
- `ronda.pedirCarta()`, `ronda.plantarse()`: se ignoran si no es el turno del jugador.
- `ronda.esTurnoJugador()` -> habilitar/deshabilitar Pedir carta y Plantarse.
- `ronda.estaTerminada()`, `ronda.getResultado()` (VICTORIA, VICTORIA_BLACKJACK, EMPATE, DERROTA), `ronda.getMensajeResultado()`, `ronda.getMensajeEstado()`.
- Manos: `ronda.getManoJugador()` / `getManoComputador()` -> `getCartas()`, `calcularPuntaje()`, `describirJugada()`, `esBlackjack()`, `esVeinteYMedio()`, `sePaso()`.
- Imagen de carta: `carta.getNombreImagen()` (p. ej. `A_PICAS`).
- El computador juega solo dentro de `plantarse()`/`pedirCarta()` (al llegar a 21) o `iniciar()` (21 inicial). Si el jugador se pasa, su mano queda con 0 cartas.

## Decisiones a confirmar
- Veinte y medio = exactamente 2 cartas: figura (J/Q/K) + 4 (como dice el enunciado; la guía HU07 dice "suma 14").
- Con Blackjack del jugador o del computador, el computador NO pide cartas (solo se miran sus 2 iniciales).

## Pendiente
- UML final del modelo, matriz de pruebas (filas Prioridad, Ases, Jugadas, Rival), un registro de error, video y reflexión individuales.
