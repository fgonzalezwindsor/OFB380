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
/** Generated Model - DO NOT CHANGE */
package org.compiere.model;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

/** Generated Model for TP_Refund
 *  @author Adempiere (generated) 
 *  @version Release 3.6.0LTS - $Id$ */
public class X_TP_Refund extends PO implements I_TP_Refund, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20160824L;

    /** Standard Constructor */
    public X_TP_Refund (Properties ctx, int TP_Refund_ID, String trxName)
    {
      super (ctx, TP_Refund_ID, trxName);
      /** if (TP_Refund_ID == 0)
        {
			setTP_Refund_ID (0);
        } */
    }

    /** Load Constructor */
    public X_TP_Refund (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuffer sb = new StringBuffer ("X_TP_Refund[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Boton1 AD_Reference_ID=135 */
	public static final int BOTON1_AD_Reference_ID=135;
	/** Complete = CO */
	public static final String BOTON1_Complete = "CO";
	/** Approve = AP */
	public static final String BOTON1_Approve = "AP";
	/** Reject = RJ */
	public static final String BOTON1_Reject = "RJ";
	/** Post = PO */
	public static final String BOTON1_Post = "PO";
	/** Void = VO */
	public static final String BOTON1_Void = "VO";
	/** Close = CL */
	public static final String BOTON1_Close = "CL";
	/** Reverse - Correct = RC */
	public static final String BOTON1_Reverse_Correct = "RC";
	/** Reverse - Accrual = RA */
	public static final String BOTON1_Reverse_Accrual = "RA";
	/** Invalidate = IN */
	public static final String BOTON1_Invalidate = "IN";
	/** Re-activate = RE */
	public static final String BOTON1_Re_Activate = "RE";
	/** <None> = -- */
	public static final String BOTON1_None = "--";
	/** Prepare = PR */
	public static final String BOTON1_Prepare = "PR";
	/** Unlock = XL */
	public static final String BOTON1_Unlock = "XL";
	/** Wait Complete = WC */
	public static final String BOTON1_WaitComplete = "WC";
	/** Set Boton1.
		@param Boton1 Boton1	  */
	public void setBoton1 (String Boton1)
	{

		set_Value (COLUMNNAME_Boton1, Boton1);
	}

	/** Get Boton1.
		@return Boton1	  */
	public String getBoton1 () 
	{
		return (String)get_Value(COLUMNNAME_Boton1);
	}

	public I_C_BPartner getC_BPartner() throws RuntimeException
    {
		return (I_C_BPartner)MTable.get(getCtx(), I_C_BPartner.Table_Name)
			.getPO(getC_BPartner_ID(), get_TrxName());	}

	/** Set Business Partner .
		@param C_BPartner_ID 
		Identifies a Business Partner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID)
	{
		if (C_BPartner_ID < 1) 
			set_Value (COLUMNNAME_C_BPartner_ID, null);
		else 
			set_Value (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
	}

	/** Get Business Partner .
		@return Identifies a Business Partner
	  */
	public int getC_BPartner_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ProjectOFB getC_ProjectOFB() throws RuntimeException
    {
		return (I_C_ProjectOFB)MTable.get(getCtx(), I_C_ProjectOFB.Table_Name)
			.getPO(getC_ProjectOFB_ID(), get_TrxName());	}

	/** Set C_ProjectOFB_ID.
		@param C_ProjectOFB_ID C_ProjectOFB_ID	  */
	public void setC_ProjectOFB_ID (int C_ProjectOFB_ID)
	{
		if (C_ProjectOFB_ID < 1) 
			set_Value (COLUMNNAME_C_ProjectOFB_ID, null);
		else 
			set_Value (COLUMNNAME_C_ProjectOFB_ID, Integer.valueOf(C_ProjectOFB_ID));
	}

	/** Get C_ProjectOFB_ID.
		@return C_ProjectOFB_ID	  */
	public int getC_ProjectOFB_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_ProjectOFB_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ProjectOFB getC_ProjectOFB2() throws RuntimeException
    {
		return (I_C_ProjectOFB)MTable.get(getCtx(), I_C_ProjectOFB.Table_Name)
			.getPO(getC_ProjectOFB2_ID(), get_TrxName());	}

	/** Set C_ProjectOFB2_ID.
		@param C_ProjectOFB2_ID C_ProjectOFB2_ID	  */
	public void setC_ProjectOFB2_ID (int C_ProjectOFB2_ID)
	{
		if (C_ProjectOFB2_ID < 1) 
			set_Value (COLUMNNAME_C_ProjectOFB2_ID, null);
		else 
			set_Value (COLUMNNAME_C_ProjectOFB2_ID, Integer.valueOf(C_ProjectOFB2_ID));
	}

	/** Get C_ProjectOFB2_ID.
		@return C_ProjectOFB2_ID	  */
	public int getC_ProjectOFB2_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_ProjectOFB2_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Comments.
		@param Comments 
		Comments or additional information
	  */
	public void setComments (String Comments)
	{
		set_Value (COLUMNNAME_Comments, Comments);
	}

	/** Get Comments.
		@return Comments or additional information
	  */
	public String getComments () 
	{
		return (String)get_Value(COLUMNNAME_Comments);
	}

	/** Set Document Date.
		@param DateDoc 
		Date of the Document
	  */
	public void setDateDoc (Timestamp DateDoc)
	{
		set_Value (COLUMNNAME_DateDoc, DateDoc);
	}

	/** Get Document Date.
		@return Date of the Document
	  */
	public Timestamp getDateDoc () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateDoc);
	}

	/** Set Date Promised.
		@param DatePromised 
		Date Order was promised
	  */
	public void setDatePromised (Timestamp DatePromised)
	{
		set_Value (COLUMNNAME_DatePromised, DatePromised);
	}

	/** Get Date Promised.
		@return Date Order was promised
	  */
	public Timestamp getDatePromised () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DatePromised);
	}

	/** Set Description.
		@param Description 
		Optional short description of the record
	  */
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription () 
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** DocStatus AD_Reference_ID=1000095 */
	public static final int DOCSTATUS_AD_Reference_ID=1000095;
	/** Borrador = DR */
	public static final String DOCSTATUS_Borrador = "DR";
	/** Completo = CO */
	public static final String DOCSTATUS_Completo = "CO";
	/** Anulado = VO */
	public static final String DOCSTATUS_Anulado = "VO";
	/** Set Document Status.
		@param DocStatus 
		The current status of the document
	  */
	public void setDocStatus (String DocStatus)
	{

		set_Value (COLUMNNAME_DocStatus, DocStatus);
	}

	/** Get Document Status.
		@return The current status of the document
	  */
	public String getDocStatus () 
	{
		return (String)get_Value(COLUMNNAME_DocStatus);
	}

	/** Set Document No.
		@param DocumentNo 
		Document sequence number of the document
	  */
	public void setDocumentNo (String DocumentNo)
	{
		set_Value (COLUMNNAME_DocumentNo, DocumentNo);
	}

	/** Get Document No.
		@return Document sequence number of the document
	  */
	public String getDocumentNo () 
	{
		return (String)get_Value(COLUMNNAME_DocumentNo);
	}

	/** Set Processed.
		@param Processed 
		The document has been processed
	  */
	public void setProcessed (boolean Processed)
	{
		set_Value (COLUMNNAME_Processed, Boolean.valueOf(Processed));
	}

	/** Get Processed.
		@return The document has been processed
	  */
	public boolean isProcessed () 
	{
		Object oo = get_Value(COLUMNNAME_Processed);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set TP_Refund.
		@param TP_Refund_ID TP_Refund	  */
	public void setTP_Refund_ID (int TP_Refund_ID)
	{
		if (TP_Refund_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_TP_Refund_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_TP_Refund_ID, Integer.valueOf(TP_Refund_ID));
	}

	/** Get TP_Refund.
		@return TP_Refund	  */
	public int getTP_Refund_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TP_Refund_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Type AD_Reference_ID=1000092 */
	public static final int TYPE_AD_Reference_ID=1000092;
	/** Normal = 01 */
	public static final String TYPE_Normal = "01";
	/** Especial = 02 */
	public static final String TYPE_Especial = "02";
	/** Set Type.
		@param Type 
		Type of Validation (SQL, Java Script, Java Language)
	  */
	public void setType (String Type)
	{

		set_Value (COLUMNNAME_Type, Type);
	}

	/** Get Type.
		@return Type of Validation (SQL, Java Script, Java Language)
	  */
	public String getType () 
	{
		return (String)get_Value(COLUMNNAME_Type);
	}
}