import axios from 'axios'
const TARGET_SERVER = process.env.VUE_ENV === 'server'
// FOR PROXY TEST:  const cnodeBaseUrl = TARGET_SERVER ? process.env.config.CNODE_HOST : "/feapi";
const cnodeBaseUrl = process.env.config.CNODE_HOST
export function fetchTopics({ cookies }) {
  return axios.get(cnodeBaseUrl + '/api/v1/topics', {
    headers: getCommonHeader({ cookies })
  })
}

export function fetchTopicDetail({ id, cookies }) {
  return axios.get(cnodeBaseUrl + `/api/v1/topic/${id}`, {
    headers: getCommonHeader({ cookies })
  })
}

function getCommonHeader({ cookies }) {
  if (TARGET_SERVER && cookies) {
    return { cookie: getCookieString(cookies) }
  } else {
    return null
  }
}

function getCookieString(cookies) {
  let cookieStr = ''
  for (var variable in cookies) {
    // eslint-disable-next-line no-prototype-builtins
    if (cookies.hasOwnProperty(variable)) {
      cookieStr += `${variable}=${encodeURIComponent(cookies[variable])}; `
    }
  }
  return cookieStr
}
