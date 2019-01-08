/**
 * 房屋API接口
 * 方法名以fetch作为开头
 */
import fetch from './tjFetch'
const TARGET_NODE = process.env.BUILD_TARGET === "node";
const baseUrl = true ? 'https://www1.fvt.tujia.com' : '';

const apiConfig = {
  unitDetail: baseUrl + '/bingo/pc/unit/getpcunit'
}
/**
 * desc
 * @param {type} parameter 参数描述
 * @returns {type} 返回值描述
 */
export function fetchUnitDetail({ unitId,fromSource,cookies }){
  console.log('arguments[0]',arguments[0])
  return fetch.post(apiConfig.unitDetail,{ data:arguments[0] })
}

// export function fetchTopics({ cookies }) {
//   return axios.get(cnodeBaseUrl + "/api/v1/topics", {
//     headers: getCommonHeader({ cookies })
//   });
// }

// export function fetchTopicDetail({ id, cookies }) {
//   return axios.get(cnodeBaseUrl + `/api/v1/topic/${id}`, {
//     headers: getCommonHeader({ cookies })
//   });
// }

function getCommonHeader({ cookies }) {
  if (TARGET_NODE && cookies) {
    return {
      cookie: getCookieString(cookies)
    };
  } else {
    return null;
  }
}
