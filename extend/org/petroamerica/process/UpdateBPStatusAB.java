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
package org.petroamerica.process;

import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;

/**
 *	CopyFromJobStandar
 *	
 */
public class UpdateBPStatusAB extends SvrProcess
{	
	/**
	 *  Prepare - e.g., get Parameters.
	 */

	protected void prepare()
	{	

	}	//	prepare

	/**
	 *  Perform process.
	 *  @return Message 
	 *  @throws Exception if not successful
	 */
	protected String doIt() throws Exception
	{	
		//PASO 1
		//actualizacion de bloqueo a N y dejar credito correo 
		String sqlP1 = "UPDATE C_BPartner bp SET SOCreditStatusOFB = 'O', IsAutoBlock = 'N'" +
		" WHERE IsActive = 'Y' AND IsAutoBlock = 'Y' AND AD_Client_ID = "+Env.getAD_Client_ID(getCtx());
		DB.executeUpdate(sqlP1, get_TrxName());
		//PASO 2
		//actualizacion de bloqueo a Y bp en vista RVPETRO_Cliente_RiesgoPropio		
		String sqlP2 = "UPDATE C_BPartner bp SET SOCreditStatusOFB = 'H', IsAutoBlock = 'Y'" +
				" WHERE SOCreditStatusOFB = 'O' " +
				" AND bp.C_BPartner_ID IN (SELECT C_BPartner_ID FROM RVPETRO_Cliente_RiesgoPropio WHERE action::text = 'Y' )";
		DB.executeUpdate(sqlP2, get_TrxName());		
		//PASO 3
		//actualizacion de bloqueo a Y bp en vista RVPETRO_Cliente_Riesgo
		String sqlP3 = "UPDATE C_BPartner bp SET SOCreditStatusOFB = 'H', IsAutoBlock = 'Y'" +
				" WHERE SOCreditStatusOFB = 'O'" +
				" AND bp.C_BPartner_ID IN (SELECT C_BPartner_ID FROM RVPETRO_Cliente_Riesgoso WHERE action::text = 'Y')";
		DB.executeUpdate(sqlP3, get_TrxName());
		
		return "OK";
	}	//	doIt
}	//	Replenish
