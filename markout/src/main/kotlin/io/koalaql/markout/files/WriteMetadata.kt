package io.koalaql.markout.files

import io.koalaql.markout.Diff
import java.nio.file.Path
import kotlin.io.path.writeText

data class WriteMetadata(
    private val keys: Collection<String>
): FileAction {
    override fun perform(path: Path) {
        path.writeText(keys.joinToString(
            separator = "\n",
            postfix = "\n"
        ))
    }

    override fun expect(path: Path): Diff? = null
}
