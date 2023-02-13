package workflows

import io.koalaql.markout.Markout
import io.koalaql.markout.workflow
import it.krzeminski.githubactions.actions.actions.CheckoutV3
import it.krzeminski.githubactions.actions.actions.SetupNodeV3
import it.krzeminski.githubactions.domain.Concurrency
import it.krzeminski.githubactions.domain.RunnerType
import it.krzeminski.githubactions.domain.triggers.Push

fun Markout.deployGhPagesYml() = file("pages.yml",
    """
    name: Deploy Docs
    on:
      push:
        branches: [main]
        paths: docusaurus/**
    permissions:
      contents: read
      pages: write
      id-token: write
    concurrency:
      group: "pages"
      cancel-in-progress: true
    jobs:
      deploy:
        environment:
          name: github-pages
          url: ${"$"}{{ steps.deployment.outputs.page_url }}
        runs-on: ubuntu-latest
        steps:
          - name: Checkout
            uses: actions/checkout@v3
          - name: Configure JDK
            uses: actions/setup-java@v3
            with:
              distribution: 'temurin'
              java-version: 19
          - name: Build Pages
            run: ./gradlew :readme:docusaurusBuild
          - name: Setup Pages
            uses: actions/configure-pages@v1
          - name: Upload artifact
            uses: actions/upload-pages-artifact@v1
            with:
              path: docusaurus/build
          - name: Deploy to GitHub Pages
            id: deployment
            uses: actions/deploy-pages@v1
    """.trimIndent()
)
