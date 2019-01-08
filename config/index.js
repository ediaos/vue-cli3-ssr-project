
const proxyMap = {
  '/bingo/pc': {
    target: 'https://www1.fvt.tujia.com',
    // changeOrigin: true,
    // pathRewrite: {
    //   '^/feapi/www/': '/'
    // }
  }
}

module.exports = {
  build: {

  },
  dev: {
    port: 8080,
    proxy: proxyMap,
  }
}