package com.efounder.JEnterprise.service.basedatamanage.eqmanage.impl;

import com.alibaba.fastjson.JSONObject;
import com.core.common.ISSPReturnObject;
import com.efounder.JEnterprise.commhandler.MsgSubPubHandler;
import com.efounder.JEnterprise.common.util.UUIDUtil;
import com.efounder.JEnterprise.domain.SysJob;
import com.efounder.JEnterprise.initializer.PlanCache;
import com.efounder.JEnterprise.initializer.PlanVariatInfoCache;
import com.efounder.JEnterprise.initializer.SbPzStructCache;
import com.efounder.JEnterprise.initializer.TimeTaskCache;
import com.efounder.JEnterprise.mapper.basedatamanage.eqmanage.BesplanConfigMapper;
import com.efounder.JEnterprise.mapper.quartz.SysJobPlanMapper;
import com.efounder.JEnterprise.model.basedatamanage.eqmanage.*;
import com.efounder.JEnterprise.service.basedatamanage.eqmanage.BesplanConfigService;
import com.efounder.JEnterprise.service.quartz.SysJobPlanTaskService;
import com.efounder.constants.ScheduleConstants;
import com.efounder.util.ScheduleUtils;
import org.ace.business.constant.DDCCmd;
import org.ace.business.constant.LDCCmd;
import org.ace.business.dto.ddc.PlanParamDDC;
import org.ace.business.dto.ldc.PlanParamLDC;
import org.ace.business.handler.SendMsgHandler;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.xml.rpc.ServiceException;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.efounder.util.ScheduleUtils.getJobKey;

/**
 * @author sunzhiyuan
 * @Data 2020/9/29 9:34
 */
@Service("BesplanConfigService")
public class BesplanConfigServiceImpl implements BesplanConfigService {

    @Autowired
    private BesplanConfigMapper besplanConfigMapper;

    @Autowired
    private CrossEquipmentServiceImpl crossequipmentservice;

    @Autowired
    private PlanVariatInfoCache planVariatInfoCache;

    @Autowired
    BesplanConfigService besplanConfigService;

    @Autowired
    private SysJobPlanMapper sysJobPlanMapper;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    SysJobPlanTaskService sysJobPlanTaskService;

    //???????????????
    @Autowired
    SbPzStructCache sbPzStructCache;

    //????????????
    @Autowired
    PlanCache planCache;

    //??????????????????
    @Autowired
    TimeTaskCache timeTaskCache;

    /**
     * ???????????????1?????????(??????????????????0??????????????????????????????0)
     * @param col ?????????????????????
     * @return
     */
    private String getAutoIncreaseCol(String col) {
        if (col == null || "".equals(col)) {
            return "1";
        }
        String regex = "^([0]+)([\\d]*)";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(new StringBuffer(col));
        if (matcher.find()) {
            return matcher.group(1) + (Integer.parseInt(matcher.group(2)) + 1);
        } else {
            return String.valueOf(Integer.parseInt(col) + 1);
        }
    }

    @Override
    public ISSPReturnObject getPlanTree() {

        ISSPReturnObject returnObject = new ISSPReturnObject();

        List<BesPlanInfo> returnList = besplanConfigMapper.getPlanTree();

        returnObject.setList(returnList);
        returnObject.setStatus("1");
        return returnObject;
    }

    @Override
    public ISSPReturnObject insertContPlan(BesPlanInfo besPlanInfo) {

        ISSPReturnObject returnObject = new ISSPReturnObject();

        //????????????Id
        String maxId = besplanConfigMapper.queryMaxId();

        besPlanInfo.setF_id(getAutoIncreaseCol(maxId));

        Boolean flag = besplanConfigMapper.insertConplan(besPlanInfo);

        int id = Integer.parseInt(maxId);

        Integer returnId = id + 1;

        if (flag){
            returnObject.setMsg("????????????????????????");
            returnObject.setStatus("1");
            returnObject.setData(returnId);
        }else {
            returnObject.setMsg("????????????????????????");
            returnObject.setStatus("0");
        }
        return returnObject;
    }

    @Override
    public ISSPReturnObject insertCollectPlan(BesPlanInfo besPlanInfo) {

        ISSPReturnObject returnObject = new ISSPReturnObject();

        //????????????Id
        String maxId = besplanConfigMapper.queryMaxId();

        besPlanInfo.setF_id(getAutoIncreaseCol(maxId));

        Boolean flag = besplanConfigMapper.insertCollectPlan(besPlanInfo);

        int id = Integer.parseInt(maxId);

        Integer returnId = id + 1;

        if (flag){
            returnObject.setMsg("????????????????????????");
            returnObject.setStatus("1");
            returnObject.setData(returnId);
        }else {
            returnObject.setMsg("????????????????????????");
            returnObject.setStatus("0");
        }

        return returnObject;
    }


    @Override
    public ISSPReturnObject editPlanInfo(BesPlanInfo besPlanInfo) {

        ISSPReturnObject returnObject = new ISSPReturnObject();

        Boolean flag = besplanConfigMapper.editPlanInfo(besPlanInfo);

        if (flag){
            returnObject.setMsg("??????????????????");
            returnObject.setStatus("1");
        }else {
            returnObject.setMsg("??????????????????");
            returnObject.setStatus("0");
        }
        return returnObject;
    }

    @Override
    public ISSPReturnObject deletePlanInfo(String id) {

        ISSPReturnObject returnObject = new ISSPReturnObject();

        List<Map<String,Object>> list = besplanConfigMapper.selectPlanInfomation(id);

        if(list != null && !list.isEmpty()) {
            list.forEach(planInfo -> {
                besplanConfigService.deletePlanInfomation((String) planInfo.get("f_id"));
            });
        }

        Boolean flag = besplanConfigMapper.deletePlanInfo(id);

        if (flag){
            returnObject.setMsg("??????????????????");
            returnObject.setStatus("1");
        }else {
            returnObject.setMsg("??????????????????");
            returnObject.setStatus("0");
        }

        return returnObject;
    }


    @Override
    public ISSPReturnObject getSceneTreeFromPlan() {
        ISSPReturnObject returnObject = new ISSPReturnObject();

        List<BeSceneInfo> returnList = besplanConfigMapper.getSceneTreeFromPlan();

        returnObject.setData(returnList);
        returnObject.setStatus("1");
        return returnObject;
    }


    /**
     *
     * @Description: ??????????????????
     *
     * @auther: wanghongjie
     * @date: 10:39 2021/8/14
     * @param: [object]
     * @return: com.core.common.ISSPReturnObject
     *
     */
    @Override
    public ISSPReturnObject insertPlanInfo(JSONObject object) throws ParseException {

        ISSPReturnObject returnObject = new ISSPReturnObject();
        String f_node_attribution = null;//???????????????cross1???lamp2???energy3
        String channelID = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat HMSsdf = new SimpleDateFormat("HH:mm:ss");

        String sceneId = (String) object.get("f_sceneInfoId");//??????id
        String modeId = (String) object.get("f_modeInfoId");//??????id
        String planType = (String) object.get("f_planType");//?????????  ????????????(0)??????????????????(1)
        String executionWay = (String) object.get("f_executionWay");//???????????????????????????(0)???????????????(1)???

        String newStartTime = object.get("f_startday") + " " + object.get("f_startime");//?????????????????????
        String newEndTime = object.get("f_enday") + " " + object.get("f_endtime");//?????????????????????
        Date newStartDate = sdf.parse(newStartTime);
        Date newEndDate = sdf.parse(newEndTime);
        Date HMSnewStartDate = HMSsdf.parse((String) object.get("f_startime"));
        Date HMSnewEndDate = HMSsdf.parse((String) object.get("f_endtime"));

        List<Map<String,Object>> pointDateMap = besplanConfigMapper.selectScenePointBymodeId(modeId);//????????????id?????????????????????????????????

        if (pointDateMap.size() == 0) {
            returnObject.setStatus("0");
            returnObject.setMsg("??????????????????????????????,?????????!");
            return returnObject;
        }

        //??????????????????????????????
//        BesPlan besPlanList1 = besplanConfigMapper.selectPlanByModeId(modeId,object.getString("f_scenetype"));
//        if (besPlanList1 != null) {
//            returnObject.setStatus("0");
//            returnObject.setMsg("?????????????????????,???????????????");
//        }

        //????????????id?????????????????????
        List<BesPlan> besPlanList = planCache.selectPlanBySceneIdCache(sceneId);
        if (besPlanList != null && besPlanList.size() > 0) {

            Boolean besPlanType = false;
            for (BesPlan besPlan: besPlanList) {
                //????????????????????????????????????????????????
                if ("0".equals(besPlan.getF_planType())) {
                        besPlanType = true;
                }
            }
            if (!besPlanType){
                if ("1".equals(planType)) {
                    returnObject.setStatus("0");
                    returnObject.setMsg("??????????????????????????????,?????????!");
                    return returnObject;
                }
            }

            for (BesPlan besPlan: besPlanList) {
                //????????????????????????
                String startTime = besPlan.getF_startday() + " " + besPlan.getF_startime();//????????????
                String endTime = besPlan.getF_enday() + " " + besPlan.getF_endtime();//????????????
                Date startDate = sdf.parse(startTime);
                Date endDate = sdf.parse(endTime);
                Date HMSstartDate = HMSsdf.parse(besPlan.getF_startime());
                Date HMSendDate = HMSsdf.parse(besPlan.getF_endtime());

                Calendar newBeginDate = Calendar.getInstance();
                newBeginDate.setTime(newStartDate);

                Calendar newEndingDate = Calendar.getInstance();
                newEndingDate.setTime(newEndDate);

                Calendar begin = Calendar.getInstance();
                begin.setTime(startDate);

                Calendar end = Calendar.getInstance();
                end.setTime(endDate);

                Calendar HMSnewBeginDate = Calendar.getInstance();
                HMSnewBeginDate.setTime(HMSnewStartDate);

                Calendar HMSnewEndingDate = Calendar.getInstance();
                HMSnewEndingDate.setTime(HMSnewEndDate);

                Calendar HMSbegin = Calendar.getInstance();
                HMSbegin.setTime(HMSstartDate);

                Calendar HMSend = Calendar.getInstance();
                HMSend.setTime(HMSendDate);

                if ("0".equals(planType)) {//????????????
                    if ("0".equals(besPlan.getF_planType())) {//????????????

                        returnObject = judgmentTime(newBeginDate,newEndingDate,begin,end,executionWay,HMSnewBeginDate,HMSnewEndingDate,HMSbegin,HMSend, planType);

                        if ("0".equals(returnObject.getStatus())) {//??????,????????????

                            return returnObject;
                        }
                    }

                } else {//???????????????

                    if ("1".equals(besPlan.getF_planType())) {//???????????????

                        returnObject = judgmentTime(newBeginDate,newEndingDate,begin,end,executionWay,HMSnewBeginDate,HMSnewEndingDate,HMSbegin,HMSend, planType);

                        if ("0".equals(returnObject.getStatus())) {//??????,????????????

                            return returnObject;
                        }
                    }
                }
            }
        } else {
            if ("1".equals(planType)) {
                returnObject.setStatus("0");
                returnObject.setMsg("??????????????????????????????,?????????!");
                return returnObject;
            }
        }


        String f_id = (String) pointDateMap.get(0).get("f_pointId");

        BESSbPzStruct besSbPzStruct = sbPzStructCache.getCachedElementById(f_id);
        if (besSbPzStruct != null) {
            f_node_attribution = besSbPzStruct.getF_node_attribution();
        } else {
            returnObject.setStatus("0");
            returnObject.setMsg("???????????????????????????,?????????!");
            return returnObject;
        }

        if ("1".equals(f_node_attribution)) {//??????

            Map<String,Object> queryDDCMapByPoint = besplanConfigMapper.queryChannelByIdDDC(f_id);
            channelID = (String) queryDDCMapByPoint.get("F_CHANNEL_ID");
        } else if ("2".equals(f_node_attribution)) {//??????

            Map<String,Object> queryLDCMapByPoint = besplanConfigMapper.queryChannelByIdLDC(f_id);
            channelID = (String) queryLDCMapByPoint.get("F_CHANNEL_ID");
        }

        returnObject = insertPlanValueInfo(object);

        if ("0".equals(returnObject.getStatus())) {//????????????????????????
            return returnObject;
        }

        returnObject = insertInstructions(channelID,object,f_node_attribution);

        return returnObject;
    }

