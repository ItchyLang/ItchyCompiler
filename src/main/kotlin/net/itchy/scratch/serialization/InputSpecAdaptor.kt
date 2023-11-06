package net.itchy.scratch.serialization

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import net.itchy.scratch.representation.InputGenerator
import java.lang.reflect.Type

class InputSpecAdaptor : JsonSerializer<InputGenerator>
{
    override fun serialize(src: InputGenerator, typeOfSrc: Type, context: JsonSerializationContext): JsonElement
    {
        val ret = JsonArray()
        ret.add(src.inputType)
        ret.add(context.serialize(src.value))
        src.id?.let { ret.add(it) }

        return ret
    }
}