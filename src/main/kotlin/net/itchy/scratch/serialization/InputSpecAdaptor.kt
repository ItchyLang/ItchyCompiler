package net.itchy.scratch.serialization

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import net.itchy.scratch.representation.InputSpec
import java.lang.reflect.Type

class InputSpecAdaptor : JsonSerializer<InputSpec>
{
    override fun serialize(src: InputSpec, typeOfSrc: Type, context: JsonSerializationContext): JsonElement
    {
        val ret = JsonArray()
        ret.add(src.inputType)
        ret.add(context.serialize(src.value))
        src.id?.let { ret.add(it) }

        return ret
    }
}