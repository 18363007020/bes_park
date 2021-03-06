<style>
    Edit in JSFiddle
    Result
    HTML
    CSS
    body {
        font-size: 11px;
        font-family: 'Open Sans', sans-serif;
        color: #4A4A4A ;
        text-align: center;
    }
    .energyEfficiencyAssessmentTotal {
        display: flex;
        align-items: flex-start;
    }
    .energyEfficiencyAssessmentTotal1 {
        display: flex;
        align-items: center;
    }
    .energyEfficiencyAssessmentLeft {
        width: 65%;
        height: 100%;
    }
    .energyEfficiencyAssessmentRight {
        width: 35%;
        height: 100%;
    }
    .energyEfficiencyAssessmentBox7{
        margin: 20px auto;
        margin-right: 60px;
        min-height: 300px;
        padding: 10px;
        position: relative;
        background: -webkit-gradient(linear, 0% 20%, 0% 92%, from(#fff), to(#f3f3f3), color-stop(.1,#fff));
        border-top: 1px solid #ccc;
        border-right: 1px solid #ccc;
        border-left: 1px solid #ccc;
        -webkit-box-shadow: 0px 0px 12px rgba(0, 0, 0, 0.2);
    }

    .energyEfficiencyAssessmentBox7:before{
        content: '';
        position:absolute;
        width: 130px;
        height: 30px;
        border-left: 1px dashed rgba(0, 0, 0, 0.1);
        border-right: 1px dashed rgba(0, 0, 0, 0.1);
        background: -webkit-gradient(linear, 555% 20%, 0% 92%, from(rgba(0, 0, 0, 0.1)), to(rgba(0, 0, 0, 0.0)), color-stop(.1,rgba(0, 0, 0, 0.2)));
        -webkit-box-shadow: 0px 0px 12px rgba(0, 0, 0, 0.2);
        -webkit-transform:translate(-50px,10px)
        skew(10deg,10deg)
        rotate(-50deg)
    }
    .energyEfficiencyAssessmentBox7:after{
        content: '';
        position:absolute;
        right:0;
        bottom:0;
        width: 130px;
        height: 30px;
        background: -webkit-gradient(linear, 555% 20%, 0% 92%, from(rgba(0, 0, 0, 0.1)), to(rgba(0, 0, 0, 0.0)), color-stop(.1,rgba(0, 0, 0, 0.2)));
        border-left: 1px dashed rgba(0, 0, 0, 0.1);
        border-right: 1px dashed rgba(0, 0, 0, 0.1);
        -webkit-box-shadow: 0px 0px 12px rgba(0, 0, 0, 0.2);
        -webkit-transform: translate(50px,-20px)
        skew(10deg,10deg)
        rotate(-50deg)
    }

    .energyEfficiencyAssessmentBox7 p{
        margin-top: 15px;
        text-align: justify;
    }

    .energyEfficiencyAssessmentBox7 h1{
        font-size: 20px;
        font-weight: bold;
        margin-top: 5px;
        text-shadow: 1px 1px 3px rgba(0,0,0,0.3);
    }

</style>
<div class="">
	<div id="energyEfficiencyAssessmentDiv">
		<div class="">
			<span class="">
				<i class="fa fa-th-list" aria-hidden="true"></i>&nbsp;??????????????????>>>
	    	</span>
		</div>
	</div>
	<div class="energyEfficiencyAssessmentTotal">
	    <div class="energyEfficiencyAssessmentLeft" style="margin-left:10px;">
	        <div class="energyEfficiencyAssessmentTotal1" style="width: 80%;height:100px;">
	            <div style="background-color:#91C7AE;width: 32px;height:16px; "></div>&nbsp;????????????&nbsp;
	            <div style="background-color:#63869E;width: 32px;height:16px; "></div>&nbsp;??????&nbsp;
	            <div style="background-color: #C23531;width: 32px;height:16px;"></div>&nbsp;???&nbsp;
	        </div>
	        <div class="COPEnergyEfficiencyAssessment" id="COPEnergyEfficiencyAssessment" style="width:80%;height:80%;" sequence="27"></div>
	    </div>
	    <div class="energyEfficiencyAssessmentRight">
	        <div class="energyEfficiencyAssessmentBox7">
	            <h1 style="color: #0c0c0c">???????????????????????????ASHRAE???</h1>
	            <p style="color: #0c0c0c">
	                ASHRAE???American Society of Heating, Refrigerating and Air-Conditioning Engineers???
	                ????????????1894???????????????????????????????????????????????????54,000?????????????????????????????????????????????????????????????????????
	                ???????????????????????????????????????????????????????????????????????????????????????????????????????????????ASHRAE????????????????????????????????????
	                ASHRAE????????????????????????????????????????????????????????????????????????????????????????????????ASHAE????????????1894??????
	                ??????????????????????????????ASRE??????1959??????????????????1904??????
	            </p>
	            <br />
	        </div>
	    </div>
	</div>
</div>


<div class="layui-fluid">
	<form class="layui-form " style="display: none;" id="DBClickAssessmentForm">
		<input type="hidden">
		<div class="layui-form-item" style = "margin-top:2vh;">
			<div class="layui-row">
				<div class="layui-col-xs2">
					<label class="layui-form-label">??????DDC???</label>
				</div>
				<div class="layui-col-xs3">
					<select id="configDBClickAssessmentFormSysName" name="configDBClickAssessmentFormSysName" lay-filter="ddcOption" lay-search="" lay-verify=""><option value="">???????????????</option></select>
				</div>
				<div class="layui-col-xs2">
					<label class="layui-form-label">???????????????</label>
				</div>
				<div class="layui-col-xs3">
					<select id="configDBClickAssessmentFormFid" name="configDBClickAssessmentFormFid" lay-search="" lay-verify=""><option value="">???????????????</option></select>
				</div>
			</div>
		</div>
		<div class="layui-form-item">
			<div class="layui-row">
				<div class="layui-col-md2">
					<label class="layui-form-label">???&emsp;&emsp;??????</label>
				</div>
				<div class="layui-col-md3">
					<input type="text"  name="commonConfigDesc" value="" placeholder="??????"   lay-verify="" class="layui-input">
				</div>
			</div>
		</div>
		<div class="layui-form-item">
			<div class="layui-col-xs9">
				<div class="layui-input-block" style="float:right;">
					<button type="button" class="layui-btn layui-btn-normal" lay-submit="" lay-filter="saveCommonConfig">??????</button>
					<button type="button" class="layui-btn layui-btn-primary" onclick="COPEnergyEfficiencyAssessment.closeAssessmentForm()">??????</button>
				</div>
			</div>
		</div>
	</form>
</div>

<script type="text/javascript">

	var form=layui.form;

	var COPEnergyEfficiencyAssessment=(function($, window, document, undefined) {
			var timer;
		   	var divHeightSum = $(window).height();//????????????????????????????????????
			var divWidthSum = $(window).width();//?????????????????????????????????
			var pageDivSequence; //div??????
	        $("#COPEnergyEfficiencyAssessment").css({"height":divHeightSum*0.7});
			$(".energyEfficiencyAssessmentRight").css({"margin-top":divHeightSum*0.2,"margin-right":divWidthSum*0.1});//??????????????????????????????????????????
			$(".energyEfficiencyAssessmentBox7").css("width",divWidthSum*0.38);
			
	    //????????????????????????
	    function showCOPEnergy(initVal,unit){
            // ??????????????????dom????????????echarts??????
            var myChart = echarts.init(document.getElementById('COPEnergyEfficiencyAssessment'),'light');

			option = {
				tooltip: {
					formatter: '{a} <br/>{b} : {c}%'
				},
				toolbox: {
					feature: {
						restore: {},
						saveAsImage: {}
					}
				},
				series: [
					{
						name: unit,
						type: 'gauge',
						detail: {formatter: '{value}%'},
						data: [{value: initVal, name: 'COP'}]
					}
				]
			};
            myChart.setOption(option);
		}


		//???div?????????????????????????????????
		$("#COPEnergyEfficiencyAssessment").dblclick(function(obj){
			pageDivSequence=$(this).attr("sequence");//??????????????????????????????
			form.on('select(ddcOption)',function (data) {
				const configDDCSysName = data.value;
				loadPointLocations(configDDCSysName);
			})
			index = layer.open({
				tytle:'??????',
				type:1,
				area:['40vw','60vh'],
				maxmin:true,
				content:$("#DBClickAssessmentForm"),
			});
		})


		$(function(){
			loadOptions();
			initAssessment();
		})

		function loadOptions(){
			//configColdHeatSourceDdcFSysName  ??????ddc
			$.ajax({
				type : "get",
				url :_ctx + '/view/strongAndWeakElectricityIntegration/integrationCommonConfig/loadDDCOption',
				dataType : "json",
				data : {},
				success : function(result) {
					if(result.code == '0'){
						$.each(result.data,function(index,item){
							$('#configDBClickAssessmentFormSysName').append(new Option(item.f_nick_name,item.f_sys_name));
						})
						form.render();
					}else{
						layer.msg(result.msg);
					}
				},
				error : function() {
					layer.msg("??????");
				}
			});

		}


		function loadPointLocations(configDDCSysName){
			//configColdHeatSourcePointLocationFid  ??????ddc??????????????????
			$.ajax({
				type : "get",
				url :_ctx + '/view/strongAndWeakElectricityIntegration/integrationCommonConfig/loadPointLocationOption',
				dataType : "json",
				data : {f_sys_name:$('#configDBClickAssessmentFormSysName').val()},
				success : function(result) {
					if(result.code == '0'){
						$.each(result.data,function(index,item){
							$('#configDBClickAssessmentFormFid').append(new Option(item.f_nick_name,item.f_sys_name));

						});
						/* ?????????????????????????????????????????????????????????div?????????????????????????????? */
						if(configDDCSysName!=null&&configDDCSysName!=""){
							$("#configDBClickAssessmentFormFid select[name='configDBClickAssessmentFormFid'] option[value="+configDDCSysName+"]").prop("selected","selected");
						}
						form.render();
					}else{
						layer.msg(result.msg);
					}
				},
				error : function() {
					layer.msg("??????");
				}
			});
		}

		var f_sysName_list = "";

		function initAssessment(){
			$.ajax({
				type : "post",
				url : _ctx + '/view/strongAndWeakElectricityIntegration/integrationCommonConfig/selectAssessment',
				dataType : "json",
				contentType:'application/json;charset=UTF-8',
				data : JSON.stringify({
				}),
				success : function (result){
					if(result.code == '1'){
						f_sysName_list = result.data[0].f_sys_name;
						selectAssessmentData(f_sysName_list);
					}else{//
						layer.msg(result.msg);
					}
				},
				error: function (result) {
					layer.msg(result.msg);
				}
			})
		}


		//???????????????????????????
		form.on('submit(saveCommonConfig)',function(data){
			confirmCommonConfig(pageDivSequence,index);
		})

		function confirmCommonConfig() {
	    	//
			$.ajax({
				type : "post",
				url : _ctx + '/view/strongAndWeakElectricityIntegration/integrationCommonConfig/insertAssessment',
				dataType : "json",
				contentType:'application/json;charset=UTF-8',
				data : JSON.stringify({
					f_sys_name	   : $('#configDBClickAssessmentFormFid').val(),//?????????DDC??????????????????
					f_ddc_sys_name : $('#configDBClickAssessmentFormSysName').val(),
					f_seq 		   : pageDivSequence,//????????????div??????
					f_type_id	   : "6",
					f_desc :$("#DBClickAssessmentForm input[name='commonConfigDesc']").val()//??????
				}),
				success : function (result){
					if(result.code == '1'){
						layer.close(index);
						index = 0;
						f_sysName_list = $('#configDBClickAssessmentFormFid').val();
						selectAssessmentData(f_sysName_list);//???????????????????????????
					}else{//
						layer.msg(result.msg);
					}
				},
				error: function (result) {
					layer.msg(result.msg);
				}
			})
		}
		
		//?????????????????????
		function selectAssessmentData(f_sysName_list) {
			$.ajax({
				type : "post",
				url : _ctx + '/view/strongAndWeakElectricityIntegration/integrationCommonConfig/loadInitValAndEngineerUnit',
				dataType : "json",
				contentType:'application/json;charset=UTF-8',
				data : JSON.stringify({
					f_sysName_list : [f_sysName_list]
				}),
				success:function (result) {
					if(result.code !== '0' || result.data == null || result.data == ""){
						layer.alert("??????????????????")
						return;
					}else {
						var initVal = result.data[0].f_init_val;
						var unit = result.data[0].f_engineer_unit;
						showCOPEnergy(initVal,unit);
					}
				}
			})
		}


	return{
		closeAssessmentForm: function () {
			layer.close(index);
			index = 0;
		},
		pageInit:function(){
    	    var data='';
            showCOPEnergy();
            //show1(data);
		}
	}
    })(jQuery, window, document);
    COPEnergyEfficiencyAssessment.pageInit();
</script>


