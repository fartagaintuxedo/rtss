/* (./) Toolbox.java v0.1 05/09/2011
 * @author Enrique Ramos Melgar
 * http://www.esc-studio.com
 *
 * THIS LIBRARY IS RELEASED UNDER A CREATIVE COMMONS ATTRIBUTION 3.0 LICENSE
 * http://creativecommons.org/licenses/by/3.0/
 * http://www.processing.org/
 * 
 */

package BeamCalc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;
import javax.swing.event.*;

import BeamCalc.LoadInterface.MyChangeAction;
import processing.core.PApplet;
import processing.core.PFont;

/**
 * This class creates a java JFrame containing a Processing application
 * containing all the controls of the solver.
 * @author Enrique Ramos
 */
public class LoadInterface {
	Structure structure;
	JSlider slider, slider2, slider3, slider4, slider5, slider6, slider7;
	JLabel label, label2, label3, label4, label5, label6, label7, label0, label00;
	JButton button1, button2, button3, button4, button5, button6, button7, button8,button9, button10;
	JTextField textFxload, textFyload, textFzload, textFxcontload, textFycontload, textFzcontload, textFxFloatingLoad, textFyFloatingLoad, textFzFloatingLoad;
	JFrame frame;
	boolean show=false;
	
	JFileChooser chooser;
	
	int xPos = 10;
	int yPos = 10;
	int buttonW = 90;

