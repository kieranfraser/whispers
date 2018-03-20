package eu.weareus.whispers.data

import com.google.gson.annotations.SerializedName

data class UserAgent(
        @SerializedName("user-agent") val userAgent: String
)
