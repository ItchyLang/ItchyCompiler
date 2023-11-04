package net.itchy.scratch.serialization

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import net.itchy.utils.VariantValue
import java.lang.reflect.Type

class VariantValueAdaptor : JsonSerializer<VariantValue>
{
    override fun serialize(src: VariantValue, typeOfSrc: Type, context: JsonSerializationContext): JsonElement
    {
        return if (src.isDouble) JsonPrimitive(src.dVal) else JsonPrimitive(src.sVal)
    }
}