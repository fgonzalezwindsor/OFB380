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

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.X_TP_Refund;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
/**
 *	
 *	
 *  @author Italo Niñoles ininoles
 *  @version $Id: SeparateInvoices.java $
 */
public class ProcessRefund extends SvrProcess
{
	private int				p_TP_Viatico_ID = 0; 
	/**
	 *  Prepare - e.g., get Parameters.
	 */
	 protected void prepare()
	{
		 p_TP_Viatico_ID=getRecord_ID();
	}	//	prepare

	/**
	 *  Perrform process.
	 *  @return Message (clear text)
	 *  @throws Exception if not successful
	 */
	protected String doIt() throws Exception
	{
		if (p_TP_Viatico_ID > 0)
		{
			X_TP_Refund viatico = new X_TP_Refund(getCtx(), p_TP_Viatico_ID, get_TrxName());
			Boolean sig1 = viatico.get_ValueAsBoolean("Signature1");
			Boolean sig2 = viatico.get_ValueAsBoolean("Signature2");
			Boolean sig3 = viatico.get_ValueAsBoolean("Signature3");
			Boolean sig4 = viatico.get_ValueAsBoolean("Signature4");
			if(sig1 == false || sig2 == false || sig3 == false || sig4 == false)
				throw new AdempiereException("Error: No se puede completar sin las firmas necesarias");						
			if(viatico.getDocStatus().compareToIgnoreCase("DR")==0)
			{	
				if(viatico.get_ValueAsBoolean("overwrite"))
				{
					viatico.setDocStatus("CO");
					viatico.setProcessed(true);
				}
				else
				{
					int cantRep = DB.getSQLValue(get_TrxName(), "SELECT COALESCE(COUNT(1),0)FROM TP_RefundLine " +
						" WHERE TP_Refund_ID="+viatico.get_ID()+" GROUP BY DateTrx HAVING COUNT(1) > 1");
					if(cantRep > 0)
						throw new AdempiereException("Error: Existe mas de una Hoja de Ruta para la misma Fecha");
					else
					{
						viatico.setDocStatus("CO");
						viatico.setProcessed(true);
					}
				}
			}
			viatico.save();
		}
		
		
	   return "Procesado ";
	}	//	doIt
}
