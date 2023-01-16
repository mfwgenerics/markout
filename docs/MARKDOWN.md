# Markdown

1. [Headers](#headers)
2. [Paragraphs](#paragraphs)
3. [Emphasis](#emphasis)
4. [Blockquotes](#blockquotes)
5. [Lists](#lists)
6. [Code](#code)
7. [Horizontal Rules](#horizontal-rules)
8. [Links](#links)
9. [Images](#images)
10. [Tables](#tables)
11. [Footnotes](#footnotes)

## Headers

### Kotlin

```kotlin
h1("Header 1")
h2("Header 2")
h3("Header 3")
h4("Header 4")
h5("Header 5")
h6("Header 6")
```

### Generated

```markdown
# Header 1

## Header 2

### Header 3

#### Header 4

##### Header 5

###### Header 6
```

### Rendered

> # Header 1
> 
> ## Header 2
> 
> ### Header 3
> 
> #### Header 4
> 
> ##### Header 5
> 
> ###### Header 6

## Paragraphs

### Kotlin

```kotlin
p {
    -"This text will appear in a paragraph."

    -"This sentence will be grouped with the preceding one."
}

p("""
    A second paragraph.
    Line breaks won't
    affect the rendered markdown
    and indent is trimmed
""")
```

### Generated

```markdown
This text will appear in a paragraph.
This sentence will be grouped with the preceding one.

A second paragraph.
Line breaks won't
affect the rendered markdown
and indent is trimmed
```

### Rendered

> This text will appear in a paragraph.
> This sentence will be grouped with the preceding one.
> 
> A second paragraph.
> Line breaks won't
> affect the rendered markdown
> and indent is trimmed

## Emphasis

### Kotlin

```kotlin
p {
    b("Bold")
    +", "
    i("Italics")
    +" and "
    b { i("Bold italics") }
    +"."
}

p {
    /* same as above, using `+` syntax sugar */
    b("Bold") + ", " + i("Italics") + " and " + b { i("Bold italics") } + "."
}

p {
    +"For inline text styling you can *still* use **raw markdown**"
}
```

### Generated

```markdown
**Bold**, *Italics* and ***Bold italics***.

**Bold**, *Italics* and ***Bold italics***.

For inline text styling you can *still* use **raw markdown**
```

### Rendered

> **Bold**, *Italics* and ***Bold italics***.
> 
> **Bold**, *Italics* and ***Bold italics***.
> 
> For inline text styling you can *still* use **raw markdown**

## Blockquotes

### Kotlin

```kotlin
+"I'm about to quote something"

quote {
    +"Here's the quote with a nested quote inside"

    quote {
        +"A final inner quote"
    }
}
```

### Generated

```markdown
I'm about to quote something

> Here's the quote with a nested quote inside
> 
> > A final inner quote
```

### Rendered

> I'm about to quote something
> 
> > Here's the quote with a nested quote inside
> > 
> > > A final inner quote

## Lists

### Kotlin

```kotlin
+"Dot points"

ul {
    li("Dot point 1")
    li("Another point")
    li("A third point")
}

+"Numbered"
ol {
    li("Item 1")
    li {
        p {
            +"You can nest any markdown inside list items"
        }

        p {
            +"Multiple paragraphs"
        }

        quote {
            +"Or even a quote"
        }
    }
    li {
        ol {
            li("This includes")
            li("Lists themselves")
        }
    }
}

+"Task lists"
cl {
    li(true, "Create a markdown DSL")
    li(true, "Add task list support")
    li(false, "Solve all of the world's problems")
}
```

### Generated

```markdown
Dot points

* Dot point 1
* Another point
* A third point

Numbered

1. Item 1
2. You can nest any markdown inside list items
   
   Multiple paragraphs
   
   > Or even a quote
3. 1. This includes
   2. Lists themselves

Task lists

- [x] Create a markdown DSL
- [x] Add task list support
- [ ] Solve all of the world's problems
```

### Rendered

> Dot points
> 
> * Dot point 1
> * Another point
> * A third point
> 
> Numbered
> 
> 1. Item 1
> 2. You can nest any markdown inside list items
>    
>    Multiple paragraphs
>    
>    > Or even a quote
> 3. 1. This includes
>    2. Lists themselves
> 
> Task lists
> 
> - [x] Create a markdown DSL
> - [x] Add task list support
> - [ ] Solve all of the world's problems

## Code

### Kotlin

```kotlin
c("Inline code block")

code("multiline\ncode\nblocks")

code("kotlin", """
    fun main() {
        println("Syntax hinted code!")
    }
""".trimIndent())

val result = code {
    /* this code block runs */
    fun square(x: Int) = x*x

    square(7)
}

+"Code executed with result: "
c("$result")
```

### Generated

````markdown
`Inline code block`

```
multiline
code
blocks
```

```kotlin
fun main() {
    println("Syntax hinted code!")
}
```

```kotlin
/* this code block runs */
fun square(x: Int) = x*x

square(7)
```

Code executed with result: `49`
````

### Rendered

> `Inline code block`
> 
> ```
> multiline
> code
> blocks
> ```
> 
> ```kotlin
> fun main() {
>     println("Syntax hinted code!")
> }
> ```
> 
> ```kotlin
> /* this code block runs */
> fun square(x: Int) = x*x
> 
> square(7)
> ```
> 
> Code executed with result: `49`

## Horizontal Rules

### Kotlin

```kotlin
t("Separated")
hr()
t("By")
hr()
t("Hrs")
```

### Generated

```markdown
Separated

---

By

---

Hrs
```

### Rendered

> Separated
> 
> ---
> 
> By
> 
> ---
> 
> Hrs

## Links

### Kotlin

```kotlin
p {
    +"Visit "
    a("https://example.com", "Example Website")
}

p {
    a("https://example.com") {
        +"Links "
        i("can contain")
        +" "
        b("inner formatting")
    }
}

p {
    a(cite("https://example.com"), "Reference style link")
}

p {
    +"Reference "
    a(cite("https://example.com"), "links")
    +" are de-duplicated"
}

p {
    a(cite("https://example.com", "Example"), "References")
    +" can be titled"
}
```

### Generated

```markdown
Visit [Example Website](https://example.com)

[Links *can contain* **inner formatting**](https://example.com)

[Reference style link][1]

Reference [links][1] are de-duplicated

[References][2] can be titled

[1]: https://example.com
[2]: https://example.com "Example"
```

### Rendered

> Visit [Example Website](https://example.com)
> 
> [Links *can contain* **inner formatting**](https://example.com)
> 
> [Reference style link][1]
> 
> Reference [links][1] are de-duplicated
> 
> [References][2] can be titled

## Images

### Kotlin

```kotlin
p {
    +"In inline contexts images will "
    img("markout.png")
    +" be shown inline "
    img("markout.png", "Alt text", "Title text is displayed on hover")
}

+"At top level images will be treated as blocks and vertically separated"
img("markout.png")
img("markout.png")
img("unknown.png", "Alt text is displayed when the image can't be displayed load")
```

### Generated

```markdown
In inline contexts images will ![](markout.png) be shown inline ![Alt text](markout.png "Title text is displayed on hover")

At top level images will be treated as blocks and vertically separated

![](markout.png)

![](markout.png)

![Alt text is displayed when the image can't be displayed load](unknown.png)
```

### Rendered

> In inline contexts images will ![](markout.png) be shown inline ![Alt text](markout.png "Title text is displayed on hover")
> 
> At top level images will be treated as blocks and vertically separated
> 
> ![](markout.png)
> 
> ![](markout.png)
> 
> ![Alt text is displayed when the image can't be displayed load](unknown.png)

## Tables

### Kotlin

```kotlin
table {
    th {
        td("Column 1")
        td { i("Italic Column") }
    }

    tr {
        td("1997")
        td("Non-italic")
    }

    tr {
        td("2023")
        td { i("Italic") }
    }
}
```

### Generated

```markdown
| Column 1 | *Italic Column* |
| -------- | --------------- |
| 1997     | Non-italic      |
| 2023     | *Italic*        |
```

### Rendered

> | Column 1 | *Italic Column* |
> | -------- | --------------- |
> | 1997     | Non-italic      |
> | 2023     | *Italic*        |

## Footnotes

### Kotlin

```kotlin
fun note() = footnote("""
    At the moment there is no way to re-use footnotes
    and the requirement for the note text to appear at
    the site of the footnote call is less than ideal
""")

+"The syntax is a work in progress" + note() + " but footnotes are possible."
```

### Generated

```markdown
The syntax is a work in progress[^1] but footnotes are possible.

[^1]: At the moment there is no way to re-use footnotes
      and the requirement for the note text to appear at
      the site of the footnote call is less than ideal
```

### Rendered

> The syntax is a work in progress[^1] but footnotes are possible.

[^1]: At the moment there is no way to re-use footnotes
      and the requirement for the note text to appear at
      the site of the footnote call is less than ideal

[1]: https://example.com
[2]: https://example.com "Example"