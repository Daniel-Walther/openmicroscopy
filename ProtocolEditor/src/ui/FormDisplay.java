/*
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2007 University of Dundee. All rights reserved.
 *
 *
 * 	This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 *	author Will Moore will@lifesci.dundee.ac.uk
 */

package ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;

import tree.DataFieldNode;
import ui.formFields.FormField;
import ui.formFields.FormFieldContainer;

import java.awt.BorderLayout;
import java.util.ArrayList;

// this panel displays the hierarchical tree, made from (FormField) JPanels
// uses recursive buildFormTree() method, indenting children each time

public class FormDisplay extends JPanel {
	
	private XMLView parentXMLView;
	
	protected static int childLeftIndent = 40;
	
	FormFieldContainer verticalFormBox;
	
	DataFieldNode rootNode;
	
	FormDisplay(XMLView parent) {
		
		parentXMLView = parent;
		rootNode = parentXMLView.getRootNode();
		
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		verticalFormBox = new FormFieldContainer();
		this.add(verticalFormBox, BorderLayout.NORTH);
				
		// get the formField JPanel from the dataField
		if (rootNode != null) {
			
			JPanel newFormField = parentXMLView.getRootNode().getFormField();
			FormField formField = (FormField)newFormField;
			formField.refreshRootField(true);	// displays the correct buttons etc. 
			verticalFormBox.add(newFormField);
			
			// pass the node and the Box that already contains it to buildFormTree()
			// this will get the nodes children and add them to the Box (within a new Box)
			buildFormTree(parentXMLView.getRootNode(), formField, verticalFormBox);
		}
	}
	
	FormDisplay(DataFieldNode rootNode) {
		
		this.rootNode = rootNode;
		
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		verticalFormBox = new FormFieldContainer();
		this.add(verticalFormBox, BorderLayout.NORTH);
				
		// get the formField JPanel from the dataField
		if (rootNode != null) {
			JPanel newFormField = rootNode.getFormField();
			FormField formField = (FormField)newFormField;
			formField.refreshRootField(true);	// displays the correct buttons etc. 
			verticalFormBox.add(newFormField);
			
			// pass the node and the Box that already contains it to buildFormTree()
			// this will get the nodes children and add them to the Box (within a new Box)
			buildFormTree(rootNode, formField, verticalFormBox);
		}
	}
	
//	 this will get the node's children and add them to the Box (within a new Box)
	// the Panel of dfNode has already been added at the top of verticalBox
	public static void buildFormTree(DataFieldNode dfNode, FormField formField, Box verticalBox) {
		
		ArrayList<DataFieldNode> children = dfNode.getChildren();
		
		// every field gets a childBox, even though it may not have any children
		FormFieldContainer childBox = new FormFieldContainer();
		childBox.setBorder(BorderFactory.createEmptyBorder(0, childLeftIndent, 0, 0));
		// the node gets a ref to the Box (used for collapsing. Box becomes hidden)
		formField.setChildContainer(childBox);
		
		//System.out.println("FormDisplay: buildFormTree() " + dfNode.getDataField().getName());
		
		boolean subStepsCollapsed = formField.subStepsCollapsed();
		
		if (!subStepsCollapsed) {
			// add the children to the childBox - this will recursively build tree for each
			showChildren(children, childBox);
		}
		// add the new childBox to it's parent
		verticalBox.add(childBox);
		
//		set visibility of the childBox wrt collapsed boolean of dataField
		//	 & sets collapse button visible if dataFieldNode has children
		formField.refreshTitleCollapsed();
		
	}
	
	public static void showChildren(ArrayList<DataFieldNode> children, Box childBox) {
		
		//System.out.println("	showChildren()");
		
		// for each child, get their JPanel, add it to the childBox
		for (DataFieldNode child: children){
			JPanel newFormField = child.getFormField();
			FormField fField = (FormField)newFormField;
			childBox.add(newFormField);
			// recursively build the tree below each child
			buildFormTree(child, fField, childBox);
		}
	}
	
	public void refreshForm() {
		// update reference to the root
		rootNode = parentXMLView.getRootNode();
		
		refreshUI();
		
	}
	
	public void refreshForm(DataFieldNode rootNode) {
		
		this.rootNode = rootNode;
		
		refreshUI();
	}
	
	
	public void refreshUI() {
		
		if (rootNode == null) return;
		
		verticalFormBox.setVisible(false);	// otherwise if the new form is smaller, old one still visible
		
		this.remove(verticalFormBox);
		
		verticalFormBox = new FormFieldContainer();
		
		JPanel newFormField = rootNode.getDataField().getFormField();
		FormField formField = (FormField)newFormField;
		formField.refreshRootField(true);	// displays the correct buttons etc. 
		verticalFormBox.add(newFormField);
		
		buildFormTree(rootNode, formField, verticalFormBox);
		
		this.add(verticalFormBox, BorderLayout.NORTH);
		this.getParent().getParent().validate();
		this.invalidate();
		this.repaint();		
	}
	
}

