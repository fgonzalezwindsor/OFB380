package org.blumos.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.compiere.model.MClient;
import org.compiere.model.MOrderLine;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.X_M_ProductionLine;
import org.compiere.model.X_M_ProductionPlan;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
/**
 *	Validator for company blumos
 *
 *  @author ininoles
 */
public class ModBlumosProduccionRedondear implements ModelValidator
{
	/**
	 *	Constructor.
	 *	The class is instantiated when logging in and client is selected/known
	 */
	public ModBlumosProduccionRedondear ()
	{
		super ();
	}	//	MyValidator

	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(ModBlumosProduccionRedondear.class);
	/** Client			*/
	private int		m_AD_Client_ID = -1;
	/**	Logger	*/
	private static CLogger s_log = CLogger.getCLogger (MOrderLine.class);

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
		engine.addModelChange(X_M_ProductionPlan.Table_Name, this);
		//	Documents to be monitored
				

	}	//	initialize

    /**
     *	Model Change of a monitored Table.
     *	
     */
	public String modelChange (PO po, int type) throws Exception
	{
		log.info(po.get_TableName() + " Type: "+type);		
		
		if((type == TYPE_BEFORE_CHANGE || type == TYPE_BEFORE_NEW) && po.get_Table_ID()==X_M_ProductionPlan.Table_ID
				&& po.is_ValueChanged("Prorrogar"))  
		{
			X_M_ProductionPlan pPlan = (X_M_ProductionPlan) po;
			if(pPlan.get_ValueAsBoolean("redondear"))
			{
				String sql = "SELECT m_productionline_id, m_product_id, movementqty " +
						" FROM m_productionline where m_productionplan_id = ?";
				PreparedStatement pstmt = null;
				try
				{
					pstmt = DB.prepareStatement (sql, null);
					pstmt.setInt (1, pPlan.get_ID());
					ResultSet rs = pstmt.executeQuery ();
					while(rs.next ())
					{
						X_M_ProductionLine pLine = new X_M_ProductionLine(po.getCtx(), rs.getInt("M_ProductionLine_ID"), po.get_TrxName());
						pLine.setMovementQty(pLine.getMovementQty().setScale(pLine.getM_Product().getC_UOM().getStdPrecision(),RoundingMode.HALF_EVEN));
						pLine.saveEx(po.get_TrxName());
					}
					rs.close ();
					pstmt.close ();
					pstmt = null;
				}
				catch (Exception e)
				{
					s_log.log (Level.SEVERE, sql, e);
				}
				/* RUTINAS PARA APLICACION DE ECUACION DE CALCULO A REBAJAR DE PILAS TIPO PA DE MINERA ANITA EN PRODUCCIONES DE SULFATO 
				  para que esto operes es necesario que
				  1.- LAS PILAS QUE NO ESTEN BAJO RIEGO ESTEN MARCADAS COMO SUSPENDIDO EN EL MAESTRO DE PRODUCTO
				  2.- ESTE INGRESADA LA MEJOR LEY ESTIMADA PARA CADA PILA
				  3.- YA SE HAYA APLICADO LA FORMULA PARA LA PRODUCCION EN CURSO, ES DECIR, DEBE HABER LINEAS EN M_PRODUCTIONLINE
				*/  
				//SI EL PRODUCTO A PRODUCIR NO ES SC01 M_Product_ID=1007494 NOS SALTAMOS RUTINA
				if(pPlan.getM_Product_ID() == 1007494)
				{
					BigDecimal potencial = DB.getSQLValueBD(po.get_TrxName(), "select sum(ms.qtyonhand*(mai.valuenumber/100)*0.7*3.92*1.1)" +
							" from m_product mp " +
							" inner join m_storage ms on (mp.m_product_id=ms.m_product_id)" +
							" inner join m_attributeinstance mai on (ms.m_attributesetinstance_id=mai.m_attributesetinstance_id)" +
							" where mp.ad_client_id=1000002 and mp.name like 'PA%' and mp.discontinued<>'Y' and ms.qtyonhand>0" +
							" and mai.m_attribute_id=1000006;");
					String sql2 = "select pl.m_productionline_id, pl.m_product_id, pl.movementqty, mp.discontinued" +
							" from m_productionline pl" +
							" INNER JOIN M_PRODUCT mp on (pl.m_product_id=mp.m_product_id)" +
							" where pl.m_productionplan_id='"+pPlan.get_ID()+"' and mp.name like 'PA%' and pl.ad_client_id=1000002'";
					BigDecimal mil = new BigDecimal(1000.0);
					PreparedStatement pstmt2 = null;
					try
					{
						pstmt2 = DB.prepareStatement (sql2, null);
						ResultSet rs2 = pstmt2.executeQuery ();						
						while(rs2.next ())
						{
							X_M_ProductionLine pLine2 = new X_M_ProductionLine(po.getCtx(), rs2.getInt("M_ProductionLine_ID"), po.get_TrxName());
							int scale = pLine2.getM_Product().getC_UOM().getStdPrecision();
							BigDecimal OnHand = DB.getSQLValueBD(po.get_TrxName(), "select sum(qtyonhand) from m_storage where m_product_id="+pLine2.getM_Product_ID());
							BigDecimal qtyPla = (BigDecimal)pLine2.get_Value("productionqty");
							BigDecimal newQty = (qtyPla.divide(potencial.multiply(OnHand))).divide(mil,scale,RoundingMode.HALF_EVEN);
							newQty = newQty.negate();
							if(pLine2.get_ValueAsBoolean("discontinued"))
							{
								pLine2.deleteEx(true, po.get_TrxName());
							}
							else
							{
								int ley = DB.getSQLValue(po.get_TrxName(),"select mai.valuenumber from m_product mp" +
										" inner join m_storage ms on (mp.m_product_id=ms.m_product_id)" +
										" inner join m_attributeinstance mai on (ms.m_attributesetinstance_id=mai.m_attributesetinstance_id)" +
										" inner join m_attributesetinstance lote on (ms.m_attributesetinstance_id=lote.m_attributesetinstance_id)" +
										" where mp.m_product_id="+pLine2.getM_Product_ID()+" and mai.m_attribute_id=1000006 and ms.qtyonhand>0;");
								String lote = DB.getSQLValueString(po.get_TrxName(),"select lote.lot from m_product mp" +
										" inner join m_storage ms on (mp.m_product_id=ms.m_product_id)" +
										" inner join m_attributeinstance mai on (ms.m_attributesetinstance_id=mai.m_attributesetinstance_id)" +
										" inner join m_attributesetinstance lote on (ms.m_attributesetinstance_id=lote.m_attributesetinstance_id)" +
										" where mp.m_product_id="+pLine2.getM_Product_ID()+" and mai.m_attribute_id=1000006 and ms.qtyonhand>0;");
								if(ley < 0)
									return "Ley ingresada menor o igual a CERO para LOTE "+lote+" Corrija en Consulta de Trazabilidad, buscando el lote indicado.";
								pLine2.setMovementQty(newQty);
							}
						}
						rs2.close ();
						pstmt2.close ();
						pstmt2 = null;
					}
					catch (Exception e)
					{
						s_log.log (Level.SEVERE, sql, e);
					}
				}
			}
		}
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