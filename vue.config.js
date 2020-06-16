const VueSSRServerPlugin = require('vue-server-renderer/server-plugin')
const VueSSRClientPlugin = require('vue-server-renderer/client-plugin')
const nodeExternals = require('webpack-node-externals')
const webpack = require('webpack')
const CopyWebpackPlugin = require('copy-webpack-plugin')
const deployConfig = require('./config')
const path = require('path')
const resolve = file => path.resolve(__dirname, file)
const TARGET_NODE = process.env.BUILD_TARGET === 'node'
const target = TARGET_NODE ? 'server' : 'client'
const isDev = process.env.NODE_ENV && process.env.NODE_ENV.indexOf('dev') > -1
module.exports = {
  publicPath: deployConfig[`${isDev ? 'dev' : 'build'}`].assetsPublicPath,
  assetsDir: 'static',
  css: {
    sourceMap: !isDev && !TARGET_NODE // if enable sourceMap:  fix ssr load Critical CSS throw replace of undefind
  },
  devServer: {
    headers: { 'Access-Control-Allow-Origin': '*' },
    proxy: deployConfig.dev.proxyTable,
    disableHostCheck: true //  新增该配置项 fix ssr console error
  },
  transpileDependencies: [],
  // eslint-disable-next-line
  configureWebpack: config => ({
    entry: `./src/entry-${target}.js`,
    target: TARGET_NODE ? 'node' : 'web',
    node: TARGET_NODE ? undefined : false,
    output: {
      libraryTarget: TARGET_NODE ? 'commonjs2' : undefined
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
          whitelist: [/\.css$/, /\?vue&type=style/]
        })
      : undefined,
    optimization: {
      splitChunks: TARGET_NODE ? false : undefined
    },
    plugins: [
      TARGET_NODE ? new VueSSRServerPlugin() : new VueSSRClientPlugin(),
      new webpack.DefinePlugin({
        'process.env.VUE_ENV': `"${target}"`,
        'process.env.NODE_DEPLOY': `"${process.env.NODE_DEPLOY}"`,
        'process.env.config': getDeployConfigDefine()
      })
    ].concat(
      isDev
        ? []
        : new CopyWebpackPlugin([
            {
              from: resolve('./static'),
              to: resolve('./dist/static'),
              toType: 'dir',
              ignore: ['index.html', '.DS_Store']
            },
            {
              from: resolve('./server'),
              to: resolve('./dist/server'),
              toType: 'dir',
              ignore: [
                'setup-dev-server.js',
                'pm2.config.template.js',
                '.DS_Store'
              ]
            },
            {
              from: resolve('./server/pm2.config.template.js'),
              to: resolve('./dist/server/pm2.config.js'),
              transform: function(content) {
                return content
                  .toString()
                  .replace('NODE_ENV_VALUE', process.env.NODE_ENV)
                  .replace('NODE_PORT_VALUE', process.env.NODE_PORT)
                  .replace('NODE_DEPLOY_VALUE', process.env.NODE_DEPLOY)
              }
            },
            {
              from: resolve('./package.json'),
              to: resolve('./dist')
            },
            {
              from: resolve('./package-lock.json'),
              to: resolve('./dist')
            }
          ])
    )
  }),
  chainWebpack: config => {
    // alias
    config.resolve.alias
      .set('@', resolve('src'))
      .set('@assets', resolve('src/assets'))

    // reset public/index.html to static/index.html
    config.plugin('html').tap(args => {
      args[0].template = resolve('./static/index.html')
      return args
    })

    if (TARGET_NODE) {
      // fix ssr bug: document not found -- https://github.com/Akryum/vue-cli-plugin-ssr/blob/master/lib/webpack.js
      const isExtracting = config.plugins.has('extract-css')
      if (isExtracting) {
        // Remove extract
        const langs = ['css', 'postcss', 'scss', 'sass', 'less', 'stylus']
        const types = ['vue-modules', 'vue', 'normal-modules', 'normal']
        for (const lang of langs) {
          for (const type of types) {
            const rule = config.module.rule(lang).oneOf(type)
            rule.uses.delete('extract-css-loader')
            // Critical CSS
            rule
              .use('vue-style')
              .loader('vue-style-loader')
              .before('css-loader')
          }
        }
        config.plugins.delete('extract-css')
      }

      config.module
        .rule('vue')
        .use('cache-loader')
        .tap(options => {
          // Change cache directory for server-side
          options.cacheIdentifier += '-server'
          options.cacheDirectory += '-server'
          return options
        })
    }
    config.module
      .rule('vue')
      .use('vue-loader')
      .tap(options => {
        if (TARGET_NODE) {
          options.cacheIdentifier += '-server'
          options.cacheDirectory += '-server'
        }
        options.optimizeSSR = TARGET_NODE
        return options
      })

    // fix ssr hot update bug
    if (TARGET_NODE) {
      config.plugins.delete('hmr')
    }
  }
}

// deploy config converter
function getDeployConfigDefine() {
  let config = {}
  Object.keys(deployConfig.env).forEach(function(key) {
    config[key] = `"${deployConfig.env[key]}"`
  })
  return config
}
