package com.struts2.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.struts2.action.CustPmtManagerDao.UploadManagerDAO;
import com.struts2.model.CustLoanBean;
import com.struts2.model.CustPmtBean;
import com.struts2.model.Installment;
import com.struts2.model.Member;

public class CustPmtManager implements UploadManagerDAO{
	@Override
	public Integer saveUploadFileDetails(CustPmtBean payment){
		Session session = HibernateUtil.getHibernateSession();
		Transaction tx = session.beginTransaction();
		session.save(payment);
		tx.commit();
	    session.close();  
	    return null;
	}
	

	@Override
	public List<CustPmtBean> getPaymentList(){
		List<CustPmtBean> paymentList = new ArrayList<CustPmtBean>();
		Session session = HibernateUtil.getHibernateSession();
		try {
			paymentList = session.createQuery("from CustPmtBean").list();
	      } catch(Exception e) {
	         e.printStackTrace();
	      }
	      return paymentList;
		
	}
	
	public List<Installment> getInstallmentList(String loanAccno){
		System.out.println("loanAccnooooooooooooooooooooooooooooooooooooooooooo"+loanAccno);
		List<Installment> installmentList = new ArrayList<Installment>();
		Session session = HibernateUtil.getHibernateSession();
		try {
			String hql = "from Installment i where i.custLoanDet.loanAccno =:loanAccno";
			Query query = session.createQuery(hql);
			query.setString("loanAccno", loanAccno);
			installmentList = (List<Installment>)query.list();
			System.out.println("installlistSizeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee "+installmentList.size());
	      } catch(Exception e) {
	         e.printStackTrace();
	      }
		return installmentList;
	}
	
	public List<Installment> getInstallmentList(){
		List<Installment> installList = new ArrayList<Installment>();
		Session session = HibernateUtil.getHibernateSession();
		try {
			installList = session.createQuery("from Installment").list();
	      } catch(Exception e) {
	         e.printStackTrace();
	      }
	      return installList;
		
	}
	
	public void saveIndivInstlmnt(Installment installment, CustPmtBean custPmtDet){
		try{
		Session session = HibernateUtil.getHibernateSession();
		Transaction tx = session.beginTransaction();
    	String hql = "SELECT u.creditPmtGvnByCust, u.debitTotLoanWithInterest, max(u.creditPmtGvnByCust)  from CustPmtBean u where u.custLoanDet.loanAccno = :loanAccno group by u.creditPmtGvnByCust, u.debitTotLoanWithInterest";
    	Query query = session.createQuery(hql);
		query.setParameter("loanAccno", custPmtDet.getCustLoanDet().getLoanAccno());
		List<CustPmtBean> loanPmtList = new ArrayList<CustPmtBean>();
		loanPmtList = (List<CustPmtBean>) query.getResultList();
		System.out.println("loanDetList.size()db:"+loanPmtList.size());
		Iterator iterator = loanPmtList.iterator();
		Object[] results=null;
		if (iterator.hasNext()){
			results = (Object[]) iterator.next();
			System.out.println("results.length"+results.length);
			double creditPmtGvnByCust = Double.parseDouble(results[0].toString())+installment.getInstallmentAmt();
			custPmtDet.setCreditPmtGvnByCust(creditPmtGvnByCust);
			double debitTotLoanWithInterest=Double.parseDouble(results[1].toString());
			double totalMonthlyPmt=installment.getInstallmentAmt();
			custPmtDet.setTotalMonthlyPmt(totalMonthlyPmt);
			if(debitTotLoanWithInterest > creditPmtGvnByCust){
				custPmtDet.setLoanStatus("unpaid");
			}else if(debitTotLoanWithInterest == creditPmtGvnByCust){
				custPmtDet.setLoanStatus("paid");
			}else{
				throw new CreditExceedsDebitException("Please check customer payments. Credit exceeds debit");
			}
			System.out.println("creditPmtGvnByCust"+results[2]);
		}else{
			double creditPmtGvnByCust = installment.getInstallmentAmt();
			custPmtDet.setCreditPmtGvnByCust(creditPmtGvnByCust);
			double debitTotLoanWithInterest=custPmtDet.getDebitTotLoanWithInterest();
			Double totalMonthlyPmt=installment.getInstallmentAmt();
			custPmtDet.setTotalMonthlyPmt(totalMonthlyPmt);
			if(debitTotLoanWithInterest > creditPmtGvnByCust){
				custPmtDet.setLoanStatus("unpaid");
			}else if(debitTotLoanWithInterest == creditPmtGvnByCust){
				custPmtDet.setLoanStatus("paid");
			}else{
				throw new CreditExceedsDebitException("Please check customer payments. Credit exceeds debit");
			}
		}
		installment.setCustLoanDet(custPmtDet.getCustLoanDet());
		session.save(custPmtDet);
		session.save(installment);
		tx.commit();
	    session.close();  
		}catch(CreditExceedsDebitException cede){
			cede.printStackTrace();
		}
		}
	
