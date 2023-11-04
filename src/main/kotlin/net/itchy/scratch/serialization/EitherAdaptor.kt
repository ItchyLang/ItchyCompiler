package net.itchy.scratch.serialization

import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import net.itchy.utils.Either
import java.lang.reflect.Type

class EitherAdaptor : JsonSerializer<Either<Any, Any>>
{
    override fun serialize(src: Either<Any, Any>, typeOfSrc: Type, context: JsonSerializationContext): JsonElement
    {
        return context.serialize(if (src.isLeft()) src.left() else src.right())
    }
}