import java.io.* // Importa las clases para manejar entrada y salida de datos
import java.nio.file.Files // Importa clases para trabajar con archivos
import java.nio.file.Paths // Importa clases para manejar rutas de archivos

fun main() {
    try {
        // IP predeterminada para escaneo de red
        val ipRange = "192.168.59.0/24" // Cambia esto a la red deseada

        // 1. Realizar escaneo de dispositivos conectados a la red con nmap
        scanNetwork(ipRange)

        // 2. Leer la salida del archivo de nmap
        leerResultadosEscaneo("network_scan.txt", "Resultados del escaneo de red:")

        // 3. IP predeterminada del dispositivo para escanear puertos
        val targetIP = "192.168.59.1" // Cambia esto a la IP que desees escanear

        // Escaneo de puertos abiertos
        scanOpenPorts(targetIP)

        // 4. Leer la salida del archivo de escaneo de puertos
        leerResultadosEscaneo("port_scan.txt", "Puertos abiertos en $targetIP:")

        // 5. Simulación de interceptación de mensajes con procesos
        simulateGame()

    } catch (e: IOException) {
        // Maneja errores de entrada/salida
        println("[PADRE - ERROR] Ocurrió un error de entrada/salida: ${e.message}")
        e.printStackTrace() // Imprime el rastro del error
    } catch (e: Exception) {
        // Maneja errores inesperados
        println("[PADRE - ERROR] Ocurrió un error inesperado: ${e.message}")
        e.printStackTrace() // Imprime el rastro del error
    }
}

// Función para escanear la red
fun scanNetwork(ipRange: String) {
    try {
        println("Escaneando la red en busca de dispositivos conectados...")
        // Inicia el proceso de escaneo usando nmap en el rango de IP especificado
        val proceso = ProcessBuilder("nmap", "-sn", ipRange)
        proceso.redirectOutput(File("network_scan.txt")) // Redirige la salida a un archivo
        val exitCode = proceso.start().waitFor() // Espera a que el proceso termine y captura el código de salida

        if (exitCode == 0) {
            println("Escaneo completado correctamente. Los resultados están en network_scan.txt")
        } else {
            println("Error en el escaneo de red.")
        }
    } catch (e: Exception) {
        // Maneja cualquier error al realizar el escaneo de red
        println("Error al realizar el escaneo de red: ${e.message}")
    }
}

// Función para escanear puertos abiertos
fun scanOpenPorts(targetIP: String) {
    try {
        println("Escaneando puertos abiertos en $targetIP...")
        // Inicia el proceso de escaneo de puertos en la IP especificada
        val proceso = ProcessBuilder("nmap", "-p-", targetIP)
        proceso.redirectOutput(File("port_scan.txt")) // Redirige la salida a un archivo
        val exitCode = proceso.start().waitFor() // Espera a que el proceso termine y captura el código de salida

        if (exitCode == 0) {
            println("Escaneo de puertos completado. Los resultados están en port_scan.txt")
        } else {
            println("Error en el escaneo de puertos.")
        }
    } catch (e: Exception) {
        // Maneja cualquier error al escanear los puertos
        println("Error al escanear los puertos: ${e.message}")
    }
}

// Función para leer y mostrar resultados de escaneo
fun leerResultadosEscaneo(archivo: String, mensaje: String) {
    try {
        // Lee todas las líneas del archivo y las muestra en consola
        val result = Files.readAllLines(Paths.get(archivo))
        println(mensaje) // Muestra el mensaje proporcionado
        result.forEach { line -> println(line) } // Imprime cada línea del resultado
    } catch (e: Exception) {
        // Maneja errores al leer el archivo
        println("Error al leer el archivo $archivo: ${e.message}")
    }
}

fun simulateGame() {
    println("\nIniciando el juego entre Padre y Cliente...\n")

    val procesoCliente = ProcessBuilder(
        "C:\\Users\\luis\\.jdks\\openjdk-23\\bin\\java.exe",
        "-javaagent:C:\\Program Files\\JetBrains\\IntelliJ IDEA Community Edition 2024.2.3\\lib\\idea_rt.jar=37635:C:\\Program Files\\JetBrains\\JetBrains IntelliJ IDEA Community Edition 2024.2.3\\bin",
        "-Dfile.encoding=UTF-8",
        "-Dsun.stdout.encoding=UTF-8",
        "-Dsun.stderr.encoding=UTF-8",
        "-classpath", "C:\\Users\\luis\\Desktop\\procesosEntrega\\out\\production\\procesosEntrega;C:\\Users\\luis\\.m2\\repository\\org\\jetbrains\\kotlin\\kotlin-stdlib\\2.0.20\\kotlin-stdlib-2.0.20.jar;C:\\Users\\luis\\.m2\\repository\\org\\jetbrains\\annotations\\13.0\\annotations-13.0.jar",
        "ClienteKt" // Nombre de la clase que se ejecutará
    ).start() // Inicia el proceso del Cliente

    // Capturar la salida del cliente en un hilo separado
    Thread {
        capturarSalida(procesoCliente, "Cliente") // Captura la salida del cliente
    }.start()

    // Permite que el Padre envíe entradas al Cliente
    val outputWriter = BufferedWriter(OutputStreamWriter(procesoCliente.outputStream))
    val inputReader = BufferedReader(InputStreamReader(System.`in`))

    var linea: String?

    println("[PADRE] Escribe tu intento para enviarlo al Cliente:")

    while (procesoCliente.isAlive) {
        linea = inputReader.readLine() // Lee la entrada del usuario en el Padre

        if (linea != null) {
            outputWriter.write(linea) // Envía la entrada al proceso Cliente
            outputWriter.newLine() // Asegura que se envíe correctamente
            outputWriter.flush() // Vacía el buffer para asegurarse de que se transmite de inmediato
        }
    }

    // Esperar a que el proceso Cliente termine
    val exitCliente = procesoCliente.waitFor()
    println("[PADRE] El proceso Cliente terminó con código $exitCliente")
}

// Función para capturar y mostrar la salida de cada proceso
fun capturarSalida(proceso: Process, nombreProceso: String) {
    val input = BufferedReader(InputStreamReader(proceso.inputStream))
    var linea: String?

    while (input.readLine().also { linea = it } != null) {
        println("[$nombreProceso] $linea")
    }
}
