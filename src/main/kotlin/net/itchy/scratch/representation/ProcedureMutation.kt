package net.itchy.scratch.representation

import com.google.gson.annotations.SerializedName
import net.itchy.scratch.representation.Mutation

data class ProcedureMutation(
    // Procedure specific members
    val procCode : String,
    val argumentIds : List<String>,
    val warp : Boolean,
    val argumentNames : List<String>
) : Mutation()