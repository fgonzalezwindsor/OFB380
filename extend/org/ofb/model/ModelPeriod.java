/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package org.ofb.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.compiere.model.MClient;
import org.compiere.model.MPeriod;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.X_C_Period_Resume;
import org.compiere.util.CLogger;
import org.compiere.util.DB;


/**
 *	Validator for company Sismode
 *
 *  @author fabian aguilar
 */
public class ModelPeriod implements ModelValidator
{
	/**
	 *	Constructor.
	 *	The class is instantiated when logging in and client is selected/known
	 */
	public ModelPeriod ()
	{
		super ();
	}	//	MyValidator

	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(ModelPeriod.class);
	/** Client			*/
	private int		m_AD_Client_ID = -1;
	

	/**
	 *	Initialize Validation
	 *	@param engine validation engine
	 *	@param client client
	 */
	public void initialize (ModelValidationEngine engine, MClient client)
	{
		//client = null for global validator
		if (client != null) {
			m_AD_Client_ID = client.getAD_Client_ID();
			log.info(client.toString());
		}
		else  {
			log.info("Initializing Model Price Validator: "+this.toString());
		}

		//	Tables to be monitored
		engine.addModelChange(MPeriod.Table_Name, this); //
		//	Documents to be monitored
		
	}	//	initialize

   
	public String modelChange (PO po, int type) throws Exception
	{
		log.info(po.get_TableName() + " Type: "+type);
		
		if(  type == TYPE_AFTER_CHANGE && po.get_Table_ID()==MPeriod.Table_ID && po.is_ValueChanged("PeriodStatus"))
		{
			if(po.get_ValueAsString("PeriodStatus").equals("C"))//calcular saldos de cuentas
			{
				MPeriod period = (MPeriod)po;
				String where =getPeriodsWhere(period);
				where+=period.getC_Period_ID();
				String sql="select AD_Org_ID,account_id,sum(amtacctdr) ,sum(amtacctcr)"
						+" from fact_acct"
						+" where c_period_id IN ("+where+") and ad_client_id=?" 
						+" group by AD_Org_ID,account_id";
				
				DB.executeUpdate("Delete from C_Period_Resume where C_Period_ID="+period.getC_Period_ID(), po.get_TrxName());
				PreparedStatement pstmt = null;
				try
				{
					pstmt = DB.prepareStatement(sql, po.get_TrxName());
					pstmt.setInt(1, period.getAD_Client_ID());
					ResultSet rs = pstmt.executeQuery();
					while (rs.next())
					{
						X_C_Period_Resume rr = new X_C_Period_Resume(po.getCtx(),0,po.get_TrxName());
						rr.setC_Period_ID(period.getC_Period_ID());
						rr.setAccount_ID(rs.getInt(2));
						rr.setAD_Org_ID(rs.getInt(1));
						rr.setAmtAcctCr(rs.getBigDecimal(4));
						rr.setAmtAcctDr(rs.getBigDecimal(3));
						rr.save();
					}
					rs.close();
					pstmt.close();
					pstmt = null;
				}
				catch (Exception e)
				{
					log.log(Level.SEVERE, sql, e);
				}
					
			}
		}
		
					
	return null;
	}	//	modelChange

	/**
	 *	Validate Document.
	 *	Called as first step of DocAction.prepareIt
     *	when you called addDocValidate for the table.
     *	Note that totals, etc. may not be correct.
	 *	@param po persistent object
	 *	@param timing see TIMING_ constants
     *	@return error message or null
	 */
	public String docValidate (PO po, int timing)
	{
		log.info(po.get_TableName() + " Timing: "+timing);
		return null;
	}	//	docValidate

	/**
	 *	User Login.
	 *	Called when preferences are set
	 *	@param AD_Org_ID org
	 *	@param AD_Role_ID role
	 *	@param AD_User_ID user
	 *	@return error message or null
	 */
	public String login (int AD_Org_ID, int AD_Role_ID, int AD_User_ID)
	{
		log.info("AD_User_ID=" + AD_User_ID);

		return null;
	}	//	login


	/**
	 *	Get Client to be monitored
	 *	@return AD_Client_ID client
	 */
	public int getAD_Client_ID()
	{
		return m_AD_Client_ID;
	}	//	getAD_Client_ID


	/**
	 * 	String Representation
	 *	@return info
	 */
	public String toString ()
	{
		StringBuffer sb = new StringBuffer ("ModelPrice");
		return sb.toString ();
	}	//	toString

	 /**
	  * get periods where
	  * fabian aguilar
	  * */
	private String getPeriodsWhere(MPeriod period)
	{
		String where="0,";
		PreparedStatement pstmt = null;
		String sql="Select * from C_Period where C_Year_ID=? and PeriodNo<?";
		try
		{
			pstmt = DB.prepareStatement(sql, period.get_TrxName());
			pstmt.setInt(1, period.getC_Year_ID());
			pstmt.setInt(2, period.getPeriodNo());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				where +=rs.getInt("C_Period_ID");
				where+=",";
			}
			rs.close();
			pstmt.close();
			pstmt = null;
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		return where;
	}
	

}	