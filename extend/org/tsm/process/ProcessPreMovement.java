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
 * Contributor(s): Chris Farley - northernbrewer                              *
 *****************************************************************************/
package org.tsm.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MOrg;
import org.compiere.model.X_Pre_M_Movement;
import org.compiere.model.X_Pre_M_MovementLine;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;

/**
 *	CopyFromJobStandar
 *	
 */
public class ProcessPreMovement extends SvrProcess
{	
	/**
	 *  Prepare - e.g., get Parameters.
	 */
	private int p_Order_ID;
		
	protected void prepare()
	{	
		p_Order_ID = getRecord_ID();
	}	//	prepare

	/**
	 *  Perform process.
	 *  @return Message 
	 *  @throws Exception if not successful
	 */
	protected String doIt() throws Exception
	{	
		X_Pre_M_Movement preMov = new X_Pre_M_Movement(getCtx(), p_Order_ID, get_TrxName());		
		//procesamos por primera vez para traer lineas
		if(preMov.getDocStatus().compareTo("DR") == 0)
		{
			int cant = 0;			
			String sql = "SELECT A_Asset_ID, C_BpartnerRef_ID FROM A_Asset " +
			//" WHERE C_BpartnerRef_ID IS NOT NULL AND AD_Org_ID = "+preMov.getAD_Org_ID()+
			" WHERE C_BpartnerRef_ID IS NOT NULL AND AD_OrgRef_ID = "+preMov.getAD_Org_ID()+
			" AND Workshift = '"+preMov.get_ValueAsString("Workshift")+"' ";
			//se agrega cadena para dia
			if(preMov.getMovementDate().getDay() == 0)
				sql = sql + " AND DaySunday = 'Y'";
			else
				sql = sql + " AND DayMS = 'Y'";
			
			PreparedStatement pstmt = null;
			try
			{
				pstmt = DB.prepareStatement (sql, get_TrxName());
				ResultSet rs = pstmt.executeQuery ();			
				
				while (rs.next())
				{
					//buscamos si posee rampla
					int ID_Rampla = DB.getSQLValue(get_TrxName(), "SELECT MAX(am.A_Asset_ID) FROM MP_AssetMeter_Log aml" +
							" INNER JOIN MP_AssetMeter am ON (aml.MP_AssetMeter_ID = am.MP_AssetMeter_ID)" +
							" INNER JOIN MP_Meter me ON (me.MP_Meter_ID = am.MP_Meter_ID) WHERE aml.IsActive = 'Y' AND am.IsActive = 'Y'" +
							" AND upper(me.name) like 'PAREO' AND aml.A_AssetRef_ID = "+rs.getInt("A_Asset_ID"));
					
					//generamos linea de pre hoja de ruta
					X_Pre_M_MovementLine pMovLine = new X_Pre_M_MovementLine(getCtx(), 0, get_TrxName());
					pMovLine.setPre_M_Movement_ID(preMov.get_ID());
					pMovLine.setAD_Org_ID(preMov.getAD_Org_ID());
					pMovLine.setA_Asset_ID(rs.getInt("A_Asset_ID"));
					pMovLine.setC_BPartner_ID(rs.getInt("C_BpartnerRef_ID"));
					if(ID_Rampla > 0)
						pMovLine.setA_AssetRef_ID(ID_Rampla);
					pMovLine.save();
					cant++;
				}
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, e.getMessage(), e);
			}
			if(cant > 0)
			{
				preMov.setDocStatus("IP");
				preMov.save();
			}
			return "Se han agregado "+cant+" lineas a la orden";
		}
		//validamos para completar
		else if(preMov.getDocStatus().compareTo("IP") == 0)
		{
			boolean overWrite = false;
			overWrite = preMov.get_ValueAsBoolean("OverWrite");
			//oberwrite false hacemos validaciones
			if(!overWrite)
			{
				//validacion cantidad de lineas
				int cantLine = DB.getSQLValue(get_TrxName(),"SELECT COUNT(1) FROM Pre_M_MovementLine " +
						"WHERE IsActive = 'Y' AND Pre_M_Movement_ID = "+preMov.get_ID());
				if(cantLine < 0)
					cantLine = 0;
				MOrg org = new MOrg(getCtx(), preMov.getAD_Org_ID(), get_TrxName());
				int qtyAsset = 0;
				//String Workshift =  preMov.get_ValueAsString("Workshift");
				if(preMov.getMovementDate().getDay() == 0 && preMov.get_ValueAsString("Workshift").compareTo("30") == 0)
					qtyAsset = org.get_ValueAsInt("qty01S");
				else if(preMov.getMovementDate().getDay() == 0 && preMov.get_ValueAsString("Workshift").compareTo("60") == 0)
					qtyAsset = org.get_ValueAsInt("qty02S");
				else if(preMov.getMovementDate().getDay() == 0 && preMov.get_ValueAsString("Workshift").compareTo("90") == 0)
					qtyAsset = org.get_ValueAsInt("qty03S");
				else if(preMov.getMovementDate().getDay() != 0 && preMov.get_ValueAsString("Workshift").compareTo("30") == 0)
					qtyAsset = org.get_ValueAsInt("qty01MS");
				else if(preMov.getMovementDate().getDay() != 0 && preMov.get_ValueAsString("Workshift").compareTo("60") == 0)
					qtyAsset = org.get_ValueAsInt("qty02MS");
				else if(preMov.getMovementDate().getDay() != 0 && preMov.get_ValueAsString("Workshift").compareTo("90") == 0)
					qtyAsset = org.get_ValueAsInt("qty03MS");
				else
					qtyAsset = 0;				
				if(cantLine != qtyAsset)
					throw new AdempiereException("Cantidad de Lineas Distinta a Cantidad de Activos");
				//validaciones socio de negocio
				String sqlLine = "SELECT Pre_M_MovementLine_ID FROM Pre_M_MovementLine " +
				" WHERE Pre_M_Movement_ID = "+preMov.get_ID();
		
				PreparedStatement pstmtLine = null;				
				pstmtLine = DB.prepareStatement (sqlLine, get_TrxName());
				ResultSet rsLine = pstmtLine.executeQuery ();								
				while (rsLine.next())
				{	
					X_Pre_M_MovementLine pMovLine = new X_Pre_M_MovementLine(getCtx(), rsLine.getInt("Pre_M_MovementLine_ID"), get_TrxName());
					//validamos solo si la linea esta activa
					if(pMovLine.isActive())
					{
						//validaciones de socio de negocio
						if(pMovLine.getC_BPartner_ID() > 0)
						{
							//validacion 6 dias seguidos trabajados		
							Timestamp dateTo = preMov.getMovementDate();
							//calculamos 6 dias atras
							Calendar calCalendario = Calendar.getInstance();
					        calCalendario.setTimeInMillis(dateTo.getTime());
					        calCalendario.add(Calendar.DATE, -6);
							Timestamp dateFrom = new Timestamp(calCalendario.getTimeInMillis());
							int cantDays = DB.getSQLValue(get_TrxName(), 
									" SELECT COUNT(pm.Pre_M_Movement_ID) FROM Pre_M_MovementLine pml " +
									" INNER JOIN Pre_M_Movement pm ON (pml.Pre_M_Movement_ID = pm.Pre_M_Movement_ID)" +
									" WHERE pml.IsActive = 'Y' AND pm.DocStatus IN ('CO') AND pml.C_Bpartner_ID = "+pMovLine.getC_BPartner_ID()+
									" AND pm.movementdate BETWEEN ? AND ? ", dateFrom,dateTo);
							if(cantDays >= 6)
							{
								throw new AdempiereException("Mas de 6 dias Trabajados. Conductor: "+pMovLine.getC_BPartner().getName());
							}
							
							//validación  
							int cantSunday = DB.getSQLValue(get_TrxName(), "SELECT COUNT(DISTINCT(pm.Pre_M_Movement_ID))" +
									" FROM Pre_M_MovementLine pml " +
									" INNER JOIN Pre_M_Movement pm ON (pml.Pre_M_Movement_ID = pm.Pre_M_Movement_ID)" +
									" WHERE pml.IsActive = 'Y' AND pm.DocStatus IN ('CO') AND to_char(movementdate, 'd') = '1'");
							
							if(cantSunday > 2)
								throw new AdempiereException("Mas de 2 domingos Trabajados. Conductor: "+pMovLine.getC_BPartner().getName());
						}
					}
				}
				//validacion fecha
				//disponibilidad no puede ser mayor a 6 dias
				Calendar calCalendario = Calendar.getInstance();
		        calCalendario.add(Calendar.DATE, 6);		        
		        if(preMov.getMovementDate().compareTo(new Timestamp(calCalendario.getTimeInMillis())) > 0)
		        	throw new AdempiereException("Error: Fecha supera 6 dias");
		        //end
				
				preMov.setDocStatus("CO");
				preMov.setProcessed(true);
				preMov.save();
				DB.executeUpdate("UPDATE Pre_M_MovementLine SET Processed = 'Y' " +
						" WHERE Pre_M_Movement_ID = "+preMov.get_ID(), get_TrxName());
				
			}
			else	// sin validaciones
			{
				preMov.setDocStatus("CO");
				preMov.setProcessed(true);
				preMov.save();
				DB.executeUpdate("UPDATE Pre_M_MovementLine SET Processed = 'Y' " +
						" WHERE Pre_M_Movement_ID = "+preMov.get_ID(), get_TrxName());
			}			
			return "Procesado";			
		}
		else
			return "Procesado";		
	}	//	doIt
}	//	Replenish
