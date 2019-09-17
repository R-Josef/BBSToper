# BBSToper
这是一个检测mcbbs服务器宣传贴顶帖后，玩家输入指令领取奖励的bukkit插件<br>
此插件的mcbbs页面：https://www.mcbbs.net/thread-789082-1-1.html<br>
可用发行版: https://github.com/R-Josef/BBSToper/releases<br>
由于2.0之前的版本tag打错了所以不提供之前版本的Releases<br>

## 许可
本软件的许可请查看LICENCE文件.<br>

## 用到的类库: 
1.Bukkit<br>
2.Jsoup<br>
3.bStats<br>

## 构建
此项目采用maven构建, 提供了pom文件
clone此git库后可以使用maven进行构建

## 使用方法
1. 获得一份构建好的jar文件, 请查看https://github.com/R-Josef/BBSToper/releases
2. 将构建好的文件放入plugins文件夹
3. 前往mcbbs复制您的帖子id并替换掉配置文件中默认链接中的id
4. 重启/启动服务器

## 命令&权限
**玩家默认拥有`bbstoper.user`权限**<br>
**op默认拥有`bbstoper.admin`权限**<br>

**bbstoper.user的子权限**<br>
`bbstoper.binding`<br>
`bbstoper.reward`<br>

**bbstoper.admin的子权限**<br>
`bbstoper.list`<br>
`bbstoper.top`<br>
`bbstoper.check`<br>
`bbstoper.delete`<br>
`bbstoper.reload`<br>

**/bbstoper /poster /bt 都是可用命令别名**


`/bbstoper` 显示箱子GUI<br>
权限: 无需权限<br>

`/bbstoper help` 显示帮助信息<br>
权限: 无需权限<br>

`/bbstoper binding <MCBBS论坛ID>` 绑定论坛账号，注意这里是ID不是uid<br>
权限: `bbstoper.binding`<br>

`/bbstoper reward` 领取奖励<br>
权限: `bbstoper.reward`<br>

`/bbstoper list <页数>` 列出所有顶帖者<br>
权限: `bbstoper.list`<br>

`/bbstoper top <页数>` 按照顶贴次数列排名出所有已绑定玩家<br>
权限: `bbstoper.top`<br>

`/bbstoper check bbsid <论坛ID>` 查看一个论坛id的绑定者<br>
权限: `bbstoper.check`<br>

`/bbstoper check player <玩家ID>` 查看一个玩家绑定的论坛id<br>
权限: `bbstoper.check`<br>

`/bbstoper delete player <玩家ID>` 删除一个玩家的数据<br>
权限: `bbstoper.delete`<br>

`/bbstoper reload` 重载插件<br>
权限: `bbstoper.reload`<br>
