<template>
  <div class="home">
    <img alt="Vue logo" src="../assets/logo.png">
     <div v-if="topicsList" class="topics-list">
      <div v-for="item in topicsList" :key="item.id" class="topics-list__item" @click="navDetail(item)">
        <h3>{{item.title}}</h3>
        <span>{{item.create_at}}</span>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
// @ is an alias to /src
export default {
  name: "home",
  computed:{
    ...mapState(['topicsList'])
  },
  asyncData({ store, route: { params, query, fullPath }, cookies, userAgent }) {
    return store.dispatch('FETCH_TOPICS_LIST')
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
  .topics-list__item{
    padding: 10px;
    border-bottom: 1px solid #999;
    &:hover{
      background: #eee;
    }
    h3{
      margin: 0;
    }
  }
}
</style>