	public LoadInterface(final Structure structure) {
		this.structure = structure;
		frame = new JFrame("Load Interface");
		try{
			int nodeID=structure.focusNodes.get(0).id;
			label0=new JLabel("Node Loads: "+nodeID);
		}catch(Exception exc){
			label0=new JLabel("");
		}
		
		textFxload = new JTextField("enter x load", 12);
		textFxload.setBounds(100, 100, 30, 10);
		
		textFyload = new JTextField("enter y load", 12);
		textFyload.setBounds(100, 100, 30, 10);
		
		textFzload = new JTextField("enter z load", 12);
		textFzload.setBounds(100, 100, 30, 10);
		
		textFxFloatingLoad = new JTextField("enter x floating load", 12);
		textFxFloatingLoad.setBounds(100, 100, 30, 10);
		
		textFyFloatingLoad = new JTextField("enter y floating load", 12);
		textFyFloatingLoad.setBounds(100, 100, 30, 10);
		
		textFzFloatingLoad = new JTextField("enter z floating load", 12);
		textFzFloatingLoad.setBounds(100, 100, 30, 10);
		
		slider = new JSlider();
		slider.setMinimum(0);
		slider.setMaximum(100);
		slider.setValue(50);
		slider.addChangeListener(new MyChangeAction());
		
		try{
			int elmID=structure.focusElms.get(0).id;
			label00=new JLabel("Node Loads: "+elmID);
		}catch(Exception exc){
			label00=new JLabel("");
		}
		
		textFxcontload = new JTextField("enter x continuous load", 4);
		textFxcontload.setBounds(100, 100, 30, 10);
		
		textFycontload = new JTextField("enter y continuous load", 4);
		textFycontload.setBounds(100, 100, 30, 10);
		
		textFzcontload = new JTextField("enter z continuous load", 4);
		textFzcontload.setBounds(100, 100, 30, 10);
		
		/////
		textFxload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//change some node value here!
				for(int i=0;i<structure.nodes.size();i++){//this should be done faster getting the selected node directly by id
					Node n = structure.nodes.get(i);
					if(n.selected){
						n.load.x=Float.parseFloat(textFxload.getText())*1000.0f;//load in KN
					}
				}
			}
		});
		
		textFyload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//change some node value here!
				for(int i=0;i<structure.nodes.size();i++){//this should be done faster getting the selected node directly by id
					Node n = structure.nodes.get(i);
					if(n.selected){
						n.load.y=Float.parseFloat(textFyload.getText())*1000.0f;//load in KN
					}
				}
			}
		});
		
		textFzload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//change some node value here!
				for(int i=0;i<structure.nodes.size();i++){//this should be done faster getting the selected node directly by id
					Node n = structure.nodes.get(i);
					if(n.selected){
						n.load.z=Float.parseFloat(textFzload.getText())*1000.0f;//load in KN
					}
				}
			}
		});
		
		textFxFloatingLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//change some node value here!
				for(int i=0;i<structure.elms.size();i++){//this should be done faster getting the selected element directly by id
					Element elm = structure.elms.get(i);
					if(elm.selected){
						elm.fLoad.load.x=-Float.parseFloat(textFxFloatingLoad.getText());
						elm.evalLoads();
					}
				}
			}
		});
		
		textFyFloatingLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//change some node value here!
				for(int i=0;i<structure.elms.size();i++){//this should be done faster getting the selected element directly by id
					Element elm = structure.elms.get(i);
					if(elm.selected){
						elm.fLoad.load.y=-Float.parseFloat(textFyFloatingLoad.getText());
						elm.evalLoads();
					}
				}
			}
		});
		
		textFzFloatingLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//change some node value here!
				for(int i=0;i<structure.elms.size();i++){//this should be done faster getting the selected element directly by id
					Element elm = structure.elms.get(i);
					if(elm.selected){
						elm.fLoad.load.z=-Float.parseFloat(textFzFloatingLoad.getText());
						elm.evalLoads();
					}
				}
			}
		});
		
		textFxcontload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//change some node value here!
				for(int i=0;i<structure.elms.size();i++){//this should be done faster getting the selected element directly by id
					Element elm = structure.elms.get(i);
					if(elm.selected){
						elm.uniformLoad.x=-Float.parseFloat(textFxcontload.getText());
						elm.evalLoads();
					}
				}
			}
		});
		
		textFycontload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//change some node value here!
				for(int i=0;i<structure.elms.size();i++){//this should be done faster getting the selected element directly by id
					Element elm = structure.elms.get(i);
					if(elm.selected){
						elm.uniformLoad.y=-Float.parseFloat(textFycontload.getText());
						elm.evalLoads();
					}
				}
			}
		});
		
		textFzcontload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//change some node value here!
				for(int i=0;i<structure.elms.size();i++){//this should be done faster getting the selected element directly by id
					Element elm = structure.elms.get(i);
					if(elm.selected){
						elm.uniformLoad.z=-Float.parseFloat(textFzcontload.getText());
						elm.evalLoads();
					}
				}
			}
		});
		
		
		
		
		JPanel panel = new JPanel();
		panel.add(label0);
		panel.add(textFxload);
		panel.add(textFyload);
		panel.add(textFzload);
		panel.add(textFxFloatingLoad);
		panel.add(textFyFloatingLoad);
		panel.add(textFzFloatingLoad);
		panel.add(slider);
		panel.add(label00);
		panel.add(textFxcontload);
		panel.add(textFycontload);
		panel.add(textFzcontload);
		
		
		frame.add(panel);
		frame.setSize(200, structure.p.frame.getHeight()/2-5);
	    frame.setLocation(structure.p.frame.getLocation().x + structure.p.frame.getWidth() + 205,structure.p.frame.getLocation().y+structure.p.frame.getHeight()/2+5);
	    frame.setVisible(false);
	    
	}
	
	public class MyChangeAction implements ChangeListener {
		public void stateChanged(ChangeEvent ce) {
			int value = slider.getValue();
			for(int i=0;i<structure.elms.size();i++){//this should be done faster getting the selected element directly by id
				Element elm = structure.elms.get(i);
				if(elm.selected){
					elm.fLoad.xpos=(float)(value/100.0)*elm.L;
					elm.evalLoads();
				}
			}
		}
	}
	
	////////methods////////
	public void setVisible(){
		frame.setVisible(true);
	}
	
	public void updatePanelNodeLoad(float fx, float fy, float fz){
		
	}
	
	public void updatePanelElmContLoad(Element Elm){
		label00.setText("<html><body>Element "+Elm.id+"<br>.</body></html>");
		textFxcontload.setText(Elm.uniformLoad.x+"");
		textFycontload.setText(Elm.uniformLoad.y+"");
		textFzcontload.setText(Elm.uniformLoad.z+"");
	}
	
	public void updatePanelElmFloatingLoad(float fx, float fy, float fz){
		
	}
	
}








