@echo off
echo 正在构建音乐应用...
cd /d "%~dp0"
call gradlew.bat assembleDebug
if %errorlevel% neq 0 (
    echo 构建失败！
    pause
    exit /b 1
)
echo 构建成功！
echo 请连接Android设备或启动模拟器，然后运行以下命令安装应用：
echo adb install app\build\outputs\apk\debug\app-debug.apk
pause 