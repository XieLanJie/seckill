<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
   <head>
      <title>秒杀详情</title>
      <%@include file="common/head.jsp" %>
   </head>
<body>
 <div class="container">
            <div class="panel panel-default text-center">
                <div class="panel-heading">
                    <h1>${seckill.name}</h1>
                </div>
                <div class="panel-body">
                    <h2 class="text-danger">
                        <span class="glyphicon glyphicon-time"></span>
                        <span class="glyphicon" id="seckill-box"></span>
                    </h2>
                </div>
            </div>
        </div>
        <div id="killPhoneModal" class="modal fade">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h3 class="modal-title text-center">
                            <span class="glyphicon glyphicon-phone"></span>秒杀电话：
                        </h3>
                    </div>
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-xs-8 col-xs-offset-2">
                                <input type="text" name="killphone" id="killphoneKey" placeholder="填手机号^O^" class="form-control"/>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <span id="killphoneMessage" class="glyphicon"></span>
                        <button type="button" id="killPhoneBtn" class="btn btn-success">
                            <span class="glyphicon glyphicon-phone"></span>
                            Submit
                        </button>
                    </div>
                </div>
            </div>
        </div>
</body>
<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="https://cdn.bootcss.com/jquery/2.1.1/jquery.min.js"></script>
<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<!-- 使用CDN获取公共js -->
<!-- Jquery Cookie插件 -->
<script src="https://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.js"></script>
<!-- Jquery countdown倒计时插件 -->
<script src="https://cdn.bootcss.com/jquery.countdown/2.1.0/jquery.countdown.js"></script>
<!-- 编写业务逻辑 -->
<script type="text/javascript" src="/resources/script/seckill.js"></script>
<script type="text/javascript">
$(function (){
    seckill.detail.init({
        seckillId : ${seckill.seckillId},
        startTime : ${seckill.startTime.time},
        endTime : ${seckill.endTime.time}
    }); 
});
</script>
</html>