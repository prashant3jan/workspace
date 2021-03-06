package com.struts2.action;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.opensymphony.xwork2.ActionSupport;
import com.struts2.model.CustPmtBean;
import com.struts2.model.Installment;


public class CustPmtAction extends ActionSupport {
    private static final long serialVersionUID = 1L;
    private CustPmtManager uploadManager;
    private Installment installmentBean;
    private CustPmtBean custPmtDetBean;
	private File fileUpload;
	private int installId;
	private String fileUploadContentType;
    private String fileUploadFileName;
    private String loanAccno;
    private Double installmentAmt;
    private int pmtId;
	private List<Installment> installmentList  = new ArrayList<Installment>();
	private List<CustPmtBean> paymentList =  new ArrayList<CustPmtBean>();
    public int getPmtId() {
		return pmtId;
	}

	public void setPmtId(int pmtId) {
		this.pmtId = pmtId;
	}

	
  
	public CustPmtAction(){
    	uploadManager = new CustPmtManager();
    }

	public String execute(){
		paymentList = uploadManager.getPaymentList();
		return "success";
	}

	public String excelFileUpload() throws Exception{
		try{
			String excelFilePath=getText("upload.file.path") + fileUploadFileName;
		uploadManager.excelFileUpload(excelFilePath, fileUpload);
		}catch(Exception e){
			e.printStackTrace();
		}
		paymentList = uploadManager.getPaymentList();
		return "success";
		
	}
       
    public String deleteInstllmnt(){
    	try{
    		System.out.println("installId"+installId);
    		System.out.println("pmtId"+pmtId);
    		System.out.println("installmentAmt"+installmentAmt);
    	uploadManager.deleteCustInstall(installId, pmtId, installmentAmt);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	installmentList = uploadManager.getInstallmentList(loanAccno);
    	return "success";
    }
    
    public  String getInstllDet(){
    	System.out.println("loanAccno"+loanAccno);
    	installmentList = uploadManager.getInstallmentList(loanAccno);
    	return "success";
    }
    
    public String saveIndivInstlmnt(){
    	System.out.println("loanAccno"+custPmtDetBean.getCustLoanDet().getLoanAccno());
    	uploadManager.saveIndivInstlmnt(installmentBean,custPmtDetBean);
    	installmentList = uploadManager.getInstallmentList();
    	return "success";
    }
    
	public Installment getInstallmentBean() {
		return installmentBean;
	}

	public void setInstallmentBean(Installment installmentBean) {
		this.installmentBean = installmentBean;
	}
    
	public int getInstallId() {
		return installId;
	}

	public void setInstallId(int installId) {
		this.installId = installId;
	}
	
	public File getFileUpload() {
		return fileUpload;
	}
	public void setFileUpload(File fileUpload) {
		this.fileUpload = fileUpload;
	}
	public String getFileUploadContentType() {
		return fileUploadContentType;
	}
	public void setFileUploadContentType(String fileUploadContentType) {
		this.fileUploadContentType = fileUploadContentType;
	}
	public String getFileUploadFileName() {
		return fileUploadFileName;
	}
	public void setFileUploadFileName(String fileUploadFileName) {
		this.fileUploadFileName = fileUploadFileName;
	}

	public List<CustPmtBean> getPaymentList() {
		return paymentList;
	}

	public void setPaymentList(List<CustPmtBean> paymentList) {
		this.paymentList = paymentList;
	}

    public List<Installment> getInstallmentList() {
		return installmentList;
	}

	public void setInstallmentList(List<Installment> installmentList) {
		this.installmentList = installmentList;
	}
  
    public String getLoanAccno() {
		return loanAccno;
	}

	public void setLoanAccno(String loanAccno) {
		this.loanAccno = loanAccno;
	}

	public CustPmtBean getCustPmtDetBean() {
		return custPmtDetBean;
	}

	public void setCustPmtDetBean(CustPmtBean custPmtDetBean) {
		this.custPmtDetBean = custPmtDetBean;
	}

	public Double getInstallmentAmt() {
		return installmentAmt;
	}

	public void setInstallmentAmt(Double installmentAmt) {
		this.installmentAmt = installmentAmt;
	}

	
}