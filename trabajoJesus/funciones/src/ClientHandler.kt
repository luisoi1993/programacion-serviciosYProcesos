import java.io.DataInputStream
import java.io.DataOutputStream
import javax.net.ssl.SSLSocket
import javax.crypto.spec.SecretKeySpec

class ClientHandler(private val cliente: SSLSocket) : Runnable {
    override fun run() {
        try {
            val entrada = DataInputStream(cliente.inputStream)
            val salida = DataOutputStream(cliente.outputStream)

            // Leer opción (registro o login) y credenciales del cliente
            val option = entrada.readInt()
            val username = entrada.readUTF()
            val password = entrada.readUTF()

            when (option) {
                1 -> {
                    // Registrar usuario
                    UserManager.registerUser(username, password)
                    salida.writeUTF("Usuario registrado exitosamente")
                }
                2 -> {
                    // Autenticar usuario
                    if (UserManager.authenticateUser(username, password)) {
                        salida.writeUTF("Autenticación exitosa")

                        // Esperar a que el cliente envíe un mensaje cifrado
                        val mensajeCifrado = entrada.readUTF()
                        val claveBytes = ByteArray(16) // Tamaño de la clave AES-128
                        entrada.readFully(claveBytes)
                        val clave = SecretKeySpec(claveBytes, "AES")

                        // Descifrar el mensaje
                        val mensajeDescifrado = CryptoUtils.decrypt(mensajeCifrado, clave)
                        println("Mensaje recibido de $username: $mensajeDescifrado")

                        // Enviar confirmación al cliente
                        salida.writeUTF("Mensaje recibido y descifrado correctamente")
                    } else {
                        salida.writeUTF("Autenticación fallida")
                    }
                }
                else -> {
                    salida.writeUTF("Opción no válida")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cliente.close()
        }
    }
}