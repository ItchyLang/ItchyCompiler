package serialization

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import representation.InputSpec
import java.lang.reflect.Type

class InputSpecAdaptor : JsonSerializer<InputSpec>
{
    override fun serialize(src: InputSpec, typeOfSrc: Type, context: JsonSerializationContext): JsonElement
    {
        var ret = JsonArray()
        ret.add(src.inputType)
        ret.add(context.serialize(src.value))
        src.id?.let { ret.add(it) }

        return ret
    }
}