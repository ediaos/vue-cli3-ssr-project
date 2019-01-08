const VueSSRServerPlugin = require("vue-server-renderer/server-plugin");
const VueSSRClientPlugin = require("vue-server-renderer/client-plugin");
const nodeExternals = require("webpack-node-externals");
const webpack = require("webpack");
const CopyWebpackPlugin = require("copy-webpack-plugin");
const SpritesmithPlugin = require('webpack-spritesmith')
const glob = require('glob')
const path = require("path");
const resolve = file => path.resolve(__dirname, file);
const TARGET_NODE = process.env.BUILD_TARGET === "node";
const target = TARGET_NODE ? "server" : "client";
const isDev =
  process.env.NODE_ENV === "development" ||
  process.env.NODE_ENV === "development_node";
const isSSRClient = process.env.BUILD_CLIENT_TARGET === "SSR";

module.exports = {
  baseUrl: isDev && isSSRClient ? "http://localhost:8081" : "/",
  assetsDir: "static",
  devServer: {
    headers: { "Access-Control-Allow-Origin": "*" }
  },
  transpileDependencies:[resolve('node_modules/@tujia/fe_js_com/src')],
  // eslint-disable-next-line
  configureWebpack: config => ({
    entry: `./src/entry-${target}.js`,
    target: TARGET_NODE ? "node" : "web",
    node: TARGET_NODE ? undefined : false,
    output: {
      libraryTarget: TARGET_NODE ? "commonjs2" : undefined
    },
    // https://webpack.js.org/configuration/externals/#function
    // https://github.com/liady/webpack-node-externals
    // 外置化应用程序依赖模块。可以使服务器构建速度更快，
    // 并生成较小的 bundle 文件。
    externals: TARGET_NODE
      ? nodeExternals({
          // 不要外置化 webpack 需要处理的依赖模块。
          // 你可以在这里添加更多的文件类型。例如，未处理 *.vue 原始文件，
          // 你还应该将修改 `global`（例如 polyfill）的依赖模块列入白名单
          whitelist: [/\.css$/]
        })
      : undefined,
    optimization: {
      splitChunks: TARGET_NODE ? false : undefined
    },
    plugins: [
      TARGET_NODE ? new VueSSRServerPlugin() : new VueSSRClientPlugin(),
      new webpack.DefinePlugin({
        "process.env.BUILD_TARGET": `"${process.env.BUILD_TARGET}"`
      }),
      new CopyWebpackPlugin([
        {
          from: resolve("./static"),
          to: resolve("./dist/static"),
          toType: "dir",
          ignore: ["index.html", ".DS_Store"]
        }
      ])
    ].concat(TARGET_NODE?[]:getCssSpritesPlugins())
  }),
  chainWebpack: config => {
    // alias
    config.resolve.alias
      .set('@',resolve('src'))
      .set('@lib','@tujia/fe_js_com/src')
      .set('@assets',resolve('src/assets'))
      .set('@img',resolve('src/assets/images'))
      .set('@sprites',resolve('src/assets/css/sprites'))
    config.resolve.modules
      .add(resolve('src/assets/images/sprites/build'))
      .add(resolve('src/assets/css/sprites'))

    // reset public/index.html to static/index.html
    config.plugin("html").tap(args => {
      args[0].template = resolve("./static/index.html");
      return args;
    });

    if (TARGET_NODE) {
      // 优化ssr loader
      config.module
        .rule("vue")
        .use("vue-loader")
        .tap(args => {
          args.optimizeSSR = true;
          return args;
        });

      // fix ssr bug: document not found -- https://github.com/Akryum/vue-cli-plugin-ssr/blob/master/lib/webpack.js
      const isExtracting = config.plugins.has("extract-css");
      if (isExtracting) {
        // Remove extract
        const langs = ["css", "postcss", "scss", "sass", "less", "stylus"];
        const types = ["vue-modules", "vue", "normal-modules", "normal"];
        for (const lang of langs) {
          for (const type of types) {
            const rule = config.module.rule(lang).oneOf(type);
            rule.uses.delete("extract-css-loader");
            // Critical CSS
            // rule.use('css-context').loader(CssContextLoader).before('css-loader')
          }
        }
        config.plugins.delete("extract-css");
      }
    }

    // fix ssr hot update bug
    if (isDev && TARGET_NODE) {
      config.plugins.delete("hmr");
    }
  }
};

// 解决雪碧图问题
function getCssSpritesPlugins(){
  const path = 'src/assets/images/sprites'
  let plugins = []
  glob.sync(path+'/*').forEach((dirPath) => {
    let name = dirPath.replace( path+'/', '')
    if (name == 'build') return
    plugins.push(
      new SpritesmithPlugin({
        src: {
            cwd: resolve(dirPath),
            glob: '*.png'
        },
        target: {
            image: resolve(`src/assets/images/sprites/build/${name}.png`),
            css: resolve(`src/assets/css/sprites/build/${name}.scss`)
        },
        apiOptions: {
            cssImageRef: `~${name}.png`
        },
        spritesmithOptions: {
          padding: 4
        }
      })
    )
  })
  return plugins
}
