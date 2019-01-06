import cookies from "js-cookie";
import Vue from "vue";
import { createApp } from "./main";
const { app, router, store } = createApp();
const userAgent = navigator.userAgent;
const isSSRClient = process.env.BUILD_CLIENT_TARGET === "SSR";

const loading = isLoading => {
  if (isLoading) {
    Vue.prototype.$toast.loading({
      mask: true,
      message: "加载中...",
      overlayStyle: {
        backgroundColor: 'rgba(0, 0, 0, 0.1)'
      },
      duration: 0
    });
  } else {
    Vue.prototype.$nextTick(()=>{
      Vue.prototype.$toast.clear();
    })
  }
};

// a global mixin that calls `asyncData` when a route component's params change
Vue.mixin({
  beforeRouteUpdate(to, from, next) {
    const { asyncData } = this.$options;
    if (asyncData) {
      asyncDatePromiseHook([this.$options],to)
        .then(()=>{
          next();
        })
    } else {
      next();
    }
  }
});

// prime the store with server-initialized state.
// the state is determined during SSR and inlined in the page markup.
if (window.__INITIAL_STATE__) {
  store.replaceState(window.__INITIAL_STATE__);
}

router.onReady(() => {
  if (!isSSRClient) {
    onReadyAsyncDataPromiseHook()
  }
  
  // Add router hook for handling asyncData before router enter.
  router.beforeResolve((to, from, next) => {
    beforeResolveAsyncDateHandle(to, from, next);
    next();
  });
  app.$mount("#app");
});

function beforeResolveAsyncDateHandle(to, from) {
  const matched = router.getMatchedComponents(to);
  const prevMatched = router.getMatchedComponents(from);
  let diffed = false;
  const activated = matched.filter((c, i) => {
    return diffed || (diffed = prevMatched[i] !== c);
  });
  asyncDatePromiseHook(activated,to)
}

function onReadyAsyncDataPromiseHook(){
  const matchedComponents = router.getMatchedComponents();
  // no matched routes
  if (!matchedComponents.length) {
    return 
  }
  asyncDatePromiseHook(matchedComponents,router.currentRoute)
}

function asyncDatePromiseHook(matchedComponents,route){
  const asyncDataHooks = matchedComponents.map(c => c.asyncData).filter(_ => _);
  if (!asyncDataHooks.length) return Promise.resolve()

  // Call fetchData hooks on components matched by the route.
  // A preFetch hook dispatches a store action and returns a Promise,
  // which is resolved when the action is complete and store state has been
  // updated.
  loading(true);
  const promise = Promise.all(
    asyncDataHooks.map(hook =>
      hook({ 
        store, 
        route, 
        cookies: cookies.get(), 
        userAgent 
      })
    )
  ).finally(() => {
    loading(false);
  });
  matchedComponents.map(c => c.asyncData&&(c.asyncData._dataPromise = promise)) 

  return promise 
}
