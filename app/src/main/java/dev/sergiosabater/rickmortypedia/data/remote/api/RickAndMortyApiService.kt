package dev.sergiosabater.rickmortypedia.data.remote.api

import dev.sergiosabater.rickmortypedia.data.remote.dto.CharacterDto
import dev.sergiosabater.rickmortypedia.data.remote.dto.CharactersResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickAndMortyApiService {

    @GET(CHARACTER_LIST)
    suspend fun getCharacters(
        @Query(QUERY_PAGE) page: Int? = null,
        @Query(QUERY_NAME) name: String? = null,
        @Query(QUERY_STATUS) status: String? = null,
        @Query(QUERY_SPECIES) species: String? = null,
        @Query(QUERY_TYPE) type: String? = null,
        @Query(QUERY_GENDER) gender: String? = null
    ): CharactersResponseDto

    @GET(CHARACTER_BY_ID)
    suspend fun getCharacterById(
        @Path("id") id: Int
    ): CharacterDto

}