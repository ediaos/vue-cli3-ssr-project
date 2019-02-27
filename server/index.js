const fs = require("fs");
const path = require("path");
const Koa = require("koa");
const koaStatic = require("koa-static");
const cookie = require("koa-cookie").default;
const mount = require("koa-mount");
const LRU = require("lru-cache");
const morgan = require("koa-morgan");
const resolve = file => path.resolve(__dirname, file);
const isServerRenderPage = require("./ssr-page-config");
const PORT = process.env.NODE_PORT ? parseInt(process.env.NODE_PORT) : 8080;

const isDev = process.env.NODE_ENV === "dev";
const template = fs.readFileSync(resolve("./index.template.html"), "utf-8");
const spaTemplate = fs.readFileSync(
  resolve(`${isDev ? "../static/index.html" : "../index.html"}`),
  "utf-8"
);
const { createBundleRenderer } = require("vue-server-renderer");
const setupServer = require(`${
  isDev ? "./setup-dev-server" : "./setup-prod-server"
}`);

const app = new Koa();
// add cookie for koa
app.use(cookie());

// 获取render
let renderer;
setupServer(app, (bundle, options) => {
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
      runInNewContext: false
    })
  );
});
// eslint-disable-next-line
async function ssrRequestHandle(ctx, next) {
  ctx.set("Content-Type", "text/html");
  const context = {
    title: "SSR PAGE TITLE", // default title
    description: "",
    keywords: "",
    ssrHeadAddInfo: "",
    url: ctx.url,
    cookies: ctx.cookie || {}, // for cookie using
    userAgent: ctx.header["user-agent"]
  };

  try {
    ctx.body = await renderer.renderToString(context);
  } catch (err) {
    handleError(ctx, err);
  }
}

const handleError = (ctx, err) => {
  if (err.url) {
    ctx.status = err.code || 302;
    ctx.redirect(err.url);
  } else if (err.code === 404) {
    ctx.status = 404;
    ctx.body = "404 | Page Not Found";
  } else {
    // Render Error Page or Redirect
    ctx.status = 500;
    ctx.body = "500 | Internal Server Error";
  }
  console.error(`error during render : url=${ctx.url} err=`, err);
};

//开放dist目录
app.use(mount("/static", koaStatic(resolve("../static"))));

// setup the access logger
if (!isDev) {
  app.use(morgan("combined"));
}

// 处理请求
app.use(async (ctx, next) => {
  if (isServerRenderPage(ctx, ctx.cookie || {})) {
    await ssrRequestHandle(ctx, next);
  } else {
    ctx.body = spaTemplate;
  }
});

app
  .listen(PORT)
  .on("listening", () => {
    console.log(`server started at localhost:${PORT}`);
  })
  .on("error", err => {
    console.log("---server error---", err);
  });
