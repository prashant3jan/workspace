package com.struts2.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class MemberBankInfo implements Serializable {
	@Id
	@Column(name = "mem_no", nullable = false)
	private String memNum;
	@Column(name = "app_age")
	private Long applicantAge;
	@Column(name = "father_age")
	private Long fatherAge;
	@Column(name = "mother_age")
	private Long motherAge;
	@Column(name = "spouse_name")
	private String spouseName;
	@Column(name = "spouse_age")
	private Long spouseAge;
	@Column(name = "app_shop_add_with_landmark")
	private String applicantShopAddressWithLandmark;
	@Column(name = "app_wife_paternal_add")
	private String applicantWifePaternalAddress;
	@Column(name = "app_local_add_with_landmark")
	private String applicantLocalAddressWithLandmark;
	@Column(name = "app_bank_name")
	private String appBankName;
	@Column(name = "app_bank_branch")
	private String appBankBranch;
	@Column(name = "app_bank_accno")
	private String appBankAccno;
	@Column(name = "app_bank_ifsc")
	private String appBankIFSC;
	@Column(name = "dpst_mode")
	private String depositMode;
	@Column(name = "co_app_res_add_with_landmark")
	private String coAppResAddWithLandmark;
	@Column(name = "co_app_mob")
	private String coAppMobile;
	public String getMemNum() {
		return memNum;
	}
	public void setMemNum(String memNum) {
		this.memNum = memNum;
	}
	public Long getApplicantAge() {
		return applicantAge;
	}
	public void setApplicantAge(Long applicantAge) {
		this.applicantAge = applicantAge;
	}
	public Long getFatherAge() {
		return fatherAge;
	}
	public void setFatherAge(Long fatherAge) {
		this.fatherAge = fatherAge;
	}
	public Long getMotherAge() {
		return motherAge;
	}
	public void setMotherAge(Long motherAge) {
		this.motherAge = motherAge;
	}
	public String getSpouseName() {
		return spouseName;
	}
	public void setSpouseName(String spouseName) {
		this.spouseName = spouseName;
	}
	public Long getSpouseAge() {
		return spouseAge;
	}
	public void setSpouseAge(Long spouseAge) {
		this.spouseAge = spouseAge;
	}
	public String getApplicantShopAddressWithLandmark() {
		return applicantShopAddressWithLandmark;
	}
	public void setApplicantShopAddressWithLandmark(String applicantShopAddressWithLandmark) {
		this.applicantShopAddressWithLandmark = applicantShopAddressWithLandmark;
	}
	public String getApplicantLocalAddressWithLandmark() {
		return applicantLocalAddressWithLandmark;
	}
	public void setApplicantLocalAddressWithLandmark(String applicantLocalAddressWithLandmark) {
		this.applicantLocalAddressWithLandmark = applicantLocalAddressWithLandmark;
	}
	public String getAppBankName() {
		return appBankName;
	}
	public void setAppBankName(String appBankName) {
		this.appBankName = appBankName;
	}
	public String getAppBankBranch() {
		return appBankBranch;
	}
	public void setAppBankBranch(String appBankBranch) {
		this.appBankBranch = appBankBranch;
	}
	public String getAppBankAccno() {
		return appBankAccno;
	}
	public void setAppBankAccno(String appBankAccno) {
		this.appBankAccno = appBankAccno;
	}
	public String getAppBankIFSC() {
		return appBankIFSC;
	}
	public void setAppBankIFSC(String appBankIFSC) {
		this.appBankIFSC = appBankIFSC;
	}
	public String getDepositMode() {
		return depositMode;
	}
	public void setDepositMode(String depositMode) {
		this.depositMode = depositMode;
	}
	public String getCoAppResAddWithLandmark() {
		return coAppResAddWithLandmark;
	}
	public void setCoAppResAddWithLandmark(String coAppResAddWithLandmark) {
		this.coAppResAddWithLandmark = coAppResAddWithLandmark;
	}
	public String getCoAppMobile() {
		return coAppMobile;
	}
	public void setCoAppMobile(String coAppMobile) {
		this.coAppMobile = coAppMobile;
	}
	public String getApplicantWifePaternalAddress() {
		return applicantWifePaternalAddress;
	}
	public void setApplicantWifePaternalAddress(String applicantWifePaternalAddress) {
		this.applicantWifePaternalAddress = applicantWifePaternalAddress;
	}
	
}
