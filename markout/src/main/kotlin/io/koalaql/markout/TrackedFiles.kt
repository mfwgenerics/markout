package io.koalaql.markout

import io.koalaql.markout.files.*
import io.koalaql.markout.output.Output
import io.koalaql.markout.output.OutputDirectory
import io.koalaql.markout.output.OutputFile
import java.nio.file.Files
import java.nio.file.Path

class TrackedFiles {
    private val tracked = linkedSetOf<Path>()
    private val paths = linkedMapOf<Path, FileAction>()

    private fun track(dir: Path) {
        metadataPaths(dir).forEach { path ->
            if (Files.isDirectory(path)) {
                track(path)
            }

            tracked.add(path)
        }
    }

    private fun write(output: Output, path: Path) {
        when (output) {
            is OutputDirectory -> {
                paths[path] = DeclareDirectory(path in tracked)

                val entries = output.entries()

                /* write metadata first for graceful crash recovery */
                val metadataPath = path.resolve(METADATA_FILE_NAME)

                paths[metadataPath] = WriteMetadata(entries.keys)

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

    private fun trackAndWrite(dir: Path, output: Output) {
        track(dir)

        tracked.forEach { path ->
            paths[path] = DeleteFile
        }

        write(output, dir)
    }

    fun perform(dir: Path, output: Output) {
        trackAndWrite(dir, output)

        paths.forEach { (path, action) -> action.perform(path) }
    }

    fun expect(dir: Path, output: Output): List<Diff> {
        trackAndWrite(dir, output)

        val diffs = arrayListOf<Diff>()

        paths.forEach { (path, action) ->
            action.expect(path, diffs)
        }

        return diffs
    }
}