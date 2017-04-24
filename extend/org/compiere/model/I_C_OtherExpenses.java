/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2007 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package org.compiere.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.util.KeyNamePair;

/** Generated Interface for C_OtherExpenses
 *  @author Adempiere (generated) 
 *  @version Release 3.6.0LTS
 */
public interface I_C_OtherExpenses 
{

    /** TableName=C_OtherExpenses */
    public static final String Table_Name = "C_OtherExpenses";

    /** AD_Table_ID=1000112 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 3 - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(3);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Client.
	  * Client/Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within client
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within client
	  */
	public int getAD_Org_ID();

    /** Column name AD_User_ID */
    public static final String COLUMNNAME_AD_User_ID = "AD_User_ID";

	/** Set User/Contact.
	  * User within the system - Internal or Business Partner Contact
	  */
	public void setAD_User_ID (int AD_User_ID);

	/** Get User/Contact.
	  * User within the system - Internal or Business Partner Contact
	  */
	public int getAD_User_ID();

	public I_AD_User getAD_User() throws RuntimeException;

    /** Column name Boton1 */
    public static final String COLUMNNAME_Boton1 = "Boton1";

	/** Set Boton1	  */
	public void setBoton1 (String Boton1);

	/** Get Boton1	  */
	public String getBoton1();

    /** Column name C_Bank_ID */
    public static final String COLUMNNAME_C_Bank_ID = "C_Bank_ID";

	/** Set Bank.
	  * Bank
	  */
	public void setC_Bank_ID (int C_Bank_ID);

	/** Get Bank.
	  * Bank
	  */
	public int getC_Bank_ID();

	public I_C_Bank getC_Bank() throws RuntimeException;

    /** Column name C_BankAccount_ID */
    public static final String COLUMNNAME_C_BankAccount_ID = "C_BankAccount_ID";

	/** Set Bank Account.
	  * Account at the Bank
	  */
	public void setC_BankAccount_ID (int C_BankAccount_ID);

	/** Get Bank Account.
	  * Account at the Bank
	  */
	public int getC_BankAccount_ID();

	public I_C_BankAccount getC_BankAccount() throws RuntimeException;

    /** Column name C_BPartner_ID */
    public static final String COLUMNNAME_C_BPartner_ID = "C_BPartner_ID";

	/** Set Business Partner .
	  * Identifies a Business Partner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID);

	/** Get Business Partner .
	  * Identifies a Business Partner
	  */
	public int getC_BPartner_ID();

	public I_C_BPartner getC_BPartner() throws RuntimeException;

    /** Column name C_BPartner_Location_ID */
    public static final String COLUMNNAME_C_BPartner_Location_ID = "C_BPartner_Location_ID";

	/** Set Partner Location.
	  * Identifies the (ship to) address for this Business Partner
	  */
	public void setC_BPartner_Location_ID (int C_BPartner_Location_ID);

	/** Get Partner Location.
	  * Identifies the (ship to) address for this Business Partner
	  */
	public int getC_BPartner_Location_ID();

	public I_C_BPartner_Location getC_BPartner_Location() throws RuntimeException;

    /** Column name c_bpartner2_id */
    public static final String COLUMNNAME_c_bpartner2_id = "c_bpartner2_id";

	/** Set Business Partner 2	  */
	public void setc_bpartner2_id (int c_bpartner2_id);

	/** Get Business Partner 2	  */
	public int getc_bpartner2_id();

	public I_C_BPartner getc_bpartner2() throws RuntimeException;

    /** Column name C_OtherExpenses_ID */
    public static final String COLUMNNAME_C_OtherExpenses_ID = "C_OtherExpenses_ID";

	/** Set C_OtherExpenses	  */
	public void setC_OtherExpenses_ID (int C_OtherExpenses_ID);

	/** Get C_OtherExpenses	  */
	public int getC_OtherExpenses_ID();

    /** Column name Category */
    public static final String COLUMNNAME_Category = "Category";

	/** Set Category	  */
	public void setCategory (String Category);

	/** Get Category	  */
	public String getCategory();

    /** Column name Concept */
    public static final String COLUMNNAME_Concept = "Concept";

	/** Set Concepto	  */
	public void setConcept (String Concept);

	/** Get Concepto	  */
	public String getConcept();

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name currency_1 */
    public static final String COLUMNNAME_currency_1 = "currency_1";

	/** Set currency_1	  */
	public void setcurrency_1 (BigDecimal currency_1);

	/** Get currency_1	  */
	public BigDecimal getcurrency_1();

    /** Column name currency_10 */
    public static final String COLUMNNAME_currency_10 = "currency_10";

	/** Set currency_10	  */
	public void setcurrency_10 (BigDecimal currency_10);

	/** Get currency_10	  */
	public BigDecimal getcurrency_10();

    /** Column name currency_100 */
    public static final String COLUMNNAME_currency_100 = "currency_100";

	/** Set currency_100	  */
	public void setcurrency_100 (BigDecimal currency_100);

	/** Get currency_100	  */
	public BigDecimal getcurrency_100();

    /** Column name currency_1000 */
    public static final String COLUMNNAME_currency_1000 = "currency_1000";

	/** Set currency_1000	  */
	public void setcurrency_1000 (BigDecimal currency_1000);

	/** Get currency_1000	  */
	public BigDecimal getcurrency_1000();

    /** Column name currency_10000 */
    public static final String COLUMNNAME_currency_10000 = "currency_10000";

	/** Set currency_10000	  */
	public void setcurrency_10000 (BigDecimal currency_10000);

