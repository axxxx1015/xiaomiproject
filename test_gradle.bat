@echo off
echo 正在测试Gradle配置...
echo.

echo 检查Java版本:
java -version
echo.

echo 检查Gradle版本:
gradlew.bat --version
echo.

echo 尝试同步项目:
gradlew.bat --refresh-dependencies
echo.

echo 测试完成
pause 