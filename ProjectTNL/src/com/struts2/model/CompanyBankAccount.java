package com.struts2.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="compbank_account")
public class CompanyBankAccount {

	@Id
	@GeneratedValue
	@Column(name="tran_id")
	private int tranId;
	@Column(name="tran_date")
	private Date tranDate;
	@Column(name="debtor_name")
	private String debtorName;
	@Column(name="creditor_name")
	private String creditorName;
	@Column(name="pmt_mode")
	private String pmtMode;
	@Column(name="chq_no")
	private String chequeNo;
	@Column(name="debit_amt")
	private Long debitAmount;
	@Column(name="credit_amt_fig")
	private Long creditAmountFig;
	@Column(name="credit_amt_words")
	private String creditAmountWords;
	@Column(name="tot_credit")
	private long totalCredit;
	@Column(name="tot_debit")
	private long totalDebit;
	@Column(name="balance")
	private long balance;
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_accno")
	private CustLoanBean loan;
	@Column(name="recipient")
	private String recipient;
	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public CompanyBankAccount(){}

	public CompanyBankAccount(int _tranId, Date _tranDate, String _debtorName, String _creditorName, String _pmtMode,
			String _chequeNo, Long _debitAmount, Long _creditAmountFig, String _creditAmtWords, String _rcpntName,
			long _totalCredit, long _totalDebit, long _balance, CustLoanBean _loan, String _recipient) {
		this.tranId = _tranId;
		this.tranDate = _tranDate;
		this.debtorName =  _debtorName;
		this.creditorName = _creditorName;
		this.pmtMode = _pmtMode;
		this.chequeNo =  _chequeNo;
		this.debitAmount=_debitAmount;
		this.creditAmountFig=_creditAmountFig;
		this.creditAmountWords = _creditAmtWords;
		this.totalDebit = _totalDebit;
		this.totalCredit = _totalCredit;
		this.balance = _balance;
		this.loan = _loan;
		this.recipient = _recipient;
	}
	public CustLoanBean getLoan() {
		return loan;
	}

	public void setLoan(CustLoanBean loan) {
		this.loan = loan;
	}

	
	public Long getCreditAmountFig() {
		return creditAmountFig;
	}

	public void setCreditAmountFig(Long creditAmountFig) {
		this.creditAmountFig = creditAmountFig;
	}

	public String getCreditAmountWords() {
		return creditAmountWords;
	}

	public void setCreditAmountWords(String creditAmountWords) {
		this.creditAmountWords = creditAmountWords;
	}

	public long getTotalCredit() {
		return totalCredit;
	}

	public void setTotalCredit(long totalCredit) {
		this.totalCredit = totalCredit;
	}

	public long getTotalDebit() {
		return totalDebit;
	}

	public void setTotalDebit(long totalDebit) {
		this.totalDebit = totalDebit;
	}

	public long getBalance() {
		return balance;
	}

	public void setBalance(long balance) {
		this.balance = balance;
	}

	public int getTranId() {
		return tranId;
	}

	public void setTranId(int tranId) {
		this.tranId = tranId;
	}

	public Date getTranDate() {
		return tranDate;
	}

	public void setTranDate(Date tranDate) {
		this.tranDate = tranDate;
	}

	public String getDebtorName() {
		return debtorName;
	}

	public void setDebtorName(String debtorName) {
		this.debtorName = debtorName;
	}

	public String getCreditorName() {
		return creditorName;
	}

	public void setCreditorName(String creditorName) {
		this.creditorName = creditorName;
	}

	public String getPmtMode() {
		return pmtMode;
	}

	public void setPmtMode(String pmtMode) {
		this.pmtMode = pmtMode;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public Long getDebitAmount() {
		return debitAmount;
	}

	public void setDebitAmount(Long debitAmount) {
		this.debitAmount = debitAmount;
	}
}
