/*
 * Created on 18/08/2004
 *
 */
package com.majitek.maji.rpc.tutorial;

import javax.swing.JPanel;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JSlider;
import javax.swing.JList;
import javax.swing.JLabel;

import java.awt.event.ItemEvent;
import java.lang.String;
import java.net.MalformedURLException;


import javax.swing.JToggleButton;

import com.majitek.maji.rpc.binding.facet.InboundBinary;
import com.majitek.maji.rpc.binding.facet.OutboundBinary;
import com.majitek.maji.rpc.client.MajiRPCClient;

import javax.swing.BoxLayout;

import org.openmaji.common.IntegerPosition;
import org.openmaji.common.Position;
import org.openmaji.common.StringValue;
import org.openmaji.common.Value;

import com.majitek.maji.rpc.binding.facet.OutboundLinear;
import com.majitek.maji.rpc.binding.facet.InboundLinear;
import com.majitek.maji.rpc.binding.facet.OutboundVariable;
import com.majitek.maji.rpc.binding.facet.InboundVariable;
import com.majitek.maji.rpc.binding.facet.OutboundVariableList;
import com.majitek.maji.rpc.binding.facet.InboundVariableList;

import java.awt.BorderLayout;

/**
 * RPCPanel
 * 
 * Tutorial code.
 * 
 * @author Warren Bloomer
 *
 */
public class RPCPanel extends JPanel {

	private JLabel jLabel = null;
	
	// text-Value demo
	private JPanel     jPanelText        = null;
	private JPanel     jPanelTextInput   = null;
	private JButton    jButtonTextSubmit = null;
	private JTextField jTextField        = null;
	
	// list-ValueList demo
	private JPanel     jPanelList    = null;
	private JList      jListControl  = null;
	private JList      jListFeedback = null;
	
	// button-Binary demo
	private JPanel        jPanelButton          = null;
	private JToggleButton jToggleButtonControl  = null;
	private JToggleButton jToggleButtonFeedback = null;
	
	// slider-Linear demo
	private JPanel  jPanelSliders        = null;  //  @jve:decl-index=0:visual-constraint="16,254"
	private JPanel  jPanelSliderControl  = null;
	private JPanel  jPanelSliderFeedback = null;
	private JSlider jSliderControl       = null;
	private JSlider jSliderFeedback      = null;

	// Bindings
	private MajiRPCClient        majiRPCClient        = null;  //  @jve:decl-index=0:visual-constraint="728,26"
	private OutboundBinary       outboundBoolean      = null;  //  @jve:decl-index=0:visual-constraint="586,99"
	private InboundBinary        inboundBoolean       = null;  //  @jve:decl-index=0:visual-constraint="731,97"
	private OutboundLinear       outboundLinear       = null;  //  @jve:decl-index=0:visual-constraint="586,148"
	private InboundLinear        inboundLinear        = null;  //  @jve:decl-index=0:visual-constraint="746,146"
	private OutboundVariable     outboundVariable     = null;  //  @jve:decl-index=0:visual-constraint="584,208"
	private InboundVariable      inboundVariable      = null;  //  @jve:decl-index=0:visual-constraint="748,207"
	private OutboundVariableList outboundVariableList = null;  //  @jve:decl-index=0:visual-constraint="579,266"
	private InboundVariableList  inboundVariableList  = null;  //  @jve:decl-index=0:visual-constraint="747,271"
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("XML RPC Test Panel");
		frame.getContentPane().add(new RPCPanel());
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	/**
	 * This is the default constructor
	 */
	public RPCPanel() {
		super();
		initialize();
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private  void initialize() {
		
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new java.awt.Dimension(420,276));
		this.add(getJPanelText(),    java.awt.BorderLayout.NORTH);
		this.add(getJPanelButton(),  java.awt.BorderLayout.WEST);
		this.add(getJPanelList(),    java.awt.BorderLayout.EAST);
		this.add(getJPanelSliders(), java.awt.BorderLayout.SOUTH);
		
		// make sure these beans are created
		getInboundBoolean();
		getInboundLinear();
		getInboundVariable();
		getInboundVariableList();
	}

