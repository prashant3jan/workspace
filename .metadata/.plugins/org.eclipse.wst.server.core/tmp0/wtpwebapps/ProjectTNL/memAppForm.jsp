<%@taglib uri="/struts-tags" prefix="s"%><%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<s:actionerror/>
<s:form action="addMemDet" method="post" enctype="multipart/form-data" theme="simple">
<table>
<tr><td style="width:400px;"><h3 class="leftbox"><a href="homePage">HOME</a></h3></td><td>(For office use only)</td></tr>
</table>
	
 <div class="container">
 <div class="leftbox"> Membership No <s:textfield name="memberBean.memNum" required="required"/></div>
 <div class="rightbox"> Membership Date (MM/dd/yyyy)  <s:textfield name="memberBean.memDate" required="required"/></div>
 </div>
 
<div id="colorbox"><h3 class="para-class" >MEMBERSHIP APPLICATION FORM</h3></div>
<div class="box2" >
 To,<br>
 The Board of Directors,<br>
 TRIVENI NIDHI LIMITED<br>
 11 B/1A/2 Lala Ram narayan Lal Road,<br>
 Bank Road Katra, Allahabad, (U.P.), India 211002<br>
 
 Dear Sir/Madam<br>
 
 I we hereby apply you for and Membership Application in your organization, I agree<br>
 to pay valuable amount for Membership fee @ 100/- for each Member<br>
 
 <div id="box3" class="rightbox">Upload Self Attested Photographs <br> Signature or thumb impression<s:file name="memberBean.memImage" id="fileinput1" onChange="previewFile1(this)" required="required" /><img id="previewImg1" src="#" alt="Placeholder" /></div> 
</div>
<div id="colorbox"><h3 class="para-class" >APPLICANT DETAILS (Please Fill In Hindi / English )</h3></div>
		
<table>
<tr>
  	<td>Applicant Name: <s:textfield name="memberBean.applicantName" style="width:170px;" required="required"/></td>
  	<td>D.O.B.(MM/dd/yyyy)  <s:textfield name="memberBean.applicantDob" required="required"/></td>
  	<td>Sex<s:select list="{'Male','Female'}" name="memberBean.applicantSex" multiple="false" /></td>
</tr>
<tr><td>Father's / Husbands Name<s:textfield name="memberBean.applicantFather" style="width:120px;" /></td></tr>
<tr><td>Applicant's Mother's Name<s:textfield name="memberBean.applicantMother" style="width:120px;" /></td></tr>
<tr><td>Applicant's Occupation<s:select name="memberBean.applicantOccupation" list="{'Business','Govt. Service', 'Pvt. Service','Agriculture','Others'}" multiple="false" /></td></tr>
<tr><td>Present Address <s:textfield name="memberBean.applicantAddress" required="required"/></td></tr>
<tr>
  	<td>Applicant's City<s:textfield name="memberBean.applicantCity" required="required"/></td>
  	<td>Applicant's State<s:textfield name="memberBean.applicantState" required="required"/></td>
  	<td>Applicant's Pin<s:textfield name="memberBean.applicantPin" required="required"/></td>
</tr>
<tr>
	<td>PAN Number <s:textfield name="memberBean.applicantPan" label="Applicant's PAN Number"/></td>
	<td>Mobile No <s:textfield name="memberBean.applicantMobile" label="Applicant's Mobile Number" required="required"/></td>
</tr>		
<tr>
	<td>Nominee's Name<s:textfield name="memberBean.nomineeName"/></td>
	<td>Nominee's Age<s:textfield name="memberBean.nomineeAge" /></td>
	<td>Relationship <s:textfield name="memberBean.relnWithNominee"/></td>
</tr>
<tr><td>Nominee's Address<s:textfield name="memberBean.nomineeAddress" /></td></tr>
</table>
<div id="colorbox"><h3 class="para-class" >MEMBERSHIP FEE</h3></div>
<div id="box5">
		<div>Membership fee in figure: <s:textfield name="memberBean.memFeeFig" /></div>
		<div>Membership Fee In Words<s:textfield name="memberBean.memFeeWords" /></div>
		<div class="container_noborder">
		<div id="box11" class="leftbox">Authorized Signatory<s:file name="memberBean.authSigntry" id="fileinput5" onChange="previewFile5(this)"  required="required"/><img id="previewSign5" src="#" alt="Placeholder" /></div>
		<div class="rightbox">Applicant Signature</div>
		</div>
