# BBSToper

这是一个检测mcbbs服务器宣传贴顶帖后，玩家输入指令领取奖励的bukkit插件

此插件的mcbbs页面：[https://www.mcbbs.net/thread-789082-1-1.html](https://www.mcbbs.net/thread-789082-1-1.html)

可用发行版: [https://github.com/R-Josef/BBSToper/releases](https://github.com/R-Josef/BBSToper/releases)

## 许可

本软件的许可请查看LICENCE文件.

## 用到的类库

1.Bukkit

2.Jsoup

3.bStats

## 构建

此项目采用maven构建, 提供了pom文件, clone此git库后可以使用maven进行构建

## 使用方法

1. 获得一份构建好的jar文件, 请查看[https://github.com/R-Josef/BBSToper/releases](https://github.com/R-Josef/BBSToper/releases)
2. 将构建好的文件放入plugins文件夹
3. 前往mcbbs复制您的帖子id并替换掉配置文件中默认链接中的id
4. 重启/启动服务器

## 命令&权限

**玩家默认拥有`bbstoper.user`权限**

| bbstoper.user的子权限 |
| --------------------- |
| `bbstoper.binding`    |
| `bbstoper.reward`     |

**op默认拥有`bbstoper.admin`权限**

| bbstoper.admin的子权限 |
| ---------------------- |
| `bbstoper.list`        |
| `bbstoper.top`         |
| `bbstoper.check`       |
| `bbstoper.delete`      |
| `bbstoper.reload`      |

**/bbstoper /poster /bt 都是可用命令别名**

| 命令                               | 权限               | 描述                               |
| ---------------------------------- | ------------------ | ---------------------------------- |
| `/bbstoper`                        | 无需权限           | 显示箱子GUI                        |
| `/bbstoper help`                   | 无需权限           | 显示帮助信息                       |
| `/bbstoper binding <MCBBS论坛ID>`  | `bbstoper.binding` | 绑定论坛账号，注意这里是ID不是uid  |
| `/bbstoper reward`                 | `bbstoper.reward`  | 领取奖励                           |
| `/bbstoper list <页数>`            | `bbstoper.list`    | 列出所有顶帖者                     |
| `/bbstoper top <页数>`             | `bbstoper.top`     | 按照顶贴次数列排名出所有已绑定玩家 |
| `/bbstoper check bbsid <论坛ID>`   | `bbstoper.check`   | 查看一个论坛id的绑定者             |
| `/bbstoper check player <玩家ID>`  | `bbstoper.check`   | 查看一个玩家绑定的论坛id           |
| `/bbstoper delete player <玩家ID>` | `bbstoper.delete`  | 删除一个玩家的数据                 |
| `/bbstoper reload`                 | `bbstoper.reload`  | 重载插件                           |

