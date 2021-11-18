app架构：
Release 1.0.0 版本
修改前先阅读readme.md

#安全 
1、混淆 	
2、所有地方不要出现"password"关键字 	
3、backup = false 	
4、加固	
5、签名加密		
6、删除多余权限	


#规范
本项目使用Kotlin MVVM设计规范，按照Android Jetpack、Kotlin语言设计特性，遵循Material Design设计风格。代码简洁可复用强


#结构
依赖文件全部放在app或common中

app:程序入口包，项目包，混淆代码
common:基础包，官方依赖放在这里，尽量使这个module可以在其他新建app中重复使用
base:基础包，第三方依赖包，引入第三方依赖放在这里
home:首页
circle:社区
car:爱车
shop:商场
my:我的


#功能
1. 网络请求    —retrofit + coroutines 
2. sp数据保持 ——datastore 
3. 数据库      —room 
4. 导航        —navigation/Arouter 
5. 消息        —livedatabus 
6. 权限        -SoulPermission 
7. utils      —可使用扩展函数 
8. paging     —后期可以实现缓存本地 
9. 图片加载    -Glide 
