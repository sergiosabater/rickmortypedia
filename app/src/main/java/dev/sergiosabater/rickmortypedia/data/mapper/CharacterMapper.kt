package dev.sergiosabater.rickmortypedia.data.mapper

import dev.sergiosabater.rickmortypedia.data.remote.dto.CharacterDto
import dev.sergiosabater.rickmortypedia.domain.model.Character
import dev.sergiosabater.rickmortypedia.domain.model.CharacterStatus

object CharacterMapper {
    fun CharacterDto.toCharacter(): Character {
        return Character(
            id = id,
            name = name,
            status = CharacterStatus.fromString(status),
            species = species,
            type = type.ifEmpty { "Unknown" },
            gender = gender,
            origin = origin.name,
            location = location.name,
            image = image,
            episodeCount = episode.size
        )
    }
}