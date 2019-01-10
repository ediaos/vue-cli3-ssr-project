import "@lib/utils/dateHelper.js";
import "@lib/business/tjSemCode.js";

window.tjGlobal = window.tjGlobal || {};
// 这里主要是因为gio里依赖，可后续优化处理
window.tjGlobal.host = process.env.config
