package com.dev_hss.pusherchat

import com.dev_hss.pusherchat.data.Message
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatService {
    @POST("/message")
    fun postMessage(@Body body: Message): Call<Void>

    companion object {
        private const val BASE_URL = "http://192.168.100.67:8080/"  //current pc's ip

        fun create(): ChatService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
            return retrofit.create(ChatService::class.java)
        }
    }
}