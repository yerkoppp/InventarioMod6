package dev.ycosorio.inventariomod6.data.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse (
    val id: Int,
    @SerialName("title") val name: String,
    val price: Double,
    val description: String,
    val category: String,
    @SerialName("image") val imageUrl: String,
    val rating: RatingDto
)

@Serializable
data class RatingDto (
    val rate: Double,
    val count: Int
)