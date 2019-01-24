module.exports = {
  apps: [
    {
      name: "pc-website",
      script: "./server/index.js",
      watch: true,
      ignore_watch: [
        // 从监控目录中排除
        "node_modules",
        "logs",
        "public",
        "static"
      ],
      error_file: "./logs/h5-err.log",
      out_file: "./logs/h5.log", // 普通日志路径
      log_date_format: "YYYY-MM-DD HH:mm:ss",
      merge_logs: true,
      instances: 2,
      env: {
        NODE_ENV: "NODE_ENV_VALUE",
        NODE_PORT: "NODE_PORT_VALUE",
        NODE_DEPLOY: "NODE_DEPLOY_VALUE"
      }
    }
  ]
};
// max_memory_restart : '100M',   // Optional: Restarts your app if it reaches 100Mo