</div>
<div class="page-break"><div><span>page break</span></div></div> 
<div><h3 class="para-class" >KYC DOCUMENTATION CHECKLIST FORM (FOR LOAN)<br></h3></div>
Membership No..................................(Required document to be collected and tick-marked asper KYC Normsof Govt of India)
<div id="box6">
<div id="colorbox"><h3 class="centeralign" >ID AND ADDRESS PROOF. Please Tick</h3></div>

<s:iterator value="documentList" var="docName"  status="st" >

 <s:if test="#st.Even">
 <div class="floatLeft"> 
 <table>
		<tr>
 		<td><s:checkbox name="memberBean.documents.checkbox" fieldValue="%{docName}" /></td>
 		<td><s:property value="docName" /></td>
 		<td><s:textfield name="memberBean.documents.textbox" /></td>
 		</tr>
 </table>
 </div>
</s:if>
<s:else>
<div class="floatRight">
<table>
<tr>
		<td><s:checkbox name="memberBean.documents.checkbox" fieldValue="%{docName}" /></td>
 		<td><s:property value="docName" /></td>
 		<td><s:textfield name="memberBean.documents.textbox" /></td>
</tr>
 </table>
 </div>
</s:else>
 </s:iterator>


</div>
Documents shall not be more than two months old.<br>
(i)Telephone Bill (ii)Bank A/c Statement (iii)Electricity Bill<br>


<h3 class="leftalign-class"> General Terms And Conditions<br></h3>
1.  Application form must be completed in full BLOCK letters in ENGLISH/HINDI. Application which is incomplete is liable to be rejected.<br>
2.  Application for membership fee should be submitted with Rs100.00/-<br>
3.  This application form is for own use only and only applicable to members.<br>
4.  The company may at any time alter, vary, add to or delete from these terms and conditions on account of <br>
    government policy as applicable from time to time or otherwise by notifying of company's notice board or by <br>
    publication on the newspapers.<br>
5.  The company reserves the right to reject any application without showing any reason.<br>
6.  Disputes if any arising in connection to the deposit scheme will be subjected to the jurisdiction of Allahabad Court.<br>
7.  In case of non-payment of the depositor part thereof as per the terms and conditions of such deposit, the <br>
    depositor may approach the Registrar of companies Uttar Pradesh and Uttarakhand.<br>
8.  In case of any deficiency of Nidhi is servicing its depositors, the depositor may approach the National Consumers<br>
    Disputes Redressal Forum, the State Consumers Disputes Redressal Forum or District Consumers Disputes <br>
    Redressal Forum, as the case may be, for redressal of this relief.<br>
9.  The Financial poistion of Nidhi as disclosed and the representations made in the application form are true and <br>
    correct and that Nidhi has complied with all the applicable rules.<br>
10. A statement to the effect that the Central Governement does not undertake any responsibility for the financial<br>
    soundness of Nidhi or for the correctness of any of the statement or the representatiuons made or opinions <br>
    expressed by Nidhi.
11. The deposits accepted by Nidhi are not insured and the repayment of deposit is not guaranteed by either the <br>
    Central Government or the Reserve Bank of India.
    
<h3 class="leftalign-class">DECLARATION / VERIFICATION<br></h3>     
1.  I hereby declare that based on the facts represented here in by myself to Triveni Nidhi Limited for my<br>
    appointment as member is true as to my based knowledge and<br> hereby abide by the following rules and<br>
    regulations of the company.<br>
2.  I hereby declare that the declarations made by me are correct and have been explained everything related to the<br>
    above account in the language known to me also I agree to abide by the rules and regulations of the company and I <br>
    shall never request anything against the terms, tenure and conditions of the scheme in letter and spirit. I also certify<br>
    that all the information particulars given by me are true to the best of my knowledge and belief.<br>
