package com.example.caso_notaria.network

import com.example.caso_notaria.model.Evento
import com.example.caso_notaria.model.Inscripcion
import com.example.caso_notaria.model.Mensaje
import com.example.caso_notaria.model.Usuario
import com.google.gson.annotations.SerializedName

// Los campos que Xano puede mandar vacios son nullable y se completan al pasar al modelo

// envia
data class LoginRequest(val email: String, val password: String)

data class RegistroRequest(
    val name: String,
    val email: String,
    val password: String,
    val nivel: String,
    @SerializedName("juego_preferido") val juegoPreferido: String,
    val plataforma: String
)

data class EventoRequest(
    val nombre: String,
    val tipo: String,
    val juego: String,
    val formato: String,
    val plataforma: String,
    val fecha: String,
    val hora: String,
    val modalidad: String,
    val lugar: String,
    val latitud: Double?,
    val longitud: Double?,
    @SerializedName("foto_uri") val fotoUri: String?,
    val cupos: Int,
    val descripcion: String
)

data class MensajeRequest(val texto: String)

// recibe
data class TokenResponse(
    val authToken: String,
    @SerializedName("user_id") val usuarioId: Int
)

// Cuerpo de las respuestas con error
data class ErrorResponse(val message: String?)

data class UsuarioDto(
    val id: Int,
    val name: String?,
    val email: String?,
    val nivel: String?,
    @SerializedName("juego_preferido") val juegoPreferido: String?,
    val plataforma: String?
)

data class EventoDto(
    val id: Int,
    val nombre: String?,
    val tipo: String?,
    val juego: String?,
    val formato: String?,
    val plataforma: String?,
    val fecha: String?,
    val hora: String?,
    val modalidad: String?,
    val lugar: String?,
    val latitud: Double?,
    val longitud: Double?,
    @SerializedName("foto_uri") val fotoUri: String?,
    val cupos: Int,
    val descripcion: String?,
    @SerializedName("organizador_id") val organizadorId: Int,
    @SerializedName("organizador_alias") val organizadorAlias: String?
)

data class InscripcionDto(
    val id: Int,
    @SerializedName("evento_id") val eventoId: Int,
    @SerializedName("usuario_id") val usuarioId: Int
)

data class ParticipanteDto(
    @SerializedName("usuario_id") val usuarioId: Int,
    val alias: String?,
    val nivel: String?,
    @SerializedName("juego_preferido") val juegoPreferido: String?,
    val plataforma: String?
)

data class MensajeDto(
    val id: Int,
    @SerializedName("created_at") val creadoEn: Long,
    @SerializedName("evento_id") val eventoId: Int,
    @SerializedName("autor_id") val autorId: Int,
    val texto: String?,
    @SerializedName("autor_alias") val autorAlias: String?
)

// Conversion entre el JSON y los modelos de la app
fun UsuarioDto.aModelo() = Usuario(
    id = id,
    alias = name.orEmpty(),
    correo = email.orEmpty(),
    nivel = nivel.orEmpty(),
    juegoPreferido = juegoPreferido.orEmpty(),
    plataforma = plataforma.orEmpty()
)

fun ParticipanteDto.aModelo() = Usuario(
    id = usuarioId,
    alias = alias.orEmpty(),
    correo = "",
    nivel = nivel.orEmpty(),
    juegoPreferido = juegoPreferido.orEmpty(),
    plataforma = plataforma.orEmpty()
)

fun EventoDto.aModelo() = Evento(
    id = id,
    nombre = nombre.orEmpty(),
    tipo = tipo.orEmpty(),
    juego = juego.orEmpty(),
    formato = formato.orEmpty(),
    plataforma = plataforma.orEmpty(),
    fecha = fecha.orEmpty(),
    hora = hora.orEmpty(),
    modalidad = modalidad.orEmpty(),
    lugar = lugar.orEmpty(),
    latitud = latitud,
    longitud = longitud,
    fotoUri = fotoUri?.ifBlank { null },
    cupos = cupos,
    descripcion = descripcion.orEmpty(),
    organizadorId = organizadorId,
    organizadorAlias = organizadorAlias.orEmpty()
)

fun Evento.aRequest() = EventoRequest(
    nombre = nombre,
    tipo = tipo,
    juego = juego,
    formato = formato,
    plataforma = plataforma,
    fecha = fecha,
    hora = hora,
    modalidad = modalidad,
    lugar = lugar,
    latitud = latitud,
    longitud = longitud,
    fotoUri = fotoUri,
    cupos = cupos,
    descripcion = descripcion
)

fun InscripcionDto.aModelo() = Inscripcion(id = id, eventoId = eventoId, usuarioId = usuarioId)

fun MensajeDto.aModelo() = Mensaje(
    id = id,
    eventoId = eventoId,
    autorId = autorId,
    autorAlias = autorAlias,
    texto = texto.orEmpty(),
    hora = creadoEn
)