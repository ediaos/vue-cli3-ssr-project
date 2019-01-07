import cookies from "js-cookie";
import Vue from "vue";
import { createApp } from "./main";
const { app, router, store } = createApp();
const userAgent = navigator.userAgent;
const isSSRClient =
  document.body.getAttribute("data-server-rendered-page") === "true";
let isSSRClientFirstLoad = isSSRClient;

const loading = isLoading => {
  if (isLoading) {
    Vue.prototype.$toast.loading({
      mask: true,
      message: "加载中...",
      overlayStyle: {
        backgroundColor: "rgba(0, 0, 0, 0.1)"
      },
      duration: 0
    });
  } else {
    Vue.prototype.$toast.clear();
  }
};

// a global mixin that calls `asyncData` when a route component's params change
Vue.mixin({
  beforeMount() {
    asyncDataPromise(this, this.$route);
  },
  beforeRouteUpdate(to, from, next) {
    asyncDataPromise(this, to, true);
    next();
  }
});

// prime the store with server-initialized state.
// the state is determined during SSR and inlined in the page markup.
if (window.__INITIAL_STATE__) {
  store.replaceState(window.__INITIAL_STATE__);
}

router.onReady(() => {
  if (!isSSRClient) {
    checkIsNeedLoading(router.getMatchedComponents(), router.currentRoute);
  }

  // Add router hook for handling asyncData before router enter.
  router.beforeResolve((to, from, next) => {
    isSSRClientFirstLoad = false;
    beforeResolveHandle(to, from);
    next();
  });
  app.$mount("#app");
});

function beforeResolveHandle(to, from) {
  const matched = router.getMatchedComponents(to);
  const prevMatched = router.getMatchedComponents(from);
  let diffed = false;
  const activated = matched.filter((c, i) => {
    return diffed || (diffed = prevMatched[i] !== c);
  });
  checkIsNeedLoading(activated);
}

function checkIsNeedLoading(matchedComponents) {
  const asyncDataHooks = matchedComponents.map(c => c.asyncData).filter(_ => _);
  if (!asyncDataHooks.length) return;
  loading(true);
}

function asyncDataPromise(vm, route, isNeedLoading) {
  const { asyncData } = vm.$options;
  if (asyncData) {
    // 将获取数据操作分配给 promise
    // 以便在组件中，我们可以在数据准备就绪后
    // 通过运行 `this.dataPromise.then(...)` 来执行其他任务
    vm.dataPromise = null;
    if (!isSSRClientFirstLoad) {
      isNeedLoading && loading(true);
      vm.dataPromise = asyncData({
        store: vm.$store,
        route: route,
        cookies: cookies.get(),
        userAgent
      });
    } else {
      vm.dataPromise = Promise.resolve();
    }
    vm.dataPromise.finally(() => {
      loading(false);
    });
  }
}
