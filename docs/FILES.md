# File Generation

Markout can run in one of two modes

1. [Apply Mode](#apply-mode)
2. [Expect Mode](#expect-mode)

## Apply Mode

Apply is the default mode. It generates files and directories and deletes
previously generated files and directories that were not regenerated.

### Files and Directories

Markout provides a straightforward DSL for generating files and directories

```kotlin
markout(Path("..")) {
    directory("my-directory") {
        directory("inner") {
            file("inner.txt", "another plain text file")
        }

        file("plain.txt", "the contents of a plain text file")

        file("circle.svg", """
            <svg height="100" width="100">
                <circle cx="50" cy="50" r="40" fill="black" />
            </svg> 
        """.trimIndent())
    }

    markdown("readme") {
        -"A markdown file"
        -"The .md extension is automatically added to the filename if it is not present"
    }
}
```

When this code is run it generates the following file tree

```
.markout
my-directory
├─ .markout
├─ inner
│  ├─ .markout
│  └─ inner.txt
├─ plain.txt
└─ circle.svg
readme.md
```

### File Tracking

When Markout generates directories it includes a `.markout` file. This is
how Markout keeps track of generated files. It should always be checked
into git. Markout will never change or delete an existing file or directory
unless it is tracked in `.markout`

File tracking allows regular files to be mixed in with generated ones.
For example, you might mix handwritten markdown into your docs directory.

## Expect Mode

Running Markout in expect mode will cause it to fail when it encounters changes.
This allows you to check that files have been generated and are consistent
with the code. It is intended for use in CI workflows.

To use Expect mode, run markout with the `MARKOUT_MODE` environment variable set to `expect`.

```shell
MARKOUT_MODE=expect ./gradlew :readme:run
```
