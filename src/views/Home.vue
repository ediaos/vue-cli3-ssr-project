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
    if(this.topicsList){
      this.topicsList[0].title = '111111111'
    }
    this.dataPromiseDone&&this.dataPromiseDone((isSucess)=>{
      console.log('created---------dataPromise',isSucess,this.topicsList)
      this.topicsList[0].title = '22222222'
    })
    console.log('created---------created')
  },
  mounted(){
    this.isMounted = true
    this.dataPromiseDone((isSucess)=>{
      // this.$options.__isAsyncDataLoad = true
      console.log('mounted---------dataPromise',isSucess)
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

