import { fetchTopics, fetchTopicDetail } from '../api'

export default {
  FETCH_TOPICS_LIST: ({ commit, state }) => {
    return state.topicsList
      ? Promise.resolve()
      : fetchTopics().then(res => commit('SET_TOPICS_LIST', { list:res.data.data }))
  },
  FETCH_TOPIC_DETAIL: ({ commit },{ id }) => {
    return fetchTopicDetail({ id }).then(res => commit('SET_TOPIC_DETAIL', { detail:res.data.data }))
  }
}