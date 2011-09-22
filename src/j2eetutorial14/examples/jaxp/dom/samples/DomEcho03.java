/*
 * Copyright (c) 2006 Sun Microsystems, Inc.  All rights reserved.  U.S.
 * Government Rights - Commercial software.  Government users are subject
 * to the Sun Microsystems, Inc. standard license agreement and
 * applicable provisions of the FAR and its supplements.  Use is subject
 * to license terms.
 *
 * This distribution may include materials developed by third parties.
 * Sun, Sun Microsystems, the Sun logo, Java and J2EE are trademarks
 * or registered trademarks of Sun Microsystems, Inc. in the U.S. and
 * other countries.
 *
 * Copyright (c) 2006 Sun Microsystems, Inc. Tous droits reserves.
 *
 * Droits du gouvernement americain, utilisateurs gouvernementaux - logiciel
 * commercial. Les utilisateurs gouvernementaux sont soumis au contrat de
 * licence standard de Sun Microsystems, Inc., ainsi qu'aux dispositions
 * en vigueur de la FAR (Federal Acquisition Regulations) et des
 * supplements a celles-ci.  Distribue par des licences qui en
 * restreignent l'utilisation.
 *
 * Cette distribution peut comprendre des composants developpes par des
 * tierces parties. Sun, Sun Microsystems, le logo Sun, Java et J2EE
 * sont des marques de fabrique ou des marques deposees de Sun
 * Microsystems, Inc. aux Etats-Unis et dans d'autres pays.
 */


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import java.io.File;
import java.io.IOException;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

// Basic GUI components
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

// GUI components for right-hand side
import javax.swing.JSplitPane;
import javax.swing.JEditorPane;

// GUI support classes
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

// For creating borders
import javax.swing.border.EmptyBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;

// For creating a TreeModel
import javax.swing.tree.*;
import javax.swing.event.*;
import java.util.*;


public class DomEcho03 extends JPanel {
    // Global value so it can be ref'd by the tree-adapter
    static Document document;
    static final int windowHeight = 460;
    static final int leftWidth = 300;
    static final int rightWidth = 340;
    static final int windowWidth = leftWidth + rightWidth;

    // An array of names for DOM node-types
    // (Array indexes = nodeType() values.)
    static final String[] typeName =
        {
            "none", "Element", "Attr", "Text", "CDATA", "EntityRef", "Entity",
            "ProcInstr", "Comment", "Document", "DocType", "DocFragment",
            "Notation",
        };
    static final int ELEMENT_TYPE = Node.ELEMENT_NODE;

    // The list of elements to display in the tree
    // Could set this with a command-line argument, but
    // not much point -- the list of tree elements still
    // has to be defined internally.
    // Extra credit: Read the list from a file
    // Super-extra credit: Process a DTD and build the list.
    static String[] treeElementNames =
        {
            "slideshow", "slide", "title", // For slideshow #1
            "slide-title", // For slideshow #10
            "item",
        };
    boolean compress = true;

    public DomEcho03() {
        // Make a nice border
        EmptyBorder eb = new EmptyBorder(5, 5, 5, 5);
        BevelBorder bb = new BevelBorder(BevelBorder.LOWERED);
        CompoundBorder cb = new CompoundBorder(eb, bb);
        this.setBorder(new CompoundBorder(cb, eb));

        // Set up the tree
        JTree tree = new JTree(new DomToTreeModelAdapter());

        // Iterate over the tree and make nodes visible
        // (Otherwise, the tree shows up fully collapsed)
        //TreePath nodePath = ???;
        //  tree.expandPath(nodePath); 
        JScrollPane treeView = new JScrollPane(tree);
        treeView.setPreferredSize(new Dimension(leftWidth, windowHeight));

        // Build right-side view
        JEditorPane htmlPane = new JEditorPane("text/html", "");
        htmlPane.setEditable(false);

        JScrollPane htmlView = new JScrollPane(htmlPane);
        htmlView.setPreferredSize(new Dimension(rightWidth, windowHeight));

        // Build split-pane view
        JSplitPane splitPane =
            new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeView, htmlView);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerLocation(leftWidth);
        splitPane.setPreferredSize(new Dimension(windowWidth + 10,
                windowHeight + 10));

