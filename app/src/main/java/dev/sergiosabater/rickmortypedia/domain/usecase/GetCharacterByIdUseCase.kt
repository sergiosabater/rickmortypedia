package dev.sergiosabater.rickmortypedia.domain.usecase

import dev.sergiosabater.rickmortypedia.data.repository.CharacterRepository
import dev.sergiosabater.rickmortypedia.domain.model.Character
import javax.inject.Inject

class GetCharacterByIdUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(id: Int): Result<Character> {
        return repository.getCharacterById(id)
    }
}