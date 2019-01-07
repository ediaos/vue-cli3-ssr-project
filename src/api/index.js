import axios from 'axios'
const TARGET_NODE = process.env.BUILD_TARGET === "node";
const cnodeBaseUrl = process.env.VUE_APP_HOST_TOPIC_LIST

export function fetchTopics({ cookies }){
  return axios.get(cnodeBaseUrl + '/api/v1/topics',{ headers: getCommonHeader({ cookies }) })
}

export function  fetchTopicDetail({ id, cookies }){
  return axios.get(cnodeBaseUrl + `/api/v1/topic/${id}`,{ headers: getCommonHeader({ cookies }) })
}

function getCommonHeader({ cookies }){
  if(TARGET_NODE && cookies){
    return {
      cookie: getCookieString(cookies)
    }
  }
  else{
    return null
  }
}

function getCookieString(cookies){
  let cookieStr = ''
  for (var variable in cookies) {
    if (cookies.hasOwnProperty(variable)) {
      cookieStr += `${variable}=${encodeURIComponent(cookies[variable])}; `
    }
  }
  return cookieStr
}