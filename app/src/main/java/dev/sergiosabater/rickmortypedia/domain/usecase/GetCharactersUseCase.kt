package dev.sergiosabater.rickmortypedia.domain.usecase

import dev.sergiosabater.rickmortypedia.data.repository.CharacterRepository
import dev.sergiosabater.rickmortypedia.domain.model.Character
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(page: Int? = null): List<Character> {
        return repository.getCharacters(page)
    }
}