# Markout

Markout is a library for generating markdown files and directories from Kotlin

1. [Use](#use)
2. [Files](#files)
3. [Markdown](#markdown)
4. [Example](#example)

## Use

```kotlin
dependencies {
    implementation("io.koalaql:markout:0.0.2")
}
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