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
package org.tsm.model;

import java.sql.Timestamp;
import java.util.Calendar;

import org.compiere.model.MClient;

import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.model.X_RH_EvaluationGuide;
import org.compiere.model.X_RH_EvaluationHeader;
import org.compiere.model.X_RH_EvaluationLine;

import java.math.BigDecimal;
import org.adempiere.exceptions.AdempiereException;


/**
 *	Validator for company TSM
 *
 *  @author mfrojas
 */
public class ModTSMValidateEvaluationDriver implements ModelValidator
{
	/**
	 *	Constructor.
	 *	The class is instantiated when logging in and client is selected/known
	 */
	public ModTSMValidateEvaluationDriver ()
	{
		super ();
	}	//	MyValidator

	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(ModTSMValidateEvaluationDriver.class);
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
			log.info("Initializing global validator: "+this.toString());
		}

		//	Tables to be monitored
		//	Documents to be monitored
		engine.addModelChange(X_RH_EvaluationLine.Table_Name, this);
				

	}	//	initialize

    /**
     *	Model Change of a monitored Table.
     *	OFB Consulting Ltda. By mfrojas
     */
	public String modelChange (PO po, int type) throws Exception
	{
		log.info(po.get_TableName() + " Type: "+type);
		//validacion fecha mismo año cabecera
		if((type == TYPE_AFTER_NEW || type == TYPE_AFTER_CHANGE) && po.get_Table_ID()==X_RH_EvaluationLine.Table_ID)  
		{
			X_RH_EvaluationLine eLine = (X_RH_EvaluationLine) po;

			X_RH_EvaluationGuide eGuide = new X_RH_EvaluationGuide(po.getCtx(),eLine.getRH_EvaluationHeader().getRH_EvaluationGuide_ID(),po.get_TrxName());

			
			BigDecimal amount = Env.ZERO;
			if(eLine.getAnswer1() != null && eLine.getAnswer1().compareTo("Y")==0)
				amount = amount.add(eGuide.getAnswer1());
			if(eLine.getAnswer2() != null && eLine.getAnswer2().compareTo("Y")==0)
				amount = amount.add(eGuide.getAnswer2());
			if(eLine.getAnswer3() != null && eLine.getAnswer3().compareTo("Y")==0)
				amount = amount.add(eGuide.getAnswer3());
			if(eLine.getAnswer4() != null && eLine.getAnswer4().compareTo("Y")==0)
				amount = amount.add(eGuide.getAnswer4());
			if(eLine.getAnswer5() != null && eLine.getAnswer5().compareTo("Y")==0)
				amount = amount.add(eGuide.getAnswer5());
			if(eLine.getAnswer6() != null && eLine.getAnswer6().compareTo("Y")==0)
				amount = amount.add(eGuide.getAnswer6());
			if(eLine.getAnswer7() != null && eLine.getAnswer7().compareTo("Y")==0)
				amount = amount.add(eGuide.getAnswer7());
			if(eLine.getAnswer8() != null && eLine.getAnswer8().compareTo("Y")==0)
				amount = amount.add(eGuide.getAnswer8());
			if(eLine.getAnswer9() != null && eLine.getAnswer9().compareTo("Y")==0)
				amount = amount.add(eGuide.getAnswer9());
			if(eLine.getAnswer10() != null && eLine.getAnswer10().compareTo("Y")==0)
				amount = amount.add(eGuide.getAnswer10());

			if(amount.compareTo(Env.ONEHUNDRED)>0)
				throw new AdempiereException ("El resultado no debe ser mayor a 100");
			else
				eLine.setResult(amount);
				
			eLine.save();
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
		StringBuffer sb = new StringBuffer ("QSS_Validator");
		return sb.toString ();
	}	//	toString


	

}	