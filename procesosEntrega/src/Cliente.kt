import kotlin.random.Random
import kotlin.system.exitProcess

fun main() {
    val maxIntentosPorNivel = 5 // Número máximo de intentos por nivel
    var nivel = 1 // Nivel inicial
    var numeroSecreto: Int
    var intentosRealizados: Int

    println("¡Bienvenido al juego de adivinanza!")
    println("Intentarás adivinar un número secreto. Cada nivel tiene un límite de intentos.")

    do {
        numeroSecreto = Random.nextInt(1, 101) // Genera un número secreto entre 1 y 100
        intentosRealizados = 0
        var intento: Int? = null // Inicializa intento como nulo

        println("\n--- Nivel $nivel ---")
        println("He elegido un número entre 1 y 100. Tienes $maxIntentosPorNivel intentos para adivinarlo.")

        do {
            println("Introduce tu intento (1-100):")

            // Intenta leer la entrada y convertirla a un número
            val input = readLine()

            // Comprueba si la entrada es válida
            if (input.isNullOrBlank() || !input.all { it.isDigit() }) {
                println("Por favor, introduce un número válido entre 1 y 100.")
                continue // Si la entrada no es válida, vuelve a pedir
            }

            intento = input.toInt() // Convierte la entrada a un entero

            // Verifica si el número está en el rango permitido
            if (intento < 1 || intento > 100) {
                println("Por favor, introduce un número entre 1 y 100.")
                continue // Si el número no está en el rango, vuelve a pedir
            }

            intentosRealizados++ // Incrementa el contador de intentos

            // Comparación del intento con el número secreto
            when {
                intento < numeroSecreto -> println("El número secreto es mayor. Intenta de nuevo.")
                intento > numeroSecreto -> println("El número secreto es menor. Intenta de nuevo.")
                else -> {
                    println("¡Felicidades! Adivinaste el número secreto en $intentosRealizados intentos.")
                    nivel++ // Incrementa el nivel
                }
            }

            // Da pistas si el jugador no adivina y le quedan intentos
            if (intentosRealizados == maxIntentosPorNivel && intento != numeroSecreto) {
                val pista = if (numeroSecreto % 2 == 0) "El número secreto es par." else "El número secreto es impar."
                println("Pista: $pista")
            }

        } while (intentosRealizados < maxIntentosPorNivel && intento != numeroSecreto) // Continúa hasta que adivine o se acaben los intentos

        if (intentosRealizados == maxIntentosPorNivel && intento != numeroSecreto) {
            println("Se han agotado tus intentos. El número secreto era: $numeroSecreto")
        }

    } while (intentosRealizados < maxIntentosPorNivel)

    println("¡Gracias por jugar! Has completado $nivel niveles.")
    exitProcess(0) // Finaliza el programa
}
