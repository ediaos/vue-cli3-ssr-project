import "@tujia/fe_css/lib/pc-base.css";
import Vue from "vue";
import App from "./App.vue";
import { createRouter } from "./router";
import { createStore } from "./store";
import tdkMixin from "./utils/tdk";
Vue.mixin(tdkMixin);
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
