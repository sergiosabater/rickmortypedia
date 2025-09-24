package dev.sergiosabater.rickmortypedia.data.datasource.remote.mapper

import dev.sergiosabater.rickmortypedia.data.datasource.remote.dto.CharacterDto
import dev.sergiosabater.rickmortypedia.domain.model.Character
import dev.sergiosabater.rickmortypedia.domain.model.CharacterStatus
import javax.inject.Inject

class CharacterMapper @Inject constructor() {
    fun toCharacter(dto: CharacterDto): Character {
        return Character(
            id = dto.id,
            name = dto.name,
            status = CharacterStatus.fromString(dto.status),
            species = dto.species,
            type = dto.type.ifEmpty { "Unknown" },
            gender = dto.gender,
            origin = dto.origin.name,
            location = dto.location.name,
            image = dto.image,
            episodeCount = dto.episode.size,
            originUrl = dto.origin.url,
            locationUrl = dto.location.url,
            episodeUrls = dto.episode,
            url = dto.url,
            created = dto.created
        )
    }
}