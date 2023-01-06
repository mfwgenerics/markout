Markout is a library for generating markdown files and directories from Kotlin

# Example

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