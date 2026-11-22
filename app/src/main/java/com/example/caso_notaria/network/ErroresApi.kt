package com.example.caso_notaria.network

import com.google.gson.Gson
import com.google.gson.JsonParseException
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

//error con mensaje que se pueda mostrar tal cual al usuario
class ErrorConMensaje(mensaje: String) : Exception(mensaje)

// Ejecuta una llamada a la API dentro de una corrutina y devuelve Result:
// success con el dato o failure si no hubo internet, el servidor respondio con error o el JSON vino mal
suspend fun <T> llamarApi(llamada: suspend () -> T): Result<T> =
    try {
        Result.success(llamada())
    } catch (e: IOException) {
        Result.failure(e)
    } catch (e: HttpException) {
        Result.failure(e)
    } catch (e: JsonParseException) {
        Result.failure(e)
    } catch (e: ErrorConMensaje) {
        Result.failure(e)
    }

// Texto para mostrar en pantalla segun el tipo de error
fun mensajeDeError(error: Throwable): String = when {
    error is ErrorConMensaje -> error.message ?: "Revisa los datos e intenta de nuevo."
    error is IOException -> "Sin conexión. Revisa tu internet e intenta de nuevo."
    error is HttpException && error.code() == 401 -> "Tu sesión expiró. Cierra sesión y vuelve a entrar."
    else -> "No pudimos cargar los datos. Intenta de nuevo."
}

// Lee el message que Xano manda cuando responde con error (400, 403, 404)
fun Response<*>.mensajeDeXano(): String? {
    val cuerpo = errorBody()?.string() ?: return null
    return try {
        Gson().fromJson(cuerpo, ErrorResponse::class.java)?.message
    } catch (e: JsonParseException) {
        null
    }
}