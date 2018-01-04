package org.blumos.model;

import java.sql.Timestamp;

import org.compiere.model.MClient;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
/**
 *	Validator for company blumos
 *
 *  @author ininoles
 */
public class ModBlumosValidaTurno implements ModelValidator
{
	/**
	 *	Constructor.
	 *	The class is instantiated when logging in and client is selected/known
	 */
	public ModBlumosValidaTurno ()
	{
		super ();
	}	//	MyValidator

	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(ModBlumosValidaTurno.class);
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
		engine.addDocValidate(MOrder.Table_Name, this);
				

	}	//	initialize

    /**
     *	Model Change of a monitored Table.
     *	
     */
	public String modelChange (PO po, int type) throws Exception
	{
		log.info(po.get_TableName() + " Type: "+type);		
		
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
		if(timing == TIMING_AFTER_COMPLETE && po.get_Table_ID()==MOrder.Table_ID)
		{
			MOrder order = (MOrder) po;
			if(order.isSOTrx() && order.getDeliveryViaRule().compareTo("P") != 0
					&& order.getDeliveryViaRule().compareTo("V") != 0
					&& order.getM_Warehouse_ID() == 1000000
					&& !order.get_ValueAsBoolean("Solutec"))
			{
				Timestamp today_00 = new Timestamp(System.currentTimeMillis());
				Timestamp today = new Timestamp(System.currentTimeMillis());
				today_00.setHours(0);
				today_00.setMinutes(0);
				today_00.setSeconds(0);
				today_00.setNanos(0);
				Timestamp date_promised = order.getDatePromised();
				date_promised.setHours(0);
				date_promised.setMinutes(0);
				date_promised.setSeconds(0);
				date_promised.setNanos(0);		
				if(date_promised.compareTo(today_00) < 0 
						&& order.getDatePromised().getHours() > 9)
				{
					Timestamp prom1 = today_00;
					prom1.setDate(prom1.getDate()+1);
					order.setDatePromised(prom1);
					DB.executeUpdate("update c_orderline set rrstartdate=sysdate+1 where C_order_id=:new.c_order_id and datepromised<=to_char(sysdate, 'dd/mm/yyyy') and datepromised=rrstartdate",po.get_TrxName());
					DB.executeUpdate("update c_orderline set datepromised=sysdate+1 where C_order_id=:new.c_order_id and datepromised<=to_char(sysdate, 'dd/mm/yyyy')",po.get_TrxName());
				}
				if(date_promised.compareTo(today_00) < 0 
						&& order.getDatePromised().getHours() < 9)
				{
					order.setDatePromised(today_00);
					DB.executeUpdate("update c_orderline set rrstartdate=sysdate where C_order_id=:new.c_order_id and datepromised<=to_char(sysdate, 'dd/mm/yyyy') and datepromised=rrstartdate",po.get_TrxName());
					DB.executeUpdate("update c_orderline set datepromised=sysdate where C_order_id=:new.c_order_id and datepromised<=to_char(sysdate, 'dd/mm/yyyy')",po.get_TrxName());
				}
				if(today.getHours() > 9)
				{
					DB.executeUpdate("update c_orderline set rrstartdate=sysdate+1 where C_order_id=:new.c_order_id and datepromised<=to_char(sysdate, 'dd/mm/yyyy') and datepromised=rrstartdate", po.get_TrxName());
					DB.executeUpdate("update c_orderline set datepromised=sysdate+1 where C_order_id=:new.c_order_id and datepromised<=to_char(sysdate, 'dd/mm/yyyy')", po.get_TrxName());
				}
			}
			
			
			
			MOrderLine[] lines = order.getLines(true, null);	//	Line is default
			for (int i = 0; i < lines.length; i++)
			{
				MOrderLine line = lines[i];
				Timestamp date= (Timestamp)line.get_Value("rrstartdate");
				if(date == null)
				{
					line.set_CustomColumn("rrstartdate", line.getDatePromised());
					line.set_CustomColumn("Qty_Original", line.getQtyOrdered());
					line.save(po.get_TrxName());
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


	

	

}	