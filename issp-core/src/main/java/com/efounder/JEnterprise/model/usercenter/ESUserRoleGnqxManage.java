package com.efounder.JEnterprise.model.usercenter;

import com.core.common.BaseEntity;

import java.io.Serializable;

/**
 * @description 角色功能权限管理
 * @author gongfanfei
 * @datetime 2018年5月21日
 */
public class ESUserRoleGnqxManage implements BaseEntity<Serializable>{

	private static final long serialVersionUID = -8213322412113123L;

	
	private String f_rolebh;// 角色编号
	private String f_gnzdid;// 功能字典ID
	private String f_sh;// 审核
	private String f_crdate;// 创建日期
	private String f_chdate;// 修改日期
	public String getF_rolebh() {
		return f_rolebh;
	}
	public void setF_rolebh(String f_rolebh) {
		this.f_rolebh = f_rolebh;
	}
	public String getF_gnzdid() {
		return f_gnzdid;
	}
	public void setF_gnzdid(String f_gnzdid) {
		this.f_gnzdid = f_gnzdid;
	}
	public String getF_sh() {
		return f_sh;
	}
	public void setF_sh(String f_sh) {
		this.f_sh = f_sh;
	}
	public String getF_crdate() {
		return f_crdate;
	}
	public void setF_crdate(String f_crdate) {
		this.f_crdate = f_crdate;
	}
	public String getF_chdate() {
		return f_chdate;
	}
	public void setF_chdate(String f_chdate) {
		this.f_chdate = f_chdate;
	}


}
