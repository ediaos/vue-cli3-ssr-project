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
  tdk(){
    return {
      title: '话题列表: ' + (this.topicsList&&this.topicsList[0].title),
      description: `话题Desc: ${this.topicsList&&this.topicsList[0].title} 时间: ${this.topicsList&&this.topicsList[0].create_at}`,
      keywords: `话题keywords`,
      ssrHeadAddInfo: `<link rel="canonical" href="https://www.github.com">`
    }
  },
  asyncData({ store, route: { params, query, fullPath }, cookies, userAgent }) {
    return store.dispatch('FETCH_TOPICS_LIST',{ cookies })
  },
  mounted(){
    this.isMounted = true
    // 注册数据回调处理,仅限mounted后面生命周期中使用
    this.dataPromise.then(()=>{
      this.topicsList&&this.topicsList.forEach(item => {
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

