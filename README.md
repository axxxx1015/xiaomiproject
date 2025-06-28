# music_tttaaayyyx - 音乐社区项目

## 项目简介

本项目是2025年6月实践课程-音乐社区第一天的实践项目，实现了一个Android音乐社区应用。

## 项目要求

## 功能实现

### 闪屏页
1. 用户第一次启动app时显示隐私内容，后续再次打开app将不再显示隐私内容了；状态保存在本地
2. 卸载或者清除本地数据后，会重复步骤1
3. 点击同意并使用，进入app首页，点击不同意，退出app
4. 用户协议和隐私协议，点击"用户协议"跳转到浏览器并打开"https://www.mi.com"，点击"隐私协议"跳转到浏览器并打开"https://www.xiaomiev.com/"
5. 通过Activity转场动画，闪屏页自然过渡到首页，动画自由发挥

### 音乐首页
1. 首页主页面包含以下类型：banner、横滑大卡、一行一列、一行两列
   - banner只有一张图片时，不可滑动
   - banner有多张图片时，可左右滑动，左右滑动可循环；可自动轮播；滑动效果可参考网易云音乐-推荐-banner
   - 每次滑动后，更新指示器的位置和状态
2. 首页支持下拉刷新和上拉加载更多
3. 类型可参考接口文档中的style、moduleName对应具体设计稿类型
4. 包含歌曲封面图、歌曲名称、歌手名称
5. 每个Item有个+号，点击弹出Toast：将音乐名称添加到音乐列表
6. 点击Item弹出Toast：音乐名称
7. 服务端没有返回模块名称，参考设计稿本地写死即可，我的歌曲是从mi端口获取的

## 技术特性

- **网络请求**: 使用Retrofit2 + OkHttp3进行网络请求
- **图片加载**: 使用Glide加载网络图片
- **下拉刷新**: 使用SwipeRefreshLayout实现下拉刷新
- **音乐播放**: 使用MediaPlayer实现音频播放
- **布局适配**: 支持多种布局类型（Banner、横滑、单列、双列）
- **状态管理**: 本地SharedPreferences保存用户隐私协议状态

## 项目结构

```
music_tttaaayyyx/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/music_tttaaayyyx/
│   │   │   ├── adapter/          # 适配器类
│   │   │   ├── network/          # 网络请求相关
│   │   │   ├── MainActivity.java # 主页面
│   │   │   ├── SplashActivity.java # 闪屏页
│   │   │   ├── AgreementActivity.java # 用户协议页
│   │   │   ├── MusicPlayer.java  # 音乐播放器
│   │   │   └── ...
│   │   └── res/
│   │       ├── layout/           # 布局文件
│   │       ├── drawable/         # 图标资源
│   │       └── values/           # 资源文件
│   └── build.gradle.kts          # 应用级构建配置
├── gradle/                       # Gradle配置
├── build.gradle.kts              # 项目级构建配置
└── README.md                     # 项目说明
```

## 运行说明

1. 使用Android Studio打开项目
2. 确保已安装Android SDK 31+
3. 同步Gradle依赖
4. 连接Android设备或启动模拟器
5. 点击运行按钮

## 开发环境

- Android Studio Hedgehog | 2023.1.1
- Android SDK 31+
- Java 11
- Gradle 8.9.2

## 依赖库

- androidx.appcompat:appcompat:1.7.0
- com.google.android.material:material:1.12.0
- androidx.constraintlayout:constraintlayout:2.2.1
- retrofit2:retrofit:2.9.0
- com.github.bumptech.glide:glide:4.16.0
- androidx.swiperefreshlayout:swiperefreshlayout:1.1.0

## 提交记录

- 2025-06-28: 完成音乐社区第一天实践项目
  - 实现闪屏页和隐私协议功能
  - 实现音乐首页多种布局类型
  - 实现音乐播放功能
  - 实现下拉刷新和上拉加载

---

**开发者**：tayx  
**项目时间**：2025年6月  
**项目类型**：Android音乐社区应用



 
