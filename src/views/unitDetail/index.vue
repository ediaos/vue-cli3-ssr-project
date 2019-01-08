/**
 * dev  : guoxing
 * desc : 说明
 * props: 
 * emit : 
 * slot : 
 */
<template>
  <article v-if="unitDetail" class='g-unit-detail'>
    {{unitDetail.unit.unitName}}
  </article>
</template>

<script>
import { mapState, mapGetters } from "vuex";
export default {
  name: "unitDetail",
  data() {
    return {};
  },
  computed: {
    ...mapState(["unitDetailApiData"]),
    ...mapGetters(["unitDetail"])
  },
  tdk() {
    const title = `房屋详情-${this.unitDetail &&
      this.unitDetail.unit.unitName}`;
    return {
      title
    };
  },
  // eslint-disable-next-line
  asyncData({ store, route: { params, query, fullPath }, cookies, userAgent }) {
    return store.dispatch("FETCH_UNIT_DETAIL", {
      unitId: params.unitid,
      cookies
    });
  },
  mounted() {
    this.dataPromiseCallBack();
  },
  beforeRouteUpdate(to, from, next) {
    // 一般建议路由变更采用计算属性或者store直接绑定
    // 特殊情况处理可以采用如下方案 重新注册数据返回处理
    this.dataPromiseCallBack();
    next();
  },
  destroyed() {
    this.$store.commit("SET_UNIT_DETAIL", null);
  },
  methods: {
    dataPromiseCallBack() {
      // 注册数据回调处理
      this.dataPromise.then(() => {
        if (!this.unitDetailApiData.isSuccess) {
          alert(this.unitDetailApiData.err.errorMsg);
        }
        console.log("this.dataPromise.then");
      });
    }
  }
};
</script>

<style lang="scss" scoped>
</style>