    /**
     *
     * @Description: ??????????????????
     *
     * @auther: wanghongjie
     * @date: 15:18 2021/8/17
     * @param:
     * @return:
     *
     */
    @Override
    public ISSPReturnObject updatePlanInfo(JSONObject object) throws ParseException {

        ISSPReturnObject returnObject = new ISSPReturnObject();
        String f_node_attribution = null;//???????????????cross1???lamp2???energy3
        String channelID = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat HMSsdf = new SimpleDateFormat("HH:mm:ss");

        String f_id = String.valueOf(object.get("f_id")) ;//??????id
        String sceneId = (String) object.get("f_sceneInfoId");//??????id
        String modeId = (String) object.get("f_modeInfoId");//??????id
        String planType = (String) object.get("f_planType");//?????????  ????????????(0)??????????????????(1)
        String executionWay = (String) object.get("f_execut");//???????????????????????????(0)???????????????(1)???

        String newStartTime = object.get("f_startday") + " " + object.get("f_startime");//?????????????????????
        String newEndTime = object.get("f_enday") + " " + object.get("f_endtime");//?????????????????????
        Date newStartDate = sdf.parse(newStartTime);
        Date newEndDate = sdf.parse(newEndTime);
        Date HMSnewStartDate = HMSsdf.parse((String) object.get("f_startime"));
        Date HMSnewEndDate = HMSsdf.parse((String) object.get("f_endtime"));

        List<Map<String,Object>> pointDateMap = besplanConfigMapper.selectScenePointBymodeId(modeId);//????????????id?????????????????????????????????

        if (pointDateMap.size() == 0) {
            returnObject.setStatus("0");
            returnObject.setMsg("??????????????????????????????,?????????!");
            return returnObject;
        }

        //??????????????????????????????
//        BesPlan besPlanList1 = besplanConfigMapper.selectPlanByModeId(modeId,object.getString("f_scenetype"));
//        if (besPlanList1 != null) {
//            returnObject.setStatus("0");
//            returnObject.setMsg("?????????????????????,???????????????");
//        }

        //????????????id?????????????????????
        List<BesPlan> besPlanList = planCache.selectPlanBySceneIdCache(sceneId);
        if (besPlanList != null && besPlanList.size() > 0) {

            Boolean besPlanType = false;
            for (BesPlan besPlan: besPlanList) {
                //????????????????????????????????????????????????
                if ("0".equals(besPlan.getF_planType())) {
                    if (f_id.equals(besPlan.getF_id())) {
                        if ("0".equals(planType)) {
                            besPlanType = true;
                        }
                    } else {
                        besPlanType = true;
                    }

                }
            }
            if (!besPlanType){
                if (!"0".equals(planType)) {
                    returnObject.setStatus("0");
                    returnObject.setMsg("??????????????????????????????,?????????!");
                    return returnObject;
                }
            }

            for (BesPlan besPlan: besPlanList) {



                if (f_id.equals(besPlan.getF_id())) {
                    continue;
                }
                //????????????????????????
                String startTime = besPlan.getF_startday() + " " + besPlan.getF_startime();//????????????
                String endTime = besPlan.getF_enday() + " " + besPlan.getF_endtime();//????????????
                Date startDate = sdf.parse(startTime);
                Date endDate = sdf.parse(endTime);
                Date HMSstartDate = HMSsdf.parse(besPlan.getF_startime());
                Date HMSendDate = HMSsdf.parse(besPlan.getF_endtime());

                Calendar newBeginDate = Calendar.getInstance();
                newBeginDate.setTime(newStartDate);

                Calendar newEndingDate = Calendar.getInstance();
                newEndingDate.setTime(newEndDate);

                Calendar begin = Calendar.getInstance();
                begin.setTime(startDate);

                Calendar end = Calendar.getInstance();
                end.setTime(endDate);

                Calendar HMSnewBeginDate = Calendar.getInstance();
                HMSnewBeginDate.setTime(HMSnewStartDate);

                Calendar HMSnewEndingDate = Calendar.getInstance();
                HMSnewEndingDate.setTime(HMSnewEndDate);

                Calendar HMSbegin = Calendar.getInstance();
                HMSbegin.setTime(HMSstartDate);

                Calendar HMSend = Calendar.getInstance();
                HMSend.setTime(HMSendDate);

                if ("0".equals(planType)) {//????????????
                    if ("0".equals(besPlan.getF_planType())) {//????????????

                        returnObject = judgmentTime(newBeginDate,newEndingDate,begin,end,executionWay,HMSnewBeginDate,HMSnewEndingDate,HMSbegin,HMSend,planType);

                        if ("0".equals(returnObject.getStatus())) {//??????,????????????

                            return returnObject;
                        }
                    }

                } else {//???????????????

                    if ("1".equals(besPlan.getF_planType())) {//???????????????

                        returnObject = judgmentTime(newBeginDate,newEndingDate,begin,end,executionWay,HMSnewBeginDate,HMSnewEndingDate,HMSbegin,HMSend,planType);

                        if ("0".equals(returnObject.getStatus())) {//??????,????????????

                            return returnObject;
                        }
                    }
                }
            }
        }


        String f_pointId = (String) pointDateMap.get(0).get("f_pointId");

        BESSbPzStruct besSbPzStruct = sbPzStructCache.getCachedElementById(f_pointId);
        if (besSbPzStruct != null) {
            f_node_attribution = besSbPzStruct.getF_node_attribution();
        } else {
            returnObject.setStatus("0");
            returnObject.setMsg("???????????????????????????,?????????!");
            return returnObject;
        }

        if ("1".equals(f_node_attribution)) {//??????

            Map<String,Object> queryDDCMapByPoint = besplanConfigMapper.queryChannelByIdDDC(f_pointId);
            channelID = (String) queryDDCMapByPoint.get("F_CHANNEL_ID");
        } else if ("2".equals(f_node_attribution)) {//??????

            Map<String,Object> queryLDCMapByPoint = besplanConfigMapper.queryChannelByIdLDC(f_pointId);
            channelID = (String) queryLDCMapByPoint.get("F_CHANNEL_ID");
        }

        returnObject = updatePlanValueInfo(object);

        if ("0".equals(returnObject.getStatus())) {//????????????????????????
            return returnObject;
        }

        returnObject = updateInstructions(channelID,object,f_node_attribution,"??????");

        return returnObject;
    }

