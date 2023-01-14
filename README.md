# Markout

Markout is a library for generating files, directories and Markdown documentation from Kotlin.
It is designed for generating GitHub Flavored Markdown docs that live alongside code.
Using [Kapshot](https://github.com/mfwgenerics/kapshot) with this project
allows literate programming and "executable documentation", enabling developers
to ensure that documentation remains correct and up to date.

1. [Getting Started](#getting-started)
2. [Files](#files)
3. [Markdown](#markdown)
4. [Example](#example)

## Getting Started

Add the markout dependency

```kotlin
/* build.gradle.kts */
dependencies {
    implementation("io.koalaql:markout:0.0.3")
}
```

### File Generation

If you want to use markout as a documentation generator, call
the `markout` function directly from your main method. Pass a path
to the directory where you want markout to generate files.
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

Currently the Gradle application plugin is the best way to run a standalone markout project

```shell
./gradlew :my-project:run
```

### Markdown Strings

If you only want to use markout to generate Markdown strings then you can use
`markdownString`

```kotlin
markdownString {
    h1("My Markdown")

    -"Text with some "+i("italics")+"."
}
```

The above will produce the String

```markdown
# My Markdown

Text with some *italics*.
```

## Files

## Markdown

1. [Basic Syntax](docs/BASIC.md)
2. [Extended Syntax](docs/EXTENDED.md)

## Example

```kotlin
h1 { t("Hello "); b("Markout!") }

p("Example paragraph")

ol {
    li("List")
    li("Of")
    li("Items")
}
```

Will produce the following markdown

```md
# Hello **Markout!**

Example paragraph

1. List
2. Of
3. Items
```