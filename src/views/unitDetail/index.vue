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
import storeModule from '@/store/modules/unitDetail'
const TARGET_NODE = process.env.BUILD_TARGET === 'node' 
const PAGE_NAME = "unitDetail"
let isRegisterModule = false

export default {
  name: PAGE_NAME,
  data() {
    return {};
  },
  computed: {
    apiData () {
      return this.$store.state[PAGE_NAME] && this.$store.state[PAGE_NAME].apiData
    },
    unitDetail(){
      return this.apiData&&this.apiData.isSuccess&&this.apiData.data
    }
  },
  tdk() {
    return {
      title: `房屋详情-${this.unitDetail && this.unitDetail.unit.unitName}`
    };
  },
  // eslint-disable-next-line
  asyncData({ store, route: { params, query, fullPath }, cookies, userAgent }) {
    // 使用服务端渲染store子模块注册比较特殊，特殊处理
    store.registerSSRModule(PAGE_NAME,storeModule)
    return store.dispatch(`${PAGE_NAME}/FETCH_UNIT_DETAIL`, {
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
    this.$store.commit(`${PAGE_NAME}/SET_UNIT_DETAIL`, null);
  },
  methods: {
    dataPromiseCallBack() {
      // 注册数据回调处理
      this.dataPromise.then(() => {
        if (!this.apiData.isSuccess) {
          alert(this.apiData.err.errorMsg);
        }
        console.log("this.dataPromise.then");
      });
    }
  }
};
</script>

<style lang="scss" scoped>
</style>
