package com.example.manoslocales.models

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException

class ToStringAdapter : TypeAdapter<String>() {
    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: String?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value)
        }
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): String {
        val peek = `in`.peek()
        if (peek == JsonToken.NULL) {
            `in`.nextNull()
            // Devuelve un String vacío si el JSON es nulo, ya que el ID no puede ser nulo
            return ""
        }
        // Acepta números, booleanos o strings y los convierte a String
        return `in`.nextString()
    }
}