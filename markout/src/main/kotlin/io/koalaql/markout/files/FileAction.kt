package io.koalaql.markout.files

import io.koalaql.markout.Diff
import java.nio.file.Path

sealed interface FileAction {
    fun perform(path: Path): Diff?
    fun expect(path: Path): Diff?
}