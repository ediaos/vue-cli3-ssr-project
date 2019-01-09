
/**
 * 服务端渲染页面配置
 */
// 需要精确匹配
const pageConfigs = [
  {
    name: 'home',
    regExp: /^\/$/
  },
  {
    name: 'detail',
    regExp: /(^\/detail\/(.+)?)|(^\/unitdetail(\/)?)|(^\/(\w+)_gongyu\/(\w+).htm)|(^\/gongyu\/(.+)?)/
  }
]

// 控制是否开启服务端渲染
const isEnableServerRender = true
// 控制是否开启用户校验服务端渲染，开启 用户登录走前端渲染，未登录走服务端渲染
const isEnableUserLoginCheck = true

// const config = require('../config') -- 等支持server端编译再做跳转
let TUJIA_ENV_DOMAIN = 'tujia.com'
switch (process.env.NODE_DEPLOY) {
  case 'dev':
    TUJIA_ENV_DOMAIN = 'dev.tujia.com'
    break;
  case 'fvt':
  case 't1':
  case 't2':
    TUJIA_ENV_DOMAIN = 'fvt.tujia.com'
    break;
  default:
    TUJIA_ENV_DOMAIN = 'tujia.com'
    break;
}




module.exports = function isServerRenderPage(ctx,cookies){
  // 关闭服务端渲染
  if(!isEnableServerRender){
    return false
  }
  // 服务端渲染判断
  const userId = cookies[`${TUJIA_ENV_DOMAIN}_PortalContext_UserId`]
  const userToken = cookies[`${TUJIA_ENV_DOMAIN}_PortalContext_UserToken`]
  // 通过url判断 确认关闭 ssr 功能
  if(ctx.query.ssr){
    return ctx.query.ssr === 'on' || ctx.query.ssr !== 'off'
  }
  // 登录用户走前端渲染
  else if(isEnableUserLoginCheck && userId && userToken){
    return false
  }
  else{
    let isSSR = false
    pageConfigs.forEach(item=>{
      if(!isSSR){
        isSSR = item.regExp.test(ctx.path)
      }
    })
    return isSSR
  }
}