	/**
	 * This method initializes jPanelText	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanelText() {
		if (jPanelText == null) {
			jPanelText = new JPanel();
			jPanelText.setLayout(new BoxLayout(jPanelText, BoxLayout.X_AXIS));
			jPanelText.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Text", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			jPanelText.add(getJPanelTextInput(), null);
			jPanelText.add(getJLabel(), null);
		}
		return jPanelText;
	}
	
	/**
	 * This method initializes jPanel8	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanelTextInput() {
		if (jPanelTextInput == null) {
			jPanelTextInput = new JPanel();
			jPanelTextInput.setPreferredSize(new java.awt.Dimension(120,36));
			jPanelTextInput.add(getJTextField(), null);
			jPanelTextInput.add(getJButtonTextSubmit(), null);
		}
		return jPanelTextInput;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setPreferredSize(new java.awt.Dimension(120,20));
		}
		return jTextField;
	}

	/**
	 * This method initializes jButton3	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButtonTextSubmit() {
		if (jButtonTextSubmit == null) {
			jButtonTextSubmit = new JButton();
			jButtonTextSubmit.setText("Submit");
			jButtonTextSubmit.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {  
					Value value = new StringValue(getJTextField().getText());
					getOutboundVariable().valueChanged(value);
				}
			});
		}
		return jButtonTextSubmit;
	}

	/**
	 * 
	 * @return
	 */
	private JLabel getJLabel() {
		if (jLabel == null) {
			jLabel = new JLabel();
			jLabel.setText("Value");
			jLabel.setEnabled(false);
			jLabel.setPreferredSize(new java.awt.Dimension(180,16));
			jLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		}
		return jLabel;
	}
		
	/**
	 * This method initializes jPanel9	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanelList() {
		if (jPanelList == null) {
			jPanelList = new JPanel();
			jPanelList.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "List", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			jPanelList.add(getJListControl(), null);
			jPanelList.add(getJListFeedback(), null);
		}
		return jPanelList;
	}
	
	private JList getJListControl() {
		if (jListControl == null) {
			jListControl = new JList();
			jListControl.setPreferredSize(new java.awt.Dimension(100,100));
			jListControl.setToolTipText("List of Values");
			jListControl.addListSelectionListener(new javax.swing.event.ListSelectionListener() { 
				public void valueChanged(javax.swing.event.ListSelectionEvent e) { 
					if (!e.getValueIsAdjusting()) {
						Object[] selectedValues = jListControl.getSelectedValues();
						OutboundVariableList list = getOutboundVariableList();
						Value[] valueList = new Value[selectedValues.length];
						for (int i=0; i<valueList.length; i++) {
							valueList[i] = new StringValue((String)selectedValues[i]);
						}
						list.valueChanged(valueList);
					}
				}
			});
			Object[] data = new Object[] { 
					"One", "Two", "Three",
					};
			jListControl.setListData(data);
		}
		return jListControl;
	}
	
	/**
	 * This method initializes jList1	
	 * 	
	 * @return javax.swing.JList	
	 */    
	private JList getJListFeedback() {
		if (jListFeedback == null) {
			jListFeedback = new JList();
			jListFeedback.setPreferredSize(new java.awt.Dimension(100,100));
			jListFeedback.setEnabled(false);
		}
		return jListFeedback;
	}
	
