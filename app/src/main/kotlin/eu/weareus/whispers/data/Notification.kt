package eu.weareus.whispers.data

import com.google.gson.annotations.SerializedName

/**
 * Created by Kieran on 19/03/2018.
 */
data class Notification(

        @SerializedName("app") val app: String ?= null,
        @SerializedName("ticker") val ticker: String ?= null,
        @SerializedName("time") val time: String ?= null,
        @SerializedName("extra") val extra: String ?= null,
        @SerializedName("id") val id: String ?= null

)