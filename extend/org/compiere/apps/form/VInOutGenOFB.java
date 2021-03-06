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
package org.compiere.apps.form;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.adempiere.plaf.AdempierePLAF;
import org.compiere.apps.ADialog;
import org.compiere.apps.ADialogDialog;
import org.compiere.apps.ConfirmPanel;
import org.compiere.apps.ProcessCtl;
import org.compiere.apps.StatusBar;
import org.compiere.grid.ed.VComboBox;
import org.compiere.grid.ed.VLocationDialog;
import org.compiere.grid.ed.VLookup;
import org.compiere.minigrid.IDColumn;
import org.compiere.minigrid.MiniTable;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MOrder;
import org.compiere.model.MPInstance;
import org.compiere.model.MPInstancePara;
import org.compiere.model.MPrivateAccess;
import org.compiere.model.MRMA;
import org.compiere.model.MRefList;
import org.compiere.plaf.CompiereColor;
import org.compiere.print.ReportCtl;
import org.compiere.print.ReportEngine;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoUtil;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTabbedPane;
import org.compiere.swing.CTextPane;
import org.compiere.util.ASyncProcess;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;
import org.compiere.util.Trx;

/**
 *	Manual Shipment Selection
 *
 *  @author Jorg Janke
 *  @version $Id: VInOutGen.java,v 1.2 2006/07/30 00:51:28 jjanke Exp $
 */
