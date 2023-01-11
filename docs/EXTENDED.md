# Extended Syntax

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
    the cite of the footnote call is less than ideal
""".trimIndent())

+"The syntax is a work in progress" + note() + " but footnotes are possible."
```

### Generated

```markdown
The syntax is a work in progress[^1] but footnotes are possible.

[^1]: At the moment there is no way to re-use footnotes
      and the requirement for the note text to appear at
      the cite of the footnote call is less than ideal
```

### Rendered

> The syntax is a work in progress[^1] but footnotes are possible.

[^1]: At the moment there is no way to re-use footnotes
      and the requirement for the note text to appear at
      the cite of the footnote call is less than ideal