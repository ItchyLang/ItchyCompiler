package net.itchy.scratch.bundling

import com.google.gson.GsonBuilder
import net.itchy.scratch.representation.*
import net.itchy.scratch.serialization.*
import net.itchy.utils.Either
import net.itchy.utils.VariantValue
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import java.io.File

// Setup GSON
private val gson = GsonBuilder()
    .registerTypeAdapter(VariantValue::class.java, VariantValueAdaptor())
    .registerTypeAdapter(Either::class.java, EitherAdaptor())
    .registerTypeAdapter(InputSpec::class.java, InputSpecAdaptor())
    .registerTypeAdapter(Input::class.java, InputAdaptor())
    .registerTypeAdapter(Field::class.java, FieldAdaptor())
    .registerTypeAdapter(Variable::class.java, VariableAdaptor())
    .registerTypeAdapter(ScratchList::class.java, ScratchListAdaptor())
    .registerTypeAdapter(ProcedureMutation::class.java, ProcedureMutationAdaptor())
    .serializeNulls()
    .setPrettyPrinting()
    .create()

fun bundle(name: String, representation: ScratchProject, outPath: String) {
    ZipFile("$outPath${File.separator}$name.sb3").use { zip ->
        for (target in representation.targets) {
            for (costume in target.costumes) {
                val parameters = ZipParameters().apply {
                    fileNameInZip = costume.md5ext
                }
                zip.addStream(costume.content.inputStream(), parameters)
            }
        }

        val parameters = ZipParameters().apply {
            fileNameInZip = "project.json"
        }
        val json = gson.toJson(representation)

        zip.addStream(json.byteInputStream(), parameters)
        zip.close()
    }
}