    /**
     *
     * @Description: ????????????
     *
     * @auther: wanghongjie
     * @date: 14:10 2021/8/17
     * @param: []
     * @return: com.core.common.ISSPReturnObject
     *
     * @param newBeginDate
     * @param newEndingDate
     * @param begin
     * @param end
     * @param executionWay
     * @param planType    */
    public ISSPReturnObject judgmentTime(Calendar newBeginDate, Calendar newEndingDate, Calendar begin, Calendar end, String executionWay,
                                         Calendar HMSnewBeginDate, Calendar HMSnewEndingDate, Calendar HMSbegin, Calendar HMSend, String planType){
        ISSPReturnObject returnObject = new ISSPReturnObject();
        String msg = null;
        if ("0".equals(planType)) {
            msg = "??????";
        } else {
            msg = "?????????";
        }
        if ((newBeginDate.after(begin) && newBeginDate.before(end)) || newBeginDate.equals(begin) || newBeginDate.equals(end)) {
            if ("0".equals(executionWay)){//????????????
                returnObject = HMSjudgmentTime(HMSnewBeginDate,HMSnewEndingDate,HMSbegin,HMSend,msg);
                if ("0".equals(returnObject.getStatus())) {//??????
                    return returnObject;
                }
            } else {
                returnObject.setStatus("0");
                returnObject.setMsg("?????????????????????" +  msg  + "???????????????????????????,???????????????");
                return returnObject;
            }
        }
        if (begin.after(newBeginDate) && begin.before(newEndingDate)) {

            if ("0".equals(executionWay)){//????????????
                returnObject = HMSjudgmentTime(HMSnewBeginDate,HMSnewEndingDate,HMSbegin,HMSend,msg);
                if ("0".equals(returnObject.getStatus())) {//??????
                    return returnObject;
                }
            } else {
                returnObject.setStatus("0");
                returnObject.setMsg("?????????????????????" +  msg  + "???????????????????????????,???????????????");
                return returnObject;
            }
        }
        if (end.after(newBeginDate) && end.before(newEndingDate)) {
            if ("0".equals(executionWay)){//????????????
                returnObject = HMSjudgmentTime(HMSnewBeginDate,HMSnewEndingDate,HMSbegin,HMSend,msg);
                if ("0".equals(returnObject.getStatus())) {//??????
                    return returnObject;
                }
            } else {
                returnObject.setStatus("0");
                returnObject.setMsg("?????????????????????" +  msg  + "???????????????????????????,???????????????");
                return returnObject;
            }
        }
        if ((newEndingDate.after(begin) && newEndingDate.before(end)) || newEndingDate.equals(begin) || newEndingDate.equals(end)) {

            if ("0".equals(executionWay)){//????????????
                returnObject = HMSjudgmentTime(HMSnewBeginDate,HMSnewEndingDate,HMSbegin,HMSend,msg);
                if ("0".equals(returnObject.getStatus())) {//??????
                    return returnObject;
                }
            } else {
                returnObject.setStatus("0");
                returnObject.setMsg("?????????????????????" +  msg  + "???????????????????????????,???????????????");
                return returnObject;
            }
        }
        return returnObject;

    }

    public ISSPReturnObject HMSjudgmentTime(Calendar HMSnewBeginDate, Calendar HMSnewEndingDate, Calendar HMSbegin,
                                            Calendar HMSend, String msg){
        ISSPReturnObject returnObject = new ISSPReturnObject();
        if ((HMSnewBeginDate.after(HMSbegin) && HMSnewBeginDate.before(HMSend)) || HMSnewBeginDate.equals(HMSbegin) || HMSnewBeginDate.equals(HMSend)) {
            returnObject.setStatus("0");
            returnObject.setMsg("?????????????????????" +  msg  + "???????????????????????????,???????????????");
            return returnObject;
        }
        if (HMSbegin.after(HMSnewBeginDate) && HMSbegin.before(HMSnewEndingDate)) {
            returnObject.setStatus("0");
            returnObject.setMsg("?????????????????????" +  msg  + "???????????????????????????,???????????????");
            return returnObject;
        }
        if (HMSend.after(HMSnewBeginDate) && HMSend.before(HMSnewEndingDate)) {
            returnObject.setStatus("0");
            returnObject.setMsg("?????????????????????" +  msg  + "???????????????????????????,???????????????");
            return returnObject;
        }
        if ((HMSnewEndingDate.after(HMSbegin) && HMSnewEndingDate.before(HMSend)) || HMSnewEndingDate.equals(HMSbegin) || HMSnewEndingDate.equals(HMSend)) {
            returnObject.setStatus("0");
            returnObject.setMsg("?????????????????????" +  msg  + "???????????????????????????,???????????????");
            return returnObject;
        }
        returnObject.setStatus("1");
        return returnObject;

    }

    /**
     * ??????????????????
     * @param channelID
     * @param object
     * @return
     */
    public ISSPReturnObject insertInstructions(String channelID, JSONObject object,String f_node_attribution){

        ISSPReturnObject returnObject = new ISSPReturnObject();

        PlanParamLDC planParamLDC = new PlanParamLDC();
        PlanParamDDC planParamDDC = new PlanParamDDC();

        //????????????Id
        String maxId = besplanConfigMapper.selectPlanMaxId();

//        Integer planId = Integer.parseInt(getAutoIncreaseCol(maxId));

        Integer planId = Integer.parseInt(maxId);

        planParamLDC.setId(planId);
        planParamDDC.setId(planId);

        Integer f_active = Integer.parseInt((String) object.get("f_active"));

        if (f_active == null){//???????????????????????? ???????????????????????? ????????? ??????1 ?????? ????????????????????????
            planParamLDC.setActive(0);
            planParamDDC.setActive(0);
            returnObject.setStatus("0");
            returnObject.setMsg("??????????????????! ?????????????????????");
            return returnObject;
        }else {
            planParamLDC.setActive(Integer.parseInt((String) object.get("f_active")));//????????????
            planParamDDC.setActive(Integer.parseInt((String) object.get("f_active")));//????????????
        }
        planParamLDC.setName((String) object.get("f_sysname"));//????????????2
        planParamDDC.setName((String) object.get("f_sysname"));//????????????2

        planParamLDC.setAlias((String) object.get("f_nickname"));//????????????3
        planParamDDC.setAlias((String) object.get("f_nickname"));//????????????3

        List<Map<String,Object>> modelist = besplanConfigMapper.selectOptionByMode((String) object.get("f_sceneInfoId"));

        if (modelist == null || modelist.isEmpty()) {
            returnObject.setStatus("0");
            returnObject.setMsg("????????????????????????????????????");
            return returnObject;
        }

        planParamLDC.setDefaultModeID((Integer) modelist.get(0).get("f_id"));//????????????4
        planParamDDC.setDefaultModeID((Integer) modelist.get(0).get("f_id"));//????????????4

        planParamLDC.setExecutionWay(Integer.parseInt((String) object.get("f_executionWay")));//????????????5
        planParamDDC.setExecutionWay(Integer.parseInt((String) object.get("f_executionWay")));//????????????5

        planParamLDC.setModeID(Integer.parseInt((String) object.get("f_modeInfoId")));//6
        planParamDDC.setModeID(Integer.parseInt((String) object.get("f_modeInfoId")));//6

        planParamLDC.setPlanType(Integer.parseInt((String) object.get("f_planType")));//7
        planParamDDC.setPlanType(Integer.parseInt((String) object.get("f_planType")));//7

        planParamLDC.setWeekMask(Integer.parseInt((String) object.get("f_weekmask"), 2));//8
        planParamDDC.setWeekMask(Integer.parseInt((String) object.get("f_weekmask"), 2));//8

        Integer scenetype = null;

        Integer scenetype1 = Integer.parseInt((String) object.get("f_scenetype"));

        if (scenetype1.equals(1)){

            scenetype = 0;
        }else {
            scenetype = 1;
        }

        planParamLDC.setSceneType(scenetype);//9
        planParamDDC.setSceneType(scenetype);//9

        planParamLDC.setSceneID(Integer.parseInt((String) object.get("f_sceneInfoId")));//10
        planParamDDC.setSceneID(Integer.parseInt((String) object.get("f_sceneInfoId")));//10

        String startDay = (String) object.get("f_startday");

        String enDay = (String) object.get("f_enday");

        planParamLDC.setStartDateYear(Integer.parseInt(startDay.substring(2,4)));
        planParamDDC.setStartDateYear(Integer.parseInt(startDay.substring(2,4)));

        planParamLDC.setStartDateMonth(Integer.parseInt(startDay.substring(5,7)));
        planParamDDC.setStartDateMonth(Integer.parseInt(startDay.substring(5,7)));

        planParamLDC.setStartDateDay(Integer.parseInt(startDay.substring(8,10)));
        planParamDDC.setStartDateDay(Integer.parseInt(startDay.substring(8,10)));

        planParamLDC.setEndDateYear(Integer.parseInt(enDay.substring(2,4)));
        planParamDDC.setEndDateYear(Integer.parseInt(enDay.substring(2,4)));

        planParamLDC.setEndDateMonth(Integer.parseInt(enDay.substring(5,7)));
        planParamDDC.setEndDateMonth(Integer.parseInt(enDay.substring(5,7)));

        planParamLDC.setEndDateDay(Integer.parseInt(enDay.substring(8,10)));
        planParamDDC.setEndDateDay(Integer.parseInt(enDay.substring(8,10)));

        String startTime = (String) object.get("f_startime");

        String endTime = (String) object.get("f_endtime");

        planParamLDC.setStartTimeHour(Integer.parseInt(startTime.substring(0,2)));
        planParamDDC.setStartTimeHour(Integer.parseInt(startTime.substring(0,2)));

        planParamLDC.setStartTimeMinute(Integer.parseInt(startTime.substring(3,5)));
        planParamDDC.setStartTimeMinute(Integer.parseInt(startTime.substring(3,5)));

        planParamLDC.setStartTimeSecond(Integer.parseInt(startTime.substring(6,8)));
        planParamDDC.setStartTimeSecond(Integer.parseInt(startTime.substring(6,8)));

        planParamLDC.setEndTimeHour(Integer.parseInt(endTime.substring(0,2)));
        planParamDDC.setEndTimeHour(Integer.parseInt(endTime.substring(0,2)));

        planParamLDC.setEndTimeMinute(Integer.parseInt(endTime.substring(3,5)));
        planParamDDC.setEndTimeMinute(Integer.parseInt(endTime.substring(3,5)));

        planParamLDC.setEndTimeSecond(Integer.parseInt(endTime.substring(6,8)));
        planParamDDC.setEndTimeSecond(Integer.parseInt(endTime.substring(6,8)));

        if ("1".equals(f_node_attribution)) {//??????
            Boolean state = SendMsgHandler.addPlanDDC(channelID,planParamDDC);
            if (!state) {
                returnObject.setStatus("1");
                returnObject.setMsg("????????????,????????????");

                return returnObject;
            }
            // ??????????????????
            MsgSubPubHandler.addSubMsg(DDCCmd.PLAN_ADD, channelID);
            returnObject.setStatus("1");
            returnObject.setMsg("????????????,????????????");

        } else  if ("2".equals(f_node_attribution)){//??????
            Boolean state = SendMsgHandler.addPlanLDC(channelID,planParamLDC);
            if (!state) {
                returnObject.setStatus("1");
                returnObject.setMsg("????????????,????????????");

                return returnObject;
            }
            // ??????????????????
            MsgSubPubHandler.addSubMsg(LDCCmd.PLAN_ADD, channelID);
            returnObject.setStatus("1");
            returnObject.setMsg("????????????,????????????");

        }
        return returnObject;
    }

