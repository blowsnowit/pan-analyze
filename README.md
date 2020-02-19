# 百度网盘分享目录解析
快速获取 百度网盘分享的目录文件列表，生成特定有格式的树结构

## TODO
查询接口 考虑限制同ip提交次数/频率或者同 url 直接返回缓存的等等...

## 解析目录
```
┣━━测试
	┣━━测试目录3
		┣━━测试文件3.txt
	┣━━测试目录2
	┣━━测试目录1
		┣━━测试文件1.txt
```
```json
[{"serverCtime":"1582111646","size":"0","childrens":[{"serverCtime":"1582111659","size":"0","childrens":[{"serverCtime":"1582111665","size":"72","name":"测试文件3.txt","localMtime":"1581696784","serverMtime":"1582111691","md5":"eb05884bds7f84045d9f3dc1667aa21c","localCtime":"1582111665"}],"name":"测试目录3","localMtime":"1582111659","serverMtime":"1582111659","localCtime":"1582111659"},{"serverCtime":"1582111656","size":"0","childrens":[],"name":"测试目录2","localMtime":"1582111656","serverMtime":"1582111656","localCtime":"1582111656"},{"serverCtime":"1582111652","size":"0","childrens":[{"serverCtime":"1582111665","size":"72","name":"测试文件1.txt","localMtime":"1581696784","serverMtime":"1582111671","md5":"eb05884bds7f84045d9f3dc1667aa21c","localCtime":"1582111665"}],"name":"测试目录1","localMtime":"1582111652","serverMtime":"1582111652","localCtime":"1582111652"}],"name":"测试","localMtime":"1582111646","serverMtime":"1582111646","md5":"","localCtime":"1582111646"}]
```



## 百度网盘分享分析
https://pan.baidu.com/s/15xRVvtl2UOVw6oJmVuhHtQ
> 直接访问

> 如果返回 cookie包含 BDCLND 说明无需密码

https://pan.baidu.com/s/1I27ypr_bXpLJWvciQq2n1g

### 获取分享信息
https://pan.baidu.com/api/shorturlinfo?shorturl=1i7hew8NKHVu9Jt5OJjt0iQ&web=5&app_id=250528&channel=chunlei&clienttype=5

### 网盘密码验证
https://pan.baidu.com/share/verify?surl=i7hew8NKHVu9Jt5OJjt0iQ&web=5&app_id=250528&channel=chunlei&clienttype=5
pwd=kb5m&vcode=&vcode_str=
> 返回一个cookie BDCLND

### 检测文件状态
https://pan.baidu.com/share/linkstatus?web=5&app_id=250528&channel=chunlei&clienttype=5&shorturl=8vBolBFEPuRu0IB2IkG2LQ
> status 0 正常，6取消分享

### 获取根目录列表
https://pan.baidu.com/share/list?&web=5&app_id=250528&channel=chunlei&clienttype=5&desc=1&showempty=0&page=1&num=20&order=time&shorturl=8vBolBFEPuRu0IB2IkG2LQ&root=1

### 获取子目录列表
https://pan.baidu.com/share/list?&web=5&app_id=250528&channel=chunlei&clienttype=5&desc=1&showempty=0&page=1&num=20&order=time&shorturl=i7hew8NKHVu9Jt5OJjt0iQ&dir=%2F%E8%B5%84%E6%96%99

https://pan.baidu.com/share/list?uk=2622789197&shareid=2397301237&order=other&desc=1&showempty=0&web=1&page=1&num=100&dir=%2F%E8%B5%84%E6%96%99&t=0.018986365057352295&channel=chunlei&web=1&app_id=250528&bdstoken=null&clienttype=0


