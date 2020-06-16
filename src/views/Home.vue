<template>
  <div class="home">
    <!-- v-if="isMounted" -->
    <img alt="Vue logo" src="../assets/logo.png" />
    <div v-if="topicsList" class="topics-list">
      <topic-item
        v-for="item in topicsList"
        :key="item.id"
        :topicInfo="item"
        @click="navDetail(item)"
      />
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import TopicItem from '../components/TopicItem'
import updateTdk from '../utils/tdk'
export default {
  name: 'home',
  components: { TopicItem },
  data() {
    return {
      isMounted: false //for control not ssr render
    }
  },
  computed: {
    ...mapState(['topicsList'])
  },
  tdk() {
    return {
      title: '话题列表: ' + (this.topicsList && this.topicsList[0].title),
      description: `话题Desc: ${this.topicsList &&
        this.topicsList[0].title} 时间: ${this.topicsList &&
        this.topicsList[0].create_at}`,
      keywords: `话题keywords`,
      ssrHeadAddInfo: `<link rel="canonical" href="https://www.github.com">`
    }
  },
  serverPrefetch() {
    return this.fetchData()
  },
  mounted() {
    this.isMounted = true
    const alreadyIncremented = !!this.topicsList
    if (!alreadyIncremented) {
      this.fetchData().then(this.fetchDataMounted)
    } else {
      this.fetchDataMounted()
    }
  },
  methods: {
    // fetchData for client and server render
    fetchData() {
      this.$loading(true)
      const cookies = this.$ssrContext && this.$ssrContext.cookies
      return this.$store
        .dispatch('FETCH_TOPICS_LIST', { cookies })
        .finally(() => {
          this.$loading(false)
          updateTdk.call(this)
        })
    },
    // fetchData callback on mounted
    fetchDataMounted() {
      this.topicsList &&
        this.topicsList.forEach(item => {
          item.create_at = new Date(item.create_at).toDateString()
        })
    },
    navDetail(detail) {
      this.$router.push({ path: `/detail/${detail.id}` })
    }
  }
}
</script>
<style lang="scss" scoped>
.topics-list {
  max-width: 500px;
  margin: 0 auto;
}
</style>
