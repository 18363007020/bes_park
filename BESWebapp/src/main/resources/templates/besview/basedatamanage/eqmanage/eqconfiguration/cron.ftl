

<script src="${ctx}/static/js/cron/jquery.easyui.min.js" type="text/javascript"></script>
<script src="${ctx}/static/js/cron/cron.js" type="text/javascript"></script>
  <style type="text/css">
    .line
    {
      position: initial;
      height: 25px;
      width: 800px;
      background-color: #ebf0f0;
      line-height: 25px;
      margin: 3px;
    }
    .imp
    {
      padding-left: 25px;
    }
    .col
    {
      width: 95px;
    }
    .tabs {
      background: #dce7f0;
    }
    .tabs-title{
      font-size: 18px;
    }
    /*ul {*/
    /*  list-style:none;*/
    /*  padding-left:10px;*/
    /*}*/
    /*li {*/
    /*  height:20px;*/
    /*}*/
  </style>

  <body>

  <div class="easyui-layout" style="width:818px;height:560px; border: 1px rgb(202, 196, 196) solid;
            border-radius: 5px;">
    <div style="height: 100%;">
      <div class="easyui-tabs" data-options="fit:true,border:false">
        <div title="秒">
          <div class="line">
            <input type="radio" checked="checked" name="second" onclick="everyTime(this)">
            每秒 允许的通配符[, - * /]</div>
          <div class="line">
            <input type="radio" name="second" onclick="cycle(this)">
            周期从
            <input class="numberspinner" style="width: 60px;" data-options="min:1,max:58" value="1"
                   id="secondStart_0">
            -
            <input class="numberspinner" style="width: 60px;" data-options="min:2,max:59" value="2"
                   id="secondEnd_0">
            秒(在这个时间段内,每秒执行一次)</div>
          <div class="line">
            <input type="radio" name="second" onclick="startOn(this)">
            从
            <input class="numberspinner" style="width: 60px;" data-options="min:0,max:59" value="0"
                   id="secondStart_1">
            秒开始,每
            <input class="numberspinner" style="width: 60px;" data-options="min:1,max:59" value="1"
                   id="secondEnd_1">
            秒执行一次</div>
          <div class="line">
            <input type="radio" name="second" id="sencond_appoint">
            指定</div>
          <div class="imp secondList">
            <input type="checkbox" value="0">00
            <input type="checkbox" value="1">01
            <input type="checkbox" value="2">02
            <input type="checkbox" value="3">03
            <input type="checkbox" value="4">04
            <input type="checkbox" value="5">05
            <input type="checkbox" value="6">06
            <input type="checkbox" value="7">07
            <input type="checkbox" value="8">08
            <input type="checkbox" value="9">09
          </div>
          <div class="imp secondList">
            <input type="checkbox" value="10">10
            <input type="checkbox" value="11">11
            <input type="checkbox" value="12">12
            <input type="checkbox" value="13">13
            <input type="checkbox" value="14">14
            <input type="checkbox" value="15">15
            <input type="checkbox" value="16">16
            <input type="checkbox" value="17">17
            <input type="checkbox" value="18">18
            <input type="checkbox" value="19">19
          </div>
          <div class="imp secondList">
            <input type="checkbox" value="20">20
            <input type="checkbox" value="21">21
            <input type="checkbox" value="22">22
            <input type="checkbox" value="23">23
            <input type="checkbox" value="24">24
            <input type="checkbox" value="25">25
            <input type="checkbox" value="26">26
            <input type="checkbox" value="27">27
            <input type="checkbox" value="28">28
            <input type="checkbox" value="29">29
          </div>
          <div class="imp secondList">
            <input type="checkbox" value="30">30
            <input type="checkbox" value="31">31
            <input type="checkbox" value="32">32
            <input type="checkbox" value="33">33
            <input type="checkbox" value="34">34
            <input type="checkbox" value="35">35
            <input type="checkbox" value="36">36
            <input type="checkbox" value="37">37
            <input type="checkbox" value="38">38
            <input type="checkbox" value="39">39
          </div>
          <div class="imp secondList">
            <input type="checkbox" value="40">40
            <input type="checkbox" value="41">41
            <input type="checkbox" value="42">42
            <input type="checkbox" value="43">43
            <input type="checkbox" value="44">44
            <input type="checkbox" value="45">45
            <input type="checkbox" value="46">46
            <input type="checkbox" value="47">47
            <input type="checkbox" value="48">48
            <input type="checkbox" value="49">49
          </div>
          <div class="imp secondList">
            <input type="checkbox" value="50">50
            <input type="checkbox" value="51">51
            <input type="checkbox" value="52">52
            <input type="checkbox" value="53">53
            <input type="checkbox" value="54">54
            <input type="checkbox" value="55">55
            <input type="checkbox" value="56">56
            <input type="checkbox" value="57">57
            <input type="checkbox" value="58">58
            <input type="checkbox" value="59">59
          </div>
        </div>
        <div title="分钟">
          <div class="line">
            <input type="radio" checked="checked" name="min" onclick="everyTime(this)">
            分钟 允许的通配符[, - * /]</div>
          <div class="line">
            <input type="radio" name="min" onclick="cycle(this)">
            周期从
            <input class="numberspinner" style="width: 60px;" data-options="min:1,max:58" value="1"
                   id="minStart_0">
            -
            <input class="numberspinner" style="width: 60px;" data-options="min:2,max:59" value="2"
                   id="minEnd_0">
            分钟(在这个时间段内,每分钟执行一次)</div>
          <div class="line">
            <input type="radio" name="min" onclick="startOn(this)">
            从
            <input class="numberspinner" style="width: 60px;" data-options="min:0,max:59" value="0"
                   id="minStart_1">
            分钟开始,每
            <input class="numberspinner" style="width: 60px;" data-options="min:1,max:59" value="1"
                   id="minEnd_1">
            分钟执行一次</div>
          <div class="line">
            <input type="radio" name="min" id="min_appoint">
            指定</div>
          <div class="imp minList">
            <input type="checkbox" value="0">00
            <input type="checkbox" value="1">01
            <input type="checkbox" value="2">02
            <input type="checkbox" value="3">03
            <input type="checkbox" value="4">04
            <input type="checkbox" value="5">05
            <input type="checkbox" value="6">06
            <input type="checkbox" value="7">07
            <input type="checkbox" value="8">08
            <input type="checkbox" value="9">09
          </div>
          <div class="imp minList">
            <input type="checkbox" value="10">10
            <input type="checkbox" value="11">11
            <input type="checkbox" value="12">12
            <input type="checkbox" value="13">13
            <input type="checkbox" value="14">14
            <input type="checkbox" value="15">15
            <input type="checkbox" value="16">16
            <input type="checkbox" value="17">17
            <input type="checkbox" value="18">18
            <input type="checkbox" value="19">19
          </div>
          <div class="imp minList">
            <input type="checkbox" value="20">20
            <input type="checkbox" value="21">21
            <input type="checkbox" value="22">22
            <input type="checkbox" value="23">23
            <input type="checkbox" value="24">24
            <input type="checkbox" value="25">25
            <input type="checkbox" value="26">26
            <input type="checkbox" value="27">27
            <input type="checkbox" value="28">28
            <input type="checkbox" value="29">29
          </div>
          <div class="imp minList">
            <input type="checkbox" value="30">30
            <input type="checkbox" value="31">31
            <input type="checkbox" value="32">32
            <input type="checkbox" value="33">33
            <input type="checkbox" value="34">34
            <input type="checkbox" value="35">35
            <input type="checkbox" value="36">36
            <input type="checkbox" value="37">37
            <input type="checkbox" value="38">38
            <input type="checkbox" value="39">39
          </div>
          <div class="imp minList">
            <input type="checkbox" value="40">40
            <input type="checkbox" value="41">41
            <input type="checkbox" value="42">42
            <input type="checkbox" value="43">43
            <input type="checkbox" value="44">44
            <input type="checkbox" value="45">45
            <input type="checkbox" value="46">46
            <input type="checkbox" value="47">47
            <input type="checkbox" value="48">48
            <input type="checkbox" value="49">49
          </div>
          <div class="imp minList">
            <input type="checkbox" value="50">50
            <input type="checkbox" value="51">51
            <input type="checkbox" value="52">52
            <input type="checkbox" value="53">53
            <input type="checkbox" value="54">54
            <input type="checkbox" value="55">55
            <input type="checkbox" value="56">56
            <input type="checkbox" value="57">57
            <input type="checkbox" value="58">58
            <input type="checkbox" value="59">59
          </div>
        </div>
        <div title="小时">
          <div class="line">
            <input type="radio" checked="checked" name="hour" onclick="everyTime(this)">
            小时 允许的通配符[, - * /]</div>
          <div class="line">
            <input type="radio" name="hour" onclick="cycle(this)">
            周期从
            <input class="numberspinner" style="width: 60px;" data-options="min:0,max:23" value="0"
                   id="hourStart_0">
            -
            <input class="numberspinner" style="width: 60px;" data-options="min:2,max:23" value="2"
                   id="hourEnd_0">
            小时(在这个时间段内,每小时执行一次)</div>
          <div class="line">
            <input type="radio" name="hour" onclick="startOn(this)">
            从
            <input class="numberspinner" style="width: 60px;" data-options="min:0,max:23" value="0"
                   id="hourStart_1">
            小时开始,每
            <input class="numberspinner" style="width: 60px;" data-options="min:1,max:23" value="1"
                   id="hourEnd_1">
            小时执行一次</div>
          <div class="line">
            <input type="radio" name="hour" id="hour_appoint">
            指定</div>
          <div class="imp hourList">
            AM:
            <input type="checkbox" value="0">00
            <input type="checkbox" value="1">01
            <input type="checkbox" value="2">02
            <input type="checkbox" value="3">03
            <input type="checkbox" value="4">04
            <input type="checkbox" value="5">05
            <input type="checkbox" value="6">06
            <input type="checkbox" value="7">07
            <input type="checkbox" value="8">08
            <input type="checkbox" value="9">09
            <input type="checkbox" value="10">10
            <input type="checkbox" value="11">11
          </div>
          <div class="imp hourList">
            PM:
            <input type="checkbox" value="12">12
            <input type="checkbox" value="13">13
            <input type="checkbox" value="14">14
            <input type="checkbox" value="15">15
            <input type="checkbox" value="16">16
            <input type="checkbox" value="17">17
            <input type="checkbox" value="18">18
            <input type="checkbox" value="19">19
            <input type="checkbox" value="20">20
            <input type="checkbox" value="21">21
            <input type="checkbox" value="22">22
            <input type="checkbox" value="23">23
          </div>
        </div>
        <div title="日">
          <div class="line">
            <input type="radio" checked="checked" name="day" onclick="everyTime(this)">
            日 允许的通配符[, - * / L W]</div>
          <div class="line">
            <input type="radio" name="day" onclick="unAppoint(this)">
            不指定</div>
          <div class="line">
            <input type="radio" name="day" onclick="cycle(this)">
            周期从
            <input class="numberspinner" style="width: 60px;" data-options="min:1,max:31" value="1"
                   id="dayStart_0">
            -
            <input class="numberspinner" style="width: 60px;" data-options="min:2,max:31" value="2"
                   id="dayEnd_0">
            日(在这个时间段内,每天执行一次)</div>
          <div class="line">
            <input type="radio" name="day" onclick="startOn(this)">
            从
            <input class="numberspinner" style="width: 60px;" data-options="min:1,max:31" value="1"
                   id="dayStart_1">
            日开始,每
            <input class="numberspinner" style="width: 60px;" data-options="min:1,max:31" value="1"
                   id="dayEnd_1">
            天执行一次</div>
          <div class="line">
            <input type="radio" name="day" onclick="workDay(this)">
            每月
            <input class="numberspinner" style="width: 60px;" data-options="min:1,max:31" value="1"
                   id="dayStart_2">
            号最近的那个工作日</div>
          <div class="line">
            <input type="radio" name="day" onclick="lastDay(this)">
            本月最后一天</div>
          <div class="line">
            <input type="radio" name="day" id="day_appoint">
            指定</div>
          <div class="imp dayList">
            <input type="checkbox" value="1">1
            <input type="checkbox" value="2">2
            <input type="checkbox" value="3">3
            <input type="checkbox" value="4">4
            <input type="checkbox" value="5">5
            <input type="checkbox" value="6">6
            <input type="checkbox" value="7">7
            <input type="checkbox" value="8">8
            <input type="checkbox" value="9">9
            <input type="checkbox" value="10">10
            <input type="checkbox" value="11">11
            <input type="checkbox" value="12">12
            <input type="checkbox" value="13">13
            <input type="checkbox" value="14">14
            <input type="checkbox" value="15">15
            <input type="checkbox" value="16">16
          </div>
          <div class="imp dayList">
            <input type="checkbox" value="17">17
            <input type="checkbox" value="18">18
            <input type="checkbox" value="19">19
            <input type="checkbox" value="20">20
            <input type="checkbox" value="21">21
            <input type="checkbox" value="22">22
            <input type="checkbox" value="23">23
            <input type="checkbox" value="24">24
            <input type="checkbox" value="25">25
            <input type="checkbox" value="26">26
            <input type="checkbox" value="27">27
            <input type="checkbox" value="28">28
            <input type="checkbox" value="29">29
            <input type="checkbox" value="30">30
            <input type="checkbox" value="31">31
          </div>
        </div>
        <div title="月">
          <div class="line">
            <input type="radio" checked="checked" name="mouth" onclick="everyTime(this)">
            月 允许的通配符[, - * /]</div>
          <div class="line">
            <input type="radio" name="mouth" onclick="unAppoint(this)">
            不指定</div>
          <div class="line">
            <input type="radio" name="mouth" onclick="cycle(this)">
            周期从
            <input class="numberspinner" style="width: 60px;" data-options="min:1,max:12" value="1"
                   id="mouthStart_0">
            -
            <input class="numberspinner" style="width: 60px;" data-options="min:2,max:12" value="2"
                   id="mouthEnd_0">
            月(在这个时间段内,每月执行一次)</div>
          <div class="line">
            <input type="radio" name="mouth" onclick="startOn(this)">
            从
            <input class="numberspinner" style="width: 60px;" data-options="min:1,max:12" value="1"
                   id="mouthStart_1">
            日开始,每
            <input class="numberspinner" style="width: 60px;" data-options="min:1,max:12" value="1"
                   id="mouthEnd_1">
            月执行一次</div>
          <div class="line">
            <input type="radio" name="mouth" id="mouth_appoint">
            指定</div>
          <div class="imp mouthList">
            <input type="checkbox" value="1">1
            <input type="checkbox" value="2">2
            <input type="checkbox" value="3">3
            <input type="checkbox" value="4">4
            <input type="checkbox" value="5">5
            <input type="checkbox" value="6">6
            <input type="checkbox" value="7">7
            <input type="checkbox" value="8">8
            <input type="checkbox" value="9">9
            <input type="checkbox" value="10">10
            <input type="checkbox" value="11">11
            <input type="checkbox" value="12">12
          </div>
        </div>
        <div title="周">
          <div class="line">
            <input type="radio" checked="checked" name="week" onclick="everyTime(this)">
            周 允许的通配符[, - * / L #]</div>
          <div class="line">
            <input type="radio" name="week" onclick="unAppoint(this)">
            不指定</div>
          <div class="line">
            <input type="radio" name="week" onclick="startOn(this)">
            周期 从星期<input class="numberspinner" style="width: 60px;" data-options="min:1,max:7"
                         id="weekStart_0" value="1">
            -
            <input class="numberspinner" style="width: 60px;" data-options="min:2,max:7" value="2"
                   id="weekEnd_0"></div>
          <div class="line">
            <input type="radio" name="week" onclick="weekOfDay(this)">
            第<input class="numberspinner" style="width: 60px;" data-options="min:1,max:4" value="1"
                    id="weekStart_1">
            周 的星期<input class="numberspinner" style="width: 60px;" data-options="min:1,max:7"
                        id="weekEnd_1" value="1"></div>
          <div class="line">
            <input type="radio" name="week" onclick="lastWeek(this)">
            本月最后一个星期<input class="numberspinner" style="width: 60px;" data-options="min:1,max:7"
                           id="weekStart_2" value="1"></div>
          <div class="line">
            <input type="radio" name="week" id="week_appoint">
            指定</div>
          <div class="imp weekList">
            <input type="checkbox" value="1">1
            <input type="checkbox" value="2">2
            <input type="checkbox" value="3">3
            <input type="checkbox" value="4">4
            <input type="checkbox" value="5">5
            <input type="checkbox" value="6">6
            <input type="checkbox" value="7">7
          </div>
        </div>
        <div title="年">
          <div class="line">
            <input type="radio" checked="checked" name="year" onclick="unAppoint(this)">
            不指定 允许的通配符[, - * /] 非必填</div>
          <div class="line">
            <input type="radio" name="year" onclick="everyTime(this)">
            每年</div>
          <div class="line">
            <input type="radio" name="year" onclick="cycle(this)">周期 从
            <input class="numberspinner" style="width: 90px;" data-options="min:2013,max:3000"
                   id="yearStart_0" value="2013">
            -
            <input class="numberspinner" style="width: 90px;" data-options="min:2014,max:3000"
                   id="yearEnd_0" value="2014"></div>
        </div>
      </div>
    </div>
    <div data-options="region:'south',border:false" style="height:250px">
      <fieldset style="border-radius: 3px; height: 220px;">
        <legend>表达式</legend>
        <table style="height: 100px;">
          <tbody>
          <tr>
            <td>
            </td>
            <td align="center">
              秒
            </td>
            <td align="center">
              分钟
            </td>
            <td align="center">
              小时
            </td>
            <td align="center">
              日
            </td>
            <td align="center">
              月<br />
            </td>
            <td align="center">
              星期
            </td>
            <td align="center">
              年
            </td>
          </tr>
          <tr>
            <td>
              表达式字段:
            </td>
            <td>
              <input type="text" name="v_second" class="col form-control" value="*" readonly="readonly" />
            </td>
            <td>
              <input type="text" name="v_min" class="col form-control" value="*" readonly="readonly" />
            </td>
            <td>
              <input type="text" name="v_hour" class="col form-control" value="*" readonly="readonly" />
            </td>
            <td>
              <input type="text" name="v_day" class="col form-control" value="*" readonly="readonly" />
            </td>
            <td>
              <input type="text" name="v_mouth" class="col form-control" value="*" readonly="readonly" />
            </td>
            <td>
              <input type="text" name="v_week" class="col form-control" value="?" readonly="readonly" />
            </td>
            <td>
              <input type="text" name="v_year" class="col form-control" readonly="readonly" />
            </td>
          </tr>
          <tr>
            <td>Cron 表达式:</td>
            <td colspan="6"><input  class="form-control" type="text" name="cron" style="width: 100%;" id="cron"
              /></td>
            <td>
              <button class="btn btn-md btn-primary" style="width: 5vw" type="button" onclick="cron_page.btnFan()">反解析到UI</button>
<#--              <input type="button" value="反解析到UI " id="btnFan" onclick="btnFan()"/>-->
            </td>
          </tr>
          <#--<tr >
            <td colspan="8" >最近5次运行时间:</td>
          </tr>
          <tr>
            <td colspan="8" id="runTime">
            </td>
          </tr>-->
          </tbody>
        </table>

        <button class="btn btn-md btn-primary" type="button" style="margin-left: 300px;margin-top: 30px;width: 200px;height: 40px;"
                data-dismiss="modal"
                onclick="cron_page.checkCron()"><strong>选中当前表达式</strong></button>

      </fieldset>
      <div style="text-align: center; margin-top: 5px;">

        <div>
        </div>
      </div>
    </div>
    <div>
    </div>
  </div>

  <script type="text/javascript">
    ;
    var cron_page = (function($, window, document,undefined) {

      $(function () {
        let cronValue = $('#updateplanTimeConfig').val();
        $("#cron").val(cronValue);
        // cron_page.cronValue(cronValue);
      })
      //选中当前表达式
      function checkCron() {

        let cron = document.getElementById("cron").value;
        basedatamanage_eqmanage_planConfig.cron(cron);
      }
    /*killIe*/
    $.parser.parse($("body"));
    var cpro_id = "u1331261";

    function btnFan() {

      //获取参数中表达式的值
      var txt = $("#cron").val();
      if (txt) {
        var regs = txt.split(' ');
        $("input[name=v_second]").val(regs[0]);
        $("input[name=v_min]").val(regs[1]);
        $("input[name=v_hour]").val(regs[2]);
        $("input[name=v_day]").val(regs[3]);
        $("input[name=v_mouth]").val(regs[4]);
        $("input[name=v_week]").val(regs[5]);

        initObj(regs[0], "second");
        initObj(regs[1], "min");
        initObj(regs[2], "hour");
        initDay(regs[3]);
        initMonth(regs[4]);
        initWeek(regs[5]);

        if (regs.length > 6) {
          $("input[name=v_year]").val(regs[6]);
          initYear(regs[6]);
        }
      }
    }


    function initObj(strVal, strid) {
      var ary = null;
      var objRadio = $("input[name='" + strid + "'");
      if (strVal == "*") {
        objRadio.eq(0).attr("checked", "checked");
      } else if (strVal.split('-').length > 1) {
        ary = strVal.split('-');
        objRadio.eq(1).attr("checked", "checked");
        $("#" + strid + "Start_0").numberspinner('setValue', ary[0]);
        $("#" + strid + "End_0").numberspinner('setValue', ary[1]);
      } else if (strVal.split('/').length > 1) {
        ary = strVal.split('/');
        objRadio.eq(2).attr("checked", "checked");
        $("#" + strid + "Start_1").numberspinner('setValue', ary[0]);
        $("#" + strid + "End_1").numberspinner('setValue', ary[1]);
      } else {
        objRadio.eq(3).attr("checked", "checked");
        if (strVal != "?") {
          ary = strVal.split(",");
          $('#update_f_weekmask_6').prop('checked',false);
          let b = 0;
          if (strid == "second" || strid == "min") {
            b = 60;
          } else if (strid == "hour") {
            b = 24
          }
          for (let a = 1; a < b; a++) {
            $("." + strid + "List input[value='" + a + "']").prop('checked',false);
          }
          for (var i = 0; i < ary.length; i++) {
            $("." + strid + "List input[value='" + ary[i] + "']").prop('checked',true);
          }
        }
      }
    }

    function initDay(strVal) {
      var ary = null;
      var objRadio = $("input[name='day'");
      if (strVal == "*") {
        objRadio.eq(0).attr("checked", "checked");
      } else if (strVal == "?") {
        objRadio.eq(1).attr("checked", "checked");
      } else if (strVal.split('-').length > 1) {
        ary = strVal.split('-');
        objRadio.eq(2).attr("checked", "checked");
        $("#dayStart_0").numberspinner('setValue', ary[0]);
        $("#dayEnd_0").numberspinner('setValue', ary[1]);
      } else if (strVal.split('/').length > 1) {
        ary = strVal.split('/');
        objRadio.eq(3).attr("checked", "checked");
        $("#dayStart_1").numberspinner('setValue', ary[0]);
        $("#dayEnd_1").numberspinner('setValue', ary[1]);
      } else if (strVal.split('W').length > 1) {
        ary = strVal.split('W');
        objRadio.eq(4).attr("checked", "checked");
        $("#dayStart_2").numberspinner('setValue', ary[0]);
      } else if (strVal == "L") {
        objRadio.eq(5).attr("checked", "checked");
      } else {
        objRadio.eq(6).attr("checked", "checked");
        ary = strVal.split(",");
        for (let a = 1; a < 32; a++) {
          $(".dayList input[value='" + a + "']").prop('checked',false);
        }
        for (var i = 0; i < ary.length; i++) {
          $(".dayList input[value='" + ary[i] + "']").prop('checked',true);
        }
      }
    }

    function initMonth(strVal) {
      var ary = null;
      var objRadio = $("input[name='mouth'");
      if (strVal == "*") {
        objRadio.eq(0).attr("checked", "checked");
      } else if (strVal == "?") {
        objRadio.eq(1).attr("checked", "checked");
      } else if (strVal.split('-').length > 1) {
        ary = strVal.split('-');
        objRadio.eq(2).attr("checked", "checked");
        $("#mouthStart_0").numberspinner('setValue', ary[0]);
        $("#mouthEnd_0").numberspinner('setValue', ary[1]);
      } else if (strVal.split('/').length > 1) {
        ary = strVal.split('/');
        objRadio.eq(3).attr("checked", "checked");
        $("#mouthStart_1").numberspinner('setValue', ary[0]);
        $("#mouthEnd_1").numberspinner('setValue', ary[1]);

      } else {
        objRadio.eq(4).attr("checked", "checked");

        ary = strVal.split(",");
        for (let a = 1; a < 13; a++) {
          $(".mouthList input[value='" + a + "']").prop('checked',false);
        }
        for (var i = 0; i < ary.length; i++) {
          $(".mouthList input[value='" + ary[i] + "']").prop('checked',true);
        }
      }
    }

    function initWeek(strVal) {
      var ary = null;
      var objRadio = $("input[name='week'");
      if (strVal == "*") {
        objRadio.eq(0).attr("checked", "checked");
      } else if (strVal == "?") {
        objRadio.eq(1).attr("checked", "checked");
      } else if (strVal.split('/').length > 1) {
        ary = strVal.split('/');
        objRadio.eq(2).attr("checked", "checked");
        $("#weekStart_0").numberspinner('setValue', ary[0]);
        $("#weekEnd_0").numberspinner('setValue', ary[1]);
      } else if (strVal.split('-').length > 1) {
        ary = strVal.split('-');
        objRadio.eq(3).attr("checked", "checked");
        $("#weekStart_1").numberspinner('setValue', ary[0]);
        $("#weekEnd_1").numberspinner('setValue', ary[1]);
      } else if (strVal.split('L').length > 1) {
        ary = strVal.split('L');
        objRadio.eq(4).attr("checked", "checked");
        $("#weekStart_2").numberspinner('setValue', ary[0]);
      } else {
        objRadio.eq(5).attr("checked", "checked");
        ary = strVal.split(",");
        for (let a = 1; a < 8; a++) {
          $(".weekList input[value='" + a + "']").prop('checked',false);
        }
        for (var i = 0; i < ary.length; i++) {
          $(".weekList input[value='" + ary[i] + "']").attr("checked", "checked");
        }
      }
    }

    function initYear(strVal) {
      var ary = null;
      var objRadio = $("input[name='year'");
      if (strVal == "*") {
        objRadio.eq(1).attr("checked", "checked");
      } else if (strVal.split('-').length > 1) {
        ary = strVal.split('-');
        objRadio.eq(2).attr("checked", "checked");
        $("#yearStart_0").numberspinner('setValue', ary[0]);
        $("#yearEnd_0").numberspinner('setValue', ary[1]);
      }
    }

    return{
      //选中当前表达式
      checkCron : function () {

        checkCron();
      },
      btnFan: function () {
        btnFan();
      },
      cronValue: function (data) {


      }
    }

    })(jQuery, window, document);
    // eqmanage_besmodulepointtype.pageInit();
  </script>
  </body>
