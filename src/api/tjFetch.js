/**
 * 对fetch.js进行有业务的二次封装，主要用于ret字段返回的业务处理
 * 返回成功，直接返回数据
 * 当errcode===999则跳转到验证码页面
 * 失败情况返回 {errorNo,errorMsg}
 */
const TARGET_NODE = process.env.BUILD_TARGET === "node";
import Fetch from "@lib/utils/fetch.js";
//注册给接口使用的环境配置
Fetch.prototype.envConfig = process.env.config;

export default new Fetch({
  reqHandle: options => {
    // 服务端渲染 增加cookie传输
    if (options.data && options.data.cookies) {
      if (TARGET_NODE) {
        options.headers = options.headers || {};
        options.headers.cookie = getCookieString(options.data.cookies);
      }
      delete options.data.cookies;
    }
  },
  resHandle: res => {
    const isSuccess = res.ret || res.isSuccess;
    const data = res.data || res.content || res;
    if (isSuccess) {
      return Promise.resolve(data);
    } else {
      return Promise.reject({
        errorNo: res.errcode || res.errorNo || res.errorCode,
        errorMsg: res.errmsg || res.errorMsg || res.errorMessage
      });
    }
  }
});

function getCookieString(cookies) {
  let cookieStr = "";
  for (var variable in cookies) {
    if (cookies.hasOwnProperty(variable)) {
      cookieStr += `${variable}=${encodeURIComponent(cookies[variable])}; `;
    }
  }
  return cookieStr;
}
