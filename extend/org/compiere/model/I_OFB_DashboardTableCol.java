/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2007 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package org.compiere.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.util.KeyNamePair;

/** Generated Interface for OFB_DashboardTableCol
 *  @author Adempiere (generated) 
 *  @version Release 3.6.0LTS
 */
public interface I_OFB_DashboardTableCol 
{

    /** TableName=OFB_DashboardTableCol */
    public static final String Table_Name = "OFB_DashboardTableCol";

    /** AD_Table_ID=1000061 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 2 - Client 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(2);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Client.
	  * Client/Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within client
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within client
	  */
	public int getAD_Org_ID();

    /** Column name ColName */
    public static final String COLUMNNAME_ColName = "ColName";

	/** Set ColName	  */
	public void setColName (String ColName);

	/** Get ColName	  */
	public String getColName();

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name DateColumn */
    public static final String COLUMNNAME_DateColumn = "DateColumn";

	/** Set Date Column.
	  * Fully qualified date column
	  */
	public void setDateColumn (String DateColumn);

	/** Get Date Column.
	  * Fully qualified date column
	  */
	public String getDateColumn();

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive (boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

    /** Column name Line */
    public static final String COLUMNNAME_Line = "Line";

	/** Set Line No.
	  * Unique line for this document
	  */
	public void setLine (int Line);

	/** Get Line No.
	  * Unique line for this document
	  */
	public int getLine();

    /** Column name OFB_DashboardTable_ID */
    public static final String COLUMNNAME_OFB_DashboardTable_ID = "OFB_DashboardTable_ID";

	/** Set OFB_DashboardTable	  */
	public void setOFB_DashboardTable_ID (int OFB_DashboardTable_ID);

	/** Get OFB_DashboardTable	  */
	public int getOFB_DashboardTable_ID();

	public I_OFB_DashboardTable getOFB_DashboardTable() throws RuntimeException;

    /** Column name OFB_DashboardTableCol_ID */
    public static final String COLUMNNAME_OFB_DashboardTableCol_ID = "OFB_DashboardTableCol_ID";

	/** Set OFB_DashboardTableCol	  */
	public void setOFB_DashboardTableCol_ID (int OFB_DashboardTableCol_ID);

	/** Get OFB_DashboardTableCol	  */
	public int getOFB_DashboardTableCol_ID();

    /** Column name SelectClause */
    public static final String COLUMNNAME_SelectClause = "SelectClause";

	/** Set Sql SELECT.
	  * SQL SELECT clause
	  */
	public void setSelectClause (String SelectClause);

	/** Get Sql SELECT.
	  * SQL SELECT clause
	  */
	public String getSelectClause();

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/** Get Updated By.
	  * User who updated this records
	  */
	public int getUpdatedBy();

    /** Column name WhereClause */
    public static final String COLUMNNAME_WhereClause = "WhereClause";

	/** Set Sql WHERE.
	  * Fully qualified SQL WHERE clause
	  */
	public void setWhereClause (String WhereClause);

	/** Get Sql WHERE.
	  * Fully qualified SQL WHERE clause
	  */
	public String getWhereClause();
}
