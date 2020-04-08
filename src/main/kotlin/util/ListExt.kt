package util

object ListExt {
    fun <T> List<T>.without(elem: T) = this.filter { it != elem }
}