<template>
  <div class="topics-list__item" @click="$emit('click',topicInfo)">
      <h3>{{topicInfo.title}}</h3>
      <span>{{topicInfo.create_at}}</span>
  </div>
</template>

<script>
export default {
  name: "TopicItem",
  serverCacheKey: props => props.topicInfo.id, // for ssr cache item component
  props: {
    topicInfo: Object
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
.topics-list__item {
  padding: 10px;
  border-bottom: 1px solid #999;
  &:hover {
    background: #eee;
  }
  h3 {
    margin: 0;
  }
}
</style>
