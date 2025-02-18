
import java.io.FileInputStream
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLServerSocket
import javax.net.ssl.SSLSocket
import javax.net.ssl.TrustManagerFactory


class ServidorSSL {
    fun iniciar() {
        val rutaAlmacen = "C:\\Users\\luis\\Desktop\\trabajoJesus\\funciones\\src\\almacen"

        // verificamos si el archivo del almacen existe
        val archivo = java.io.File(rutaAlmacen)
        if (!archivo.exists()) {
            println("ERROR: No se encontró el archivo de almacén en la ruta: $rutaAlmacen")
            return // si no existe, salimos de la funcion
        }

        // cargamos el almacen de claves con el archivo y la contraseña
        val almacen = KeyStore.getInstance("JKS")
        almacen.load(FileInputStream(rutaAlmacen), "1234567".toCharArray())

        // inicializamos el gestor de claves con el almacen y la contraseña
        val gestorClaves = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        gestorClaves.init(almacen, "1234567".toCharArray())

        // inicializamos el gestor de confianza q usa el mismo almacen
        val gestorConfianza = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        gestorConfianza.init(almacen)

        // creamos el contexto ssl para manejar conexiones seguras
        val contexto = SSLContext.getInstance("TLS")
        contexto.init(gestorClaves.keyManagers, gestorConfianza.trustManagers, null)

        // creamos el socket ssl del servidor
        val fabricaSocket = contexto.serverSocketFactory
        val servidorSSL = fabricaSocket.createServerSocket(6000) as SSLServerSocket

        println("servidor iniciado, esperando conexiones...")

        while (true) { // bucle infinito para aceptar clientes
            // aceptamos la conexion de un cliente
            val cliente = servidorSSL.accept() as SSLSocket
            println("cliente conectado: ${cliente.inetAddress.hostAddress}")

            // creamos un hilo pa manejar el cliente
            Thread(ManejadorCliente(cliente)).start()
        }
    }
}


fun main() {

    GestorUsuarios.registrarUsuario("usuario", "contraseña")

    // iniciamos el servidor
    ServidorSSL().iniciar()
}