	public void deleteCustInstall(int installId, int pmtId, double installmentAmt){
		Session session = HibernateUtil.getHibernateSession();
		Transaction tx = session.beginTransaction();
		System.out.println("pmtId"+pmtId);
		String hql = "SELECT u.creditPmtGvnByCust, u.totalMonthlyPmt  from CustPmtBean u where u.pmtId = :pmtId";
    	Query query = session.createQuery(hql);
		query.setParameter("pmtId", pmtId);
		List<CustPmtBean> loanPmtList = new ArrayList<CustPmtBean>();
		loanPmtList = (List<CustPmtBean>) query.getResultList();
		//loanPmtList.get(0).getCreditPmtGvnByCust();
		System.out.println("loanPmtList.size()"+loanPmtList.size());
		Iterator iterator = loanPmtList.iterator();
		Object[] results=null;
		if (iterator.hasNext()){
			results= (Object[])(iterator.next());
			//System.out.println("creditPmtGvnByCust"+creditPmtGvnByCust);
			double creditPmtGvnByCust= Double.valueOf(results[0].toString()).doubleValue();
			double totalMonthlyPmt= Double.valueOf(results[1].toString()).doubleValue();
			double newCreditAmount = creditPmtGvnByCust-installmentAmt;
			double newTotalMonthlyPmt = totalMonthlyPmt - installmentAmt;
			System.out.println("newCreditAmount"+newCreditAmount);
			System.out.println("newTotalMonthlyPmt"+newTotalMonthlyPmt);
			if(newCreditAmount <=0){
				CustPmtBean custPmtDet = session.load(CustPmtBean.class, pmtId);
						if(null != custPmtDet){
							session.delete(custPmtDet);
						}
			}else{
			String updateHql = "Update CustPmtBean u set u.creditPmtGvnByCust= :newCreditAmount, u.totalMonthlyPmt= :newTotalMonthlyPmt where u.pmtId = :pmtId";
			Query updateQuery = session.createQuery(updateHql);
			updateQuery.setParameter("newCreditAmount", newCreditAmount);
			updateQuery.setParameter("newTotalMonthlyPmt", newTotalMonthlyPmt);
			updateQuery.setParameter("pmtId", pmtId);
			int res = updateQuery.executeUpdate();
			}
		}
		Installment installment = session.load(Installment.class, installId);
		if(null != installment){
			System.out.println("installment is not null");
			session.delete(installment);
		}
		tx.commit();
		session.close();
		System.out.println("delete successful");
		}
	
