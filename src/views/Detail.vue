<template>
  <div class="detail-layout" v-if="topicDetail">
    <div v-if="topicsList" class="footer">
      <span @click="prev">上一篇</span> | <span @click="next">下一篇</span>
    </div>
    <h1>{{ topicDetail.title }}</h1>
    <div>
      作者: {{ topicDetail.author.loginname }} 时间: {{ topicDetail.create_at }}
      <span v-if="pageIndex > -1">位置:{{ pageIndex }}</span>
    </div>
    <div v-html="topicDetail.content"></div>
  </div>
</template>
<script>
import { mapState } from "vuex";
export default {
  name: "Detail",
  data() {
    return {
      pageIndex: -1,
      isMounted: false //for control not ssr render
    };
  },
  computed: {
    ...mapState(["topicDetail", "topicsList"])
  },
  tdk() {
    return {
      title: this.topicDetail && this.topicDetail.title
    };
  },
  serverPrefetch() {
    return this.fetchData();
  },
  mounted() {
    this.isMounted = true;
    const alreadyIncremented = !!this.topicDetail;
    if (!alreadyIncremented) {
      this.fetchData().then(this.fetchDataMounted());
    } else {
      this.fetchDataMounted();
    }
  },
  beforeRouteUpdate(to, from, next) {
    // 一般建议路由变更采用计算属性或者store直接绑定
    // 特殊情况处理可以采用如下方案 重新注册数据返回处理
    this.fetchData().then(this.fetchDataMounted());
    next();
  },
  destroyed() {
    this.$store.commit("SET_TOPIC_DETAIL", { detail: null });
  },
  methods: {
    // fetchData for client and server render
    fetchData() {
      this.isMounted && this.$loading(true);
      return this.$store
        .dispatch("FETCH_TOPIC_DETAIL", {
          id: this.$route.params.id
        })
        .finally(() => {
          this.$loading(false);
        });
    },
    // fetchData callback on mounted
    fetchDataMounted() {
      this.pageIndex = this._getCurrentIndexInList();
    },
    next() {
      const itemIndex = this._getCurrentIndexInList();
      if (itemIndex > -1 && itemIndex < this.topicsList.length - 1) {
        const id = this.topicsList[itemIndex + 1].id;
        this.$router.push({ path: `/detail/${id}` });
      }
    },
    prev() {
      const itemIndex = this._getCurrentIndexInList();
      if (itemIndex > -1 && itemIndex > 0) {
        const id = this.topicsList[itemIndex - 1].id;
        this.$router.push({ path: `/detail/${id}` });
      }
    },
    _getCurrentIndexInList() {
      if (!this.topicsList) return undefined;
      const finds = this.topicsList.filter(i => i.id == this.$route.params.id);
      const itemIndex = finds.length ? this.topicsList.indexOf(finds[0]) : -1;
      return itemIndex;
    }
  }
};
</script>
<style lang="scss" scoped>
.detail-layout {
  max-width: 600px;
  margin: 0 auto;
}
.footer {
  margin-bottom: 60px;
}
</style>
