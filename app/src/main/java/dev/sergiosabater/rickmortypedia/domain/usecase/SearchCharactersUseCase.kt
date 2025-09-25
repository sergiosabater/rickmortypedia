package dev.sergiosabater.rickmortypedia.domain.usecase

import dev.sergiosabater.rickmortypedia.data.repository.CharacterRepository
import dev.sergiosabater.rickmortypedia.domain.model.Character
import javax.inject.Inject

class SearchCharactersUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(
        name: String? = null,
        status: String? = null,
        species: String? = null
    ): Result<List<Character>> {
        return repository.searchCharacters(name, status, species)
    }
}