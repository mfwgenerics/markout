package io.koalaql.markout

import io.koalaql.markout.files.FileAction
import java.nio.file.Path

class ActionableFiles(
    private val paths: Map<Path, FileAction>
) {
    fun perform(): List<Diff> {
        return paths.mapNotNull { (path, action) -> action.perform(path) }
    }

    fun expect(): List<Diff> {
        val visited = linkedMapOf<Path, Boolean>()

        val diffs = arrayListOf<Diff>()

        /* we don't check paths if their parents have already failed */
        fun visit(path: Path?): Boolean {
            if (path == null) return true /* handle null parent case */
            visited[path]?.let { return it }

            val parentExpected = visit(path.parent)

            if (!parentExpected) {
                visited[path] = false
                return false
            }

            val diff = paths[path]?.expect(path)

            if (diff != null) diffs.add(diff)

            val result = diff == null

            visited[path] = result
            return result
        }

        paths.keys.forEach { visit(it) }

        return diffs
    }
}