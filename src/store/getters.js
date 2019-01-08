export default {
  unitDetail(state) {
    const data = state.unitDetailApiData;
    return data && data.isSuccess && data.data;
  }
};
