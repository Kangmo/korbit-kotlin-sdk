package org.kangmo.helper

import com.google.gson.*
import java.lang.reflect.Type

/**
 * A BigDecimal serializer that encloses the value in quotes making it as string type.
 * This is necessary not to lose any precision for the decimal type to avoid conversion to floats or doubles.
 */
class BigDecimalSerializer : JsonSerializer<java.math.BigDecimal> {

  override fun serialize(src: java.math.BigDecimal?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
    if (src == null) throw IllegalArgumentException()

    val stringElement = JsonPrimitive(src.toPlainString())

    return stringElement
  }
}

/**
 * Created by kangmo on 01/12/2016.
 */
object JsonUtil {
  var gson : Gson? = null
  fun get() : Gson {
    if (gson == null) {
      gson = GsonBuilder()
        .registerTypeAdapter(java.math.BigDecimal::class.java, BigDecimalSerializer())
        .setPrettyPrinting()
        .serializeNulls()
        .create()
    }
    return gson!!
  }
  /*
  inline fun <reified T> toJsonArray(elements: List<T>): JsonArray {
    val jsonArray = JsonArray()

    elements.forEach { jsonElement ->
      val jsonTree = JsonUtil.get().toJsonTree(jsonElement)
      jsonArray.add(jsonTree)
    }
    return jsonArray
  }
  */
}
