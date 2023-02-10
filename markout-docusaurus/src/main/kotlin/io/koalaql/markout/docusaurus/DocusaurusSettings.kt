package io.koalaql.markout.docusaurus

import io.koalaql.markout.MarkoutDsl
import io.koalaql.markout.text.LineWriter

@MarkoutDsl
interface DocusaurusLogo {
    @MarkoutDsl
    var alt: String
    @MarkoutDsl
    var src: String
}

@MarkoutDsl
interface DocusaurusSettings {
    @MarkoutDsl
    var title: String
    @MarkoutDsl
    var tagline: String
    @MarkoutDsl
    var url: String

    @MarkoutDsl
    var github: String

    @MarkoutDsl
    fun logo(block: DocusaurusLogo.() -> Unit)
}

fun buildConfigJs(out: LineWriter, builder: DocusaurusSettings.() -> Unit) {
    val settings = object : DocusaurusSettings {
        override var title: String = ""
        override var tagline: String = ""
        override var url: String = ""

        override var github: String = ""

        var logo: DocusaurusLogo? = null

        override fun logo(block: DocusaurusLogo.() -> Unit) {
            logo = object : DocusaurusLogo {
                override var alt: String = ""
                override var src: String = ""
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
            inline("logo: {")
            newline()

            alt.takeIf { it.isNotBlank() }?.let {
                inline("  alt: '${alt}',")
                newline()
            }

            inline("  src: '${src}'")

            newline()
            inline("},")
            newline()
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

    out.raw("""
        
          },
          footer: {
            style: 'dark',
            links: [
              {
                title: 'Docs',
                items: [
                  {
                    label: 'Tutorial',
                    to: '/docs/intro',
                  },
                ],
              },
              {
                title: 'Community',
                items: [
                  {
                    label: 'Stack Overflow',
                    href: 'https://stackoverflow.com/questions/tagged/docusaurus',
                  },
                  {
                    label: 'Discord',
                    href: 'https://discordapp.com/invite/docusaurus',
                  },
                  {
                    label: 'Twitter',
                    href: 'https://twitter.com/docusaurus',
                  },
                ],
              },
              {
                title: 'More',
                items: [
                  {
                    label: 'Blog',
                    to: '/blog',
                  },
                  {
                    label: 'GitHub',
                    href: 'https://github.com/facebook/docusaurus',
                  },
                ],
              },
            ],
            copyright: `Copyright © ${"$"}{new Date ().getFullYear()} My Project, Inc. Built with Docusaurus.`,
          },
          prism: {
            theme: lightCodeTheme,
            darkTheme: darkCodeTheme,
          },
        }),
    };
    module.exports = config;
    """.trimIndent())
}