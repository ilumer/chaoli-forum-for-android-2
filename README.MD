## 超理论坛Android app测试版
[超理论坛](https://chaoli.club)

[介绍链接](https://chaoli.club/index.php/2911)

[源码食用指南](http://www.jianshu.com/p/54ea9ada6fe3)

### 已知的问题
* LaTeX公式有时会和下面一行交叉~~，行内公式$$....$$的显示会不正常~~ 0.99.1-beta版中可以显示，但不能居中
* ~~还没有支持\begin和\end之间的LaTeX~~ 0.99.1-beta版已更新
* ~~有时网络不稳定，可能是使用的网络库已经很老的原因~~ 0.99.0-beta版使用OkHttp和Retrofit代替了AsyncHttpClient
* 帖子里“引用”部分的框是我随意画的，比较难看-w-

### 等待后续实现的功能
* 一些界面的美化
* JSDraw
* 多重引用
* @
* 发帖时的多功能文本框（通过按钮添加表情、标签，支持预览）
* 楼层的编辑、删除

### 接下来要做的
* ~~换掉app中正在使用的比较老的网络库~~ 在0.99.0-beta版中已经换掉
* 更改app的架构，计划用MVVM架构