    /**
     * ???????????????????????????
     * @param object
     * @return
     */
    public ISSPReturnObject insertPlanValueInfo(JSONObject object){

        ISSPReturnObject returnObject = new ISSPReturnObject();

        BesPlan besPlan = new BesPlan(); //16

        //????????????Id
        String maxId = besplanConfigMapper.selectPlanMaxId();

        besPlan.setF_id(getAutoIncreaseCol(maxId));//1

        besPlan.setF_sysname((String) object.get("f_sysname"));//2

        besPlan.setF_nickname((String) object.get("f_nickname"));//3

        String startDay = (String) object.get("f_startday");

        String enDay = (String) object.get("f_enday");

        String startTime = (String) object.get("f_startime");

        String endTime = (String) object.get("f_endtime");

        besPlan.setF_startday(startDay);//4

        besPlan.setF_startime(startTime);//5

        besPlan.setF_enday(enDay);//6

        besPlan.setF_endtime(endTime);//7

        besPlan.setF_planType((String) object.get("f_planType"));//8

        besPlan.setF_sceneInfoId((String) object.get("f_sceneInfoId"));//9

        besPlan.setF_modeInfoId((String) object.get("f_modeInfoId"));//10

        besPlan.setF_scenetype((String) object.get("f_scenetype"));//11

        besPlan.setF_weekmask((String) object.get("f_weekmask"));//12

        besPlan.setF_execut((String) object.get("f_executionWay"));//13

        String active = (String)object.get("f_active");//14

        if (active == null){
            besPlan.setF_active("0");
        }else {
            besPlan.setF_active((String) object.get("f_active"));
        }

        besPlan.setF_planId(String.valueOf(object.get("f_planId")));//15

        Boolean flag = besplanConfigMapper.insertPlanInfo(besPlan);

        if (flag){
            //??????????????????
            planCache.addOneplanCache(besPlan);
            returnObject.setStatus("1");
            returnObject.setMsg("??????????????????");
        }else {
            returnObject.setStatus("0");
            returnObject.setMsg("??????????????????");
        }
        return returnObject;
    }

    /**
     *
     * @Description: ??????????????????????????????
     *
     * @auther: wanghongjie
     * @date: 15:22 2021/8/17
     * @param:
     * @return:
     *
     */
    public ISSPReturnObject updatePlanValueInfo(JSONObject object){

        ISSPReturnObject returnObject = new ISSPReturnObject();

        BesPlan besPlan = new BesPlan(); //16

        besPlan.setF_id(String.valueOf(object.get("f_id")));//1

        besPlan.setF_sysname((String) object.get("f_sysname"));//2

        besPlan.setF_nickname((String) object.get("f_nickname"));//3

        String startDay = (String) object.get("f_startday");

        String enDay = (String) object.get("f_enday");

        String startTime = (String) object.get("f_startime");

        String endTime = (String) object.get("f_endtime");

        besPlan.setF_startday(startDay);//4

        besPlan.setF_startime(startTime);//5

        besPlan.setF_enday(enDay);//6

        besPlan.setF_endtime(endTime);//7

        besPlan.setF_planType((String) object.get("f_planType"));//8

        besPlan.setF_sceneInfoId((String) object.get("f_sceneInfoId"));//9

        besPlan.setF_modeInfoId((String) object.get("f_modeInfoId"));//10

        besPlan.setF_scenetype((String) object.get("f_scenetype"));//11

        besPlan.setF_weekmask((String) object.get("f_weekmask"));//12

        besPlan.setF_execut((String) object.get("f_execut"));//13

        String active = (String)object.get("f_active");//14

        if (active == null){
            besPlan.setF_active("0");
        }else {
            besPlan.setF_active((String) object.get("f_active"));
        }

        besPlan.setF_planId(String.valueOf(object.get("f_planId")));//15

        Boolean flag = besplanConfigMapper.updatePlanInfo(besPlan);

        if (flag){
            //??????????????????
            planCache.updateOneplanCache(besPlan);
            returnObject.setStatus("1");
            returnObject.setMsg("??????????????????");
        }else {
            returnObject.setStatus("0");
            returnObject.setMsg("??????????????????");
        }
        return returnObject;
    }

    /**
     * ??????????????????
     * @return
     */
    @Override
    public ISSPReturnObject selectAllPlan() {

        ISSPReturnObject returnObject = new ISSPReturnObject();

        List<BesPlan> list = besplanConfigMapper.selectAllPlan();

        returnObject.setData(list);

        returnObject.setStatus("1");

        return returnObject;
    }

    /**
     *
     * @Description: ??????????????????
     *
     * @auther: wanghongjie
     * @date: 15:38 2021/8/17
     * @param: [object]
     * @return: com.core.common.ISSPReturnObject
     *
     */
    @Override
    public ISSPReturnObject synchronizationInfo(JSONObject object) {

        ISSPReturnObject returnObject = new ISSPReturnObject();
        String f_node_attribution = null;//???????????????cross1???lamp2???energy3
        String channelID = null;

        Map<String,Object> data = (Map<String, Object>) object.get("data");

        String modeId = (String) data.get("f_modeInfoId");

        List<Map<String,Object>> pointDateMap = besplanConfigMapper.selectPointValueInfo(modeId);

        if (pointDateMap == null) {
            returnObject.setStatus("0");
            returnObject.setMsg("??????????????????????????????,????????????");
            return returnObject;
        }

        String f_id = (String) pointDateMap.get(0).get("f_pointId");

        BESSbPzStruct besSbPzStruct = sbPzStructCache.getCachedElementById(f_id);
        if (besSbPzStruct == null) {
            returnObject.setStatus("0");
            returnObject.setMsg("?????????????????????????????????,????????????");
            return returnObject;
        }
        f_node_attribution = besSbPzStruct.getF_node_attribution();

        if ("1".equals(f_node_attribution)) {//??????

            Map<String,Object> queryDDCMapByPoint = besplanConfigMapper.queryChannelByIdDDC(f_id);
            channelID = (String) queryDDCMapByPoint.get("F_CHANNEL_ID");
        } else if ("2".equals(f_node_attribution)) {//??????

            Map<String,Object> queryLDCMapByPoint = besplanConfigMapper.queryChannelByIdLDC(f_id);
            channelID = (String) queryLDCMapByPoint.get("F_CHANNEL_ID");
        }

        returnObject = updateInstructions(channelID,object,f_node_attribution,"??????");

        return returnObject;
    }

