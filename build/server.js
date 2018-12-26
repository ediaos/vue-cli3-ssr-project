const fs = require("fs");
const Koa = require('koa')
const koaStatic = require('koa-static')
const path = require('path')
const app = new Koa()
const resolve = (file) => path.resolve(__dirname, file)
const isProd = process.env.NODE_ENV === 'production'

// 获取render
const { createBundleRenderer } = require('vue-server-renderer')
const bundle = require("../dist/vue-ssr-server-bundle.json");
const clientManifest = require("../dist/vue-ssr-client-manifest.json");
const ssrTemplate = fs.readFileSync(resolve("../index.template.html"), "utf-8")
const renderer = createBundleRenderer(bundle,{
  template:ssrTemplate,
  clientManifest,
  runInNewContext:false
})
function renderToString(content){
  return new Promise((resolve,reject)=>{
    renderer.renderToString(content,(err,html)=>{
      err?reject(err):resolve(html)
    })
  })
}

//开放dist目录
app.use(koaStatic(resolve('../dist')))

// 处理请求
app.use(async (ctx, next)=>{
  const context = {
    title: 'ssr test',
    url: ctx.url
  }
  ctx.body = await renderToString(context)
})

app.listen(9080)
  .on('listening',()=>{
    console.log(`server started at localhost:9080`);
  })
  .on('error',(err)=>{
    console.log('---server error---')
    console.log(err)
  })