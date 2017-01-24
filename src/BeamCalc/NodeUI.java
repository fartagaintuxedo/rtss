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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.*;

import BeamCalc.LoadInterface.MyChangeAction;
//import BeamCalc.ElementUI.MyChangeAction;
import processing.core.PApplet;
import processing.core.PFont;

/**
 * This class creates a java JFrame containing a Processing application
 * containing all the controls of the solver.
 * @author Jaime de Miguel
 */
public class NodeUI {
	Structure structure;
	ArrayList <Node> nodes;
	JFrame frame;
	JPanel panelMain;
	JLabel nodeID;
	JTextField textFxLoad, textFyLoad, textFzLoad;
	
	
	
	ActionListener listenerJT, listenerJB;
	ChangeListener listenerJS;
	
	NodeUI(final Structure structure){
		this.structure=structure;
		nodes = new ArrayList<Node>();
		nodeID = new JLabel("Node");
		
		textFxLoad = new JTextField("", 3);
		textFyLoad = new JTextField("", 3);
		textFzLoad = new JTextField("", 3);
		 
		frame=new JFrame("Node User Interface");
		
		init();
	}
	
	void init(){
		initListener();
		
		panelMain = new JPanel();
		panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.PAGE_AXIS));

		JPanel[] panels = new JPanel[3];
		
		for(int i=0;i<panels.length;i++){
			panels[i] = new JPanel();
			//panels[i].setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));
			panelMain.add(panels[i]);
		}
		panels[panels.length-1].setPreferredSize(new Dimension(panels[0].getPreferredSize().width, 1000));// a dirty trick working incredibly well :)
		
		panels[0].add(nodeID);
		
		JLabel jl;
		jl = new JLabel("Discrete Loads (KN)");
		panels[1].add(jl);
		
		jl = new JLabel("x");
		panels[2].add(jl);
		panels[2].add(textFxLoad);
		textFxLoad.addActionListener(listenerJT);
		jl = new JLabel(" y");
		panels[2].add(jl);
		panels[2].add(textFyLoad);
		textFyLoad.addActionListener(listenerJT);
		jl = new JLabel(" z");
		panels[2].add(jl);
		panels[2].add(textFzLoad);
		textFzLoad.addActionListener(listenerJT);
		
		
		
		frame.add(panelMain);
		frame.setBounds(structure.p.frame.getLocation().x + structure.p.frame.getWidth() + 5, structure.p.frame.getLocation().y, 200, structure.p.frame.getHeight() / 2 - 160);
		//frame.setSize(250, structure.p.frame.getHeight()-5);
	    //frame.setLocation(structure.p.frame.getLocation().x + structure.p.frame.getWidth() + 205, structure.p.frame.getLocation().y);
	    frame.setVisible(false);
	}
	
	void initListener(){
		textFxLoad.setName("fx");
		textFyLoad.setName("fy");
		textFzLoad.setName("fz");
		
		listenerJT = new ActionListener() { //for text fields
		    public void actionPerformed(ActionEvent e) {
		        JTextField jt = ((JTextField)e.getSource());
		        String name=jt.getName();
		        float val = Float.parseFloat(jt.getText());
		        val = val*1000;
		        for(Node node : nodes){
			        if (name.equals("fx")){
			        	node.load.x = val;
			        }
			        else if(name.equals("fy")){
				        node.load.y = val;
			        }
				    else if(name.equals("fz")){
				        node.load.z = val;
				    }
		        }
		    }
		};
		

		
	}
	//the functions below are used to retrieve the values of the elements and feed them into the text fields, sliders, buttons etc.
	public void updatePanelData(ArrayList <Node> nds){
		updatePanelNodeID(nds);
		updatePanelNodeLoad(nds);
	}
	
	private void updatePanelNodeID(ArrayList <Node> nds){
		nodes = nds;
		String ids="";
		for(Node n : nds){
			ids+=n.id+", ";
		}
		ids = ids.replaceFirst(", $", "");
		nodeID.setText("Node: "+ids);
	}
	
	private void updatePanelNodeLoad(ArrayList <Node> nds){
		if(nds.size() == 1){
			Node n = nds.get(0);
			textFxLoad.setText(n.load.x/1000+"");//loads are divided by 1000 to express KN instead of N
			textFyLoad.setText(n.load.y/1000+"");
			textFzLoad.setText(n.load.z/1000+"");
		}else{
			textFxLoad.setText("-");
			textFyLoad.setText("-");
			textFzLoad.setText("-");
		}
	}
	
}
	
	




	