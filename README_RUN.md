# 音乐应用运行指南

## 环境要求
- Android Studio Hedgehog | 2023.1.1 或更高版本
- Android SDK 35
- Java 11
- Gradle 8.4

## 运行步骤

### 方法一：使用Android Studio
1. 打开Android Studio
2. 选择 "Open an existing Android Studio project"
3. 选择 `music_tttaaayyyx` 文件夹
4. 等待Gradle同步完成
5. 连接Android设备或启动模拟器
6. 点击运行按钮（绿色三角形）

### 方法二：使用命令行
1. 打开命令提示符或PowerShell
2. 进入项目目录：
   ```bash
   cd E:\Android\xiaomiproject\music_tttaaayyyx
   ```
3. 构建项目：
   ```bash
   gradlew.bat assembleDebug
   ```
4. 安装到设备：
   ```bash
   adb install app\build\outputs\apk\debug\app-debug.apk
   ```

### 方法三：使用运行脚本
1. 双击 `run.bat` 文件
2. 等待构建完成
3. 按照提示安装应用

## 功能说明

### 闪屏页功能
- ✅ 用户第一次启动显示隐私协议
- ✅ 同意后保存状态，后续不再显示
- ✅ 点击用户协议跳转到 https://www.mi.com
- ✅ 点击隐私协议跳转到 https://www.xiaomiev.com/
- ✅ 点击不同意退出应用
- ✅ Activity转场动画

### 音乐首页功能
- ✅ Banner轮播（自动轮播、循环滑动）
- ✅ 横滑大卡布局
- ✅ 一行一列布局
- ✅ 一行两列布局
- ✅ 下拉刷新
- ✅ 上拉加载更多
- ✅ 音乐项点击显示音乐名称
- ✅ +号按钮添加到播放列表
- ✅ 网络图片加载

## 网络配置
- 已配置网络安全策略，允许HTTP请求
- 支持小米音乐API接口
- 支持网易云音乐图片资源

## 注意事项
1. 确保设备有网络连接
2. 首次运行需要同意隐私协议
3. 如果遇到网络问题，请检查网络连接
4. 应用需要网络权限才能正常加载音乐数据

## 故障排除
- 如果构建失败，请检查Android SDK版本
- 如果网络请求失败，请检查网络连接
- 如果图片加载失败，请检查网络权限 