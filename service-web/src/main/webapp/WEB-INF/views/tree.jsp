<%--
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
            /*margin-top: 50px;*/
        }
        textarea{
            width: 100%;
            height: calc(100vh - 50px);
        }
    </style>
</head>
<body>
<main id="app">
    <ul class="nav nav-tabs nav-justified">
        <li role="presentation" :class="active === 'treeStr'?'active':''" @click="active = 'treeStr'"><a href="javascript:;">解析树</a></li>
        <li role="presentation" :class="active === 'treeJson'?'active':''" @click="active = 'treeJson'"><a href="javascript:;" >解析json</a></li>
    </ul>
    <textarea v-show="active === 'treeStr'">${treeStr}</textarea>
    <textarea v-show="active === 'treeJson'">${treeJson}</textarea>
</main>

<!-- jQuery文件。务必在.js 之前引入 -->
<script src="https://cdn.bootcss.com/jquery/3.4.1/jquery.min.js"></script>
<script src="https://cdn.bootcss.com/twitter-bootstrap/3.4.1/js/bootstrap.min.js"></script>
<script src="https://cdn.bootcss.com/vue/2.6.10/vue.min.js"></script>

<script>
  var vm = new Vue({
    el: '#app',
    data: {
      active: "treeStr"
    }
  })
</script>
</body>
</html>
