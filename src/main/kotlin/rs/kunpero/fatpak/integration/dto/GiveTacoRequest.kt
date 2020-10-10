package rs.kunpero.fatpak.integration.dto

class GiveTacoRequest (
    val token: String,
    val uid: String,
    val amount: Int,
    val message: String
) {
}