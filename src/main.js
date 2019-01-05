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

// Vue.mixin({
//   beforeCreate () {
//     if(this.$options.asyncData){
//       console.log('this.$options._dataPromise',this.$options)
//       this.dataPromiseDone = (cb)=>{
//         if(this.$options.asyncData._dataPromise){
//           this.$options.asyncData._dataPromise
//             .then(res=>cb(true,res))
//             .catch(err=>cb(false,err))
//             .finally(()=>{
//               console.log('dataPromiseDone----done--cb')
//               this.$options.asyncData._dataPromise = null
//             })
//         }
//         else{
//           console.log('dataPromiseDone----cb')
//           cb(true)
//         }
//       }
//       // this.dataPromise = new Promise((resolve,reject)=>{
//       //   return this._dataPromise ? this._dataPromise.finally(()=>this._dataPromise = null) : resolve()
//       // })
//     }
//   }
// })