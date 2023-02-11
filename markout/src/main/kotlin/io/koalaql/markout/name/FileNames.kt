package io.koalaql.markout.name

fun tracked(name: String) = TrackedName(name)
fun untracked(name: String) = UntrackedName(name)