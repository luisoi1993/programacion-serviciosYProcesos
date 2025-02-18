import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.FileWriter
import java.io.BufferedWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.net.ssl.SSLSocket
import javax.crypto.spec.SecretKeySpec

// esta clase se encarga de manejar al cliente q se conecta al servidor ssl
class ManejadorCliente(private val cliente: SSLSocket) : Runnable {
    override fun run() {
        try {
            // se crean las entradas y salidas de datos par hablar con el cliente
            val entrada = DataInputStream(cliente.inputStream)
            val salida = DataOutputStream(cliente.outputStream)

            // se lee la opcion q mando el cliente y sus credenciales
            val opcion = entrada.readInt()
            val nombreUsuario = entrada.readUTF()
            val contraseña = entrada.readUTF()

            when (opcion) {
                1 -> { // si la opcion es 1, se registra el usuario
                    GestorUsuarios.registrarUsuario(nombreUsuario, contraseña)
                    salida.writeUTF("Usuario registrado exitosamente")
                }
                2 -> { // si la opcion es 2, intenta iniciar sesion
                    if (GestorUsuarios.autenticarUsuario(nombreUsuario, contraseña)) {
                        salida.writeUTF("Autenticación exitosa")
                        var operacion: String
                        do {
                            operacion = entrada.readUTF()
                            when (operacion) {
                                "ENVIAR_MENSAJE" -> { // si el cliente manda un mensaje
                                    val mensajeCifrado = entrada.readUTF()
                                    val claveBytes = ByteArray(16)
                                    entrada.readFully(claveBytes)
                                    val clave = SecretKeySpec(claveBytes, "AES")
                                    val mensajeDescifrado = UtilesCripto.descifrar(mensajeCifrado, clave)
                                    println("Mensaje recibido de $nombreUsuario: $mensajeDescifrado")
                                    salida.writeUTF("Mensaje recibido y descifrado correctamente")
                                }
                                "GUARDAR" -> { // si el cliente manda datos para guardar en un archivo
                                    val informacion = entrada.readUTF()
                                    val fechaActual = LocalDateTime.now()
                                    val formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                    val fechaFormateada = fechaActual.format(formatoFecha)
                                    val linea = "[Usuario: $nombreUsuario, Fecha: $fechaFormateada] $informacion"

                                    try {
                                        val rutaFichero = "C:\\Users\\luis\\Desktop\\trabajoJesus\\funciones\\src\\informacion.txt"
                                        val escritor = BufferedWriter(FileWriter(rutaFichero, true))
                                        escritor.write(linea)
                                        escritor.newLine()
                                        escritor.close()
                                        salida.writeUTF("Información guardada correctamente")
                                    } catch (e: Exception) {
                                        salida.writeUTF("Error al guardar: ${e.message}")
                                    }
                                }
                                "CERRAR" -> { // si el cliente quiere cerrar sesion
                                    println("$nombreUsuario cerró sesión")
                                    break
                                }
                                else -> salida.writeUTF("Operación no válida") // si el cliente mando algo q no se entiende
                            }
                        } while (true)
                    } else {
                        salida.writeUTF("Autenticación fallida") // si la contraseña no es correcta
                    }
                }
                else -> salida.writeUTF("Opción no válida") // si el usuario manda algo q no sea 1 ni 2
            }
        } catch (e: Exception) {
            e.printStackTrace() // si hay un error, lo muestra en la consola
        } finally {
            cliente.close() // se cierra la conexion con el cliente
            println("Conexión cerrada con ${cliente.inetAddress.hostAddress}")
        }
    }
}
