package com.openstore.app.json

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.float
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

fun JsonObject.getString(name: String): String = getValue(name).jsonPrimitive.content
fun JsonObject.optString(name: String): String? = get(name)?.jsonPrimitive?.content

fun JsonObject.getLong(name: String): Long = getValue(name).jsonPrimitive.long
fun JsonObject.optLong(name: String): Long? = get(name)?.jsonPrimitive?.long

fun JsonObject.getInt(name: String): Int = getValue(name).jsonPrimitive.int
fun JsonObject.optInt(name: String): Int? = get(name)?.jsonPrimitive?.int

fun JsonObject.getDouble(name: String): Double = getValue(name).jsonPrimitive.double
fun JsonObject.optDouble(name: String): Double? = get(name)?.jsonPrimitive?.double

fun JsonObject.getFloat(name: String): Float = getValue(name).jsonPrimitive.float
fun JsonObject.optFloat(name: String): Float? = get(name)?.jsonPrimitive?.float

fun JsonObject.getBoolean(name: String): Boolean = getValue(name).jsonPrimitive.boolean
fun JsonObject.optBoolean(name: String): Boolean? = get(name)?.jsonPrimitive?.boolean

val JsonElement.contentOrNull: String?
    get() = (this as? JsonPrimitive)?.contentOrNull

fun jsonArrayOf(vararg elements: JsonElement): JsonArray =
    JsonArray(elements.toList())

fun jsonObjectOf(vararg pairs: Pair<String, JsonElement>): JsonObject =
    JsonObject(pairs.toMap())

fun jsonObjectOfNotNull(vararg pairs: Pair<String, JsonElement>?): JsonObject =
    JsonObject(pairs.filterNotNull().toMap())
