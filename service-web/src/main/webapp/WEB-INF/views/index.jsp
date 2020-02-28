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
    <title>网盘分享目录解析</title>
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
    <div class="title">网盘分享目录解析</div>
    <div v-if="tip.text!== ''" style="text-align: center;" :class="'alert alert-' + tip.type" role="alert">{{tip.text}}</div>
    <form method="post" action="/tree" @submit="doSubmit" class="search-wraper" role="search">
        <div class="form-group">
            <input name="url" class="form-control search clearable" placeholder="网盘分享地址"
                   value="<%=request.getParameter("url") != null ? request.getParameter("url"):"https://pan.baidu.com/s/1Jp2Y2ahowxWLuAF2_vDJEw"%>"
                   autocomplete="off" autofocus="" tabindex="0" autocorrect="off" autocapitalize="off" spellcheck="false">
        </div>
        <div class="form-group">
            <input name="password" class="form-control search clearable" placeholder="网盘分享密码"
                   value="<%=request.getParameter("password") != null ? request.getParameter("password"):"x1qq"%>"
                   autocomplete="off" autofocus="" tabindex="0" autocorrect="off" autocapitalize="off" spellcheck="false">
        </div>
        <div class="form-group">
            <input style="width: 100%;height: 50px;" type="submit" class="btn btn-default"
                   :value="status === 0 ?'查询':'查询中...'">
        </div>
    </form>
    <div style="text-align: center;">支持：百度云盘，天翼云盘</div>
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
      tip: {
        text: "",
        type: ""
      },
      key: null,
      status: 0,
      timer: null
    },
    methods:{
      doSubmit(e){
        e.preventDefault();
        //手动提交

        var params = $("form").serialize();
        var that = this;
        if(that.timer){
          clearInterval(that.timer);
        }

        that.status = 1;
        $.post("/add",params,function (response) {
          that.status = 0;
          if (response.code !== 1) {
            that.showTip(response.message);
          }else{
            that.showTip('查询成功,前面还有' + response.num + '个人，请耐心等待...',"success");
            var key = response.key;
            that.timer = setInterval(function(){
              $.get("/check","key=" + key,function (result) {
                if (result.code === 1){
                  clearInterval(that.timer);
                  location.href = "/tree?key=" + key
                }else if (result.code === -1){
                  that.showTip(result.message);
                  clearInterval(that.timer);
                }
              })
            },1000)
          }
        })
      },

      //success info warning danger
      showTip(text,type){
        if (!type || type === "") type = "danger";
        this.tip = {
          text: text,
          type: type
        }
      }
    }
  })
</script>
</body>
</html>
