/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                        *
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
package org.tsm.process;

import java.util.logging.*;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.*;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.*;

/**
 *	
 *	
 *  @author Fabian Aguilar faaguilar
 *  @version $Id: ProcessOT.java,v 1.2 2008/06/12 00:51:01  $
 */
public class ProcessRequestOT extends SvrProcess
{
	
	/**
	 *  Prepare - e.g., get Parameters.
	 */
	private String P_DocAction;
	/*OT ID*/
	private int 			Record_ID;
	
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("DocAction"))
				P_DocAction = (String)para[i].getParameter();
			else
				log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
		}
		
		Record_ID=getRecord_ID();
	}	//	prepare

	/**
	 *  Perrform process.
	 *  @return Message (clear text)
	 *  @throws Exception if not successful
	 */
	protected String doIt() throws Exception
	{
		//String newStatus = P_DocAction;
		String newAction = P_DocAction;
		X_MP_OT_Request ROT=new X_MP_OT_Request(Env.getCtx(), Record_ID,get_TrxName());

		//validacion de rol
		int cant = DB.getSQLValue(get_TrxName(), "SELECT COUNT(1) FROM AD_Document_Action_Access daa" +
				" INNER JOIN AD_Ref_List rl ON (daa.AD_Ref_List_ID = rl.AD_Ref_List_ID) " +
				" WHERE value = '"+newAction+"' AND AD_Role_ID = "+Env.getAD_Role_ID(getCtx())+
				" AND C_DocType_ID = " + ROT.get_ValueAsInt("C_DocType_ID"));
		//procesar
		if(cant > 0)
		{	
			if(P_DocAction.equals("PR"))
			{
				ROT.setProcessed(true);			
				ROT.setDocStatus("WC");
				ROT.save();
				return "Confirmado";
			}
			else if(P_DocAction.equals("CO"))
			{
				ROT.setProcessed(true);			
				ROT.setDocStatus("CO");
				ROT.save();
				return "Confirmado";
			}
			else if(P_DocAction.equals("RA") && ROT.isProcessed() && ROT.getDocStatus().equals("WC"))
			{
				ROT.setProcessed(false);
				ROT.save();
				return "Reactivado para Edicion";
			}
			else if(P_DocAction.equals("AN"))
			{
				ROT.setProcessed(true);
				ROT.setDocStatus("VO");
				ROT.save();
				return "Anulado";
			}
			else if(P_DocAction.equals("CL"))
			{
				ROT.setProcessed(true);
				ROT.setDocStatus("CL");
				ROT.save();
				return "Cerrado";
			}
			return "No es posible Cumplir la Accion ";
		}
		else
			throw new AdempiereException("Error: Permisos de rol insuficientes");
	}	//	doIt
}	//	CopyOrder