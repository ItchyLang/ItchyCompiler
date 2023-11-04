package serialization

import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import util.Either
import java.lang.reflect.Type

class EitherAdaptor : JsonSerializer<Either<Any, Any>>
{
    override fun serialize(src: Either<Any, Any>, typeOfSrc: Type, context: JsonSerializationContext): JsonElement
    {
        if (src.isLeft())
            return context.serialize(src.left())
        else
            return context.serialize(src.right())
    }
}