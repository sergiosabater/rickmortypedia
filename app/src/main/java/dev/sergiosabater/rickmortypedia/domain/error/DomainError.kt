package dev.sergiosabater.rickmortypedia.domain.error

sealed class DomainError(message: String) : Exception(message) {
    object NetworkError : DomainError("Connection error. Check your internet.")
    object ServerError : DomainError("Server error. Please try again later.")
    data class CharacterNotFound(val id: Int) : DomainError("Character with ID $id not found")
    object NoCharactersFound : DomainError("No characters were found with those criteria")
    data class UnknownError(val originalMessage: String?) :
        DomainError("Unexpected error: $originalMessage")

    object NoCachedData : DomainError("No data is stored locally")
    object SyncFailed : DomainError("Error synchronizing with the server")
    object DatabaseError : DomainError("Local database error")
}