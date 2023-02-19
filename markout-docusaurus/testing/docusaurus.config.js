// @ts-check

const lightCodeTheme = require('prism-react-renderer/themes/github');
const darkCodeTheme = require('prism-react-renderer/themes/dracula');

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'Test Site',
  tagline: 'Test Tagline',
  url: 'http://localhost:3000',
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
        },
        blog: false
      }),
    ],
  ],
  
themeConfig:
  /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
  ({
    navbar: {
      title: 'Test Site',
        logo: {
          alt: 'My Logo',
          src: 'img/logo.svg'
        },
      items: [
        {
          href: 'https://github.com/mfwgenerics/markout',
          label: 'GitHub',
          position: 'right',
        },
      ],
    },
      footer: {
        style: 'dark',
        copyright: 'Copyright © 2023 My Project',
      },
    prism: {
      theme: lightCodeTheme,
      darkTheme: darkCodeTheme,
      additionalLanguages: ["kotlin", "java"],
    },
  }),
};
module.exports = config;