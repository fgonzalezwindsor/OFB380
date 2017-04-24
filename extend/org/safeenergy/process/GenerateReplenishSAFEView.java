package org.safeenergy.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.compiere.model.MRequisition;
import org.compiere.model.MRequisitionLine;
import org.compiere.model.MWarehouse;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.AdempiereSystemError;
import org.compiere.util.DB;

public class GenerateReplenishSAFEView  extends SvrProcess
{
	/**
	 *  Prepare - e.g., get Parameters.
	 */
	private int				p_Partner = 0; 

	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			
			if (name.equals("Partner"))
				p_Partner = para[i].getParameterAsInt();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	}	//	prepare

	/**
	 *  Perform process.
	 *  @return Message 
	 *  @throws Exception if not successful
	 */
	protected String doIt() throws Exception
	{
		int M_Requisition_ID = getRecord_ID();
		MRequisition req = new MRequisition(getCtx(), M_Requisition_ID, get_TrxName());
						
		MWarehouse wh = new MWarehouse(getCtx(), req.getM_Warehouse_ID(), get_TrxName());
		if (wh.get_ID() == 0)  
			throw new AdempiereSystemError("@FillMandatory@ @M_Warehouse_ID@");
				
		String sqlDet = "SELECT M_Product_ID,C_BPartner_ID, qtytoorder FROM RVOFB_ReplenishDPP " +
				" WHERE M_WareHouse_ID = "+req.getM_Warehouse_ID()+" AND C_BPartner_ID = "+p_Partner+" AND AD_Client_ID = "+req.getAD_Client_ID();
		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement (sqlDet, get_TrxName());
			ResultSet rs = pstmt.executeQuery ();
			while (rs.next ())
			{
				MRequisitionLine line = new MRequisitionLine(req);
				line.setM_Product_ID(rs.getInt("M_Product_ID"));
				line.setC_BPartner_ID(rs.getInt("C_BPartner_ID"));
				line.setQty(rs.getBigDecimal("qtytoorder"));
				line.setPrice();
				line.save();
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;			
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);
		}		
		return "Detalle Generado";
	}	//	doIt

	
	
}
