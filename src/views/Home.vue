<template>
  <div class="home">
    <img alt="Vue logo" src="../assets/logo.png">
  </div>
</template>

<script>
import { mapState } from "vuex";
import TopicItem from "../components/TopicItem";
// @ is an alias to /src
export default {
  name: "home",
  tdk(){
    return {
      title: '首页'
    }
  }
};
</script>
<style lang="scss" scoped>
.topics-list {
  max-width: 500px;
  margin: 0 auto;
}
</style>
