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
to [kotlinx-knit](https://github.com/Kotlin/kotlinx-knit)

## Project Purpose

Documenting code is a time-consuming and error-prone process.
Handwritten sample code is vulnerable to typos and syntax errors
and it silently goes out of date as projects evolve.
Results shown in documentation are also not guaranteed to match the
real behavior of the code. Markout seeks to address this by allowing
your docs to execute code from your project and embed the results.
Your generated documentation is checked into Git and used to perform
snapshot testing on future builds.

Another goal of this project is to make it easy for Kotlin developers to use the
[Docusaurus](https://docusaurus.io/) static site generator to quickly build
and deploy documentation on GitHub pages. Markout can create, configure, install, build
and run Docusaurus projects without requiring Node.js to be installed. It integrates
with Gradle's [Continuous Build](https://docs.gradle.org/current/userguide/command_line_interface.html#sec:continuous_build)
to enable hot reloads and previews as you code.
Docusaurus support is optional and provided through a separate Gradle plugin.

Markout is designed to integrate with [Kapshot](https://github.com/mfwgenerics/kapshot),
a minimal Kotlin compiler plugin that allows source code to be
captured and inspected at runtime.
Kapshot is the magic ingredient that enables fully executable and testable sample code blocks.