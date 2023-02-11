package io.koalaql.markout.files

import java.nio.file.Path
import kotlin.io.path.deleteExisting
import kotlin.io.path.deleteIfExists
import kotlin.io.path.writeText

data class WriteMetadata(
    private val keys: Collection<String>
): FileAction {
    override fun perform(path: Path): Nothing? {
        if (keys.isEmpty()) {
            path.deleteIfExists()
        } else {
            path.writeText(keys.joinToString(
                separator = "\n",
                postfix = "\n"
            ))
        }

        return null
    }

    override fun expect(path: Path): Nothing? = null
}
