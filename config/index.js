
const deploy = process.env.NODE_DEPLOY || 'prod'
const envBase = require('./env/env')
const envDeploy = require(`./env/env.${deploy}`)
const env = Object.assign(envBase,envDeploy)
const proxyMap = {
  '/bingo/pc': {
    target: env.TUJIA_HOST,
    changeOrigin: true
  }
}

module.exports = {
  env, //环境变量
  build: {
  },
  dev: {
    proxyTable: proxyMap,
  }
}