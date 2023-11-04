package representation

data class ProcedureMutation(
    // Procedure specific members
    val procCode : String,
    val argumentIds : ArrayList<String>,
    val warp : Boolean,
    val argumentNames : ArrayList<String>?
) : Mutation()