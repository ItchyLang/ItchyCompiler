package net.itchy.scratch.representation

import com.google.gson.annotations.SerializedName

data class StopMutation(
    // Stop specific members
    @SerializedName("hasnext")
    val hasNext : Boolean
) : Mutation()