# The Jahia Nextjs initiative : *nextjs-proxy*

## Initiative overview
The aim of the Jahia Nextjs initiative is to explore and explain
the Jahia capabilities, to easily create and manage headless web project.
Solutions we use are :
- [Jahia][jahia-website] : a Cloud / On-premise *DXP* solution to create and contribute
- [Vercel][vercel-website] a next-js Cloud platform provider to render the web project

To know more about the Jahia Nextjs initiative [read this dedicated page][initiative.md].

## Nextjs-proxy Overview
This module is one of the four components of the Jahia Nextjs initiative. It is a servlet proxy used by Jahia
to render Nextjs application inside Jahia. That means you can edit, compose and preview website rendered with
the Nextjs framework. This module is used in conjunction with the [headless-templatesSet] module.

<img src="/doc/images/204_provision.png" width="800px"/>

The Nextjs proxy module provides :
- an [HTTP proxy][proxy] to redirect edit and preview rendering.
- a [mixin][definition] to configure paths and token to interact with your Nextjs app.
- [mixins][definition] and their [contentList Initializers][initializer] to list and select headless page templates or content views.
- some [json overrides][overrides] to disable form fields or to use specific selector types.
- an [api authorization configuration][authorization] to authorize graphQL call coming from the Nextjs server.

[jahia-website]: https://www.jahia.com
[vercel-website]: https://vercel.com
[initiative.md]: https://github.com/Jahia/jahia-nextjs-initiative/blob/main/README.md
[headless-templatesSet]: https://github.com/Jahia/headless-templatesSet

[proxy]: ./src/main/java/org/jahia/se/modules/nextjs/ProxyServlet.java
[initializer]: ./src/main/java/org/jahia/se/modules/nextjs/initializers
[definition]: ./src/main/resources/META-INF/definitions.cnd
[authorization]: ./src/main/resources/META-INF/configurations/org.jahia.bundles.api.authorization-nextjsproxy.yaml
[overrides]: ./src/main/resources/META-INF/jahia-content-editor-forms/fieldsets
