<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<s:actionerror/>
<s:form action="test" method="post" >
<table>
<tr><td style="width:400px;"><h3 class="leftbox"><a href="homePage">HOME</a></h3></td></tr>
</table>
<table cellpadding="3" cellspacing="3" border="1">
<thead>
 <tr>
 <th>Installment ID</th>
 <th>Payment ID</th>
 <th>Loan Account No </th>
 <th>Installment Date</th>
 <th>Installment Amt</th>
 <th>Delete</th>
 </tr>
 </thead>
 <tbody>
  <s:iterator value="installmentList" >
  <tr>
  <td><s:property value="installId"/></td>
  <td><s:property value="custPmtDet.pmtId"/></td>
  <td><s:property value="custLoanDet.loanAccno"/></td>
  <td><s:property value="installmentDt"/></td>
  <td><s:property value="installmentAmt"/></td>
  <td><a href="installDelete?installId=<s:property value="installId"/>&pmtId=<s:property value="custPmtDet.pmtId"/>&installmentAmt=<s:property value="installmentAmt"/>&loanAccno=<s:property value="custLoanDet.loanAccno"/>">Delete</a></td>
  </tr>
  </s:iterator>
</tbody>
</table>
</s:form>