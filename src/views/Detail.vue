<template>
  <div class="detail-layout" v-if="topicDetail">
    <h1>{{topicDetail.title}}</h1>
    <div>
      作者: {{topicDetail.author.loginname}} 时间: {{ topicDetail.create_at }}
    </div>
    <div v-html="topicDetail.content">
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
export default {
  name: "Detail",
  computed:{
    ...mapState(['topicDetail'])
  },
  asyncData({ store, route: { params, query, fullPath }, cookies, userAgent }) {
    return store.dispatch('FETCH_TOPIC_DETAIL',{ id: params.id })
  },
  destroyed(){
    this.$store.commit('SET_TOPIC_DETAIL', { detail: null })
  }
};
</script>
<style lang="scss" scoped>
.detail-layout{
  max-width: 600px;
  margin: 0 auto;
}
</style>
