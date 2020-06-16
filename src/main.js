import Vue from 'vue'
import App from './App.vue'
import { createRouter } from './router'
import { createStore } from './store'
import { sync } from 'vuex-router-sync'

//关闭生产模式下给出的提示
Vue.config.productionTip = true

// for using vant components
import Vant from 'vant'
Vue.use(Vant)
// 控制服务端渲染时候，返回HTML移除三方css，转为 preload 加载，前提: 默认支持的 preload 加载
if (process.env.VUE_ENV === 'client') {
  require('vant/lib/index.css')
}

// global loading
Vue.prototype.$loading = isLoading => {
  if (isLoading) {
    Vue.prototype.$toast.loading({
      mask: true,
      message: '加载中...',
      overlayStyle: {
        backgroundColor: 'rgba(0, 0, 0, 0.1)'
      },
      duration: 0
    })
  } else {
    Vue.prototype.$toast.clear()
  }
}

export function createApp() {
  const router = createRouter()
  const store = createStore()
  // sync the router with the vuex store.
  // this registers `store.state.route`
  sync(store, router)
  const app = new Vue({
    router,
    store,
    render: h => h(App)
  })
  return { app, router, store }
}

// promise.finally Polyfill
if (!Promise.prototype.finally) {
  Promise.prototype.finally = function(callback) {
    let P = this.constructor
    return this.then(
      value => P.resolve(callback()).then(() => value),
      reason =>
        P.resolve(callback()).then(() => {
          throw reason
        })
    )
  }
}
