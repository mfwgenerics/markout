package io.koalaql.markout.name

sealed class FileName {
    abstract val name: String

    override fun toString(): String = name
}