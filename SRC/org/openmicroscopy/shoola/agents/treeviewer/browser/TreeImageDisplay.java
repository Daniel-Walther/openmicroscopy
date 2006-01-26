/*
 * org.openmicroscopy.shoola.agents.treemng.browser.TreeImageDisplay
 *
 *------------------------------------------------------------------------------
 *
 *  Copyright (C) 2004 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *------------------------------------------------------------------------------
 */

package org.openmicroscopy.shoola.agents.treeviewer.browser;




//Java imports
import java.awt.Color;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.tree.DefaultMutableTreeNode;



//Third-party libraries

//Application-internal dependencies
import pojos.CategoryData;
import pojos.CategoryGroupData;
import pojos.DatasetData;
import pojos.ImageData;
import pojos.ProjectData;

/** 
 * Represents a component in the composite structure used to visualize an
 * image hierarchy. 
 * <p>A concrete component can be either a {@link TreeImageNode}, to represent a
 * single image, or an {@link TreeImageSet}, to represent a collection of 
 * images. An {@link TreeImageSet} can also contain other image sets, thus 
 * leading to a composite structure. This is a tree whose leaf nodes are 
 * {@link TreeImageNode} objects and internal nodes are {@link TreeImageSet} 
 * objects.</p>
 * <p>So we have a general purpose, set-based structure we can use to visualize 
 * any image hierarchy: Project/Dataset/Image, Category Group/Category/Image, 
 * or Screen/Plate/Well/Image. The original data hierarchy translates into
 * a visualization tree as follows. Each image object corresponds to an
 * {@link TreeImageNode} and an image container, such as Dataset or Category, 
 * corresponds to an {@link TreeImageSet}. All {@link TreeImageNode} objects
 * that are created for the images in a given image container are added to the 
 * {@link TreeImageSet} object created for that image container. Nested
 * containers translate into nested {@link TreeImageSet}s. For example, say you 
 * have a Project <code>p_1</code> and two datasets in it, <code>d_1</code> 
 * and <code>d_2</code>. The former contains image <code>i_1</code> and 
 * <code>i_2</code>, as the latter only has one image, <code>i_3</code>. 
 * This would translate into three {@link TreeImageNode}s 
 * <code>in_1, in_2, in_3</code>, respectively for <code>i_1, i_2, i_3</code>.
 * You would then create two {@link TreeImageSet}s <code>ds_1, ds_2</code>,
 * respectively for <code>d_1, d_2</code>, and add <code>in_1, in_2</code> to 
 * <code>ds_1</code> and <code>in_3</code> to <code> ds_2</code>. Finally you
 * would create a third {@link TreeImageSet} <code>ps_1</code> for the Project 
 * and add <code>ds_1, ds_2</code> to it.</p> 
 * <p>Operations on a visualization tree are performed through visitors.  The
 * {@link TreeImageDisplayVisitor} interface allows you to define arbitrary 
 * operations that can then be applied to the tree by calling the 
 * {@link #accept(TreeImageDisplayVisitor) accept} method, usually on the root 
 * node.
 * An example of this is layout management. In fact, an {@link TreeImageSet} can
 * contain other nodes &#151; this class inherits from 
 * {@link DefaultMutableTreeNode} and nodes are added to its internal desktop,
 * which has no layout manager. In order to position the contained nodes
 * properly, you can write a layout class that implements the 
 * {@link TreeImageDisplayVisitor} interface to lay out the contents of
 * every {@link TreeImageSet} node in a visualization tree.</p>
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @version 2.2
 * <small>
 * (<b>Internal version:</b> $Revision$ $Date$)
 * </small>
 * @since OME2.2
 */
