<%@ page import="org.springframework.web.servlet.ModelAndView" %><%--
  Created by IntelliJ IDEA.
  User: blowsnow
  Date: 2020/2/19
  Time: 10:23
--%>
<%@page contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <base href="<%=basePath%>">
    <meta name="description" content="overview & stats"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport"
          content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta charset="utf-8"/>
    <link href="https://cdn.bootcss.com/twitter-bootstrap/3.4.1/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.bootcss.com/font-awesome/5.11.2/css/all.min.css" rel="stylesheet">
    <link href="/static/assets/css/main.css" rel="stylesheet">
    <title>百度网盘分享目录解析</title>
    <style>
        main{
            margin-top: 100px;
        }
        .title{
            font-size: 40px;
            text-align: center;
        }
    </style>
</head>
<body>
<main id="app">
    <div class="title">百度网盘分享目录解析</div>
    <div v-if="tip!== ''" style="text-align: center;" class="alert alert-danger" role="alert">{{tip}}</div>
    <form method="post" action="/tree" @submit="doSubmit" class="search-wraper" role="search">
        <div class="form-group">
            <input name="url" class="form-control search clearable" placeholder="百度网盘分享地址"
                   value="<%=request.getParameter("url") != null ? request.getParameter("url"):""%>"
                   autocomplete="off" autofocus="" tabindex="0" autocorrect="off" autocapitalize="off" spellcheck="false">
        </div>
        <div class="form-group">
            <input name="password" class="form-control search clearable" placeholder="百度网盘分享密码"
                   value="<%=request.getParameter("password") != null ? request.getParameter("password"):""%>"
                   autocomplete="off" autofocus="" tabindex="0" autocorrect="off" autocapitalize="off" spellcheck="false">
        </div>
        <div class="form-group">
            <input style="width: 100%;height: 50px;" type="submit" class="btn btn-default" placeholder="百度网盘分享密码" autocomplete="off" autofocus="" tabindex="0" autocorrect="off" autocapitalize="off" spellcheck="false">
        </div>
    </form>
    <div style="text-align: center;">注意：最多进行显示3层文件夹，后续的不进行查询</div>
    <div style="text-align: center;">注意：使用队列查询，请耐心等待自动跳转</div>
</main>

<!-- jQuery文件。务必在.js 之前引入 -->
<script src="https://cdn.bootcss.com/jquery/3.4.1/jquery.min.js"></script>
<script src="https://cdn.bootcss.com/twitter-bootstrap/3.4.1/js/bootstrap.min.js"></script>
<script src="https://cdn.bootcss.com/vue/2.6.10/vue.min.js"></script>
<script>
  var vm = new Vue({
    el: '#app',
    data: {
      tip: "${tip}",
      key: null
    },
    methods:{
      doSubmit(e){
        e.preventDefault();
        //手动提交
        var params = $("form").serialize();
        var that = this;
        $.post("/add",params,function (response) {
          console.log(response,that);
          if (response.code !== 1) {
            that.tip = response.message;
          }else{
            that.tip = '查询成功,前面还有' + response.num + '个人，请耐心等待...';
            var key = response.key;
            var timer = setInterval(function(){
              $.get("/check","key=" + key,function (status) {
                if (status.code === 1){
                  clearInterval(timer);
                  location.href = "/tree?key=" + key
                }
              })
            },1000)
          }
        })
      }
    }
  })
</script>
</body>
</html>
