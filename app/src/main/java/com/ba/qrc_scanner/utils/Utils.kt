package com.ba.qrc_scanner.utils

import android.util.Base64
import com.ba.qrc_scanner.utils.exceptions.DecodeException
import com.ba.qrc_scanner.utils.exceptions.InvalidJsonObjectException
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import org.json.JSONException
import org.json.JSONObject

fun decodeBase64(base64Str: String): String {
    return try {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        String(decodedBytes, charset("UTF-8"))
    } catch (e: IllegalArgumentException) {
        throw DecodeException("Invalid Base64 input: ${e.message}")
    }
}


// JSON parsing with generic type and custom exception
inline fun <reified T> jsonStringToDataBean(json: String): T {
    return try {
        Gson().fromJson(json, T::class.java)
    } catch (e: JsonSyntaxException) {
        throw JsonParseException("JSON parsing failed: ${e.message}")
    }
}


fun toJsonObject(json: String): JSONObject {
    return try {
        JSONObject(json)
    } catch (e: JSONException) {
        throw InvalidJsonObjectException("Invalid JSON Object: ${e.message}")
    }
}