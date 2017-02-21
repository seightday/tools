// ==UserScript==
// @name         churujing_reservation
// @namespace    http://tampermonkey.net/
// @version      0.1
// @description  try to take over the world!
// @author       You
// @match        http://yysl.sz3e.com/wsyysq/select_sldw_zbs.jsp?type=sz
// @grant        none
// ==/UserScript==

(function() {
    'use strict';
//<div class="b_box ss_list" id="receivepoints">
//<span receivepointsid="440303000000" class="">罗湖出入境(湖贝路)</span>
//<span receivepointsid="440303000001" class="">罗湖出入境(沿河北路)</span>
//<span receivepointsid="440304000000" class="">福田出入境</span>
//<span receivepointsid="440305000000" class="">南山出入境</span>
//<span receivepointsid="440306000000" class="">宝安出入境</span>
//<span receivepointsid="440306000001" class="">龙华出入境</span>
//<span receivepointsid="440307000000" class="">龙岗出入境</span>
//<span receivepointsid="440307000001" class="">大鹏出入境</span>
//<span receivepointsid="440308000000" class="">盐田出入境</span>
//<span receivepointsid="440309000000" class="">光明出入境</span>
//<span receivepointsid="440310000000" class="">坪山出入境</span>
//</div>
var column=4;
    var ids=[
440306000000
];

    setInterval(function(){
        
        $('#receivepoints span[receivepointsid="'+ids[0]+'"]').click();
        setTimeout(function(){
            for(var i=1;i<=7;i++){
                var s=$('#row'+i+column+' span').text();
                console.info(s);
                if(s!='预约已满'){
                    var src="http://cdnringuc.shoujiduoduo.com/ringres/user/a24/633/17146633.aac";
                    $('head').append('<div id="sound" style="display:none"><audio autoplay="autoplay" controls="controls"loop="loop" preload="auto" src="'+src+'">你的浏览器不支持audio标签</audio></div>');
                    $.ajax({
                        type : "get",
                        async:false,
                        cache : false,
                        url : "http://localhost:8080/sendEmail",
                        data:{content:'可预约'},
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


            }
        },3000);

        
        
    },5000);
})();