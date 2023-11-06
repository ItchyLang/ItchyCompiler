package net.itchy.scratch.representation

data class ProcedureMutation(
    // Procedure-specific members
    val procCode: String,
    val argumentIds: List<String>,
    val warp: Boolean,
    val argumentNames: List<String>
): Mutation()