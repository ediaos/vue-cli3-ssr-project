import Vue from "vue";
import Vuex from "vuex";
import actions from "./actions";
import mutations from "./mutations";
import getters from "./getters";
Vue.use(Vuex);

export function createStore() {
  return new Vuex.Store({
    state: {
      envConfig: process.env.config
    },
    mutations,
    actions,
    getters
  });
}

//fix ssr registerModule bug
Vuex.Store.prototype.registerSSRModule = function(name,storeModule,isPreserveState=true){
  if(!this._modules.root._children[name]){
    this.registerModule(name, storeModule,{ preserveState: isPreserveState && Boolean(this.state[name]) })
  }
}
