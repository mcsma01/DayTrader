/**
 *
 *  Copyright 2003-2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/*
 * This module was adapted from IzPack work done by Julien Ponge
 * and Elmar Grom.
 * http://www.izforge.com/izpack/
 * http://developer.berlios.de/projects/izpack/
 */ 
package com.izforge.izpack.panels;
import com.izforge.izpack.Pack;
import com.izforge.izpack.util.Debug;
import com.izforge.izpack.installer.IzPanel;
import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Set;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import net.n3.nanoxml.NonValidator;
import net.n3.nanoxml.StdXMLBuilder;
import net.n3.nanoxml.StdXMLParser;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLElement;

import com.izforge.izpack.LocaleDatabase;
import com.izforge.izpack.Pack;
import com.izforge.izpack.gui.ButtonFactory;
import com.izforge.izpack.gui.TwoColumnConstraints;
import com.izforge.izpack.gui.TwoColumnLayout;
import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import com.izforge.izpack.installer.IzPanel;
import com.izforge.izpack.installer.ResourceManager;
import com.izforge.izpack.util.MultiLineLabel;
import com.izforge.izpack.util.OsConstraint;
import com.izforge.izpack.util.OsVersion;
import com.izforge.izpack.util.VariableSubstitutor;

public class ValidatePackSelections extends IzPanel {
   protected   String sTitle = null; // panel title
   protected   GeronimoConfigProcessor gcp = null;
   protected void setTitle( String title ) {
      sTitle = title;
   }
   protected String getTitle() { // ###ead
      return sTitle;
   }

   protected static boolean fAdvancedMode = false;
   static { // static initializer
      Properties props = System.getProperties();
      Enumeration names = props.propertyNames();
      
      while( names.hasMoreElements() ) {
         String name = (String)names.nextElement();
         if( name.equalsIgnoreCase( "advancedmode" ))  {
            String am = props.getProperty( name );
            if( am != null && am.equalsIgnoreCase( "true" )) {
               fAdvancedMode = true;
            }
         }
      }
   }

/*---------------------------------------------------------------------------*/
/**
 * This panel is designed to collect user input during the installation process. The panel is
 * initially blank and is populated with input elements based on the XML specification in a resource
 * file.
 * 
 * 
 * @version 0.0.1 / 10/19/02
 * @author getDirectoryCreated
 */
/*---------------------------------------------------------------------------*/
/*
 * $ @design
 * 
 * Each field is specified in its own node, containing attributes and data. When this class is
 * instantiated, the specification is read and analyzed. Each field node is processed based on its
 * type. An specialized member function is called for each field type that creates the necessary UI
 * elements. All UI elements are stored in the uiElements vector. Elements are packaged in an object
 * array that must follow this pattern:
 * 
 * index 0 - a String object, that specifies the field type. This is identical to the string used to
 * identify the field type in the XML file. index 1 - a String object that contains the variable
 * name for substitution. index 2 - the constraints object that should be used for positioning the
 * UI element index 3 - the UI element itself index 4 - a Vector containg a list of pack for which
 * the item should be created. This is used by buildUI() to decide if the item should be added to
 * the UI.
 * 
 * In some cases additional entries are used. The use depends on the specific needs of the type of
 * input field.
 * 
 * When the panel is activated, the method buildUI() walks the list of UI elements adds them to the
 * panel together with the matching constraint.
 * 
 * When an attempt is made to move on to another panel, the method readInput() walks the list of UI
 * elements again and calls specialized methods that know how to read the user input from each of
 * the UI elemnts and set the associated varaible.
 * 
 * The actual variable substitution is not performed by this panel but by the variable substitutor.
 * 
 * To Do: ------ * make sure all header documentation is complete and correct
 * --------------------------------------------------------------------------
 */

    // ------------------------------------------------------------------------
    // Constant Definitions
    // ------------------------------------------------------------------------

    // The constants beginning with 'POS_' define locations in the object arrays
    // that used to hold all information for the individual fields. Some data is
    // not required for all field types. If this happens withing the array, that
    // location must be padded with 'null'. At the end of the array it can be
    // omitted. The data stored in this way is in most cases only known by
    // convention between the add and the associated read method. the following
    // positions are also used by other service methods in this class and must
    // not be used for other purposes:
    // - POS_DISPLAYED
    // - POS_TYPE
    // - POS_CONSTRAINTS
    // - POS_PACKS

    /**
     * 
     */
    private static final long serialVersionUID = 3257850965439886129L;

    protected static final int POS_SUPPRESS = 0; 

    protected static final int POS_DISPLAYED = 1;

    protected static final int POS_TYPE = 2;

    protected static final int POS_VARIABLE = 3;

    protected static final int POS_CONSTRAINTS = 4;

    protected static final int POS_FIELD = 5;

    protected static final int POS_PACKS = 6;

    protected static final int POS_OS = 7;

    protected static final int POS_TRUE = 8;

    protected static final int POS_FALSE = 9;

    protected static final int POS_MESSAGE = 10;

    protected static final int POS_GROUP = 11;



    /** The name of the XML file that specifies the panel layout */
    protected static final String SPEC_FILE_NAME = "userInputSpec.xml";

    protected static final String LANG_FILE_NAME = "userInputLang.xml";

    /** how the spec node for a specific panel is identified */
    protected static final String NODE_ID = "panel";

    protected static final String FIELD_NODE_ID = "field";

    protected static final String INSTANCE_IDENTIFIER = "order";

    protected static final String TYPE = "type";

    protected static final String DESCRIPTION = "description";

    protected static final String VARIABLE = "variable";

    protected static final String TEXT = "txt";

    protected static final String KEY = "id";

    protected static final String SPEC = "spec";

    protected static final String SET = "set";

    protected static final String TRUE = "true";

    protected static final String FALSE = "false";

    protected static final String ALIGNMENT = "align";

    protected static final String LEFT = "left";

    protected static final String CENTER = "center";

    protected static final String RIGHT = "right";

    protected static final String TOP = "top";

    protected static final String ITALICS = "italic";

    protected static final String BOLD = "bold";

    protected static final String SIZE = "size";

    protected static final String VALIDATOR = "validator";

    protected static final String PROCESSOR = "processor";

    protected static final String CLASS = "class";

    protected static final String FIELD_LABEL = "label";

    protected static final String TITLE_FIELD = "title";

    protected static final String TEXT_FIELD = "text";

    protected static final String TEXT_SIZE = "size";

    protected static final String STATIC_TEXT = "staticText";

    protected static final String COMBO_FIELD = "combo";

    protected static final String COMBO_CHOICE = "choice";

    protected static final String COMBO_VALUE = "value";

    protected static final String RADIO_FIELD = "radio";

    protected static final String RADIO_CHOICE = "choice";

    protected static final String RADIO_VALUE = "value";

    protected static final String SPACE_FIELD = "space";

    protected static final String DIVIDER_FIELD = "divider";

    protected static final String CHECK_FIELD = "check";

    protected static final String RULE_FIELD = "rule";

    protected static final String RULE_LAYOUT = "layout";

    protected static final String RULE_SEPARATOR = "separator";

    protected static final String RULE_RESULT_FORMAT = "resultFormat";

    protected static final String RULE_PLAIN_STRING = "plainString";

    protected static final String RULE_DISPLAY_FORMAT = "displayFormat";

    protected static final String RULE_SPECIAL_SEPARATOR = "specialSeparator";

    protected static final String RULE_ENCRYPTED = "processed";

    protected static final String RULE_PARAM_NAME = "name";

    protected static final String RULE_PARAM_VALUE = "value";

    protected static final String RULE_PARAM = "param";

    protected static final String PWD_FIELD = "password";

    protected static final String PWD_INPUT = "pwd";

    protected static final String PWD_SIZE = "size";

    protected static final String SEARCH_FIELD = "search";

    // internal value for the button used to trigger autodetection
    protected static final String SEARCH_BUTTON_FIELD = "autodetect";

    protected static final String SEARCH_CHOICE = "choice";

    protected static final String SEARCH_FILENAME = "filename";

    protected static final String SEARCH_RESULT = "result";

    protected static final String SEARCH_VALUE = "value";

    protected static final String SEARCH_TYPE = "type";

    protected static final String SEARCH_FILE = "file";

    protected static final String SEARCH_DIRECTORY = "directory";

    protected static final String SEARCH_PARENTDIR = "parentdir";

    protected static final String SEARCH_CHECKFILENAME = "checkfilename";

    protected static final String SELECTEDPACKS = "createForPack"; // renamed

    protected static final String UNSELECTEDPACKS = "createForUnselectedPack"; // new

    // node

    protected static final String NAME = "name";

    protected static final String OS = "os";

    protected static final String FAMILY = "family";

    protected static final String VALUE          = "value";
    protected static final String DEPENDS        = "depends";
    protected static final String SUPPRESS       = "suppress";
    protected static final String WHEN           = "when";
    protected static final String EXCLUSIVE      = "exclusiveOf";
    protected static final String AUTO_INSTALL   = "AutomatedInstallation";
    protected static final String SELECTED_PACKS = "selected";
    protected static final String PANEL_PKG      = "com.izforge.izpack.panels";
    protected static final String PACKS_PANEL    = "ImgPacksPanel";
    // ------------------------------------------------------------------------
    // Variable Declarations
    // ------------------------------------------------------------------------
    protected static HashMap varMap = new HashMap(); //###ead
    protected static boolean fStaticInitComplete = false; //###ead

    protected static int instanceCount = 0;

    protected int instanceNumber = 0;

    protected boolean fInitComplete = false; //###ead used for one time init in buildUI for each panel
    /**
     * If there is a possibility that some UI elements will not get added we can not allow to go
     * back to the PacksPanel, because the process of building the UI is not reversable. This
     * variable keeps track if any packs have been defined and will be used to make a decision for
     * locking the 'previous' button.
     */
    protected boolean packsDefined = false;

    protected InstallerFrame parentFrame;

    /** The parsed result from reading the XML specification from the file */
    protected XMLElement spec;

    protected boolean haveSpec = false;

    /** Holds the references to all of the UI elements */
    protected Vector uiElements = new Vector();

    /** Holds the references to all radio button groups */
    protected Vector buttonGroups = new Vector();

    /** Holds the references to all password field groups */
    protected Vector passwordGroups = new Vector();

    /**
     * used for temporary storage of references to password groups that have already been read in a
     * given read cycle.
     */
    protected Vector passwordGroupsRead = new Vector();

    /** Used to track search fields. Contains SearchField references. */
    protected Vector searchFields = new Vector();

    /** Holds all user inputs for use in automated installation */
    //protected Vector entries = new Vector(); // removed this ###ead

    protected TwoColumnLayout layout;

    protected LocaleDatabase langpack = null;

    /*--------------------------------------------------------------------------*/
    // This method can be used to search for layout problems. If this class is
    // compiled with this method uncommented, the layout guides will be shown
    // on the panel, making it possible to see if all components are placed
    // correctly.
    /*--------------------------------------------------------------------------*/
    // public void paint (Graphics graphics)
    // {
    // super.paint (graphics);
    // layout.showRules ((Graphics2D)graphics, Color.red);
    // }
    /*--------------------------------------------------------------------------*/
    /**
     * Constructs a <code>UserInputPanel</code>.
     * 
     * @param parent reference to the application frame
     * @param installData shared information about the installation
     */
    /*--------------------------------------------------------------------------*/
    protected static boolean fHasSetAdvancedMode = false; // ##ead

    public ValidatePackSelections(InstallerFrame parent, InstallData installData)

