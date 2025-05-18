package kuke.board.common

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import lombok.extern.slf4j.Slf4j
import mu.KotlinLogging
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

@Slf4j
object DataSerializer {
    private val log = KotlinLogging.logger {}

    private val objectMapper: ObjectMapper = ObjectMapper()
        .registerModule(JavaTimeModule())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun <T> deserialize(data: String, clazz: Class<T>): T? {
        return try {
            objectMapper.readValue(data, clazz)
        } catch (e: JsonProcessingException) {
            log.error("[DataSerializer.deserializer] data=$data clazz=$clazz", e)
            null
        }
    }

    fun <T> deserialize(data: Any, clazz: Class<T>): T? {
        return objectMapper.convertValue(data, clazz)
    }

    fun serialize(obj: Any): String? {
        return try {
            objectMapper.writeValueAsString(obj)
        } catch (e: JsonProcessingException) {
            log.error("[DataSerializer.serialize] obj=$obj", e)
            null
        }
    }
}