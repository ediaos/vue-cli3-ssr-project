import { createApp } from "./main";

export default context => {
  return new Promise((resolve, reject) => {
    const { app, router } = createApp();
    const { url } = context;
    // set router's location
    router.push(url);
    router.onReady(() => {
      const matchedComponents = router.getMatchedComponents();
      // no matched routes
      if (!matchedComponents.length) {
        return reject({ code: 404 });
      }
      resolve(app);
    });
  });
};
