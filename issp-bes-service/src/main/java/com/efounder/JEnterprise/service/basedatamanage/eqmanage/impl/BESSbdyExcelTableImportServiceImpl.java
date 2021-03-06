package com.efounder.JEnterprise.service.basedatamanage.eqmanage.impl;

import com.alibaba.fastjson.JSONObject;
import com.core.common.ISSPReturnObject;
import com.core.common.constant.BesNodeType;
import com.efounder.JEnterprise.common.util.DateUtil;
import com.efounder.JEnterprise.common.util.UUIDUtil;
import com.efounder.JEnterprise.initializer.*;
import com.efounder.JEnterprise.mapper.basedatamanage.eqmanage.*;
import com.efounder.JEnterprise.model.basedatamanage.deviceConfiguration.*;
import com.efounder.JEnterprise.model.basedatamanage.eqmanage.*;
import com.efounder.JEnterprise.model.excelres.ExcelError;
import com.efounder.JEnterprise.model.excelres.ExcelReturn;
import com.efounder.JEnterprise.model.excelres.Pzlj;
import com.efounder.JEnterprise.service.basedatamanage.eqmanage.BESSbdyExcelTableImportService;
import com.framework.common.utils.ExcelUtil;
import org.apache.shiro.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 8:30 2020/9/18
 * @Modified By:
 */
@Service("BESSbdyExcelTableImportService")
public class BESSbdyExcelTableImportServiceImpl implements BESSbdyExcelTableImportService {
    private static final Logger log = LoggerFactory.getLogger(BESSbdyExcelTableImportServiceImpl.class);
    @Autowired
    private Pzlj pzlj;// ????????????????????????

    @Autowired
    private BESSbdyMapper besSbdyMapper;
    @Autowired
    private BESSbdyExcelTableImportMapper besSbdyExcelTableImportMapper;

    @Autowired
    private SbPzStructCache sbPzStructCache;

    @Autowired
    private BESSbdyMapper besSbPzStructMapper;

    @Autowired
    private BesCollectorMapper besCollectorMapper;

    @Autowired
    private CollectorCache collectorCache;


    @Autowired
    private DdcCache ddcCache;
    @Autowired
    private BesDdcMapper besDdcMapper;

    @Autowired
    private AmmeterCache ammeterCache;

    @Autowired
    private BESAmmeterMapper besAmmeterMapper;

    @Autowired
    private AiPointCache aiPointCache;

    @Autowired
    private AoPointCache aoPointCache;

    @Autowired
    private DiPointCache diPointCache;

    @Autowired
    private DoPointCache doPointCache;

    @Autowired
    private BesAiPointMapper besAiPointMapper;

    @Autowired
    private BesAoPointMapper besAoPointMapper;

    @Autowired
    private BesDiPointMapper besDiPointMapper;

    @Autowired
    private BesDoPointMapper besDoPointMapper;

    @Autowired
    private VirtualPointCache virtualPointCache;

    @Autowired
    private BesVirtualPointMapper besVirtualPointMapper;


