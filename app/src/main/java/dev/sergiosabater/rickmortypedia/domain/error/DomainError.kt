package dev.sergiosabater.rickmortypedia.domain.error

sealed class DomainError(message: String) : Exception(message) {
    object NetworkError : DomainError("Error de conexión. Verifica tu internet.")
    object ServerError : DomainError("Error del servidor. Inténtalo más tarde.")
    data class CharacterNotFound(val id: Int) : DomainError("Personaje con ID $id no encontrado.")
    object NoCharactersFound : DomainError("No se encontraron personajes con esos criterios.")
    data class UnknownError(val originalMessage: String?) :
        DomainError("Error inesperado: $originalMessage")
}