    {
        super(parent, installData);

        if( fHasSetAdvancedMode == false ) { // ###ead
           String sVal = "false";
           if( fAdvancedMode )
              sVal = "true";
           installData.setVariable( "advanced.mode", sVal );
           fHasSetAdvancedMode = true;
        }

        instanceNumber = instanceCount++;
        this.parentFrame = parent;

        gcp = new GeronimoConfigProcessor();
        // ----------------------------------------------------
        // ----------------------------------------------------
        layout = new TwoColumnLayout(10, 5, 30, 25, TwoColumnLayout.LEFT);
        setLayout(layout);

        // ----------------------------------------------------
        // get a locale database
        // ----------------------------------------------------
        try
        {
            // this.langpack = parent.langpack;

            String resource = LANG_FILE_NAME + "_" + idata.localeISO3;
            this.langpack = new LocaleDatabase(ResourceManager.getInstance().getInputStream(
                    resource));
        }
        catch (Throwable exception)
        {}

        // ----------------------------------------------------
        // read the specifications
        // ----------------------------------------------------
        try
        {
            readSpec();
        }
        catch (Throwable exception)
        {
            // log the problem
            exception.printStackTrace();
        }

        if (!haveSpec)
        {
            // return if we could not read the spec. further
            // processing will only lead to problems. In this
            // case we must skip the panel when it gets activated.
            return;
        }
        // ----------------------------------------------------
        // process all field nodes. Each field node is analyzed
        // for its type, then an appropriate memeber function
        // is called that will create the correct UI elements.
        // ----------------------------------------------------
        Vector fields = spec.getChildrenNamed(FIELD_NODE_ID);

        for (int i = 0; i < fields.size(); i++)
        {
            XMLElement field = (XMLElement) fields.elementAt(i);
            String attribute = field.getAttribute(TYPE);

            if (attribute != null)
            {
                if (attribute.equals(RULE_FIELD))
                {
                    addRuleField(field);
                }
                else if (attribute.equals(TEXT_FIELD))
                {
                    addTextField(field);
                }
                else if (attribute.equals(COMBO_FIELD))
                {
                    addComboBox(field);
                }
                else if (attribute.equals(RADIO_FIELD))
                {
                    addRadioButton(field);
                }
                else if (attribute.equals(PWD_FIELD))
                {
                    addPasswordField(field);
                }
                else if (attribute.equals(SPACE_FIELD))
                {
                    addSpace(field);
                }
                else if (attribute.equals(DIVIDER_FIELD))
                {
                    addDivider(field);
                }
                else if (attribute.equals(CHECK_FIELD))
                {
                    addCheckBox(field);
                }
                else if (attribute.equals(STATIC_TEXT))
                {
                    addText(field);
                }
                else if (attribute.equals(TITLE_FIELD))
                {
                    addTitle(field);
                }
                else if (attribute.equals(SEARCH_FIELD))
                {
                    addSearch(field);
                }
            }
        }
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Indicates wether the panel has been validated or not. The installer won't let the user go
     * further through the installation process until the panel is validated. Default behavior is to
     * return true.
     * 
     * @return A boolean stating wether the panel has been validated or not.
     */
    /*--------------------------------------------------------------------------*/

    public boolean isValidated()
    {
      String panelName = getTitle();
      return  (readInput()  && gcp.checkInput( panelName, idata ));
    }

    /*--------------------------------------------------------------------------*/
    /**
     * This method is called when the panel becomes active.
     */
    /*--------------------------------------------------------------------------*/
    public void panelActivate()
    {
        if (spec == null)
        {
            // TODO: translate
            emitError("User input specification could not be found.",
                    "The specification for the user input panel could not be found. Please contact the packager.");
            parentFrame.skipPanel();
        }
        if( fStaticInitComplete == false ) { 
           Set keys = varMap.keySet();
           Iterator iter = keys.iterator();
           while( iter.hasNext() ) {
              String var = (String)iter.next();
              Debug.trace( "varMap: " + var );
              VCheckBox vcb = (VCheckBox)varMap.get( var );
              if( vcb != null ) {
                 vcb.setupDependents();
              }
           }
           fStaticInitComplete = true;
        }
        Vector forPacks = spec.getChildrenNamed(SELECTEDPACKS);
        Vector forUnselectedPacks = spec.getChildrenNamed(UNSELECTEDPACKS);
        Vector forOs = spec.getChildrenNamed(OS);

        if (!itemRequiredFor(forPacks) || !itemRequiredForUnselected(forUnselectedPacks)
                || !itemRequiredForOs(forOs))
        {
            parentFrame.skipPanel();
            return;
        }
        if (!haveSpec)
        {
            parentFrame.skipPanel();
            return;
        }
        String panelName = getTitle();
        gcp.panelEntryTasks( idata, panelName );
        gcp.panelDebug( idata, panelName );
        if( gcp.shouldSkipPanel( idata, panelName )) {
           parentFrame.skipPanel();
           return;
        }
        // if (uiBuilt)
        // {
        // return;
        // }
        // ----------------------------------------------------
        // ----------------------------------------------------

        buildUI();
        if (packsDefined)
        {
            parentFrame.lockPrevButton();
        }
        if( gcp.isPackSelectionProblem( idata ) ) {
            parentFrame.lockNextButton();
        }
        gcp.panelNavDebug( idata );
        if( gcp.isCheckpointPanel( panelName )) {
           String msgs[] = { // this is the default, no error message
              "Congratulations!",
              "    Geronimo configuration is complete.",
              "________________",
              "Press 'Next' to continue with file installation or,",
              "    press 'Previous' to review or change configuration", "selections.",
              "",
              "",
              "",
              "",
              "",
              "" };
           if( gcp.haveConfigErrors( idata, msgs )) {
              parentFrame.lockNextButton();
           }
           Object[] uiElement;
           int j = 0;
           // Title
           // 2 Msg labels
           // DIVIDER
           // 8 Msg Labels
           for( int i = 0; i <  10; ++i ) { // skip title
              uiElement = (Object[])uiElements.elementAt(i); 
              if( i != 99 ) {  // there's a divider on the panel
                              // so skip it
                 //System.out.println("Element: " + i );
                 // all of the fields here are JLabels, so there's no
                 // need to check type. Also, they're all displayed,
                 // so there's no need to check POS_DISPLAYED
                 MultiLineLabel label = (MultiLineLabel)uiElement[ POS_FIELD ]; 
                 label.setText( msgs[ j ] );
                 ++j;
              }
           }
        }

    }

    public void panelDeactivate() {
       // ###ead -- work-around for bug in automation xml processing for the
       // packs panel. Exit, re-entry and exit causes multiple panel entries
       // to appear.  This results in potentially incorrect <selected>
       // sections to appear with incorrect selected pack lists.  The last
       // entry is correct, but unfortunately, the first is used by automated
       // install.  The work-around is to get the xml, walk the tree to the
       // packs panel info, delete all the selected sections and then
       // call the packs panel makeXMLData function to re-create one
       // entry.  It is possible to delete all but the last entry as
       // well.
       // This is all done when the last panel is exited -- although
       //    which direction is unknown/unchecked.
       if( gcp.isCheckpointPanel( getTitle() )) {
          XMLElement xml = idata.xmlData;
          XMLElement panelRoot = null;
          Debug.trace( "-ValidatePackSelections.panelDeactivate() - xml: " + xml.getName() );
          panelRoot = xml.getFirstChildNamed( PANEL_PKG + "." + PACKS_PANEL );
          if( panelRoot != null ) {
             Vector vSel = panelRoot.getChildrenNamed( SELECTED_PACKS );
             for( int i = 0; i < vSel.size(); ++i ) {
                XMLElement sel = (XMLElement)vSel.elementAt(i);
                Debug.trace( "-ValidatePackSelections.panelDeactivate() - vSel( " + i + " ) = " + sel.toString() );
             }
             int children = panelRoot.getChildrenCount();
             int curChild = 0;
             while( panelRoot.getChildrenCount() > 0 ) {
                panelRoot.removeChildAtIndex( 0 );
                Debug.trace( "-ValidatePackSelections.panelDeactivate() - removing ImgPack child: " + (++curChild) + " of " + children ); 
             }
             
             List panels = idata.panels;
             Class packsPanelCls = null;
             try {
                packsPanelCls = Class.forName( PANEL_PKG + "." + PACKS_PANEL );
             } catch( Exception e ) {
               e.printStackTrace();
               throw new RuntimeException( e );
             }
             IzPanel packsPanel = null;
             for( int i = 0; i < panels.size(); ++i ) {
                packsPanel = (IzPanel)panels.get(i);
                if( packsPanelCls.isInstance( packsPanel )) { // there's only one
                   break;
                }
                packsPanel = null;
             }
             if( packsPanel != null ) {
                Debug.trace( "-ValidatePackSelections.panelDeactivate() - calling ImgPacks panel to make new xml." );
                packsPanel.makeXMLData( panelRoot );
             }
          }
       }
    }
    /*--------------------------------------------------------------------------*/
    /**
     * Asks the panel to set its own XML data that can be brought back for an automated installation
     * process. Use it as a blackbox if your panel needs to do something even in automated mode.
     * 
     * @param panelRoot The XML root element of the panels blackbox tree.
     */
    /*--------------------------------------------------------------------------*/
    public void makeXMLData(XMLElement panelRoot)
    {
        Map entryMap = new HashMap();
        /* ###ead
           this approach doesn't work correctly for Geronimo (and there's a bug)
        for (int i = 0; i < entries.size(); i++)
        {
            TextValuePair pair = (TextValuePair) entries.elementAt(i);
            entryMap.put(pair.toString(), pair.getValue());
        }
        */ 
        // ###ead -- new approach
        Object[] uiElement;

        for (int i = 0; i < uiElements.size(); i++) {
            uiElement = (Object[]) uiElements.elementAt(i);
            String var = (String)uiElement[POS_VARIABLE];
            if( var != null ) {
               String val = idata.getVariable( var );
               if( val != null ) {
                  Debug.trace( "-makeXMLData(): entryMap.put( " + var + ", " + val + " )");
                  entryMap.put( var, val );
               } else Debug.trace( "-makeXMLData(): entryMap.put( " + var + " was null" );
            }
        }
        // end new approach
        new ValidatePackSelectionsAutomationHelper(entryMap, getTitle() ).makeXMLData(idata, panelRoot);
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Builds the UI and makes it ready for display
     */
    /*--------------------------------------------------------------------------*/
    protected void buildUI()
    {
        Object[] uiElement;

        for (int i = 0; i < uiElements.size(); i++)
        {
            uiElement = (Object[]) uiElements.elementAt(i);

            if ( itemRequiredFor((Vector) uiElement[POS_PACKS])  &&     itemRequiredForOs((Vector) uiElement[POS_OS]) && ( isFieldSuppressed( (Vector)uiElement[POS_SUPPRESS] ) == false ))
            {
                try
                {
                    if (uiElement[POS_DISPLAYED] == null
                           /*###ead || uiElement[POS_DISPLAYED].toString().equals("false")*/)
                    {
                        add((JComponent) uiElement[POS_FIELD], uiElement[POS_CONSTRAINTS]);
                    }

                    uiElement[POS_DISPLAYED] = Boolean.valueOf(true);
                    ((JComponent) uiElement[POS_FIELD]).setVisible(true); //###ead                     //###ead uiElements.remove(i);
                    //###ead uiElements.add(i, uiElement);
                }
                catch (Throwable exception)
                {
                    System.out.println("Internal format error in field: "
                            + uiElement[POS_TYPE].toString()); // !!! logging
                }
            }
            else
            {
                try
                {
                    if (uiElement[POS_DISPLAYED] != null
                            && uiElement[POS_DISPLAYED].toString().equals("true"))
                    {
                        //###ead remove((JComponent) uiElement[POS_FIELD]);
                        ((JComponent) uiElement[POS_FIELD]).setVisible(false); //###ead
                        uiElement[POS_DISPLAYED] = Boolean.valueOf(false); //###ead
                    } 
                    if( uiElement[POS_DISPLAYED] == null ) { //###ead
                        add((JComponent) uiElement[POS_FIELD],  uiElement[POS_CONSTRAINTS]);
                        ((JComponent) uiElement[POS_FIELD]).setVisible(false);     
                        uiElement[POS_DISPLAYED] = Boolean.valueOf(false);
                    }
                }
                catch (Throwable exception)
                {
                    System.out.println("Internal format error in field: "
                            + uiElement[POS_TYPE].toString()); // !!! logging
                }
                uiElement[POS_DISPLAYED] = Boolean.valueOf(false);
                //###ead uiElements.remove(i);
                //###ead uiElements.add(i, uiElement);
            }
        }
        if( fInitComplete == false ) { //###ead
           //fInitComplete = true;
           for (int i = 0; i < uiElements.size(); i++) {
              uiElement = (Object[]) uiElements.elementAt(i);
              Object uiComp = uiElement[POS_FIELD];
              if( uiComp instanceof VCheckBox ) {
                 VCheckBox vcb = (VCheckBox)uiComp;
                 vcb.setValidStates();
              }
           }
        }
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Reads the input data from all UI elements and sets the associated variables.
     * 
     * @return <code>true</code> if the operation is successdul, otherwise <code>false</code>.
     */
    /*--------------------------------------------------------------------------*/
    protected boolean readInput()
    {
        boolean success;
        String fieldType = null;
        Object[] field = null;

        passwordGroupsRead.clear();
        // ----------------------------------------------------
        // cycle through all but the password fields and read
        // their contents
        // ----------------------------------------------------
        for (int i = 0; i < uiElements.size(); i++)
        {
            field = (Object[]) uiElements.elementAt(i);

            if ((field != null) && (((Boolean) field[POS_DISPLAYED]).booleanValue()))
            {
                fieldType = (String) (field[POS_TYPE]);

                // ------------------------------------------------
                if (fieldType.equals(RULE_FIELD))
                {
                    success = readRuleField(field);
                    if (!success) { return (false); }
                }

                // ------------------------------------------------
                if (fieldType.equals(PWD_FIELD))
                {
                    success = readPasswordField(field);
                    if (!success) { return (false); }
                }

                // ------------------------------------------------
                else if (fieldType.equals(TEXT_FIELD))
                {
                    success = readTextField(field);
                    if (!success) { return (false); }
                }

                // ------------------------------------------------
                else if (fieldType.equals(COMBO_FIELD))
                {
                    success = readComboBox(field);
                    if (!success) { return (false); }
                }

                // ------------------------------------------------
                else if (fieldType.equals(RADIO_FIELD))
                {
                    success = readRadioButton(field);
                    if (!success) { return (false); }
                }

                // ------------------------------------------------
                else if (fieldType.equals(CHECK_FIELD))
                {
                    success = readCheckBox(field);
                    if (!success) { return (false); }
                }
                else if (fieldType.equals(SEARCH_FIELD))
                {
                    success = readSearch(field);
                    if (!success) { return (false); }
                }
            }
        }

        return (true);
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Reads the XML specification for the panel layout. The result is stored in spec.
     * 
     * @exception Exception for any problems in reading the specification
     */
    /*--------------------------------------------------------------------------*/
    protected void readSpec() throws Exception
    {
        InputStream input = null;
        XMLElement data;
        Vector specElements;
        String attribute;
        String instance = Integer.toString(instanceNumber);

        try
        {
            input = parentFrame.getResource(SPEC_FILE_NAME);
        }
        catch (Exception exception)
        {
            haveSpec = false;
            return;
        }
        if (input == null)
        {
            haveSpec = false;
            return;
        }

        // initialize the parser
        StdXMLParser parser = new StdXMLParser();
        parser.setBuilder(new StdXMLBuilder());
        parser.setValidator(new NonValidator());
        parser.setReader(new StdXMLReader(input));

        // get the data
        data = (XMLElement) parser.parse();

        // extract the spec to this specific panel instance
        if (data.hasChildren())
        {
            specElements = data.getChildrenNamed(NODE_ID);
            //Debug.trace( "--> Panel elements: " + specElements.size() );
            for (int i = 0; i < specElements.size(); i++)
            {
                data = (XMLElement) specElements.elementAt(i);
                attribute = data.getAttribute(INSTANCE_IDENTIFIER);
                //Debug.trace( "----> instance: '" + instance + "'  instanceNumber: '" + instanceNumber + "'");
                if (instance.equals(attribute))
                {
                    // use the current element as spec
                    spec = data;
                    // close the stream
                    input.close();
                    haveSpec = true;
                    return;
                }
            }

            haveSpec = false;
            return;
        }

        haveSpec = false;
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Adds the title to the panel. There can only be one title, if mutiple titles are defined, they
     * keep overwriting what has already be defined, so that the last definition is the one that
     * prevails.
     * 
     * @param spec a <code>XMLElement</code> containing the specification for the title.
     */
    /*--------------------------------------------------------------------------*/
    protected void addTitle(XMLElement spec)
    {
        String title = getText(spec);
	setTitle( title );
        boolean italic = getBoolean(spec, ITALICS, false);
        boolean bold = getBoolean(spec, BOLD, false);
        float multiplier = getFloat(spec, SIZE, 2.0f);
        int justify = getAlignment(spec);

        if (title != null)
        {
            JLabel label = new JLabel(title);
            Font font = label.getFont();
            float size = font.getSize();
            int style = 0;

            if (bold)
            {
                style = style + Font.BOLD;
            }
            if (italic)
            {
                style = style + Font.ITALIC;
            }

            font = font.deriveFont(style, (size * multiplier));
            label.setFont(font);
            label.setAlignmentX(0);

            TwoColumnConstraints constraints = new TwoColumnConstraints();
            constraints.align = justify;
            constraints.position = TwoColumnConstraints.NORTH;

            add(label, constraints);
        }
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Adds a rule field to the list of UI elements.
     * 
     * @param spec a <code>XMLElement</code> containing the specification for the rule field.
     */
    /*--------------------------------------------------------------------------*/
    protected void addRuleField(XMLElement spec)
    {
        Vector forPacks = spec.getChildrenNamed(SELECTEDPACKS);
        Vector forOs = spec.getChildrenNamed(OS);
        XMLElement element = spec.getFirstChildNamed(SPEC);
        String variable = spec.getAttribute(VARIABLE);
        RuleInputField field = null;
        JLabel label;
        String layout;
        String set;
        String separator;
        String format;
        String validator = null;
        String message = null;
        boolean hasParams = false;
        String paramName = null;
        String paramValue = null;
        HashMap validateParamMap = null;
        Vector validateParams = null;
        String processor = null;
        int resultFormat = RuleInputField.DISPLAY_FORMAT;

        // ----------------------------------------------------
        // extract the specification details
        // ----------------------------------------------------
        if (element != null)
        {
            label = new JLabel(getText(element));
            layout = element.getAttribute(RULE_LAYOUT);
            set = element.getAttribute(SET);

            // retrieve value of variable if not specified
            // (does not work here because of special format for set attribute)
            // if (set == null)
            // {
            // set = idata.getVariable (variable);
            // }

            separator = element.getAttribute(RULE_SEPARATOR);
            format = element.getAttribute(RULE_RESULT_FORMAT);

            if (format != null)
            {
                if (format.equals(RULE_PLAIN_STRING))
                {
                    resultFormat = RuleInputField.PLAIN_STRING;
                }
                else if (format.equals(RULE_DISPLAY_FORMAT))
                {
                    resultFormat = RuleInputField.DISPLAY_FORMAT;
                }
                else if (format.equals(RULE_SPECIAL_SEPARATOR))
                {
                    resultFormat = RuleInputField.SPECIAL_SEPARATOR;
                }
                else if (format.equals(RULE_ENCRYPTED))
                {
                    resultFormat = RuleInputField.ENCRYPTED;
                }
            }
        }
        // ----------------------------------------------------
        // if there is no specification element, return without
        // doing anything.
        // ----------------------------------------------------
        else
        {
            return;
        }

        // ----------------------------------------------------
        // get the description and add it to the list of UI
        // elements if it exists.
        // ----------------------------------------------------
        element = spec.getFirstChildNamed(DESCRIPTION);
        addDescription(element, forPacks, forOs, null);

        // ----------------------------------------------------
        // get the validator and processor if they are defined
        // ----------------------------------------------------
        element = spec.getFirstChildNamed(VALIDATOR);
        if (element != null)
        {
            validator = element.getAttribute(CLASS);
            message = getText(element);
            // ----------------------------------------------------------
            // check and see if we have any parameters for this validator.
            // If so, then add them to validateParamMap.
            // ----------------------------------------------------------
            validateParams = element.getChildrenNamed(RULE_PARAM);
            if (validateParams != null && validateParams.size() > 0 && validateParamMap == null)
            {

                validateParamMap = new HashMap();
                hasParams = true;

            }

            for (Iterator it = validateParams.iterator(); it.hasNext();)
            {
                element = (XMLElement) it.next();
                paramName = element.getAttribute(RULE_PARAM_NAME);
                paramValue = element.getAttribute(RULE_PARAM_VALUE);
                validateParamMap.put(paramName, paramValue);
            }
        }

        element = spec.getFirstChildNamed(PROCESSOR);
        if (element != null)
        {
            processor = element.getAttribute(CLASS);
        }

        // ----------------------------------------------------
        // create an instance of RuleInputField based on the
        // extracted specifications, then add it to the list
        // of UI elements.
        // ----------------------------------------------------
        if (hasParams)
        {
            field = new RuleInputField(layout, set, separator, validator, validateParamMap,
                    processor, resultFormat, getToolkit(), idata);
        }
        else
        {
            field = new RuleInputField(layout, set, separator, validator, processor, resultFormat,
                    getToolkit(), idata);

        }
        TwoColumnConstraints constraints = new TwoColumnConstraints();
        constraints.position = TwoColumnConstraints.WEST;

        uiElements
                .add(new Object[] { null, null, FIELD_LABEL, null, constraints, label, forPacks, forOs});

        TwoColumnConstraints constraints2 = new TwoColumnConstraints();
        constraints2.position = TwoColumnConstraints.EAST;

        uiElements.add(new Object[] { null, null, RULE_FIELD, variable, constraints2, field, forPacks,
                forOs, null, null, message});
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Reads the data from the rule input field and sets the associated variable.
     * 
     * @param field the object array that holds the details of the field.
     * 
     * @return <code>true</code> if there was no problem reading the data or if there was an
     * irrecovarable problem. If there was a problem that can be corrected by the operator, an error
     * dialog is popped up and <code>false</code> is returned.
     */
    /*--------------------------------------------------------------------------*/
    protected boolean readRuleField(Object[] field)
    {
        RuleInputField ruleField = null;
        String variable = null;

        try
        {
            ruleField = (RuleInputField) field[POS_FIELD];
            variable = (String) field[POS_VARIABLE];
        }
        catch (Throwable exception)
        {
            return (true);
        }
        if ((variable == null) || (ruleField == null)) { return (true); }

        boolean success = ruleField.validateContents();
        if (!success)
        {
            String message = "";
            try
            {
                message = langpack.getString((String) field[POS_MESSAGE]);
                if (message.equals(""))
                {
                    message = (String) field[POS_MESSAGE];
                }
            }
            catch (Throwable t)
            {
                message = (String) field[POS_MESSAGE];
            }
            JOptionPane.showMessageDialog(parentFrame, message, parentFrame.langpack
                    .getString("UserInputPanel.error.caption"), JOptionPane.WARNING_MESSAGE);
            return (false);
        }

        idata.setVariable(variable, ruleField.getText());
        //entries.add(new TextValuePair(variable, ruleField.getText())); 
        return (true);
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Adds a text field to the list of UI elements
     * 
     * @param spec a <code>XMLElement</code> containing the specification for the text field.
     */
    /*--------------------------------------------------------------------------*/
    protected void addTextField(XMLElement spec)
    {
        Vector forPacks = spec.getChildrenNamed(SELECTEDPACKS);
        Vector forOs = spec.getChildrenNamed(OS);
        XMLElement element = spec.getFirstChildNamed(SPEC);
        JLabel label;
        String set;
        int size;

        String variable = spec.getAttribute(VARIABLE);
        if ((variable == null) || (variable.length() == 0)) { return; }

        // ----------------------------------------------------
        // extract the specification details
        // ----------------------------------------------------
        if (element != null)
        {
            label = new JLabel(getText(element));
            set = element.getAttribute(SET);
            if (set == null)
            {
                set = idata.getVariable(variable);
                if (set == null)
                {
                    set = "";
                }
            }else{
                if (set != null && !"".equals(set)){
                    VariableSubstitutor vs = new VariableSubstitutor(idata.getVariables());
                    set = vs.substitute(set, null);
                }
            }
                
            try
            {
                size = Integer.parseInt(element.getAttribute(TEXT_SIZE));
            }
            catch (Throwable exception)
            {
                size = 1;
            }
        }
        // ----------------------------------------------------
        // if there is no specification element, return without
        // doing anything.
        // ----------------------------------------------------
        else
        {
            return;
        }

        // ----------------------------------------------------
        // get the description and add it to the list UI
        // elements if it exists.
        // ----------------------------------------------------
        element = spec.getFirstChildNamed(DESCRIPTION);
        addDescription(element, forPacks, forOs, null);

        // ----------------------------------------------------
        // construct the UI element and add it to the list
        // ----------------------------------------------------
        JTextField field = new JTextField(set, size);
        field.setCaretPosition(0);

        TwoColumnConstraints constraints = new TwoColumnConstraints();
        constraints.position = TwoColumnConstraints.WEST;

        uiElements
                .add(new Object[] { null, null, FIELD_LABEL, null, constraints, label, forPacks, forOs});

        TwoColumnConstraints constraints2 = new TwoColumnConstraints();
        constraints2.position = TwoColumnConstraints.EAST;

        uiElements.add(new Object[] { null, null, TEXT_FIELD, variable, constraints2, field, forPacks,
                forOs});
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Reads data from the text field and sets the associated variable.
     * 
     * @param field the object array that holds the details of the field.
     * 
     * @return <code>true</code> if there was no problem reading the data or if there was an
     * irrecovarable problem. If there was a problem that can be corrected by the operator, an error
     * dialog is popped up and <code>false</code> is returned.
     */
    /*--------------------------------------------------------------------------*/
    protected boolean readTextField(Object[] field)
    {
        JTextField textField = null;
        String variable = null;
        String value = null;

        try
        {
            textField = (JTextField) field[POS_FIELD];
            variable = (String) field[POS_VARIABLE];
            value = textField.getText();
        }
        catch (Throwable exception)
        {
            return (true);
        }
        if ((variable == null) || (value == null)) { return (true); }

        idata.setVariable(variable, value);
        // entries.add(new TextValuePair(variable, value));
        return (true);
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Adds a combo box to the list of UI elements. <br>
     * This is a complete example of a valid XML specification
     * 
     * <pre>
     * 
     *  
     *   
     *    &lt;field type=&quot;combo&quot; variable=&quot;testVariable&quot;&gt;
     *      &lt;description text=&quot;Description for the combo box&quot; id=&quot;a key for translated text&quot;/&gt;
     *      &lt;spec text=&quot;label&quot; id=&quot;key for the label&quot;/&gt;
     *        &lt;choice text=&quot;choice 1&quot; id=&quot;&quot; value=&quot;combo box 1&quot;/&gt;
     *        &lt;choice text=&quot;choice 2&quot; id=&quot;&quot; value=&quot;combo box 2&quot; set=&quot;true&quot;/&gt;
     *        &lt;choice text=&quot;choice 3&quot; id=&quot;&quot; value=&quot;combo box 3&quot;/&gt;
     *        &lt;choice text=&quot;choice 4&quot; id=&quot;&quot; value=&quot;combo box 4&quot;/&gt;
     *      &lt;/spec&gt;
     *    &lt;/field&gt;
     *    
     *   
     *  
     * </pre>
     * 
     * @param spec a <code>XMLElement</code> containing the specification for the combo box.
     */
    /*--------------------------------------------------------------------------*/
    protected void addComboBox(XMLElement spec)
    {
        Vector forPacks = spec.getChildrenNamed(SELECTEDPACKS);
        Vector forOs = spec.getChildrenNamed(OS);
        XMLElement element = spec.getFirstChildNamed(SPEC);
        String variable = spec.getAttribute(VARIABLE);
        TextValuePair listItem = null;
        JComboBox field = new JComboBox();
        JLabel label;

        // ----------------------------------------------------
        // extract the specification details
        // ----------------------------------------------------
        if (element != null)
        {
            label = new JLabel(getText(element));

            Vector choices = element.getChildrenNamed(COMBO_CHOICE);

            if (choices == null) { return; }

            for (int i = 0; i < choices.size(); i++)
            {
                String processorClass = ((XMLElement) choices.elementAt(i))
                        .getAttribute("processor");

                if (processorClass != null && !"".equals(processorClass))
                {
                    String choiceValues = "";
                    try
                    {
                        choiceValues = ((Processor) Class.forName(processorClass).newInstance())
                                .process(null);
                    }
                    catch (Throwable t)
                    {
                        t.printStackTrace();
                    }
                    String set = ((XMLElement) choices.elementAt(i)).getAttribute(SET);
                    if (set == null)
                    {
                        set = "";
                    }
                    if (set != null && !"".equals(set)){
                        VariableSubstitutor vs = new VariableSubstitutor(idata.getVariables());
                        set = vs.substitute(set, null);
                    }
                    
                    StringTokenizer tokenizer = new StringTokenizer(choiceValues, ":");
                    int counter = 0;
                    while (tokenizer.hasMoreTokens())
                    {
                        String token = tokenizer.nextToken();
                        listItem = new TextValuePair(token, token);
                        field.addItem(listItem);
                        if (set.equals(token))
                        {
                            field.setSelectedIndex(field.getItemCount() - 1);
                        }
                        counter++;
                    }
                }
                else
                {
                    listItem = new TextValuePair(getText((XMLElement) choices.elementAt(i)),
                            ((XMLElement) choices.elementAt(i)).getAttribute(COMBO_VALUE));
                    field.addItem(listItem);
                    String set = ((XMLElement) choices.elementAt(i)).getAttribute(SET);
                    if (set != null)
                    {
                        if (set != null && !"".equals(set)){
                            VariableSubstitutor vs = new VariableSubstitutor(idata.getVariables());
                            set = vs.substitute(set, null);
                        }
                        if (set.equals(TRUE))
                        {
                            field.setSelectedIndex(i);
                        }
                    }
                }

            }
        }
        // ----------------------------------------------------
        // if there is no specification element, return without
        // doing anything.
        // ----------------------------------------------------
        else
        {
            return;
        }

        // ----------------------------------------------------
        // get the description and add it to the list of UI
        // elements if it exists.
        // ----------------------------------------------------
        element = spec.getFirstChildNamed(DESCRIPTION);
        addDescription(element, forPacks, forOs, null);

        TwoColumnConstraints constraints = new TwoColumnConstraints();
        constraints.position = TwoColumnConstraints.WEST;

        uiElements
                .add(new Object[] { null, null, FIELD_LABEL, null, constraints, label, forPacks, forOs});

        TwoColumnConstraints constraints2 = new TwoColumnConstraints();
        constraints2.position = TwoColumnConstraints.EAST;

        uiElements.add(new Object[] { null, null, COMBO_FIELD, variable, constraints2, field, forPacks,
                forOs});
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Reads the content of the combobox field and substitutes the associated variable.
     * 
     * @param field the object array that holds the details of the field.
     * 
     * @return <code>true</code> if there was no problem reading the data or if there was an
     * irrecovarable problem. If there was a problem that can be corrected by the operator, an error
     * dialog is popped up and <code>false</code> is returned.
     */
    /*--------------------------------------------------------------------------*/
    protected boolean readComboBox(Object[] field)
    {
        String variable;
        String value;
        JComboBox comboBox;

        try
        {
            variable = (String) field[POS_VARIABLE];
            comboBox = (JComboBox) field[POS_FIELD];
            value = ((TextValuePair) comboBox.getSelectedItem()).getValue();
        }
        catch (Throwable exception)
        {
            return true;
        }
        if ((variable == null) || (value == null)) { return true; }

        idata.setVariable(variable, value);
        // entries.add(new TextValuePair(variable, value));
        return true;
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Adds a radio button set to the list of UI elements. <br>
     * This is a complete example of a valid XML specification
     * 
     * <pre>
     * 
     *  
     *   
     *    &lt;field type=&quot;radio&quot; variable=&quot;testVariable&quot;&gt;
     *      &lt;description text=&quot;Description for the radio buttons&quot; id=&quot;a key for translated text&quot;/&gt;
     *      &lt;spec text=&quot;label&quot; id=&quot;key for the label&quot;/&gt;
     *        &lt;choice text=&quot;radio 1&quot; id=&quot;&quot; value=&quot;&quot;/&gt;
     *        &lt;choice text=&quot;radio 2&quot; id=&quot;&quot; value=&quot;&quot; set=&quot;true&quot;/&gt;
     *        &lt;choice text=&quot;radio 3&quot; id=&quot;&quot; value=&quot;&quot;/&gt;
     *        &lt;choice text=&quot;radio 4&quot; id=&quot;&quot; value=&quot;&quot;/&gt;
     *        &lt;choice text=&quot;radio 5&quot; id=&quot;&quot; value=&quot;&quot;/&gt;
     *      &lt;/spec&gt;
     *    &lt;/field&gt;
     *    
     *   
     *  
     * </pre>
     * 
     * @param spec a <code>XMLElement</code> containing the specification for the radio button
     * set.
     */
    /*--------------------------------------------------------------------------*/
    protected void addRadioButton(XMLElement spec)
    {
        Vector forPacks = spec.getChildrenNamed(SELECTEDPACKS);
        Vector forOs = spec.getChildrenNamed(OS);
        String variable = spec.getAttribute(VARIABLE);
        String value = null;

        XMLElement element = null;
        ButtonGroup group = new ButtonGroup();

        TwoColumnConstraints constraints = new TwoColumnConstraints();
        constraints.position = TwoColumnConstraints.BOTH;
        constraints.indent = true;
        constraints.stretch = true;

        // ----------------------------------------------------
        // get the description and add it to the list of UI
        // elements if it exists.
        // ----------------------------------------------------
        element = spec.getFirstChildNamed(DESCRIPTION);
        addDescription(element, forPacks, forOs, null);

        // ----------------------------------------------------
        // extract the specification details
        // ----------------------------------------------------
        element = spec.getFirstChildNamed(SPEC);

        if (element != null)
        {
            Vector choices = element.getChildrenNamed(RADIO_CHOICE);

            if (choices == null) { return; }

            // --------------------------------------------------
            // process each choice element
            // --------------------------------------------------
            for (int i = 0; i < choices.size(); i++)
            {
                JRadioButton choice = new JRadioButton();
                choice.setText(getText((XMLElement) choices.elementAt(i)));
                value = (((XMLElement) choices.elementAt(i)).getAttribute(RADIO_VALUE));

                group.add(choice);

                String set = ((XMLElement) choices.elementAt(i)).getAttribute(SET);
                if (set != null)
                {
                    if (set != null && !"".equals(set)){
                        VariableSubstitutor vs = new VariableSubstitutor(idata.getVariables());
                        set = vs.substitute(set, null);
                    }
                    if (set.equals(TRUE))
                    {
                        choice.setSelected(true);
                    }
                }

                buttonGroups.add(group);
                uiElements.add(new Object[] { null, null, RADIO_FIELD, variable, constraints, choice,
                        forPacks, forOs, value, null, null, group});
            }
        }
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Reads the content of the radio button field and substitutes the associated variable.
     * 
     * @param field the object array that holds the details of the field.
     * 
     * @return <code>true</code> if there was no problem reading the data or if there was an
     * irrecovarable problem. If there was a problem that can be corrected by the operator, an error
     * dialog is popped up and <code>false</code> is returned.
     */
    /*--------------------------------------------------------------------------*/
    protected boolean readRadioButton(Object[] field)
    {
        String variable = null;
        String value = null;
        JRadioButton button = null;

        try
        {
            button = (JRadioButton) field[POS_FIELD];

            if (!button.isSelected()) { return (true); }

            variable = (String) field[POS_VARIABLE];
            value = (String) field[POS_TRUE];
        }
        catch (Throwable exception)
        {
            return (true);
        }

        idata.setVariable(variable, value);
        // entries.add(new TextValuePair(variable, value));
        return (true);
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Adds one or more password fields to the list of UI elements. <br>
     * This is a complete example of a valid XML specification
     * 
     * <pre>
     * 
     *  
     *   
     *    &lt;field type=&quot;password&quot; variable=&quot;testVariable&quot;&gt;
     *      &lt;description align=&quot;left&quot; txt=&quot;Please enter your password&quot; id=&quot;a key for translated text&quot;/&gt;
     *      &lt;spec&gt;
     *        &lt;pwd txt=&quot;Password&quot; id=&quot;key for the label&quot; size=&quot;10&quot; set=&quot;&quot;/&gt;
     *        &lt;pwd txt=&quot;Retype password&quot; id=&quot;another key for the label&quot; size=&quot;10&quot; set=&quot;&quot;/&gt;
     *      &lt;/spec&gt;
     *      &lt;validator class=&quot;com.izforge.sample.PWDValidator&quot; txt=&quot;Both versions of the password must match&quot; id=&quot;key for the error text&quot;/&gt;
     *      &lt;processor class=&quot;com.izforge.sample.PWDEncryptor&quot;/&gt;
     *    &lt;/field&gt;
     *    
     *   
     *  
     * </pre>
     * 
     * @param spec a <code>XMLElement</code> containing the specification for the set of password
     * fields.
     */
    /*--------------------------------------------------------------------------*/
    protected void addPasswordField(XMLElement spec)
    {
        Vector forPacks = spec.getChildrenNamed(SELECTEDPACKS);
        Vector forOs = spec.getChildrenNamed(OS);
        String variable = spec.getAttribute(VARIABLE);
        String validator = null;
        String message = null;
        String processor = null;
        XMLElement element = null;
        PasswordGroup group = null;
        int size = 0;

        // ----------------------------------------------------
        // get the description and add it to the list of UI
        // elements if it exists.
        // ----------------------------------------------------
        element = spec.getFirstChildNamed(DESCRIPTION);
        addDescription(element, forPacks, forOs, null);

        // ----------------------------------------------------
        // get the validator and processor if they are defined
        // ----------------------------------------------------
        element = spec.getFirstChildNamed(VALIDATOR);
        if (element != null)
        {
            validator = element.getAttribute(CLASS);
            message = getText(element);
        }

        element = spec.getFirstChildNamed(PROCESSOR);
        if (element != null)
        {
            processor = element.getAttribute(CLASS);
        }

        group = new PasswordGroup(validator, processor);

        // ----------------------------------------------------
        // extract the specification details
        // ----------------------------------------------------
        element = spec.getFirstChildNamed(SPEC);

        if (element != null)
        {
            Vector inputs = element.getChildrenNamed(PWD_INPUT);

            if (inputs == null) { return; }

            // --------------------------------------------------
            // process each input field
            // --------------------------------------------------
            XMLElement fieldSpec;
            for (int i = 0; i < inputs.size(); i++)
            {
                fieldSpec = (XMLElement) inputs.elementAt(i);
                String set = fieldSpec.getAttribute(SET);
                if (set != null && !"".equals(set)){
                    VariableSubstitutor vs = new VariableSubstitutor(idata.getVariables());
                    set = vs.substitute(set, null);
                }
                JLabel label = new JLabel(getText(fieldSpec));
                try
                {
                    size = Integer.parseInt(fieldSpec.getAttribute(PWD_SIZE));
                }
                catch (Throwable exception)
                {
                    size = 1;
                }

                // ----------------------------------------------------
                // construct the UI element and add it to the list
                // ----------------------------------------------------
                JPasswordField field = new JPasswordField(set, size);
                field.setCaretPosition(0);

                TwoColumnConstraints constraints = new TwoColumnConstraints();
                constraints.position = TwoColumnConstraints.WEST;

                uiElements.add(new Object[] { null, null, FIELD_LABEL, null, constraints, label,
                        forPacks, forOs});

                TwoColumnConstraints constraints2 = new TwoColumnConstraints();
                constraints2.position = TwoColumnConstraints.EAST;

                uiElements.add(new Object[] { null, null, PWD_FIELD, variable, constraints2, field,
                        forPacks, forOs, null, null, message, group});
                group.addField(field);
            }
        }

        passwordGroups.add(group);
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Reads the content of the password field and substitutes the associated variable.
     * 
     * @param field a password group that manages one or more passord fields.
     * 
     * @return <code>true</code> if there was no problem reading the data or if there was an
     * irrecovarable problem. If there was a problem that can be corrected by the operator, an error
     * dialog is popped up and <code>false</code> is returned.
     */
    /*--------------------------------------------------------------------------*/
    protected boolean readPasswordField(Object[] field)
    {
        PasswordGroup group = null;
        String variable = null;
        String message = null;

        try
        {
            group = (PasswordGroup) field[POS_GROUP];
            variable = (String) field[POS_VARIABLE];
            message = (String) field[POS_MESSAGE];
        }
        catch (Throwable exception)
        {
            return (true);
        }
        if ((variable == null) || (passwordGroupsRead.contains(group))) { return (true); }
        passwordGroups.add(group);

        boolean success = group.validateContents();

        if (!success)
        {
            JOptionPane.showMessageDialog(parentFrame, message, parentFrame.langpack
                    .getString("UserInputPanel.error.caption"), JOptionPane.WARNING_MESSAGE);
            return (false);
        }

        idata.setVariable(variable, group.getPassword());
        // entries.add(new TextValuePair(variable, group.getPassword()));
        return (true);
    }
    protected boolean isFieldSuppressed( Vector suppressions ) { // ###ead
       boolean fRet = false;
       for( int i = 0; suppressions != null &&  i < suppressions.size( ); ++i ) {
          TextValuePair tvp = (TextValuePair)suppressions.elementAt(i);
          String var = tvp.toString();
          String val = idata.getVariable( var );
          if( val.equalsIgnoreCase( tvp.getValue() )) {
             fRet = true;
             break;
          }
       }
       return fRet;
    }
    protected Vector getFieldSuppressionList( XMLElement spec ) { // ###ead
       Vector supps = null;
       XMLElement suppress = spec.getFirstChildNamed( SUPPRESS );
       if( suppress != null ) {
          Vector whens = suppress.getChildrenNamed( WHEN );
          for( int i = 0; i < whens.size(); ++i ) {
             if( supps == null ) supps = new Vector();
             XMLElement when = (XMLElement)whens.elementAt(i);
             String var = when.getAttribute( VARIABLE );
             String val = when.getAttribute( VALUE );
             supps.add( new TextValuePair( var, val ));
          }
       }
       return supps;
    }
    /*--------------------------------------------------------------------------*/
    /**
     * Adds a chackbox to the list of UI elements.
     * 
     * @param spec a <code>XMLElement</code> containing the specification for the checkbox.
     */
    /*--------------------------------------------------------------------------*/
    protected void addCheckBox(XMLElement spec)
    {
        Vector forPacks = spec.getChildrenNamed(SELECTEDPACKS);
        Vector forOs = spec.getChildrenNamed(OS);
        String label = "";
        String set = null;
        String trueValue = null;
        String falseValue = null;
        String variable = spec.getAttribute(VARIABLE);
        XMLElement detail = spec.getFirstChildNamed(SPEC);
        Vector suppress = getFieldSuppressionList( spec ); // ###ead
        if (variable == null) { return; }

        if (detail != null)
        {
            label = getText(detail);
            set = detail.getAttribute(SET);
            trueValue = detail.getAttribute(TRUE);
            falseValue = detail.getAttribute(FALSE);
        }

        VCheckBox checkbox = new VCheckBox(label, varMap, idata, spec);

        if (set != null)
        {
            if (set != null && !"".equals(set)){
                VariableSubstitutor vs = new VariableSubstitutor(idata.getVariables());
                set = vs.substitute(set, null);
            }
            if (set.equals(FALSE))
            {
                checkbox.setSelected(false);
            }
            if (set.equals(TRUE))
            {
                checkbox.setSelected(true);
            }
        }

        // ----------------------------------------------------
        // get the description and add it to the list of UI
        // elements if it exists.
        // ----------------------------------------------------
        XMLElement element = spec.getFirstChildNamed(DESCRIPTION);
        addDescription( element, forPacks, forOs, suppress );

        TwoColumnConstraints constraints = new TwoColumnConstraints();
        constraints.position = TwoColumnConstraints.BOTH;
        constraints.stretch = true;
        constraints.indent = true;

        uiElements.add(new Object[] { suppress, null, CHECK_FIELD, variable, constraints, checkbox, forPacks,
                forOs, trueValue, falseValue});
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Reads the content of the checkbox field and substitutes the associated variable.
     * 
     * @param field the object array that holds the details of the field.
     * 
     * @return <code>true</code> if there was no problem reading the data or if there was an
     * irrecovarable problem. If there was a problem that can be corrected by the operator, an error
     * dialog is popped up and <code>false</code> is returned.
     */
    /*--------------------------------------------------------------------------*/
    protected boolean readCheckBox(Object[] field)
    {
        String variable = null;
        String trueValue = null;
        String falseValue = null;
        JCheckBox box = null;

        try
        {
            box = (JCheckBox) field[POS_FIELD];
            variable = (String) field[POS_VARIABLE];
            trueValue = (String) field[POS_TRUE];
            if (trueValue == null)
            {
                trueValue = "";
            }

            falseValue = (String) field[POS_FALSE];
            if (falseValue == null)
            {
                falseValue = "";
            }
        }
        catch (Throwable exception)
        {
            return (true);
        }

        if (box.isSelected())
        {
            idata.setVariable(variable, trueValue);
            // entries.add(new TextValuePair(variable, trueValue));
        }
        else
        {
            idata.setVariable(variable, falseValue);
            // entries.add(new TextValuePair(variable, falseValue));
        }

        return (true);
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Adds a search field to the list of UI elements.
     * <p>
     * This is a complete example of a valid XML specification
     * 
     * <pre>
     * 
     *  
     *   
     *    &lt;field type=&quot;search&quot; variable=&quot;testVariable&quot;&gt;
     *      &lt;description text=&quot;Description for the search field&quot; id=&quot;a key for translated text&quot;/&gt;
     *      &lt;spec text=&quot;label&quot; id=&quot;key for the label&quot; filename=&quot;the_file_to_search&quot; result=&quot;directory&quot; /&gt; &lt;!-- values for result: directory, file --&gt;
     *        &lt;choice dir=&quot;directory1&quot; set=&quot;true&quot; /&gt; &lt;!-- default value --&gt;
     *        &lt;choice dir=&quot;dir2&quot; /&gt;
     *      &lt;/spec&gt;
     *    &lt;/field&gt;
     *    
     *   
     *  
     * </pre>
     * 
     * @param spec a <code>XMLElement</code> containing the specification for the search field
     */
    /*--------------------------------------------------------------------------*/
    protected void addSearch(XMLElement spec)
    {
        Vector forPacks = spec.getChildrenNamed(SELECTEDPACKS);
        Vector forOs = spec.getChildrenNamed(OS);
        XMLElement element = spec.getFirstChildNamed(SPEC);
        String variable = spec.getAttribute(VARIABLE);
        String filename = null;
        String check_filename = null;
        int search_type = 0;
        int result_type = 0;
        JComboBox combobox = new JComboBox();
        JLabel label = null;

        // System.out.println ("adding search combobox, variable "+variable);

        // allow the user to enter something
        combobox.setEditable(true);

        // ----------------------------------------------------
        // extract the specification details
        // ----------------------------------------------------
        if (element != null)
        {
            label = new JLabel(getText(element));

            // search type is optional (default: file)
            search_type = SearchField.TYPE_FILE;

            String search_type_str = element.getAttribute(SEARCH_TYPE);

            if (search_type_str != null)
            {
                if (search_type_str.equals(SEARCH_FILE))
                {
                    search_type = SearchField.TYPE_FILE;
                }
                else if (search_type_str.equals(SEARCH_DIRECTORY))
                {
                    search_type = SearchField.TYPE_DIRECTORY;
                }
            }

            // result type is mandatory too
            String result_type_str = element.getAttribute(SEARCH_RESULT);

            if (result_type_str == null)
            {
                return;
            }
            else if (result_type_str.equals(SEARCH_FILE))
            {
                result_type = SearchField.RESULT_FILE;
            }
            else if (result_type_str.equals(SEARCH_DIRECTORY))
            {
                result_type = SearchField.RESULT_DIRECTORY;
            }
            else if (result_type_str.equals(SEARCH_PARENTDIR))
            {
                result_type = SearchField.RESULT_PARENTDIR;
            }
            else
            {
                return;
            }

            // might be missing - null is okay
            filename = element.getAttribute(SEARCH_FILENAME);

            check_filename = element.getAttribute(SEARCH_CHECKFILENAME);

            Vector choices = element.getChildrenNamed(SEARCH_CHOICE);

            if (choices == null) { return; }

            for (int i = 0; i < choices.size(); i++)
            {
                XMLElement choice_el = (XMLElement) choices.elementAt(i);

                if (!OsConstraint.oneMatchesCurrentSystem(choice_el)) continue;

                String value = choice_el.getAttribute(SEARCH_VALUE);

                combobox.addItem(value);

                String set = ((XMLElement) choices.elementAt(i)).getAttribute(SET);
                if (set != null)
                {
                    if (set != null && !"".equals(set)){
                        VariableSubstitutor vs = new VariableSubstitutor(idata.getVariables());
                        set = vs.substitute(set, null);
                    }
                    if (set.equals(TRUE))
                    {
                        combobox.setSelectedIndex(i);
                    }
                }
            }
        }
        // ----------------------------------------------------
        // if there is no specification element, return without
        // doing anything.
        // ----------------------------------------------------
        else
        {
            return;
        }

        // ----------------------------------------------------
        // get the description and add it to the list of UI
        // elements if it exists.
        // ----------------------------------------------------
        element = spec.getFirstChildNamed(DESCRIPTION);
        addDescription(element, forPacks, forOs, null );

        TwoColumnConstraints westconstraint1 = new TwoColumnConstraints();
        westconstraint1.position = TwoColumnConstraints.WEST;

        uiElements.add(new Object[] { null, null, FIELD_LABEL, null, westconstraint1, label, forPacks,
                forOs});

        TwoColumnConstraints eastconstraint1 = new TwoColumnConstraints();
        eastconstraint1.position = TwoColumnConstraints.EAST;

        StringBuffer tooltiptext = new StringBuffer();

        if ((filename != null) && (filename.length() > 0))
        {
            tooltiptext.append(MessageFormat.format(parentFrame.langpack
                    .getString("UserInputPanel.search.location"), new String[] { filename}));
        }

        boolean showAutodetect = (check_filename != null) && (check_filename.length() > 0);
        if (showAutodetect)
        {
            tooltiptext.append(MessageFormat.format(parentFrame.langpack
                    .getString("UserInputPanel.search.location.checkedfile"),
                    new String[] { check_filename}));
        }

        if (tooltiptext.length() > 0) combobox.setToolTipText(tooltiptext.toString());

        uiElements.add(new Object[] { null, null, SEARCH_FIELD, variable, eastconstraint1, combobox,
                forPacks, forOs});

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new com.izforge.izpack.gui.FlowLayout(
                com.izforge.izpack.gui.FlowLayout.LEADING));

        JButton autodetectButton = ButtonFactory.createButton(parentFrame.langpack
                .getString("UserInputPanel.search.autodetect"), idata.buttonsHColor);
        autodetectButton.setVisible(showAutodetect);

        autodetectButton.setToolTipText(parentFrame.langpack
                .getString("UserInputPanel.search.autodetect.tooltip"));

        buttonPanel.add(autodetectButton);

        JButton browseButton = ButtonFactory.createButton(parentFrame.langpack
                .getString("UserInputPanel.search.browse"), idata.buttonsHColor);

        buttonPanel.add(browseButton);

        TwoColumnConstraints eastonlyconstraint = new TwoColumnConstraints();
        eastonlyconstraint.position = TwoColumnConstraints.EASTONLY;

        uiElements.add(new Object[] { null, null, SEARCH_BUTTON_FIELD, null, eastonlyconstraint,
                buttonPanel, forPacks, forOs});

        searchFields.add(new SearchField(filename, check_filename, parentFrame, combobox,
                autodetectButton, browseButton, search_type, result_type));
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Reads the content of the search field and substitutes the associated variable.
     * 
     * @param field the object array that holds the details of the field.
     * 
     * @return <code>true</code> if there was no problem reading the data or if there was an
     * irrecovarable problem. If there was a problem that can be corrected by the operator, an error
     * dialog is popped up and <code>false</code> is returned.
     */
    /*--------------------------------------------------------------------------*/
    protected boolean readSearch(Object[] field)
    {
        String variable = null;
        String value = null;
        JComboBox comboBox = null;

        try
        {
            variable = (String) field[POS_VARIABLE];
            comboBox = (JComboBox) field[POS_FIELD];
            for (int i = 0; i < this.searchFields.size(); ++i)
            {
                SearchField sf = (SearchField) this.searchFields.elementAt(i);
                if (sf.belongsTo(comboBox))
                {
                    value = sf.getResult();
                    break;
                }
            }
        }
        catch (Throwable exception)
        {
            return (true);
        }
        if ((variable == null) || (value == null)) { return (true); }

        idata.setVariable(variable, value);
        // entries.add(new TextValuePair(variable, value));
        return (true);
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Adds text to the list of UI elements
     * 
     * @param spec a <code>XMLElement</code> containing the specification for the text.
     */
    /*--------------------------------------------------------------------------*/
    protected void addText(XMLElement spec)
    {
        Vector forPacks = spec.getChildrenNamed(SELECTEDPACKS);
        Vector forOs = spec.getChildrenNamed(OS);
        Vector suppress = getFieldSuppressionList( spec );
        addDescription(spec, forPacks, forOs, suppress );
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Adds a dummy field to the list of UI elements to act as spacer.
     * 
     * @param spec a <code>XMLElement</code> containing other specifications. At present this
     * information is not used but might be in future versions.
     */
    /*--------------------------------------------------------------------------*/
    protected void addSpace(XMLElement spec)
    {
        Vector forPacks = spec.getChildrenNamed(SELECTEDPACKS);
        Vector forOs = spec.getChildrenNamed(OS);
        JPanel panel = new JPanel();

        TwoColumnConstraints constraints = new TwoColumnConstraints();
        constraints.position = TwoColumnConstraints.BOTH;
        constraints.stretch = true;

        uiElements
                .add(new Object[] { null, null, SPACE_FIELD, null, constraints, panel, forPacks, forOs});
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Adds a dividing line to the list of UI elements act as separator.
     * 
     * @param spec a <code>XMLElement</code> containing additional specifications.
     */
    /*--------------------------------------------------------------------------*/
    protected void addDivider(XMLElement spec)
    {
        Vector forPacks = spec.getChildrenNamed(SELECTEDPACKS);
        Vector forOs = spec.getChildrenNamed(OS);
        JPanel panel = new JPanel();
        String alignment = spec.getAttribute(ALIGNMENT);

        if (alignment != null)
        {
            if (alignment.equals(TOP))
            {
                panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.gray));
            }
            else
            {
                panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray));
            }
        }
        else
        {
            panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray));
        }

        TwoColumnConstraints constraints = new TwoColumnConstraints();
        constraints.position = TwoColumnConstraints.BOTH;
        constraints.stretch = true;

        uiElements.add(new Object[] { null, null, DIVIDER_FIELD, null, constraints, panel, forPacks,
                forOs});
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Adds a description to the list of UI elements.
     * 
     * @param spec a <code>XMLElement</code> containing the specification for the description.
     */
    /*--------------------------------------------------------------------------*/
    protected void addDescription(XMLElement spec, Vector forPacks, Vector forOs, Vector suppress )
    {
        String description;
        TwoColumnConstraints constraints = new TwoColumnConstraints();
        constraints.position = TwoColumnConstraints.BOTH;
        constraints.stretch = true;

        if (spec != null)
        {
            description = getText(spec);

            // if we have a description, add it to the UI elements
            if (description != null)
            {
                String alignment = spec.getAttribute(ALIGNMENT);
                int justify = MultiLineLabel.LEFT;

                if (alignment != null)
                {
                    if (alignment.equals(LEFT))
                    {
                        justify = MultiLineLabel.LEFT;
                    }
                    else if (alignment.equals(CENTER))
                    {
                        justify = MultiLineLabel.CENTER;
                    }
                    else if (alignment.equals(RIGHT))
                    {
                        justify = MultiLineLabel.RIGHT;
                    }
                }

                MultiLineLabel label = new MultiLineLabel(description, justify);

                uiElements.add(new Object[] { suppress, null, DESCRIPTION, null, constraints, label,
                        forPacks, forOs});
            }
        }
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Retrieves the value of a boolean attribute. If the attribute is found and the values equals
     * the value of the constant <code>TRUE</code> then true is returned. If it equals
     * <code>FALSE</code> the false is returned. In all other cases, including when the attribute
     * is not found, the default value is returned.
     * 
     * @param element the <code>XMLElement</code> to search for the attribute.
     * @param attribute the attribute to search for
     * @param defaultValue the default value to use if the attribute does not exist or a illegal
     * value was discovered.
     * 
     * @return <code>true</code> if the attribute is found and the value equals the the constant
     * <code>TRUE</code>. <<code> if the
     *            attribute is <code>FALSE</code>. In all other cases the
     *            default value is returned.
     */
    /*--------------------------------------------------------------------------*/
    protected boolean getBoolean(XMLElement element, String attribute, boolean defaultValue)
    {
        boolean result = defaultValue;

        if ((attribute != null) && (attribute.length() > 0))
        {
            String value = element.getAttribute(attribute);

            if (value != null)
            {
                if (value.equals(TRUE))
                {
                    result = true;
                }
                else if (value.equals(FALSE))
                {
                    result = false;
                }
            }
        }

        return (result);
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Retrieves the value of an integer attribute. If the attribute is not found or the value is
     * non-numeric then the default value is returned.
     * 
     * @param element the <code>XMLElement</code> to search for the attribute.
     * @param attribute the attribute to search for
     * @param defaultValue the default value to use in case the attribute does not exist.
     * 
     * @return the value of the attribute. If the attribute is not found or the content is not a
     * legal integer, then the default value is returned.
     */
    /*--------------------------------------------------------------------------*/
//    private int getInt(XMLElement element, String attribute, int defaultValue)
//    {
//        int result = defaultValue;
//
//        if ((attribute != null) && (attribute.length() > 0))
//        {
//            try
//            {
//                result = Integer.parseInt(element.getAttribute(attribute));
//            }
//            catch (Throwable exception)
//            {}
//        }
//
//        return (result);
//    }

    /*--------------------------------------------------------------------------*/
    /**
     * Retrieves the value of a floating point attribute. If the attribute is not found or the value
     * is non-numeric then the default value is returned.
     * 
     * @param element the <code>XMLElement</code> to search for the attribute.
     * @param attribute the attribute to search for
     * @param defaultValue the default value to use in case the attribute does not exist.
     * 
     * @return the value of the attribute. If the attribute is not found or the content is not a
     * legal integer, then the default value is returned.
     */
    /*--------------------------------------------------------------------------*/
    protected float getFloat(XMLElement element, String attribute, float defaultValue)
    {
        float result = defaultValue;

        if ((attribute != null) && (attribute.length() > 0))
        {
            try
            {
                result = Float.parseFloat(element.getAttribute(attribute));
            }
            catch (Throwable exception)
            {}
        }

        return (result);
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Extracts the text from an <code>XMLElement</code>. The text must be defined in the
     * resource file under the key defined in the <code>id</code> attribute or as value of the
     * attribute <code>text</code>.
     * 
     * @param element the <code>XMLElement</code> from which to extract the text.
     * 
     * @return The text defined in the <code>XMLElement</code>. If no text can be located,
     * <code>null</code> is returned.
     */
    /*--------------------------------------------------------------------------*/
    protected String getText(XMLElement element)
    {
        if (element == null) { return (null); }

        String key = element.getAttribute(KEY);
        String text = null;

        if ((key != null) && (langpack != null))
        {
            try
            {
                text = langpack.getString(key);
            }
            catch (Throwable exception)
            {
                text = null;
            }
        }

        // if there is no text in the description, then
        // we were unable to retrieve it form the resource.
        // In this case try to get the text directly from
        // the XMLElement
        if (text == null)
        {
            text = element.getAttribute(TEXT);
        }

        return (text);
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Retreives the alignment setting for the <code>XMLElement</code>. The default value in case
     * the <code>ALIGNMENT</code> attribute is not found or the value is illegal is
     * <code>TwoColumnConstraints.LEFT</code>.
     * 
     * @param element the <code>XMLElement</code> from which to extract the alignment setting.
     * 
     * @return the alignement setting for the <code>XMLElement</code>. The value is either
     * <code>TwoColumnConstraints.LEFT</code>, <code>TwoColumnConstraints.CENTER</code> or
     * <code>TwoColumnConstraints.RIGHT</code>.
     * 
     * @see com.izforge.izpack.gui.TwoColumnConstraints
     */
    /*--------------------------------------------------------------------------*/
    protected int getAlignment(XMLElement element)
    {
        int result = TwoColumnConstraints.LEFT;

        String value = element.getAttribute(ALIGNMENT);

        if (value != null)
        {
            if (value.equals(LEFT))
            {
                result = TwoColumnConstraints.LEFT;
            }
            else if (value.equals(CENTER))
            {
                result = TwoColumnConstraints.CENTER;
            }
            else if (value.equals(RIGHT))
            {
                result = TwoColumnConstraints.RIGHT;
            }
        }

        return (result);
    }

    /**
     * Verifies if an item is required for the operating system the installer executed. The
     * configuration for this feature is: <br/> &lt;os family="unix"/&gt; <br>
     * <br>
     * <b>Note:</b><br>
     * If the list of the os is empty then <code>true</code> is always returnd.
     * 
     * @param os The <code>Vector</code> of <code>String</code>s. containing the os names
     * 
     * @return <code>true</code> if the item is required for the os, otherwise returns
     * <code>false</code>.
     */
    public boolean itemRequiredForOs(Vector os)
    {
        if (os.size() == 0) { return true; }

        for (int i = 0; i < os.size(); i++)
        {
            String family = ((XMLElement) os.elementAt(i)).getAttribute(FAMILY);
            boolean match = false;

            if (family.equals("windows"))
            {
                match = OsVersion.IS_WINDOWS;
            }
            else if (family.equals("mac"))
            {
                match = OsVersion.IS_OSX;
            }
            else if (family.equals("unix"))
            {
                match = OsVersion.IS_UNIX;
            }
            return match;
        }
        return false;
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Verifies if an item is required for any of the packs listed. An item is required for a pack
     * in the list if that pack is actually selected for installation. <br>
     * <br>
     * <b>Note:</b><br>
     * If the list of selected packs is empty then <code>true</code> is always returnd. The same
     * is true if the <code>packs</code> list is empty.
     * 
     * @param packs a <code>Vector</code> of <code>String</code>s. Each of the strings denotes
     * a pack for which an item should be created if the pack is actually installed.
     * 
     * @return <code>true</code> if the item is required for at least one pack in the list,
     * otherwise returns <code>false</code>.
     */
    /*--------------------------------------------------------------------------*/
    /*
     * $ @design
     * 
     * The information about the installed packs comes from InstallData.selectedPacks. This assumes
     * that this panel is presented to the user AFTER the PacksPanel.
     * --------------------------------------------------------------------------
     */
    protected boolean itemRequiredFor(Vector packs)
    {

        String selected;
        String required;

        if (packs.size() == 0) { return (true); }

        // ----------------------------------------------------
        // We are getting to this point if any packs have been
        // specified. This means that there is a possibility
        // that some UI elements will not get added. This
        // means that we can not allow to go back to the
        // PacksPanel, because the process of building the
        // UI is not reversable.
        // ----------------------------------------------------
        // packsDefined = true;

        // ----------------------------------------------------
        // analyze if the any of the packs for which the item
        // is required have been selected for installation.
        // ----------------------------------------------------
        for (int i = 0; i < idata.selectedPacks.size(); i++)
        {
            selected = ((Pack) idata.selectedPacks.get(i)).name;

            for (int k = 0; k < packs.size(); k++)
            {
                required = (String) ((XMLElement) packs.elementAt(k)).getAttribute(NAME, "");
                if (selected.equals(required)) { return (true); }
            }
        }

        return (false);
    }

    /*--------------------------------------------------------------------------*/
    /**
     * Verifies if an item is required for any of the packs listed. An item is required for a pack
     * in the list if that pack is actually NOT selected for installation. <br>
     * <br>
     * <b>Note:</b><br>
     * If the list of selected packs is empty then <code>true</code> is always returnd. The same
     * is true if the <code>packs</code> list is empty.
     * 
     * @param packs a <code>Vector</code> of <code>String</code>s. Each of the strings denotes
     * a pack for which an item should be created if the pack is actually installed.
     * 
     * @return <code>true</code> if the item is required for at least one pack in the list,
     * otherwise returns <code>false</code>.
     */
    /*--------------------------------------------------------------------------*/
    /*
     * $ @design
     * 
     * The information about the installed packs comes from InstallData.selectedPacks. This assumes
     * that this panel is presented to the user AFTER the PacksPanel.
     * --------------------------------------------------------------------------
     */
    protected boolean itemRequiredForUnselected(Vector packs)
    {

        String selected;
        String required;

        if (packs.size() == 0) { return (true); }

        // ----------------------------------------------------
        // analyze if the any of the packs for which the item
        // is required have been selected for installation.
        // ----------------------------------------------------
        for (int i = 0; i < idata.selectedPacks.size(); i++)
        {
            selected = ((Pack) idata.selectedPacks.get(i)).name;

            for (int k = 0; k < packs.size(); k++)
            {
                required = (String) ((XMLElement) packs.elementAt(k)).getAttribute(NAME, "");
                if (selected.equals(required)) { return (false); }
            }
        }

        return (true);
    }

    // ----------- Inheritance stuff -----------------------------------------
    /**
     * Returns the uiElements.
     * 
     * @return Returns the uiElements.
     */
    protected Vector getUiElements()
    {
        return uiElements;
    }

    // --------------------------------------------------------------------------
    // Inner Classes
    // --------------------------------------------------------------------------

    /*---------------------------------------------------------------------------*/
    /**
     * This class can be used to associate a text string and a (text) value.
     */
    /*---------------------------------------------------------------------------*/
    protected static class TextValuePair
    {

        private String text = "";

        private String value = "";

        /*--------------------------------------------------------------------------*/
        /**
         * Constructs a new Text/Value pair, initialized with the text and a value.
         * 
         * @param text the text that this object should represent
         * @param value the value that should be associated with this object
         */
        /*--------------------------------------------------------------------------*/
        public TextValuePair(String text, String value)
        {
            this.text = text;
            this.value = value;
        }

        /*--------------------------------------------------------------------------*/
        /**
         * Sets the text
         * 
         * @param text the text for this object
         */
        /*--------------------------------------------------------------------------*/
        public void setText(String text)
        {
            this.text = text;
        }

        /*--------------------------------------------------------------------------*/
        /**
         * Sets the value of this object
         * 
         * @param value the value for this object
         */
        /*--------------------------------------------------------------------------*/
        public void setValue(String value)
        {
            this.value = value;
        }

        /*--------------------------------------------------------------------------*/
        /**
         * This method returns the text that was set for the object
         * 
         * @return the object's text
         */
        /*--------------------------------------------------------------------------*/
        public String toString()
        {
            return (text);
        }

        /*--------------------------------------------------------------------------*/
        /**
         * This method returns the value that was associated with this object
         * 
         * @return the object's value
         */
        /*--------------------------------------------------------------------------*/
        public String getValue()
        {
            return (value);
        }
    }

    /*---------------------------------------------------------------------------*/
    /**
     * This class encapsulates a lot of search field functionality.
     * 
     * A search field supports searching directories and files on the target system. This is a
     * helper class to manage all data belonging to a search field.
     */
    /*---------------------------------------------------------------------------*/

    protected class SearchField implements ActionListener
    {

        /** used in constructor - we search for a directory. */
        public static final int TYPE_DIRECTORY = 1;

        /** used in constructor - we search for a file. */
        public static final int TYPE_FILE = 2;

        /** used in constructor - result of search is the directory. */
        public static final int RESULT_DIRECTORY = 1;

        /** used in constructor - result of search is the whole file name. */
        public static final int RESULT_FILE = 2;

        /** used in constructor - result of search is the parent directory. */
        public static final int RESULT_PARENTDIR = 3;

        protected String filename = null;

        protected String checkFilename = null;

        protected JButton autodetectButton = null;

        protected JButton browseButton = null;

        protected JComboBox pathComboBox = null;

        protected int searchType = TYPE_DIRECTORY;

        protected int resultType = RESULT_DIRECTORY;

        protected InstallerFrame parent = null;

        /*---------------------------------------------------------------------------*/
        /**
         * Constructor - initializes the object, adds it as action listener to the "autodetect"
         * button.
         * 
         * @param filename the name of the file to search for (might be null for searching
         * directories)
         * @param checkFilename the name of the file to check when searching for directories (the
         * checkFilename is appended to a found directory to figure out whether it is the right
         * directory)
         * @param combobox the <code>JComboBox</code> holding the list of choices; it should be
         * editable and contain only Strings
         * @param autobutton the autodetection button for triggering autodetection
         * @param browsebutton the browse button to look for the file
         * @param search_type what to search for - TYPE_FILE or TYPE_DIRECTORY
         * @param result_type what to return as the result - RESULT_FILE or RESULT_DIRECTORY or
         * RESULT_PARENTDIR
         */
        /*---------------------------------------------------------------------------*/
        public SearchField(String filename, String checkFilename, InstallerFrame parent,
                JComboBox combobox, JButton autobutton, JButton browsebutton, int search_type,
                int result_type)
        {
            this.filename = filename;
            this.checkFilename = checkFilename;
            this.parent = parent;
            this.autodetectButton = autobutton;
            this.browseButton = browsebutton;
            this.pathComboBox = combobox;
            this.searchType = search_type;
            this.resultType = result_type;

            this.autodetectButton.addActionListener(this);
            this.browseButton.addActionListener(this);

            autodetect();
        }

        /**
         * Check whether the given combobox belongs to this searchfield. This is used when reading
         * the results.
         */
        public boolean belongsTo(JComboBox combobox)
        {
            return (this.pathComboBox == combobox);
        }

        /** check whether the given path matches */
        protected boolean pathMatches(String path)
        {
            if (path != null)
            { // Make sure, path is not null
                // System.out.println ("checking path " + path);

                File file = null;

                if ((this.filename == null) || (this.searchType == TYPE_DIRECTORY))
                {
                    file = new File(path);
                }
                else
                {
                    file = new File(path, this.filename);
                }

                if (file.exists())
                {

                    if (((this.searchType == TYPE_DIRECTORY) && (file.isDirectory()))
                            || ((this.searchType == TYPE_FILE) && (file.isFile())))
                    {
                        // no file to check for
                        if (this.checkFilename == null) return true;

                        file = new File(file, this.checkFilename);

                        return file.exists();
                    }

                }

                // System.out.println (path + " did not match");
            } // end if
            return false;
        }

        /** perform autodetection */
        public boolean autodetect()
        {
            Vector items = new Vector();

            /*
             * Check if the user has entered data into the ComboBox and add it to the Itemlist
             */
            String selected = (String) this.pathComboBox.getSelectedItem();
            boolean found = false;
            for (int x = 0; x < this.pathComboBox.getItemCount(); x++)
            {
                if (((String) this.pathComboBox.getItemAt(x)).equals(selected))
                {
                    found = true;
                }
            }
            if (!found)
            {
                // System.out.println("Not found in Itemlist");
                this.pathComboBox.addItem(this.pathComboBox.getSelectedItem());
            }

            // Checks whether a placeholder item is in the combobox
            // and resolve the pathes automatically:
            // /usr/lib/* searches all folders in usr/lib to find
            // /usr/lib/*/lib/tools.jar
            for (int i = 0; i < this.pathComboBox.getItemCount(); ++i)
            {
                String path = (String) this.pathComboBox.getItemAt(i);

                if (path.endsWith("*"))
                {
                    path = path.substring(0, path.length() - 1);
                    File dir = new File(path);

                    if (dir.isDirectory())
                    {
                        File[] subdirs = dir.listFiles();
                        for (int x = 0; x < subdirs.length; x++)
                        {
                            String search = subdirs[x].getAbsolutePath();
                            if (this.pathMatches(search))
                            {
                                items.add(search);
                            }
                        }
                    }
                }
                else
                {
                    if (this.pathMatches(path))
                    {
                        items.add(path);
                    }
                }
            }
            // Make the enties in the vector unique
            items = new Vector(new HashSet(items));

            // Now clear the combobox and add the items out of the newly
            // generated vector
            this.pathComboBox.removeAllItems();
            VariableSubstitutor vs = new VariableSubstitutor(idata.getVariables());
            for (int i = 0; i < items.size(); i++)
            {
                this.pathComboBox.addItem(vs.substitute((String) items.get(i), "plain"));
            }

            // loop through all items
            for (int i = 0; i < this.pathComboBox.getItemCount(); ++i)
            {
                String path = (String) this.pathComboBox.getItemAt(i);

                if (this.pathMatches(path))
                {
                    this.pathComboBox.setSelectedIndex(i);
                    return true;
                }

            }

            // if the user entered something else, it's not listed as an item
            if (this.pathMatches((String) this.pathComboBox.getSelectedItem())) { return true; }

            return false;
        }

        /*--------------------------------------------------------------------------*/
        /**
         * This is called if one of the buttons has bee pressed.
         * 
         * It checks, which button caused the action and acts accordingly.
         */
        /*--------------------------------------------------------------------------*/
        public void actionPerformed(ActionEvent event)
        {
            //System.out.println ("autodetection button pressed.");

            if (event.getSource() == this.autodetectButton)
            {
                if (!autodetect())
                    JOptionPane.showMessageDialog(parent, parent.langpack
                            .getString("UserInputPanel.search.autodetect.failed.message"),
                            parent.langpack
                                    .getString("UserInputPanel.search.autodetect.failed.caption"),
                            JOptionPane.WARNING_MESSAGE);
            }
            else if (event.getSource() == this.browseButton)
            {
                JFileChooser chooser = new JFileChooser();

                if (this.searchType == RESULT_DIRECTORY)
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int result = chooser.showOpenDialog(this.parent);

                if (result == JFileChooser.APPROVE_OPTION)
                {
                    File f = chooser.getSelectedFile();

                    this.pathComboBox.setSelectedItem(f.getAbsolutePath());

                    // use any given directory directly
                    if (!this.pathMatches(f.getAbsolutePath()))
                    {
                        JOptionPane.showMessageDialog(parent, parent.langpack
                                .getString("UserInputPanel.search.wrongselection.message"),
                                parent.langpack
                                        .getString("UserInputPanel.search.wrongselection.caption"),
                                JOptionPane.WARNING_MESSAGE);
                    }
                }

            }

            // we don't care for anything more here - getResult() does the rest
        }

        /*--------------------------------------------------------------------------*/
        /**
         * Return the result of the search according to result type.
         * 
         * Sometimes, the whole path of the file is wanted, sometimes only the directory where the
         * file is in, sometimes the parent directory.
         * 
         * @return null on error
         */
        /*--------------------------------------------------------------------------*/
        public String getResult()
        {
            String item = (String) this.pathComboBox.getSelectedItem();
            if (item != null) item = item.trim();
            String path = item;

            File f = new File(item);

            if (!f.isDirectory())
            {
                path = f.getParent();
            }

            // path now contains the final content of the combo box
            if (this.resultType == RESULT_DIRECTORY)
            {
                return path;
            }
            else if (this.resultType == RESULT_FILE)
            {
                if (this.filename != null)
                {
                    return path + File.separatorChar + this.filename;
                }
                else
                {
                    return item;
                }
            }
            else if (this.resultType == RESULT_PARENTDIR)
            {
                File dir = new File(path);
                return dir.getParent();
            }

            return null;
        }

    } // private class SearchFile


} // public class UserInputPanel
/*---------------------------------------------------------------------------*/

class VCheckBox extends JCheckBox implements ItemListener {
   String myVarName = null;
   Vector dependsOnVars = new Vector();
   Vector exclusiveOfVars = new Vector();
   Vector dependents = new Vector();
   HashMap varMap = null; // gets set as ref to static varMap defined in ValidatePackSelections
   InstallData idata = null;
   private VCheckBox() {
   }
   // currently, VCheckBox variables are "true" or "false"
   // the true="xxx" and false="xxx" attributes of <spec> are not
   // honored at this time.
   protected VCheckBox( String label, HashMap varMap, InstallData idata, XMLElement spec ) {
      super( label );
      this.idata = idata;
      myVarName = spec.getAttribute(ValidatePackSelections.VARIABLE);
      this.varMap = varMap;
      if( myVarName != null )
         varMap.put( myVarName, this );
      Vector vSpec = spec.getChildrenNamed( ValidatePackSelections.SPEC );
      if( vSpec.size() > 0 ) {
          String myInitialValue = (String)((XMLElement)(vSpec.elementAt(0))).getAttribute(ValidatePackSelections.SET );
          if( myInitialValue == null ) myInitialValue = "false";
          setDesiredState( myInitialValue.equalsIgnoreCase("true")?true:false );
      }
      Vector depends = spec.getChildrenNamed( ValidatePackSelections.DEPENDS );
      for( int i =0; i < depends.size(); ++i ) {
         String varName = (String)((XMLElement)(depends.elementAt(i))).getAttribute( ValidatePackSelections.VARIABLE );
         Debug.trace( "VCheckBox: " + myVarName + " depends on: " + varName );
         dependsOnVars.add( varName );
      }
      Vector exclusiveOf = spec.getChildrenNamed( ValidatePackSelections.EXCLUSIVE );
      for( int i =0; i < exclusiveOf.size(); ++i ) {
         String varName = (String)((XMLElement)(exclusiveOf.elementAt(i))).getAttribute( ValidatePackSelections.VARIABLE );
         Debug.trace( "VCheckBox: " + myVarName + " exclusive of: " + varName );
         exclusiveOfVars.add( varName );
      }
      addItemListener( this );
  }
  protected String getVarName() {
     return myVarName;
  }
  public void setDesiredState( boolean b ) {
     setSelected( b );
     idata.setVariable( myVarName, String.valueOf( b ));
  }


  protected void setupDependents() {
        // must be called by main line processing after all fields created,
        // but before first is shown. Currently, this is done using a static
        // boolean var in the panelActivate method
        for( int i = 0; i < dependsOnVars.size(); ++i ) {
           String depVar = (String)dependsOnVars.elementAt( i );
           VCheckBox dependOn = (VCheckBox)varMap.get( depVar );
           if( dependOn == null ) {
              System.out.println( "Packaging error. Depends on variable: " + depVar + " is not defined" );
           } else {
              dependOn.addDependent( myVarName );
              Debug.trace( dependOn.myVarName + ".addDependent(" + myVarName + ")" );
           }
        }
  }
  protected void addDependent( String depVarName ) {
     dependents.add( depVarName );
  }

  public void itemStateChanged( ItemEvent event ) {
     if( isSelected() ) 
        idata.setVariable( myVarName, "true" );
     else idata.setVariable( myVarName, "false" );

     setValidStates( true );
  }
  public void setValidStates() {
     setValidStates( false );
  }
  public boolean reverseDepsSelected() {
     // return indication that all reverse dependencies
     // are selected to determine if self should
     // be enabled (not selected though)
     boolean allEnabled = true; // def to true in case no rev deps exist
     for( int i = 0; i < dependsOnVars.size(); ++i ) {
        String depOn = (String)dependsOnVars.elementAt(i);
        if( depOn != null ) {
           VCheckBox vDepOn = (VCheckBox)varMap.get( depOn );
           if( vDepOn == null ) {
              Exception e = new NullPointerException( "Invalid state" );
              e.printStackTrace();
              throw new RuntimeException( e );
           }
           if( vDepOn.isSelected() == false ) { 
              allEnabled = false;
              break;
           }
        }
     }
     return allEnabled;
  }

  public void setValidStates( boolean fDirectlyFromEvent ) {
     setToVariableValue( idata );
     boolean isSelected = isSelected();
     if( reverseDepsSelected() == false ) {
        setDesiredState( false ); 
        setEnabled( false );
     }
     for( int i = 0; i < dependents.size(); ++i ) {
        String depVar = (String)dependents.elementAt( i );
        VCheckBox dep = (VCheckBox)varMap.get( depVar );
        if( dep != null ) {
           if( isSelected ) {
              // enable all our dependents, but leave selected state alone
              dep.setActive( true );
              dep.setEnabled( true );
           } else {
              // disable all our dependents and set selected state to false.
              dep.setDesiredState( false );
              dep.setActive( false );
              dep.setEnabled( false );
           }
        }
     }
     if( fDirectlyFromEvent ) {
        for( int i = 0; i < exclusiveOfVars.size(); ++i ) {
           String xofVar = (String)exclusiveOfVars.elementAt( i );
           VCheckBox xof = (VCheckBox)varMap.get( xofVar );
           if( xof != null ) {
              if( isSelected() ) {
                 xof.setDesiredState( false );
                 xof.setActive( false );
                 xof.setEnabled( false );
              } else {
                 xof.setActive( true );
                 xof.setEnabled( true );
              }
           }
        }
     }
  }

  protected void setToVariableValue( InstallData idata ) {
     Debug.trace( ">VCheckBox.setToVariableValue( " + myVarName + ")" );
     String value = idata.getVariable( myVarName );
     boolean selected = false;
     if( value != null && value.equalsIgnoreCase( "true" )) 
        selected = true;
     setSelected( selected );
     Debug.trace( "-VCheckBox.setToVariableValue() new value is: " + selected );
     Debug.trace( "<VCheckBox.setToVariableValue( " + myVarName + ")" );
  }

  public void setActive( boolean fActive ) {
     Debug.trace( ">VCheckBox.setActive( " + fActive + ") : " + myVarName );
     for( int i = 0; i < dependents.size(); ++i ) {
        String depVar = (String)dependents.elementAt( i );
        VCheckBox dep = (VCheckBox)varMap.get( depVar );
        if( dep != null ) {
           if( fActive ) {
              // enable all our dependents, but leave selected state alone
              dep.setActive( true );
              dep.setEnabled( true );
           } else {
              // disable all our dependents and set selected state to false.
              dep.setActive( false );
              dep.setDesiredState( false );
              dep.setEnabled( false );
           }
        }
     }
     Debug.trace( "<VCheckBox.setActive( " + fActive + ")" );
  }
}
