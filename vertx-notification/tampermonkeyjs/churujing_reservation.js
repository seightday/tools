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
//<span receivepointsid="440303000000" class="">�޺����뾳(����·)</span>
//<span receivepointsid="440303000001" class="">�޺����뾳(�غӱ�·)</span>
//<span receivepointsid="440304000000" class="">������뾳</span>
//<span receivepointsid="440305000000" class="">��ɽ���뾳</span>
//<span receivepointsid="440306000000" class="">�������뾳</span>
//<span receivepointsid="440306000001" class="">�������뾳</span>
//<span receivepointsid="440307000000" class="">���ڳ��뾳</span>
//<span receivepointsid="440307000001" class="">�������뾳</span>
//<span receivepointsid="440308000000" class="">������뾳</span>
//<span receivepointsid="440309000000" class="">�������뾳</span>
//<span receivepointsid="440310000000" class="">ƺɽ���뾳</span>
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
                if(s!='ԤԼ����'){
                    var src="http://cdnringuc.shoujiduoduo.com/ringres/user/a24/633/17146633.aac";
                    $('head').append('<div id="sound" style="display:none"><audio autoplay="autoplay" controls="controls"loop="loop" preload="auto" src="'+src+'">����������֧��audio��ǩ</audio></div>');
                    $.ajax({
                        type : "get",
                        async:false,
                        cache : false,
                        url : "http://localhost:8080/sendEmail",
                        data:{content:'��ԤԼ'},
                        dataType : "jsonp",
                        //jsonp: "callbackparam",//��������ڽ���callback���õ�function���Ĳ���
                        //jsonpCallback:"success_jsonpCallback",//callback��function����
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