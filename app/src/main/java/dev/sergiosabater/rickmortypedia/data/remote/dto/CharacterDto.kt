package dev.sergiosabater.rickmortypedia.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CharacterDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("species")
    val species: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("gender")
    val gender: String,

    @SerializedName("origin")
    val origin: CharacterLocationDto,

    @SerializedName("location")
    val location: CharacterLocationDto,

    @SerializedName("image")
    val image: String,

    @SerializedName("episode")
    val episode: List<String>,

    @SerializedName("url")
    val url: String,

    @SerializedName("created")
    val created: String
)

data class CharacterLocationDto(
    @SerializedName("name")
    val name: String,

    @SerializedName("url")
    val url: String
)

data class CharactersResponseDto(
    @SerializedName("info")
    val info: PageInfoDto,

    @SerializedName("results")
    val results: List<CharacterDto>
)

data class PageInfoDto(
    @SerializedName("count")
    val count: Int,

    @SerializedName("pages")
    val pages: Int,

    @SerializedName("next")
    val next: String?,

    @SerializedName("prev")
    val prev: String?
)