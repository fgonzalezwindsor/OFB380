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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.util.Env;

/** Generated Model for CD_Line
 *  @author Adempiere (generated) 
 *  @version Release 3.6.0LTS - $Id$ */
public class X_CD_Line extends PO implements I_CD_Line, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20160908L;

    /** Standard Constructor */
    public X_CD_Line (Properties ctx, int CD_Line_ID, String trxName)
    {
      super (ctx, CD_Line_ID, trxName);
      /** if (CD_Line_ID == 0)
        {
			setCD_Line_ID (0);
        } */
    }

    /** Load Constructor */
    public X_CD_Line (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_CD_Line[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Amount.
		@param Amt 
		Amount
	  */
	public void setAmt (BigDecimal Amt)
	{
		set_Value (COLUMNNAME_Amt, Amt);
	}

	/** Get Amount.
		@return Amount
	  */
	public BigDecimal getAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Amt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public I_C_BankAccount getC_BankAccount() throws RuntimeException
    {
		return (I_C_BankAccount)MTable.get(getCtx(), I_C_BankAccount.Table_Name)
			.getPO(getC_BankAccount_ID(), get_TrxName());	}

	/** Set Bank Account.
		@param C_BankAccount_ID 
		Account at the Bank
	  */
	public void setC_BankAccount_ID (int C_BankAccount_ID)
	{
		if (C_BankAccount_ID < 1) 
			set_Value (COLUMNNAME_C_BankAccount_ID, null);
		else 
			set_Value (COLUMNNAME_C_BankAccount_ID, Integer.valueOf(C_BankAccount_ID));
	}

	/** Get Bank Account.
		@return Account at the Bank
	  */
	public int getC_BankAccount_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BankAccount_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_Charge getC_Charge() throws RuntimeException
    {
		return (I_C_Charge)MTable.get(getCtx(), I_C_Charge.Table_Name)
			.getPO(getC_Charge_ID(), get_TrxName());	}

	/** Set Charge.
		@param C_Charge_ID 
		Additional document charges
	  */
	public void setC_Charge_ID (int C_Charge_ID)
	{
		if (C_Charge_ID < 1) 
			set_Value (COLUMNNAME_C_Charge_ID, null);
		else 
			set_Value (COLUMNNAME_C_Charge_ID, Integer.valueOf(C_Charge_ID));
	}

	/** Get Charge.
		@return Additional document charges
	  */
	public int getC_Charge_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Charge_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_CD_Header getCD_Header() throws RuntimeException
    {
		return (I_CD_Header)MTable.get(getCtx(), I_CD_Header.Table_Name)
			.getPO(getCD_Header_ID(), get_TrxName());	}

	/** Set CD_Header.
		@param CD_Header_ID CD_Header	  */
	public void setCD_Header_ID (int CD_Header_ID)
	{
		if (CD_Header_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_CD_Header_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_CD_Header_ID, Integer.valueOf(CD_Header_ID));
	}

	/** Get CD_Header.
		@return CD_Header	  */
	public int getCD_Header_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_CD_Header_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set CD_Line.
		@param CD_Line_ID CD_Line	  */
	public void setCD_Line_ID (int CD_Line_ID)
	{
		if (CD_Line_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_CD_Line_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_CD_Line_ID, Integer.valueOf(CD_Line_ID));
	}

	/** Get CD_Line.
		@return CD_Line	  */
	public int getCD_Line_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_CD_Line_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** Type AD_Reference_ID=1000087 */
	public static final int TYPE_AD_Reference_ID=1000087;
	/** Egreso = EG */
	public static final String TYPE_Egreso = "EG";
	/** Ingreso = IN */
	public static final String TYPE_Ingreso = "IN";
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