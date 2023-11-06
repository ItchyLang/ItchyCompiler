package net.itchy.scratch.serialization

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import net.itchy.scratch.representation.ProcedureMutation
import java.lang.reflect.Type

class ProcedureMutationAdaptor : JsonSerializer<ProcedureMutation>
{
    override fun serialize(src: ProcedureMutation, typeOfSrc: Type, context: JsonSerializationContext): JsonElement
    {
        var ret = JsonObject()
        ret.addProperty("tagName", src.tagName)
        ret.add("children", context.serialize(src.children))
        ret.addProperty("proccode", src.procCode)
        ret.addProperty("argumentids", src.argumentIds.joinToString(
            separator = ",",
            prefix = "[",
            postfix = "]"
        ) { "\"${it}\"" })
        ret.addProperty("argumentnames", src.argumentNames.joinToString(
            separator = ",",
            prefix = "[",
            postfix = "]"
        ) { "\"${it}\"" })
        ret.addProperty("argumentdefaults", src.argumentNames.joinToString(
            separator = ",",
            prefix = "[",
            postfix = "]"
        ) { "\"\"" })
        ret.addProperty("warp", src.warp.toString())
        return ret
    }
}