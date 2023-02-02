package io.koalaql.markout.files

import io.koalaql.markout.Diff
import io.koalaql.markout.DiffType
import java.nio.file.Path

object AlreadyExistsError: FileAction {
    override fun perform(path: Path) {
        error("$path already exists")
    }

    override fun expect(path: Path): Diff {
        return Diff(DiffType.UNTRACKED, path)
    }
}