package dev.ycosorio.inventariomod6.data.remote

import retrofit2.http.GET

interface ApiService {

    @GET("products")
    suspend fun getAllProducts(): List<ProductResponse>
}