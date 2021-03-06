<%@ taglib prefix="s" uri="/struts-tags" %>
<table>
<tr><td style="width:400px;"><h3 class="leftbox"><a href="homePage">HOME</a></h3></td></tr>
</table>
<s:actionerror />
<s:form action="doUpload" namespace="/" method="post" enctype="multipart/form-data" >
<table>
<tr><td><h1>Upload Member Payment Excel Sheet</h1></td></tr>
<tr><td><s:file name="fileUpload" label="Upload Customer Payment File" required="required"/></td></tr>
<tr><td><s:submit value="submit" name="Upload"/></td></tr>
</table>
</s:form>
<s:form action="enterIndivInstlmnt"  method="post">
<table>
<tr><td><h1>Enter Member Payment Installments</h1></td></tr>
<tr><td>Membership No <s:textfield name="custPmtDetBean.member.memNum" required="required" /></td></tr>
<tr><td>LoanAccount No <s:textfield name="custPmtDetBean.custLoanDet.loanAccno" required="required" /></td></tr>
<tr><td>Installment amount: <s:textfield name="installmentBean.installmentAmt" required="required"/></td></tr>
<tr><td>Installment Date (MM/dd/yyyy)<s:textfield name="installmentBean.installmentDt" required="required"/></td></tr>
<tr><td>Monthly Start Date (MM/dd/yyyy)  <s:textfield name="custPmtDetBean.monthStartDate" required="required"/></td></tr>
<tr><td>Monthly End Date (MM/dd/yyyy)  <s:textfield name="custPmtDetBean.monthEndDate" required="required"/></td></tr>
<tr><td>Weekly Due <s:textfield name="custPmtDetBean.weeklyDue"  required="required"/></td></tr>
<tr><td>Loan Member Name <s:textfield name="custPmtDetBean.loanMemName" required="required"/></td></tr>
<tr><td>Total Loan Amount Including Interest <s:textfield name="custPmtDetBean.debitTotLoanWithInterest" required="required"/></td></tr>
<tr><td>Date of Loan Disbursement By Company <s:textfield name="custPmtDetBean.dateOfLoan" required="required"/></td></tr>
<tr><td><s:submit value="Add Loan" align="center" /></td></tr>
</table>
</s:form>
<div class="page-break"><div><span>page break</span></div></div> 

<h2>Loan Details</h2>
<div class="zui-wrapper">
    <div class="zui-scroller">
        <table class="zui-table">
            <thead>
                <tr>	
                	<th class="zui-sticky-col">Membership Number</th>
                	<th>LoanAccountNumber</th>
                	<th>Date Of Loan</th>
                	<th>Loan Member Name</th>
                	<th>Weekly Due</th>
                	<th>Debit(Loan Given)</th>
                	<th>Credit(Amount Received)</th>
                	<th>Month Start Date</th>
                	<th>Month End Date</th>
                	<th>Total Monthly Payment</th>
                	<th>Payment Details</th>
					</tr>
            </thead>
            <tbody>
            <s:iterator value="paymentList" var="payment">
				<tr>
					<td class="zui-sticky-col"><s:property value="member.memNum"/></td>
						<td><s:property value="custLoanDet.loanAccno"/></td>
						<td><s:property value="dateOfLoan"/></td>
						<td><s:property value="loanMemName"/></td>
						<td><s:property value="weeklyDue"/></td>
						<td><s:property value="debitTotLoanWithInterest"/></td>
						<td><s:property value="creditPmtGvnByCust"/></td>
						<td><s:property value="monthStartDate"/></td>
						<td><s:property value="monthEndDate"/></td>
						<td><s:property value="totalMonthlyPmt"/></td>
					    <td><a href="dispInstllDet?loanAccno=<s:property value="custLoanDet.loanAccno"/>">Get Installment Details</a></td>
				</tr>	
			</s:iterator>
		</tbody>
	</table>
</div>
</div>
