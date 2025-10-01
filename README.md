# PELauncherDemo
我的世界基岩版运行前动态注入示例  
_MinecraftPE Pre-Runtime Dynamic Injection Example_

### 用法扩展
在 C++ 层使用 Hook 框架对原生方法和字段进行 Hook  
添加外部 Assets 路径来加载外部资源以更改 MinecraftPE 的行为和纹理

### 弊端
需要提取并修改 MinecraftPE 的 `classes.dex` 文件使其不会自动加载 `libminecraftpe.so` 或其他动态链接库

### 构建
```bash
./gradlew build
```
或使用 Android Studio

### 说明
Demo 使用 zihao_il 破解验证的 1.17.0.02_arm64-v8a 版本作为测试  
Demo 适用范围: `1.16-` ~ `1.21.7-`  
此启动方法不一定适用于 1.21.7 及以后的版本  
基于此 Demo 的 Hook 不一定适用于 1.21.7 及以后的版本  
请遵守 [许可证](#许可证)

### 许可证
PELauncherDemo: [GNU General Public License v3.0](LICENSE)