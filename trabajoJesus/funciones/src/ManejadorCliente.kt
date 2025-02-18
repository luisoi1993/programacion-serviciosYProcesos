
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.FileWriter
import java.io.BufferedWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.net.ssl.SSLSocket
import javax.crypto.spec.SecretKeySpec


class ManejadorCliente(private val cliente: SSLSocket) : Runnable {
    // este es el metodo que se ejecuta cuando empieza el hilo
    override fun run() {
        try {
            // creamos los flujos para leer y escribir datos con el cliente
            val entrada = DataInputStream(cliente.inputStream)
            val salida = DataOutputStream(cliente.outputStream)

            // leemos la opcion que el cliente elige (registro o login)
            val opcion = entrada.readInt()
            val nombreUsuario = entrada.readUTF()
            val contraseña = entrada.readUTF()

            // si elige 1, es que quiere registrar un nuevo usuario
            when (opcion) {
                1 -> {
                    // registramos al usuario en el gestor de usuarios
                    GestorUsuarios.registrarUsuario(nombreUsuario, contraseña)
                    // le decimos al cliente que se registro correctamente
                    salida.writeUTF("Usuario registrado exitosamente")
                }
                2 -> {
                    // si elige 2, intenta hacer login
                    if (GestorUsuarios.autenticarUsuario(nombreUsuario, contraseña)) {
                        // si la autenticacion es correcta, le decimos que entro bien
                        salida.writeUTF("Autenticación exitosa")

                        var operacion: String
                        do {
                            // leemos la operacion que quiere hacer el cliente
                            operacion = entrada.readUTF()
                            when (operacion) {
                                "ENVIAR_MENSAJE" -> {
                                    // si quiere enviar un mensaje, leemos el mensaje cifrado y la clave
                                    val mensajeCifrado = entrada.readUTF()
                                    val claveBytes = ByteArray(16) // clave de tamaño AES-128
                                    entrada.readFully(claveBytes) // leemos la clave
                                    val clave = SecretKeySpec(claveBytes, "AES") // creamos la clave AES

                                    // desciframos el mensaje
                                    val mensajeDescifrado = UtilesCripto.descifrar(mensajeCifrado, clave)
                                    println("Mensaje recibido de $nombreUsuario: $mensajeDescifrado")

                                    // le decimos al cliente que todo0 fue bien
                                    salida.writeUTF("Mensaje recibido y descifrado correctamente")
                                }
                                "GUARDAR" -> {
                                    // si quiere guardar informacion, leemos la informacion
                                    val informacion = entrada.readUTF()

                                    // obtenemos la fecha y hora de ahora
                                    val fechaActual = LocalDateTime.now()
                                    val formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                    val fechaFormateada = fechaActual.format(formatoFecha)

                                    // creamos la linea con la informacion
                                    val linea = "[Usuario: $nombreUsuario, Fecha: $fechaFormateada] $informacion"

                                    // intentamos guardar la informacion en un archivo
                                    try {

                                        val rutaFichero = "C:\\Users\\luis\\Desktop\\trabajoJesus\\funciones\\src\\informacion.txt"
                                        val archivo = java.io.File(rutaFichero)

                                        // abrimos el archivo en modo append (para añadir al final)
                                        val escritor = BufferedWriter(FileWriter(archivo, true))
                                        escritor.write(linea) // escribimos la linea en el archivo
                                        escritor.newLine() // añadimos un salto de linea
                                        escritor.close() // cerramos el escritor

                                        // le decimos al cliente que todoo fue bien
                                        salida.writeUTF("Información guardada correctamente en el fichero.")
                                    } catch (e: Exception) {
                                        // si hubo un error al guardar, se lo decimos al cliente
                                        salida.writeUTF("Error al guardar la información en el fichero: ${e.message}")
                                    }
                                }
                                else -> {
                                    // si la operacion no es reconocida, le decimos que no es valida
                                    salida.writeUTF("Operación no válida")
                                }
                            }
                        } while (operacion != "CERRAR") // mientras la operacion no sea cerrar seguimos
                    } else {
                        // si no se autentica correctamente, le decimos que fallo
                        salida.writeUTF("Autenticación fallida")
                    }
                }
                else -> {
                    // si la opcion no es 1 ni 2, le decimos que la opcion no es valida
                    salida.writeUTF("Opción no válida")
                }
            }
        } catch (e: Exception) {
            // si pasa un error, mostramos el error
            e.printStackTrace()
        } finally {
            // al final siempre cerramos la conexion con el cliente
            cliente.close()
        }
    }
}
