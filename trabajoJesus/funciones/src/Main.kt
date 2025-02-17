fun main() {
    // Registrar un usuario de prueba
    UserManager.registerUser("usuario", "contraseña")

    // Iniciar el servidor SSL
    ServidorSSL().main()
}