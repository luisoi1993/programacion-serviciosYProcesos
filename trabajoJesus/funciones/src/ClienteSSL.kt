import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.FileInputStream
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import java.util.Scanner

// esta clase es el cliente q se conecta al servidor con ssl
class ClienteSSL {
    private val escaner = Scanner(System.`in`)

    fun iniciar() {
        var continuar = true

        while (continuar) {
            println("¿Desea registrarse (1), iniciar sesión (2) o salir (3)?")
            val opcion = escaner.nextInt()
            escaner.nextLine() // esto es pa q no se quede pillado el scanner

            if (opcion == 3) {
                println("Saliendo del programa...")
                break
            }

            println("Ingrese su nombre de usuario:")
            val nombreUsuario = escaner.nextLine()

            println("Ingrese su contraseña:")
            val contraseña = escaner.nextLine()

            // ruta donde esta el almacen de claves
            val rutaAlmacen = "C:\\Users\\luis\\Desktop\\trabajoJesus\\funciones\\src\\almacen"
            val archivo = java.io.File(rutaAlmacen)

            if (!archivo.exists()) { // si no encuentra el almacen da error
                println("ERROR: No se encontró el archivo de almacén en la ruta: $rutaAlmacen")
                continue
            }

            try {
                // cargar el almacen de claves
                val almacen = KeyStore.getInstance("JKS")
                almacen.load(FileInputStream(rutaAlmacen), "1234567".toCharArray())

                // gestor de claves q usa el almacen
                val gestorClaves = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
                gestorClaves.init(almacen, "1234567".toCharArray())

                // gestor de confianza pa verificar el servidor
                val gestorConfianza = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                gestorConfianza.init(almacen)

                // contexto SSL q usa TLS
                val contexto = SSLContext.getInstance("TLS")
                contexto.init(gestorClaves.keyManagers, gestorConfianza.trustManagers, null)

                val fabricaSSL = contexto.socketFactory // crea la fabrica de sockets seguros
                val cliente = fabricaSSL.createSocket("localhost", 6000) // conecta al servidor en el puerto 6000

                val salida = DataOutputStream(cliente.getOutputStream()) // pa mandar datos al servidor
                val entrada = DataInputStream(cliente.getInputStream()) // pa recibir datos del servidor

                // manda la opcion y las credenciales al servidor
                salida.writeInt(opcion)
                salida.writeUTF(nombreUsuario)
                salida.writeUTF(contraseña)

                val respuesta = entrada.readUTF() // recibe la respuesta del servidor
                println(respuesta) // la muestra en pantalla

                if (respuesta == "Autenticación exitosa") { // si el usuario y contraseña son correctos
                    var operacion: Int
                    do {
                        // menu de opciones
                        println("Seleccione una operación:")
                        println("1. Enviar mensaje cifrado")
                        println("2. Guardar información en fichero de texto")
                        println("3. Cerrar sesión")
                        operacion = escaner.nextInt()
                        escaner.nextLine()

                        when (operacion) {
                            1 -> { // enviar mensaje cifrado
                                println("Ingrese el mensaje a enviar:")
                                val mensaje = escaner.nextLine()

                                salida.writeUTF("ENVIAR_MENSAJE") // avisa al servidor
                                val clave = UtilesCripto.generarClaveAES() // genera clave pa cifrar
                                val mensajeCifrado = UtilesCripto.cifrar(mensaje, clave) // cifra el mensaje
                                salida.writeUTF(mensajeCifrado) // manda el mensaje cifrado
                                salida.write(clave.encoded) // manda la clave

                                val respuestaServidor = entrada.readUTF() // recibe la respuesta del servidor
                                println(respuestaServidor)
                            }
                            2 -> { // guardar información en un fichero
                                println("Ingrese la información que desea guardar:")
                                val informacion = escaner.nextLine()

                                salida.writeUTF("GUARDAR") // avisa al servidor
                                salida.writeUTF(informacion) // manda la info

                                val respuestaServidor = entrada.readUTF() // recibe la respuesta del servidor
                                println(respuestaServidor)
                            }
                            3 -> {
                                salida.writeUTF("CERRAR")  // avisa al servidor q cierra sesion
                                println("Cerrando sesión y volviendo al menú principal...")
                            }
                            else -> println("Opción no válida") // si mete un número q no vale
                        }
                    } while (operacion != 3) // repite el menú hasta q elige cerrar sesión
                }

                salida.close() // cierra el flujo de salida
                entrada.close() // cierra el flujo de entrada
                cliente.close() // cierra la conexión

            } catch (e: Exception) { // si algo falla muestra error
                println("Error en la comunicación: ${e.message}")
            }
        }
    }
}

fun main() {
    ClienteSSL().iniciar() // ejecuta el cliente
}
