Android4静默安装APK

## 版本：0.0.5
#### 实现的功能：
*****
*****
1. 待安装的apk必须存在U盘根目录下的【diagnose】文件夹下。

2. 【diagnose】文件夹必须有且只有一个待安装的apk，否则无法实现静默安装。

3. 如果盒子之前已经安装过【diagnose】文件夹下的待安装的apk，则不会进行静默安装而是直接启动之前安装过的apk，前提是已经存在的apk与待安装的apk的包名完全一致。
4. 利用一个广播接收器接收到U盘挂载广播而后再进行判断该APK是否存在，判断方式为利用包名进行判断，若存在该apk则直接启动，若不存在则进行安装，安装使用的是“pm install -r *” 方式进行

***If you are interested in this or hava any questions ,please contact me immediately.***

+ ***Email:*** *xiaolaobanduolaoo@gmail.com*

![You either run from things or you face them,Mr. White.](https://y.yarn.co/6f47c962-2823-4bb4-b7a1-0dd8d0c56430_text.gif "Jesse")

