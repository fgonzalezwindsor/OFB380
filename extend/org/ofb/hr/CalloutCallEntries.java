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
package org.ofb.hr;

import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/**
 *	hro Callouts.
 *	
 *  @author Fabian Aguilar
 *  @version $v 1.0 
 */
public class CalloutCallEntries extends CalloutEngine
{
	/**	Debug Steps			*/
	private boolean steps = false;

	/**
	 *
	 *  @param ctx      Context
	 *  @param WindowNo current Window No
	 *  @param mTab     Model Tab
	 *  @param mField   Model Field
	 *  @param value    The new value
	 *  @return Error message or ""
	 */
	public String jobRequest (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value)
	{
		if(value==null)
			return "";
		
		int jobRequest_ID = (Integer)value;
		
		X_HRO_JobRequest jr = new X_HRO_JobRequest(Env.getCtx(), jobRequest_ID, null);
		
		mTab.setValue("HR_Job_ID", jr.getHR_Job_ID());
		mTab.setValue("JobCant", jr.getJobCant());
		
		return "";
    }	
	
	
}	//	CalloutOrder
