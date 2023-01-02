package io.koalaql.markout.output

class OutputDirectory(
    val entries: Map<String, () -> Output>
): Output