# Markout: Markdown DSL and File Generator

Markout is a library for generating files, directories and Markdown documentation from Kotlin.
It is designed for generating GitHub Flavored Markdown docs that live alongside code.
Using [Kapshot](https://github.com/mfwgenerics/kapshot) with this project
allows literate programming and "executable documentation" which ensures
that documentation remains correct and up to date.

1. [Intro](#intro)
2. [Getting Started](#getting-started)
3. [Usage](#usage)

## Intro

Markout provides a fully featured Markdown DSL to support documentation
generation and automation. It is flexible, mixes easily with raw markdown and
is intended to be built upon and used in conjunction with other tools.
The Markdown DSL can build strings or output directly to a file.

In addition to the Markdown DSL, Markout provides tools for managing
generated files and directories. Files and directories can be declared using
a DSL and then validated or synchronized. Snapshot testing can be performed on
generated files.

## Getting Started

Add the `markout` dependency

```kotlin
/* build.gradle.kts */
dependencies {
    implementation("io.koalaql:markout:0.0.6")
}
```

#### File Generation

If you want to use Markout as a documentation generator, call
the `markout` function directly from your main method. Pass a path
to the directory where you want Markout to generate files.
The path can be relative or absolute.

```kotlin
fun main() = markout(Path(".")) {
    markdown("hello") {
        p("This file was generated using markout")

        p {
            i("Hello ") + "World!"
        }
    }
}
```

Currently the Gradle application plugin is the best way to run a standalone Markout project

```shell
./gradlew :my-project:run
```

#### Markdown Strings

If you only want to use Markout to generate Markdown strings then you can use
`markdown` as a standalone function

```kotlin
markdown {
    h1("My Markdown")

    -"Text with some *italics*."
}
```

The above will produce the String

```markdown
# My Markdown

Text with some *italics*.
```

## Usage

1. [File Generation](docs/FILES.md)
2. [Markdown](docs/MARKDOWN.md)
