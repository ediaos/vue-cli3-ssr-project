const VueSSRServerPlugin = require("vue-server-renderer/server-plugin");
const VueSSRClientPlugin = require("vue-server-renderer/client-plugin");
const nodeExternals = require("webpack-node-externals");
const webpack = require("webpack");
const CopyWebpackPlugin = require("copy-webpack-plugin");
const SpritesmithPlugin = require("webpack-spritesmith");
const deployConfig = require("./config");
const glob = require("glob");
const path = require("path");
const resolve = file => path.resolve(__dirname, file);
const TARGET_NODE = process.env.BUILD_TARGET === "node";
const target = TARGET_NODE ? "server" : "client";
const isDev = process.env.NODE_ENV === "dev";

module.exports = {
  assetsDir: "static",
  baseUrl: deployConfig.env.TUJIA_CDN_HOST,
  devServer: {
    headers: { "Access-Control-Allow-Origin": "*" },
    proxy: deployConfig.dev.proxyTable
  },
  transpileDependencies: [resolve("node_modules/@tujia/fe_js_com/src")],
  // eslint-disable-next-line
  configureWebpack: (config) => ({
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
      splitChunks: TARGET_NODE
        ? false
        : {
            chunks: "all",
            cacheGroups: {
              libs: {
                name: "chunk-vendors",
                test: /[\/]node_modules[\/]/,
                priority: 10,
                chunks: "initial" // 只打包初始时依赖的第三方
              },
              elementUI: {
                name: "chunk-elementUI", // 单独将 elementUI 拆包
                priority: 20, // 权重要大于 libs 和 app 不然会被打包进 libs 或者 app
                test: /[\/]node_modules[\/]element-ui[\/]/
              }
              // commons: {
              //   name: 'chunk-comomns',
              //   test: resolve('src/components'), // 可自定义拓展你的规则,可后续根据业务拆分
              //   minChunks: 2, // 最小共用次数
              //   priority: 5,
              //   reuseExistingChunk: true
              // }
            }
          }
    },
    plugins: [
      TARGET_NODE ? new VueSSRServerPlugin() : new VueSSRClientPlugin(),
      new webpack.DefinePlugin({
        "process.env.VUE_ENV": `"${target}"`,
        "process.env.NODE_DEPLOY": `"${process.env.NODE_DEPLOY}"`,
        "process.env.config": getDeployConfigDefine()
      })
    ]
      .concat(
        isDev
          ? []
          : new CopyWebpackPlugin([
              {
                from: resolve("./static"),
                to: resolve("./dist/static"),
                toType: "dir",
                ignore: ["index.html", ".DS_Store"]
              },
              {
                from: resolve("./server"),
                to: resolve("./dist/server"),
                toType: "dir",
                ignore: [
                  "setup-dev-server.js",
                  "pm2.config.template.js",
                  ".DS_Store"
                ]
              },
              {
                from: resolve("./server/pm2.config.template.js"),
                to: resolve("./dist/server/pm2.config.js"),
                transform: function(content) {
                  return content
                    .toString()
                    .replace("NODE_ENV_VALUE", process.env.NODE_ENV)
                    .replace("NODE_PORT_VALUE", process.env.NODE_PORT)
                    .replace("NODE_DEPLOY_VALUE", process.env.NODE_DEPLOY);
                }
              },
              {
                from: resolve("./package.json"),
                to: resolve("./dist")
              },
              {
                from: resolve("./package-lock.json"),
                to: resolve("./dist")
              }
            ])
      )
      .concat(TARGET_NODE ? [] : getCssSpritesPlugins())
  }),
  chainWebpack: config => {
    // alias
    config.resolve.alias
      .set("@", resolve("src"))
      .set("@lib", "@tujia/fe_js_com/src")
      .set("@assets", resolve("src/assets"));
    config.resolve.modules.add("assets/images/sprites/build");

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

// deploy config converter
function getDeployConfigDefine() {
  let config = {};
  Object.keys(deployConfig.env).forEach(function(key) {
    config[key] = `"${deployConfig.env[key]}"`;
  });
  return config;
}

// 解决雪碧图问题
function getCssSpritesPlugins() {
  const path = "src/assets/images/sprites";
  let plugins = [];
  glob.sync(path + "/*").forEach(dirPath => {
    let name = dirPath.replace(path + "/", "");
    if (name === "build") {
      return;
    }
    plugins.push(
      new SpritesmithPlugin({
        src: {
          cwd: resolve(dirPath),
          glob: "*.png"
        },
        target: {
          image: resolve(`src/assets/images/sprites/build/${name}.png`),
          css: resolve(`src/assets/images/sprites/build/${name}.scss`)
        },
        apiOptions: {
          cssImageRef: `~${name}.png`
        },
        spritesmithOptions: {
          padding: 4
        }
      })
    );
  });
  return plugins;
}
