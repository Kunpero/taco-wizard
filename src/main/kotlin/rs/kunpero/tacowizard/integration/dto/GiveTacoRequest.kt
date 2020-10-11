package rs.kunpero.tacowizard.integration.dto

data class GiveTacoRequest (
    val token: String,
    val uid: String,
    val amount: Int,
    val message: String
) {
}