	/**
	 * This method initializes jPanelButton	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanelButton() {
		if (jPanelButton == null) {
			jPanelButton = new JPanel();
			jPanelButton.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Toggle Buttons", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			jPanelButton.setPreferredSize(new java.awt.Dimension(140,60));
			jPanelButton.add(getJToggleButtonControl(), null);
			jPanelButton.add(getJToggleButtonFeedback(), null);
		}
		return jPanelButton;
	}
	
	/**
	 * This method initializes jToggleButton	
	 * 	
	 * @return javax.swing.JToggleButton	
	 */    
	private JToggleButton getJToggleButtonControl() {
		
		if (jToggleButtonControl == null) {
			jToggleButtonControl = new JToggleButton();
			jToggleButtonControl.setText("Toggle Button");
			
			jToggleButtonControl.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {  
					getOutboundBinary().valueChanged(e.getStateChange() == ItemEvent.SELECTED);
				}
			});
		}
		return jToggleButtonControl;
	}

	/**
	 * This method initializes jToggleButtonFeedback	
	 * 	
	 * @return javax.swing.JToggleButton	
	 */    
	private JToggleButton getJToggleButtonFeedback() {
		if (jToggleButtonFeedback == null) {
			jToggleButtonFeedback = new JToggleButton();
			jToggleButtonFeedback.setText("Feedback");
			jToggleButtonFeedback.setEnabled(true);
		}
		return jToggleButtonFeedback;
	}
	
	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanelSliders() {
		if (jPanelSliders == null) {
			jPanelSliders = new JPanel();
			jPanelSliders.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Sliders", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			jPanelSliders.add(getJPanelSliderControl(), null);
			jPanelSliders.add(getJPanelSliderFeedback(), null);
		}
		return jPanelSliders;
	}
	
	/**
	 * This method initializes jPanelSliderControl
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanelSliderControl() {
		if (jPanelSliderControl == null) {
			JLabel label = new JLabel();
			label.setText("Control");
			jPanelSliderControl = new JPanel();
			jPanelSliderControl.setLayout(new BoxLayout(jPanelSliderControl, BoxLayout.Y_AXIS));
			jPanelSliderControl.add(label, null);
			jPanelSliderControl.add(getJSliderControl(), null);
		}
		return jPanelSliderControl;
	}
	
	/**
	 * This method initializes jPanelSliderFeedback	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanelSliderFeedback() {
		if (jPanelSliderFeedback == null) {
			JLabel label = new JLabel();
			label.setText("Feedback");
			jPanelSliderFeedback = new JPanel();
			jPanelSliderFeedback.setLayout(new BoxLayout(jPanelSliderFeedback, BoxLayout.Y_AXIS));
			jPanelSliderFeedback.add(label, null);
			jPanelSliderFeedback.add(getJSliderFeedback(), null);
		}
		return jPanelSliderFeedback;
	}
	
	/**
	 * This method initializes jSliderControl	
	 * 	
	 * @return javax.swing.JSlider	
	 */    
	private JSlider getJSliderControl() {
		if (jSliderControl == null) {
			jSliderControl = new JSlider();
			jSliderControl.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					Position p = new IntegerPosition(
							jSliderControl.getValue(), 
							1, 
							jSliderControl.getMinimum(), 
							jSliderControl.getMaximum()
						);
					getOutboundLinear().valueChanged(p);
				}
			});
		}
		return jSliderControl;
	}
	
	/**
	 * This method initializes jSliderFeedback	
	 * 	
	 * @return javax.swing.JSlider	
	 */    
	private JSlider getJSliderFeedback() {
		if (jSliderFeedback == null) {
			jSliderFeedback = new JSlider();
			jSliderFeedback.setEnabled(false);
		}
		return jSliderFeedback;
	}
	
	
	/**
	 * This method initializes XMLRPCClient	
	 * 	
	 * @return com.majitek.dashboard.xmlrpc.XMLRPCClient	
	 */
	private MajiRPCClient getMajiRPCClient() {
		if (majiRPCClient == null) {
			majiRPCClient = new MajiRPCClient();
			try {
				majiRPCClient.setAddress("http://localhost:8000/maji/rpc");
				majiRPCClient.setUsername("guest");
				majiRPCClient.setPassword("guest99");
			}
			catch (MalformedURLException ex)  {
				ex.printStackTrace();
			}
		}
		return majiRPCClient;
	}
	
	/**
	 * This method initializes outboundBoolean	
	 * 	
	 * @return com.majitek.dashboard.binding.OutboundBoolean	
	 */    
	private OutboundBinary getOutboundBinary() {
		if (outboundBoolean == null) {
			outboundBoolean = new OutboundBinary();
			outboundBoolean.setFacetId("binaryInput");
			outboundBoolean.setMeemPath("hyperspace:/user/guest/test/LoopbackBinary");
			outboundBoolean.setFacetEventListener(getMajiRPCClient());
		}
		return outboundBoolean;
	}

	/**
	 * This method initializes inboundBoolean	
	 * 	
	 * @return com.majitek.dashboard.binding.InboundBoolean	
	 */    
	private InboundBinary getInboundBoolean() {
		if (inboundBoolean == null) {
			inboundBoolean = new InboundBinary();
			inboundBoolean.setMeemPath("hyperspace:/user/guest/test/LoopbackBinary");
			inboundBoolean.setFacetId("binaryOutput");
			inboundBoolean.setFacetEventSender(getMajiRPCClient());
			
			inboundBoolean.addBinaryFacet(new org.openmaji.common.Binary() { 
				public void valueChanged(boolean b) {
					getJToggleButtonFeedback().setSelected(b);
				}
			});
		}
		return inboundBoolean;
	}
	
	/**
	 * This method initializes outboundLinear	
	 * 	
	 * @return com.majitek.maji.rpc.binding.facet.OutboundLinear	
	 */    
	private OutboundLinear getOutboundLinear() {
		if (outboundLinear == null) {
			outboundLinear = new OutboundLinear();
			outboundLinear.setFacetEventListener(getMajiRPCClient());
			outboundLinear.setMeemPath("hyperspace:/user/guest/test/LoopbackLinear");
			outboundLinear.setFacetId("linearInput");
		}
		return outboundLinear;
	}
	/**
	 * This method initializes inboundLinear	
	 * 	
	 * @return com.majitek.maji.rpc.binding.facet.InboundLinear	
	 */    
	private InboundLinear getInboundLinear() {
		if (inboundLinear == null) {
			inboundLinear = new InboundLinear();
			inboundLinear.setMeemPath("hyperspace:/user/guest/test/LoopbackLinear");
			inboundLinear.setFacetId("linearOutput");
			inboundLinear.setFacetEventSender(getMajiRPCClient());
			inboundLinear.addLinearFacet(new org.openmaji.common.Linear() { 
				public void valueChanged(org.openmaji.common.Position p) {   
					getJSliderFeedback().setValue(p.intValue());
				}
			});
		}
		return inboundLinear;
	}
	/**
	 * This method initializes outboundVariable	
	 * 	
	 * @return com.majitek.maji.rpc.binding.facet.OutboundVariable	
	 */    
	private OutboundVariable getOutboundVariable() {
		if (outboundVariable == null) {
			outboundVariable = new OutboundVariable();
			outboundVariable.setMeemPath("hyperspace:/user/guest/test/LoopbackVariable");
			outboundVariable.setFacetId("variableInput");
			outboundVariable.setFacetEventListener(getMajiRPCClient());
		}
		return outboundVariable;
	}
	/**
	 * This method initializes inboundVariable	
	 * 	
	 * @return com.majitek.maji.rpc.binding.facet.InboundVariable	
	 */    
	private InboundVariable getInboundVariable() {
		if (inboundVariable == null) {
			inboundVariable = new InboundVariable();
			inboundVariable.setMeemPath("hyperspace:/user/guest/test/LoopbackVariable");
			inboundVariable.setFacetId("variableOutput");
			inboundVariable.setFacetEventSender(getMajiRPCClient());
			inboundVariable.addVariableFacet(new org.openmaji.common.Variable() { 
				public void valueChanged(org.openmaji.common.Value v) { 
					getJLabel().setText(v.toString());
				}
			});
		}
		return inboundVariable;
	}
	
	
	/**
	 * This method initializes outboundVariableList	
	 * 	
	 * @return com.majitek.maji.rpc.binding.facet.OutboundVariableList	
	 */    
	private OutboundVariableList getOutboundVariableList() {
		if (outboundVariableList == null) {
			outboundVariableList = new OutboundVariableList();
			outboundVariableList.setMeemPath("hyperspace:/user/guest/test/LoopbackVariableList");
			outboundVariableList.setFacetId("variableListInput");
			outboundVariableList.setFacetEventListener(getMajiRPCClient());
		}
		return outboundVariableList;
	}
	/**
	 * This method initializes inboundVariableList	
	 * 	
	 * @return com.majitek.maji.rpc.binding.facet.InboundVariableList	
	 */    
	private InboundVariableList getInboundVariableList() {
		if (inboundVariableList == null) {
			inboundVariableList = new InboundVariableList();
			inboundVariableList.setMeemPath("hyperspace:/user/guest/test/LoopbackVariableList");
			inboundVariableList.setFacetId("variableListOutput");
			inboundVariableList.setFacetEventSender(getMajiRPCClient());
			inboundVariableList.addVariableListFacet(new org.openmaji.common.VariableList() { 
				public void valueChanged(org.openmaji.common.Value[] v) {
					getJListFeedback().setListData(v);
				}
			});
		}
		return inboundVariableList;
	}
	
	
}  //  @jve:decl-index=0:visual-constraint="10,10"
