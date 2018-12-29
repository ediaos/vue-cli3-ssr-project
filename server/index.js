const fs = require("fs");
const path = require('path')
const Koa = require('koa')
const koaStatic = require('koa-static')
const LRU = require('lru-cache')
const resolve = (file) => path.resolve(__dirname, file)
// const morgan = require('koa-morgan')  for logs
const app = new Koa()

const isProd = true//process.env.NODE_ENV === 'production'
const template = fs.readFileSync(resolve("./index.template.html"), "utf-8")
const { createBundleRenderer } = require('vue-server-renderer')
const setupServer = require(`${isProd?'./setup-prod-server':'./setup-dev-server'}`)

// 获取render
let renderer
setupServer(app,(bundle, options)=>{
  renderer = createBundleRenderer(
    bundle,
    Object.assign(options, {
      template,
      // for component caching
      cache: new LRU({
        max: 1000,
        maxAge: 1000 * 60 * 5
      }),
      // this is only neede when vue-server-renderer is npm-linked
      // basedir: resolve('../dist'),
      // recommended for performance
      runInNewContext: false
    })
  )
  console.log('---renderer update---',renderer)
})

async function ssrRequestHandle(ctx, next) {
  const s = Date.now()
  ctx.set('Content-Type', 'text/html')
  const context = {
    title: 'SSR PAGE', // default title
    url: ctx.url,
    cookies: ctx.cookies, // for cookie using
    userAgent: ctx.header['user-agent']
  }
  console.log('----------ssrRequestHandle' ,renderer)
  renderer.renderToString(context, (err, html) => {
    if(!err){
      ctx.body = html
    }
    else{
      handleError(ctx,err)
    }
    console.log(`[DATE] whole request: ${Date.now() - s}ms  url=${ctx.url}`)
  })    
}

const handleError = (ctx,err) => {
  if (err.url) {
    ctx.status = err.code || 302
    ctx.redirect(err.url)
  } else if (err.code === 404) {
    ctx.status = 404
    ctx.body = '404 | Page Not Found'
  } else {
    // Render Error Page or Redirect
    ctx.status = 500
    ctx.body = '500 | Internal Server Error'
  }
  console.error(`error during render : url=${ctx.url} err=${JSON.stringify(err)}`)
}

//开放dist目录
app.use(koaStatic(resolve('../dist')))

// 处理请求
app.use(ssrRequestHandle)

app.listen(9080)
  .on('listening',()=>{
    console.log(`server started at localhost:9080`);
  })
  .on('error',(err)=>{
    console.log('---server error---')
    console.log(err)
  })


