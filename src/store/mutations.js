export default {
  SET_TOPICS_LIST: (state, { list }) => {
    state.topicsList = list;
  },
  SET_UNIT_DETAIL: (state, data) => {
    state.unitDetailApiData = data;
  }
};
