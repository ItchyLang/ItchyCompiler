package serialization

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import representation.Field
import java.lang.reflect.Type

class FieldAdaptor : JsonSerializer<Field>
{
    override fun serialize(src: Field, typeOfSrc: Type, context: JsonSerializationContext): JsonElement
    {
        var ret = JsonArray()
        ret.add(context.serialize(src.value))
        ret.add(src.id)
        return ret
    }
}