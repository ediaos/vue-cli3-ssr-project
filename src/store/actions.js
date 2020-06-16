import { fetchTopics, fetchTopicDetail } from '../api'

export default {
  FETCH_TOPICS_LIST: ({ commit, state }, { cookies }) => {
    return state.topicsList
      ? Promise.resolve()
      : fetchTopics({ cookies }).then(res =>
          commit('SET_TOPICS_LIST', { list: res.data.data })
        )
  },
  FETCH_TOPIC_DETAIL: ({ commit }, { id, cookies }) => {
    return fetchTopicDetail({ id, cookies }).then(res =>
      commit('SET_TOPIC_DETAIL', { detail: res.data.data })
    )
  }
}
