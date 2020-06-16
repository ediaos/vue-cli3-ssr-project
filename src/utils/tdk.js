const TARGET_NODE = process.env.VUE_ENV === 'server'

function getTdk(vm) {
  const { tdk } = vm.$options
  if (tdk) {
    return tdk.call(vm)
  }
}

function updateTdk(vm, vdom) {
  const tdk = getTdk(vm)
  vdom.title = (tdk && tdk.title) || 'SSR PAGE'
  vdom.description = (tdk && tdk.description) || ''
  vdom.keywords = (tdk && tdk.keywords) || ''
  // for append more meta or link seo need
  vdom.ssrHeadAddInfo = (tdk && tdk.ssrHeadAddInfo) || ''
}

function serverUpdateTdk() {
  updateTdk(this, this.$ssrContext)
}

function clientUpdateTdk() {
  updateTdk(this, document)
}

export default (TARGET_NODE ? serverUpdateTdk : clientUpdateTdk)
