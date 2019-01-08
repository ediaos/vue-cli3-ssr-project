/**
 * 房屋API接口
 * 方法名以fetch作为开头
 */
import fetch from "./tjFetch";
const TARGET_NODE = process.env.BUILD_TARGET === "node";
const baseUrl = TARGET_NODE ? fetch.envConfig.TUJIA_HOST : "";

const apiConfig = {
  unitDetail: baseUrl + "/bingo/pc/unit/getpcunit"
};
/**
 * desc
 * @param {type} parameter 参数描述
 * @returns {type} 返回值描述
 */
/* eslint-disable-next-line */
export function fetchUnitDetail(data = { unitId, fromSource, cookies }) {
  return fetch.post(apiConfig.unitDetail, { data });
}
