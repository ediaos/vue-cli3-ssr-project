## 雪碧图 sprites 使用
sprites 是基于webpack-spritesmith插件做的雪碧图功能
- 创建雪碧图在sprites中创建雪碧图文件夹，页面雪碧图以 page- 开头，公共雪碧图以 common-开头，公共组件库以 component-开头，图片放到这些文件夹下面
- 图片需要以icon开头
- 使用方式如下
```
@import '~page-unit-detail.scss';
@include sprites([$icon-landlord]);
```