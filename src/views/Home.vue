<template>
  <div class="home">
    <!-- v-if="isMounted" -->
    <img alt="Vue logo" src="../assets/logo.png">
    <div v-if="topicsList" class="topics-list">
      <topic-item v-for="item in topicsList" :key="item.id" :topicInfo="item" @click="navDetail(item)"/>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import TopicItem from '../components/TopicItem'
// @ is an alias to /src
export default {
  name: "home",
  components: { TopicItem },
  data(){
    return{
      isMounted:false //for control not ssr render
    }
  },
  computed:{
    ...mapState(['topicsList'])
  },
  asyncData({ store, route: { params, query, fullPath }, cookies, userAgent }) {
    return store.dispatch('FETCH_TOPICS_LIST')
  },
  mounted(){
    this.isMounted = true
    // 注册数据回调处理,仅限mounted后面生命周期中使用
    this.dataPromise.then(()=>{
      this.topicsList.forEach(item => {
        item.create_at = new Date(item.create_at).toDateString()
      });
    })
  },
  methods:{
    navDetail(detail){
      this.$router.push({ path: `/detail/${detail.id}`})
    }
  }
};
</script>
<style lang="scss" scoped>
.topics-list{
  max-width: 500px;
  margin: 0 auto;
}
</style>

