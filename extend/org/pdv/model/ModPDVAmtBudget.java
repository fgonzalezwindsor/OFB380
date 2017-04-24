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
package org.pdv.model;

import java.math.BigDecimal;

import org.compiere.model.MAllocationHdr;
import org.compiere.model.MAllocationLine;
import org.compiere.model.MClient;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MRequisition;
import org.compiere.model.MRequisitionLine;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.X_EO_Budget;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;


/**
 *	Validator for PDV Colegios
 *
 *  @author Fabian Aguilar
 */
public class ModPDVAmtBudget implements ModelValidator
{
	/**
	 *	Constructor.
	 *	The class is instantiated when logging in and client is selected/known
	 */
	public ModPDVAmtBudget ()
	{
		super ();
	}	//	MyValidator

	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(ModPDVAmtBudget.class);
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
		engine.addModelChange(MInvoiceLine.Table_Name, this);
		engine.addModelChange(X_EO_Budget.Table_Name, this);
		//	Documents to be monitored
		engine.addDocValidate(MRequisition.Table_Name, this);
		engine.addDocValidate(MInvoice.Table_Name, this);
		engine.addDocValidate(MAllocationHdr.Table_Name, this);
			
		
	}	//	initialize

    /**
     *	Model Change of a monitored Table.
     *	OFB Consulting Ltda. By Julio Farías
     */
	public String modelChange (PO po, int type) throws Exception
	{
		log.info(po.get_TableName() + " Type: "+type);
		
		if((type == TYPE_BEFORE_CHANGE || type == TYPE_BEFORE_NEW) && po.get_Table_ID()==MInvoiceLine.Table_ID)
		{
			MInvoiceLine invLine = (MInvoiceLine)po;
			MInvoice inv = new MInvoice(po.getCtx(), invLine.getC_Invoice_ID(), po.get_TrxName());
			if (inv.isSOTrx() == false)
			{
				if(invLine.getC_OrderLine_ID() > 0)
				{	
					int id_BudLine = DB.getSQLValue(po.get_TrxName(), "SELECT MAX(EO_budget_ID) FROM M_RequisitionLine WHERE C_OrderLine_ID = "+invLine.getC_OrderLine_ID());
					if(id_BudLine > 0)
					{
						invLine.set_CustomColumn("EO_Budget_ID", id_BudLine);
						//invLine.save();
					}
				}
			}
		}				
		if((type == TYPE_BEFORE_CHANGE || type == TYPE_BEFORE_NEW) && po.get_Table_ID()==X_EO_Budget.Table_ID)
		{
			X_EO_Budget bud = (X_EO_Budget)po;
			//BigDecimal amtComm = (BigDecimal)bud.get_Value("QtyCommitted");
			BigDecimal amtExe = (BigDecimal)bud.get_Value("QtyExecuted");
			BigDecimal amtAva = (BigDecimal)bud.get_Value("QtyAvailable");
			if (amtAva == null)
			{
				amtAva = bud.getTotalAmt();
			}
			if (amtExe == null)
			{
				amtExe = Env.ZERO;
			}
			if (amtAva != null && amtExe != null)
			{
				amtAva = amtAva.subtract(amtExe);
			}
			DB.executeUpdate("UPDATE EO_Budget SET QtyAvailable = "+amtAva+" WHERE EO_Budget_ID = "+bud.get_ID(), po.get_TrxName());
		}	
		return null;
	}	//	modelChange

	public String docValidate (PO po, int timing)
	{
		log.info(po.get_TableName() + " Timing: "+timing);
		
		if(timing == TIMING_AFTER_COMPLETE && po.get_Table_ID()==MRequisition.Table_ID)
		{
			MRequisition req = (MRequisition)po;
			if (req.isSOTrx() == false)
			{
				MRequisitionLine[] rLines = req.getLines();			
				for (int i = 0; i < rLines.length; i++)
				{
					MRequisitionLine line = rLines[i];
					int id_Budget = line.get_ValueAsInt("EO_Budget_ID");
					if(id_Budget > 0)
					{
						X_EO_Budget bud = new X_EO_Budget(po.getCtx(), id_Budget,po.get_TrxName());
						BigDecimal amt = Env.ZERO;
						amt = line.getLineNetAmt();
						
						BigDecimal qtyComm = (BigDecimal)bud.get_Value("QtyCommitted");
						if (qtyComm == null)
							qtyComm = Env.ZERO;
						if (qtyComm != null)
							qtyComm = qtyComm.add(amt);
						else						
							qtyComm = amt;
						bud.set_CustomColumn("QtyCommitted", qtyComm);
						bud.save();
					}
				}
			}
		}
		/*if(timing == TIMING_AFTER_COMPLETE && po.get_Table_ID()==MInvoice.Table_ID)
		{
			MInvoice inv = (MInvoice)po;
			if (inv.isSOTrx() == false)
			{
				MInvoiceLine[] iLines = inv.getLines();
				for (int i = 0; i < iLines.length; i++)
				{
					MInvoiceLine line = iLines[i];
					int id_Budget = line.get_ValueAsInt("EO_Budget_ID");
					if(id_Budget > 0)
					{
						X_EO_Budget bud = new X_EO_Budget(po.getCtx(), id_Budget,po.get_TrxName());
						BigDecimal amt = Env.ZERO;
						amt = line.getLineNetAmt();
						
						BigDecimal qtyComm = (BigDecimal)bud.get_Value("QtyCommitted");
						if (qtyComm == null)
							qtyComm = Env.ZERO;
						if (qtyComm != null)
							qtyComm = qtyComm.add(amt);
						else						
							qtyComm = amt;
						bud.set_CustomColumn("QtyCommitted", qtyComm);
						bud.save();
					}
				}
			}
		}*/		
		if(timing == TIMING_AFTER_COMPLETE && po.get_Table_ID()==MAllocationHdr.Table_ID)
		{
			MAllocationHdr alloHdr = (MAllocationHdr)po;
			MAllocationLine[] aLines = alloHdr.getLines(false);			 
			for (int i = 0; i < aLines.length; i++)
			{
				MAllocationLine aline = aLines[i];
				if (aline.getC_Payment_ID() > 0 && aline.getC_Invoice_ID() > 0)
				{
					if(aline.getC_Invoice().isSOTrx() == false)
					{
						MInvoice inv = new MInvoice(po.getCtx(), aline.getC_Invoice_ID(), po.get_TrxName());
						MInvoiceLine[] iLines = inv.getLines();
						for (int a = 0; a < iLines.length; a++)
						{
							MInvoiceLine iline = iLines[a];
							int id_Budget = iline.get_ValueAsInt("EO_Budget_ID");
							if(id_Budget > 0)
							{
								X_EO_Budget bud = new X_EO_Budget(po.getCtx(),id_Budget, po.get_TrxName());
								// se calcula neto a sumar, desde la asignacion o desde la linea
								BigDecimal qtyNeto = Env.ZERO;
								if(aline.getAmount().abs().compareTo(iline.getLineNetAmt()) > 0)
									qtyNeto = iline.getLineNetAmt();
								else
									qtyNeto = aline.getAmount().abs();
								//se le suma a valor ejecutado en el presupuesto
								BigDecimal qtyExe = (BigDecimal)bud.get_Value("QtyExecuted");								
								if (qtyExe == null)
									qtyExe = Env.ZERO;
								if (qtyExe != null)
									qtyExe = qtyExe.add(qtyNeto);
								else
									qtyExe = qtyNeto;
								bud.set_CustomColumn("QtyExecuted", qtyExe);
								bud.save();		
							}
						}
					}
				}
			}
		}		
		if(timing == TIMING_AFTER_VOID && po.get_Table_ID()==MAllocationHdr.Table_ID)
		{
			MAllocationHdr alloHdr = (MAllocationHdr)po;
			MAllocationLine[] aLines = alloHdr.getLines(false);			 
			for (int i = 0; i < aLines.length; i++)
			{
				MAllocationLine aline = aLines[i];
				if (aline.getC_Payment_ID() > 0 && aline.getC_Invoice_ID() > 0)
				{
					if(aline.getC_Invoice().isSOTrx() == false)
					{
						MInvoice inv = new MInvoice(po.getCtx(), aline.getC_Invoice_ID(), po.get_TrxName());
						MInvoiceLine[] iLines = inv.getLines();
						for (int a = 0; a < iLines.length; a++)
						{
							MInvoiceLine iline = iLines[a];
							int id_Budget = iline.get_ValueAsInt("EO_Budget_ID");
							if(id_Budget > 0)
							{
								X_EO_Budget bud = new X_EO_Budget(po.getCtx(),id_Budget, po.get_TrxName());
								// se calcula neto a sumar, desde la asignacion o desde la linea
								BigDecimal qtyNeto = Env.ZERO;
								if(aline.getAmount().abs().compareTo(iline.getLineNetAmt()) > 0)
									qtyNeto = iline.getLineNetAmt();
								else
									qtyNeto = aline.getAmount().abs();
								//se le suma a valor ejecutado en el presupuesto
								BigDecimal qtyExe = (BigDecimal)bud.get_Value("QtyExecuted");								
								if (qtyExe == null)
									qtyExe = Env.ZERO;
								if (qtyExe != null)
									qtyExe = qtyExe.subtract(qtyNeto);
								else
									qtyExe = qtyNeto;
								bud.set_CustomColumn("QtyExecuted", qtyExe);
								bud.save();		
							}
						}
					}
				}
			}
		}
		
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


	

}	