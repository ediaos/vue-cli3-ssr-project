const path = require('path')
const webpack = require('webpack')
const MemoryFS = require('memory-fs')
const webpackConfig = require('@vue/cli-service/webpack.config')
const axios = require('axios')
const chalk = require('chalk')
const proxy = require('koa-proxies')
const config = require('../config')



let renderer = null
// 2、编译webpack配置文件
const serverCompiler = webpack(webpackConfig)

// 指定输出到的内存流中
const mfs = new MemoryFS()
serverCompiler.outputFileSystem = mfs

// 3、监听文件修改，实时编译获取最新的 vue-ssr-server-bundle.json
let bundle = null
let clientManifest = null
serverCompiler.watch({},(err,stats)=>{
  if(err){
    throw err
  }
  stats = stats.toJson()
  stats.errors.forEach(error => console.error(error) )
  stats.warnings.forEach( warn => console.warn(warn) )
  const bundlePath = path.join(
    webpackConfig.output.path,
    'vue-ssr-server-bundle.json'
  )
  bundle = JSON.parse(mfs.readFileSync(bundlePath,'utf-8'))
  if(clientManifest){
    renderer(bundle, {
      clientManifest
    })
  }else{
    clientCompilerUpdate()
  }
  console.log('new bundle generated',Boolean(bundle),Boolean(clientManifest))
})

const updateClientManifest = async()=>{
  const result = await axios.get('http://localhost:8081/vue-ssr-client-manifest.json')
  if(result.data){
    clientManifest = result.data
  }
  else{
    console.log('updateClientManifest err',result)
  }
}

const clientCompilerUpdate = async ()=>{
  await updateClientManifest()
  if(bundle&&clientManifest){
    renderer(bundle, {
      clientManifest
    })
  }
}

function serverLog(){
  setTimeout(() => {
    console.log()
    console.log(` SSR App running at:   ${chalk.cyan('http://localhost:8080')}`)
    console.log(` ${chalk.yellow('Current page is SSR ,"http://localhost:8081" is a static path')}`)
  }, 3000);
}

module.exports = async function setupServer (app, createRenderer){
  devMiddleWare(app)
  renderer = createRenderer 
  serverLog()
}

function devMiddleWare(app){
  // 接口代理
  const proxyTable = config.dev.proxyTable
  Object.keys(proxyTable).forEach(function(key) {
    app.use(proxy(key, proxyTable[key]))
  })

  // app.use(proxy('/static', {
  //   target: 'https://api.github.com/users',    
  //   changeOrigin: true,
  //   agent: new httpsProxyAgent('http://1.2.3.4:88'), // if you need or just delete this line
  //   rewrite: path => path.replace(/^\/octocat(\/|\/\w+)?$/, '/vagusx'),
  //   logs: true
  // }))
}

