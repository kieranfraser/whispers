package eu.weareus.whispers.network

import io.reactivex.Single
import eu.weareus.whispers.data.UserAgent
import retrofit2.http.GET

interface ApiService {

    @GET("user-agent")
    fun getUserAgent(): Single<UserAgent>

}
