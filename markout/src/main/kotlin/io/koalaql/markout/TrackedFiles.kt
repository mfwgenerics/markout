package io.koalaql.markout

import io.koalaql.markout.files.*
import io.koalaql.markout.output.Output
import io.koalaql.markout.output.OutputDirectory
import io.koalaql.markout.output.OutputFile
import java.nio.file.Files
import java.nio.file.Path

class TrackedFiles {
    private val paths = linkedMapOf<Path, FileAction>()

    fun track(dir: Path) {
        metadataPaths(dir).forEach { path ->
            if (Files.isDirectory(path)) {
                track(path)

                paths[path] = ForgetDirectory
            } else {
                paths[path] = DeleteFile
            }
        }
    }

    fun write(output: Output, path: Path) {
        when (output) {
            is OutputDirectory -> {
                paths[path] = DeclareDirectory(paths[path]?:NoAction)

                val entries = output.entries()

                /* write metadata first for graceful crash recovery */
                val metadataPath = path.resolve(METADATA_FILE_NAME)

                paths[metadataPath] = WriteToFile(true) { stream ->
                    stream.writer().use {
                        it.append(entries.keys.joinToString(
                            separator = "\n",
                            postfix = "\n"
                        ))
                    }
                }

                entries.forEach { (name, output) ->
                    write(output, path.resolve(name))
                }
            }
            is OutputFile -> {
                val tracked = paths.remove(path)

                paths[path] = WriteToFile(tracked != null, output)
            }
        }
    }

    fun perform() {
        paths.forEach { (path, action) -> action.perform(path) }
    }
}