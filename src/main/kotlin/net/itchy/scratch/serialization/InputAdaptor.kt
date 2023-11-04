package net.itchy.scratch.serialization

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import net.itchy.scratch.representation.Input
import java.lang.reflect.Type

class InputAdaptor : JsonSerializer<Input>
{
    override fun serialize(src: Input, typeOfSrc: Type, context: JsonSerializationContext): JsonElement
    {
        val ret = JsonArray()
        ret.add(src.shadowState)
        ret.add(context.serialize(src.actualInput))
        src.obscuredShadow?.let { ret.add(context.serialize(it)) }

        return ret
    }
}