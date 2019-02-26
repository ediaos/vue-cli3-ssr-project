global.renderServer = function (ctx, cb) {
    console.log("ctx=>", ctx)
    cb(null, "<h1>200 OK</h1>")
}
module.exports = {
    renderServer: renderServer
}