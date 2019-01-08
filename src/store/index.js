import Vue from "vue";
import Vuex from "vuex";
import actions from "./actions";
import mutations from "./mutations";
import getters from "./getters";
const TARGET_NODE = process.env.BUILD_TARGET === 'node' 
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
Vuex.Store.prototype.registerSSRModule = function(name,storeModule){
  if(!this._modules.root._children[name]){
    this.registerModule(name, storeModule,{ preserveState: !TARGET_NODE })
  }
}