    /**
     * ??????????????????????????????
     */
    @Override
    @Transactional(propagation = Propagation.NESTED)
    public ISSPReturnObject impExcel(HttpServletRequest request,
                                     @RequestParam(required = false, value = "file") MultipartFile multipartFile) throws IOException,Exception {
        ISSPReturnObject returnObject = new ISSPReturnObject();
        String fold = request.getParameter("fold");
        String dayFold = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String realPath = pzlj.getUploadPath();// ??????????????????????????????
        // ????????????????????????request
        // ????????????file??????
        if (multipartFile != null) {
            if (multipartFile.getSize() != 0L) {
                // ??????????????????
                String pictureFile_name = multipartFile.getOriginalFilename();
                // uuid
                String UUID = UUIDUtil.getRandom32BeginTimePK();
                // ???????????????
                String wjmc_url = UUID + pictureFile_name.substring(pictureFile_name.lastIndexOf("."));
                // ????????????
                String fileUrl = realPath + "/" + fold + "/" + dayFold + "/" + wjmc_url;
                // ????????????
                File uploadPic = new File(fileUrl);
                if (!uploadPic.getParentFile().exists()) {
                    uploadPic.getParentFile().mkdirs();// ??????????????????
                    uploadPic.createNewFile();// ????????????
                }
                // ??????????????????
//                multipartFile.transferTo(uploadPic);

                //?????????????????????
                OutputStream out = new FileOutputStream(uploadPic);
                out.write(multipartFile.getBytes());
                // ????????????????????????
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(fileUrl);
                    ExcelUtil<sbdyStruct> util = new ExcelUtil<>(sbdyStruct.class);
                    List<sbdyStruct> list = util.importExcel("?????????", fis);// ??????excel,???????????????list

                    if (list.size() >0) {

                        if (list.get(0).getfNodeattribution().equals("3")) {//??????

                            returnObject = energy(list,fileUrl);

                        } else if (list.get(0).getfNodeattribution().equals("2")) {//????????????

                            returnObject = lightingControl(list,fileUrl);

                        } else if (list.get(0).getfNodeattribution().equals("1")) {//????????????

                            returnObject = buildingAutomation(list,fileUrl);

                        }
                    }


                    // ????????????????????? ???????????????list??????????????????
                }  catch (FileNotFoundException e) {
                    returnObject.setStatus("0");
                    returnObject.setMsg("???????????????");
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    returnObject.setStatus("0");
                    returnObject.setMsg("???????????????");
                    e.printStackTrace();
                } catch (Exception e) {
                    returnObject.setStatus(returnObject.getStatus());
                    returnObject.setMsg(returnObject.getMsg());
//					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    e.printStackTrace();
                }
            }
        } else {
            returnObject.setMsg("?????????????????????");
            returnObject.setStatus("0");
        }
        return returnObject;
    }

    /**
     * ????????????
     */
    @Override
    public ISSPReturnObject exportPoint(HttpServletRequest request) {
        ISSPReturnObject returnObject = new ISSPReturnObject();
        // ???????????????.
        ExcelUtil<sbdyStructExport> util = new ExcelUtil<>(sbdyStructExport.class);
        // ???????????????
        String file = System.currentTimeMillis() + "";
        String FileName = "sheet";// sheet?????????
        String FilePath = request.getServletContext().getRealPath("/") + "WEB-INF\\file\\excel\\" + file + ".xls";
        String fPointName = request.getParameter("f_pointName");

        //??????????????????????????????
        List<BESSbPzStruct> cacheSbPzStruct = sbPzStructCache.getCascadeSubordinate(fPointName);
        //????????????,???List<BESSbPzStruct>??????List<sbdyStruct>
        List<sbdyStructExport> sbdyStructList = cacheSbPzStruct.stream().map(item->
                {
                    sbdyStructExport map = new sbdyStructExport();
                    map.setfId(item.getF_id());
                    map.setfType(item.getF_type());
                    map.setfSysname(item.getF_sys_name());
                    map.setfNickname(item.getF_nick_name());
                    map.setfDescription(item.getF_description());
                    map.setfNodeattribution(item.getF_node_attribution());
                    map.setfPsysname(item.getF_psys_name());
                    return map;
                }
        ).collect(Collectors.toList());
        //???????????????
        ExcelReturn res = util.resList(FileName, FilePath, sbdyStructList);
        Map<String, String> map = new HashMap<>();
        map.put("status", res.getStatus());// 1.?????? 0.??????
        map.put("file", file);
        returnObject.setMap(map);
        return returnObject;
    }

    /**
     * ?????????????????????
     */
    @Override
    public void syncCache() {

        List<BESSbPzStruct> besSbPzStructs = besSbPzStructMapper.findAll();

        if (besSbPzStructs == null || besSbPzStructs.isEmpty())
        {
            return;
        }

        besSbPzStructs.forEach(besSbPzStruct -> {

            String sysName = besSbPzStruct.getF_sys_name();

            BESSbPzStruct besSbPzStruct1 = sbPzStructCache.getCachedElement(sysName);

            String type = besSbPzStruct.getF_type();


            if (besSbPzStruct1 != null)
            {
                if (StringUtils.hasText(besSbPzStruct1.getF_init_val()))
                {
                    return;
                }


                // ???????????????ai???
                if (BesNodeType.AI.equals(type))
                {
                    BesAiPoint besAiPoint = besAiPointMapper.queryAiPoint(sysName);
                    aiPointCache.updateOneAiPointCache(besAiPoint);
                    // ????????????????????????????????????
                    sbPzStructCache.updateOneSbPzStructCache(besSbPzStruct);

                    return;
                }
                // ???????????????ao???
                if (BesNodeType.AO.equals(type))
                {
                    BesAoPoint besAoPoint = besAoPointMapper.queryAoPoint(sysName);
                    aoPointCache.updateOneAoPointCache(besAoPoint);
                    // ????????????????????????????????????
                    sbPzStructCache.updateOneSbPzStructCache(besSbPzStruct);

                    return;
                }
                // ???????????????di???
                if (BesNodeType.DI.equals(type))
                {
                    BesDiPoint besDiPoint = besDiPointMapper.queryDiPoint(sysName);
                    diPointCache.updateOneDiPointCache(besDiPoint);
                    // ????????????????????????????????????
                    sbPzStructCache.updateOneSbPzStructCache(besSbPzStruct);

                    return;
                }
                // ???????????????do???
                if (BesNodeType.DO.equals(type))
                {
                    BesDoPoint besDoPoint = besDoPointMapper.queryDoPoint(sysName);
                    doPointCache.updateOneDoPointCache(besDoPoint);
                    // ????????????????????????????????????
                    sbPzStructCache.updateOneSbPzStructCache(besSbPzStruct);

                }

                return;
            }

            // ????????????????????????????????????
            sbPzStructCache.addOneSbPzStructCache(besSbPzStruct);

            // ??????????????????????????????
            if (BesNodeType.COLLECTOR.equals(type))
            {
                BesCollector besCollector = besCollectorMapper.selectBySysName(sysName);

                // ??????????????????????????????
                collectorCache.addOneCollectorCache(besCollector);

                return;
            }

            // ????????????DDC ?????????
            if (BesNodeType.DDC.equals(type) || BesNodeType.IP_ROUTER.equals(type))
            {
                BesDdc besDdc = besDdcMapper.selectBySysName(sysName);
                ddcCache.addOneDdcCache(besDdc);

                return;
            }

            // ?????????????????????
            if (BesNodeType.AMMETER.equals(type))
            {
                BESAmmeter besAmmeter = besAmmeterMapper.queryAmmeter(sysName);
                ammeterCache.addOneAmmeterCache(besAmmeter);

                return;
            }

            // ???????????????ai???
            if (BesNodeType.AI.equals(type))
            {
                BesAiPoint besAiPoint = besAiPointMapper.queryAiPoint(sysName);
                aiPointCache.addOneAiPointCache(besAiPoint);

                return;
            }
            // ???????????????ao???
            if (BesNodeType.AO.equals(type))
            {
                BesAoPoint besAoPoint = besAoPointMapper.queryAoPoint(sysName);
                aoPointCache.addOneAoPointCache(besAoPoint);

                return;
            }
            // ???????????????di???
            if (BesNodeType.DI.equals(type))
            {
                BesDiPoint besDiPoint = besDiPointMapper.queryDiPoint(sysName);
                diPointCache.addOneDiPointCache(besDiPoint);

                return;
            }
            // ???????????????do???
            if (BesNodeType.DO.equals(type))
            {
                BesDoPoint besDoPoint = besDoPointMapper.queryDoPoint(sysName);
                doPointCache.addOneDoPointCache(besDoPoint);

                return;
            }

            // ?????????????????????
            if (BesNodeType.VPOINT.equals(type))
            {
                BesVirtualPoint besVirtualPoint = besVirtualPointMapper.queryVirtualPoint(sysName);
                virtualPointCache.addOneVirtualPointCache(besVirtualPoint);
            }


        });

    }


    @Transactional(propagation = Propagation.NESTED)
    public ISSPReturnObject energy(List<sbdyStruct> list,String fileUrl){
        ISSPReturnObject returnObject = new ISSPReturnObject();
        // ????????????????????????
        List<ExcelError> excelErrors = new ArrayList<>();
        boolean inportflag = false;
        Map<String, Object> psysName = null;
        // ????????????????????????
        FileInputStream fis = null;

        String nodeTabName = "???????????????";


        try {

            Set<String> point = new HashSet<>();
            for (int i = 0; i < list.size(); i++) {

                //????????????????????????????????????????????????
                Boolean success = point.add(list.get(i).getfSysname());
                if (!success) {
                    throw new Exception(list.get(i).getfSysname() +":???????????????????????????????????????");
                }
                boolean flag = true; //???????????????????????????
                sbdyStruct sbdy = list.get(i);
                String date = DateUtil.getCurrTime();
                String errMsg = "";
                if (sbdy.getfType() == null || sbdy.getfType().equals("")) {
                    errMsg = "??????????????????";
                    flag = false;
                } else if (sbdy.getfSysname() == null || sbdy.getfSysname().equals("")) {
                    if ("".equals(errMsg)) {
                        errMsg = "??????????????????";
                        flag = false;
                    } else {
                        errMsg = errMsg + ",????????????????????????";
                        flag = false;
                    }
                } else if (sbdy.getfDescription() == null || sbdy.getfDescription().equals("")) {
                    if ("".equals(errMsg)) {
                        errMsg = "????????????";
                        flag = false;
                    } else {
                        errMsg = errMsg + ",????????????";
                        flag = false;
                    }

                } else if (sbdy.getfPsysname() == null || sbdy.getfPsysname().equals("")) {

                    if ("".equals(errMsg)) {
                        errMsg = "???????????????????????????";
                        flag = false;
                    } else {
                        errMsg = errMsg + ",???????????????????????????";
                        flag = false;
                    }
                } else if (sbdy.getfNickname() == null || sbdy.getfNickname().equals("")) {

                    if ("".equals(errMsg)) {
                        errMsg = "????????????";
                        flag = false;
                    } else {
                        errMsg = errMsg + ",????????????";
                        flag = false;
                    }
                }else if (sbdy.getfNodeattribution() == null || sbdy.getfNodeattribution().equals("")) {

                    if ("".equals(errMsg)) {
                        errMsg = "??????????????????";
                        flag = false;
                    } else {
                        errMsg = errMsg + ",??????????????????";
                        flag = false;
                    }
                }

                if (!flag) {
                    ExcelError excelError = new ExcelError();
                    excelError.setRow((i + 2) + "");
                    excelError.setErrorMsg(errMsg);
                    excelErrors.add(excelError);
                }
            }

            /*if (excelErrors.size() > 0) {
                returnObject.setMsg("?????????????????????????????????????????????excel???????????????");
                returnObject.setStatus("2");
                returnObject.setList(excelErrors);
//						return returnObject;
                throw new Exception((Throwable) returnObject.getList());
            }*/

            if (excelErrors.size() > 0) {
                throw new Exception(nodeTabName+"???" + excelErrors.get(0).getRow() + "???" +excelErrors.get(0).getErrorMsg());
            }

            for (sbdyStruct sbdyStructs : list) {

                if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                    return returnObject;
                }
                switch (sbdyStructs.getfType()) {

                    case "31": {//??????????????????


                        returnObject = insertBesStruct(sbdyStructs,nodeTabName);

                        if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                            return returnObject;
                        }

                        break;
                    }
                    case "26": {//?????????????????????

                        fis = new FileInputStream(fileUrl);
                        ExcelUtil<besCollectorExcel> util = new ExcelUtil<>(besCollectorExcel.class);
                        List<besCollectorExcel> collectorlist = util.importExcel("?????????????????????", fis);// ??????excel,???????????????list

                        List lists=new ArrayList();

                        Set<String> point_collector = new HashSet<>();
                        for (besCollectorExcel besCollectorExcel : collectorlist) {

                            //???????????????????????????????????????????????????????????????
                            Boolean success = point_collector.add(besCollectorExcel.getfSysName());
                            if (!success) {
                                throw new Exception(besCollectorExcel.getfSysName() +":????????????????????????????????????????????????");
                            }
                            lists.add(besCollectorExcel.getfSysName());
                        }
                        Boolean aa = lists.contains(sbdyStructs.getfSysname());

                        if (!aa) {
                            throw new Exception(sbdyStructs.getfSysname() +"???????????????????????????????????????????????????");
                        }
                        nodeTabName = "????????????????????????";
                        returnObject = insertBesStruct(sbdyStructs,nodeTabName);

                        if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                            return returnObject;
                        }

                        for (besCollectorExcel collectorExcel : collectorlist) {

                            String f_guid 					= UUIDUtil.getRandom32BeginTimePK();
                            String f_sys_name 				= collectorExcel.getfSysName();
                            String f_nick_name 				= collectorExcel.getfNickName();
                            String f_ssqy 					= collectorExcel.getfSsqy();
                            String f_azwz 					= collectorExcel.getfAzwz();
                            String f_description 			= collectorExcel.getfDescription();
                            String collectorIp 				= collectorExcel.getfIpAddr();
                            String f_coll_cycle 			= collectorExcel.getfCollCycle();
                            String f_node_type 				= collectorExcel.getfNodeType();
                            String f_his_data_save_cycle 	= collectorExcel.getfHisDataSaveCycle();
                            String yqbh 					= collectorExcel.getfYqbh();
                            String f_upload_cycle 			= collectorExcel.getfUploadCycle();
                            String f_gateway 				= collectorExcel.getfGateway();
                            String f_mask 					= collectorExcel.getfMask();
                            String f_ip_master 				= collectorExcel.getfIpMaster();
                            String f_port_master 			= collectorExcel.getfPortMaster();

                            if (       !StringUtils.hasText(f_sys_name)
                                    || !StringUtils.hasText(f_nick_name)
                                    || !StringUtils.hasText(f_ssqy)
                                    || !StringUtils.hasText(f_azwz)
                                    || !StringUtils.hasText(f_description)
                                    || !StringUtils.hasText(collectorIp)
                                    || !StringUtils.hasText(f_coll_cycle)
                                    || !StringUtils.hasText(f_node_type)
                                    || !StringUtils.hasText(f_his_data_save_cycle)
                                    || !StringUtils.hasText(yqbh)
                                    || !StringUtils.hasText(f_upload_cycle)
                                    || !StringUtils.hasText(f_gateway)
                                    || !StringUtils.hasText(f_mask)
                                    || !StringUtils.hasText(f_ip_master)
                                    || !StringUtils.hasText(f_port_master)
                            )
                            {
                                throw new Exception(f_sys_name + ":??????????????????????????????????????????");
                            }


                            String collectorTableName = "bes_collector";

                            if (sbdyStructs.getfSysname().equals(f_sys_name)) {

                                //???????????????????????????????????????????????????
                                if (null == besSbdyExcelTableImportMapper.getSbTreeInfoBySysName(f_sys_name)) {
                                    throw new Exception(f_sys_name + ":????????????????????????????????????");
                                }

                                // ??????ip????????????????????????
                                int besDDCSize = besSbdyExcelTableImportMapper.getSizeByIpAddrBesDdc(collectorIp);
                                int besCollectorSize = besSbdyExcelTableImportMapper.getSizeByIpAddrBesCollector(collectorIp);
                                if (besDDCSize == 1 || besCollectorSize == 1){

                                    throw new Exception(collectorIp +":ip ????????????");
                                }

                                // ????????????id
                                String maxId = besSbdyExcelTableImportMapper.queryCollectorMaxId();
                                String id = getAutoIncreaseCol(maxId);
                                String f_sbid = id;

                                BesCollector besCollector = new BesCollector();

                                besCollector.setfGuid(f_guid);
                                besCollector.setfSbId(f_sbid);
                                besCollector.setfSysName(f_sys_name);
                                besCollector.setfNickName(f_nick_name);
                                besCollector.setfEnabled(1);
                                besCollector.setfSsqy(f_ssqy);
                                besCollector.setfAzwz(f_azwz);
                                besCollector.setfDescription(f_description);
                                besCollector.setfIpAddr(collectorIp);
                                besCollector.setfCollCycle(Integer.parseInt(f_coll_cycle));
                                besCollector.setfNodeType(f_node_type);
                                besCollector.setfHisDataSaveCycle(Integer.parseInt(f_his_data_save_cycle));
                                besCollector.setfParkYqbh(yqbh);
                                besCollector.setfCollectorState("0");
                                besCollector.setfOnlineState("0");
                                besCollector.setfChannelId(collectorIp);
                                besCollector.setfUploadCycle(f_upload_cycle);
                                besCollector.setfGateway(f_gateway);
                                besCollector.setfMask(f_mask);
                                besCollector.setfIpMaster(f_ip_master);
                                besCollector.setfPortMaster(f_port_master);

                                inportflag = besSbdyExcelTableImportMapper.add_sbdyStructCollector(besCollector);
                            }
                        }
                        if (inportflag) {
                            returnObject.setMsg("???????????????");
                            returnObject.setStatus("1");
                        } else {
                            throw new Exception("???????????????????????????????????????????????????????????????");
//						returnObject.setMsg("??????????????????????????????????????????????????????????????????");
                        }

                        break;

                    }
                    case "27": {//??????????????????
                        fis = new FileInputStream(fileUrl);
                        ExcelUtil<besBusExcel> util = new ExcelUtil<>(besBusExcel.class);
                        List<besBusExcel> buslist = util.importExcel("??????????????????", fis);// ??????excel,???????????????list

                        List lists=new ArrayList();

                        Set<String> point_bus = new HashSet<>();
                        for (besBusExcel besBusExcel : buslist) {
                            //????????????????????????????????????????????????
                            Boolean success = point_bus.add(besBusExcel.getfSysName());
                            if (!success) {
                                throw new Exception(besBusExcel.getfSysName() +":?????????????????????????????????????????????");
                            }

                            lists.add(besBusExcel.getfSysName());
                        }



                        Boolean aa = lists.contains(sbdyStructs.getfSysname());

                        if (!aa) {
                            throw new Exception(sbdyStructs.getfSysname() +":????????????????????????????????????????????????");
                        }

                        nodeTabName = "???????????????";
                        returnObject = insertBesStruct(sbdyStructs,nodeTabName);

                        if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                            return returnObject;
                        }

                        for (besBusExcel besBusExcel : buslist) {

                            String f_sys_name = besBusExcel.getfSysName();
                            String f_nick_name = besBusExcel.getfNickName();
                            String f_port = besBusExcel.getfPort();

                            if (sbdyStructs.getfSysname().equals(f_sys_name)) {

                                if (!StringUtils.hasText(f_sys_name)
                                        || !StringUtils.hasText(f_nick_name)
                                        || !StringUtils.hasText(f_port)
                                ) {
                                    throw new Exception(f_sys_name + "???????????????????????????????????????");
                                }


                                //???????????????????????????????????????????????????
                                if (null == besSbdyExcelTableImportMapper.getSbTreeInfoBySysName(f_sys_name)) {
                                    throw new Exception(f_sys_name + "????????????????????????????????????");
                                }

                                inportflag = besSbdyExcelTableImportMapper.add_sbdyStructBus(besBusExcel);
                            }
                        }
                        if (inportflag) {
                            returnObject.setMsg("???????????????");
                            returnObject.setStatus("1");
                        } else {
                            throw new Exception("????????????");
//						returnObject.setMsg("???????????????");
//						returnObject.setStatus("0");
//						return returnObject;
                        }
                        break;
                    }
                    case "28": {//??????????????????
                        fis = new FileInputStream(fileUrl);
                        ExcelUtil<besAmmeterExcel> util = new ExcelUtil<>(besAmmeterExcel.class);
                        List<besAmmeterExcel> ammeterlist = util.importExcel("??????????????????", fis);// ??????excel,???????????????list

                        List lists=new ArrayList();

                        Set<String> point_ammeter = new HashSet<>();
                        for (besAmmeterExcel besAmmeterExcel : ammeterlist) {
                            //??????????????????????????????????????????????????????
                            Boolean success = point_ammeter.add(besAmmeterExcel.getfSysName());
                            if (!success) {
                                throw new Exception(besAmmeterExcel.getfSysName() +":???????????????????????????????????????");
                            }

                            lists.add(besAmmeterExcel.getfSysName());
                        }
                        Boolean aa = lists.contains(sbdyStructs.getfSysname());

                        if (!aa) {
                            throw new Exception(sbdyStructs.getfSysname() +":????????????????????????????????????????????????");
                        }
                        nodeTabName = "?????????????????????";
                        returnObject = insertBesStruct(sbdyStructs,nodeTabName);
                        if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                            return returnObject;
                        }


                        for (besAmmeterExcel besAmmeterExcel : ammeterlist) {

                            String f_sys_name = besAmmeterExcel.getfSysName();            //????????????
                            String f_nick_name = besAmmeterExcel.getfNickName();            //??????
                            String f_azwz = besAmmeterExcel.getfAzwz();                //????????????
                            String f_wldz = besAmmeterExcel.getfWldz();                //????????????
                            String f_description = besAmmeterExcel.getfDescription();        //??????
                            String f_blxbh = besAmmeterExcel.getfBlxbh();				//??????????????????
                            String f_comm_rate = besAmmeterExcel.getfCommRate();            //?????????????????????
                            String f_protocol_type = besAmmeterExcel.getfProtocolType();        //??????????????????
                            String f_cjfabh = besAmmeterExcel.getfCjfabh();                //??????????????????
                            String f_communication_port = besAmmeterExcel.getfCommunicationPort();    //????????????
                            String f_yqbh = besAmmeterExcel.getfYqbh();                //????????????
                            String f_percentage = besAmmeterExcel.getfPercentage();            //??????
                            String f_com_data_bit = besAmmeterExcel.getfComDataBit();            //?????????
                            String f_com_parity_bit = besAmmeterExcel.getfComParityBit();        //?????????
                            String f_com_stop_bit = besAmmeterExcel.getfComStopBit();            //?????????
                            String f_function_code = besAmmeterExcel.getfFunctionCode();        //?????????

                            if (sbdyStructs.getfSysname().equals(f_sys_name)) {

                                if (!StringUtils.hasText(f_sys_name)
                                        || !StringUtils.hasText(f_nick_name)
                                        || !StringUtils.hasText(f_azwz)
                                        || !StringUtils.hasText(f_wldz)
                                        || !StringUtils.hasText(f_description)
                                        || !StringUtils.hasText(f_blxbh)
                                        || !StringUtils.hasText(f_comm_rate)
                                        || !StringUtils.hasText(f_protocol_type)
                                        || !StringUtils.hasText(f_cjfabh)
                                        || !StringUtils.hasText(f_communication_port)
                                        || !StringUtils.hasText(f_yqbh)
                                        || !StringUtils.hasText(f_percentage)
                                        || !StringUtils.hasText(f_com_data_bit)
                                        || !StringUtils.hasText(f_com_parity_bit)
                                        || !StringUtils.hasText(f_com_stop_bit)
                                        || !StringUtils.hasText(f_function_code)

                                ) {
                                    throw new Exception(f_sys_name + ":???????????????????????????????????????");
                                }

                                //???????????????????????????????????????????????????
                                if (null == besSbdyExcelTableImportMapper.getSbTreeInfoBySysName(f_sys_name)) {
                                    throw new Exception(f_sys_name + ":????????????????????????????????????");
                                }

                                //??????????????????????????????????????????????????????
                                String f_comm_rate_mc = besSbdyExcelTableImportMapper.selectF_comm_rate_mc(f_comm_rate);
                                if (f_comm_rate_mc == null) {
                                    throw new Exception(f_sys_name + ":????????????????????????????????????????????????????????????,?????????");
                                }
                                //??????????????????????????????????????????????????????
                                String f_protocol_type_mc = besSbdyExcelTableImportMapper.selectF_protocol_type_mc(f_protocol_type);
                                if (f_protocol_type_mc == null) {
                                    throw new Exception(f_sys_name + ":???????????????????????????????????????????????????????????????,?????????");
                                }
                                //????????????????????????????????????????????????
                                String f_cjfamc = besSbdyExcelTableImportMapper.selectF_cjfabh_mc(f_cjfabh);
                                if (f_cjfamc == null) {
                                    throw new Exception(f_sys_name + ":?????????????????????????????????????????????????????????,?????????");
                                }
                                //????????????????????????????????????????????????
                                String f_blxmc = besSbdyExcelTableImportMapper.selectF_blxbh_mc(f_blxbh);
                                if (f_blxmc == null) {
                                    throw new Exception(f_sys_name + ":?????????????????????????????????????????????????????????,?????????");
                                }

                                String f_guid = UUIDUtil.getRandom32BeginTimePK();

                                // ????????????id
                                String maxId = besSbdyExcelTableImportMapper.queryAmmeterMaxId();
                                String meterID = getAutoIncreaseCol(maxId);
                                String f_sbid = meterID;

                                BESAmmeter besAmmeter = new BESAmmeter();
                                besAmmeter.setfGuid(f_guid);
                                besAmmeter.setfSbid(f_sbid);
                                besAmmeter.setfSysName(f_sys_name);
                                besAmmeter.setfNickName(f_nick_name);
                                besAmmeter.setfEnabled(1);
                                besAmmeter.setfAzwz(f_azwz);
                                ;
                                besAmmeter.setfWldz(f_wldz);
                                besAmmeter.setfDescription(f_description);
                                besAmmeter.setfType("0");
                                besAmmeter.setfCommRate(f_comm_rate);
                                besAmmeter.setfCommRateMc(f_comm_rate_mc);
                                besAmmeter.setfProtocolType(f_protocol_type);
                                besAmmeter.setfProtocolTypeMc(f_protocol_type_mc);
                                besAmmeter.setfCjfabh(f_cjfabh);
                                besAmmeter.setfCjfamc(f_cjfamc);
                                besAmmeter.setfBlxbh(f_blxbh);
                                besAmmeter.setfBlxmc(f_blxmc);
                                besAmmeter.setfCommunicationPort(f_communication_port);
                                besAmmeter.setfYqbh(f_yqbh);
                                besAmmeter.setfPercentage(f_percentage);
                                besAmmeter.setfAmmeterState("0");
                                besAmmeter.setfComDataBit(f_com_data_bit);
                                besAmmeter.setfComParityBit(f_com_parity_bit);
                                besAmmeter.setfComStopBit(f_com_stop_bit);
                                besAmmeter.setfFunctionCode(f_function_code);

                                inportflag = besSbdyExcelTableImportMapper.add_sbdyStructAmmeter(besAmmeter);
                            }
                        }
                        if (inportflag) {
                            returnObject.setMsg("???????????????");
                            returnObject.setStatus("1");
                        } else {
                            throw new Exception("????????????");
//								returnObject.setMsg("???????????????");
//								returnObject.setStatus("0");
//								return returnObject;
                        }

                        break;
                    }

                    default:
                        throw new Exception("?????????????????????");