        // Add GUI components
        this.setLayout(new BorderLayout());
        this.add("Center", splitPane);
    } // constructor

    public static void main(String[] argv) {
        if (argv.length != 1) {
            System.err.println("Usage: java DomEcho filename");
            System.exit(1);
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //factory.setValidating(true);   
        //factory.setNamespaceAware(true);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new File(argv[0]));
            makeFrame();
        } catch (SAXParseException spe) {
            // Error generated by the parser
            System.out.println("\n** Parsing error" + ", line " +
                spe.getLineNumber() + ", uri " + spe.getSystemId());
            System.out.println("   " + spe.getMessage());

            // Use the contained exception, if any
            Exception x = spe;

            if (spe.getException() != null) {
                x = spe.getException();
            }

            x.printStackTrace();
        } catch (SAXException sxe) {
            // Error generated during parsing)
            Exception x = sxe;

            if (sxe.getException() != null) {
                x = sxe.getException();
            }

            x.printStackTrace();
        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();
        } catch (IOException ioe) {
            // I/O error
            ioe.printStackTrace();
        }
    } // main

    public static void makeFrame() {
        // Set up a GUI framework
        JFrame frame = new JFrame("DOM Echo");
        frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });

        // Set up the tree, the views, and display it all
        final DomEcho03 echoPanel = new DomEcho03();
        frame.getContentPane()
             .add("Center", echoPanel);
        frame.pack();

        Dimension screenSize = Toolkit.getDefaultToolkit()
                                      .getScreenSize();
        int w = windowWidth + 10;
        int h = windowHeight + 10;
        frame.setLocation((screenSize.width / 3) - (w / 2),
            (screenSize.height / 2) - (h / 2));
        frame.setSize(w, h);
        frame.setVisible(true);
    } // makeFrame

    boolean treeElement(String elementName) {
        for (int i = 0; i < treeElementNames.length; i++) {
            if (elementName.equals(treeElementNames[i])) {
                return true;
            }
        }

        return false;
    }

    // This class wraps a DOM node and returns the text we want to
    // display in the tree. It also returns children, index values,
    // and child counts.
    public class AdapterNode {
        org.w3c.dom.Node domNode;

        // Construct an Adapter node from a DOM node
        public AdapterNode(org.w3c.dom.Node node) {
            domNode = node;
        }

        // Return a string that identifies this node in the tree
        public String toString() {
            String s = typeName[domNode.getNodeType()];
            String nodeName = domNode.getNodeName();

            if (!nodeName.startsWith("#")) {
                s += (": " + nodeName);
            }

            if (domNode.getNodeValue() != null) {
                if (s.startsWith("ProcInstr")) {
                    s += ", ";
                } else {
                    s += ": ";
                }

                // Trim the value to get rid of NL's at the front
                String t = domNode.getNodeValue()
                                  .trim();
                int x = t.indexOf("\n");

                if (x >= 0) {
                    t = t.substring(0, x);
                }

                s += t;
            }

            return s;
        }

        /*
         * Return children, index, and count values
         */
        public int index(AdapterNode child) {
            //System.err.println("Looking for index of " + child);
            int count = childCount();

            for (int i = 0; i < count; i++) {
                AdapterNode n = this.child(i);

                if (child.domNode == n.domNode) {
                    return i;
                }
            }

            return -1; // Should never get here.
        }

        public AdapterNode child(int searchIndex) {
            //Note: JTree index is zero-based. 
            org.w3c.dom.Node node = domNode.getChildNodes()
                                           .item(searchIndex);

            if (compress) {
                // Return Nth displayable node
                int elementNodeIndex = 0;

                for (int i = 0; i < domNode.getChildNodes()
                                               .getLength(); i++) {
                    node = domNode.getChildNodes()
                                  .item(i);

                    if ((node.getNodeType() == ELEMENT_TYPE) &&
                            treeElement(node.getNodeName()) &&
                            (elementNodeIndex++ == searchIndex)) {
                        break;
                    }
                }
            }

            return new AdapterNode(node);
        }

        public int childCount() {
            if (!compress) {
                // Indent this
                return domNode.getChildNodes()
                              .getLength();
            }

            int count = 0;

            for (int i = 0; i < domNode.getChildNodes()
                                           .getLength(); i++) {
                org.w3c.dom.Node node = domNode.getChildNodes()
                                               .item(i);

                if ((node.getNodeType() == ELEMENT_TYPE) &&
                        treeElement(node.getNodeName())) {
                    // Note: 
                    //   Have to check for proper type. 
                    //   The DOCTYPE element also has the right name
                    ++count;
                }
            }

            return count;
        }
    }

    // This adapter converts the current Document (a DOM) into 
    // a JTree model. 
    public class DomToTreeModelAdapter implements javax.swing.tree.TreeModel {
        /*
         * Use these methods to add and remove event listeners.
         * (Needed to satisfy TreeModel interface, but not used.)
         */
        private Vector listenerList = new Vector();

        // Basic TreeModel operations
        public Object getRoot() {
            //System.err.println("Returning root: " +document);
            return new AdapterNode(document);
        }

        public boolean isLeaf(Object aNode) {
            // Determines whether the icon shows up to the left.
            // Return true for any node with no children
            AdapterNode node = (AdapterNode) aNode;

            if (node.childCount() > 0) {
                return false;
            }

            return true;
        }

        public int getChildCount(Object parent) {
            AdapterNode node = (AdapterNode) parent;

            return node.childCount();
        }

        public Object getChild(Object parent, int index) {
            AdapterNode node = (AdapterNode) parent;

            return node.child(index);
        }

        public int getIndexOfChild(Object parent, Object child) {
            AdapterNode node = (AdapterNode) parent;

            return node.index((AdapterNode) child);
        }

        public void valueForPathChanged(TreePath path, Object newValue) {
            // Null. We won't be making changes in the GUI
            // If we did, we would ensure the new value was really new,
            // adjust the model, and then fire a TreeNodesChanged event.
        }

        public void addTreeModelListener(TreeModelListener listener) {
            if ((listener != null) && !listenerList.contains(listener)) {
                listenerList.addElement(listener);
            }
        }

        public void removeTreeModelListener(TreeModelListener listener) {
            if (listener != null) {
                listenerList.removeElement(listener);
            }
        }

        // Note: Since XML works with 1.1, this example uses Vector.
        // If coding for 1.2 or later, though, I'd use this instead:
        //   private List listenerList = new LinkedList();
        // The operations on the List are then add(), remove() and
        // iteration, via:
        //  Iterator it = listenerList.iterator();
        //  while ( it.hasNext() ) {
        //    TreeModelListener listener = (TreeModelListener) it.next();
        //    ...
        //  }
        public void fireTreeNodesChanged(TreeModelEvent e) {
            Enumeration listeners = listenerList.elements();

            while (listeners.hasMoreElements()) {
                TreeModelListener listener =
                    (TreeModelListener) listeners.nextElement();
                listener.treeNodesChanged(e);
            }
        }

        public void fireTreeNodesInserted(TreeModelEvent e) {
            Enumeration listeners = listenerList.elements();

            while (listeners.hasMoreElements()) {
                TreeModelListener listener =
                    (TreeModelListener) listeners.nextElement();
                listener.treeNodesInserted(e);
            }
        }

        public void fireTreeNodesRemoved(TreeModelEvent e) {
            Enumeration listeners = listenerList.elements();

            while (listeners.hasMoreElements()) {
                TreeModelListener listener =
                    (TreeModelListener) listeners.nextElement();
                listener.treeNodesRemoved(e);
            }
        }

        public void fireTreeStructureChanged(TreeModelEvent e) {
            Enumeration listeners = listenerList.elements();

            while (listeners.hasMoreElements()) {
                TreeModelListener listener =
                    (TreeModelListener) listeners.nextElement();
                listener.treeStructureChanged(e);
            }
        }
    }
}
