package serialization

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import util.VariantValue
import java.lang.reflect.Type

class VariantValueAdaptor : JsonSerializer<VariantValue>
{
    override fun serialize(src: VariantValue, typeOfSrc: Type, context: JsonSerializationContext): JsonElement
    {
        if (src.isDouble)
            return JsonPrimitive(src.dVal)
        else
            return JsonPrimitive(src.sVal)
    }
}