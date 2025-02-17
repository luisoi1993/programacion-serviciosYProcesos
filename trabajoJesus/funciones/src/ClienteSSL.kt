import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.FileInputStream
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import java.util.Scanner

class ClienteSSL {
    private val scanner = Scanner(System.`in`)

    fun iniciar() {
        var continuar = true

        while (continuar) {
            println("¿Desea registrarse (1), iniciar sesión (2) o salir (3)?")
            val option = scanner.nextInt()
            scanner.nextLine()  // Consumir el salto de línea

            if (option == 3) {
                println("Saliendo del programa...")
                break
            }

            println("Ingrese su nombre de usuario:")
            val username = scanner.nextLine()

            println("Ingrese su contraseña:")
            val password = scanner.nextLine()

            val rutaAlmacen = "C:\\Users\\luis\\Desktop\\trabajoJesus\\funciones\\src\\almacen"
            val file = java.io.File(rutaAlmacen)

            if (!file.exists()) {
                println("ERROR: No se encontró el archivo de almacén en la ruta: $rutaAlmacen")
                continue  // Volver al menú principal en lugar de detener el programa
            }

            try {
                val almacen = KeyStore.getInstance("JKS")
                almacen.load(FileInputStream(rutaAlmacen), "1234567".toCharArray())

                val manager = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
                manager.init(almacen, "1234567".toCharArray())

                val confianza = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                confianza.init(almacen)

                val contexto = SSLContext.getInstance("TLS")
                contexto.init(manager.keyManagers, confianza.trustManagers, null)

                val sslFabrica = contexto.socketFactory
                val cliente = sslFabrica.createSocket("localhost", 6000)

                val salida = DataOutputStream(cliente.getOutputStream())
                val entrada = DataInputStream(cliente.getInputStream())

                salida.writeInt(option)
                salida.writeUTF(username)
                salida.writeUTF(password)

                val respuesta = entrada.readUTF()
                println(respuesta)

                if (respuesta == "Autenticación exitosa") {
                    var operacion: Int
                    do {
                        println("Seleccione una operación:")
                        println("1. Enviar mensaje cifrado")
                        println("2. Cerrar sesión")
                        operacion = scanner.nextInt()
                        scanner.nextLine()  // Consumir el salto de línea

                        when (operacion) {
                            1 -> {
                                println("Ingrese el mensaje a enviar:")
                                val mensaje = scanner.nextLine()

                                val clave = CryptoUtils.generateAESKey()
                                val mensajeCifrado = CryptoUtils.encrypt(mensaje, clave)

                                salida.writeUTF(mensajeCifrado)
                                salida.write(clave.encoded)

                                val respuestaServidor = entrada.readUTF()
                                println(respuestaServidor)
                            }
                            2 -> println("Cerrando sesión y volviendo al menú principal...")
                            else -> println("Opción no válida, intente de nuevo.")
                        }
                    } while (operacion != 2)
                }

                salida.close()
                entrada.close()
                cliente.close()

            } catch (e: Exception) {
                println("Error en la comunicación con el servidor: ${e.message}")
            }
        }
    }
}


// Función principal (punto de entrada)
fun main() {
    val clienteSSL = ClienteSSL()
    clienteSSL.iniciar()  // Ejecutar el cliente
}