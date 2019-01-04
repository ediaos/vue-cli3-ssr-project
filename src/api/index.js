import axios from 'axios'

export function  fetchTopics(){
  return axios.get('https://cnodejs.org/api/v1/topics')
}

export function  fetchTopicDetail({ id }){
  return axios.get(`https://cnodejs.org/api/v1/topic/${id}`)
}