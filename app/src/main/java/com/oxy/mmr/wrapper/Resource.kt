package com.oxy.mmr.wrapper

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Failure(val msg: String) : Resource<Nothing>()
    object Loading : Resource<Nothing>() {
        // migrate to data object when kotlin 1.9
        override fun toString(): String {
            return "Loading"
        }
    }
}