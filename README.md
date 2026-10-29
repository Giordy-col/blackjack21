# 21 Black Jack – Duelo contra el computador

Mini proyecto 1 · 750014C FPOE · Universidad del Valle · Profesora Paola J. Rodríguez Carrillo

## Integrantes
|giordy santiago arcila enriquez | 2242797 | giordy.arcila@correounivalle.edu.co | giordy_col
| Santiago Muñoz bolaños 202539226 | santiago.munoz.bolanos@correounivalle.edu.co | santiagomunozbolanos-netizen

## Requisitos
- JDK 17 o superior
- Maven 3.8 o superior (descarga JavaFX 21 automáticamente; necesita internet la primera vez)

## Cómo ejecutar
```bash
mvn javafx:run
```

## Cómo correr las pruebas (sin JUnit, imprimen CUMPLE / FALLA)
```bash
mvn -q compile test-compile
java -cp target/classes:target/test-classes blackjack.modelo.PruebasModelo
java -cp target/classes:target/test-classes blackjack.modelo.PruebasBilletera
```
En Windows cambia `:` por `;` en el classpath.

## Estructura (patrón MVC)
```
src/main/java/blackjack/
  Main.java                          arranque; la X de la ventana pide confirmación
  modelo/   Carta, Rango, Palo, Mazo (arreglo de 52), Mano (lista enlazada),
            Ronda (turnos, computador, resultado), Resultado,
            Billetera (saldo, validación, pagos), Partida (une Billetera y Ronda)
  controlador/  JuegoController (eventos y actualización de la vista), Reglas (texto de «Ver reglas»)
src/main/resources/blackjack/
  vista/Juego.fxml                   pantalla
  imagenes/                          52 cartas (A_PICAS.png, 10_CORAZONES.png, ...) y REVERSO.png
src/test/java/blackjack/modelo/      PruebasModelo, PruebasBilletera
```
El modelo calcula puntajes, jugadas, resultado y pagos; el controlador solo llama al modelo y actualiza la vista.
