const bundle = require("../vue-ssr-server-bundle.json");
const clientManifest = require("../vue-ssr-client-manifest.json");
function setupServer(app, createRenderer) {
  createRenderer(bundle, {
    clientManifest
  });
}
module.exports = {
  setupServer
};
