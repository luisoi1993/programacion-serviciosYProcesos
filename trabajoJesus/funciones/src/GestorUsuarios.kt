
import java.security.MessageDigest
import java.util.*


object GestorUsuarios {

    // se crea un mapa mutable para simular una base de datos de usuarios, donde cada clave es un nombre de usuario
    // y cada valor es la contraseña cifrada
    private val usuarios = mutableMapOf<String, String>()


    fun registrarUsuario(nombreUsuario: String, contraseña: String) {
        // ciframos la contraseña utilizando el méttodo hashContraseña, que devuelve el hash de la contraseña
        val contraseñaHash = hashContraseña(contraseña)

        // almacenamos el nombre de usuario junto con su contraseña cifrada en el mapa 'usuarios'
        usuarios[nombreUsuario] = contraseñaHash
    }


    fun autenticarUsuario(nombreUsuario: String, contraseña: String): Boolean {
        // recuperamos el hash de la contraseña almacenada para el nombre de usuario proporcionado
        val contraseñaHash = usuarios[nombreUsuario]

        // comparamos el hash de la contraseña ingresada con el que tenemos almacenado para ese usuario
        return contraseñaHash == hashContraseña(contraseña)
    }

    // función privada que utiliza SHA-512 para cifrar la contraseña
    private fun hashContraseña(contraseña: String): String {
        // se crea un objeto MessageDigest para realizar el hash usando el algoritmo SHA-512
        val digest = MessageDigest.getInstance("SHA-512")

        // generamos el hash de la contraseña transformándola en un arreglo de bytes
        val hash = digest.digest(contraseña.toByteArray())

        // codificamos el hash en formato Base64, para que sea legible y fácil de almacenar
        return Base64.getEncoder().encodeToString(hash)
    }
}
