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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.compiere.model.X_C_Margin;
import org.compiere.model.X_C_MarginHeader;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;

/**
 *	CopyFromJobStandar
 *	
 */
public class GenerateMargin extends SvrProcess
{	
	/**
	 *  Prepare - e.g., get Parameters.
	 */
	private int p_MarginHead_ID;
		
	protected void prepare()
	{	
		p_MarginHead_ID = getRecord_ID();
	}	//	prepare

	/**
	 *  Perform process.
	 *  @return Message 
	 *  @throws Exception if not successful
	 */
	protected String doIt() throws Exception
	{	
		X_C_MarginHeader head = new X_C_MarginHeader(getCtx(), p_MarginHead_ID, get_TrxName());
		
		DB.executeUpdateEx("DELETE FROM C_Margin WHERE C_MarginHeader_ID = "+head.get_ID(), get_TrxName());
		
		String sqlPList = "select ao.name as org, mp.name as producto,mp.M_Product_ID,ao.ad_org_id, " +
				" COALESCE((select sum(cil.linenetamt) from rv_c_invoiceline cil where cil.issotrx='Y' and dame_periodo(cil.dateinvoiced)=p.c_period_id and cil.m_product_id=mp.m_product_id and cil.ad_org_id=ao.ad_org_id and cil.docstatus in ('CO','CL')),0) as ventas, " +
				" COALESCE((select sum(cil.qtyentered) from rv_c_invoiceline cil where cil.issotrx='Y' and dame_periodo(cil.dateinvoiced)=p.c_period_id and cil.m_product_id=mp.m_product_id and cil.ad_org_id=ao.ad_org_id and cil.docstatus in ('CO','CL')),0) as q_litros, " +
				" COALESCE((select sum(cil.linenetamt) from rv_c_invoiceline cil where cil.issotrx='N' and dame_periodo(cil.dateinvoiced)=p.c_period_id and cil.m_product_id=mp.m_product_id and cil.ad_org_id=ao.ad_org_id and cil.docstatus in ('CO','CL')),0) as compras," +
				" COALESCE(fpa_costo_final(ao.ad_org_id, mp.m_product_id, p.c_period_id),0) as precio_final, " +
				" COALESCE(fpa_inventario_final(ao.ad_org_id, mp.m_product_id, p.c_period_id),0) as inv_final," +
				" coalesce((select sum(jl.amtacctcr)+sum(jl.amtacctdr) from GL_JournalLine jl where lower(jl.description) like 'margen%' and jl.ad_org_id=ao.ad_org_id and jl.m_product_id=mp.m_product_id and dame_periodo(jl.dateacct)=p.c_period_id),0) as asiento_margen " +
				" FROM ad_org ao, m_product mp, c_period p " +
				" WHERE ao.ad_org_id <> 0 AND ao.ad_client_id = "+head.getAD_Client_ID()+
				" AND mp.ad_client_id = "+head.getAD_Client_ID()+" AND p.ad_client_id = "+head.getAD_Client_ID()+
				" and p.c_period_id = ? " +
				" GROUP BY ao.ad_org_id, mp.m_product_id, p.c_period_id " +
				" order by  mp.m_product_id,ao.ad_org_id";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sqlPList, get_TrxName());
			pstmt.setInt(1, head.getC_Period_ID());
			//pstmt.setInt(2, head.getAD_Org_ID());
			rs = pstmt.executeQuery ();			
			while (rs.next ())
			{
				X_C_Margin marg = new X_C_Margin(getCtx(), 0, get_TrxName());
				marg.setC_MarginHeader_ID(head.get_ID());
				marg.setAD_Org_ID(rs.getInt("AD_Org_ID"));
				marg.setM_Product_ID(rs.getInt("M_Product_ID"));
				marg.setC_Period_ID(head.getC_Period_ID());
				marg.settotal_sales2(rs.getBigDecimal("ventas"));				
				//marg.settotal(rs.getBigDecimal("asiento_margen"));
				marg.setqty_sales(rs.getBigDecimal("q_litros"));
				marg.setprovisiones(rs.getBigDecimal("asiento_margen"));				
				//ir a buscar margen anterior
				int ID_Anterior = DB.getSQLValue(get_TrxName(), 
						"SELECT C_Margin_ID FROM C_Margin WHERE M_Product_ID = "+rs.getInt("M_Product_ID")+
						" AND AD_Org_ID = "+rs.getInt("AD_Org_ID")+" AND C_Period_ID = "+(head.getC_Period_ID()-1));
				if(ID_Anterior > 0)
				{
					X_C_Margin old = new X_C_Margin(getCtx(), ID_Anterior, get_TrxName());
					//calculo de costo_inicial
					marg.setcosto_inicial(old.getinv_final().multiply(old.getprecio_final()).setScale(0, RoundingMode.HALF_EVEN));					
					marg.setinventario_inicial(old.getinv_final());
				}
				else
				{
					marg.setcosto_inicial(Env.ZERO);
					marg.setinventario_inicial(Env.ZERO);
				}
				//seteamos valores para ir a buscar en proximo margen
				marg.setprecio_final(rs.getBigDecimal("precio_final"));
				marg.setinv_final(rs.getBigDecimal("inv_final"));
				//end
				marg.settotal_compras(rs.getBigDecimal("compras"));
				//seteo costo final
				marg.setcosto_final(marg.getinv_final().multiply(marg.getprecio_final()).setScale(0, RoundingMode.HALF_EVEN));
				//calculo de costo de venta
				BigDecimal costo_venta = Env.ZERO;
				//costo_venta = marg.getinventario_inicial().add(marg.gettotal_compras()).subtract(marg.getinv_final().multiply(marg.getprecio_final())).subtract(marg.gettotal()).setScale(0, RoundingMode.HALF_EVEN);
				costo_venta = marg.getcosto_inicial().add(marg.gettotal_compras()).subtract(marg.getcosto_final()).subtract(marg.getprovisiones()).setScale(0, RoundingMode.HALF_EVEN);
				if(costo_venta != null)
					marg.setqty_cost(costo_venta);
				else
					marg.setqty_cost(Env.ZERO);				
				//end				
				//seteo margen bruto
				marg.settotal(marg.gettotal_sales2().subtract(marg.getqty_cost()));
				//calculamos $/Litro
				BigDecimal pLitro = Env.ZERO;
				if(marg.getqty_sales().compareTo(Env.ZERO) != 0)
					pLitro = marg.gettotal().divide(marg.getqty_sales(),RoundingMode.HALF_EVEN);
				marg.settotal_purchases2(pLitro);				
				//seteo costo final
				marg.setcosto_final(marg.getinv_final().multiply(marg.getprecio_final()).setScale(0, RoundingMode.HALF_EVEN));
				//calculo de %sobre ventas
				BigDecimal sobre_ventas = Env.ZERO;
				if(marg.gettotal_sales2().compareTo(Env.ZERO) != 0)
					sobre_ventas = marg.gettotal().multiply(Env.ONEHUNDRED).divide(marg.gettotal_sales2(),RoundingMode.HALF_EVEN);
				if(sobre_ventas != null)
					marg.setporcent(sobre_ventas);
				else
					marg.setporcent(Env.ZERO);
				marg.save();
				//
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);
		}
		finally
		{
			pstmt.close();
			rs.close();	
			pstmt = null;
			rs = null;	
		}
		return "OK";
	}	//	doIt
}	//	Replenish
