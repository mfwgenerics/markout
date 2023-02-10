package io.koalaql.markout.docusaurus

import io.koalaql.markout.text.LineWriter

interface DocusaurusSettings {
    var title: String
    var tagline: String
    var url: String
}

fun buildConfigJs(out: LineWriter, builder: DocusaurusSettings.() -> Unit) {
    val settings = object : DocusaurusSettings {
        override var title: String = ""
        override var tagline: String = ""
        override var url: String = ""
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
        settings.title
            .takeIf { it.isNotBlank() }
            ?.let {
                inline("title: '$it',")
                newline()
            }

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

    out.raw("""
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
            title: 'My Site',
            logo: {
              alt: 'My Site Logo',
              src: 'img/logo.svg',
            },
            items: [
              {
                type: 'doc',
                docId: 'intro',
                position: 'left',
                label: 'Tutorial',
              },
              {to: '/blog', label: 'Blog', position: 'left'},
              {
                href: 'https://github.com/facebook/docusaurus',
                label: 'GitHub',
                position: 'right',
              },
            ],
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