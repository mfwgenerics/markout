---
sidebar_position: 1
---

# Project structure

Similar to a regular Kotlin application, Markout projects run through a `main` method.
This `main` method is called by Markout when it needs to generate or check files.

### Choosing a plugin

To start, you choose one of these three plugins based on how much functionality you require.

| Id                              | Functionality                                   | Link                                                                      |
| ------------------------------- | ----------------------------------------------- | ------------------------------------------------------------------------- |
| `io.koalaql.markout`            | Files and folders, generation and check tasks   | [Gradle](https://plugins.gradle.org/plugin/io.koalaql.markout)            |
| `io.koalaql.markout-markdown`   | Markdown generation and code capture            | [Gradle](https://plugins.gradle.org/plugin/io.koalaql.markout-markdown)   |
| `io.koalaql.markout-docusaurus` | Docusaurus site templating and Markdown support | [Gradle](https://plugins.gradle.org/plugin/io.koalaql.markout-docusaurus) |

For this example we will use the `io.koalaql.markout-markdown` plugin to generate some Markdown docs.

### Configure the plugin

Apply the Markout plugin in your buildscript and configure the main class.

```kotlin title="build.gradle.kts"
plugins {
    id("io.koalaql.markout-markdown") version "0.0.9"
}

markout {
    /* Main.kt file in the myproject package */
    mainClass = "myproject.MainKt"
}
```

Now define your `main` method

```kotlin
package myproject

import io.koalaql.markout.markout
import io.koalaql.markout.md.markdown

fun main() = markout {
    markdown("hello") {
        h1("Hello world")
    }
}
```
