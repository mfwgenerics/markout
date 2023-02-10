package io.koalaql.markout.docusaurus

import io.koalaql.markout.MarkoutDsl
import io.koalaql.markout.text.LineWriter

@MarkoutDsl
interface DocusaurusLogo {
    var alt: String
    var src: String
}

@MarkoutDsl
interface DocusaurusFooter {
    var copyright: String
}

@MarkoutDsl
interface DocusaurusSettings {
    var title: String
    var tagline: String
    var url: String

    var github: String

    @MarkoutDsl
    fun logo(block: DocusaurusLogo.() -> Unit)

    @MarkoutDsl
    fun footer(block: DocusaurusFooter.() -> Unit)
}

fun buildConfigJs(out: LineWriter, builder: DocusaurusSettings.() -> Unit) {
    val settings = object : DocusaurusSettings {
        override var title: String = ""
        override var tagline: String = ""
        override var url: String = ""

        override var github: String = ""

        var logo: DocusaurusLogo? = null
        var footer: DocusaurusFooter? = null

        override fun logo(block: DocusaurusLogo.() -> Unit) {
            logo = object : DocusaurusLogo {
                override var alt: String = ""
                override var src: String = ""
            }.apply(block)
        }

        override fun footer(block: DocusaurusFooter.() -> Unit) {
            footer = object : DocusaurusFooter {
                override var copyright: String = ""
            }.apply(block)
        }
    }.apply(builder)

    out.raw("""
        // @ts-check

        const lightCodeTheme = require('prism-react-renderer/themes/github');
        const darkCodeTheme = require('prism-react-renderer/themes/dracula');

        /** @type {import('@docusaurus/types').Config} */
        const config = {
    """.trimIndent())

    out.newline()

    with (out.prefixed("  ")) {
        inline("title: '${settings.title}',")
        newline()

        settings.tagline
            .takeIf { it.isNotBlank() }
            ?.let {
                inline("tagline: '$it',")
                newline()
            }

        settings.url
            .takeIf { it.isNotBlank() }
            ?.let {
                inline("url: '$it',")
                newline()
            }
    }

    out.prefixed("  ").raw("""
        baseUrl: '/',
        onBrokenLinks: 'throw',
        onBrokenMarkdownLinks: 'warn',
        favicon: 'img/favicon.ico',
        
        i18n: {
          defaultLocale: 'en',
          locales: ['en'],
        },
        
        presets: [
          [
            'classic',
            /** @type {import('@docusaurus/preset-classic').Options} */
            ({
              docs: {
                routeBasePath: '/',
                sidebarPath: require.resolve('./sidebars.js'),
                // Please change this to your repo.
                // Remove this to remove the "edit this page" links.
                editUrl:
                  'https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/',
              },
              blog: false
            }),
          ],
        ],
        
        
    """.trimIndent())

    out.raw("""
      themeConfig:
        /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
        ({
          navbar: {
            title: '${settings.title}',
      
    """.trimIndent())

    with (out.prefixed("        ")) {
        settings.logo?.apply {
            line("logo: {")

            alt.takeIf { it.isNotBlank() }?.let {
                line("  alt: '${alt}',")
            }

            line("  src: '${src}'")
            line("},")
        }

        settings.github.takeIf { it.isNotBlank() }?.let {
            raw("""
                items: [
                  {
                    href: '$it',
                    label: 'GitHub',
                    position: 'right',
                  },
                ],
            """.trimIndent())
        }
    }

    out.newline()
    out.line("      },")

    with (out.prefixed("      ")) {
        settings.footer?.apply {
            line("footer: {")
            line("  style: 'dark',")

            copyright.takeIf { it.isNotBlank() }?.let {
                line("  copyright: '$it',")
            }

            line("},")
        }
    }

    out.raw("""
          prism: {
            theme: lightCodeTheme,
            darkTheme: darkCodeTheme,
          },
        }),
    };
    module.exports = config;
    """.trimIndent())
}