public class VInOutGenOFB extends CPanel
	implements FormPanel, ActionListener, VetoableChangeListener, 
		ChangeListener, TableModelListener, ASyncProcess
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8529925342013824806L;

	/**
	 *	Initialize Panel
	 *  @param WindowNo window
	 *  @param frame frame
	 */
	public void init (int WindowNo, FormFrame frame)
	{
		log.info("");
		m_WindowNo = WindowNo;
		m_frame = frame;
		Env.setContext(Env.getCtx(), m_WindowNo, "IsSOTrx", "Y");
		try
		{
			fillPicks();
			jbInit();
			dynInit();
			frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
			frame.getContentPane().add(statusBar, BorderLayout.SOUTH);
		}
		catch(Exception ex)
		{
			log.log(Level.SEVERE, "init", ex);
		}
	}	//	init

	/**	Window No			*/
	private int         	m_WindowNo = 0;
	/**	FormFrame			*/
	private FormFrame 		m_frame;

	private boolean			m_selectionActive = true;
	private Object 			m_M_Warehouse_ID = null;
	private Object 			m_C_BPartner_ID = null;
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(VInOutGenOFB.class);
	//
	private CTabbedPane tabbedPane = new CTabbedPane();
	private CPanel selPanel = new CPanel();
	private CPanel selNorthPanel = new CPanel();
	private BorderLayout selPanelLayout = new BorderLayout();
	private CLabel lWarehouse = new CLabel();
	private VLookup fWarehouse;
	private CLabel lBPartner = new CLabel();
	private VLookup fBPartner;
	private FlowLayout northPanelLayout = new FlowLayout();
	private ConfirmPanel confirmPanelSel = new ConfirmPanel(true);
	private ConfirmPanel confirmPanelGen = new ConfirmPanel(false, true, false, false, false, false, true);
	private StatusBar statusBar = new StatusBar();
	private CPanel genPanel = new CPanel();
	private BorderLayout genLayout = new BorderLayout();
	private CTextPane info = new CTextPane();
	private JScrollPane scrollPane = new JScrollPane();
	private MiniTable miniTable = new MiniTable();
	
	private CLabel     lDocType = new CLabel();
	private VComboBox  cmbDocType = new VComboBox();
	private CLabel     lDocAction = new CLabel();
	private VLookup    docAction;

	/** User selection */
	private ArrayList<Integer> selection = null;
	
	/**
	 *	Static Init.
	 *  <pre>
	 *  selPanel (tabbed)
	 *      fOrg, fBPartner
	 *      scrollPane & miniTable
	 *  genPanel
	 *      info
	 *  </pre>
	 *  @throws Exception
	 */
	void jbInit() throws Exception
	{
		CompiereColor.setBackground(this);
		//
		selPanel.setLayout(selPanelLayout);
		lWarehouse.setLabelFor(fWarehouse);
		lBPartner.setLabelFor(fBPartner);
		lBPartner.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
		lDocAction.setLabelFor(docAction);
		lDocAction.setText(Msg.translate(Env.getCtx(), "DocAction"));
		selNorthPanel.setLayout(northPanelLayout);
		northPanelLayout.setAlignment(FlowLayout.LEFT);
		tabbedPane.add(selPanel, "Seleccion para Consolidado");
		selPanel.add(selNorthPanel, BorderLayout.NORTH);
		selNorthPanel.add(lWarehouse, null);
		selNorthPanel.add(fWarehouse, null);
		selNorthPanel.add(lBPartner, null);
		selNorthPanel.add(fBPartner, null);
		selPanel.setName("selPanel");
		selPanel.add(confirmPanelSel, BorderLayout.SOUTH);
		selPanel.add(scrollPane, BorderLayout.CENTER);
		scrollPane.getViewport().add(miniTable, null);
		confirmPanelSel.addActionListener(this);
		//
		tabbedPane.add(genPanel, Msg.getMsg(Env.getCtx(), "Generate"));
		genPanel.setLayout(genLayout);
		genPanel.add(info, BorderLayout.CENTER);
		genPanel.setEnabled(false);
		info.setBackground(AdempierePLAF.getFieldBackground_Inactive());
		info.setEditable(false);
		genPanel.add(confirmPanelGen, BorderLayout.SOUTH);
		confirmPanelGen.addActionListener(this);
		
		lDocType.setLabelFor(cmbDocType);
		selNorthPanel.add(lDocType, null);
		selNorthPanel.add(cmbDocType, null);
		selNorthPanel.add(lDocAction, null);
		selNorthPanel.add(docAction, null);
	}	//	jbInit

	/**
	 *	Fill Picks.
	 *		Column_ID from C_Order
	 *  @throws Exception if Lookups cannot be initialized
	 */
	private void fillPicks() throws Exception
	{
		//	C_OrderLine.M_Warehouse_ID
		MLookup orgL = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, 2223, DisplayType.TableDir);
		fWarehouse = new VLookup ("M_Warehouse_ID", true, false, true, orgL);
		lWarehouse.setText(Msg.translate(Env.getCtx(), "M_Warehouse_ID"));
		fWarehouse.addVetoableChangeListener(this);
		m_M_Warehouse_ID = fWarehouse.getValue();
		//   Document Action Prepared/ Completed
		MLookup docActionL = MLookupFactory.get(Env.getCtx(), m_WindowNo, 4324 /* M_InOut.DocStatus */, 
				DisplayType.List, Env.getLanguage(Env.getCtx()), "DocAction", 135 /* _Document Action */,
				false, "AD_Ref_List.Value IN ('CO','PR')");
		docAction = new VLookup("DocAction", true, false, true,docActionL);
		//faaguilar OFB default value
		Object[] arr=null;
		docAction.setValue(DB.getSQLValueString("AD_Client","select InOutStatus from AD_Client where AD_Client_ID="+Env.getAD_Client_ID(Env.getCtx()),arr));
		//faaguilar OFB default value END
		
		docAction.addVetoableChangeListener(this);
		//		C_Order.C_BPartner_ID
		MLookup bpL = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, 2762, DisplayType.Search);
		fBPartner = new VLookup ("C_BPartner_ID", false, false, true, bpL);
		lBPartner.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
		fBPartner.addVetoableChangeListener(this);
		//Document Type Sales Order/Vendor RMA
		lDocType.setText(Msg.translate(Env.getCtx(), "C_DocType_ID"));
		cmbDocType.addItem(new KeyNamePair(MOrder.Table_ID, Msg.translate(Env.getCtx(), "Order")));
		cmbDocType.addItem(new KeyNamePair(MRMA.Table_ID, Msg.translate(Env.getCtx(), "VendorRMA")));
		cmbDocType.addActionListener(this);
	}	//	fillPicks

	/**
	 *	Dynamic Init.
	 *	- Create GridController & Panel
	 *	- AD_Column_ID from C_Order
	 */
	private void dynInit()
	{
		//  create Columns
		miniTable.addColumn("C_Order_ID");
		miniTable.addColumn("AD_Org_ID");
		if(VLocationDialog.getZones(Env.getCtx()).length>0)//faaguilar OFB
			miniTable.addColumn("C_Zone_ID");//faaguilar OFB
		miniTable.addColumn("C_DocType_ID");
		miniTable.addColumn("DocumentNo");
		miniTable.addColumn("Description");//faaguilar OFB
		miniTable.addColumn("C_BPartner_ID");
		miniTable.addColumn("DateOrdered");
		miniTable.addColumn("Location");//faaguilar OFB
		miniTable.addColumn("TotalLines");
		//
		miniTable.setMultiSelection(true);
		miniTable.setRowSelectionAllowed(true);
		//  set details
		miniTable.setColumnClass(0, IDColumn.class, false, " ");
		miniTable.setColumnClass(1, String.class, true, Msg.translate(Env.getCtx(), "AD_Org_ID"));
		if(VLocationDialog.getZones(Env.getCtx()).length>0){//faaguilar OFB
			miniTable.setColumnClass(2, String.class, true, "Zona");//faaguilar OFB
			miniTable.setColumnClass(3, String.class, true, Msg.translate(Env.getCtx(), "C_DocType_ID"));
			miniTable.setColumnClass(4, String.class, true, Msg.translate(Env.getCtx(), "DocumentNo"));
			miniTable.setColumnClass(5, String.class, true, Msg.translate(Env.getCtx(), "Description"));//faaguilar OFB
			miniTable.setColumnClass(6, String.class, true, Msg.translate(Env.getCtx(), "C_BPartner_ID"));
			miniTable.setColumnClass(7, Timestamp.class, true, Msg.translate(Env.getCtx(), "DateOrdered"));
			miniTable.setColumnClass(8, String.class, true, Msg.translate(Env.getCtx(), "Location"));//faaguilar OFB
			miniTable.setColumnClass(9, BigDecimal.class, true, Msg.translate(Env.getCtx(), "TotalLines"));
		}else{
		miniTable.setColumnClass(2, String.class, true, Msg.translate(Env.getCtx(), "C_DocType_ID"));
		miniTable.setColumnClass(3, String.class, true, Msg.translate(Env.getCtx(), "DocumentNo"));
		miniTable.setColumnClass(4, String.class, true, Msg.translate(Env.getCtx(), "Description"));//faaguilar OFB
		miniTable.setColumnClass(5, String.class, true, Msg.translate(Env.getCtx(), "C_BPartner_ID"));
		miniTable.setColumnClass(6, Timestamp.class, true, Msg.translate(Env.getCtx(), "DateOrdered"));
		miniTable.setColumnClass(7, String.class, true, Msg.translate(Env.getCtx(), "Location"));//faaguilar OFB
		miniTable.setColumnClass(8, BigDecimal.class, true, Msg.translate(Env.getCtx(), "TotalLines"));
		}
		//
		miniTable.autoSize();
		miniTable.getModel().addTableModelListener(this);
		//	Info
		statusBar.setStatusLine(Msg.getMsg(Env.getCtx(), "InOutGenerateSel"));//@@
		statusBar.setStatusDB(" ");
		//	Tabbed Pane Listener
		tabbedPane.addChangeListener(this);
	}	//	dynInit

	/**
	 * Get SQL for Orders that needs to be shipped
	 * @return sql
	 */
	private String getOrderSQL()
	{
	//  Create SQL
        StringBuffer sql = new StringBuffer(
            "SELECT C_Order_ID, o.Name, dt.Name, ic.DocumentNo, bp.Name, DateOrdered, TotalLines, ic.Description, ic.C_BPartner_ID " //faaguilar OFB add Description , C_bPartner_ID
            + "FROM M_InOut_Candidate_v ic, AD_Org o, C_BPartner bp, C_DocType dt "
            + "WHERE ic.AD_Org_ID=o.AD_Org_ID"
            + " AND ic.C_BPartner_ID=bp.C_BPartner_ID"
            + " AND ic.C_DocType_ID=dt.C_DocType_ID"
            + " AND ic.AD_Client_ID=?");
            /*+ " AND NOT EXISTS (SELECT * FROM M_InOut i, M_InOutLine il, C_OrderLine ol"
            + " WHERE ol.C_Order_ID=ic.C_Order_ID AND  i.M_InOut_ID = il.M_InOut_ID AND"
            + " il.C_OrderLine_ID = ol.C_OrderLine_ID AND i.DocStatus IN ('IP', 'CO', 'CL')) ");*///comment by faaguilar

        if (m_M_Warehouse_ID != null)
            sql.append(" AND ic.M_Warehouse_ID=").append(m_M_Warehouse_ID);
        if (m_C_BPartner_ID != null)
            sql.append(" AND ic.C_BPartner_ID=").append(m_C_BPartner_ID);
        
        // bug - [ 1713317 ] Generate Shipments (manual) show locked records
        /* begin - Exclude locked records; @Trifon */
        int AD_User_ID = Env.getContextAsInt(Env.getCtx(), "#AD_User_ID");
        String lockedIDs = MPrivateAccess.getLockedRecordWhere(MOrder.Table_ID, AD_User_ID);
        if (lockedIDs != null)
        {
            if (sql.length() > 0)
                sql.append(" AND ");
            sql.append("C_Order_ID").append(lockedIDs);
        }
        /* eng - Exclude locked records; @Trifon */
          
        //
        sql.append(" ORDER BY o.Name,bp.Name,DateOrdered");
        
        return sql.toString();
	}
	
	/**
	 * Get SQL for Vendor RMA that need to be shipped
	 * @return sql
	 */
	private String getRMASql()
	{
	    StringBuffer sql = new StringBuffer();
	    
	    sql.append("SELECT rma.M_RMA_ID, org.Name, dt.Name, rma.DocumentNo, bp.Name, rma.Created, rma.Amt ");
	    sql.append("FROM M_RMA rma INNER JOIN AD_Org org ON rma.AD_Org_ID=org.AD_Org_ID ");
	    sql.append("INNER JOIN C_DocType dt ON rma.C_DocType_ID=dt.C_DocType_ID ");
	    sql.append("INNER JOIN C_BPartner bp ON rma.C_BPartner_ID=bp.C_BPartner_ID ");
	    sql.append("INNER JOIN M_InOut io ON rma.InOut_ID=io.M_InOut_ID ");
	    sql.append("WHERE rma.DocStatus='CO' ");
	    sql.append("AND dt.DocBaseType = 'POO' ");
	    sql.append("AND EXISTS (SELECT * FROM M_RMA r INNER JOIN M_RMALine rl ");
	    sql.append("ON r.M_RMA_ID=rl.M_RMA_ID WHERE r.M_RMA_ID=rma.M_RMA_ID ");
	    sql.append("AND rl.IsActive='Y' AND rl.M_InOutLine_ID > 0 AND rl.QtyDelivered < rl.Qty) ");
	    sql.append("AND NOT EXISTS (SELECT * FROM M_InOut oio WHERE oio.M_RMA_ID=rma.M_RMA_ID ");
	    sql.append("AND oio.DocStatus IN ('IP', 'CO', 'CL')) " );
	    sql.append("AND rma.AD_Client_ID=?");
	    
	    if (m_M_Warehouse_ID != null)
            sql.append(" AND io.M_Warehouse_ID=").append(m_M_Warehouse_ID);
        if (m_C_BPartner_ID != null)
            sql.append(" AND bp.C_BPartner_ID=").append(m_C_BPartner_ID);
        
        int AD_User_ID = Env.getContextAsInt(Env.getCtx(), "#AD_User_ID");
        String lockedIDs = MPrivateAccess.getLockedRecordWhere(MRMA.Table_ID, AD_User_ID);
        if (lockedIDs != null)
        {
            sql.append(" AND rma.M_RMA_ID").append(lockedIDs);
        }
	    
	    sql.append(" ORDER BY org.Name, bp.Name, rma.Created ");

	    return sql.toString();
	}
	
	/**
	 *  Query Info
	 */
	private void executeQuery()
	{
		log.info("");
		int AD_Client_ID = Env.getAD_Client_ID(Env.getCtx());
		
		String sql = "";
		
		KeyNamePair docTypeKNPair = (KeyNamePair)cmbDocType.getSelectedItem();
		
		if (docTypeKNPair.getKey() == MRMA.Table_ID)
		{
		    sql = getRMASql();
		}
		else
		{
		    sql = getOrderSQL();
		}

		log.fine(sql);
		//  reset table
		int row = 0;
		miniTable.setRowCount(row);
		//  Execute
		try
		{
			PreparedStatement pstmt = DB.prepareStatement(sql.toString(), null);
			pstmt.setInt(1, AD_Client_ID);
			ResultSet rs = pstmt.executeQuery();
			//
			while (rs.next())
			{
				//faaguilar OFb get Location
				int Zone_ID=0;
				String location=DB.getSQLValueString(null,"select name from C_BPARTNER_LOCATION where ISSHIPTO='Y' and C_BPartner_ID=?",rs.getInt(9));
				int Location_ID=DB.getSQLValue(null, "select C_Location_ID from C_BPARTNER_LOCATION where ISSHIPTO='Y' and C_BPartner_ID=?",rs.getInt(9));
				if(VLocationDialog.getZones(Env.getCtx()).length>0)
				  Zone_ID=DB.getSQLValue(null, "select C_Zone_ID from C_Location where C_Location_ID="+Location_ID);
				
				int column=0;
				//end faaguilar OFb get Location
				//  extend table
				miniTable.setRowCount(row+1);
				//  set values
				miniTable.setValueAt(new IDColumn(rs.getInt(1)), row, column);   //  C_Order_ID
				
				miniTable.setValueAt(rs.getString(2), row, ++column);              //  Org
				if(VLocationDialog.getZones(Env.getCtx()).length>0){//faaguilar OFB zones BEGIN
					if(Zone_ID>0){//faaguilar OFb Zones
						miniTable.setValueAt(new MRefList (Env.getCtx(), Zone_ID, null).getName(), row, ++column); //faaguilar OFb Zones
					}
					else
					{
						miniTable.setValueAt(new String(""), row, ++column);
					}
				}//faaguilar OFB zones END
				
				miniTable.setValueAt(rs.getString(3), row, ++column);              //  DocType
				miniTable.setValueAt(rs.getString(4), row, ++column);              //  Doc No
				//faaguilar OFB length Validation
				if(rs.getString(8)!=null)
				{
					if(rs.getString(8).length()>80)
					miniTable.setValueAt(rs.getString(8).substring(0,79), row, ++column); //  Description
					else
					miniTable.setValueAt(rs.getString(8), row, ++column);              //  Description
				}
				else
				miniTable.setValueAt("", row, ++column);  
				//END faaguilar OFB length Validation
				miniTable.setValueAt(rs.getString(5), row, ++column);              //  BPartner
				miniTable.setValueAt(rs.getTimestamp(6), row, ++column);           //  DateOrdered
				miniTable.setValueAt(location, row, ++column);           //  Location faaguilar OFB
				miniTable.setValueAt(rs.getBigDecimal(7), row, ++column);          //  TotalLines
				//  prepare next
				row++;
			}
			rs.close();
			pstmt.close();
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql.toString(), e);
		}
		//
		miniTable.autoSize();
	//	statusBar.setStatusDB(String.valueOf(miniTable.getRowCount()));
	}   //  executeQuery

	/**
	 * 	Dispose
	 */
	public void dispose()
	{
		if (m_frame != null)
			m_frame.dispose();
		m_frame = null;
	}	//	dispose

	/**
	 *	Action Listener
	 *  @param e event
	 */
	public void actionPerformed (ActionEvent e)
	{
		log.info("Cmd=" + e.getActionCommand());
		//
		if (e.getActionCommand().equals(ConfirmPanel.A_CANCEL))
		{
			dispose();
			return;
		}
		if (cmbDocType.equals(e.getSource()))
		{
		    executeQuery();
		    return;
		}
		//
		saveSelection();
		if (selection != null
			&& selection.size() > 0
			&& m_selectionActive	//	on selection tab
			&& m_M_Warehouse_ID != null)
			generateShipments ();
		else
			dispose();
	}	//	actionPerformed

	/**
	 *	Vetoable Change Listener - requery
	 *  @param e event
	 */
	public void vetoableChange(PropertyChangeEvent e)
	{
		log.info(e.getPropertyName() + "=" + e.getNewValue());
		if (e.getPropertyName().equals("M_Warehouse_ID"))
			m_M_Warehouse_ID = e.getNewValue();
		if (e.getPropertyName().equals("C_BPartner_ID"))
		{
			m_C_BPartner_ID = e.getNewValue();
			fBPartner.setValue(m_C_BPartner_ID);	//	display value
		}
		executeQuery();
	}	//	vetoableChange

	/**
	 *	Change Listener (Tab changed)
	 *  @param e event
	 */
	public void stateChanged (ChangeEvent e)
	{
		int index = tabbedPane.getSelectedIndex();
		m_selectionActive = (index == 0);
	}	//	stateChanged

	/**
	 *  Table Model Listener
	 *  @param e event
	 */
	public void tableChanged(TableModelEvent e)
	{
		int rowsSelected = 0;
		int rows = miniTable.getRowCount();
		for (int i = 0; i < rows; i++)
		{
			IDColumn id = (IDColumn)miniTable.getValueAt(i, 0);     //  ID in column 0
			if (id != null && id.isSelected())
				rowsSelected++;
		}
		statusBar.setStatusDB(" " + rowsSelected + " ");
	}   //  tableChanged

	/**
	 *	Save Selection & return selecion Query or ""
	 *  @return where clause like C_Order_ID IN (...)
	 */
	private void saveSelection()
	{
		log.info("");
		//  ID selection may be pending
		miniTable.editingStopped(new ChangeEvent(this));
		//  Array of Integers
		ArrayList<Integer> results = new ArrayList<Integer>();
		selection = null;

		//	Get selected entries
		int rows = miniTable.getRowCount();
		for (int i = 0; i < rows; i++)
		{
			IDColumn id = (IDColumn)miniTable.getValueAt(i, 0);     //  ID in column 0
		//	log.fine( "Row=" + i + " - " + id);
			if (id != null && id.isSelected())
				results.add(id.getRecord_ID());
		}

		if (results.size() == 0)
			return;
		log.config("Selected #" + results.size());
		selection = results;
		
	}	//	saveSelection

	
	/**************************************************************************
	 *	Generate Shipments
	 */
	private void generateShipments ()
	{
		log.info("M_Warehouse_ID=" + m_M_Warehouse_ID);
		String trxName = Trx.createTrxName("IOG");	
		Trx trx = Trx.get(trxName, true);	//trx needs to be committed too
		//String trxName = null;
		//Trx trx = null;
		
		m_selectionActive = false;  //  prevents from being called twice
		statusBar.setStatusLine(Msg.getMsg(Env.getCtx(), "InOutGenerateGen"));
		statusBar.setStatusDB(String.valueOf(selection.size()));

		//	Prepare Process
		int AD_Process_ID = 0;	  
       
        AD_Process_ID = DB.getSQLValue(trx.getTrxName(), "select AD_Process_ID from AD_Process where Name='InOutGenerateOFB'");
        if(AD_Process_ID<=0){
        	String msg = "No Process InOutGenerateOFB";  
			info.setText(msg);
			log.log(Level.SEVERE, msg);
			return;
        }
        
		
		MPInstance instance = new MPInstance(Env.getCtx(), AD_Process_ID, 0);
		if (!instance.save())
		{
			info.setText(Msg.getMsg(Env.getCtx(), "ProcessNoInstance"));
			return;
		}
		
		//insert selection
		StringBuffer insert = new StringBuffer();
		insert.append("INSERT INTO T_SELECTION(AD_PINSTANCE_ID, T_SELECTION_ID) ");
		int counter = 0;
		for(Integer selectedId : selection)
		{
			counter++;
			if (counter > 1)
				insert.append(" UNION ");
			insert.append("SELECT ");
			insert.append(instance.getAD_PInstance_ID());
			insert.append(", ");
			insert.append(selectedId);
			insert.append(" FROM DUAL ");
			
			if (counter == 1000) 
			{
				if ( DB.executeUpdate(insert.toString(), trxName) < 0 )
				{
					String msg = "No Shipments";     //  not translated!
					log.config(msg);
					info.setText(msg);
					trx.rollback();
					return;
				}
				insert = new StringBuffer();
				insert.append("INSERT INTO T_SELECTION(AD_PINSTANCE_ID, T_SELECTION_ID) ");
				counter = 0;
			}
		}
		if (counter > 0)
		{
			if ( DB.executeUpdate(insert.toString(), trxName) < 0 )
			{
				String msg = "No Shipments";     //  not translated!
				log.config(msg);
				info.setText(msg);
				trx.rollback();
				return;
			}
		}
		
		//call process
		ProcessInfo pi = new ProcessInfo ("VInOutGen", AD_Process_ID);
		pi.setAD_PInstance_ID (instance.getAD_PInstance_ID());

		//	Add Parameter - Selection=Y
		MPInstancePara ip = new MPInstancePara(instance, 10);
		ip.setParameter("Selection","Y");
		if (!ip.save())
		{
			String msg = "No Parameter added";  //  not translated
			info.setText(msg);
			log.log(Level.SEVERE, msg);
			return;
		}
		//Add Document action parameter
		ip = new MPInstancePara(instance, 20);
		String docActionSelected = (String)docAction.getValue();
		ip.setParameter("DocAction", docActionSelected);
		if(!ip.save())
		{
			String msg = "No DocACtion Parameter added";
			info.setText(msg);
			log.log(Level.SEVERE, msg);
			return;
		}
		//	Add Parameter - M_Warehouse_ID=x
		ip = new MPInstancePara(instance, 30);
		ip.setParameter("M_Warehouse_ID", Integer.parseInt(m_M_Warehouse_ID.toString()));
		if(!ip.save())
		{
			String msg = "No Parameter added";  //  not translated
			info.setText(msg);
			log.log(Level.SEVERE, msg);
			return;
		}

		//	Execute Process
		ProcessCtl worker = new ProcessCtl(this, Env.getWindowNo(this), pi, trx);
		worker.start();     //  complete tasks in unlockUI / generateShipments_complete
		//
	}	//	generateShipments

	/**
	 *  Complete generating shipments.
	 *  Called from Unlock UI
	 *  @param pi process info
	 */
	private void generateShipments_complete (ProcessInfo pi)
	{
		//  Switch Tabs
		tabbedPane.setSelectedIndex(1);
		//
		ProcessInfoUtil.setLogFromDB(pi);
		StringBuffer iText = new StringBuffer();
		iText.append("<b>").append(pi.getSummary())
			.append("</b><br>(")
			.append(Msg.getMsg(Env.getCtx(), "InOutGenerateInfo"))
			//  Shipments are generated depending on the Delivery Rule selection in the Order
			.append(")<br>")
			.append(pi.getLogInfo(true));
		info.setText(iText.toString());

		//	Reset Selection
		/*
		String sql = "UPDATE C_Order SET IsSelected='N' WHERE " + m_whereClause;
		int no = DB.executeUpdate(sql, null);
		log.config("Reset=" + no);*/

		//	Get results
		int[] ids = pi.getIDs();
		if (ids == null || ids.length == 0)
			return;
		log.config("PrintItems=" + ids.length);

		confirmPanelGen.getOKButton().setEnabled(false);
		
		// faaguilar OFB begin
		boolean print = false;
		for (int i = 0; i < ids.length; i++)
		{
			if(ids[i]!=0)
				print = true;
		}
		// faaguilar OFB end
		if(print)// faaguilar OFB
		//	OK to print shipments
			if (ADialog.ask(m_WindowNo, this, "PrintShipments"))
			{
			//	info.append("\n\n" + Msg.getMsg(Env.getCtx(), "PrintShipments"));
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				int retValue = ADialogDialog.A_CANCEL;	//	see also ProcessDialog.printShipments/Invoices
				do
				{
					//	Loop through all items
					for (int i = 0; i < ids.length; i++)
					{
						int M_InOut_ID = ids[i];
						if(M_InOut_ID!=0)// faaguilar OFB end
						ReportCtl.startDocumentPrint(ReportEngine.SHIPMENT, M_InOut_ID, this, Env.getWindowNo(this), true);
					}
					ADialogDialog d = new ADialogDialog (m_frame,
						Env.getHeader(Env.getCtx(), m_WindowNo),
						Msg.getMsg(Env.getCtx(), "PrintoutOK?"),
						JOptionPane.QUESTION_MESSAGE);
					retValue = d.getReturnCode();
				}
				while (retValue == ADialogDialog.A_CANCEL);
				setCursor(Cursor.getDefaultCursor());
			}	//	OK to print shipments
		
		//
		confirmPanelGen.getOKButton().setEnabled(true);
	}   //  generateShipments_complete

	
	/**************************************************************************
	 *  Lock User Interface.
	 *  Called from the Worker before processing
	 *  @param pi process info
	 */
	public void lockUI (ProcessInfo pi)
	{
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		this.setEnabled(false);
	}   //  lockUI

	/**
	 *  Unlock User Interface.
	 *  Called from the Worker when processing is done
	 *  @param pi result of execute ASync call
	 */
	public void unlockUI (ProcessInfo pi)
	{
		this.setEnabled(true);
		this.setCursor(Cursor.getDefaultCursor());
		//
		generateShipments_complete(pi);
	}   //  unlockUI

	/**
	 *  Is the UI locked (Internal method)
	 *  @return true, if UI is locked
	 */
	public boolean isUILocked()
	{
		return this.isEnabled();
	}   //  isUILocked

	/**
	 *  Method to be executed async.
	 *  Called from the Worker
	 *  @param pi ProcessInfo
	 */
	public void executeASync (ProcessInfo pi)
	{
	}   //  executeASync

}	//	VInOutGen