    /**
     * ??????????????????
     * @param channelID
     * @param object
     * @return
     */
    public ISSPReturnObject updateInstructions(String channelID, JSONObject object,String f_node_attribution,String type){

        ISSPReturnObject returnObject = new ISSPReturnObject();

        PlanParamLDC planParamLDC = new PlanParamLDC();
        PlanParamDDC planParamDDC = new PlanParamDDC();

        Map<String,Object> map = (Map<String, Object>) object.get("data");
        if (map == null) {
            map = object;
        }

        planParamLDC.setId(Integer.parseInt((String) map.get("f_id")));//id
        planParamDDC.setId(Integer.parseInt((String) map.get("f_id")));//id

        planParamLDC.setActive(Integer.parseInt((String) map.get("f_active")));//????????????
        planParamDDC.setActive(Integer.parseInt((String) map.get("f_active")));//????????????

        planParamLDC.setName((String) map.get("f_sysname"));//??????
        planParamDDC.setName((String) map.get("f_sysname"));//??????

        planParamLDC.setAlias((String) map.get("f_nickname"));//??????
        planParamDDC.setAlias((String) map.get("f_nickname"));//??????

        planParamLDC.setPlanType(Integer.parseInt((String) map.get("f_planType")));//????????????
        planParamDDC.setPlanType(Integer.parseInt((String) map.get("f_planType")));//????????????

        List<Map<String,Object>> modelist = besplanConfigMapper.selectOptionByMode((String) map.get("f_sceneInfoId"));

        if (modelist == null || modelist.isEmpty()) {
            returnObject.setStatus("0");
            returnObject.setMsg("????????????????????????????????????");
            return returnObject;
        }

        planParamLDC.setDefaultModeID((Integer) modelist.get(0).get("f_id"));//????????????4
        planParamDDC.setDefaultModeID((Integer) modelist.get(0).get("f_id"));//????????????4

        planParamLDC.setModeID(Integer.parseInt((String) map.get("f_modeInfoId")));//6
        planParamDDC.setModeID(Integer.parseInt((String) map.get("f_modeInfoId")));//6

        String startDay = (String) map.get("f_startday");

        String enDay = (String) map.get("f_enday");

        planParamLDC.setStartDateYear(Integer.parseInt(startDay.substring(2,4)));
        planParamDDC.setStartDateYear(Integer.parseInt(startDay.substring(2,4)));

        planParamLDC.setStartDateMonth(Integer.parseInt(startDay.substring(5,7)));
        planParamDDC.setStartDateMonth(Integer.parseInt(startDay.substring(5,7)));

        planParamLDC.setStartDateDay(Integer.parseInt(startDay.substring(8,10)));
        planParamDDC.setStartDateDay(Integer.parseInt(startDay.substring(8,10)));

        planParamLDC.setEndDateYear(Integer.parseInt(enDay.substring(2,4)));
        planParamDDC.setEndDateYear(Integer.parseInt(enDay.substring(2,4)));

        planParamLDC.setEndDateMonth(Integer.parseInt(enDay.substring(5,7)));
        planParamDDC.setEndDateMonth(Integer.parseInt(enDay.substring(5,7)));

        planParamLDC.setEndDateDay(Integer.parseInt(enDay.substring(8,10)));
        planParamDDC.setEndDateDay(Integer.parseInt(enDay.substring(8,10)));

        String startTime = (String) map.get("f_startime");

        String endTime = (String) map.get("f_endtime");

        planParamLDC.setStartTimeHour(Integer.parseInt(startTime.substring(0,2)));
        planParamDDC.setStartTimeHour(Integer.parseInt(startTime.substring(0,2)));

        planParamLDC.setStartTimeMinute(Integer.parseInt(startTime.substring(3,5)));
        planParamDDC.setStartTimeMinute(Integer.parseInt(startTime.substring(3,5)));

        planParamLDC.setStartTimeSecond(Integer.parseInt(startTime.substring(6,8)));
        planParamDDC.setStartTimeSecond(Integer.parseInt(startTime.substring(6,8)));

        planParamLDC.setEndTimeHour(Integer.parseInt(endTime.substring(0,2)));
        planParamDDC.setEndTimeHour(Integer.parseInt(endTime.substring(0,2)));

        planParamLDC.setEndTimeMinute(Integer.parseInt(endTime.substring(3,5)));
        planParamDDC.setEndTimeMinute(Integer.parseInt(endTime.substring(3,5)));

        planParamLDC.setEndTimeSecond(Integer.parseInt(endTime.substring(6,8)));
        planParamDDC.setEndTimeSecond(Integer.parseInt(endTime.substring(6,8)));

        planParamLDC.setExecutionWay(Integer.parseInt((String) map.get("f_execut")));//????????????5
        planParamDDC.setExecutionWay(Integer.parseInt((String) map.get("f_execut")));//????????????5

        planParamLDC.setWeekMask(Integer.parseInt((String) map.get("f_weekmask"), 2));//8
        planParamDDC.setWeekMask(Integer.parseInt((String) map.get("f_weekmask"), 2));//8


        //???????????????
        if ("1".equals(map.get("f_scenetype"))) {
            planParamLDC.setSceneType(0);
            planParamDDC.setSceneType(0);
        } else if ("2".equals(map.get("f_scenetype"))) {
            planParamLDC.setSceneType(1);
            planParamDDC.setSceneType(1);
        }
//        planParamLDC.setSceneType(Integer.parseInt((String) map.get("f_scenetype")));//9

        planParamLDC.setSceneID(Integer.parseInt((String) map.get("f_sceneInfoId")));//10
        planParamDDC.setSceneID(Integer.parseInt((String) map.get("f_sceneInfoId")));//10

        if ("1".equals(f_node_attribution)) {//??????

            Boolean state = SendMsgHandler.setPlanParamDDC(channelID, planParamDDC);
            if (!state) {
                returnObject.setStatus("1");
                if ("??????".equals(type)) {
                    returnObject.setStatus("1");
                    returnObject.setMsg("????????????,????????????");
                } else if("??????".equals(type)){
                    returnObject.setStatus("0");
                    returnObject.setMsg("????????????");
                }
                return returnObject;
            }
            if ("??????".equals(type)) {
                returnObject.setMsg("????????????,????????????");
            } else if("??????".equals(type)){
                returnObject.setMsg("????????????");
            }
            returnObject.setStatus("1");
            // ??????????????????
            MsgSubPubHandler.addSubMsg(DDCCmd.PLAN_PARAM_SET, channelID);

        } else if ("2".equals(f_node_attribution)){//??????

            Boolean state = SendMsgHandler.setPlanParamLDC(channelID, planParamLDC);
            if (!state) {

                if ("??????".equals(type)) {
                    returnObject.setStatus("1");
                    returnObject.setMsg("????????????,????????????");
                } else if("??????".equals(type)){
                    returnObject.setStatus("0");
                    returnObject.setMsg("????????????");
                }
                return returnObject;
            }
            returnObject.setStatus("1");
            returnObject.setMsg("????????????,????????????");
            // ??????????????????
            MsgSubPubHandler.addSubMsg(LDCCmd.PLAN_PARAM_SET, channelID);
        }

//        Boolean state = SendMsgHandler.setPlanParamLDC(channelID, planParamLDC);
//
//        returnObject = updatePointValueInfo(object);
//
//        String maxId = (String) map.get("f_id");
//
//        String synchro = null;
//
//        if (state){
//            returnObject.setStatus("1");
//            synchro = "1";
//            besplanConfigMapper.insertPlanSynchroState(maxId,synchro);
//            returnObject.setMsg("????????????!");
//        }else {
//            synchro = "0";
//            returnObject.setStatus("0");
//            besplanConfigMapper.insertPlanSynchroState(maxId,synchro);
//            returnObject.setMsg("????????????!");
//        }
//
//        // ??????????????????
//        MsgSubPubHandler.addSubMsg(LDCCmd.PLAN_PARAM_SET, channelID);
        return returnObject;
    }

    /**
     * ??????????????????
     * @param object
     * @return
     */
    @Override
    public ISSPReturnObject insertTimeTaskInfomation(JSONObject object) {

        ISSPReturnObject returnObject = new ISSPReturnObject();

        BesPlan besPlan = new BesPlan();

        String  tasktype = (String) object.get("f_tasktype");

        if (tasktype.equals("0")){//?????????
            besPlan.setF_active("1");//??????
        }

        //????????????Id
        String maxId = besplanConfigMapper.selectPlanMaxId();

        String returnId = getAutoIncreaseCol(maxId);

        besPlan.setF_id(getAutoIncreaseCol(maxId));//1

        besPlan.setF_sysname((String) object.get("planTaskName"));//2

        besPlan.setF_nickname((String) object.get("planTaskNickname"));//3

        besPlan.setF_sceneInfoId((String) object.get("f_sceneTaskInfo"));

        besPlan.setF_modeInfoId((String) object.get("f_modeTaskInfo"));

        besPlan.setF_scenetype((String) object.get("f_scenetype"));

        besPlan.setF_planId(String.valueOf(object.get("planId")));

        besPlan.setF_invoke((String) object.get("f_invoke"));

        List<Map<String, Object>> modeList = besplanConfigMapper.selectScenePointBymodeId(besPlan.getF_modeInfoId());
        if (modeList == null || modeList.size() == 0) {
            returnObject.setStatus("0");
            returnObject.setMsg("??????????????????????????????,?????????");
            return returnObject;
        }
        //??????????????????????????????
//        BesPlan besPlanList = besplanConfigMapper.selectPlanByModeId(besPlan.getF_modeInfoId(),besPlan.getF_scenetype());
//        if (besPlanList != null) {
//            returnObject.setStatus("0");
//            returnObject.setMsg("?????????????????????,???????????????");
//        }

        Boolean flag = besplanConfigMapper.insertCollectPlanInfo(besPlan);

        if (flag){
            planCache.addOneplanCache(besPlan);
            returnObject = InsertTimeTaskInfo(object,besPlan);

            String taskId = (String) returnObject.getData();
            Map<String,Object> map = new HashMap<>();
            map.put("planId",returnId);
            map.put("taskId",taskId);
            returnObject.setMap(map);
            returnObject.setStatus("1");
            returnObject.setMsg("????????????????????????");
        }else {
            returnObject.setStatus("0");
            returnObject.setMsg("????????????????????????");
        }
        return returnObject;
    }

    /**
     *
     * @Description: ???????????????????????????
     *
     * @auther: wanghongjie
     * @date:  2021/8/19
     * @param: [object, besPlan]
     * @return: com.core.common.ISSPReturnObject
     *
     */
    public ISSPReturnObject InsertTimeTaskInfo(JSONObject object,BesPlan besPlan){

        ISSPReturnObject returnObject = new ISSPReturnObject();

        String  tasktype = (String) object.get("f_tasktype");

        String maxId = besplanConfigMapper.selectTimeTaskMaxId();

        String f_timename = (String) object.get("f_timename");

        String planTimeConfig = (String) object.get("planTimeConfig");

        String pId = besPlan.getF_id();

        if (tasktype.equals("0")){//?????????
            //????????????Id
            BesTimeTask besTimeTask = new BesTimeTask();

            String taskId = getAutoIncreaseCol(maxId);

            besTimeTask.setF_id(taskId);//1

            besTimeTask.setF_timename(f_timename);//2

            besTimeTask.setF_tasktype(tasktype);//3

            besTimeTask.setF_cronstartexpre("?????????");//4

            besTimeTask.setF_pId(pId);

            besTimeTask.setF_modeId((String) object.get("f_modeTaskInfo"));

            besTimeTask.setF_specificvalue(((String) object.get("f_specificvalue")));

            boolean flag = besplanConfigMapper.insertBesTimeTaskInfo(besTimeTask);

            if (flag){
                timeTaskCache.addOnetimeTaskCache(besTimeTask);
//                planVariatInfoCache.addOneVariatInfo(besTimeTask);
                returnObject.setData(taskId);
                returnObject.setStatus("1");
            }else {
                returnObject.setStatus("0");
            }

        }else if (tasktype.equals("1")){//??????????????????????????????
            //????????????Id
            BesTimeTask besTimeTask = new BesTimeTask();

            String taskId = getAutoIncreaseCol(maxId);

            besTimeTask.setF_id(taskId);//1

            besTimeTask.setF_timename(f_timename);//2

            besTimeTask.setF_tasktype(tasktype);//3

            besTimeTask.setF_cronstartexpre(planTimeConfig);//4

            besTimeTask.setF_pId(pId);

            besTimeTask.setF_specificvalue("?????????");

            boolean flag = besplanConfigMapper.insertBesTimeTaskInfo(besTimeTask);

            if (flag){
                timeTaskCache.addOnetimeTaskCache(besTimeTask);
                returnObject.setData(taskId);
                returnObject.setStatus("1");
            }else {
                returnObject.setStatus("0");
            }
        }
        return returnObject;
    }

