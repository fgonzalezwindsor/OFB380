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

/** Generated Model for T_BudgetDistributionPay
 *  @author Adempiere (generated) 
 *  @version Release 3.6.0LTS - $Id$ */
public class X_T_BudgetDistributionPay extends PO implements I_T_BudgetDistributionPay, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20140627L;

    /** Standard Constructor */
    public X_T_BudgetDistributionPay (Properties ctx, int T_BudgetDistributionPay_ID, String trxName)
    {
      super (ctx, T_BudgetDistributionPay_ID, trxName);
      /** if (T_BudgetDistributionPay_ID == 0)
        {
			setC_Payment_ID (0);
			setT_BudgetDistributionPay_ID (0);
        } */
    }

    /** Load Constructor */
    public X_T_BudgetDistributionPay (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_T_BudgetDistributionPay[")
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

	public I_C_Payment getC_Payment() throws RuntimeException
    {
		return (I_C_Payment)MTable.get(getCtx(), I_C_Payment.Table_Name)
			.getPO(getC_Payment_ID(), get_TrxName());	}

	/** Set Payment.
		@param C_Payment_ID 
		Payment identifier
	  */
	public void setC_Payment_ID (int C_Payment_ID)
	{
		if (C_Payment_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_Payment_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Payment_ID, Integer.valueOf(C_Payment_ID));
	}

	/** Get Payment.
		@return Payment identifier
	  */
	public int getC_Payment_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Payment_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** ContributionType AD_Reference_ID=1000052 */
	public static final int CONTRIBUTIONTYPE_AD_Reference_ID=1000052;
	/** Presupuesto UNAB = 01 */
	public static final String CONTRIBUTIONTYPE_PresupuestoUNAB = "01";
	/** Presupuesto MINEDUC = 02 */
	public static final String CONTRIBUTIONTYPE_PresupuestoMINEDUC = "02";
	/** Set ContributionType.
		@param ContributionType ContributionType	  */
	public void setContributionType (String ContributionType)
	{

		set_Value (COLUMNNAME_ContributionType, ContributionType);
	}

	/** Get ContributionType.
		@return ContributionType	  */
	public String getContributionType () 
	{
		return (String)get_Value(COLUMNNAME_ContributionType);
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

	/** Set Percentage.
		@param Percentage 
		Percent of the entire amount
	  */
	public void setPercentage (BigDecimal Percentage)
	{
		set_Value (COLUMNNAME_Percentage, Percentage);
	}

	/** Get Percentage.
		@return Percent of the entire amount
	  */
	public BigDecimal getPercentage () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Percentage);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set T_BudgetDistributionPay.
		@param T_BudgetDistributionPay_ID T_BudgetDistributionPay	  */
	public void setT_BudgetDistributionPay_ID (int T_BudgetDistributionPay_ID)
	{
		if (T_BudgetDistributionPay_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_T_BudgetDistributionPay_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_T_BudgetDistributionPay_ID, Integer.valueOf(T_BudgetDistributionPay_ID));
	}

	/** Get T_BudgetDistributionPay.
		@return T_BudgetDistributionPay	  */
	public int getT_BudgetDistributionPay_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_T_BudgetDistributionPay_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}