public abstract class TreeImageDisplay
    extends DefaultMutableTreeNode
{
    
    /** The gap between the number of items and the name. */
    private static final String SPACE = "    ";
    
    /** 
     * Back pointer to the parent node or <code>null</code> if this is the root.
     */
    private TreeImageDisplay    parentDisplay;
    
    /** 
     * The set of nodes that have been added to this node.
     * Will always be empty for a leaf node. 
     */
    private Set                 childrenDisplay;
    
    /**
     * The tooltip: annotation if the <code>DataObject</code>
     * can be annotated and the inserted date if any.
     */
    private String              tooltip;
    
    /** 
     * Tells if the node has to be highlighted.
     * If <code>null</code>, the node will display the normal
     * background. If a color is specified, the node will be highlighted
     * using the specified color. 
     */
    private Color				highlight;
    
    /** The number of items. */
    protected int				numberItems;
    
    /**
     * Checks if the algorithm to visit the tree is one of the constants
     * defined by {@link TreeImageDisplayVisitor}.
     * 
     * @param type The algorithm type.
     * @return  Returns <code>true</code> if the type is supported,
     *          <code>false</code> otherwise.
     */
    private boolean checkAlgoType(int type)
    {
        switch (type) {
            case TreeImageDisplayVisitor.TREEIMAGE_NODE_ONLY:
            case TreeImageDisplayVisitor.TREEIMAGE_SET_ONLY:    
            case TreeImageDisplayVisitor.ALL_NODES:
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Constructor used by subclasses.
     * 
     * @param hierarchyObject The original object in the image hierarchy which
     *                        is visualized by this node.  
     *                        Never pass <code>null</code>. 
     */
    protected TreeImageDisplay(Object hierarchyObject)
    {
        super();
        if (hierarchyObject == null) 
            throw new NullPointerException("No hierarchy object.");
        setUserObject(hierarchyObject);
        childrenDisplay = new HashSet();
        numberItems = -1;
    }
    
    /**
     * Returns the parent node to this node in the visualization tree.
     * 
     * @return The parent node or <code>null</code> if this node has no parent.
     *          This can happen if this node hasn't been linked yet or if it's
     *          the root node.
     */
    public TreeImageDisplay getParentDisplay() { return parentDisplay; }
    
    /**
     * Returns all the child nodes to this node in the visualization tree.
     * Note that, although never <code>null</code>, the returned set may be
     * empty.  In particular, this is always the case for a leaf node &#151;
     * that is an {@link TreeImageNode}.
     * 
     * @return A <i>read-only</i> set containing all the child nodes.
     */
    public Set getChildrenDisplay() 
    { 
        return Collections.unmodifiableSet(childrenDisplay);
    }
    
    /**
     * Returns <code>true</code> if the nodes contain children,
     * <code>false</code> otherwise.
     * 
     * @return See above.
     */
    public boolean hasChildrenDisplay()
    { 
        return (childrenDisplay.size() != 0);
    }
    
    /**
     * Adds a node to the visualization tree as a child of this node.
     * The node is added to the internal desktop of this node, but you
     * will have to set its bounds for it to show up &#151; this is a
     * consequence of the fact that a desktop has no layout manager.
     * The <code>child</code>'s parent is set to be this node.  If <code>
     * child</code> is currently a child to another node <code>n</code>, 
     * then <code>child</code> is first 
     * {@link #removeChildDisplay(TreeImageDisplay) removed} from <code>n</code>
     * and then added to this node. 
     * 
     * @param child The node to add. Mustn't be <code>null</code>.
     * @see DefaultMutableTreeNode
     */
    public void addChildDisplay(TreeImageDisplay child)
    {
        if (child == null) throw new NullPointerException("No child.");
        if (childrenDisplay.contains(child)) return;
        if (child.parentDisplay != null)  //Was the child of another node.
            child.parentDisplay.removeChildDisplay(child);
        child.parentDisplay = this;
        childrenDisplay.add(child);
        numberItems = childrenDisplay.size();
    }
    
    /**
     * Removes the specified <code>child</code> node.
     * If <code>child</code> is not among the children of this node, no action
     * is taken. Otherwise, it is removed from the children set and orphaned.
     * That is, its parent (which is this node) is set to <code>null</code>.
     * 
     * @param child The node to remove. Mustn't be <code>null</code>.
     */
    public void removeChildDisplay(TreeImageDisplay child)
    {
        if (child == null) throw new NullPointerException("No child.");
        if (childrenDisplay.contains(child)) {
            //NOTE: parentDisplay != null b/c child has been added through
            //the add method.
            child.parentDisplay.childrenDisplay.remove(child);
            child.parentDisplay = null;
            numberItems = childrenDisplay.size();
        }
    }
    
    /** Removes all <code>children</code> nodes from the children set. */
    public void removeAllChildrenDisplay()
    {
        Iterator i = childrenDisplay.iterator();
        Set toRemove = new HashSet(childrenDisplay.size());
        while (i.hasNext())
            toRemove.add(i.next());
        i = toRemove.iterator();
        while (i.hasNext())
            removeChildDisplay((TreeImageDisplay) i.next());
    }
    
    /**
     * Has the specified object visit this node and all nodes below this one
     * in the visualization tree.
     * For each node, the <code>visit</code> method is called passing in the
     * node being visited.
     * 
     * @param visitor The visitor. Mustn't be <code>null</code>.
     * @see TreeImageDisplayVisitor
     */
    public void accept(TreeImageDisplayVisitor visitor)
    {
        if (visitor == null) throw new NullPointerException("No visitor.");
        accept(visitor, TreeImageDisplayVisitor.ALL_NODES);
    }
    
    /**
     * Has the specified object visit this node and all nodes below this one
     * in the visualization tree.
     * According to the specified <code>algoType</code>,
     * the <code>visit</code> method is called passing in the
     * node being visited. 
     * 
     * @param visitor   The visitor. Mustn't be <code>null</code>.
     * @param algoType  The algorithm selected. Must be one of the constants
     *                  defined by {@link TreeImageDisplayVisitor}.
     * @see TreeImageDisplayVisitor
     */
    public void accept(TreeImageDisplayVisitor visitor, int algoType)
    {
        if (visitor == null) throw new NullPointerException("No visitor.");
        if (!checkAlgoType(algoType))
            throw new IllegalArgumentException("Algorithm not supported.");
        Iterator i = childrenDisplay.iterator();
        TreeImageDisplay child;
        switch (algoType) {
            case TreeImageDisplayVisitor.TREEIMAGE_NODE_ONLY:
                while (i.hasNext()) {
                    child = (TreeImageDisplay) i.next();
                    child.accept(visitor, algoType);
                }
                if (this instanceof TreeImageNode) doAccept(visitor);
                break;
            case TreeImageDisplayVisitor.TREEIMAGE_SET_ONLY:
                while (i.hasNext()) {
                    child = (TreeImageDisplay) i.next();
                    if (child instanceof TreeImageSet)
                        child.accept(visitor, algoType);
                }
                if (this instanceof TreeImageSet) doAccept(visitor);
                break;
            case TreeImageDisplayVisitor.ALL_NODES:
                while (i.hasNext()) {
                    child = (TreeImageDisplay) i.next();
                    child.accept(visitor, algoType);
                }
                doAccept(visitor);
                break;
        }
    }
    
    /**
     * Sets the text displayed in a tool tip.
     * 
     * @param tooltip The text to set.
     */
    public void setToolTip(String tooltip) { this.tooltip = tooltip; }
    
    /**
     * Returns the text displayed in a tool tip.
     * 
     * @return See above.
     */
    public String getToolTip() { return tooltip; }
    
    /**
     * Returns the name of the node.
     * 
     * @return See above.
     */
    public String getNodeName()
    { 
        Object obj = getUserObject();
        if (obj instanceof ProjectData) return ((ProjectData) obj).getName();
        else if (obj instanceof DatasetData) 
            return ((DatasetData) obj).getName();
        else if (obj instanceof ImageData) 
            return ((ImageData) obj).getName();
        else if (obj instanceof CategoryGroupData) 
            return ((CategoryGroupData) obj).getName();
        else if (obj instanceof CategoryData) 
            return ((CategoryData) obj).getName();
        else if (obj instanceof String) return (String) obj;
        return "";
    }
    
    /**
     * Returns the name of the node and the number of items
     * in the specified containers.
     * 
     * @return See above.
     */
    public String getNodeText()
    {
        String name = getNodeName();
        if (getUserObject() instanceof ImageData) return name;
        if (numberItems == -1) return (name+SPACE+"...");
        String s = "item";
        if (numberItems > 1) s +="s";
        return (name+SPACE+numberItems+" "+s);
    }
    
    /**
     * Sets the highlight color. 
     * 
     * @param highlight The color to set.
     */
    public void setHighLight(Color highlight) { this.highlight = highlight; }
    
    /**
     * Returns the highlight color.
     * 
     * @return See above.
     */
    public Color getHighLight() { return highlight; }
    
    /** 
     * Overriden to return the name of the hierarchy object. 
     * @see #toString()
     */
    public String toString() { return getNodeName(); }
    
    /**
     * Overriden to make sure that the userObject is not <code>null</code>.
     */
    public void setUserObject(Object userObject)
    {
        if (userObject == null)
            throw new NullPointerException("No userObject.");
        super.setUserObject(userObject);
    }
    
    /**
     * Made final to ensure objects are compared by reference so that the
     * {@link #addChildDisplay(TreeImageDisplay) addChildDisplay} and
     * {@link #removeChildDisplay(TreeImageDisplay) removeChildDisplay} methods
     * will work fine.
     * @see #equals(Object)
     */
    public final boolean equals(Object x) { return (this == x); }
    
    /**
     * Implemented by subclasses to call the right version of the <code>visit
     * </code> method on the specified <code>visitor</code>.
     * This method is called by {@link #accept(TreeImageDisplayVisitor)} during
     * the nodes iteration.  Subclasses will just call the <code>visit</code>
     * method passing a reference to <code>this</code>.
     * 
     * @param visitor The visitor.  Will never be <code>null</code>.
     */
    protected abstract void doAccept(TreeImageDisplayVisitor visitor);
    
    /**
     * Tells if the children of this node are {@link TreeImageNode}s.
     * 
     * @return <code>true</code> if there's at least one {@link TreeImageNode} 
     *          child, <code>false</code> otherwise.
     */
    public abstract boolean containsImages();
    
}
