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
package org.ofb.process;

import java.math.*;
import java.sql.*;
import java.util.Properties;
import java.util.logging.*;

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
public class ProcessPMScoring extends SvrProcess
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
		
		X_PM_Scoring Score=new X_PM_Scoring(Env.getCtx(), Record_ID,get_TrxName());
		
		if(P_DocAction.equals("CO") && !Score.isProcessed()){
			
			if(getPending(Score.getPM_Tender_ID())>0)
				return "existen proyectos sin evaluar";
			
			X_PM_Approval App = getApproval();
			if(App == null){
				App = new X_PM_Approval(Env.getCtx(), 0,get_TrxName());
				App.setAD_Org_ID(Score.getAD_Org_ID());
				App.setDateTrx(new Timestamp(TimeUtil.getToday().getTimeInMillis()));
				App.setPM_Scoring_ID(Score.getPM_Scoring_ID());
				App.setPM_Tender_ID(Score.getPM_Tender_ID());
				App.setDocStatus("DR");
				App.save();
			}
			else
			{
				App.setDocStatus("DR");
				App.save();
			}
			
			Score.setProcessed(true);
			Score.setDocStatus("CO");
			Score.save();
			return "Confirmado";
		}
		

			return "No es posible Cumplir la Accion ";
		
	}	//	doIt


    public X_PM_Approval getApproval(){
		
		PreparedStatement pstmt = null;
		X_PM_Approval Pro = null;
		
		String mysql="SELECT * from PM_Approval where PM_Scoring_ID = ?";
		try
		{
			pstmt = DB.prepareStatement(mysql, get_TrxName());
			pstmt.setInt(1, Record_ID);
			
			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
			{
				Pro = new X_PM_Approval(Env.getCtx(),rs,get_TrxName());
			}
			rs.close();
			pstmt.close();
			pstmt = null;
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);
		}
		
		return Pro;
		
	}
    
    public int getPending(int Tender_ID){
		   
		   int count = 0;
		   PreparedStatement pstmt = null;
		   String mysql="SELECT count(1) from c_project where (score is null or score=0 ) and pm_tender_id = ?";
			try
			{
				pstmt = DB.prepareStatement(mysql, get_TrxName());
				pstmt.setInt(1, Tender_ID);
				
				ResultSet rs = pstmt.executeQuery();
				if (rs.next())
				{
					count = rs.getInt(1);
				}
				rs.close();
				pstmt.close();
				pstmt = null;
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, e.getMessage(), e);
			}
			return count;
			
	   }
	
}	//	CopyOrder
