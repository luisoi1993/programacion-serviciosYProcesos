import java.io.FileInputStream
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLServerSocket
import javax.net.ssl.SSLSocket
import javax.net.ssl.TrustManagerFactory

class ServidorSSL {
    fun main() {
        val rutaAlmacen = "C:\\Users\\luis\\Desktop\\trabajoJesus\\funciones\\src\\almacen"

        // Verificar si el archivo existe
        val file = java.io.File(rutaAlmacen)
        if (!file.exists()) {
            println("ERROR: No se encontró el archivo de almacén en la ruta: $rutaAlmacen")
            return
        }

        // Cargar el almacén de claves
        val almacen = KeyStore.getInstance("JKS")
        almacen.load(FileInputStream(rutaAlmacen), "1234567".toCharArray())

        // Inicializar el administrador de claves
        val manager = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        manager.init(almacen, "1234567".toCharArray())

        // Inicializar el administrador de confianza
        val confianza = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        confianza.init(almacen)

        // Crear el contexto SSL
        val contexto = SSLContext.getInstance("TLS")
        contexto.init(manager.keyManagers, confianza.trustManagers, null)

        // Crear el socket SSL del servidor
        val socketFabrica = contexto.serverSocketFactory
        val servidorSSL = socketFabrica.createServerSocket(6000) as SSLServerSocket

        println("Servidor iniciado, esperando conexiones...")

        while (true) {
            // Aceptar conexiones de clientes
            val cliente = servidorSSL.accept() as SSLSocket
            println("Cliente conectado: ${cliente.inetAddress.hostAddress}")

            // Manejar cada cliente en un hilo separado
            Thread(ClientHandler(cliente)).start()
        }
    }
}