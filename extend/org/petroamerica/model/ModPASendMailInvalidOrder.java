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
package org.petroamerica.model;

import org.compiere.model.MClient;
import org.compiere.model.MOrder;
import org.compiere.model.MOrg;
import org.compiere.model.MUser;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.EMail;
/**
 *	Validator for PA
 *
 *  @author Italo Ni�oles
 */
public class ModPASendMailInvalidOrder implements ModelValidator
{
	/**
	 *	Constructor.
	 *	The class is instantiated when logging in and client is selected/known
	 */
	public ModPASendMailInvalidOrder ()
	{
		super ();
	}	//	MyValidator

	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(ModPASendMailInvalidOrder.class);
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
		engine.addModelChange(MOrder.Table_Name, this);		
		
	}	//	initialize

    /**
     *	Model Change of a monitored Table.
     *	OFB Consulting Ltda. By italo ni�oles
     */
	public String modelChange (PO po, int type) throws Exception
	{
		log.info(po.get_TableName() + " Type: "+type);
		if(type == TYPE_AFTER_CHANGE && po.get_Table_ID()==MOrder.Table_ID && po.is_ValueChanged("DocStatus")) 
		{	
			MOrder order = (MOrder)po;
			if(order.getDocStatus().compareTo("IN") == 0)//envio de correo
			{
				//buscamo usuario relacionado
				MOrg org = new MOrg(po.getCtx(), order.getAD_Org_ID(), po.get_TrxName());
				int ID_User = org.get_ValueAsInt("SalesRep_ID");
				if(ID_User > 0)
				{
					MUser user = new MUser(po.getCtx(), ID_User, po.get_TrxName());
					if(user.getEMail() != null)
					{
						//generamos mensaje completo
						String ln = System.getProperty("line.separator");
						StringBuilder str = new StringBuilder();
						str.append("Estimado Usuario:");
						str.append(ln);
						str.append(ln);
						str.append("La nota de venta numero "+order.getDocumentNo()+", ha quedado en estado invalido");
						str.append(ln);
						str.append("Se ruega revisar la situaci�n");
						str.append(ln);
						str.append(ln);
						str.append(ln);
						str.append("ATTE Adempiere");					
						//creamos email
						MClient client = new MClient(po.getCtx(), order.getAD_Client_ID(), po.get_TrxName());
						EMail mail = new EMail(client, client.getRequestEMail(), user.getEMail(),
								"Nota de venta "+order.getDocumentNo()+" con problemas", str.toString());
						//seteamos parametros de autenticacion
						mail.createAuthenticator(client.getRequestUser(),client.getRequestUserPW());
						//enviamos correo
						mail.send();					
						log.config("Correo Enviado a "+user.getEMail());
						log.config("Errores Correo: "+mail.getSentMsg());
					}
				}
				
			}
		}
		return null;
	}	//	modelChange

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


	

}	