package com.example.newslapp.util
//sealed is kinda an abstract class but we can define which classes can inherit from it
//this is all about knowing where and why the error or other stuff happen

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()

}