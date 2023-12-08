package com.example.jampez.data.api.wrappers

class ResourceFactory<T> {

    val loading: Resource<T>
        get() = Resource(Status.LOADING, null, null, -1)

    fun getLoading(data: T): Resource<T> {
        return Resource(Status.LOADING, data, null, -1)
    }

    fun getResource(response: ApiResponse<T>): Resource<T> {
        return Resource(
            if (response.success) Status.SUCCESS else Status.ERROR,
            response.body,
            response.errorMessage,
            response.code
        )
    }

    fun getResource(response: ApiResponse<T>, data: T): Resource<T> {
        return Resource(
            if (response.success) Status.SUCCESS else Status.ERROR,
            data,
            response.errorMessage,
            response.code
        )
    }

    fun getResource(status: Status, body: T, errorMessage: String?, code: Int): Resource<T> {
        return Resource(status, body, errorMessage, code)
    }
}