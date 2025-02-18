import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.FileInputStream
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import java.util.Scanner

class ClienteSSL {
    private val escaner = Scanner(System.`in`)

    fun iniciar() {
        var continuar = true   // creamos una variable para seguir o no dentro del bucle.

        while (continuar) {   // bucle que se repite mientras "continuar" sea verdadero.
            println("¿Desea registrarse (1), iniciar sesión (2) o salir (3)?")
            val opcion = escaner.nextInt()
            escaner.nextLine()  // limpiamos el buffer para que no queden saltos de línea.

            if (opcion == 3) {
                println("Saliendo del programa...")
                break
            }

            println("Ingrese su nombre de usuario:")
            val nombreUsuario = escaner.nextLine()

            println("Ingrese su contraseña:")
            val contraseña = escaner.nextLine()

            val rutaAlmacen = "C:\\Users\\luis\\Desktop\\trabajoJesus\\funciones\\src\\almacen"
            val archivo = java.io.File(rutaAlmacen)  // creamos un objeto File para verificar la existencia del archivo.

            if (!archivo.exists()) {  // si no existe el archivo en la ruta indicada.
                println("ERROR: No se encontró el archivo de almacén en la ruta: $rutaAlmacen")
                continue  // volvemos al menú principal sin cerrar el programa.
            }

            try {
                val almacen = KeyStore.getInstance("JKS")   // creamos un KeyStore para manejar las claves.
                almacen.load(FileInputStream(rutaAlmacen), "1234567".toCharArray())  // cargamos el almacén con una contraseña.

                val gestorClaves = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())  // creamos un gestor de claves.
                gestorClaves.init(almacen, "1234567".toCharArray())   // inicializamos el gestor con la clave.

                val gestorConfianza = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())   // creamos un gestor de confianza.
                gestorConfianza.init(almacen)   // inicializamos el gestor de confianza.

                val contexto = SSLContext.getInstance("TLS")   // creamos un contexto SSL para comunicación segura.
                contexto.init(gestorClaves.keyManagers, gestorConfianza.trustManagers, null)   // inicializamos el contexto con los gestores.

                val fabricaSSL = contexto.socketFactory   // creamos una fábrica de sockets SSL.
                val cliente = fabricaSSL.createSocket("localhost", 6000)  // creamos un socket SSL conectado al servidor en localhost, puerto 6000.

                val salida = DataOutputStream(cliente.getOutputStream())   // creamos un flujo de salida para enviar datos al servidor.
                val entrada = DataInputStream(cliente.getInputStream())   // creamos un flujo de entrada para recibir datos del servidor.

                salida.writeInt(opcion)   // enviamos la opción seleccionada por el usuario.
                salida.writeUTF(nombreUsuario)  // enviamos el nombre de usuario.
                salida.writeUTF(contraseña)  // enviamos la contraseña.

                val respuesta = entrada.readUTF()   // leemos la respuesta del servidor.
                println(respuesta)   // mostramos la respuesta.

                if (respuesta == "Autenticación exitosa") {   // si la autenticación fue exitosa.
                    var operacion: Int
                    do {
                        println("Seleccione una operación:")
                        println("1. Enviar mensaje cifrado")
                        println("2. Guardar información en fichero de texto")
                        println("3. Cerrar sesión")
                        operacion = escaner.nextInt()
                        escaner.nextLine()  // limpiamos el buffer.

                        when (operacion) {
                            1 -> {
                                println("Ingrese el mensaje a enviar:")
                                val mensaje = escaner.nextLine()

                                salida.writeUTF("ENVIAR_MENSAJE")   // enviamos la operación "ENVIAR_MENSAJE".

                                val clave = UtilesCripto.generarClaveAES()  // generamos una clave AES.
                                val mensajeCifrado = UtilesCripto.cifrar(mensaje, clave)  // ciframos el mensaje.

                                salida.writeUTF(mensajeCifrado)  // enviamos el mensaje cifrado.
                                salida.write(clave.encoded)   // enviamos la clave.

                                val respuestaServidor = entrada.readUTF()   // leemos la respuesta del servidor.
                                println(respuestaServidor)   // mostramos la respuesta.
                            }
                            2 -> {
                                println("Ingrese la información que desea guardar en el fichero de texto:")
                                val informacion = escaner.nextLine()

                                salida.writeUTF("GUARDAR")   // enviamos la operación "GUARDAR".

                                salida.writeUTF(informacion)   // enviamos la información a guardar.

                                val respuestaServidor = entrada.readUTF()   // leemos la respuesta del servidor.
                                println(respuestaServidor)
                            }
                            3 -> println("Cerrando sesión y volviendo al menú principal...")
                            else -> println("Opción no válida, intente de nuevo.")  // si la opción no es válida, mostramos un mensaje de error.
                        }
                    } while (operacion != 3)
                }

                salida.close()  // cerramos el flujo de salida.
                entrada.close()  // cerramos el flujo de entrada.
                cliente.close()  // cerramos el socket.

            } catch (e: Exception) {   // si hay un error en el bloque try.
                println("Error en la comunicación con el servidor: ${e.message}")
            }
        }
    }
}


fun main() {
    val clienteSSL = ClienteSSL()   // creamos un objeto ClienteSSL.
    clienteSSL.iniciar()  // ejecutamos el cliente.
}
