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
      duration: 0
    });
  } else {
    Vue.prototype.$toast.clear();
  }
};

// a global mixin that calls `asyncData` when a route component's params change
let mixin = {
  beforeRouteUpdate(to, from, next) {
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
  }
};
// for unSSR client. Add forst router hook for handling asyncData before router enter.
// 非SSR编译，首次需要触发beforeRouteEnter来进行加载数据，之后走beforeResolve逻辑
if (!isSSRClient) {
  mixin.beforeRouteEnter = (to, from, next) => {
    if (isSpaClientFirstLoad) {
      isSpaClientFirstLoad = false;
      beforeRouteAsyncDatePromiseHandle(to, from, next);
    } else {
      next();
    }
  };
}
Vue.mixin(mixin);

// prime the store with server-initialized state.
// the state is determined during SSR and inlined in the page markup.
if (window.__INITIAL_STATE__) {
  store.replaceState(window.__INITIAL_STATE__);
}

router.onReady(() => {
  // Add router hook for handling asyncData before router enter.
  router.beforeResolve((to, from, next) => {
    beforeRouteAsyncDatePromiseHandle(to, from, next);
  });
  app.$mount("#app");
});

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
  Promise.all(
    asyncDataHooks.map(hook =>
      hook({ store, route: to, cookies: cookies.get(), userAgent })
    )
  ).finally(() => {
    loading(false);
    next();
  });
}
