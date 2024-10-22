/*Juego: "Adivina la dirección IP"
El programa ejecutará un comando del sistema (como arp -a para obtener las
direcciones IP y MAC de la red).
La salida se guardará en un archivo.
Se leerá ese archivo y el usuario tendrá que adivinar la dirección IP correcta
a partir de una serie de pistas que se le irán dando.*/

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)

    // 1. Obtener la IP base de la red local
    println("Introduce el rango de IPs de tu red (e.g., 192.168.1.0/24): ")
    val ipRange = scanner.nextLine()

    // 2. Realizar escaneo de dispositivos conectados a la red con nmap
    scanNetwork(ipRange)

    // 3. Leer la salida del archivo de nmap
    try {
        val result = Files.readAllLines(Paths.get("network_scan.txt"))
        println("Resultados del escaneo:")
        result.forEach { line ->
            println(line)
        }
    } catch (e: Exception) {
        println("Error al leer el archivo: ${e.message}")
    }

    // 4. Preguntar si el usuario quiere escanear puertos abiertos de un dispositivo
    println("Introduce la IP de un dispositivo para escanear sus puertos abiertos: ")
    val targetIP = scanner.nextLine()
    scanOpenPorts(targetIP)

    scanner.close()
}

fun scanNetwork(ipRange: String) {
    try {
        println("Escaneando la red en busca de dispositivos conectados...")
        val proceso = ProcessBuilder("nmap", "-sn", ipRange)
        proceso.redirectOutput(File("network_scan.txt"))
        val exitCode = proceso.start().waitFor()

        if (exitCode == 0) {
            println("Escaneo completado correctamente. Los resultados están en network_scan.txt")
        } else {
            println("Hubo un error al ejecutar el escaneo de red.")
        }
    } catch (e: Exception) {
        println("Error al realizar el escaneo de red: ${e.message}")
    }
}

fun scanOpenPorts(targetIP: String) {
    try {
        println("Escaneando puertos abiertos en $targetIP...")
        val proceso = ProcessBuilder("nmap", "-p-", targetIP)
        proceso.redirectOutput(File("port_scan.txt"))
        val exitCode = proceso.start().waitFor()

        if (exitCode == 0) {
            println("Escaneo de puertos completado. Los resultados están en port_scan.txt")
            val result = Files.readAllLines(Paths.get("port_scan.txt"))
            println("Puertos abiertos en $targetIP:")
            result.forEach { line ->
                println(line)
            }
        } else {
            println("Hubo un error al escanear los puertos del dispositivo.")
        }
    } catch (e: Exception) {
        println("Error al escanear los puertos: ${e.message}")
    }
}
