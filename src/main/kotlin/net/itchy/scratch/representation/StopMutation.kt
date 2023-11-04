package representation

data class StopMutation(
    // Stop specific members
    val hasNext : Boolean
) : Mutation()