import Vue from "vue";
import App from "./App.vue";
import { createRouter } from "./router";
import { createStore } from "./store";
//关闭生产模式下给出的提示
Vue.config.productionTip = true;

// for using vant components
import Vant from "vant";
import "vant/lib/index.css";
Vue.use(Vant);

export function createApp() {
  const router = createRouter();
  const store = createStore();
  const app = new Vue({
    router,
    store,
    render: h => h(App)
  });
  return { app, router, store };
}

Vue.mixin({
  beforeCreate () {
    const { asyncData } = this.$options
    if(asyncData){
      // 注册通过asyncData数据请求返回后完成回调
      // 当有asyncData的时候，
      //     服务端渲染，因为是在router进来之前加载数据，所以在调用直接cb
      //     前端渲染，目前空是空在router进去之后再渲染，所以需要等待数据回调后再 cb
      // 这里采用cb的原因是 promise 在ssr create情况下无法做到处理完数据再渲染.
      this.dataPromiseDone = (cb)=>{
        asyncData._dataPromise ? asyncData._dataPromise.finally(()=>cb()) : cb()
      }
    }
  }
})