    /**
     * ??????????????????
     * @return
     */
    @Override
    public ISSPReturnObject selectDisrepeatSceneMode(String type) {

        ISSPReturnObject returnObject = new ISSPReturnObject();

        List<Map<String,Object>> scenelist = besplanConfigMapper.selectAllSceneInfo(type);

        returnObject.setList(scenelist);

        return returnObject;
    }

    /**
     * ????????????Id?????????????????????????????????
     * @param f_sceneInfoId
     * @return
     */
    @Override
    public ISSPReturnObject selectOptionByMode(String f_sceneInfoId) {

        ISSPReturnObject returnObject = new ISSPReturnObject();

        List<Map<String,Object>> modelist = besplanConfigMapper.selectOptionByMode(f_sceneInfoId);

        String sceneType = besplanConfigMapper.selectSceneTypeBySceneId(f_sceneInfoId);

        if (sceneType!=null){
            returnObject.setData(sceneType);
        }

        returnObject.setList(modelist);

        return returnObject;
    }

    /**
     * ????????????ID?????????ID??????????????????
     * @param nodeId
     * @return
     */
    @Override
    public ISSPReturnObject selectPlanInfomation(String nodeId) {

        ISSPReturnObject returnObject = new ISSPReturnObject();

        List<Map<String,Object>> list = besplanConfigMapper.selectPlanInfomation(nodeId);

        if (list.size() == 0){
            returnObject.setStatus("0");
            returnObject.setList(null);
            return returnObject;
        }else {
            for (int i = 0; i < list.size(); i++){

                String sceneInfoId = (String) list.get(i).get("f_sceneInfoId");
                String modeInfoId = (String) list.get(i).get("f_modeInfoId");
                BeSceneInfo beSceneInfo = besplanConfigMapper.selectSceneInfomation(sceneInfoId);
                BeSceneMode beSceneMode = besplanConfigMapper.selectModeInfomation(modeInfoId);
                if (beSceneInfo != null && beSceneMode != null){
                    String f_pointId = beSceneMode.getF_id();
                    List<String> pointList = besplanConfigMapper.selectPointInfomation(f_pointId);
                    StringBuffer pointName=new StringBuffer(pointList.toString());
                    pointName.deleteCharAt(pointName.lastIndexOf("[")).deleteCharAt(pointName.lastIndexOf("]"));
                    String f_modename = beSceneMode.getF_modename();
                    String f_scenename = beSceneInfo.getF_name();
                    String f_scenenickname = beSceneInfo.getF_scenenickname();
                    String f_discription =  beSceneInfo.getF_discription();
                    Map<String,Object> map = list.get(i);
                    map.put("f_modename",f_modename);
                    map.put("f_scenename",f_scenename);
                    map.put("f_scenenickname",f_scenenickname);
                    map.put("f_discription",f_discription);
                    map.put("f_pointname",pointName.toString());
                    map.replace("f_startday",list.get(i).get("f_startday") + " " + list.get(i).get("f_startime"));
                    map.replace("f_enday",list.get(i).get("f_enday") + " " + list.get(i).get("f_endtime"));
                }
            }
        }
        returnObject.setList(list);
        return returnObject;
    }


    /**
     * ????????????ID?????????ID????????????????????????
     * @param nodeId
     * @return
     */
    @Override
    public ISSPReturnObject selectCollectPlanInfomation(String nodeId) {

        ISSPReturnObject returnObject = new ISSPReturnObject();

        List<Map<String,Object>> planList = besplanConfigMapper.selectPlanCollectInfo(nodeId);

        for (int i = 0; i<planList.size();i++){
            String planId = String.valueOf(planList.get(i).get("f_id")) ;
            List<Map<String,Object>> timeTaskList = besplanConfigMapper.selectTimeTaskInfo(planId);
            String sceneInfoId = (String) planList.get(i).get("f_sceneInfoId");
            String modeInfoId = (String) planList.get(i).get("f_modeInfoId");
            BeSceneInfo beSceneInfo = besplanConfigMapper.selectSceneInfomation(sceneInfoId);
            BeSceneMode beSceneMode = besplanConfigMapper.selectModeInfomation(modeInfoId);
            if (beSceneInfo != null && beSceneMode != null){
                String f_pointId = beSceneMode.getF_id();
                List<String> pointList = besplanConfigMapper.selectPointInfomation(f_pointId);
                StringBuffer pointName=new StringBuffer(pointList.toString());
                pointName.deleteCharAt(pointName.lastIndexOf("[")).deleteCharAt(pointName.lastIndexOf("]"));
                String f_modename = beSceneMode.getF_modename();
                String f_scenename = beSceneInfo.getF_name();
                String f_scenenickname = beSceneInfo.getF_scenenickname();
                String f_discription =  beSceneInfo.getF_discription();
                planList.get(i).put("f_modename",f_modename);
                planList.get(i).put("f_scenename",f_scenename);
                planList.get(i).put("f_scenenickname",f_scenenickname);
                planList.get(i).put("f_discription",f_discription);
                planList.get(i).put("f_pointname",pointName.toString());
            }
            if (timeTaskList.size() !=0){
                String f_timename = (String) timeTaskList.get(0).get("f_timename");
                String f_jobId = (String) timeTaskList.get(0).get("f_jobId");
                String f_tasktype = (String) timeTaskList.get(0).get("f_tasktype");
                String f_timetype = (String) timeTaskList.get(0).get("f_timetype");
                //String f_specifictime = (String) timeTaskList.get(0).get("f_specifictime");
                String f_specificvalue = (String) timeTaskList.get(0).get("f_specificvalue");
                String f_taskId = (String) timeTaskList.get(0).get("f_id");
                String f_cronstartexpre = (String) timeTaskList.get(0).get("f_cronstartexpre");
                String f_convertmin = (String) timeTaskList.get(0).get("f_convertmin");
                planList.get(i).put("f_timename",f_timename);
                planList.get(i).put("f_taskId",f_taskId);
                planList.get(i).put("f_tasktype",f_tasktype);
                planList.get(i).put("f_timetype",f_timetype);
                planList.get(i).put("f_jobId",f_jobId);
                //planList.get(i).put("f_specifictime",f_specifictime);
                planList.get(i).put("f_specificvalue",f_specificvalue);
                planList.get(i).put("f_cronstartexpre",f_cronstartexpre);
                planList.get(i).put("f_convertmin",f_convertmin);
            }
        }
        returnObject.setList(planList);
        return returnObject;
    }

    /**
     * ?????????????????????
     * @param fId
     * @return
     */
    @Override
    public ISSPReturnObject selectUnderInfomation(String fId, String planId) throws UnsupportedEncodingException, RemoteException, ServiceException {
        ISSPReturnObject returnObject = new ISSPReturnObject();
        String f_node_attribution = null;//???????????????cross1???lamp2???energy3
        Boolean deleteState = false;

        String channelID = "";

        String modeId =  besplanConfigMapper.selectBesPlanByPlanId(fId);

        if (modeId != null && modeId != ""){

            List<Map<String,Object>> list = besplanConfigMapper.selectPointValueInfo(modeId);

            if (list == null) {
                returnObject.setStatus("0");
                returnObject.setMsg("???????????????????????????,?????????!");
                return returnObject;
            }

            if (list.size() > 0){

                String f_id = (String) list.get(0).get("f_pointId");

                //????????????????????????????????????
                BESSbPzStruct besSbPzStruct = sbPzStructCache.getCachedElementById(f_id);
                if (besSbPzStruct != null) {
                    f_node_attribution = besSbPzStruct.getF_node_attribution();
                } else {
                    returnObject.setStatus("0");
                    returnObject.setMsg("???????????????????????????,?????????!");
                    return returnObject;
                }

                if ("1".equals(f_node_attribution)) {//??????

                    Map<String,Object> queryDDCMapByPoint = besplanConfigMapper.queryChannelByIdDDC(f_id);
                    channelID = (String) queryDDCMapByPoint.get("F_CHANNEL_ID");
                    Integer id = Integer.parseInt(fId);

                    deleteState = SendMsgHandler.getPlanParamDDC(channelID,id);

                } else if ("2".equals(f_node_attribution)) {//??????

                    Map<String,Object> queryLDCMapByPoint = besplanConfigMapper.queryChannelByIdLDC(f_id);
                    channelID = (String) queryLDCMapByPoint.get("F_CHANNEL_ID");
                    Integer id = Integer.parseInt(fId);

                    deleteState = SendMsgHandler.getPlanParamLDC(channelID,id);
                }

                if (!deleteState){
                    returnObject.setStatus("0");
                    returnObject.setMsg("??????????????????????????????");
                    return returnObject;
                }else {
                    if ("1".equals(f_node_attribution)) {//??????
                        // ??????????????????
                        MsgSubPubHandler.addSubMsg(DDCCmd.PLAN_PARAM_GET, channelID);

                    } else if ("2".equals(f_node_attribution)) {//??????
                        // ??????????????????
                        MsgSubPubHandler.addSubMsg(LDCCmd.PLAN_PARAM_GET, channelID);
                    }
                    returnObject.setStatus("1");
                    returnObject.setMsg("??????????????????????????????");
                }
            }
        }
        return returnObject;
    }