3.  I have read and understood the financial and other particulars furnished and representations made by Nidhi in this<br>
    application form and after careful consideration I am making the deposit with Nidhi at my own risk and violation.<br>  
<br><br>
   <div id="wrapper">
    <div id="box7" class="rightbox"><s:file name="memberBean.memSign" id="fileinput2" onChange="previewFile2(this)" required="required"/><img id="previewSign2" src="#" alt="Placeholder" /><br>Acceptance by Member</div>
    <div id="box8" class="leftbox"><s:textfield name="memberBean.grntrDet" required="required"/><br>Guarantor Detail</div>
    <div id="box9" class="rightbox"><s:file name="memberBean.empSign" id="fileinput3" onChange="previewFile3(this)" required="required"/><img id="previewSign3" src="#" alt="Placeholder" /><br>Authorized Employee/Officer</div>
    <div id="box10" class="centerbox"><s:file name="memberBean.grntrSign" id="fileinput4" onChange="previewFile4(this)" required="required"/><img id="previewSign4" src="#" alt="Placeholder" /><br>Guarantor Signature</div>
	</div>
<br><br>
<s:submit value="Add Member" align="center"  />
<br><br>
</s:form>

<div class="page-break"><div><span>page break</span></div></div> 

<h2>Members</h2>
<div class="zui-wrapper">
    <div class="zui-scroller">
        <table class="zui-table">
            <thead>
                <tr>	
                	<th class="zui-sticky-col">Membership No</th>
					<th>Membership Dt</th>
					<th>Applicant Name</th>
					<th>D.O.B.</th>
					<th>Sex</th>
					<th>Father's/Husband's Name</th>
					<th>Mother's Name</th>
					<th>Occupation</th>
					<th>Present Address</th>
					<th>City</th>
					<th>State</th>
					<th>Pin</th>
					<th>Applicant PAN</th>
					<th>Applicant Mobile</th>
					<th>Nominee Name</th>
					<th>Nominee Age</th>
					<th>Reln wth Nominee</th>
					<th>Nominee Address</th>
					<th>Member Fee(Fig)</th>
					<th>Membership Fee(words)</th>
					<th>Member image</th>
					<th>Docs Given</th>
					<th>Authorized Signatory</th>
					<th>Member Sign</th>
					<th>Guarantor Detail</th>
					<th>Guarantor Sign</th>
					<th>Employee Sign</th>
					<th>Delete</th>
					</tr>
            </thead>
            <tbody>
            <s:iterator value="memberList" var="member">
				<tr>
					<td class="zui-sticky-col"><s:property value="memNum"/></td>
					<td><s:date name="memDate" format="dd/MM/yyyy" /></td>
					<td><s:property value="applicantName"/></td>
					<td><s:date name="applicantDob" format="dd/MM/yyyy" /></td>
					<td><s:property value="applicantSex"/></td>
					<td><s:property value="applicantFather"/></td>
					<td><s:property value="applicantMother"/></td>
					<td> <s:property value="applicantOccupation"/> </td>
					<td><s:property value="applicantAddress"/></td>
					<td><s:property value="applicantCity"/></td>
					<td><s:property value="applicantState"/></td>
					<td><s:number name="applicantPin" /></td>
					<td><s:property value="applicantPan"/></td>
					<td><s:property value="applicantMobile"/></td>
					<td><s:property value="nomineeName"/></td>
					<td><s:number name="nomineeAge" /> </td>
					<td><s:property value="relnWithNominee"/></td>
					<td><s:property value="nomineeAddress"/></td>
					<td><s:property value="memFeeFig"/></td>
					<td><s:property value="memFeeWords"/></td>
					<td><s:property value="memImage" /></td>  
					<td><s:property value="documents"/></td>
					<td><s:property value="authSigntry"/></td>
					<td><s:property value="memSign"/></td>
					<td><s:property value="grntrDet"/></td>
					<td><s:property value="grntrSign"/></td>
					<td><s:property value="empSign"/></td>
					<td><a href="memDelete?memNum=<s:property value="memNum"/>">Delete</a></td>
				</tr>	
			</s:iterator>
		</tbody>
	</table>
</div>
</div>


