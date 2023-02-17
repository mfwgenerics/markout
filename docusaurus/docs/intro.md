---
sidebar_position: 1
slug: /
---

# Introduction

Markout is an executable documentation platform for Kotlin that
allows you to document Kotlin projects in code rather than text.
Documentation written this way can be tested and verified on every build.
Sample code becomes error proof, stays up-to-date and forms an
extra test suite for your project. Markout can serve as an alternative
to [kotlinx-knit](https://github.com/Kotlin/kotlinx-knit).

## Project purpose

Documenting code is a time-consuming and error-prone process.
Not only is handwritten sample code vulnerable to typos and syntax errors,
it silently goes out of date as projects evolve.
Results shown in documentation are also not guaranteed to match the
real behavior of the code. Markout seeks to address this by allowing
your docs to execute code from your project and embed the results.
Your generated documentation is checked into Git and used to perform
snapshot testing on future builds.

Another goal of this project is to make it easy for Kotlin developers to use the
[Docusaurus][1] static site generator to quickly build
and deploy documentation on GitHub pages. Markout can create, configure, install, build
and run Docusaurus projects without requiring Node.js to be installed. It integrates
with Gradle's [Continuous Build](https://docs.gradle.org/current/userguide/command_line_interface.html#sec:continuous_build)
to enable hot reloads and previews as you code.
Docusaurus support is optional and provided through a separate Gradle plugin.

Markout is designed to integrate with [Kapshot][2],
a minimal Kotlin compiler plugin that allows source code to be
captured and inspected at runtime.
Kapshot is the magic ingredient that enables fully executable and testable sample code blocks.

## How it works

Markout is designed around a core file generation layer that allows file trees to be declared in code.
These file trees are reconciled into a target directory, which is your project root directory by default.
Through extra plugins and libraries, the file generation layer can be extended with functionality for
generating markdown, capturing source code and building Docusaurus websites.

### File generation

Markout generates files by running code from Kotlin projects with the Markout Gradle plugin applied.
You supply a `main` method which invokes a `markout` block to describe how files should be generated.
This code runs every time files are generated or verified.

```kotlin title="Main.kt"
fun main() = markout {
    file("README.txt", "Hello world!")

    directory("docs") {
        file("INTRO.txt", "Another text file!")
        file("OUTRO.txt", "A final text file")
    }
}
```

When the code above is run using `:markout`, it generates the following file tree
and creates it in the project directory.

```
my-project
├─ docs
│  ├─ INTRO.txt
│  └─ OUTRO.txt
└─ README.txt
```

The `:markoutCheck` task then verifies that these files match subsequent runs of the code.

### Markdown DSL

Markout is extended with a DSL for generating markdown files procedurally.
The DSL can also be used to generate standalone Markdown strings.

````mdx-code-block
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

<Tabs>
<TabItem value='main.kt' label='Main.kt'>

```kotlin
val markoutVersion = "0.0.7"

markout {
    markdown("README.md") {
        h2("Readme")

        p("Here's some *generated* Markdown with a list")

        p("Using Markout version `$markoutVersion`")

        ol {
            li("One")
            li("Two")
        }
    }
}
```

</TabItem>
<TabItem value='readme.md' label='README.md'>

```markdown
## Readme

Here's some *generated* Markdown with a list

Using Markout version `0.0.7`

1. One
2. Two
```

</TabItem>
<TabItem value='rendered' label='Rendered'>

> ## Readme
> 
> Here's some *generated* Markdown with a list
> 
> Using Markout version `0.0.7`
> 
> 1. One
> 2. Two

</TabItem>
</Tabs>

````

### Source code capture

Source code capture works using the [Kapshot][2] plugin.
This allows you to execute your sample code blocks and use the results.

`````mdx-code-block
<Tabs>
<TabItem value='main.kt' label='Main.kt'>

```kotlin
fun <T> Markdown.execCodeBlock(block: CapturedBlock<T>): T {
    code("kotlin", block.source.text)

    return block()
}

markout {
    markdown("EXAMPLE.md") {
        val result = execCodeBlock {
            fun square(x: Int) = x*x

            square(7)
        }

        p("The code above results in: $result")
        p("If the result changes unexpectedly then `./gradlew check` will fail")
    }
}
```

</TabItem>
<TabItem value='example.md' label='EXAMPLE.md'>

````markdown
```kotlin
fun square(x: Int) = x*x

square(7)
```

The code above results in: 49

If the result changes unexpectedly then `./gradlew check` will fail
````

</TabItem>
<TabItem value='rendered' label='Rendered'>

> ```kotlin
> fun square(x: Int) = x*x
> 
> square(7)
> ```
> 
> The code above results in: 49
> 
> If the result changes unexpectedly then `./gradlew check` will fail

</TabItem>
</Tabs>

`````

### Docusaurus sites

The Docusaurus plugin provides a `docusaurus` builder and Gradle tasks for building and running a [Docusaurus][1] site.

````mdx-code-block
<Tabs>
<TabItem value='main.kt' label='Main.kt'>

```kotlin
markout {
    docusaurus("my-site") {
        configure {
            title = "Example Site"
        }

        docs {
            markdown("hello.md") {
                h1("Hello Docusaurus!")
            }
        }
    }
}
```

</TabItem>
<TabItem value='generated files' label='Generated Files'>

```
my-project
└─ my-site
   ├─ docs
   │  └─ hello.md
   ├─ static
   │  └─ .nojekyll
   ├─ .gitignore
   ├─ babel.config.js
   ├─ docusaurus.config.js
   ├─ linux.yarnrc
   ├─ package.json
   ├─ sidebars.js
   ├─ tsconfig.json
   └─ yarn.lock
```

</TabItem>
</Tabs>

````

[1]: https://docusaurus.io/
[2]: https://github.com/mfwgenerics/kapshot
