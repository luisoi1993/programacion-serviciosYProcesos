import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object CryptoUtils {
    fun generateAESKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(128)
        return keyGen.generateKey()
    }

    fun encrypt(text: String, key: SecretKey): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encrypted = cipher.doFinal(text.toByteArray())
        return Base64.getEncoder().encodeToString(encrypted)
    }

    fun decrypt(encryptedText: String, key: SecretKey): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, key)
        val decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedText))
        return String(decrypted)
    }
}