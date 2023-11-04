package serialization

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import representation.Input
import java.lang.reflect.Type

class InputAdaptor : JsonSerializer<Input>
{
    override fun serialize(src: Input, typeOfSrc: Type, context: JsonSerializationContext): JsonElement
    {
        var ret = JsonArray()
        ret.add(src.shadowState)
        ret.add(context.serialize(src.actualInput))
        src.obscuredShadow?.let { ret.add(context.serialize(it)) }

        return ret
    }
}