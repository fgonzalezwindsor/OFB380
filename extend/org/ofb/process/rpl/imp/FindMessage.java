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
package org.ofb.process.rpl.imp;

import java.sql.*;
import org.compiere.db.DB_Oracle;
import org.compiere.db.DB_PostgreSQL;
import org.compiere.model.*;
import org.compiere.process.SvrProcess;
import org.compiere.util.*;

/**
 *	
 *	
 *  @author Fabian Aguilar faaguilar
 *  @version $Id: FindMessage.java,v 1.2 2010/08/12 00:51:01  $
 */
public class FindMessage extends SvrProcess
{
	
	
	protected void prepare()
	{
		
	}	//	prepare

	/**
	 *  Perrform process.
	 *  @return Message (clear text)
	 *  @throws Exception if not successful
	 */
	protected String doIt() throws Exception
	{
		int count=0;
		PreparedStatement pstmt = null;
		String sql="select host,port,account,passwordinfo,dbtype,dbinstance from OFB_impprocessor where isactive='Y'";
		try {
			pstmt = DB.prepareStatement (sql, get_TrxName());
			ResultSet rs = pstmt.executeQuery ();
			
			while ( rs.next() ) {
				try{
					Connection conn=null;
					if(rs.getString("DBType").equals("Orc")){//Oracle Connection
						DB_Oracle oracle=new DB_Oracle();
						String url=oracle.getConnectionURL(rs.getString("host"), rs.getInt("port"), rs.getString("DBInstance"), rs.getString("account"));
						conn=oracle.getDriverConnection(url, rs.getString("account"), rs.getString("passwordinfo"));
					}
					else{//Postgres Connection
						DB_PostgreSQL postgre=new DB_PostgreSQL();
						String url=postgre.getConnectionURL(rs.getString("host"), rs.getInt("port"), rs.getString("DBInstance"), rs.getString("account"));
						conn=postgre.getDriverConnection(url, rs.getString("account"), rs.getString("passwordinfo"));
					}
					int lastM=DB.getSQLValue(get_TrxName(), "select max(remoteid) from ofb_rplImp where remotehost=?",rs.getString("host"));
					String sqlMessage="select * from ofb_rplexp where ofb_rplexp_id>"+lastM +" order by ofb_rplexp_id Asc" ;
					Statement st=conn.createStatement();
					ResultSet rsM = st.executeQuery(sqlMessage);
					while (rsM.next()){
						X_OFB_RplImp message= new X_OFB_RplImp(Env.getCtx(), 0,get_TrxName());
						message.setProcessed(false);
						message.setRemoteHost(rsM.getString("serverhost"));
						message.setRemoteID(rsM.getInt("ofb_rplexp_id"));
						message.setRemoteRecord_ID(rsM.getInt("record_id"));
						message.setTableName(rsM.getString("tablename"));
						message.setxml(rsM.getString("xml"));
						message.save();
						count++;
					}
					rsM.close();
					st.close();
					conn.close();
				}
				catch (Exception e){
					log.warning(rs.getString("host")+":"+e);
					continue;
				}
				
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;
		} 
		catch (SQLException e) {
			log.severe(sql+"/"+e);
			
		} 
		finally {
				if (pstmt != null) 
					pstmt.close ();
				pstmt = null;
		}	
			return "Messages: " + count;
		
	}	//	doIt


	
}	//	CopyOrder
