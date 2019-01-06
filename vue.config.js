const VueSSRServerPlugin = require("vue-server-renderer/server-plugin");
const VueSSRClientPlugin = require("vue-server-renderer/client-plugin");
const nodeExternals = require("webpack-node-externals");
const merge = require("lodash.merge");
const webpack = require("webpack");
var CopyWebpackPlugin = require("copy-webpack-plugin");
const path = require("path");
const resolve = file => path.resolve(__dirname, file);
const TARGET_NODE = process.env.BUILD_TARGET === "node";
const target = TARGET_NODE ? "server" : "client";
const isProd = process.env.NODE_ENV === "production";
const isSSRClient = process.env.BUILD_CLIENT_TARGET === "SSR";

module.exports = {
  baseUrl: !isProd && isSSRClient ? "http://localhost:8081" : "/",
  assetsDir: "static",
  devServer: {
    headers: { "Access-Control-Allow-Origin": "*" }
  },
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
      splitChunks: undefined
    },
    plugins: [
      TARGET_NODE ? new VueSSRServerPlugin() : new VueSSRClientPlugin(),
      new webpack.DefinePlugin({
        "process.env.BUILD_CLIENT_TARGET": `"${
          process.env.BUILD_CLIENT_TARGET
        }"`,
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
    ]
  }),
  chainWebpack: config => {
    // reset public/index.html to static/index.html
    config.plugin("html").tap(args => {
      args[0].template = resolve("./static/index.html");
      return args;
    });

    // fix ssr hot update bug
    if (!isProd && TARGET_NODE) {
      config.plugins.delete("hmr");
    }
  }
};