	public void excelFileUpload(String excelFilePath, File fileUpload){
				
		FileInputStream inputStream = null;
		XSSFWorkbook xssfworkbook = null;
    	/*steps for uploading a file*/
		
		System.out.println("excelFilePath"+excelFilePath);
    	File fileToCreate = new File(excelFilePath);
        try {
            FileUtils.copyFile(fileUpload, fileToCreate);
        } catch (IOException ex) {
            System.out.println("Couldn't save file: " + ex.getMessage());
        }
        /*steps for uploading payment  data into the database*/
        
        try {
        	List<Installment> installmentList = new ArrayList<Installment>();
            inputStream = new FileInputStream(excelFilePath);
            xssfworkbook = new XSSFWorkbook(inputStream);
            XSSFSheet xssfSheet = xssfworkbook.getSheetAt(0);
            Map<Integer, Date> map = new LinkedHashMap<Integer, Date>();
            XSSFSheet xssfsheet = xssfworkbook.getSheetAt(0);
            XSSFRow row = xssfsheet.getRow(0);
            int colNum = row.getLastCellNum();
            int totcolZeroBased = colNum-1;
            Double monthlyPayment = 0.0;
            System.out.println("Total Number of Columns in the excel is : "+colNum);
            int rowNum = xssfsheet.getLastRowNum()+1;
            System.out.println("Total Number of Rows in the excel is : "+rowNum);
            Iterator<Row> rowIterator = xssfSheet.iterator();
            Row firstRow = xssfsheet.getRow(0);
            Iterator<Cell> cellIterator = firstRow.cellIterator();
            while(cellIterator.hasNext()){
            	Cell nextCell = cellIterator.next();
            	int columnIndex = nextCell.getColumnIndex();
            	if (columnIndex == 0) {
            		System.out.println("columnIndex"+columnIndex);
            		continue;
				} else if (columnIndex == 1) {
					System.out.println("columnIndex"+columnIndex);
					continue;
				} else if (columnIndex == 2) {
					System.out.println("columnIndex"+columnIndex);
					continue;
				} else if (columnIndex == 3) {
					System.out.println("columnIndex"+columnIndex);
					continue;
				} else if (columnIndex == 4) {
					System.out.println("columnIndex"+columnIndex);
					continue;
				} else if (columnIndex == 5) {
					System.out.println("columnIndex"+columnIndex);
					continue;
				} else if (columnIndex == 6) {
					System.out.println("columnIndex"+columnIndex);
					continue;
				} else if (columnIndex == totcolZeroBased) {
					System.out.println("columnIndex"+columnIndex);
					continue;
				} else {
				String paymentDate = nextCell.getStringCellValue();
				DateTimeFormatter formatter_1 = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			    LocalDate local_paymentDate = LocalDate.parse(paymentDate, formatter_1);
			    Date paymentDate_formatted = new LocalDateAttributeConverter().convertToDatabaseColumn(local_paymentDate);
			    System.out.println("paymentDate_formatted "+paymentDate_formatted);
			    //installmentDateList.add(convertedDate);
			    map.put(columnIndex, paymentDate_formatted);
			    System.out.println("map.size"+map.size());
				}
            }
            while (rowIterator.hasNext()) {
            	Row nextRow = rowIterator.next();
            	if(nextRow.getRowNum()==0){
            	       continue; //just skip the rows if row number is 0 
            	}
                Iterator<Cell> cellIterator1 = nextRow.cellIterator();
            	CustPmtBean custPmtDet  = new CustPmtBean();
            	
            	String loanAccNo = null;
            	double debitTotLoanWithInterest=0;
            	double totPmtRcvdPreviously=0;
            	String loanStatus=null;
                while (cellIterator1.hasNext()) {
                	Cell nextCell = cellIterator1.next();
                    int columnIndexNew = nextCell.getColumnIndex();
                    if (columnIndexNew == 0) {
						String weeklyDue = nextCell.getStringCellValue();
						System.out.println("columnIndexNewwwwwwwwwwwwwwwwwwwwwwwwwww "+columnIndexNew);
						System.out.println("weeklyDueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"+weeklyDue);
						custPmtDet.setWeeklyDue(weeklyDue);
					} else if (columnIndexNew == 1) {
						nextCell.setCellType(Cell.CELL_TYPE_STRING);
						String membershipNumber = nextCell.getStringCellValue();
						System.out.println("columnIndexNewwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww "+columnIndexNew);
						System.out.println("membershipNumberrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr"+membershipNumber);
						Member member = new Member();
						member.setMemNum(membershipNumber);
						custPmtDet.setMember(member);
					} else if (columnIndexNew == 2) {
						String applicantName = nextCell.getStringCellValue();
						System.out.println("columnIndexNewwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww "+columnIndexNew);
						System.out.println("memberNameeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"+applicantName);
						custPmtDet.setLoanMemName(applicantName);
					} else if (columnIndexNew == 3) {
						debitTotLoanWithInterest = (double)nextCell.getNumericCellValue();
						System.out.println("columnIndexNewwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww"+columnIndexNew);
						System.out.println("loanAmountgivennnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"+debitTotLoanWithInterest);
						custPmtDet.setDebitTotLoanWithInterest(debitTotLoanWithInterest);
					} else if (columnIndexNew == 4) {
						totPmtRcvdPreviously = (double)nextCell.getNumericCellValue();
						System.out.println("columnIndexNewwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww"+columnIndexNew);
						System.out.println("totAmountReceivedddddddddddddddddddddddddddddddd"+totPmtRcvdPreviously);
						custPmtDet.setCreditPmtGvnByCust(totPmtRcvdPreviously);
					} else if (columnIndexNew == 5) {
						String dateOfLoan = nextCell.getStringCellValue();
						System.out.println("columnIndexNewwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww"+columnIndexNew);
						DateTimeFormatter formatter_2 = DateTimeFormatter.ofPattern("dd-MM-yyyy");
					    LocalDate local_date_4 = LocalDate.parse(dateOfLoan, formatter_2);
					    Date dateOfLoan_formatted = new LocalDateAttributeConverter().convertToDatabaseColumn(local_date_4);
					    System.out.println("dateOfLoan_formattedddddddddddddddddddddddddddd"+dateOfLoan_formatted);
					    custPmtDet.setDateOfLoan(dateOfLoan_formatted); 
					} else if (columnIndexNew == 6) {
						loanAccNo = nextCell.getStringCellValue();
						System.out.println("columnIndexNewwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww"+columnIndexNew);
						System.out.println("loanAccountNumberrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr"+loanAccNo);
					} else if (columnIndexNew == totcolZeroBased) {
						System.out.println("columnIndexNew"+columnIndexNew);
						/* do nothing as last column is a derived column*/continue;
					} else {
						CustLoanBean custLoanDet = new CustLoanBean();
						custLoanDet.setLoanAccno(loanAccNo);
						custPmtDet.setCustLoanDet(custLoanDet);
						Installment installment = new Installment();
						int instlAmtRcvd = (int)nextCell.getNumericCellValue();
						monthlyPayment +=instlAmtRcvd;
						System.out.println("columnIndexNewwwwwwwwwwwwwwwwwwwwwwwwww"+columnIndexNew);
						System.out.println("instlAmtRcvdddddddddddddddddddddddddddd"+instlAmtRcvd);
						installment.setInstallmentAmt(instlAmtRcvd);
						Date installmentDate = map.get(columnIndexNew);
						System.out.println("installmentDateeeeeeeeeeeeeeeeeeeeeeeee"+installmentDate);
						installment.setInstallmentDt(installmentDate);
						installment.setCustLoanDet(custLoanDet);
						installmentList.add(installment);
						
					}         
                }
                custPmtDet.setInstallmentList(installmentList);
                custPmtDet.setMonthStartDate(map.get(7));
                int size = map.size();
                int value = size+7;
                int second_last_value= size+6;
                for(int  i=7; i<value; i++){
                System.out.println("map.valueeeeeeeeeeeeeee "+map.get(i));}
				/*steps for getting previous customer payment reference*/
				Session session = HibernateUtil.getHibernateSession();
				Transaction tx = session.beginTransaction();
		    	String hql = "SELECT u.creditPmtGvnByCust, max(u.creditPmtGvnByCust)  from CustPmtBean u where u.custLoanDet.loanAccno = :loanAccno group by u.creditPmtGvnByCust";
		    	Query query = session.createQuery(hql);
				query.setParameter("loanAccno", loanAccNo);
				List<CustPmtBean> loanPmtList = new ArrayList<CustPmtBean>();
				loanPmtList = (List<CustPmtBean>) query.getResultList();
				System.out.println("loanDetList.size()db:"+loanPmtList.size());
				Iterator iterator = loanPmtList.iterator();
				Object[] results=null;
				if (iterator.hasNext()){
					results = (Object[]) iterator.next();
					System.out.println("results.length"+results.length);
					double creditPmtGvnByCust = Double.parseDouble(results[0].toString())+monthlyPayment;
					custPmtDet.setCreditPmtGvnByCust(creditPmtGvnByCust);
					if(debitTotLoanWithInterest > creditPmtGvnByCust){
						custPmtDet.setLoanStatus("unpaid");
					}else if(debitTotLoanWithInterest == creditPmtGvnByCust){
						custPmtDet.setLoanStatus("paid");
					}else{
						throw new CreditExceedsDebitException("Please check customer payments. Credit exceeds debit");
					}
				}else{
					double creditPmtGvnByCust = monthlyPayment;
					custPmtDet.setCreditPmtGvnByCust(creditPmtGvnByCust);
					if(debitTotLoanWithInterest > creditPmtGvnByCust){
						custPmtDet.setLoanStatus("unpaid");
					}else if(debitTotLoanWithInterest == creditPmtGvnByCust){
						custPmtDet.setLoanStatus("paid");
					}else{
						throw new CreditExceedsDebitException("Please check customer payments. Credit exceeds debit");
					}
				}
				/*steps for getting previous amount reference */
                custPmtDet.setMonthEndDate(map.get(second_last_value));
                custPmtDet.setTotalMonthlyPmt(monthlyPayment);
                CustPmtManager uploadManager = new CustPmtManager();
            	uploadManager.saveUploadFileDetails(custPmtDet);
            	installmentList.clear();
            }

        }catch (IOException ex1) {
            System.out.println("Error reading file");
            ex1.printStackTrace();
        } catch (Exception e){
        	e.printStackTrace();
        }finally{
        	try{
        	 inputStream.close();
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        }
       

	}
		
}


