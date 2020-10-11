package rs.kunpero.tacowizard.integration.dto

class GiveTacoRequest (
    val token: String,
    val uid: String,
    val amount: Int,
    val message: String
) {
}