//					returnObject.setMsg("?????????????????????");
//					returnObject.setStatus("0");
//					return returnObject;
                }

            }
        }catch (FileNotFoundException | NullPointerException e) {
            returnObject.setStatus("0");
            returnObject.setMsg("???????????????");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception e) {
            returnObject.setStatus("0");
            returnObject.setMsg(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return returnObject;
    }

    @Transactional(propagation = Propagation.NESTED)
    public ISSPReturnObject insertBesStruct(sbdyStruct sbdy,String nodeTabName){
        ISSPReturnObject returnObject = new ISSPReturnObject();
        Map<String, Object> psysName = null;
        boolean  inportflag = false;

        try {

            //????????????????????????????????????????????????????????????
            psysName = besSbdyExcelTableImportMapper.selectSbdyByPsysName(sbdy.getfPsysname());

            if (psysName == null) {
                throw new Exception(nodeTabName+ "???" + sbdy.getfSysname() + ":??????????????????");
            }

            if (null != besSbdyExcelTableImportMapper.getSbTreeInfoBySysName(sbdy.getfSysname())) {
                throw new Exception(nodeTabName+ "???" + sbdy.getfSysname() + ":????????????????????????");
            }

            String allpath = (String) psysName.get("F_ALLPATH");//?????????????????????
            allpath = allpath + ">" + sbdy.getfSysname();
            String f_status = "0";//??????

            BESSbPzStruct besSbPzStruct = new BESSbPzStruct();
            besSbPzStruct.setF_type(sbdy.getfType());
            besSbPzStruct.setF_sys_name(sbdy.getfSysname());
            besSbPzStruct.setF_node_attribution(sbdy.getfNodeattribution());
            besSbPzStruct.setF_nick_name(sbdy.getfNickname());
            besSbPzStruct.setF_psys_name(sbdy.getfPsysname());
            besSbPzStruct.setF_description(sbdy.getfDescription());
            besSbPzStruct.setF_allpath(allpath);
            besSbPzStruct.setF_status(f_status);


            inportflag = besSbdyExcelTableImportMapper.add_sbdyStruct(besSbPzStruct);

            //???????????????,???????????????,?????????bes_sbdy_struct???????????????
            Boolean aa =  besSbdyExcelTableImportMapper.add_sbdyStructCopy(besSbPzStruct);

            if (inportflag) {
               if (aa) {
                   returnObject.setMsg("???????????????");
                   returnObject.setStatus("1");
                   returnObject.setData(allpath);
               }

            } else {
                throw new Exception("????????????");
//				returnObject.setMsg("???????????????");
//				returnObject.setStatus("0");
//				return returnObject;
            }

        } catch (FileNotFoundException | NullPointerException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception e) {
            returnObject.setStatus("0");
            returnObject.setMsg(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return returnObject;
    }

    /**
     *
     * @Description: ????????????
     *
     * @auther: wanghongjie
     * @date: 9:33 2020/10/11
     * @param: [list, fileUrl]
     * @return: com.core.common.ISSPReturnObject
     *
     */
    @Transactional(propagation = Propagation.NESTED)
    public ISSPReturnObject buildingAutomation(List<sbdyStruct> list, String fileUrl) {
        ISSPReturnObject returnObject = new ISSPReturnObject();

        // ????????????????????????
        List<ExcelError> excelErrors = new ArrayList<>();
        boolean inportflag = false;
        Map<String, Object> psysName = null;
        // ????????????????????????
        FileInputStream fis = null;
        //???ecxcel????????????????????????list??????,????????????
        List<besPointExcel> pointList = null;
        List<besModuleExcel> Modulelist = null;
        List<besDDCExcel> DDClist= null;

        String nodeTabName = "?????????????????????";

        try {

            excelErrors = nodeTable(list);

            /*if (excelErrors.size() > 0) {
                returnObject.setMsg("?????????????????????????????????????????????excel???????????????");
                returnObject.setStatus("2");
                returnObject.setList(excelErrors);
//						return returnObject;
                throw new Exception((Throwable) returnObject.getList());
            }*/

            if (excelErrors.size() > 0) {
                throw new Exception(nodeTabName + "???" + excelErrors.get(0).getRow() + "???" +excelErrors.get(0).getErrorMsg());
            }

            Set<String> point = new HashSet<>();

            for (sbdyStruct sbdyStructs : list) {
                //????????????????????????????????????????????????????????????
                Boolean success = point.add(sbdyStructs.getfSysname());
                if (!success) {
                    throw new Exception(sbdyStructs.getfSysname() +":?????????????????????????????????????????????");
                }
            }
            for (sbdyStruct sbdyStructs : list) {


                switch (sbdyStructs.getfType()) {
                    case "1":
                    case "24":
                    case "8":
                    case "23":	{//??????????????????,??????(???????????????),??????,??????

                        returnObject = insertBesStruct(sbdyStructs,nodeTabName);
                        if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                            return returnObject;
                        }


                        break;
                    }
                    case "2": {//?????????????????????

                        if (DDClist == null) {
                            fis = new FileInputStream(fileUrl);
                            ExcelUtil<besDDCExcel> util = new ExcelUtil<>(besDDCExcel.class);
                            DDClist = util.importExcel("?????????????????????", fis);// ??????excel,???????????????list
                        }

                        returnObject = insertDDCByExcel(DDClist,sbdyStructs);

                        if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                            return returnObject;
                        }

                        break;

                    }
                    case "9":{//??????

                        if (Modulelist == null) {
                            fis = new FileInputStream(fileUrl);
                            ExcelUtil<besModuleExcel> util = new ExcelUtil<>(besModuleExcel.class);
                            Modulelist = util.importExcel("????????????", fis);// ??????excel,???????????????list
                        }

                        returnObject = insertModuleByExcel(Modulelist,sbdyStructs);

                        if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                            return returnObject;
                        }

                        break;
                    }
                    case "DO":
                    case "DI":
                    case "AO":
                    case "AI": {

                        if (pointList == null) {
                            fis = new FileInputStream(fileUrl);
                            ExcelUtil<besPointExcel> util = new ExcelUtil<>(besPointExcel.class);
                            pointList = util.importExcel("????????????", fis);// ??????excel,???????????????list
                        }

                        List lists=new ArrayList();

                        Set<String> point_DODIAOAI = new HashSet<>();
                        for (besPointExcel besPointExcel : pointList) {
                            //??????????????????????????????????????????????????????
                            Boolean success1 = point_DODIAOAI.add(besPointExcel.getfSysNameOld());
                            if (!success1) {
                                throw new Exception(besPointExcel.getfSysNameOld() +":???????????????????????????????????????");
                            }
                            lists.add(besPointExcel.getfSysNameOld());
                        }
                        Boolean aa = lists.contains(sbdyStructs.getfSysname());

                        if (!aa) {
                            throw new Exception(sbdyStructs.getfSysname() +"??????????????????????????????????????????");
                        }

                        returnObject = pointInsertByExcel(pointList,sbdyStructs);

                        if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                            return  returnObject;
                        }
                        break;
                    }
                    default:
                        throw new Exception("?????????????????????");
                }
            }

        } catch (FileNotFoundException | NullPointerException e) {
            returnObject.setStatus("0");
            returnObject.setMsg("???????????????");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception e) {
            returnObject.setStatus("0");
            returnObject.setMsg(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return returnObject;
    }


    /**
     *
     * @Description: ?????????????????????????????????
     *
     * @auther: wanghongjie
     * @date: 10:36 2020/9/14
     * @param: [list]
     * @return: java.util.List<com.efounder.JEnterprise.model.excelres.ExcelError>
     *
     */
    private List<ExcelError> nodeTable(List<sbdyStruct> list) {
        List<ExcelError> excelErrors = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            boolean flag = true; //???????????????????????????
            sbdyStruct sbdy = list.get(i);
            String date = DateUtil.getCurrTime();
            String errMsg = "";
            if (sbdy.getfType() == null || sbdy.getfType().equals("")) {
                errMsg = "??????????????????";
                flag = false;
            } else if (sbdy.getfSysname() == null || sbdy.getfSysname().equals("")) {
                if ("".equals(errMsg)) {
                    errMsg = "??????????????????";
                    flag = false;
                } else {
                    errMsg = errMsg + ",??????????????????";
                    flag = false;
                }
            } else if (sbdy.getfDescription() == null || sbdy.getfDescription().equals("")) {
                if ("".equals(errMsg)) {
                    errMsg = "????????????";
                    flag = false;
                } else {
                    errMsg = errMsg + ",????????????";
                    flag = false;
                }

            } else if (sbdy.getfPsysname() == null || sbdy.getfPsysname().equals("")) {

                if ("".equals(errMsg)) {
                    errMsg = "???????????????????????????";
                    flag = false;
                } else {
                    errMsg = errMsg + ",???????????????????????????";
                    flag = false;
                }
            }else if (sbdy.getfNodeattribution() == null || sbdy.getfNodeattribution().equals("")) {

                if ("".equals(errMsg)) {
                    errMsg = "??????????????????";
                    flag = false;
                } else {
                    errMsg = errMsg + ",??????????????????";
                    flag = false;
                }
            }
            else if (sbdy.getfNickname() == null || sbdy.getfNickname().equals("")) {

                if ("".equals(errMsg)) {
                    errMsg = "????????????";
                    flag = false;
                } else {
                    errMsg = errMsg + ",????????????";
                    flag = false;
                }
            }

            if (!flag) {
                ExcelError excelError = new ExcelError();
                excelError.setRow((i + 2) + "");
                excelError.setErrorMsg(errMsg);
                excelErrors.add(excelError);
            }
        }

        return excelErrors;
    }

    /**
     *
     * @Description: excel????????????DDC??????????????????
     *
     * @auther: wanghongjie
     * @date: 17:13 2020/9/16
     * @param: [ddClist, sbdyStructs]
     * @return: com.core.common.ISSPReturnObject
     *
     */
    @Transactional(propagation = Propagation.NESTED)
    public ISSPReturnObject insertDDCByExcel(List<besDDCExcel> ddClist, sbdyStruct sbdyStructs) {
        ISSPReturnObject returnObject = new ISSPReturnObject();
        Boolean inportflag = null;

        String nodeTabName = "????????????????????????";
        try {

            List lists=new ArrayList();

            Set<String> point = new HashSet<>();
            for (besDDCExcel besDDCExcel : ddClist) {

                //????????????????????????????????????????????????
                Boolean success = point.add(besDDCExcel.getfSysName());
                if (!success) {
                    throw new Exception(besDDCExcel.getfSysName() +":??????????????????????????????????????????");
                }
                lists.add(besDDCExcel.getfSysName());
            }
            Boolean aa = lists.contains(sbdyStructs.getfSysname());

            if (!aa) {
                throw new Exception(sbdyStructs.getfSysname() +"?????????????????????????????????????????????");
            }

            returnObject = insertBesStruct(sbdyStructs,nodeTabName);

            if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                return returnObject;
            }

            String allPath = (String) returnObject.getData();

            for (besDDCExcel besDDCExcel : ddClist) {

                String f_sys_name 		= besDDCExcel.getfSysName();
                String f_nick_name 		= besDDCExcel.getfNickName();
                String f_ssqy 			= besDDCExcel.getfSsqy();
                String f_azwz 			= besDDCExcel.getfAzwz();
                String f_description 	= besDDCExcel.getfDescription();
                String DDCIp 			= besDDCExcel.getfIpAddr();
                String f_node_type 		= besDDCExcel.getfNodeType();
                String yqbh 			= besDDCExcel.getfYqbh();
                String f_gateway 		= besDDCExcel.getfGateway();
                String f_mask 			= besDDCExcel.getfMask();
                String f_ip_master 		= besDDCExcel.getfIpMaster();
                String f_port_master 	= besDDCExcel.getfPortMaster();
                String f_enabled		= besDDCExcel.getfEnabled();

                if (       !StringUtils.hasText(f_sys_name)
                        || !StringUtils.hasText(f_nick_name)
                        || !StringUtils.hasText(f_ssqy)
                        || !StringUtils.hasText(f_azwz)
                        || !StringUtils.hasText(f_description)
                        || !StringUtils.hasText(DDCIp)
                        || !StringUtils.hasText(f_node_type)
                        || !StringUtils.hasText(yqbh)
                        || !StringUtils.hasText(f_gateway)
                        || !StringUtils.hasText(f_mask)
                        || !StringUtils.hasText(f_ip_master)
                        || !StringUtils.hasText(f_port_master)
                        || !StringUtils.hasText(f_enabled)
                )
                {
                    throw new Exception(f_sys_name + "????????????????????????????????????");
                }

                if (sbdyStructs.getfSysname().equals(f_sys_name)) {

                    //???????????????????????????????????????????????????
                    if (null == besSbdyExcelTableImportMapper.getSbTreeInfoBySysName(f_sys_name)) {
                        throw new Exception(f_sys_name + "????????????????????????????????????");
                    }

                    // ??????ip????????????????????????
                    int besDDCSize = besSbdyExcelTableImportMapper.getSizeByIpAddrBesDdc(DDCIp);
                    int besCollectorSize = besSbdyExcelTableImportMapper.getSizeByIpAddrBesCollector(DDCIp);
                    if (besDDCSize == 1 || besCollectorSize == 1){

                        throw new Exception(DDCIp +"ip ????????????");
                    }

                    // ????????????id

                    String sbid 	= besSbdyExcelTableImportMapper.select_f_sbid_By_Bes_Ddc();
                    String meterID 	= getAutoIncreaseCol(sbid);
                    String f_sbid 	= meterID;

//								besDDCExcel.setfGuid(f_guid);
                    besDDCExcel.setfSbid(f_sbid);
                    besDDCExcel.setfPollStatus("1");
                    besDDCExcel.setfEnabled(f_enabled);
                    besDDCExcel.setfDdcState("0");
                    besDDCExcel.setfOnlineState("0");
                    besDDCExcel.setfChannelId(DDCIp);

                    inportflag = besSbdyExcelTableImportMapper.add_sbdyStructDDC(besDDCExcel);

                    //DDC??????????????????????????????????????????
								/*if (inportflag) {
									returnObject = insertVirtualPoint_bus_line(f_sys_name,allPath,f_node_type);
								}*/

                }
            }
						/*if (returnObject.getMsg().equals("????????????")) {
							returnObject.setMsg("???????????????");
							returnObject.setStatus("1");
						} else {
							throw new Exception("???????????????????????????????????????????????????????????????");
//						returnObject.setMsg("??????????????????????????????????????????????????????????????????");
						}*/

            if (inportflag) {
                returnObject.setMsg("???????????????");
                returnObject.setStatus("1");
            } else {
                throw new Exception("????????????");
            }

        }catch (NullPointerException e) {
            returnObject.setStatus("0");
            returnObject.setMsg("???????????????");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception e) {
            returnObject.setStatus("0");
            returnObject.setMsg(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return returnObject;
    }
    /**
     *
     * @Description: ??????????????????
     *
     * @auther: wanghongjie
     * @date: 17:09 2020/9/16
     * @param: [modulelist, sbdyStructs]
     * @return: com.core.common.ISSPReturnObject
     *
     */
    @Transactional(propagation = Propagation.NESTED)
    public ISSPReturnObject insertModuleByExcel(List<besModuleExcel> modulelist, sbdyStruct sbdyStructs) {
        ISSPReturnObject returnObject = new ISSPReturnObject();
        Boolean inportflag = null;

        String nodeTabName = "?????????????????????";
        try {
            JSONObject obj = new JSONObject();
            List lists=new ArrayList();

            Set<String> point = new HashSet<>();
            for (besModuleExcel besModuleExcel : modulelist) {

                //????????????????????????????????????????????????????????????
                Boolean success = point.add(besModuleExcel.getfSysName());
                if (!success) {
                    throw new Exception(besModuleExcel.getfSysName() +":?????????????????????????????????????????????");
                }
                lists.add(besModuleExcel.getfSysName());
            }
            Boolean aa = lists.contains(sbdyStructs.getfSysname());

            if (!aa) {
                throw new Exception(sbdyStructs.getfSysname() +"??????????????????????????????????????????");
            }

            returnObject = insertBesStruct(sbdyStructs,nodeTabName);

            if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                return returnObject;
            }

            String allPath = (String) returnObject.getData();

            for (besModuleExcel besModuleExcel : modulelist) {

                String f_sys_name 		= besModuleExcel.getfSysName();
                String f_nick_name 		= besModuleExcel.getfNickName();
                String f_azwz 			= besModuleExcel.getfAzwz();
                String f_description 	= besModuleExcel.getfDescription();
                String f_node_type 		= besModuleExcel.getfNodeType();
                String yqbh 			= besModuleExcel.getfYqbh();
                String f_module_type	= besModuleExcel.getfModuleType();
                String f_addr 			= besModuleExcel.getfAddr();
                String f_enabled		= besModuleExcel.getfEnabled();

                if (       !StringUtils.hasText(f_sys_name)
                        || !StringUtils.hasText(f_nick_name)
                        || !StringUtils.hasText(f_azwz)
                        || !StringUtils.hasText(f_description)
                        || !StringUtils.hasText(f_node_type)
                        || !StringUtils.hasText(yqbh)
                        || !StringUtils.hasText(f_module_type)
                        || !StringUtils.hasText(f_addr)
                        || !StringUtils.hasText(f_enabled)
                )
                {
                    throw new Exception(f_sys_name+":?????????????????????????????????");
                }

                if (sbdyStructs.getfSysname().equals(f_sys_name)) {

                    //???????????????????????????????????????????????????
                    if (null == besSbdyExcelTableImportMapper.getSbTreeInfoBySysName(f_sys_name)) {
                        throw new Exception(f_sys_name + "????????????????????????????????????");
                    }

                    //????????????????????????????????????ddc?????????????????????,????????????????????????????????????,????????????
                    List<Map<String,Object>> f_addrList = besSbdyExcelTableImportMapper.f_addrListByPName(sbdyStructs.getfPsysname());
                    for (int i = 0; i < f_addrList.size(); i++){
                        if (f_addr.equals((String) f_addrList.get(i).get("F_ADDR"))){
                            throw new Exception(f_addr + ":??????????????????");
                        }
                    }

                    Map<String, Object> ddcinfo = null;
                    if (sbdyStructs.getfNodeattribution().equals("1")) {//????????????
                        ddcinfo = besSbdyExcelTableImportMapper.queryDDCInfoByModule(sbdyStructs.getfPsysname());
                    } else if (sbdyStructs.getfNodeattribution().equals("2")) {//??????
                        ddcinfo = besSbdyExcelTableImportMapper.queryLDCInfoByModule(sbdyStructs.getfPsysname());
                    }
                    //??????sbid
//                    obj.put("f_sbid", getSbid((String)ddcinfo.get("F_SYS_NAME")) + 1);

                    String f_point_type_cl = besSbdyExcelTableImportMapper.selectPointTypeClByModule(f_module_type);//??????????????????????????????
                    if (f_point_type_cl == null) {
                        throw new Exception(f_module_type + ":???????????????????????????");
                    }
                    obj.put("attr_f_sys_name",f_sys_name);
                    obj.put("other_node_types",f_point_type_cl);
                    obj.put("f_allpath",allPath);
                    obj.put("f_node_attribution",sbdyStructs.getfNodeattribution());
                    Integer addDefaultNodeCount = addDefaultNodes(obj);//??????????????????????????????

                    String idByModule = besSbdyExcelTableImportMapper.selectIdByModule(f_sys_name);//?????????????????????F_ID
                    // ????????????id
                    String sbid = besSbdyExcelTableImportMapper.select_f_sbid_By_Bes_Module();
                    String meterID = getAutoIncreaseCol(sbid);
                    String f_sbid  = meterID;

                    //??????????????????????????????????????????F_ID
                    String moduleTypeId = besSbdyExcelTableImportMapper.selectModuleTypeId(f_module_type);
//								besDDCExcel.setfGuid(f_guid);
                    besModuleExcel.setfSbid(f_sbid);
                    besModuleExcel.setfStructId(idByModule);
                    besModuleExcel.setfEnabled(f_enabled);
                    besModuleExcel.setfType(moduleTypeId);

                    inportflag = besSbdyExcelTableImportMapper.add_sbdyStructModule(besModuleExcel);

                    if (inportflag) {
                        //20210802  ???excel?????????????????????????????????,????????????????????????,???????????????????????????
                        if ("WKQ_MOD".equals(f_module_type) || "????????????".equals(f_module_type) || "???????????????????????????".equals(f_module_type)) {

                            returnObject = insertPointAutomatic(besModuleExcel,f_module_type);

                            if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                                return  returnObject;
                            }
                        }
                    }
                }
            }

            if (inportflag) {
                returnObject.setMsg("???????????????");
                returnObject.setStatus("1");
            } else {
                throw new Exception("????????????");
            }
        }catch (NullPointerException e) {
            returnObject.setStatus("0");
            returnObject.setMsg("???????????????");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception e) {
            returnObject.setStatus("0");
            returnObject.setMsg(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return returnObject;
    }

    /**
     *
     * @Description: ??????????????????
     *
     * @auther: wanghongjie
     * @date: 18:21 2021/8/2
     * @param:
     * @return:
     *
     */
    @Transactional(propagation = Propagation.NESTED)
    public ISSPReturnObject insertPointAutomatic(besModuleExcel besModuleExcel, String  f_module_type) {
        ISSPReturnObject returnObject = new ISSPReturnObject();
        besPointExcel besPointExcel = new besPointExcel();
        sbdyStruct sbdyStruct = new sbdyStruct();
        String nodeTabName = "?????????????????????????????????";

        if ("WKQ_MOD".equals(f_module_type)) {
            for (int i = 0; i < 12; i++) {
                String f_Psys_name = besModuleExcel.getfSysName();
                String f_sys_name = f_Psys_name + "0" + String.valueOf(i);

                sbdyStruct.setfDescription("???????????????");
                sbdyStruct.setfNickname(f_sys_name);
                sbdyStruct.setfSysname(f_sys_name);
                sbdyStruct.setfNodeattribution("1");
                sbdyStruct.setfPsysname(f_Psys_name);

                besPointExcel.setfEnabled("1");
                besPointExcel.setfWorkMode("1");
                besPointExcel.setfReversed("0");
                besPointExcel.setfAlarmEnable("0");
                besPointExcel.setfYqbh("0000");
                besPointExcel.setfEnergystatics("0");


                if (i <= 1) {

                    sbdyStruct.setfType("DO");
                    besPointExcel.setfSysNameOld(f_sys_name);
                    besPointExcel.setPointTypeName("DO");
                    besPointExcel.setfChannelIndex(String.valueOf(i));

                    if (i == 0) {
                        besPointExcel.setfNickName("?????????");
                        besPointExcel.setfDescription("?????????");
                        besPointExcel.setfInitVal("255");
                    } else {
                        besPointExcel.setfNickName("??????");
                        besPointExcel.setfDescription("??????");
                        besPointExcel.setfInitVal("0");
                    }

                    returnObject = insertDO_DIPoint(besPointExcel,sbdyStruct);

                    if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                        return returnObject;
                    }

                } else if ((i > 1 && i <= 3) || i > 4) {

                    sbdyStruct.setfType("AO");
                    besPointExcel.setfSysNameOld(f_sys_name);
                    besPointExcel.setPointTypeName("AO");
                    besPointExcel.setfChannelIndex(String.valueOf(i));
                    besPointExcel.setfSinnalType("0");
                    besPointExcel.setfMinVal("0");

                    if (i == 2) {
                        besPointExcel.setfNickName("??????");
                        besPointExcel.setfDescription("??????");
                        besPointExcel.setfInitVal("0");
                        besPointExcel.setfEngineerUnit("#");
                        besPointExcel.setfMaxVal("10");
                        besPointExcel.setfAccuracy("0");
                    } else if (i == 3){
                        besPointExcel.setfNickName("??????");
                        besPointExcel.setfDescription("??????");
                        besPointExcel.setfInitVal("0");
                        besPointExcel.setfEngineerUnit("#");
                        besPointExcel.setfMaxVal("10");
                        besPointExcel.setfAccuracy("0");
                    } else if (i == 5){
                        besPointExcel.setfNickName("??????");
                        besPointExcel.setfDescription("??????");
                        besPointExcel.setfInitVal("0");
                        besPointExcel.setfEngineerUnit("W");
                        besPointExcel.setfMaxVal("1000000");
                        besPointExcel.setfAccuracy("0");
                    }else if (i == 6){
                        besPointExcel.setfNickName("????????????");
                        besPointExcel.setfDescription("????????????");
                        besPointExcel.setfInitVal("0");
                        besPointExcel.setfEngineerUnit("H");
                        besPointExcel.setfMaxVal("1000000");
                        besPointExcel.setfAccuracy("0");
                    }else if (i == 7){
                        besPointExcel.setfNickName("????????????");
                        besPointExcel.setfDescription("????????????");
                        besPointExcel.setfInitVal("0");
                        besPointExcel.setfEngineerUnit("???");
                        besPointExcel.setfMaxVal("100");
                        besPointExcel.setfAccuracy("1");
                    }else if (i == 8){
                        besPointExcel.setfNickName("????????????");
                        besPointExcel.setfDescription("????????????");
                        besPointExcel.setfInitVal("0");
                        besPointExcel.setfEngineerUnit("???");
                        besPointExcel.setfMaxVal("100");
                        besPointExcel.setfAccuracy("1");
                    }else if (i == 9){
                        besPointExcel.setfNickName("????????????");
                        besPointExcel.setfDescription("????????????");
                        besPointExcel.setfInitVal("0");
                        besPointExcel.setfEngineerUnit("KW.H");
                        besPointExcel.setfMaxVal("1000000");
                        besPointExcel.setfAccuracy("0");
                    }else if (i == 10){
                        besPointExcel.setfNickName("?????????");
                        besPointExcel.setfDescription("?????????");
                        besPointExcel.setfInitVal("0");
                        besPointExcel.setfEngineerUnit("H");
                        besPointExcel.setfMaxVal("10");
                        besPointExcel.setfAccuracy("0");
                    }else if (i == 11){
                        besPointExcel.setfNickName("?????????");
                        besPointExcel.setfDescription("?????????");
                        besPointExcel.setfInitVal("0");
                        besPointExcel.setfEngineerUnit("H");
                        besPointExcel.setfMaxVal("10");
                        besPointExcel.setfAccuracy("0");
                    }

                    returnObject = insertAO_AIPoint(besPointExcel,sbdyStruct);

                    if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                        return returnObject;
                    }

                } else if (i == 4) {
                    sbdyStruct.setfType("AI");
                    besPointExcel.setfSysNameOld(f_sys_name);
                    besPointExcel.setPointTypeName("AI");
                    besPointExcel.setfChannelIndex(String.valueOf(i));

                    besPointExcel.setfNickName("????????????");
                    besPointExcel.setfDescription("????????????");
                    besPointExcel.setfInitVal("0");
                    besPointExcel.setfEngineerUnit("???");
                    besPointExcel.setfMaxVal("100");
                    besPointExcel.setfAccuracy("1");

                    returnObject = insertAO_AIPoint(besPointExcel,sbdyStruct);

                    if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                        return returnObject;
                    }
                }
            }
        }
        else if ("????????????".equals(f_module_type)){

            for (int i = 0; i < 5; i++) {
                String f_Psys_name = besModuleExcel.getfSysName();
                String f_sys_name = f_Psys_name + "0" + String.valueOf(i);

                sbdyStruct.setfDescription("????????????");
                sbdyStruct.setfNickname(f_sys_name);
                sbdyStruct.setfSysname(f_sys_name);
                sbdyStruct.setfNodeattribution("2");
                sbdyStruct.setfPsysname(f_Psys_name);

                besPointExcel.setfEnabled("1");
                besPointExcel.setfWorkMode("1");
                besPointExcel.setfReversed("0");
                besPointExcel.setfAlarmEnable("0");
                besPointExcel.setfYqbh("0000");
                besPointExcel.setfEnergystatics("0");


                if (i == 0) {

                    sbdyStruct.setfType("DO");
                    besPointExcel.setfSysNameOld(f_sys_name);
                    besPointExcel.setPointTypeName("DO");
                    besPointExcel.setfChannelIndex(String.valueOf(i));

                    besPointExcel.setfNickName("??????");
                    besPointExcel.setfDescription("??????");
                    besPointExcel.setfInitVal("255");

                    returnObject = insertDO_DIPoint(besPointExcel,sbdyStruct);

                    if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                        return returnObject;
                    }

                } else {

                    sbdyStruct.setfType("AI");
                    besPointExcel.setfSysNameOld(f_sys_name);
                    besPointExcel.setPointTypeName("AI");
                    besPointExcel.setfChannelIndex(String.valueOf(i));
                    besPointExcel.setfSinnalType("0");
                    besPointExcel.setfMinVal("0");


                    if (i == 1) {
                        besPointExcel.setfNickName("??????");
                        besPointExcel.setfDescription("??????");
                        besPointExcel.setfInitVal("0");
                        besPointExcel.setfEngineerUnit("kwh");
                        besPointExcel.setfMaxVal("1000000");
                        besPointExcel.setfAccuracy("3");
                    } else if (i == 2){
                        besPointExcel.setfNickName("??????");
                        besPointExcel.setfDescription("??????");
                        besPointExcel.setfInitVal("0");
                        besPointExcel.setfEngineerUnit("W");
                        besPointExcel.setfMaxVal("1000000");
                        besPointExcel.setfAccuracy("1");
                    } else if (i == 3){
                        besPointExcel.setfNickName("??????");
                        besPointExcel.setfDescription("??????");
                        besPointExcel.setfInitVal("0");
                        besPointExcel.setfEngineerUnit("A");
                        besPointExcel.setfMaxVal("1000000");
                        besPointExcel.setfAccuracy("2");
                    }else if (i == 4){
                        besPointExcel.setfNickName("??????");
                        besPointExcel.setfDescription("??????");
                        besPointExcel.setfInitVal("0");
                        besPointExcel.setfEngineerUnit("V");
                        besPointExcel.setfMaxVal("1000000");
                        besPointExcel.setfAccuracy("2");
                    }

                    returnObject = insertAO_AIPoint(besPointExcel,sbdyStruct);

                    if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                        return returnObject;
                    }
                }
            }
        }
        else if ("???????????????????????????".equals(f_module_type)) {
            for (int i = 1; i < 4; i++) {

                String f_Psys_name = besModuleExcel.getfSysName();
                String f_sys_name = f_Psys_name + "0" + String.valueOf(i);

                sbdyStruct.setfDescription("???????????????");
                sbdyStruct.setfNickname(f_sys_name);
                sbdyStruct.setfSysname(f_sys_name);
                sbdyStruct.setfNodeattribution("2");
                sbdyStruct.setfPsysname(f_Psys_name);

                besPointExcel.setfSysNameOld(f_sys_name);
                besPointExcel.setfMinVal("0");
                besPointExcel.setfEnabled("1");
                besPointExcel.setfWorkMode("1");
                besPointExcel.setfReversed("0");
                besPointExcel.setfAlarmEnable("0");
                besPointExcel.setfYqbh("0000");
                besPointExcel.setfEnergystatics("0");
                besPointExcel.setfChannelIndex(String.valueOf(i));

                if (i == 1) {
                    sbdyStruct.setfType("DI");
                    besPointExcel.setPointTypeName("DI");
                    besPointExcel.setfNickName("????????????");
                    besPointExcel.setfDescription("????????????");
                    besPointExcel.setfInitVal("0");
                    besPointExcel.setfSourced("0");

                    returnObject = insertDO_DIPoint(besPointExcel,sbdyStruct);

                    if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                        return returnObject;
                    }

                } else if (i == 3) {
                    sbdyStruct.setfType("AI");
                    besPointExcel.setPointTypeName("AI");
                    besPointExcel.setfSinnalType("0");
                    besPointExcel.setfNickName("????????????");
                    besPointExcel.setfDescription("????????????");
                    besPointExcel.setfInitVal("0");
                    besPointExcel.setfEngineerUnit("LUX");
                    besPointExcel.setfMaxVal("1000000");
                    besPointExcel.setfAccuracy("2");

                    returnObject = insertAO_AIPoint(besPointExcel,sbdyStruct);

                    if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                        return returnObject;
                    }
                }
            }
        }
        return returnObject;
    }

    /**
     *
     * @Description: Excel??????????????????
     *
     * @auther: wanghongjie
     * @date: 10:35 2020/9/14
     * @param: [pointList, sbdyStructs, allPath]
     * @return: void
     *
     */
    @Transactional(propagation = Propagation.NESTED)
    public ISSPReturnObject pointInsertByExcel(List<besPointExcel> pointList, sbdyStruct sbdyStructs) {
        ISSPReturnObject returnObject = new ISSPReturnObject();

        try {

            for (besPointExcel besPoint : pointList) {

                if (besPoint.getPointTypeName() == null) {
                    throw new Exception(besPoint.getfSysNameOld() + "???????????????????????????????????????");
                }
                if (besPoint.getfSysNameOld().equals(sbdyStructs.getfSysname())) {
                    //????????????????????????????????????
                    String pSysNameType = besSbdyExcelTableImportMapper.selectPSysNameType(sbdyStructs.getfPsysname());

                    if (pSysNameType == null) {
                        throw new Exception(besPoint.getfSysNameOld() + "?????????????????????????????????");
                    }
                    if (pSysNameType.equals("9")) {//????????????

                        if (!besPoint.getPointTypeName().equals("DO") && !besPoint.getPointTypeName().equals("DI")
                                && !besPoint.getPointTypeName().equals("AO") && !besPoint.getPointTypeName().equals("AI")) {

                            throw new Exception(besPoint.getfSysNameOld() + "???????????????????????????????????????");
                        }
                        if (besPoint.getPointTypeName().equals("DO") || besPoint.getPointTypeName().equals("DI")) {

                            returnObject = insertDO_DIPoint(besPoint,sbdyStructs);

                            if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                                return returnObject;
                            }
                            if (returnObject.getMsg() == "???????????????") {
                                return returnObject;
                            }


                        } else if (besPoint.getPointTypeName().equals("AO") || besPoint.getPointTypeName().equals("AI")) {

                            returnObject = insertAO_AIPoint(besPoint,sbdyStructs);
                            if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                                return returnObject;
                            }
                            if (returnObject.getMsg() == "???????????????") {
                                return returnObject;
                            }

                        }
                    } else if (pSysNameType.equals("24")) {//???????????????????????????

                        if (!besPoint.getPointTypeName().equals("DO")  && !besPoint.getPointTypeName().equals("DI")
                                && !besPoint.getPointTypeName().equals("AO") && !besPoint.getPointTypeName().equals("AI")) {

                            throw new Exception(besPoint.getfSysNameOld() + "???????????????????????????????????????");
                        }

                        if (besPoint.getPointTypeName().equals("DO") || besPoint.getPointTypeName().equals("DI")) {

                            returnObject = insertDO_DIVPoint(besPoint,sbdyStructs);
                            if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                                return returnObject;
                            }
                            if (returnObject.getMsg() == "???????????????") {
                                return returnObject;
                            }

                        }else if (besPoint.getPointTypeName().equals("AO") || besPoint.getPointTypeName().equals("AI")) {

                            returnObject = insertAO_AIVPoint(besPoint,sbdyStructs);
                            if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                                return returnObject;
                            }
                            if (returnObject.getMsg() == "???????????????") {
                                return returnObject;
                            }
                        }
                    }
                }
            }


        }catch (NullPointerException e) {
            returnObject.setStatus("0");
            returnObject.setMsg("???????????????");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception e) {
            returnObject.setStatus("0");
            returnObject.setMsg(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return returnObject;
    }

    /**
     *
     * @Description: Excel DO,DI????????????
     *
     * @auther: wanghongjie
     * @date: 14:03 2020/9/15
     * @param: [besPoint, sbdyStructs]
     * @return: com.core.common.ISSPReturnObject
     *
     */
    @Transactional(propagation = Propagation.NESTED)
    public ISSPReturnObject insertDO_DIPoint(besPointExcel besPoint, sbdyStruct sbdyStructs) {
        ISSPReturnObject returnObject = new ISSPReturnObject();
        Integer f_idBySbdyStruct ;
        String allpath;
        String f_sys_name;
        String f_type = null;
        String tabName = null;
        Boolean Has_it_been_added = true;//????????????????????????,???????????????

        try {

            //??????????????????????????????????????????????????????????????????,??????????????????????????????
            Map<String,Object> moduleType = besSbdyExcelTableImportMapper.selectF_POINT_TYPE_CL(sbdyStructs.getfPsysname());

            String F_POINT_TYPE_CL = (String) moduleType.get("F_POINT_TYPE_CL");

            //????????????
            String moduleName = (String) moduleType.get("F_MODULE_TYPE");

            if ("3???????????????".equals(moduleName) || "7???????????????".equals(moduleName) || "11???????????????".equals(moduleName)) {
                besPoint.setfEnabled("1");
                besPoint.setfWorkMode("1");
                besPoint.setfReversed("0");
                besPoint.setfInitVal("255");
                besPoint.setfAlarmEnable("0");
                besPoint.setfYqbh("0000");
            }


            String f_alarm_type;
            String f_close_state;
            String f_alarm_priority;
            String f_fault_state;

            String f_sys_name_old 	= besPoint.getfSysNameOld();
            String f_nick_name 		= besPoint.getfNickName();
            String f_enabled 		= besPoint.getfEnabled();
            String f_channel_index 	= besPoint.getfChannelIndex();
            String f_reversed 		= besPoint.getfReversed();

            String f_work_mode 		= besPoint.getfWorkMode();
            String f_init_val 		= besPoint.getfInitVal();
            String f_description 	= besPoint.getfDescription();
            String yqbh				= besPoint.getfYqbh();
            String f_alarm_enable 	= besPoint.getfAlarmEnable();

            if (!f_alarm_enable.equals("0")) {
                f_alarm_type 	= besPoint.getfAlarmType();
                f_close_state 	= besPoint.getfCloseState();
                f_alarm_priority = besPoint.getfAlarmPriority();
                f_fault_state    = besPoint.getfFaultState();
            } else {//??????excel?????????DO?????????????????????,??????????????????,????????????,????????????,?????????????????????????????????
                f_alarm_type 	= "0";//0:	?????????  1:	????????????  2:	????????????
                besPoint.setfAlarmType(f_alarm_type);
                f_close_state 	= "0";//0:	???????????? 1:	????????????
                besPoint.setfCloseState(f_close_state);
                f_alarm_priority = "0";//0:	?????? 1:	?????? 2:	??????
                besPoint.setfAlarmPriority(f_alarm_priority);
                f_fault_state    = "0";//0:	????????? 1:	??????
                besPoint.setfFaultState(f_fault_state);

            }

            if (besPoint.getfFaultState() == null) {//???????????????????????????,???????????????????????????,????????????????????????????????????
                f_fault_state    = "0";//0:	????????? 1:	??????
                besPoint.setfFaultState(f_fault_state);
            }

            if (       !StringUtils.hasText(f_sys_name_old)
                    || !StringUtils.hasText(f_nick_name)
                    || !StringUtils.hasText(f_enabled)
                    || !StringUtils.hasText(f_channel_index)
                    || !StringUtils.hasText(f_reversed)
                    || !StringUtils.hasText(f_work_mode)
                    || !StringUtils.hasText(f_init_val)
                    || !StringUtils.hasText(f_description)
                    || !StringUtils.hasText(yqbh)
                    || !StringUtils.hasText(f_alarm_enable)
                    || !StringUtils.hasText(f_alarm_type)
                    || !StringUtils.hasText(f_close_state)
                    || !StringUtils.hasText(f_alarm_priority)
                    || !StringUtils.hasText(f_fault_state)
            )
            {
                throw new Exception(f_sys_name_old+":?????????????????????????????????");
            }


            //??????????????????????????????????????????????????????????????????,??????????????????????????????????????????
            List<Map<String,Object>> module_pointList = besSbdyExcelTableImportMapper.selectModule_pointList(sbdyStructs.getfPsysname());

            String name = (String) module_pointList.get(Integer.parseInt(f_channel_index)).get("f_sys_name_old");

//			for (int i = 0;i < module_pointList.size(); i++) {


//				String name = (String) module_pointList.get(i).get("f_sys_name_old");

            if (name.contains(sbdyStructs.getfPsysname())){

                if (Integer.valueOf(name.replace(sbdyStructs.getfPsysname(),"")).equals(Integer.valueOf(f_channel_index))) {

                    Has_it_been_added = false;//??????????????????

                    //?????????????????????????????????,???????????????????????????????????????????????????,???????????????F_ID??????F_ALLPATH
//                    String tabNameSbdy =  "bes_sbpz_struct";
                    String tabNameSbdy =  "bes_sbpz_struct_copy";
                    Map<String,Object> pointMap = besSbdyExcelTableImportMapper.selectPointMap(tabNameSbdy,name);
                    f_idBySbdyStruct = (Integer) pointMap.get("F_ID");
                    allpath = (String) pointMap.get("F_ALLPATH");

//                    //??????????????????????????????????????????????????????????????????,??????????????????????????????
//                    Map<String,Object> moduleType = besSbdyExcelTableImportMapper.selectF_POINT_TYPE_CL(sbdyStructs.getfPsysname());
//
//                    String F_POINT_TYPE_CL = (String) moduleType.get("F_POINT_TYPE_CL");
//
//                    //????????????
//                    String moduleName = (String) moduleType.get("F_MODULE_TYPE");

                    final String substring = F_POINT_TYPE_CL.substring(Integer.parseInt(f_channel_index), Integer.parseInt(f_channel_index) + 1);
                    if (sbdyStructs.getfType().equals("DO")) {
                        if (!substring.equals("3")) {//DO???
                            throw new Exception(f_sys_name_old+"???????????????????????????????????????????????????");
                        }
                    }
                    if (sbdyStructs.getfType().equals("DI")) {
                        if (!substring.equals("4") && !substring.equals("2")) {//UI???,DI???
                            throw new Exception(f_sys_name_old+"???????????????????????????????????????????????????");
                        }
                    }

                    if (besPoint.getPointTypeName().equals("DO")) {
                        f_type = "13";
                        tabName = "BES_DIGIT_OUPUT";
                        allpath = allpath.replace(">DO??????",">"+f_sys_name_old);

                    } else if (besPoint.getPointTypeName().equals("DI")) {

                        f_type = "12";
                        tabName = "BES_DIGIT_INPUT";

                        if (pointMap.get("F_TYPE").equals(12)) {
                            allpath = allpath.replace(">DI??????",">"+f_sys_name_old);
                        } else if (pointMap.get("F_TYPE").equals(14)) {
                            allpath = allpath.replace(">UI??????",">"+f_sys_name_old);
                        }


                        String f_sourced = besPoint.getfSourced();
                        if (!StringUtils.hasText(f_sourced)) {
                            throw new Exception(f_sys_name_old+"?????????????????????????????????");
                        }
                    }

                    f_sys_name = (String) pointMap.get("F_SYS_NAME");
                    String f_sbid = (String) pointMap.get("F_SBID");
                    besPoint.setfSysName(f_sys_name);
                    besPoint.setfNodeType(f_type);

//                    Boolean updateStruct = besSbdyExcelTableImportMapper.updateStructPoint(f_idBySbdyStruct,f_sys_name_old,f_nick_name,allpath,f_description,f_type);


//                    if (updateStruct) {

                        //??????????????????
                        Boolean aaa = besSbdyExcelTableImportMapper.updateStructPointCopy(f_idBySbdyStruct,f_sys_name_old,
                                f_nick_name,allpath,f_description,f_type);

                        if (aaa) {
                            besPoint.setfPointState("0");
                            besPoint.setfSbid(f_sbid);
                            besPoint.setfStructId(String.valueOf(f_idBySbdyStruct));
                            //??????????????????????????????????????????
                            Boolean insertPointMapToNodeTable = besSbdyExcelTableImportMapper.insertPointMapToNodeTable_DO_DI(tabName,besPoint);

                            if (insertPointMapToNodeTable) {
                                returnObject.setMsg("???????????????");
                                returnObject.setStatus("1");
                            } else {
                                throw new Exception("????????????");
                            }
                        }

//                    }
                }
            }
//			}
            if (Has_it_been_added) {
                throw new Exception(f_sys_name_old +":??????????????????");
            }
        } catch (FileNotFoundException | NullPointerException e) {
            returnObject.setStatus("0");
            returnObject.setMsg("???????????????");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception e) {
            returnObject.setStatus("0");
            returnObject.setMsg(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return returnObject;
    }

    /**
     *
     * @Description: Excel AO,AI????????????
     *
     * @auther: wanghongjie
     * @date: 14:26 2020/9/15
     * @param: [besPoint, sbdyStructs]
     * @return: com.core.common.ISSPReturnObject
     *
     */
    @Transactional(propagation = Propagation.NESTED)
    public ISSPReturnObject insertAO_AIPoint(besPointExcel besPoint, sbdyStruct sbdyStructs) {
        ISSPReturnObject returnObject = new ISSPReturnObject();
        Integer f_idBySbdyStruct ;
        String allpath;
        String f_sys_name;
        String f_type = null;
        String tabName = null;
        String f_energystatics = null;
        Boolean Has_it_been_added = true;//????????????????????????,???????????????

        try {
            //??????????????????????????????????????????????????????????????????,??????????????????????????????
            Map<String,Object> moduleType = besSbdyExcelTableImportMapper.selectF_POINT_TYPE_CL(sbdyStructs.getfPsysname());

            //???????????????
            String F_POINT_TYPE_CL = (String) moduleType.get("F_POINT_TYPE_CL");

            //????????????
            String moduleName = (String) moduleType.get("F_MODULE_TYPE");

            if ("3???????????????".equals(moduleName) || "7???????????????".equals(moduleName) || "11???????????????".equals(moduleName)) {
                besPoint.setfEnabled("1");
                besPoint.setfWorkMode("1");
                besPoint.setfReversed("0");
                besPoint.setfInitVal("0");
                besPoint.setfSinnalType("0");
                besPoint.setfEngineerUnit("K");
                besPoint.setfMaxVal("1000000");
                besPoint.setfMinVal("0");
                besPoint.setfAccuracy("2");
                besPoint.setfAlarmEnable("0");
                besPoint.setfYqbh("0000");
            }

            String f_alarm_type;
            String f_close_state;
            String f_alarm_priority;
            String f_high_limit;
            String f_low_limit;


            String f_sys_name_old 	= besPoint.getfSysNameOld();
            String f_nick_name 		= besPoint.getfNickName();
            String f_enabled 		= besPoint.getfEnabled();
            String f_channel_index 	= besPoint.getfChannelIndex();
            String f_engineer_unit 	= besPoint.getfEngineerUnit();
            String f_sinnal_type	= besPoint.getfSinnalType();
            String f_max_val		= besPoint.getfMaxVal();
            String f_min_val		= besPoint.getfMinVal();
            String f_accuracy		= besPoint.getfAccuracy();
            String f_reversed 		= besPoint.getfReversed();
            String f_work_mode 		= besPoint.getfWorkMode();
            String f_init_val 		= besPoint.getfInitVal();
            String f_description 	= besPoint.getfDescription();
            String yqbh				= besPoint.getfYqbh();
            String f_alarm_enable 	= besPoint.getfAlarmEnable();

            if (!f_alarm_enable.equals("0")) {
                f_alarm_type 	= besPoint.getfAlarmType();
                f_close_state 	= besPoint.getfCloseState();
                f_alarm_priority = besPoint.getfAlarmPriority();
                f_high_limit    = besPoint.getfHighLimit();
                f_low_limit		= besPoint.getfLowLimit();
            } else {//??????excel?????????DO?????????????????????,??????????????????,????????????,????????????,?????????????????????????????????
                f_alarm_type 	= "0";//0:	?????????  1:	????????????  2:	????????????
                besPoint.setfAlarmType(f_alarm_type);
                f_close_state 	= "0";//0:	???????????? 1:	????????????
                besPoint.setfCloseState(f_close_state);
                f_alarm_priority = "0";//0:	?????? 1:	?????? 2:	??????
                besPoint.setfAlarmPriority(f_alarm_priority);
                f_high_limit = "100";
                besPoint.setfHighLimit(f_high_limit);
                f_low_limit = "0";
                besPoint.setfLowLimit(f_low_limit);

            }

            if (       !StringUtils.hasText(f_sys_name_old)
                    || !StringUtils.hasText(f_nick_name)
                    || !StringUtils.hasText(f_enabled)
                    || !StringUtils.hasText(f_channel_index)
                    || !StringUtils.hasText(f_reversed)
                    || !StringUtils.hasText(f_engineer_unit)
                    || !StringUtils.hasText(f_work_mode)
                    || !StringUtils.hasText(f_init_val)
                    || !StringUtils.hasText(f_description)
                    || !StringUtils.hasText(yqbh)
                    || !StringUtils.hasText(f_alarm_enable)
                    || !StringUtils.hasText(f_alarm_type)
                    || !StringUtils.hasText(f_close_state)
                    || !StringUtils.hasText(f_alarm_priority)
                    || !StringUtils.hasText(f_high_limit)
                    || !StringUtils.hasText(f_low_limit)
                    || !StringUtils.hasText(f_sinnal_type)
                    || !StringUtils.hasText(f_max_val)
                    || !StringUtils.hasText(f_min_val)
                    || !StringUtils.hasText(f_accuracy)
            )
            {
                throw new Exception(f_sys_name_old+":?????????????????????????????????");
            }

            //??????????????????????????????????????????????????????????????????,??????????????????????????????????????????
            List<Map<String,Object>> module_pointList = besSbdyExcelTableImportMapper.selectModule_pointList(sbdyStructs.getfPsysname());

            String name = (String) module_pointList.get(Integer.parseInt(f_channel_index)).get("f_sys_name_old");
//			for (int i = 0;i < module_pointList.size(); i++) {

//				String name = (String) module_pointList.get(i).get("f_sys_name_old");

            if (name.contains(sbdyStructs.getfPsysname())){

                if (Integer.valueOf(name.replace(sbdyStructs.getfPsysname(),"")).equals(Integer.valueOf(f_channel_index))) {

                    Has_it_been_added = false;//??????????????????

                    //?????????????????????????????????,???????????????????????????????????????????????????,???????????????F_ID??????F_ALLPATH
//                    String tabNameSbdy =  "bes_sbpz_struct";
                    String tabNameSbdy =  "bes_sbpz_struct_copy";
                    Map<String,Object> pointMap = besSbdyExcelTableImportMapper.selectPointMap(tabNameSbdy,name);
                    f_idBySbdyStruct = (Integer) pointMap.get("F_ID");
                    allpath = (String) pointMap.get("F_ALLPATH");

//                    //??????????????????????????????????????????????????????????????????,??????????????????????????????
//                    Map<String,Object> moduleType = besSbdyExcelTableImportMapper.selectF_POINT_TYPE_CL(sbdyStructs.getfPsysname());
//
//                    //???????????????
//                    String F_POINT_TYPE_CL = (String) moduleType.get("F_POINT_TYPE_CL");
//
//                    //????????????
//                    String moduleName = (String) moduleType.get("F_MODULE_TYPE");

                    //????????????
                    Integer channel_index = Integer.parseInt(f_channel_index);

                    if ("3???????????????".equals(moduleName)) {

                        if (channel_index >= 3 && channel_index <= 5) {
                            f_nick_name = f_nick_name + "????????????";
                            besPoint.setfNickName(f_nick_name);
                            besPoint.setfDescription(f_nick_name);
                            besPoint.setfEngineerUnit("kwh");
                        } else if (channel_index >= 6 && channel_index <= 8) {
                            f_nick_name = f_nick_name + "??????";
                            besPoint.setfNickName(f_nick_name);
                            besPoint.setfDescription(f_nick_name);
                            besPoint.setfEngineerUnit("W");

                        }
                    } else if ("7???????????????".equals(moduleName)) {

                        if (channel_index >= 7 && channel_index <= 13) {
                            f_nick_name = f_nick_name + "????????????";
                            besPoint.setfNickName(f_nick_name);
                            besPoint.setfDescription(f_nick_name);
                            besPoint.setfEngineerUnit("kwh");
                        } else if (channel_index >= 14 && channel_index <= 20) {
                            f_nick_name = f_nick_name + "??????";
                            besPoint.setfNickName(f_nick_name);
                            besPoint.setfDescription(f_nick_name);
                            besPoint.setfEngineerUnit("W");
                        }

                    } else if ("11???????????????".equals(moduleName)) {

                        if (channel_index >= 11 && channel_index <= 21) {
                            f_nick_name = f_nick_name + "????????????";
                            besPoint.setfNickName(f_nick_name);
                            besPoint.setfDescription(f_nick_name);
                            besPoint.setfEngineerUnit("kwh");
                        } else if (channel_index >= 22 && channel_index <= 32) {
                            f_nick_name = f_nick_name + "??????";
                            besPoint.setfNickName(f_nick_name);
                            besPoint.setfDescription(f_nick_name);
                            besPoint.setfEngineerUnit("W");
                        }
                    }

                    final String substring = F_POINT_TYPE_CL.substring(Integer.parseInt(f_channel_index), Integer.parseInt(f_channel_index) + 1);
                    if (sbdyStructs.getfType().equals("AO")) {
                        if (!substring.equals("1")) {//DO???
                            throw new Exception(f_sys_name_old+"???????????????????????????????????????????????????");
                        }
                    }
                    if (sbdyStructs.getfType().equals("AI")) {
                        if (!substring.equals("4") && !substring.equals("0")) {//UI???
                            throw new Exception(f_sys_name_old+"???????????????????????????????????????????????????");
                        }
                    }

                    if (besPoint.getPointTypeName().equals("AO")) {
                        f_type = "11";
                        tabName = "BES_ANALOG_OUPUT";
                        allpath = allpath.replace(">AO??????",">"+f_sys_name_old);

                    } else if (besPoint.getPointTypeName().equals("AI")) {

                        f_type = "10";
                        tabName = "BES_ANALOG_INPUT";
                        if (pointMap.get("F_TYPE").equals(10)) {
                            allpath = allpath.replace(">AI??????",">"+f_sys_name_old);
                        } else if (pointMap.get("F_TYPE").equals(14)) {
                            allpath = allpath.replace(">UI??????",">"+f_sys_name_old);
                        }

                        String fEnergystatics = besPoint.getfEnergystatics();//???????????????????????????

                        if (StringUtils.hasText(fEnergystatics)) {
                            if ("1".equals(fEnergystatics)){//???
                                f_energystatics = "0";
                            } else if ("0".equals(fEnergystatics)) {//???
                                f_energystatics = "1";
                            }
                        } else {
                            f_energystatics = "1";//???????????????0:??????1:??????
                        }

                        besPoint.setfEnergystatics(f_energystatics);

                    }

                    f_sys_name = (String) pointMap.get("F_SYS_NAME");
                    String f_sbid = (String) pointMap.get("F_SBID");
                    besPoint.setfSysName(f_sys_name);
                    besPoint.setfNodeType(f_type);

//                    Boolean updateStruct = besSbdyExcelTableImportMapper.updateStructPoint(f_idBySbdyStruct,f_sys_name_old,f_nick_name,allpath,f_description,f_type);

//                    if (updateStruct) {

                        //??????????????????
                        Boolean aaa = besSbdyExcelTableImportMapper.updateStructPointCopy(f_idBySbdyStruct,f_sys_name_old,
                                f_nick_name,allpath,f_description,f_type);
                        if (aaa) {
                            besPoint.setfPointState("0");
                            besPoint.setfSbid(f_sbid);
                            besPoint.setfStructId(String.valueOf(f_idBySbdyStruct));
                            //??????????????????????????????????????????
                            Boolean insertPointMapToNodeTable = besSbdyExcelTableImportMapper.insertPointMapToNodeTable_AO_AI(tabName,besPoint);

                            if (insertPointMapToNodeTable) {
                                returnObject.setMsg("???????????????");
                                returnObject.setStatus("1");
                            } else {
                                throw new Exception("????????????");
                            }
                        }


//                    }
                }
            }
//			}

            if (Has_it_been_added) {
                throw new Exception(f_sys_name_old +":??????????????????");
            }

        } catch (FileNotFoundException | NullPointerException e) {
            returnObject.setStatus("0");
            returnObject.setMsg("???????????????");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception e) {
            returnObject.setStatus("0");
            returnObject.setMsg(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return returnObject;
    }


    /**
     *
     * @Description: Excel ??????DO,DI????????????
     *
     * @auther: wanghongjie
     * @date: 11:54 2020/9/16
     * @param: [besPoint, sbdyStructs]
     * @return: com.core.common.ISSPReturnObject
     *
     */
    @Transactional(propagation = Propagation.NESTED)
    public ISSPReturnObject insertDO_DIVPoint(besPointExcel besPoint, sbdyStruct sbdyStructs) {
        ISSPReturnObject returnObject = new ISSPReturnObject();

        String nodeTabName = "???????????????";

        try {
            String f_alarm_type;
            String f_close_state;
            String f_alarm_priority;
            String f_fault_state;

            String f_sys_name_old 	= besPoint.getfSysNameOld();
            String f_nick_name 		= besPoint.getfNickName();
            String f_enabled 		= besPoint.getfEnabled();
            String f_init_val 		= besPoint.getfInitVal();
            String f_description 	= besPoint.getfDescription();
            String yqbh				= besPoint.getfYqbh();
            String f_alarm_enable 	= besPoint.getfAlarmEnable();

            if (!f_alarm_enable.equals("0")) {
                f_alarm_type 	= besPoint.getfAlarmType();
                f_close_state 	= besPoint.getfCloseState();
                f_alarm_priority = besPoint.getfAlarmPriority();
                f_fault_state    = besPoint.getfFaultState();
            } else {//??????excel?????????DO?????????????????????,??????????????????,????????????,????????????,?????????????????????????????????
                f_alarm_type 	= "0";//0:	?????????  1:	????????????  2:	????????????
                besPoint.setfAlarmType(f_alarm_type);
                f_close_state 	= "0";//0:	???????????? 1:	????????????
                besPoint.setfCloseState(f_close_state);
                f_alarm_priority = "0";//0:	?????? 1:	?????? 2:	??????
                besPoint.setfAlarmPriority(f_alarm_priority);
                f_fault_state    = "0";//0:	????????? 1:	??????
                besPoint.setfFaultState(f_fault_state);

            }

            if (besPoint.getfFaultState() == null) {//???????????????????????????,???????????????????????????,????????????????????????????????????
                f_fault_state    = "0";//0:	????????? 1:	??????
                besPoint.setfFaultState(f_fault_state);
            }

            if (       !StringUtils.hasText(f_sys_name_old)
                    || !StringUtils.hasText(f_nick_name)
                    || !StringUtils.hasText(f_enabled)
                    || !StringUtils.hasText(f_init_val)
                    || !StringUtils.hasText(f_description)
                    || !StringUtils.hasText(yqbh)
                    || !StringUtils.hasText(f_alarm_enable)
                    || !StringUtils.hasText(f_alarm_type)
                    || !StringUtils.hasText(f_close_state)
                    || !StringUtils.hasText(f_alarm_priority)
                    || !StringUtils.hasText(f_fault_state)
            )
            {
                throw new Exception(f_sys_name_old +":?????????????????????????????????");
            }

            if (sbdyStructs.getfType().equals("DO") || sbdyStructs.getfType().equals("DI")) {
                sbdyStructs.setfType("16");
            }
            returnObject = insertBesStruct(sbdyStructs,nodeTabName);
            if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                return returnObject;
            }


            if (sbdyStructs.getfSysname().equals(f_sys_name_old)) {

                //???????????????????????????????????????????????????
                if (null == besSbdyExcelTableImportMapper.getSbTreeInfoBySysName(f_sys_name_old)) {
                    throw new Exception(f_sys_name_old + ":????????????????????????????????????");
                }

            }
            //?????????????????????????????????????????????,???????????????F_ID
//            String tabNameSbdy =  "bes_sbpz_struct";
            String tabNameSbdy =  "bes_sbpz_struct_copy";
            Map<String,Object> pointMap = besSbdyExcelTableImportMapper.selectPointMap(tabNameSbdy,f_sys_name_old);
            besPoint.setfSbid(String.valueOf(pointMap.get("F_ID")));
            besPoint.setfPointState("0");
            if (besPoint.getPointTypeName().equals("DO")) {
                besPoint.setfNodeType("7");
            } else if (besPoint.getPointTypeName().equals("DI")) {
                besPoint.setfNodeType("6");
            }

            //????????????DO,DI????????????????????????
            Boolean insertVPointMapToNodeTable = besSbdyExcelTableImportMapper.insertVPointMapToNodeTable_DO_DI(besPoint);
            if (insertVPointMapToNodeTable) {
                returnObject.setMsg("???????????????");
                returnObject.setStatus("1");
            } else {
                throw new Exception("????????????");
            }


        } catch (FileNotFoundException | NullPointerException e) {
            returnObject.setStatus("0");
            returnObject.setMsg("???????????????");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception e) {
            returnObject.setStatus("0");
            returnObject.setMsg(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return returnObject;
    }

    /**
     *
     * @Description: Excel ??????AO,AI????????????
     *
     * @auther: wanghongjie
     * @date: 11:54 2020/9/16
     * @param: [besPoint, sbdyStructs]
     * @return: com.core.common.ISSPReturnObject
     *
     */
    @Transactional(propagation = Propagation.NESTED)
    public ISSPReturnObject insertAO_AIVPoint(besPointExcel besPoint, sbdyStruct sbdyStructs) {
        ISSPReturnObject returnObject = new ISSPReturnObject();

        String nodeTabName = "???????????????";
        try {
            String f_alarm_type;
            String f_close_state;
            String f_alarm_priority;
            String f_high_limit;
            String f_low_limit;

            String f_sys_name_old 	= besPoint.getfSysNameOld();
            String f_nick_name 		= besPoint.getfNickName();
            String f_enabled 		= besPoint.getfEnabled();
            String f_init_val 		= besPoint.getfInitVal();
            String f_description 	= besPoint.getfDescription();
            String yqbh				= besPoint.getfYqbh();
            String f_alarm_enable 	= besPoint.getfAlarmEnable();
            String f_engineer_unit 	= besPoint.getfEngineerUnit();
            String f_accuracy		= besPoint.getfAccuracy();

            if (!f_alarm_enable.equals("0")) {
                f_alarm_type 	= besPoint.getfAlarmType();
                f_close_state 	= besPoint.getfCloseState();
                f_alarm_priority = besPoint.getfAlarmPriority();
                f_high_limit    = besPoint.getfHighLimit();
                f_low_limit		= besPoint.getfLowLimit();
            } else {//??????excel?????????DO?????????????????????,??????????????????,????????????,????????????,?????????????????????????????????
                f_alarm_type 	= "0";//0:	?????????  1:	????????????  2:	????????????
                besPoint.setfAlarmType(f_alarm_type);
                f_close_state 	= "0";//0:	???????????? 1:	????????????
                besPoint.setfCloseState(f_close_state);
                f_alarm_priority = "0";//0:	?????? 1:	?????? 2:	??????
                besPoint.setfAlarmPriority(f_alarm_priority);
            }

            if (       !StringUtils.hasText(f_sys_name_old)
                    || !StringUtils.hasText(f_nick_name)
                    || !StringUtils.hasText(f_enabled)
                    || !StringUtils.hasText(f_init_val)
                    || !StringUtils.hasText(f_description)
                    || !StringUtils.hasText(yqbh)
                    || !StringUtils.hasText(f_alarm_enable)
                    || !StringUtils.hasText(f_alarm_type)
                    || !StringUtils.hasText(f_close_state)
                    || !StringUtils.hasText(f_alarm_priority)
                    || !StringUtils.hasText(f_engineer_unit)
                    || !StringUtils.hasText(f_accuracy)
            )
            {
                throw new Exception(f_sys_name_old + ":?????????????????????????????????");
            }

            if (sbdyStructs.getfType().equals("AO") || sbdyStructs.getfType().equals("AI")) {
                sbdyStructs.setfType("16");
            }
            returnObject = insertBesStruct(sbdyStructs,nodeTabName);

            if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                return returnObject;
            }

            if (sbdyStructs.getfSysname().equals(f_sys_name_old)) {

                //???????????????????????????????????????????????????
                if (null == besSbdyExcelTableImportMapper.getSbTreeInfoBySysName(f_sys_name_old)) {
                    throw new Exception(f_sys_name_old + "????????????????????????????????????");
                }

            }
            //?????????????????????????????????????????????,???????????????F_ID
//            String tabNameSbdy =  "bes_sbpz_struct";
            String tabNameSbdy =  "bes_sbpz_struct_copy";
            Map<String,Object> pointMap = besSbdyExcelTableImportMapper.selectPointMap(tabNameSbdy,f_sys_name_old);
            besPoint.setfSbid(String.valueOf(pointMap.get("F_ID")));
            besPoint.setfPointState("0");
            if (besPoint.getPointTypeName().equals("AO")) {
                besPoint.setfNodeType("5");
            } else if (besPoint.getPointTypeName().equals("AI")) {
                besPoint.setfNodeType("4");

                String fEnergystatics = besPoint.getfEnergystatics();//???????????????????????????

                if (StringUtils.hasText(fEnergystatics)) {
                    if ("1".equals(fEnergystatics)){//???
                        besPoint.setfEnergystatics("0");
                    } else if ("0".equals(fEnergystatics)) {//???
                        besPoint.setfEnergystatics("1");
                    }
                } else {
                    besPoint.setfEnergystatics("1");//???????????????0:??????1:??????
                }
            }

            //????????????AO,AI????????????????????????
            Boolean insertVPointMapToNodeTable = besSbdyExcelTableImportMapper.insertVPointMapToNodeTable_AO_AI(besPoint);
            if (insertVPointMapToNodeTable) {
                returnObject.setMsg("???????????????");
                returnObject.setStatus("1");
            } else {
                throw new Exception("????????????");
            }


        } catch (FileNotFoundException | NullPointerException e) {
            returnObject.setStatus("0");
            returnObject.setMsg("???????????????");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception e) {
            returnObject.setStatus("0");
            returnObject.setMsg(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return returnObject;
    }



    @Transactional(propagation = Propagation.NESTED)
    public ISSPReturnObject lightingControl(List<sbdyStruct> list, String fileUrl) {
        ISSPReturnObject returnObject = new ISSPReturnObject();

        // ????????????????????????
        List<ExcelError> excelErrors = new ArrayList<>();
        boolean inportflag = false;
        Map<String, Object> psysName = null;
        // ????????????????????????
        FileInputStream fis = null;
        //???ecxcel????????????????????????list??????,????????????
        List<besPointExcel> pointList = null;
        List<besModuleExcel> Modulelist = null;
        List<besDDCExcel> DDClist= null;
        List<besCouplerExcel> couplerList = null;

        String nodeTabName = "???????????????";

        try {

            excelErrors = nodeTable(list);

            /*if (excelErrors.size() > 0) {
                returnObject.setMsg("?????????????????????????????????????????????excel???????????????");
                returnObject.setStatus("2");
                returnObject.setList(excelErrors);
//						return returnObject;
                throw new Exception((Throwable) returnObject.getList());
            }*/

            if (excelErrors.size() > 0) {
                throw new Exception(nodeTabName + "???" + excelErrors.get(0).getRow() + "???" +excelErrors.get(0).getErrorMsg());
            }

            Set<String> point = new HashSet<>();
            for (sbdyStruct sbdyStructs : list) {
                //??????????????????????????????????????????????????????
                Boolean success = point.add(sbdyStructs.getfSysname());
                if (!success) {
                    throw new Exception(sbdyStructs.getfSysname() +":?????????????????????????????????");
                }
            }
            for (sbdyStruct sbdyStructs : list) {
                //??????????????????????????????????????????????????????
//                Boolean success = point.add(sbdyStructs.getfSysname());
//                if (!success) {
//                    throw new Exception(sbdyStructs.getfSysname() +":?????????????????????????????????");
//                }

                if (sbdyStructs.getfType() == null) {
                    throw new Exception(sbdyStructs.getfSysname() + ":?????????????????????????????????,?????????");
                }
                switch (sbdyStructs.getfType()) {
                    case "21":
                    case "24": {//??????????????????,??????(???????????????)

                        returnObject = insertBesStruct(sbdyStructs,nodeTabName);

                        if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                            return returnObject;
                        }

                        break;
                    }
                    case "3": {//?????????????????????

                        if (DDClist == null) {
                            fis = new FileInputStream(fileUrl);
                            ExcelUtil<besDDCExcel> util = new ExcelUtil<>(besDDCExcel.class);
                            DDClist = util.importExcel("?????????????????????", fis);// ??????excel,???????????????list
                        }

                        returnObject = insertDDCByExcel(DDClist,sbdyStructs);

                        if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                            return returnObject;
                        }

                        break;

                    }
                    case "5" :
                    case "6" : {

                        if (couplerList == null) {
                            fis = new FileInputStream(fileUrl);
                            ExcelUtil<besCouplerExcel> util = new ExcelUtil<>(besCouplerExcel.class);
                            couplerList = util.importExcel("???????????????", fis);// ??????excel,???????????????list
                        }
                        returnObject = insertCouplerByExcel(couplerList,sbdyStructs);

                        if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                            return returnObject;
                        }
                        break;
                    }
                    case "9":{//??????

                        if (Modulelist == null) {
                            fis = new FileInputStream(fileUrl);
                            ExcelUtil<besModuleExcel> util = new ExcelUtil<>(besModuleExcel.class);
                            Modulelist = util.importExcel("????????????", fis);// ??????excel,???????????????list
                        }

                        returnObject = insertModuleByExcel(Modulelist,sbdyStructs);

                        if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                            return returnObject;
                        }

                        break;
                    }
                    case "DO":
                    case "DI":
                    case "AO":
                    case "AI": {

                        if (pointList == null) {
                            fis = new FileInputStream(fileUrl);
                            ExcelUtil<besPointExcel> util = new ExcelUtil<>(besPointExcel.class);
                            pointList = util.importExcel("????????????", fis);// ??????excel,???????????????list
                        }

                        List lists=new ArrayList();

                        Set<String> point_DODIAOAI = new HashSet<>();
                        for (besPointExcel besPointExcel : pointList) {
                            //????????????????????????????????????????????????
                            Boolean success1 = point_DODIAOAI.add(besPointExcel.getfSysNameOld());
                            if (!success1) {
                                throw new Exception(besPointExcel.getfSysNameOld() +":???????????????????????????????????????");
                            }
                            lists.add(besPointExcel.getfSysNameOld());
                        }
                        Boolean aa = lists.contains(sbdyStructs.getfSysname());

                        if (!aa) {
                            throw new Exception(sbdyStructs.getfSysname() +"??????????????????????????????????????????");
                        }

                        returnObject = pointInsertByExcel(pointList,sbdyStructs);

                        if (returnObject.getMsg() != null && returnObject.getMsg() != "???????????????") {
                            return  returnObject;
                        }
                        break;
                    }
                    default:
                        throw new Exception("?????????????????????");
                }
            }

        } catch (FileNotFoundException | NullPointerException e) {
            returnObject.setStatus("0");
            returnObject.setMsg("???????????????");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception e) {
            returnObject.setStatus("0");
            returnObject.setMsg(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return returnObject;
    }

    /**
     *
     * @Description: ?????????????????????
     *
     * @auther: wanghongjie
     * @date: 10:35 2020/9/17
     * @param: [couplerList, sbdyStructs]
     * @return: com.core.common.ISSPReturnObject
     *
     */
    public ISSPReturnObject insertCouplerByExcel(List<besCouplerExcel> couplerList, sbdyStruct sbdyStructs) {
        ISSPReturnObject returnObject = new ISSPReturnObject();
        Boolean inportflag = false;
        Map<String,Object> psysNameMap;

        String nodeTabName = "??????????????????";

        try {

            JSONObject obj = new JSONObject();
            List lists=new ArrayList();

            Set<String> point = new HashSet<>();
            for (besCouplerExcel besCouplerExcel : couplerList) {
                //????????????????????????????????????????????????
                Boolean success = point.add(besCouplerExcel.getfSysName());
                if (!success) {
                    throw new Exception(besCouplerExcel.getfSysName() +":???????????????????????????????????????");
                }
                lists.add(besCouplerExcel.getfSysName());
            }
            Boolean aa = lists.contains(sbdyStructs.getfSysname());

            if (!aa) {
                throw new Exception(sbdyStructs.getfSysname() +"?????????????????????????????????????????????");
            }

            returnObject = insertBesStruct(sbdyStructs,nodeTabName);

            for (besCouplerExcel besCouplerExcel : couplerList) {

                String f_sys_name 		= besCouplerExcel.getfSysName();
                String f_nick_name 		= besCouplerExcel.getfNickName();
                String f_azwz 			= besCouplerExcel.getfAzwz();
                String f_description 	= besCouplerExcel.getfDescription();
                String f_node_type 		= besCouplerExcel.getfNodeType();
                String f_addr 			= besCouplerExcel.getfAddr();
                String f_belong_iprouter= besCouplerExcel.getfBelongIprouter();

                if (       !StringUtils.hasText(f_sys_name)
                        || !StringUtils.hasText(f_nick_name)
                        || !StringUtils.hasText(f_azwz)
                        || !StringUtils.hasText(f_description)
                        || !StringUtils.hasText(f_node_type)
                        || !StringUtils.hasText(f_addr)
                        || !StringUtils.hasText(f_belong_iprouter)
                )
                {
                    throw new Exception(f_sys_name + ":????????????????????????????????????");
                }

                if (sbdyStructs.getfSysname().equals(f_sys_name)) {

                    //???????????????????????????????????????????????????
                    if (null == besSbdyExcelTableImportMapper.getSbTreeInfoBySysName(f_sys_name)) {
                        throw new Exception(f_sys_name + "????????????????????????????????????");
                    }

                    //??????????????????????????????????????????????????????????????????????????????
                    List<Map<String,Object>> lightCoupler= besSbdyExcelTableImportMapper.selectLightCouplerList(sbdyStructs.getfPsysname());

                    psysNameMap = besSbdyExcelTableImportMapper.selectSbdyByPsysName(sbdyStructs.getfPsysname());
                    //????????????????????????????????????,?????????????????????DDC?????????
                    if (sbdyStructs.getfType().equals("5")) {
                        if (!psysNameMap.get("F_TYPE").equals(3)) {
                            throw new Exception(f_sys_name + ":?????????????????????DDC????????????");
                        }

                        if (lightCoupler.size() >0) {//???????????????????????????
                            if (sbdyStructs.getfType() != String.valueOf(lightCoupler.get(0).get("F_TYPE"))) {//???????????????????????????????????????????????????????????????
                                if (lightCoupler.get(0).get("F_TYPE").equals(6)) {//???????????????
                                    throw new Exception(f_sys_name + ":???????????????DDC?????????????????????????????????,??????????????????????????????");
                                }else if (lightCoupler.get(0).get("F_TYPE").equals(9)) {//??????
                                    throw new Exception(f_sys_name + ":???????????????DDC??????????????????????????????,??????????????????????????????");
                                }
                            }
                        }
                    }
                    //????????????????????????????????????,??????????????????????????????????????????DDC???????????????
                    if (sbdyStructs.getfType().equals("6")) {
                        if (!psysNameMap.get("F_TYPE").equals(3) && !psysNameMap.get("F_TYPE").equals(5)) {
                            throw new Exception(f_sys_name + ":?????????????????????DDC????????????????????????????????????");
                        }

                        if (lightCoupler.size() >0) {//???????????????????????????
                            if (sbdyStructs.getfType() != String.valueOf(lightCoupler.get(0).get("F_TYPE"))) {//???????????????????????????????????????????????????????????????
                                if (lightCoupler.get(0).get("F_TYPE").equals(5)) {//???????????????
                                    throw new Exception(f_sys_name + ":???????????????DDC?????????????????????????????????,??????????????????????????????");
                                }else if (lightCoupler.get(0).get("F_TYPE").equals(9)) {//??????
                                    throw new Exception(f_sys_name + ":???????????????DDC??????????????????????????????,??????????????????????????????");
                                }
                            }
                        }
                    }

                    besCouplerExcel.setfType(sbdyStructs.getfType());

                    inportflag = besSbdyExcelTableImportMapper.add_sbdyStructCoupler(besCouplerExcel);
                }
            }

            if (inportflag) {
                returnObject.setMsg("???????????????");
                returnObject.setStatus("1");
            } else {
                throw new Exception("????????????");
            }
        } catch (FileNotFoundException | NullPointerException e) {
            returnObject.setStatus("0");
            returnObject.setMsg("???????????????");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception e) {
            returnObject.setStatus("0");
            returnObject.setMsg(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return returnObject;
    }


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

    /**
     * ??????????????????(??????????????????)
     * @param obj
     * @return
     */
    private int addDefaultNodes(JSONObject obj){
        List<BESModulePointType> modulePointTypeList = besSbdyMapper.getModulePointTypeInfo();//???????????????????????????
        Map<String, String > mPointTypeMap = new HashMap<>();//(key:ID value:???????????????)
        for(BESModulePointType mPointType : modulePointTypeList){
            mPointTypeMap.put(mPointType.getfId(), mPointType.getfModulepointType());
        }
        List<BESEpModuleTypeRlgl> epNoduleTypeList = besSbdyMapper.getEpModuleTypeRlglInfo();//?????????????????????????????????????????????
        Map<String, String > epModuleTypeRlglMap = new HashMap<>();//(key:ID value:?????????????????????)
        for(BESEpModuleTypeRlgl epModuleTypeRlgl : epNoduleTypeList){
            epModuleTypeRlglMap.put(epModuleTypeRlgl.getfModulepointId(), epModuleTypeRlgl.getfEpTreenodeType());
        }
        String pFsysName = obj.getString("attr_f_sys_name");
        char[] nodeTypes = obj.getString("other_node_types").toCharArray();
        List<BESSbPzStruct> nodeList = new ArrayList<>();
        List<BESSbPzStruct> nodeList1 = new ArrayList<>();
        //?????????id??????1????????????????????????id
//        int f_sbid = obj.getInteger("f_sbid") + 1;
        for(int i=0;i<nodeTypes.length;i++){

            String nodeName = mPointTypeMap.get(String.valueOf(nodeTypes[i]));

            BESSbPzStruct sbPzStruct = new BESSbPzStruct();
            BESSbPzStruct sbPzStruct1 = new BESSbPzStruct();
//            sbPzStruct.setF_sbid(String.valueOf(f_sbid));
            sbPzStruct.setF_sys_name(pFsysName+"0"+String.valueOf(i));
            sbPzStruct.setF_psys_name(pFsysName);
            sbPzStruct.setF_nick_name(nodeName);
            sbPzStruct.setF_allpath(obj.getString("f_allpath")+">"+nodeName);
            sbPzStruct.setF_type(epModuleTypeRlglMap.get(String.valueOf(nodeTypes[i])));
            sbPzStruct.setF_node_attribution(obj.getString("f_node_attribution"));
            sbPzStruct.setF_status("0");

            sbPzStruct1.setF_sys_name(pFsysName+"0"+String.valueOf(i));
            sbPzStruct1.setF_psys_name(pFsysName);
            sbPzStruct1.setF_nick_name(nodeName);
            sbPzStruct1.setF_allpath(obj.getString("f_allpath")+">"+nodeName);
            sbPzStruct1.setF_type(epModuleTypeRlglMap.get(String.valueOf(nodeTypes[i])));
            sbPzStruct1.setF_node_attribution(obj.getString("f_node_attribution"));
            sbPzStruct1.setF_status("0");
            nodeList.add(sbPzStruct);
            nodeList1.add(sbPzStruct1);
//            f_sbid++;
        }

        int insertCount = besSbdyMapper.batchInsert(nodeList);
        if (insertCount > 0) {
            besSbdyMapper.batchInsertCopy(nodeList1);
        }


        return insertCount;
    }

    private int getSbid(String f_sys_name) {
        int maxSbid = 1;
        int count = besSbdyMapper.getSumSbCount(f_sys_name);
        if(count>0){
            maxSbid = count;
        }
        return maxSbid;
    }

    /**
     *
     * @Description: DDC??????????????????????????????????????????,???????????????
     *
     * @auther: wanghongjie
     * @date: 9:59 2020/9/12
     * @param: [f_sys_name]
     * @return: void
     *
     */
    /*@Transactional(propagation = Propagation.NESTED)
    public  ISSPReturnObject insertVirtualPoint_bus_line(String fSysName, String path, String f_node_type) {
        ISSPReturnObject returnObject = new ISSPReturnObject();
        Boolean addVPoint ;
        Boolean addBus ;
        Boolean addLine = false;
        try {

            BESSbPzStruct besSbPzStruct = new BESSbPzStruct();
            //????????????
            String vPointNodeNoPage = fSysName + "01";
            //????????????
            String busNode = fSysName + "02";
            //????????????
            String pnp = busNode + "01";
            String fln1 = busNode + "02";
            String fln2 = busNode + "03";
            String fln3 = busNode + "04";
            String fln4 = busNode + "05";

            List lineList = new ArrayList();
            lineList.add(pnp);
            lineList.add(fln1);
            lineList.add(fln2);
            lineList.add(fln3);
            lineList.add(fln4);


            besSbPzStruct.setF_poll_status("1");
            besSbPzStruct.setF_status("0");
            besSbPzStruct.setF_node_attribution(f_node_type);

            //?????????????????????????????????
            besSbPzStruct.setF_sys_name(vPointNodeNoPage);
            besSbPzStruct.setF_nick_name("??????");
            besSbPzStruct.setF_allpath(path + ">??????");
            besSbPzStruct.setF_type("24");
            besSbPzStruct.setF_psys_name(fSysName);

            //???????????????????????????????????????????????????
            if (null != besSbdyMapper.getSbTreeInfoBySysName(vPointNodeNoPage)) {
                throw new Exception("????????????????????????????????????????????????");
            }

            addVPoint = besSbdyMapper.add_sbdyStruct(besSbPzStruct);
            if (addVPoint) {
                besSbPzStruct.setF_sys_name(busNode);
                besSbPzStruct.setF_nick_name("??????");
                besSbPzStruct.setF_allpath(path + ">??????");
                besSbPzStruct.setF_type("8");
                besSbPzStruct.setF_psys_name(fSysName);
                //???????????????????????????????????????????????????
                if (null != besSbdyMapper.getSbTreeInfoBySysName(busNode)) {
                    throw new Exception("????????????????????????????????????????????????");
                }

                addBus = besSbdyMapper.add_sbdyStruct(besSbPzStruct);

                if (addBus) {
                    besSbPzStruct.setF_type("23");
                    besSbPzStruct.setF_psys_name(busNode);
                    for (int i = 0;i < lineList.size(); i++) {
                        if (i == 0) {
                            besSbPzStruct.setF_sys_name(pnp);
                            besSbPzStruct.setF_nick_name("PNP");
                            besSbPzStruct.setF_allpath(path + ">PNP");
                            //???????????????????????????????????????????????????
                            if (null != besSbdyMapper.getSbTreeInfoBySysName(pnp)) {
                                throw new Exception("???????????????PNP???????????????????????????");
                            }
                            addLine = besSbdyMapper.add_sbdyStruct(besSbPzStruct);
                        }else if (i == 1) {
                            besSbPzStruct.setF_sys_name(fln1);
                            besSbPzStruct.setF_nick_name("FLN1");
                            besSbPzStruct.setF_allpath(path + ">FLN1");
                            //???????????????????????????????????????????????????
                            if (null != besSbdyMapper.getSbTreeInfoBySysName(fln1)) {
                                throw new Exception("???????????????FLN1???????????????????????????");
                            }
                            addLine = besSbdyMapper.add_sbdyStruct(besSbPzStruct);
                        }else if (i == 2) {
                            besSbPzStruct.setF_sys_name(fln2);
                            besSbPzStruct.setF_nick_name("FLN2");
                            besSbPzStruct.setF_allpath(path + ">FLN2");
                            //???????????????????????????????????????????????????
                            if (null != besSbdyMapper.getSbTreeInfoBySysName(fln2)) {
                                throw new Exception("???????????????FLN2???????????????????????????");
                            }
                            addLine = besSbdyMapper.add_sbdyStruct(besSbPzStruct);
                        }else if (i == 3) {
                            besSbPzStruct.setF_sys_name(fln3);
                            besSbPzStruct.setF_nick_name("FLN3");
                            besSbPzStruct.setF_allpath(path + ">FLN3");
                            //???????????????????????????????????????????????????
                            if (null != besSbdyMapper.getSbTreeInfoBySysName(fln3)) {
                                throw new Exception("???????????????FLN3???????????????????????????");
                            }
                            addLine = besSbdyMapper.add_sbdyStruct(besSbPzStruct);
                        }else if (i == 4) {
                            besSbPzStruct.setF_sys_name(fln4);
                            besSbPzStruct.setF_nick_name("FLN4");
                            besSbPzStruct.setF_allpath(path + ">FLN4");
                            //???????????????????????????????????????????????????
                            if (null != besSbdyMapper.getSbTreeInfoBySysName(fln4)) {
                                throw new Exception("???????????????FLN4???????????????????????????");
                            }
                            addLine = besSbdyMapper.add_sbdyStruct(besSbPzStruct);


                        }
                    }

                    if (addLine) {
                        returnObject.setMsg("????????????");
                    }
                }
            }


        } catch (FileNotFoundException | NullPointerException e) {
            returnObject.setStatus("0");
            returnObject.setMsg("???????????????");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception e) {
            returnObject.setStatus("0");
            returnObject.setMsg(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return returnObject;
    }*/
}
