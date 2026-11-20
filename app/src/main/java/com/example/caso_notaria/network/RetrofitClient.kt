package com.example.caso_notaria.network

import com.example.caso_notaria.repository.SesionManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Crea Retrofit una sola vez para toda la app
object RetrofitClient {

    private const val URL_BASE = "https://x8ki-letl-twmt.n7.xano.io/"

    @Volatile
    private var api: ApiService? = null

    fun getApi(sesion: SesionManager): ApiService {
        return api ?: synchronized(this) {
            val instancia = crearApi(URL_BASE) { sesion.token() }
            api = instancia
            instancia
        }
    }

    // Arma Retrofit con la direccion de Xano y el token de la sesion.
    // Si hay sesion, cada llamada lleva el encabezado "Authorization: Bearer <token>"
    fun crearApi(urlBase: String, token: () -> String?): ApiService {
        val cliente = OkHttpClient.Builder()
            .addInterceptor { cadena ->
                val original = cadena.request()
                val guardado = token()
                val pedido = if (guardado != null && original.header("Authorization") == null) {
                    original.newBuilder().header("Authorization", "Bearer $guardado").build()
                } else {
                    original
                }
                cadena.proceed(pedido)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(urlBase)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}