    /**
     *????????????????????????
     * @param fId
     * @return
     */
    @Override
    public ISSPReturnObject deletePlanInfomation(String fId) {

        ISSPReturnObject returnObject = new ISSPReturnObject();
        String f_node_attribution = null;//???????????????cross1???lamp2???energy3
        Boolean deleteState = false;

        String modeId =  besplanConfigMapper.selectBesPlanByPlanId(fId);

        String channelID = "";

        if (modeId!=null && modeId!=""){

            List<Map<String,Object>> list = besplanConfigMapper.selectPointInfomationByModeId(modeId);

            if (list.size() > 0){

                String f_id = (String) list.get(0).get("f_pointId");

                //????????????????????????????????????
                BESSbPzStruct besSbPzStruct = sbPzStructCache.getCachedElementById(f_id);
                if (besSbPzStruct != null) {
                    f_node_attribution = besSbPzStruct.getF_node_attribution();
                } else {
                    returnObject.setStatus("0");
                    returnObject.setMsg("???????????????????????????,?????????!");
                    return returnObject;
                }

                returnObject = deletePlanInfoInstructions(Integer.valueOf(fId));

                if ("0".equals(returnObject.getStatus())) {//????????????
                    returnObject.setStatus("0");
                    returnObject.setMsg("??????????????????!");
                    return returnObject;
                }

                if ("1".equals(f_node_attribution)) {//??????

                    Map<String,Object> queryDDCMapByPoint = besplanConfigMapper.queryChannelByIdDDC(f_id);
                    channelID = (String) queryDDCMapByPoint.get("F_CHANNEL_ID");
                    Integer id = Integer.parseInt(fId);

                    deleteState = SendMsgHandler.deletePlanDDC(channelID,id);

                } else if ("2".equals(f_node_attribution)) {//??????

                    Map<String,Object> queryLDCMapByPoint = besplanConfigMapper.queryChannelByIdLDC(f_id);
                    channelID = (String) queryLDCMapByPoint.get("F_CHANNEL_ID");
                    Integer id = Integer.parseInt(fId);

                    deleteState = SendMsgHandler.deletePlanLDC(channelID,id);
                }
            }
        }

        if (!deleteState){
            returnObject.setStatus("1");
            returnObject.setMsg("??????????????????????????????");
            return returnObject;
        }else {
            if ("1".equals(f_node_attribution)) {//??????
                // ??????????????????
                MsgSubPubHandler.addSubMsg(DDCCmd.PLAN_DELETE, channelID);

            } else if ("2".equals(f_node_attribution)) {//??????
                // ??????????????????
                MsgSubPubHandler.addSubMsg(LDCCmd.PLAN_DELETE, channelID);
            }
            returnObject.setStatus("1");
            returnObject.setMsg("??????????????????????????????");
        }
        return returnObject;
    }

    /**
     *
     * @Description: ??????????????????????????????
     *
     * @auther: wanghongjie
     * @date: 10:10 2021/8/17
     * @param: [fId]
     * @return: com.core.common.ISSPReturnObject
     *
     */
    public ISSPReturnObject deletePlanInfoInstructions(Integer fId){

        ISSPReturnObject returnObject = new ISSPReturnObject();

        Boolean flag = besplanConfigMapper.deletePlanInfomation(String.valueOf(fId));

        if (flag){
            //??????????????????
            planCache.deleteOneplanCache(String.valueOf(fId));
            //??????????????????
            Boolean deleteTaskInfomationByPId = besplanConfigMapper.deleteTaskInfomationByPId(String.valueOf(fId));
            if (deleteTaskInfomationByPId) {
                timeTaskCache.deleteOnetimeTaskCacheByPId(String.valueOf(fId));
                //????????????id??????sys_job??????????????????????????????
                besplanConfigMapper.deleteSysJobByPId(String.valueOf(fId));
            }
            returnObject.setStatus("1");
        }else {
            returnObject.setStatus("0");
        }

        return returnObject;
    }

    @Override
    public ISSPReturnObject selectPlanInfomationByPlanId(String sceneId, String modeID) {

        ISSPReturnObject returnObject = new ISSPReturnObject();

        String sceneName = besplanConfigMapper.selectPlanInfomationBySceneId(sceneId);

        String modeName = besplanConfigMapper.selectPlanInfomationByModeId(modeID);

        Map<String,Object> map = new HashMap<>();

        map.put("sceneName",sceneName);

        map.put("modeName",modeName);

        returnObject.setMap(map);

        return returnObject;
    }

    /**
     * ??????????????????
     * @param object
     * @return
     */
    @Override
    public ISSPReturnObject deleteCollectInfomation(JSONObject object) {

        ISSPReturnObject returnObject = new ISSPReturnObject();

        Map<String,Object> dataMap = (Map<String, Object>) object.get("data");

        String f_planId = (String) dataMap.get("f_id") ;

        Boolean flag = besplanConfigMapper.deletePlanInfomation(f_planId);

        String f_taskId = (String) dataMap.get("f_taskId");

        String taskType = (String) dataMap.get("f_tasktype");

        if (flag){
            planCache.deleteOneplanCache(f_planId);

            if (taskType.equals("1")){//????????????

                returnObject = deleteTimeTaskInfo(f_planId,f_taskId);

            }else if (taskType.equals("0")){//???????????????

                returnObject = deleteVariatTaskInfo(f_planId,f_taskId);

            }
        }else {
            returnObject.setStatus("0");
        }
        return returnObject;
    }

    /**
     * ?????????????????????????????????
     * @return
     */
    public ISSPReturnObject deleteTimeTaskInfo(String f_planId,String f_taskId){

        ISSPReturnObject returnObject = new ISSPReturnObject();
        String job_group = null;//????????????

        //??????f_id?????????????????????????????????????????????
        Map<String,Object> sys_job = besplanConfigMapper.selectSysJob(f_planId);

        if (sys_job != null) {
            job_group = (String) sys_job.get("job_group");
        }

        String JobId = besplanConfigMapper.selectTimeTaskJobId(f_taskId);

        Boolean flag = besplanConfigMapper.deleteTaskInfomation(f_taskId);

        if (flag){
            timeTaskCache.deleteOnetimeTaskCache(f_taskId);
            if (null == JobId){
                returnObject.setStatus("0");
            }else {
                Map<String,Object> map = new HashMap<>();
                map.put("job_id",JobId);
                map.put("job_group",job_group);
                Boolean flag1 = besplanConfigMapper.deleteQuartzSysJobInfo(JobId);

                deleteJob(map);

                if (flag1){

                    returnObject.setStatus("1");
                }else {

                    returnObject.setStatus("0");
                }
            }
        }else {
            returnObject.setStatus("0");
        }
        return returnObject;
    }


    /**
     * ????????????????????????????????????
     * @return
     */
    public ISSPReturnObject deleteVariatTaskInfo(String f_planId,String f_taskId){

        ISSPReturnObject returnObject = new ISSPReturnObject();

        Boolean flag = besplanConfigMapper.deleteTaskInfomation(f_taskId);

        if (flag){
            returnObject.setStatus("1");
            timeTaskCache.deleteOnetimeTaskCache(f_taskId);
        }else {
            returnObject.setStatus("0");
        }

        return returnObject;
    }

    /**
     * ??????????????????????????????JobId
     * @param object
     * @return
     */
    @Override
    public ISSPReturnObject insertTimeTaskJobId(JSONObject object) {

        ISSPReturnObject returnObject = new ISSPReturnObject();

        String jobId = (String) object.get("jobId");

        String taskId = (String) object.get("taskId");

        Boolean flag = besplanConfigMapper.insertTimeTaskJobId(jobId,taskId);

        if (flag){
            returnObject.setStatus("1");
        }else {
            returnObject.setStatus("0");
        }
        return returnObject;
    }

    /**
     * ???????????????????????????
     * @param taskId,planId
     * @return
     */
    @Override
    public ISSPReturnObject executeInfomation(String taskId, String planId) {

        ISSPReturnObject returnObject = new ISSPReturnObject();

//        Map<String,Object> map = (Map<String, Object>) object.get("data");

//        String planId = (String) map.get("f_id");

        String f_invoke = "1";

        boolean flag = besplanConfigMapper.executePlanInfoByPlanId(planId,f_invoke);

        if (flag){
            planCache.updateOneplanByPlanIdCache(planId,f_invoke);
            returnObject.setStatus("1");
        }else {
            returnObject.setStatus("0");
        }
        return returnObject;
    }

    /**
     * ???????????????????????????
     * @param taskId planId
     * @return
     */
    @Override
    public ISSPReturnObject stopInfomation(String taskId,String planId) {

        ISSPReturnObject returnObject = new ISSPReturnObject();

//        Map<String,Object> map = (Map<String, Object>) object.get("data");

        //       String planId = (String) map.get("f_id");

        String f_invoke = "0";

//        String f

        boolean flag = besplanConfigMapper.stopPlanInfoByPlanId(planId,f_invoke);

        if (flag){
            planCache.updateOneplanByPlanIdCache(planId,f_invoke);
            returnObject.setStatus("1");
        }else {
            returnObject.setStatus("0");
        }
        return returnObject;
    }

    /**
     * ????????????????????????
     * @param planId
     * @return
     */
    @Override
    public ISSPReturnObject selectTimeTaskInfoByPlanId(String planId) {

        ISSPReturnObject returnObject = new ISSPReturnObject();

        Map<String,Object> map = besplanConfigMapper.selectTimeTaskInfoByPlanId(planId);

        if (map.size()!=0){
            returnObject.setStatus("1");
            returnObject.setMap(map);
        }
        return returnObject;
    }

