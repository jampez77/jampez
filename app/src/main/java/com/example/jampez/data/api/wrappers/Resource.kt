package com.example.jampez.data.api.wrappers

enum class Status{
    SUCCESS,
    ERROR,
    LOADING
}
class Resource<T> (
    val status: Status,
    val data: T?,
    val message: String?,
    val code: Int?
) {
    companion object {
        @JvmStatic
        fun <T> success(data: T?, code: Int?): Resource<T> =
            Resource(Status.SUCCESS, data, null, code)

        @JvmStatic
        fun <T> error(message: String?, data: T?, code: Int = -1): Resource<T> =
            Resource(Status.ERROR, data, message, code)

        fun <T> loading(data: T?): Resource<T> = Resource(Status.LOADING, data, null, -1)

        @JvmStatic
        fun <T> loading(): Resource<T> = loading(null)
    }
}