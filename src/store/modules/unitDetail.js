/**
 * 详情页store
 */
import { fetchUnitDetail } from '@/api/unitApi'
export default {
  namespaced: true,
  // 重要信息：state 必须是一个函数，
  // 因此可以创建多个实例化该模块
  state: () => ({
    apiData: {}
  }),
  actions: {
    // eslint-disable-next-line
    FETCH_UNIT_DETAIL: ({ commit }, params = { unitId, fromSource, cookies }) => {
      return fetchUnitDetail(params)
        .then((data) => commit('SET_UNIT_DETAIL', { isSuccess: true, data }))
        .catch((err) => {
          commit('SET_UNIT_DETAIL', { isSuccess: false, err })
        })
    }
  },
  mutations: {
    SET_UNIT_DETAIL: (state, data) => {
      state.apiData = data || {}
    }
  }
}