    /**
     *
     * @Description: ????????????????????????
     *
     * @auther: wanghongjie
     * @date: 11:07 2021/7/31
     * @param: [object]
     * @return: com.core.common.ISSPReturnObject
     *
     */
    @Transactional
    @Override
    public ISSPReturnObject updateTimeTaskInfomation(JSONObject object) {
        ISSPReturnObject returnObject = new ISSPReturnObject();
        ISSPReturnObject returnObj = new ISSPReturnObject();
        Boolean amountOfChange = null;//???????????????
        Boolean bes_timetask = null;
        String f_jobId = null;//??????Id
        Map<String,Object> map = new HashMap<>();
        Boolean SysJobMessage;//??????????????????????????????????????????

        Integer f_id = object.getInteger("f_id");//??????ID
        String f_tasktype = object.getString("f_tasktype");//????????????(0 ????????? 1?????????
        String planId = object.getString("planId");//bes_planinfo??? Id

        //??????????????????????????????????????????????????????
        if ("0".equals(f_tasktype)) {//?????????
            object.put("f_active","1");
            amountOfChange = true;
            f_jobId = "";

        } else {
            object.put("f_active","");
            amountOfChange = false;
        }

        List<Map<String,Object>> modeList = besplanConfigMapper.selectScenePointBymodeId(object.getString("f_modeTaskInfo"));
        if (modeList == null || modeList.size() == 0) {
            returnObject.setMsg("???????????????????????????,?????????");
            returnObject.setStatus("0");
            return returnObject;
        }

        //??????f_id?????????????????????????????????????????????
        Map<String,Object> sys_job = besplanConfigMapper.selectSysJob(String.valueOf(f_id));
        if(sys_job == null) {//??????????????????????????????????????????????????????????????????
            SysJobMessage = false;
        } else {//???????????????????????????????????????????????????????????????
            SysJobMessage = true;
        }

        object.put("f_jobId",f_jobId);

        if ("".equals(object.getString("f_specificvalue"))) {
            object.put("f_specificvalue","?????????");
        }

        //??????????????????????????????
//        BesPlan besPlanList = besplanConfigMapper.selectPlanByModeId(object.getString("f_modeTaskInfo"),object.getString("f_scenetype"));

//        if (besPlanList != null) {
//            if (!besPlanList.getF_id().equals(object.getString("f_id"))) {
//                returnObject.setStatus("0");
//                returnObject.setMsg("?????????????????????,???????????????");
//                return returnObject;
//            }
//        }

        try {
            //??????bes_plan?????????
            Boolean bes_plan = besplanConfigMapper.updatePlan(object);
            if (!bes_plan) {
                returnObject.setMsg("????????????");
                returnObject.setStatus("0");
                return returnObject;
            } else {
                BesPlan plan = new BesPlan();
                plan.setF_id(object.getString("f_id"));
                plan.setF_sysname(object.getString("planTaskName"));
                plan.setF_nickname(object.getString("planTaskNickname"));
                plan.setF_sceneInfoId(object.getString("f_sceneTaskInfo"));
                plan.setF_scenetype(object.getString("f_scenetype"));
                plan.setF_modeInfoId(object.getString("f_modeTaskInfo"));
                plan.setF_active(object.getString("f_active"));
                plan.setF_planId(object.getString("f_planId"));

                planCache.updateOneplanByIdCache(plan);
            }

            if (amountOfChange) {//???????????????

                if (SysJobMessage) {//????????????????????????????????????

                    //sys_job ?????????????????????????????????
                    besplanConfigMapper.deleteSysJob(object);

                    object.put("f_jobId","");
                    object.put("planTimeConfig","?????????");
                    boolean updateTimeTask = besplanConfigMapper.updateTimeTask(object);
                    if (updateTimeTask) {
                        BesTimeTask besTimeTask = new BesTimeTask();
                        besTimeTask.setF_pId(object.getString("f_id"));
                        besTimeTask.setF_timename(object.getString("f_timename"));
                        besTimeTask.setF_tasktype(object.getString("f_tasktype"));
                        besTimeTask.setF_specificvalue(object.getString("f_specificvalue"));
                        besTimeTask.setF_cronstartexpre(object.getString("planTimeConfig"));
                        besTimeTask.setF_jobId(object.getString("f_jobId"));
                        timeTaskCache.updateOnetimeTaskCache(besTimeTask);
                    }

                    map.put("job_id",sys_job.get("job_id"));
                    map.put("job_group",sys_job.get("job_group"));
                    //??????????????????
                    returnObj = deleteJob(map);


                    if ("1".equals(returnObj.getStatus())){
                        returnObject.setMsg("????????????");
                        returnObject.setStatus("1");
                        return returnObject;
                    } else {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        returnObject.setMsg("????????????");
                        returnObject.setStatus("0");
                        return returnObject;
                    }
                } else {
                    boolean updateTimeTask =  besplanConfigMapper.updateTimeTask(object);
                    if (updateTimeTask) {
                        returnObject.setMsg("????????????");
                        returnObject.setStatus("1");
                        return returnObject;
                    } else {
                        returnObject.setMsg("????????????");
                        returnObject.setStatus("0");
                        return returnObject;
                    }
                }

            } else {//????????????

                SysJob sysJob = new SysJob();

                String f_id_new = UUIDUtil.getRandom32BeginTimePK();

                sysJob.setJobId(f_id_new);

                sysJob.setPlanId(String.valueOf(f_id));

                sysJob.setJobName(object.getString("f_timename"));

                sysJob.setJobGroup("DEFAULT");

                sysJob.setInvokeTarget("besTask.executePlanTaskInfo");

                sysJob.setCronExpression(object.getString("planTimeConfig"));

                sysJob.setMisfirePolicy("3");

                sysJob.setConcurrent("0");

                sysJob.setStatus("0");

                sysJob.setCreateBy("admin");

                sysJob.setUpdateBy("admin");

                sysJob.setStatus(ScheduleConstants.Status.NORMAL.getValue());

                object.put("f_jobId",f_id_new);

                if (SysJobMessage) {//????????????????????????????????????,????????????,????????????????????????
                    //sys_job ?????????????????????????????????
                    besplanConfigMapper.updateSysJob(object);

                    object.put("f_specificvalue","?????????");

                    boolean updateTimeTask = besplanConfigMapper.updateTimeTask(object);
                    if (updateTimeTask) {
                        BesTimeTask besTimeTask = new BesTimeTask();
                        besTimeTask.setF_pId(object.getString("f_id"));
                        besTimeTask.setF_timename(object.getString("f_timename"));
                        besTimeTask.setF_tasktype(object.getString("f_tasktype"));
                        besTimeTask.setF_specificvalue(object.getString("f_specificvalue"));
                        besTimeTask.setF_cronstartexpre(object.getString("planTimeConfig"));
                        besTimeTask.setF_jobId(object.getString("f_jobId"));
                        timeTaskCache.updateOnetimeTaskCache(besTimeTask);
                    }

                    map.put("job_id",sys_job.get("job_id"));
                    map.put("job_group",sys_job.get("job_group"));
                    //??????????????????
                    returnObj = deleteJob(map);

                    if ("1".equals(returnObj.getStatus())){
                        returnObject.setMsg("????????????");
                        returnObject.setStatus("1");
                        ScheduleUtils.createScheduleJob(scheduler,sysJob);
                        return returnObject;

                    }else {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        returnObject.setMsg("????????????");
                        returnObject.setStatus("0");
                        return returnObject;
                    }
                } else {//???????????????,????????????????????????
                    //??????????????????

                    int rows = sysJobPlanMapper.insertSysJobTaskInfo(sysJob);

                    object.put("f_specificvalue","?????????");
                    object.put("f_jobId",f_id_new);
                    boolean updateTimeTask = besplanConfigMapper.updateTimeTask(object);
                    if (updateTimeTask) {
                        BesTimeTask besTimeTask = new BesTimeTask();
                        besTimeTask.setF_pId(object.getString("f_id"));
                        besTimeTask.setF_timename(object.getString("f_timename"));
                        besTimeTask.setF_tasktype(object.getString("f_tasktype"));
                        besTimeTask.setF_specificvalue(object.getString("f_specificvalue"));
                        besTimeTask.setF_cronstartexpre(object.getString("planTimeConfig"));
                        besTimeTask.setF_jobId(object.getString("f_jobId"));
                        timeTaskCache.updateOnetimeTaskCache(besTimeTask);
                    }

                    if (rows > 0){
                        returnObject.setMsg("????????????");
                        returnObject.setStatus("1");
                        ScheduleUtils.createScheduleJob(scheduler,sysJob);
                        return returnObject;

                    }else {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        returnObject.setMsg("????????????");
                        returnObject.setStatus("0");
                        return returnObject;
                    }
                }
            }

        } catch (Exception e) {

        }
        return returnObject;
    }

    /**
     *
     * @Description: ??????????????????
     *
     * @auther: wanghongjie
     * @date: 11:03 2021/7/31
     * @param: [map]
     * @return: com.core.common.ISSPReturnObject
     *
     */
    public ISSPReturnObject deleteJob(Map<String,Object> map){

        ISSPReturnObject returnObject = new ISSPReturnObject();

        String jobId = (String) map.get("job_id");

        String jobGroup = (String) map.get("job_group");

        //??????????????????
        try {
            scheduler.deleteJob(getJobKey(jobId, jobGroup));
            returnObject.setStatus("1");
        } catch (SchedulerException e) {
            returnObject.setStatus("0");
            e.printStackTrace();
        }
        return returnObject;
    }
}
