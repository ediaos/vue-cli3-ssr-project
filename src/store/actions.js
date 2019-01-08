import { fetchUnitDetail } from "../api/unitApi";

export default {
  FETCH_TOPICS_LIST: ({ commit, state }, { cookies }) => {
    return state.topicsList
      ? Promise.resolve()
      : fetchTopics({ cookies }).then(res =>
          commit("SET_TOPICS_LIST", { list: res.data.data })
        );
  },
  FETCH_UNIT_DETAIL: ({ commit }, params = { unitId,fromSource,cookies }) => {
    return fetchUnitDetail(params).then(data =>
      commit("SET_UNIT_DETAIL", { isSuccess:true, data})
    ).catch(err=>{
      commit("SET_UNIT_DETAIL", { isSuccess:false, err})
    });
  }
};
