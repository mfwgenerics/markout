package io.koalaql.markout.docusaurus

import io.koalaql.markout.md.Markdown

class DocusaurusMarkdownWrapper(
    private val markdown: Markdown
): DocusaurusMarkdown, Markdown by markdown