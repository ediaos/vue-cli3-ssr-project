export default {
  SET_TOPICS_LIST: (state, { list }) => {
    state.topicsList = list
  },
  SET_TOPIC_DETAIL: (state, { detail }) => {
    state.topicDetail = detail
  }
}
