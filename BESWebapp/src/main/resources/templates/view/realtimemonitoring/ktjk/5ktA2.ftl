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
                        <span>面板开关机控制</span>
                    </li>
                    <li style="width: 20%;">
                        <select id="KT_K_5_A2_1_KGKZ" onchange="dataAnalysis_ktjk2.setLampVPoint(this)">
		                   <option value="255">开</option>
		                   <option value="0">关</option>
                        </select>
                    </li>
                    <li style="width: 28%;">
                        <span>面板工作模式控制</span>
                    </li>
                    <li style="width: 20%;">
                        <select id="KT_K_5_A2_1_MSKZ" onchange="dataAnalysis_ktjk2.setLampVPoint(this)">
                            <option value="1">制冷模式</option>
                            <option value="2">制热模式</option>
                            <option value="3">手动除霜</option>
                        </select>
                    </li>

                </ul>
                

                <ul>
                    <li>
                        <span>面板1开关机</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_1_KGJ"></span>
                    </li>
                    <li>
                        <span>面板1显示板工作模式</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_1_XSBGZMS"></span>
                    </li>
					<li>
                        <span>面板1制冷进水设置温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_1_ZLJSSZWD"></span>
                    </li>
                </ul>

                 <ul>
                	<li>
                        <span>面板1故障标志</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_1_GZBZ"></span>
                    </li>
                    <li>
                        <span>室外机1模块1工作模式</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_1_GZMS"></span>
                    </li>
                    <li>
                        <span>室外机1模块1空调进水温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_1_KTJSWD"></span>
                    </li>
				</ul>
				<ul>
                	<li>
                        <span>室外机1模块1空调出水温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_1_KTCSWD"></span>
                    </li>
                    <li>
                        <span>室外机1模块1外环境温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_1_WHJWD"></span>
                    </li>
                    <li>
                        <span>室外机1模块2工作模式</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_2_GZMS"></span>
                    </li>
				</ul>
				<ul>
				    <li>
                        <span>室外机1模块2空调进水温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_2_KTJSWD"></span>
                    </li>
                	<li>
                        <span>室外机1模块2空调出水温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_2_KTCSWD"></span>
                    </li>
                    <li>
                        <span>室外机1模块2外环境温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_2_WHJWD"></span>
                    </li>
                </ul>
                <ul>
                	<li>
                        <span>室外机2模块3工作模式</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_3_GZMS"></span>
                    </li>
				    <li>
                        <span>室外机2模块3空调进水温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_3_KTJSWD"></span>
                    </li>
                	<li>
                        <span>室外机2模块3空调出水温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_3_KTCSWD"></span>
                    </li>
                    
                </ul>
                <ul>
                	<li>
                        <span>室外机2模块3外环境温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_3_WHJWD"></span>
                    </li>
				   <li>
                        <span>室外机2模块4工作模式</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_4_GZMS"></span>
                    </li>
				    <li>
                        <span>室外机2模块4空调进水温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_4_KTJSWD"></span>
                    </li>
                </ul>
                 <ul>
                	<li>
                        <span>室外机2模块4空调出水温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_4_KTCSWD"></span>
                    </li>
				    <li>
                        <span>室外机2模块4外环境温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_4_WHJWD"></span>
                    </li>
				   <li>
                        <span>室外机3模块5工作模式</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_3_GZMS"></span>
                    </li>
                </ul>
                <ul>
				    <li>
                        <span>室外机3模块5空调进水温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_5_KTJSWD"></span>
                    </li>
                	<li>
                        <span>室外机3模块5空调出水温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_5_KTCSWD"></span>
                    </li>
                    <li>
                        <span>室外机3模块5外环境温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_5_WHJWD"></span>
                    </li>
                </ul>
                <ul>
                	<li>
                        <span>室外机3模块6工作模式</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_6_GZMS"></span>
                    </li>
				    <li>
                        <span>室外机3模块6空调进水温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_6_KTJSWD"></span>
                    </li>
                	<li>
                        <span>室外机3模块6空调出水温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_6_KTCSWD"></span>
                    </li>
                    
                </ul>
                <ul>

				 
                </ul>
				
            </div>
        </div>
        
        <!--状态 -->
        <div class="ktjk_top_right">
            <div class="ktjk_top_right_all">
                 <ul>
                     <li>
                         <span>室外机3模块6外环境温度</span>
                     </li>
                     <li>
                         <span id="KT_K_5_A2_1_6_WHJWD"></span>
                     </li>
				   <li>
                        <span>室外机4模块7工作模式</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_7_GZMS"></span>
                    </li>
				    <li>
                        <span>室外机4模块7空调进水温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_7_KTJSWD"></span>
                    </li>

                </ul>  

                 
                 <ul>
                	<li>
                        <span>室外机4模块7空调出水温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_7_KTCSWD"></span>
                    </li>
				    <li>
                        <span>室外机4模块7外环境温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_7_WHJWD"></span>
                    </li>
				   <li>
                        <span>室外机4模块8工作模式</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_8_GZMS"></span>
                    </li>
                </ul>
                <ul>
				    <li>
                        <span>室外机4模块8空调进水温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_8_KTJSWD"></span>
                    </li>
                	<li>
                        <span>室外机4模块8空调出水温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_8_KTCSWD"></span>
                    </li>
                    <li>
                        <span>室外机4模块8外环境温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_8_WHJWD"></span>
                    </li>
                </ul>
                <ul>
                	<li>
                        <span>室外机5模块9工作模式</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_9_GZMS"></span>
                    </li>
				    <li>
                        <span>室外机5模块9空调进水温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_9_KTJSWD"></span>
                    </li>
                	<li>
                        <span>室外机5模块9空调出水温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_9_KTCSWD"></span>
                    </li>
                    
                </ul>
                
                <ul>
                	<li>
                        <span>室外机5模块9外环境温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_9_WHJWD"></span>
                    </li>
				   <li>
                        <span>室外机5模块10工作模式</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_10_GZMS"></span>
                    </li>
				    <li>
                        <span>室外机5模块10空调进水温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_10_KTJSWD"></span>
                    </li>
                </ul>  

                 
                 <ul>
                	<li>
                        <span>室外机5模块10空调出水温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_10_KTCSWD"></span>
                    </li>
				    <li>
                        <span>室外机5模块10外环境温度</span>
                    </li>
                    <li>
                        <span id="KT_K_5_A2_1_10_WHJWD"></span>
                    </li>
				   
                </ul>
                
                 
            </div>
        </div>
        
    </div>
</div>
