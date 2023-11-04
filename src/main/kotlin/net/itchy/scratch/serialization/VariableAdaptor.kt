package serialization

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import representation.Variable
import java.lang.reflect.Type

class VariableAdaptor : JsonSerializer<Variable>
{
    override fun serialize(src: Variable, typeOfSrc: Type, context: JsonSerializationContext): JsonElement
    {
        val ret = JsonArray()
        ret.add(src.name)
        ret.add(context.serialize(src.value))
        return ret
    }
}