package id.revan.beritaku.domain

sealed class Output<T> {
    data class Success<T>(val output: T) : Output<T>()
    data class Error<T>(val code: Int) : Output<T>()
}