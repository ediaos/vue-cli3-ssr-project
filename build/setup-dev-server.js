const webpack = require('webpack')
const MemoryFS = require('memory-fs')
const webpackConfig = require('@vue/cli-service/webpack.config')
const axios = require('axios')

// 2、编译webpack配置文件
const serverCompiler = webpack(webpackConfig)
const mfs = new MemoryFS()
// 指定输出到的内存流中
serverCompiler.outputFileSystem = mfs
console.log(webpackConfig)

// 3、监听文件修改，实时编译获取最新的 vue-ssr-server-bundle.json
let bundle = null
serverCompiler.watch({},(err,stats)=>{
  console.log('-----update----')
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
  console.log('new bundle generated')
})

const { createBundleRenderer } = require('vue-server-renderer')
// 处理请求
const handleRequest = async ctx => {
  console.log('path', ctx.path)
  if (!bundle) {
    ctx.body = '等待webpack打包完成后在访问在访问'
    return
  }
  // 4、获取最新的 vue-ssr-client-manifest.json
  const clientManifestResp = await axios.get('http://localhost:8080/vue-ssr-client-manifest.json')
  const clientManifest = clientManifestResp.data

  const renderer = createBundleRenderer(bundle, {
    runInNewContext: false,
    template: fs.readFileSync(path.resolve(__dirname, "../index.template.html"), "utf-8"),
    clientManifest: clientManifest
  });
  ctx.title = '呵呵呵呵'
  const html = await renderToString(ctx,renderer)
  ctx.body = html;
}
function renderToString(context,renderer) {
  return new Promise((resolve, reject) => {
    renderer.renderToString(context, (err, html) => {
      err ? reject(err) : resolve(html);
    });
  });
}



const fs = require("fs");
const Koa = require('koa')
const koaStatic = require('koa-static')
const path = require('path')
const app = new Koa()
const resolve = (file) => path.resolve(__dirname, file)


// 获取render
const ssrTemplate = fs.readFileSync(resolve("../index.template.html"), "utf-8")

//开放dist目录
app.use(koaStatic(resolve('../dist')))

// 处理请求
app.use(handleRequest)

app.listen(9080)
  .on('listening',()=>{
    console.log(`server started at localhost:9080`);
  })
  .on('error',(err)=>{
    console.log('---server error---')
    console.log(err)
  })
