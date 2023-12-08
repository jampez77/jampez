package com.example.jampez.data.api.wrappers

import okhttp3.Headers
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import java.lang.Exception

class ApiResponse<T>(call: Call<T>) {

    var errorMessage: String? = null
    var body: T? = null
    var code: Int = -1
    var success = code in 200..300
    var headers: Headers? = null

    init {
        try {
            call.execute().apply {
                code = code()
                body = body()
                errorMessage = parseError(errorBody())
                success = code in 200..300
                headers = headers()
            }
        } catch (ex: Exception) {
            errorMessage = ex.message
            body = null
            code = -1
            success = false
            headers = null
        }
    }

    private fun parseError(errorBody: ResponseBody?): String {
        if (errorBody != null) {
            try {
                val errorObject = JSONObject(errorBody.string())
                when {
                    errorObject.has("reason") -> {
                        return errorObject.getString("reason")
                    }
                    errorObject.has("message") -> {
                        return errorObject.getString("message")
                    }
                    errorObject.has("detail") -> {
                        return errorObject.getString("detail")
                    }
                }
            } catch (e: Exception) {
                return try {
                    errorBody.string()
                } catch (e: Exception) {
                    errorBody.toString()
                }
            }
        }
        return ""
    }
}