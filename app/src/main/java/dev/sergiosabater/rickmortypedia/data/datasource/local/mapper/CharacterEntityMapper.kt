package dev.sergiosabater.rickmortypedia.data.datasource.local.mapper

import dev.sergiosabater.rickmortypedia.data.datasource.local.entity.CharacterEntity
import dev.sergiosabater.rickmortypedia.domain.model.Character
import dev.sergiosabater.rickmortypedia.domain.model.CharacterStatus
import javax.inject.Inject

class CharacterEntityMapper @Inject constructor() {

    fun toEntity(character: Character, page: Int = 1): CharacterEntity {
        return CharacterEntity(
            id = character.id,
            name = character.name,
            status = character.status.name,
            species = character.species,
            type = character.type,
            gender = character.gender,
            originName = character.origin,
            originUrl = character.originUrl,
            locationName = character.location,
            locationUrl = character.locationUrl,
            image = character.image,
            episodeUrls = character.episodeUrls.joinToString(separator = ","), // Serializing the list
            url = character.url,
            created = character.created,
            page = page
        )
    }

    fun toDomain(entity: CharacterEntity): Character {
        return Character(
            id = entity.id,
            name = entity.name,
            status = CharacterStatus.fromString(entity.status),
            species = entity.species,
            type = entity.type,
            gender = entity.gender,
            origin = entity.originName,
            location = entity.locationName,
            image = entity.image,
            episodeCount = entity.episodeUrls.split(",").size, // Calculate the count
            originUrl = entity.originUrl,
            locationUrl = entity.locationUrl,
            episodeUrls = entity.episodeUrls.split(",").filter { it.isNotBlank() },
            url = entity.url,
            created = entity.created
        )

    }
}