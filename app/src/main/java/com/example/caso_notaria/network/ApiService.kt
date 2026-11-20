package com.example.caso_notaria.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

// Grupos de endpoints en Xano.
private const val AUTH = "/api:tjA5KM8v"
private const val MYGOS = "/api:I-2YImHk"

// Endpoints de la API de Mygos en Xano. Los que piden sesion reciben el token en RetrofitClient
interface ApiService {

    // Autenticacion
    @POST("$AUTH/auth/signup")
    suspend fun registrar(@Body datos: RegistroRequest): Response<TokenResponse>

    @POST("$AUTH/auth/login")
    suspend fun iniciarSesion(@Body datos: LoginRequest): Response<TokenResponse>

    // Recibe el token aparte porque se llama justo al entrar, antes de guardarlo en la sesion
    @GET("$AUTH/auth/me")
    suspend fun obtenerUsuarioConectado(@Header("Authorization") autorizacion: String): UsuarioDto

    // Eventos
    @GET("$MYGOS/eventos")
    suspend fun obtenerEventos(): List<EventoDto>

    @GET("$MYGOS/eventos/{eventoId}")
    suspend fun obtenerEvento(@Path("eventoId") eventoId: Int): Response<EventoDto>

    @POST("$MYGOS/eventos")
    suspend fun publicarEvento(@Body evento: EventoRequest): EventoDto

    // Inscripciones
    @GET("$MYGOS/inscripciones")
    suspend fun obtenerInscripciones(): List<InscripcionDto>

    @POST("$MYGOS/eventos/{eventoId}/inscripciones")
    suspend fun inscribirse(@Path("eventoId") eventoId: Int): Response<InscripcionDto>

    @GET("$MYGOS/eventos/{eventoId}/participantes")
    suspend fun obtenerParticipantes(@Path("eventoId") eventoId: Int): List<ParticipanteDto>

    // Chat
    @GET("$MYGOS/eventos/{eventoId}/mensajes")
    suspend fun obtenerMensajes(@Path("eventoId") eventoId: Int): List<MensajeDto>

    @POST("$MYGOS/eventos/{eventoId}/mensajes")
    suspend fun enviarMensaje(@Path("eventoId") eventoId: Int, @Body mensaje: MensajeRequest): MensajeDto
}