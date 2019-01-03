<template>
  <div class="about">
    <h1>This is an about page</h1>
  </div>
</template>
<script>
export default {
  name: "About",
  asyncData({ store, route: { params, query, fullPath }, cookies, userAgent }) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        resolve();
      }, 3000);
    });
  },
  created() {}
};
</script>
