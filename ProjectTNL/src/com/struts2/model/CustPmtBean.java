package com.struts2.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
@Entity
@Table(name="custpmt_bean")
public class CustPmtBean{
	@Id
	@GeneratedValue
	@Column(name="payment_id")
	private int pmtId;
	@Column(name = "wkly_due")
	private String weeklyDue;
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_accno")
	private CustLoanBean custLoanDet;	
	public CustLoanBean getCustLoanDet() {
		return custLoanDet;
	}

	public void setCustLoanDet(CustLoanBean custLoanDet) {
		this.custLoanDet = custLoanDet;
	}

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mem_no")
	private Member member;
	@Column(name = "debit_loanAmtWithInt")
	private Double debitTotLoanWithInterest;
	@Temporal(TemporalType.DATE)
	@Column(name = "loan_dt")
	private Date dateOfLoan;
	@Column(name = "loan_mem_name")
	private String loanMemName;
	@Column(name = "credit_pmtGvnByCust")
	private double creditPmtGvnByCust;
	@Column(name = "start_date")
	private Date monthStartDate;
	@Column(name = "end_date")
	private Date monthEndDate;
	private Double totalMonthlyPmt;
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="payment_id")
	private List<Installment> installmentList = new ArrayList<Installment>();
	private String loanStatus;
	
public CustPmtBean(){}
	
	public CustPmtBean(String _weeklyDue, Member _member, Double _debitTotLoanWithInterest, double _creditPmtGvnByCust,
			Date _dateOfLoan, List<Installment> _installmentList, String _loanMemName, Date _monthStartDate, Date _monthEndDate, Double _totalMonthlyPmt, String _loanStatus){
		this.weeklyDue = _weeklyDue;
		this.member = _member;
		this.debitTotLoanWithInterest = _debitTotLoanWithInterest;
		this.creditPmtGvnByCust = _creditPmtGvnByCust;
		this.dateOfLoan = _dateOfLoan;
		this.installmentList = _installmentList;
		this.loanMemName = _loanMemName;
		this.monthStartDate = _monthEndDate;
		this.totalMonthlyPmt = _totalMonthlyPmt;
		this.loanStatus = _loanStatus;
	}
	
	public Double getDebitTotLoanWithInterest() {
		return debitTotLoanWithInterest;
	}

	public void setDebitTotLoanWithInterest(Double debitTotLoanWithInterest) {
		this.debitTotLoanWithInterest = debitTotLoanWithInterest;
	}

	public double getCreditPmtGvnByCust() {
		return creditPmtGvnByCust;
	}

	public void setCreditPmtGvnByCust(double creditPmtGvnByCust) {
		this.creditPmtGvnByCust = creditPmtGvnByCust;
	}


	public int getPmtId() {
		return pmtId;
	}

	public void setPmtId(int pmtId) {
		this.pmtId = pmtId;
	}

	public Date getMonthStartDate() {
		return monthStartDate;
	}

	public void setMonthStartDate(Date monthStartDate) {
		this.monthStartDate = monthStartDate;
	}

	public Date getMonthEndDate() {
		return monthEndDate;
	}

	public void setMonthEndDate(Date monthEndDate) {
		this.monthEndDate = monthEndDate;
	}

	public String getLoanMemName() {
		return loanMemName;
	}

	public void setLoanMemName(String loanMemName) {
		this.loanMemName = loanMemName;
	}

	public List<Installment> getInstallmentList() {
		return installmentList;
	}

	public void setInstallmentList(List<Installment> installmentList) {
		this.installmentList = installmentList;
	}

	public Date getDateOfLoan() {
		return dateOfLoan;
	}

	public void setDateOfLoan(Date dateOfLoan) {
		this.dateOfLoan = dateOfLoan;
	}


	public String getWeeklyDue() {
		return weeklyDue;
	}

	public void setWeeklyDue(String weeklyDue) {
		this.weeklyDue = weeklyDue;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public Double getTotalMonthlyPmt() {
		return totalMonthlyPmt;
	}

	public void setTotalMonthlyPmt(Double totalMonthlyPmt) {
		this.totalMonthlyPmt = totalMonthlyPmt;
	}

	public String getLoanStatus() {
		return loanStatus;
	}

	public void setLoanStatus(String loanStatus) {
		this.loanStatus = loanStatus;
	}
}
