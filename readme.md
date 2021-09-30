app架构：
Alpha 0.1 版本（项目实现网络请求、sp、数据库、导航、消息、权限、paging、utils等，但功能不完善，很多功能需要添加，现有的功能也可能删除或修改）
修改前先阅读readme.md

#安全
1、混淆
2、所有地方不要出现"password"关键字
3、重要页面截图录频
4、加固
5、签名加密
6、使用userAgent判断移动端


#规范
按照Android Jetpack、Kotlin语言设计特性，遵循Material Design设计风格
多使用系统自带的库解决问题


#结构
依赖文件全部放在app或common中

app:程序入口包，项目包
common:基础包，官方依赖放在这里，尽量使这个module可以在其他新建app中重复使用
base:基础包，第三方依赖包，引入第三方依赖放在这里，并加入混淆代码
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
5. 消息        —live data bus
6. 权限        -ActivityResultContracts
7. utils      —可使用扩展函数
8. paging     —后期可以实现缓存本地
9. 图片加载    -Glide
10.刷新       -SwipeRefreshLayout 
11.图片       -ShapeableImageView
12.

#后续：
1. 多类型adapter
2. 相机相册
3. banner、viewpager2等
4. 签名打包
5. 集成主流数据统计