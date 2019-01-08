import { fetchUnitDetail } from "../api/unitApi";

export default {
  FETCH_TOPICS_LIST: () => {
    return Promise.resolve();
  },
  // eslint-disable-next-line
  FETCH_UNIT_DETAIL: ({ commit }, params = { unitId, fromSource, cookies }) => {
    return fetchUnitDetail(params)
      .then(data => commit("SET_UNIT_DETAIL", { isSuccess: true, data }))
      .catch(err => {
        commit("SET_UNIT_DETAIL", { isSuccess: false, err });
      });
  }
};
