<style type="text/css">

</style>

<div class="Information_area"
	 style="background: url('static/images/ktjk/ktjk1.png'); position: relative;background-size: 53% 34%; background-repeat: no-repeat;background-position: bottom right;">
    <div class="ktjk_row" >
    <!--开关 -->
        <div class="ktjk_top_left3">
            <div class="ktjk_top_right_all">
                <ul>
                    <li style="width: 28%;">
                        <span>旁通阀控制</span>
                    </li>
                    <#--<li style="width: 20%;">
                        <select id="KT_YWA5_DDC1_M108" onchange="dataAnalysis_ktjk2.setWaterPoint(this)">
		                   <option value="255">开</option>
		                   <option value="0">关</option>
                        </select>
                    </li>-->
                    <li style="width: 20%;">
                        <input type="number" id="KT_SAW_DDC2_M108" class="form-control number" style="width:40%;display: inline-block;" >
                        <span class="input-group-btn" style="display: inline-block;">
                        <button  type="button" class="btn btn-primary" ids="SAW_DDC2_M108" onclick="dataAnalysis_ktjk2.setWaterPoint(this)">设置</button></span>
                    </li>
                    <li>
                        <span>旁通阀反馈</span>
                    </li>
                    <li>
                        <span id="KT_SAW_DDC2_M1013"></span>
                    </li>

                    


                </ul>
                	
                <ul>
                <li style="width: 28%;">
                        <span>水泵1开度控制</span>
                    </li>
                    <li style="width: 20%;">
                        <input type="number" id="KT_SAW_DDC2_M208" class="form-control number" style="width:40%;display: inline-block;" >
                        <span class="input-group-btn" style="display: inline-block;">
                        <button  type="button" class="btn btn-primary" ids="SAW_DDC2_M208" onclick="dataAnalysis_ktjk2.setWaterPoint(this)">设置</button></span>
                    </li>
                    <li>
                        <span>水泵1开度反馈</span>
                    </li>
                    <li>
                        <span id="KT_SAW_DDC2_M2011"></span>
                    </li>
                </ul>
                
                 <ul>

                     <li style="width: 28%;">
                         <span>水泵2开度控制</span>
                     </li>
                     <li style="width: 20%;">
                         <input type="number" id="KT_SAW_DDC2_M209" class="form-control number" style="width:40%;display: inline-block;" >
                         <span class="input-group-btn" style="display: inline-block;">
                        <button type="button" class="btn btn-primary" ids="SAW_DDC2_M209" onclick="dataAnalysis_ktjk2.setWaterPoint(this)">设置</button></span>
                     </li>
					<li>
                        <span>水泵2开度反馈</span>
                    </li>
                    <li>
                        <span id="KT_SAW_DDC2_M2012"></span>
                    </li>
                    
                </ul>
                
                <ul>
                <li style="width: 28%">
                        <span>水泵3开度控制</span>
                    </li>
                    <li style="width: 20%;">
                        <input type="number" id="KT_SAW_DDC2_M2010" class="form-control number" style="width:40%;display: inline-block;" >
                        <span class="input-group-btn" style="display: inline-block;">
                        <button type="button" class="btn btn-primary" ids="SAW_DDC2_M2010" onclick="dataAnalysis_ktjk2.setWaterPoint(this)">设置</button></span>
                    </li>
                    <li>
                        <span>水泵3开度反馈</span>
                    </li>
                    <li>
                        <span id="KT_SAW_DDC2_M2013"></span>
                    </li>
                </ul>

                 <ul>

                     <li style="width: 28%;">
                         <span>室外机水阀1开度控制</span>
                     </li>
                     <li style="width: 20%;">
                         <input type="number" id="KT_SAW_DDC1_M104" class="form-control number" style="width:40%;display: inline-block;" >
                         <span class="input-group-btn" style="display: inline-block;">
                        <button type="button" class="btn btn-primary" ids="SAW_DDC1_M104" onclick="dataAnalysis_ktjk2.setWaterPoint(this)">设置</button></span>
                     </li>
                     <li>
                        <span>室外机水阀1开度反馈</span>
                    </li>
                    <li>
                        <span id="KT_SAW_DDC1_M108"></span>
                    </li>
                </ul>
                
                <ul>
                <li style="width: 28%">
                        <span>室外机水阀2开度控制</span>
                    </li>
                    <li style="width: 20%;">
                        <input type="number" id="KT_SAW_DDC1_M105" class="form-control number" style="width:40%;display: inline-block;" >
                        <span class="input-group-btn" style="display: inline-block;">
                        <button type="button" class="btn btn-primary" ids="SAW_DDC1_M105" onclick="dataAnalysis_ktjk2.setWaterPoint(this)">设置</button></span>
                    </li>
                    <li>
                        <span>室外机水阀2开度反馈</span>
                    </li>
                    <li>
                        <span id="KT_SAW_DDC1_M109"></span>
                    </li>
                </ul>

                 <ul>

                     <li style="width: 28%;">
                         <span>室外机水阀3开度控制</span>
                     </li>
                     <li style="width: 20%;">
                         <input type="number" id="KT_SAW_DDC1_M106" class="form-control number" style="width:40%;display: inline-block;" >
                         <span class="input-group-btn" style="display: inline-block;">
                        <button type="button" class="btn btn-primary" ids="SAW_DDC1_M106" onclick="dataAnalysis_ktjk2.setWaterPoint(this)">设置</button></span>
                     </li>
                     
                     <li>
                        <span>室外机水阀3开度反馈</span>
                    </li>
                    <li>
                        <span id="KT_SAW_DDC1_M1010"></span>
                    </li>
                </ul>
                
                <ul>
                <li style="width: 28%">
                        <span>室外机水阀4开度控制</span>
                    </li>
                    <li style="width: 20%;">
                        <input type="number" id="KT_SAW_DDC1_M204" class="form-control number" style="width:40%;display: inline-block;" >
                        <span class="input-group-btn" style="display: inline-block;">
                        <button type="button" class="btn btn-primary" ids="SAW_DDC1_M204" onclick="dataAnalysis_ktjk2.setWaterPoint(this)">设置</button></span>
                    </li>
                    <li>
                        <span>室外机水阀4开度反馈</span>
                    </li>
                    <li>
                        <span id="KT_SAW_DDC1_M208"></span>
                    </li>
                </ul>
                
                 <ul>
                     <li style="width: 28%;">
                         <span>室外机水阀5开度控制</span>
                     </li>
                     <li style="width: 20%;">
                         <input type="number" id="KT_SAW_DDC1_M205" class="form-control number" style="width:40%;display: inline-block;" >
                         <span class="input-group-btn" style="display: inline-block;">
                        <button type="button" class="btn btn-primary" ids="SAW_DDC1_M205" onclick="dataAnalysis_ktjk2.setWaterPoint(this)">设置</button></span>
                     </li>
                     <li>
                        <span>室外机水阀5开度反馈</span>
                    </li>
                    <li>
                        <span id="KT_SAW_DDC1_M209"></span>
                    </li>
                </ul>
                
                <ul>
                <li style="width: 28%">
                        <span>室外机水阀6开度控制</span>
                    </li>
                    <li style="width: 20%;">
                        <input type="number" id="KT_SAW_DDC1_M206" class="form-control number" style="width:40%;display: inline-block;" >
                        <span class="input-group-btn" style="display: inline-block;">
                        <button type="button" class="btn btn-primary" ids="SAW_DDC1_M206" onclick="dataAnalysis_ktjk2.setWaterPoint(this)">设置</button></span>
                    </li>
                    <li>
                        <span>室外机水阀6开度反馈</span>
                    </li>
                    <li>
                        <span id="KT_SAW_DDC1_M2010"></span>
                    </li>
                </ul>
                
                 <ul>
                     <li style="width: 28%;">
                         <span>室外机水阀7开度控制</span>
                     </li>
                     <li style="width: 20%;">
                         <input type="number" id="KT_SAW_DDC1_M207" class="form-control number" style="width:40%;display: inline-block;" >
                         <span class="input-group-btn" style="display: inline-block;">
                        <button type="button" class="btn btn-primary" ids="SAW_DDC1_M207" onclick="dataAnalysis_ktjk2.setWaterPoint(this)">设置</button></span>
                     </li>
                     <li>
                        <span>室外机水阀7开度反馈</span>
                    </li>
                    <li>
                        <span id="KT_SAW_DDC1_M2011"></span>
                    </li>
                </ul>
                
                <ul>
                     <li style="width: 28%;">
                         <span>室外机水阀8开度控制</span>
                     </li>
                     <li style="width: 20%;">
                         <input type="number" id="KT_SAW_DDC1_M304" class="form-control number" style="width:40%;display: inline-block;" >
                         <span class="input-group-btn" style="display: inline-block;">
                        <button type="button" class="btn btn-primary" ids="SAW_DDC1_M304" onclick="dataAnalysis_ktjk2.setWaterPoint(this)">设置</button></span>
                     </li>
                     <li>
                        <span>室外机水阀8开度反馈</span>
                    </li>
                    <li>
                        <span id="KT_SAW_DDC1_M308"></span>
                    </li>
                </ul>
                
                <ul>
                     <li style="width: 28%;">
                         <span>室外机水阀9开度控制</span>
                     </li>
                     <li style="width: 20%;">
                         <input type="number" id="KT_SAW_DDC1_M305" class="form-control number" style="width:40%;display: inline-block;" >
                         <span class="input-group-btn" style="display: inline-block;">
                        <button type="button" class="btn btn-primary" ids="SAW_DDC1_M305" onclick="dataAnalysis_ktjk2.setWaterPoint(this)">设置</button></span>
                     </li>
                     <li>
                        <span>室外机水阀9开度反馈</span>
                    </li>
                    <li>
                        <span id="KT_SAW_DDC1_M309"></span>
                    </li>
                </ul>
                
                
                
                
            </div>
        </div>
    <!--状态 -->
        <div class="ktjk_top_right">
            <div class="ktjk_top_right_all">
				
                
                
                <ul>
                     <li style="width: 28%;">
                         <span>室外机水阀10开度控制</span>
                     </li>
                     <li style="width: 20%;">
                         <input type="number" id="KT_SAW_DDC1_M404" class="form-control number" style="width:40%;display: inline-block;" >
                         <span class="input-group-btn" style="display: inline-block;">
                        <button type="button" class="btn btn-primary" ids="SAW_DDC1_M404" onclick="dataAnalysis_ktjk2.setWaterPoint(this)">设置</button></span>
                     </li>
                     <li>
                        <span>室外机水阀10开度反馈</span>
                    </li>
                    <li>
                        <span id="KT_SAW_DDC1_M408"></span>
                    </li>
                </ul>
                <ul>
                     <li style="width: 28%;">
                         <span>室外机水阀11开度控制</span>
                     </li>
                     <li style="width: 20%;">
                         <input type="number" id="KT_SAW_DDC1_M405" class="form-control number" style="width:40%;display: inline-block;" >
                         <span class="input-group-btn" style="display: inline-block;">
                        <button type="button" class="btn btn-primary" ids="SAW_DDC1_M405" onclick="dataAnalysis_ktjk2.setWaterPoint(this)">设置</button></span>
                     </li>
                     <li>
                        <span>室外机水阀11开度反馈</span>
                    </li>
                    <li>
                        <span id="KT_SAW_DDC1_M409"></span>
                    </li>
                </ul>
                
                <ul>
                     <li style="width: 28%;">
                         <span>室外机水阀12开度控制</span>
                     </li>
                     <li style="width: 20%;">
                         <input type="number" id="KT_SAW_DDC1_M406" class="form-control number" style="width:40%;display: inline-block;" >
                         <span class="input-group-btn" style="display: inline-block;">
                        <button type="button" class="btn btn-primary" ids="SAW_DDC1_M406" onclick="dataAnalysis_ktjk2.setWaterPoint(this)">设置</button></span>
                     </li>
                     <li>
                        <span>室外机水阀12开度反馈</span>
                    </li>
                    <li>
                        <span id="KT_SAW_DDC1_M4010"></span>
                    </li>
                </ul>
                
                <ul>
                     <li style="width: 28%;">
                         <span>室外机水阀13开度控制</span>
                     </li>
                     <li style="width: 20%;">
                         <input type="number" id="KT_SAW_DDC1_M407" class="form-control number" style="width:40%;display: inline-block;" >
                         <span class="input-group-btn" style="display: inline-block;">
                        <button type="button" class="btn btn-primary" ids="SAW_DDC1_M407" onclick="dataAnalysis_ktjk2.setWaterPoint(this)">设置</button></span>
                     </li>
                     <li>
                        <span>室外机水阀13开度反馈</span>
                    </li>
                    <li>
                        <span id="KT_SAW_DDC1_M4011"></span>
                    </li>
                </ul>
                
                <ul>
                     <li style="width: 28%;">
                         <span>室外机水阀14开度控制</span>
                     </li>
                     <li style="width: 20%;">
                         <input type="number" id="KT_SAW_DDC1_M504" class="form-control number" style="width:40%;display: inline-block;" >
                         <span class="input-group-btn" style="display: inline-block;">
                        <button type="button" class="btn btn-primary" ids="SAW_DDC1_M504" onclick="dataAnalysis_ktjk2.setWaterPoint(this)">设置</button></span>
                     </li>
                     <li>
                        <span>室外机水阀14开度反馈</span>
                    </li>
                    <li>
                        <span id="KT_SAW_DDC1_M508"></span>
                    </li>
                </ul>
                
                <ul>
                     <li style="width: 28%;">
                         <span>室外机水阀15开度控制</span>
                     </li>
                     <li style="width: 20%;">
                         <input type="number" id="KT_SAW_DDC1_M505" class="form-control number" style="width:40%;display: inline-block;" >
                         <span class="input-group-btn" style="display: inline-block;">
                        <button type="button" class="btn btn-primary" ids="SAW_DDC1_M505" onclick="dataAnalysis_ktjk2.setWaterPoint(this)">设置</button></span>
                     </li>
                     <li>
                        <span>室外机水阀15开度反馈</span>
                    </li>
                    <li>
                        <span id="KT_SAW_DDC1_M509"></span>
                    </li>
                </ul>
                <ul>
                    <li>
                        <span>室外温度</span>
                    </li>
                    <li>
                        <span id="KT_SAW_DDC2_M103"></span>
                    </li>

                    <li>
                        <span>室外湿度</span>
                    </li>
                    <li>
                        <span id="KT_SAW_DDC2_M104"></span>
                    </li>

                    <li>
                        <span>供水温度</span>
                    </li>
                    <li>
                        <span id="KT_SAW_DDC2_M105"></span>
                    </li>

                </ul>

                <ul>
                	<li>
                        <span>供水压力</span>
                    </li>
                    <li>
                        <span id="KT_SAW_DDC2_M106"></span>
                    </li>

                    <li>
                        <span>回水流量</span>
                    </li>
                    <li>
                        <span id="KT_SAW_DDC2_M107"></span>
                    </li>

                    <li>
                        <span>回水温度</span>
                    </li>
                    <li>
                        <span id="KT_SAW_DDC2_M1011"></span>
                    </li>

                </ul>

                <ul>
                	<li>
                        <span>回水压力</span>
                    </li>
                    <li>
                        <span id="KT_SAW_DDC2_M1012"></span>
                    </li>

                	

                </ul>
            </div>
        </div>
    </div>


</div>
