import '@tujia/fe_css/lib/pc-base.css'
import './business/elementUI'
import Vue from 'vue'
import App from './App.vue'
import { createRouter } from './router'
import { createStore } from './store'
import { sync } from 'vuex-router-sync'
import tdkMixin from './utils/tdk'
Vue.mixin(tdkMixin)
// 关闭生产模式下给出的提示
Vue.config.productionTip = true

export function createApp() {
  const router = createRouter()
  const store = createStore()
  // sync the router with the vuex store.
  // this registers `store.state.route`
  sync(store, router)
  const app = new Vue({
    router,
    store,
    render: (h) => h(App)
  })
  return { app, router, store }
}
