package com.example.jampez.utils

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallMock<T>(private val response: Response<T>) : Call<T> {

    companion object {
        @JvmStatic
        fun <T> mockSuccessResponse(body: T): CallMock<T> {
            return CallMock(Response.success(body))
        }

        @JvmStatic
        fun <T> mockErrorResponse(
            errorCode: Int,
            contentType: String,
            content: String
        ) : CallMock<T> {
            return CallMock(
                Response.error(
                    errorCode,
                    content.toResponseBody(contentType.toMediaTypeOrNull())
                )
            )
        }
    }
    override fun clone(): Call<T>  = this

    override fun execute(): Response<T>  = response

    override fun isExecuted(): Boolean  = false

    override fun cancel() {}

    override fun isCanceled(): Boolean  = false

    override fun request(): Request {
        return this.request()
    }

    override fun enqueue(callback: Callback<T>) {}

}