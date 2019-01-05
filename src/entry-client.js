import cookies from "js-cookie";
import Vue from "vue";
import { createApp } from "./main";
const { app, router, store } = createApp();
const userAgent = navigator.userAgent;
const isSSRClient = process.env.BUILD_CLIENT_TARGET === "SSR";
let isSpaClientFirstLoad = !isSSRClient;

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
let mixin = {
  beforeRouteUpdate(to, from, next) {
    console.log('--------beforeRouteUpdate')
    // asyncDatePromiseHook(this,to,next)
    const { asyncData } = this.$options;
    if (asyncData) {
      loading(true);
      asyncData({
        store,
        route: to,
        cookies: cookies.get(),
        userAgent
      }).finally(() => {
        loading(false);
        next();
      });
    } else {
      next();
    }
  },
};

// for unSSR client. Add forst router hook for handling asyncData before router enter.
// 非SSR编译，首次需要触发beforeRouteEnter来进行加载数据，之后走beforeResolve逻辑
// if (!isSSRClient) {
//   console.log('------mixin ---beforeRouteEnter')
//   mixin.
//   beforeRouteEnter = (to, from, next) => {
//     console.log('------beforeRouteEnter')
//     if (isSpaClientFirstLoad) {
//       isSpaClientFirstLoad = false;
//       beforeRouteAsyncDatePromiseHandle(to, from, next);
//     } else {
//       next();
//     }
//   };
// }

Vue.mixin(mixin);

// prime the store with server-initialized state.
// the state is determined during SSR and inlined in the page markup.
if (window.__INITIAL_STATE__) {
  store.replaceState(window.__INITIAL_STATE__);
}

router.onReady(() => {
  console.log('----------onReady')
  test()
  // Add router hook for handling asyncData before router enter.
  router.beforeResolve((to, from, next) => {
    console.log('-------router.beforeResolve')
    beforeRouteAsyncDatePromiseHandle(to, from, next);
  });
  app.$mount("#app");
});

app.$mount("#app");

function asyncDatePromiseHook(vm,route,next){
  const { asyncData } = vm.$options;
    if (asyncData) {
      loading(true);
      asyncData({
        store:vm.$store,
        route,
        cookies: cookies.get(),
        userAgent
      }).finally(() => {
        loading(false);
        next&&next();
      });
    } else {
      next&&next();
    }
}

function beforeRouteAsyncDatePromiseHandle(to, from, next) {
  const matched = router.getMatchedComponents(to);
  const prevMatched = router.getMatchedComponents(from);
  let diffed = false;
  const activated = matched.filter((c, i) => {
    return diffed || (diffed = prevMatched[i] !== c);
  });
  const asyncDataHooks = activated.map(c => c.asyncData).filter(_ => _);
  if (!asyncDataHooks.length) {
    return next();
  }
  loading(true);
  const promise = Promise.all(
    asyncDataHooks.map(hook =>
      hook({ store, route: to, cookies: cookies.get(), userAgent })
    )
  ).finally(() => {
    loading(false);
    // next();
  });
  activated.map(c => c.asyncData&&(c.asyncData._dataPromise = promise))
  console.log('----activated',activated)
  next()
}

function test(){
  const matchedComponents = router.getMatchedComponents();
  // no matched routes
  if (!matchedComponents.length) {
    return 
  }
  // Call fetchData hooks on components matched by the route.
  // A preFetch hook dispatches a store action and returns a Promise,
  // which is resolved when the action is complete and store state has been
  // updated.
  const promise = Promise.all(
    matchedComponents.map(
      ({ asyncData }) =>
        asyncData &&
        asyncData({
          store,
          route: router.currentRoute,
          cookies:cookies.get(),
          userAgent
        })
    )
  )
  matchedComponents.map(c => c.asyncData&&(c.asyncData._dataPromise = promise))    
}