	/** Get currency_10000	  */
	public BigDecimal getcurrency_10000();

    /** Column name currency_2000 */
    public static final String COLUMNNAME_currency_2000 = "currency_2000";

	/** Set currency_2000	  */
	public void setcurrency_2000 (BigDecimal currency_2000);

	/** Get currency_2000	  */
	public BigDecimal getcurrency_2000();

    /** Column name currency_20000 */
    public static final String COLUMNNAME_currency_20000 = "currency_20000";

	/** Set currency_20000	  */
	public void setcurrency_20000 (BigDecimal currency_20000);

	/** Get currency_20000	  */
	public BigDecimal getcurrency_20000();

    /** Column name currency_5 */
    public static final String COLUMNNAME_currency_5 = "currency_5";

	/** Set currency_5	  */
	public void setcurrency_5 (BigDecimal currency_5);

	/** Get currency_5	  */
	public BigDecimal getcurrency_5();

    /** Column name currency_50 */
    public static final String COLUMNNAME_currency_50 = "currency_50";

	/** Set currency_50	  */
	public void setcurrency_50 (BigDecimal currency_50);

	/** Get currency_50	  */
	public BigDecimal getcurrency_50();

    /** Column name currency_500 */
    public static final String COLUMNNAME_currency_500 = "currency_500";

	/** Set currency_500	  */
	public void setcurrency_500 (BigDecimal currency_500);

	/** Get currency_500	  */
	public BigDecimal getcurrency_500();

    /** Column name currency_5000 */
    public static final String COLUMNNAME_currency_5000 = "currency_5000";

	/** Set currency_5000	  */
	public void setcurrency_5000 (BigDecimal currency_5000);

	/** Get currency_5000	  */
	public BigDecimal getcurrency_5000();

    /** Column name DatePromised */
    public static final String COLUMNNAME_DatePromised = "DatePromised";

	/** Set Date Promised.
	  * Date Order was promised
	  */
	public void setDatePromised (Timestamp DatePromised);

	/** Get Date Promised.
	  * Date Order was promised
	  */
	public Timestamp getDatePromised();

    /** Column name Description */
    public static final String COLUMNNAME_Description = "Description";

	/** Set Description.
	  * Optional short description of the record
	  */
	public void setDescription (String Description);

	/** Get Description.
	  * Optional short description of the record
	  */
	public String getDescription();

    /** Column name DocAction */
    public static final String COLUMNNAME_DocAction = "DocAction";

	/** Set Document Action.
	  * The targeted status of the document
	  */
	public void setDocAction (String DocAction);

	/** Get Document Action.
	  * The targeted status of the document
	  */
	public String getDocAction();

    /** Column name DocStatus */
    public static final String COLUMNNAME_DocStatus = "DocStatus";

	/** Set Document Status.
	  * The current status of the document
	  */
	public void setDocStatus (String DocStatus);

	/** Get Document Status.
	  * The current status of the document
	  */
	public String getDocStatus();

    /** Column name DocumentDate */
    public static final String COLUMNNAME_DocumentDate = "DocumentDate";

	/** Set DocumentDate	  */
	public void setDocumentDate (Timestamp DocumentDate);

	/** Get DocumentDate	  */
	public Timestamp getDocumentDate();

    /** Column name DocumentNo */
    public static final String COLUMNNAME_DocumentNo = "DocumentNo";

	/** Set Document No.
	  * Document sequence number of the document
	  */
	public void setDocumentNo (String DocumentNo);

	/** Get Document No.
	  * Document sequence number of the document
	  */
	public String getDocumentNo();

    /** Column name Expense */
    public static final String COLUMNNAME_Expense = "Expense";

	/** Set Expense	  */
	public void setExpense (String Expense);

	/** Get Expense	  */
	public String getExpense();

    /** Column name GrandTotal */
    public static final String COLUMNNAME_GrandTotal = "GrandTotal";

	/** Set Grand Total.
	  * Total amount of document
	  */
	public void setGrandTotal (BigDecimal GrandTotal);

	/** Get Grand Total.
	  * Total amount of document
	  */
	public BigDecimal getGrandTotal();

    /** Column name horas */
    public static final String COLUMNNAME_horas = "horas";

	/** Set horas	  */
	public void sethoras (Timestamp horas);

	/** Get horas	  */
	public Timestamp gethoras();

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive (boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

    /** Column name Override */
    public static final String COLUMNNAME_Override = "Override";

	/** Set Override	  */
	public void setOverride (boolean Override);

	/** Get Override	  */
	public boolean isOverride();

    /** Column name Processed */
    public static final String COLUMNNAME_Processed = "Processed";

	/** Set Processed.
	  * The document has been processed
	  */
	public void setProcessed (boolean Processed);

	/** Get Processed.
	  * The document has been processed
	  */
	public boolean isProcessed();

    /** Column name TenderType */
    public static final String COLUMNNAME_TenderType = "TenderType";

	/** Set Tender type.
	  * Method of Payment
	  */
	public void setTenderType (String TenderType);

	/** Get Tender type.
	  * Method of Payment
	  */
	public String getTenderType();

    /** Column name TotalLines */
    public static final String COLUMNNAME_TotalLines = "TotalLines";

	/** Set Total Lines.
	  * Total of all document lines
	  */
	public void setTotalLines (BigDecimal TotalLines);

	/** Get Total Lines.
	  * Total of all document lines
	  */
	public BigDecimal getTotalLines();

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/** Get Updated By.
	  * User who updated this records
	  */
	public int getUpdatedBy();
}
