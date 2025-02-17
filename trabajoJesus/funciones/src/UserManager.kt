import java.security.MessageDigest
import java.util.*

object UserManager {
    private val users = mutableMapOf<String, String>() // Simulación de base de datos de usuarios

    fun registerUser(username: String, password: String) {
        val hashedPassword = hashPassword(password)
        users[username] = hashedPassword
    }

    fun authenticateUser(username: String, password: String): Boolean {
        val hashedPassword = users[username]
        return hashedPassword == hashPassword(password)
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-512")
        val hash = digest.digest(password.toByteArray())
        return Base64.getEncoder().encodeToString(hash)
    }
}