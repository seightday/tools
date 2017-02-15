// ==UserScript==
// @name         reservation
// @namespace    http://tampermonkey.net/
// @version      0.1
// @description  try to take over the world!
// @author       You
// @match        http://hzyy.szga.gov.cn/reservation.shtml
// @grant        none
// ==/UserScript==

(function() {
    'use strict';


    setInterval(function(){
        var thisweek= $('#thisweek');
        if(!thisweek&&!alerted){
            alert("no this week");
            return;
        }
        $('#thisweek').click();
        setTimeout(function(){
            //$('.weui_dialog_ft').click();
            closeTipDiv();
            //41,42,45
            if($('#51 a').html()!=="已约满"||$('#52 a').html()!=="已约满"||$('#53 a').html()!=="已约满"||$('#54 a').html()!=="已约满"||$('#55 a').html()!=="已约满"||$('#56 a').html()!=="已约满"||$('#57 a').html()!=="已约满"){
                //alert("可以预约了");
                var src="http://cdnringuc.shoujiduoduo.com/ringres/user/a24/633/17146633.aac";
                $('head').append('<div id="sound" style="display:none"><audio autoplay="autoplay" controls="controls"loop="loop" preload="auto" src="'+src+'">你的浏览器不支持audio标签</audio></div>');
                $.ajax({
                    type : "get",
                    async:false,
                    cache : false,
                    url : "http://localhost:8080/sendemail",
                    dataType : "jsonp",
                    //jsonp: "callbackparam",//服务端用于接收callback调用的function名的参数
                    //jsonpCallback:"success_jsonpCallback",//callback的function名称
                    success : function(json){
                        console.info(json);
                    },
                    error:function(){
                        console.info('fail');
                    }
                });
            }


            // window.location.href="http://hzyy.szga.gov.cn/reservation.shtml";
            //http://hzyy.szga.gov.cn/reservation!finish.shtml submit
        },2000);

    },5000);
})();