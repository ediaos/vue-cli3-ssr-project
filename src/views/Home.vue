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
  created(){
    // 注册数据回调处理
    // 部分数据特殊处理，一般建议这种数据放到action中直接处理好，
    // 避免ssr渲染的时候多次计算,
    this.dataPromiseDone(()=>{
      this.topicsList.forEach(item => {
        item.create_at = new Date(item.create_at).toDateString()
      });
    })
  },
  mounted(){
    this.isMounted = true
    // this.dataPromiseDone(()=>{
    // })
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

