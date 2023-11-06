package net.itchy.scratch.serialization

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import net.itchy.scratch.representation.ScratchList
import java.lang.reflect.Type

class ScratchListAdaptor : JsonSerializer<ScratchList>
{
    override fun serialize(src: ScratchList, typeOfSrc: Type, context: JsonSerializationContext): JsonElement
    {
        val ret = JsonArray()
        ret.add(src.name)
        ret.add(context.serialize(src.value))
        return ret
    }
}