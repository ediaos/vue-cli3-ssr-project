# vue-cli3-ssr-project

基于 Vue-Cli3 改造的 SSR 服务端渲染项目，目的是改造成可用于工程上

- 基于 Cli3 进行改造的前后端渲染项目
- 支持最新的 Vue 版本 v2.6.0+ , 支持 serverPrefetch
- 支持通过配置决定 Page 是否服务端渲染
- 使用 KOA 作为 server 端框架
- PM2 配置支持
- 支持前端渲染基于路由变更后再请求数据，用户体验更好
- 优化基于 cli3 的环境配置，改为 config 文件中配置，包括 cdn 地址支持
- 优化 dev 开发 ssr，通过双启动(ssr-server & client-dev-server)方式，实现前后端 dev 开发无感知,解决方案更加优雅
- SEO 的支持，支持 tkd 以及 head 附加

## v0.2.1

- add critical css to ssr
- fix enable map file on ssr runtime bug

## v0.2.0

- update to Vue v2.6.0+

## Project setup

```
npm install
```

### Compiles and hot-reloads for development

```
npm run dev
```

### Compiles and minifies for production

```
npm run build
```

### Run your tests

```
npm run test
```

### Lints and fixes files

```
npm run lint
```
