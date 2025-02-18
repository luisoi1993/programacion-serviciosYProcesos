
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

// objeto con funciones par trabajar con cifrado aes
object UtilesCripto {

    // funcion q genera una clave secreta aes de 128 bits
    fun generarClaveAES(): SecretKey {
        val generadorClave = KeyGenerator.getInstance("AES") // creamos el generador de claves
        generadorClave.init(128) // le decimos q sea de 128 bits
        return generadorClave.generateKey() // generamos la clave y la devolvemos
    }

    // funcion q cifra un texto con la clave aes
    fun cifrar(texto: String, clave: SecretKey): String {
        val cifrador = Cipher.getInstance("AES") // creamos el cifrador con aes
        cifrador.init(Cipher.ENCRYPT_MODE, clave) // lo inicializamos en modo cifrado
        val cifrado = cifrador.doFinal(texto.toByteArray()) // ciframos el texto
        return Base64.getEncoder().encodeToString(cifrado) // lo pasamos a base64 pa q sea legible
    }

    // funcion q descifra un texto en base64 con la clave aes
    fun descifrar(textoCifrado: String, clave: SecretKey): String {
        val cifrador = Cipher.getInstance("AES") // creamos el cifrador otra vez
        cifrador.init(Cipher.DECRYPT_MODE, clave) // lo ponemos en modo descifrado
        val descifrado = cifrador.doFinal(Base64.getDecoder().decode(textoCifrado)) // desciframos
        return String(descifrado) // lo convertimos a string pa q se pueda leer
    }
}
