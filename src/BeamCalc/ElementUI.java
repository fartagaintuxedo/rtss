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

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.swing.*;
import javax.swing.event.*;

import BeamCalc.LoadInterface.MyChangeAction;
//import BeamCalc.ElementUI.MyChangeAction;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;


/**
 * This class creates a java JFrame containing a Processing application
 * containing all the controls of the solver.
 * @author Jaime de Miguel
 */
public class ElementUI {
	Structure structure;
	ArrayList <Element> elms;
	JFrame frame;
	JPanel panelMain;
	JLabel elementID, loadPos;
	JTextField textFxUniformLoad, textFyUniformLoad, textFzUniformLoad;
	JTextField textFxFloatingLoad, textFyFloatingLoad, textFzFloatingLoad;
	JSlider sliderFL, sliderW, sliderH;
	JTextField textFSectionWidth, textFSectionHeight;
	ButtonGroup jbgSectionTypes;
	JRadioButton jbRectSection, jbIPESection, jbHEBSection;
	JToggleButton buttonRotateProfile;
	HashMap <String, float[]> hebProfiles, ipeProfiles;
	
	ActionListener listenerJT, listenerJB;
	ChangeListener listenerJS;
	
	ElementUI(final Structure structure){
		this.structure=structure;
		elms = new ArrayList<Element>();
		elementID = new JLabel("Element");
		
		textFxUniformLoad = new JTextField("", 3);
		textFyUniformLoad = new JTextField("", 3);
		textFzUniformLoad = new JTextField("", 3);
		
		textFxFloatingLoad = new JTextField("", 3);
		textFyFloatingLoad = new JTextField("", 3);
		textFzFloatingLoad = new JTextField("", 3);
		sliderFL = new JSlider(0, 100, 50); //(min, max, val)
		sliderW = new JSlider(0, 100); //min, max
		sliderH = new JSlider(0, 100); //min, max
		
		textFSectionWidth = new JTextField("", 3);
		textFSectionHeight = new JTextField("", 3);
		
		jbgSectionTypes = new ButtonGroup();//button group is needed for the radio buttons
		jbRectSection = new JRadioButton("Rectangular");
		jbIPESection = new JRadioButton("IPE");
		jbHEBSection = new JRadioButton("HEB");
		
		buttonRotateProfile = new JToggleButton("Rotate Profile");
		
		frame=new JFrame("Element User Interface");
		
		//init ipe and heb hashmaps
		hebProfiles = new HashMap <String, float[]> ();
		ipeProfiles = new HashMap <String, float[]> ();
				
		init();
		
		
	}
	
	void init(){
		initListeners();
		loadProfilesHashMaps();//here we load the complete catalog of HEB and IPE profiles
		
		panelMain = new JPanel();
		panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.PAGE_AXIS));

		JPanel[] panels = new JPanel[12];
		
		for(int i=0;i<panels.length;i++){
			panels[i] = new JPanel();
			//panels[i].setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));
			panelMain.add(panels[i]);
		}
		panels[panels.length-1].setPreferredSize(new Dimension(panels[0].getPreferredSize().width, 1000));// a dirty trick working incredibly well :)
		
		panels[0].add(elementID);
		
		JLabel jl;
		jl = new JLabel("Uniform Loads (KN)");
		panels[1].add(jl);
		
		jl = new JLabel("x");
		panels[2].add(jl);
		panels[2].add(textFxUniformLoad);
		textFxUniformLoad.addActionListener(listenerJT);
		jl = new JLabel(" y");
		panels[2].add(jl);
		panels[2].add(textFyUniformLoad);
		textFyUniformLoad.addActionListener(listenerJT);
		jl = new JLabel(" z");
		panels[2].add(jl);
		panels[2].add(textFzUniformLoad);
		textFzUniformLoad.addActionListener(listenerJT);
		
		jl = new JLabel("Floating Loads (KN)");
		panels[3].add(jl);
		
		jl = new JLabel("x");
		panels[4].add(jl);
		panels[4].add(textFxFloatingLoad);
		textFxFloatingLoad.addActionListener(listenerJT);
		jl = new JLabel(" y");
		panels[4].add(jl);
		panels[4].add(textFyFloatingLoad);
		textFyFloatingLoad.addActionListener(listenerJT);
		jl = new JLabel(" z");
		panels[4].add(jl);
		panels[4].add(textFzFloatingLoad);
		textFzFloatingLoad.addActionListener(listenerJT);
		
		loadPos = new JLabel("Load position: ");
		panels[5].add(loadPos);
		
		sliderFL.addChangeListener(listenerJS);
		panels[6].add(sliderFL);
		
		jl = new JLabel("Profile and Section (mm)");
		panels[7].add(jl);
		
		jbRectSection.addActionListener(listenerJB);
		jbIPESection.addActionListener(listenerJB);
		jbHEBSection.addActionListener(listenerJB);
		buttonRotateProfile.addActionListener(listenerJB);
		jbgSectionTypes.add(jbRectSection);
		jbgSectionTypes.add(jbIPESection);
		jbgSectionTypes.add(jbHEBSection);
		panels[8].add(jbRectSection);
		panels[8].add(jbIPESection);
		panels[8].add(jbHEBSection);
		
		jl = new JLabel("Width");
		panels[9].add(jl);
		panels[9].add(textFSectionWidth);
		textFSectionWidth.addActionListener(listenerJT);
		
		sliderW.setPreferredSize(new Dimension(100, 20));
		sliderW.addChangeListener(listenerJS);
		panels[9].add(sliderW);
		
		jl = new JLabel("Height");
		panels[10].add(jl);
		panels[10].add(textFSectionHeight);
		textFSectionHeight.addActionListener(listenerJT);
		
		sliderH.setPreferredSize(new Dimension(100, 20));
		sliderH.addChangeListener(listenerJS);
		panels[10].add(sliderH);
		
		panels[11].add(buttonRotateProfile);
		
		frame.add(panelMain);
		frame.setSize(250, structure.p.frame.getHeight()-240);
	    frame.setLocation(structure.p.frame.getLocation().x + structure.p.frame.getWidth() + 205, structure.p.frame.getLocation().y);
	    frame.setVisible(false);
	}
	
	void initListeners(){
		textFxUniformLoad.setName("ux");
		textFyUniformLoad.setName("uy");
		textFzUniformLoad.setName("uz");
		textFxFloatingLoad.setName("fx");
		textFyFloatingLoad.setName("fy");
		textFzFloatingLoad.setName("fz");
		sliderFL.setName("fs");
		sliderW.setName("slw");
		sliderH.setName("slh");
		jbRectSection.setActionCommand("rect");
		jbIPESection.setActionCommand("ipe");
		jbHEBSection.setActionCommand("heb");
		buttonRotateProfile.setActionCommand("rotate");
		textFSectionWidth.setName("sw");
		textFSectionHeight.setName("sh");
		
		listenerJT = new ActionListener() { //for text fields
		    public void actionPerformed(ActionEvent e) {
		        JTextField jt = ((JTextField)e.getSource());
		        String name=jt.getName();
		        float val = Float.parseFloat(jt.getText());
		        float unifLoadVal = (-1)*val;//life is tough, sometimes
		        float floatingLoadVal = (-1000)*val;//from KN to N
		        for(Element elm : elms){
			        if (name.equals("ux")){
			        	elm.uniformLoad.x = unifLoadVal;
			        }
			        else if(name.equals("uy")){
				        elm.uniformLoad.y = unifLoadVal;
			        }
				    else if(name.equals("uz")){
				        elm.uniformLoad.z = unifLoadVal;
				    }
				    else if(name.equals("fx")){
			        	elm.fLoad.load.x = floatingLoadVal;
				    }
				    else if(name.equals("fy")){
			        	elm.fLoad.load.y = floatingLoadVal;
				    }
				    else if(name.equals("fz")){
			        	elm.fLoad.load.z = floatingLoadVal;
				    }
				    else if(name.equals("sw")){//section width text field
				    	if(elm.section.profileType.equals("rect")){
				    		if(elm.section.rotated==false){
					    		elm.section.beamWidth = val;
				    		}else if(elm.section.rotated==true){
				    			elm.section.beamHeight = val;
				    		}
				    		elm.section.updateInertias();
				    		PApplet.println(elm.lW);
				    		sliderW.setValue((int)val/10);//because slider increases by 10//the sliders are updated directly here and not taking the values from the elements, is that ok?
				    	}
				    }else if(name.equals("sh")){//section-height text field
				    	if(elm.section.profileType.equals("rect")){
				    		if(elm.section.rotated==false){
					    		elm.section.beamHeight = val;
				    		}else if(elm.section.rotated==true){
				    			elm.section.beamWidth = val;
				    		}
				    		elm.section.updateInertias();
				    		sliderH.setValue((int)val/10);//because slider increases by 10 - yes its not clear, they are then multiplied again by 10 because the slider is scaled by 10 - TODO change this//the sliders are updated directly here and not taking the values from the elements, is that ok?
				    	}
				    	else if(elm.section.profileType.equals("heb")){
				    		doNormalizedProfileEvent(elm, val, hebProfiles);
				    	}
				    	else if(elm.section.profileType.equals("ipe")){
				    		doNormalizedProfileEvent(elm, val, ipeProfiles);
				    	}
				    }
			        elm.evalLoads();//loads have to be evaluated once per element
		        }
		    }
		};
		
		listenerJS = new ChangeListener() { //for sliders
			public void stateChanged(ChangeEvent e) {
				JSlider js = ((JSlider)e.getSource());
		        String name=js.getName();
		        int value = js.getValue();
		        
		        for(Element elm : elms){
			        if(name.equals("fs")){//slider: floating load
			        	elm.fLoad.xpos=(float)(value/100.0)*elm.L;
			        	loadPos.setText("Load position: "+(int)(elm.fLoad.xpos)+" mm");
			        }
			        if(name.equals("slw") && elm.section.profileType.equals("rect")){//slider: profile width
			        	textFSectionWidth.setText(value*10+"");
			        	doRobotPressEnter(textFSectionWidth);
			        }
			        if(name.equals("slh")){//slider: profile height
			        	textFSectionHeight.setText(value*10+"");
			        	doRobotPressEnter(textFSectionHeight);
			        }
			        elm.evalLoads();//loads have to be evaluated once per element
		        }
			}
		};
		
		listenerJB = new ActionListener() { //for buttons (radio buttons and the last one is the rotate button)
		    public void actionPerformed(ActionEvent e) {
		    	String cmd = e.getActionCommand();
		    	for(Element elm : elms){
			    	if(cmd.equals("rect")){
			    		elm.section.profileType="rect";
			    		enableWidthControl(true);
			    		doRobotPressEnter(textFSectionWidth);//if we change profile type we must re-evaluate 
			    		doRobotPressEnter(textFSectionHeight);
			    	}else if(cmd.equals("ipe")){
			    		elm.section.profileType="ipe";
			    		enableWidthControl(false);
			    		doRobotPressEnter(textFSectionHeight);//if we change profile type we must re-evaluate 
			    	}else if(cmd.equals("heb")){
			    		elm.section.profileType="heb";
			    		enableWidthControl(false);
			    		doRobotPressEnter(textFSectionHeight);//if we change profile type we must re-evaluate 
			    	}
			    	else if(cmd.equals("rotate")){			    		
			    		elm.section.rotate();
			    		if(elm.section.rotated==true){
			    			buttonRotateProfile.setSelected(true);
			    			buttonRotateProfile.setText("Rotate back");
		    			}else if(elm.section.rotated==false){
			    			buttonRotateProfile.setSelected(false);
			    			buttonRotateProfile.setText("Rotate Profile");
			    		}
			    		doRobotPressEnter(textFSectionHeight);//if we change profile type we must re-evaluate 
			    	}
		    	}
		    }
		};
	}
	
	private void doNormalizedProfileEvent(Element elm, float val, HashMap <String, float[]> profileTable){
		String key = Integer.toString((int)val);
		if(profileTable.containsKey(key)){ //check if the profile exists
			int sectionWidth = (int)(profileTable.get(key)[2]);
			textFSectionWidth.setText(Integer.toString(sectionWidth));
			sliderH.setValue(Integer.parseInt(key)/10);
			sliderW.setValue(sectionWidth/10);
			updateElementValues(elm, profileTable.get(key));
			PApplet.println("w="+elm.section.beamWidth);
			PApplet.println("h="+elm.section.beamHeight);
			PApplet.println("Ixz="+elm.section.Ixz);
		}else{ //if it doesn't exist check for the closest one below
			String alternativeKey = getAlternativeKey(val, profileTable);
			
			int sectionWidth = (int)(profileTable.get(alternativeKey)[2]);
			textFSectionHeight.setText(alternativeKey);
			textFSectionWidth.setText(Integer.toString(sectionWidth));
			sliderH.setValue(Integer.parseInt(alternativeKey)/10);
			sliderW.setValue(sectionWidth/10);
			updateElementValues(elm, profileTable.get(alternativeKey));
		}
	}
	
	private String getAlternativeKey(float val, HashMap <String, float[]> profileTable){//when user inputs non existing heb or ipe profile we provide the right one
		Set <String> keys = profileTable.keySet();
		int[] intKeys = new int[keys.size()+1];
		int i = 0;
		for(String k : keys){
			intKeys[i] = Integer.parseInt(k);
			i++;
		}
		intKeys[intKeys.length-1] = (int)val;
		Arrays.sort(intKeys);
		int valIndex = 0;
		for(int j=0;j<intKeys.length;j++)if(intKeys[j] == val)valIndex = j;
		String alternativeKey = "";
		if(valIndex == 0){
			alternativeKey = Integer.toString(intKeys[1]);
		}else{
			alternativeKey = Integer.toString(intKeys[valIndex - 1]);
		}
		return alternativeKey;
	}
	
	private void doRobotPressEnter(JTextField textfield){
		textfield.requestFocusInWindow();
		Robot robot;
		try {
			robot = new Robot();
			robot.keyPress(KeyEvent.VK_ENTER);
		} catch (AWTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void enableWidthControl(boolean bool){
		textFSectionWidth.setEditable(bool);
		sliderW.setEnabled(bool);
	}
	
	private void updateElementValues(Element elm, float[] profileValues){
		//TODO update density and affect loads... (own weight)
		if(elm.section.rotated==false){
			elm.section.beamHeight = profileValues[1];
			elm.section.beamWidth = profileValues[2];
			elm.section.A = profileValues[3]*100;
			elm.section.Ixy = profileValues[7]*10000;
			elm.section.Ixz = profileValues[4]*10000;
			elm.section.J = profileValues[9]*10000;
		}else if(elm.section.rotated==true){
			elm.section.beamHeight = profileValues[2];
			elm.section.beamWidth = profileValues[1];
			elm.section.A = profileValues[3]*100;
			elm.section.Ixy = profileValues[4]*10000;
			elm.section.Ixz = profileValues[7]*10000;
			elm.section.J = profileValues[9]*10000;
		}
	}
	
	//the functions below are used to retrieve the values of the elements and feed them into the text fields, sliders, buttons etc.
	public void updatePanelData(ArrayList <Element> elements){
		updatePanelElmID(elements);
		updatePanelElmUniformLoad(elements);
		updatePanelElmFloatingLoad(elements);
		updatePanelSectionData(elements);
	}
	
	private void updatePanelElmID(ArrayList <Element> elements){
		elms = elements;
		String ids="";
		for(Element elm : elms){
			ids+=elm.id+", ";
		}
		ids = ids.replaceFirst(", $", "");
		elementID.setText("Element: "+ids);
	}
	
	private void updatePanelElmUniformLoad(ArrayList <Element> elements){
		if(elements.size() == 1){
			Element e = elements.get(0);
			textFxUniformLoad.setText(0-e.uniformLoad.x+"");//JDM, yes, we have to change that sign, sorry
			textFyUniformLoad.setText(0-e.uniformLoad.y+"");//The weird 0-value is just to avoid displaying -0.0 in the box
			textFzUniformLoad.setText(0-e.uniformLoad.z+"");
		}else{
			textFxUniformLoad.setText("-");
			textFyUniformLoad.setText("-");
			textFzUniformLoad.setText("-");
		}
		
	}
	
	private void updatePanelElmFloatingLoad(ArrayList <Element> elements){
		if(elements.size() == 1){
			Element e = elements.get(0);
			textFxFloatingLoad.setText(0-e.fLoad.load.x/1000+"");//JDM, yes, we have to change that sign, sorry//divided by 1000 to express KN instead of N
			textFyFloatingLoad.setText(0-e.fLoad.load.y/1000+"");//The weird 0-value is just to avoid displaying -0.0 in the box
			textFzFloatingLoad.setText(0-e.fLoad.load.z/1000+"");
			sliderFL.setValue((int)(e.fLoad.xpos*100.0/e.L));
		}else{
			textFxFloatingLoad.setText("-");
			textFyFloatingLoad.setText("-");
			textFzFloatingLoad.setText("-");
			sliderFL.setValue((int)(sliderFL.getMinimum() + sliderFL.getMaximum()/2.0));//middle value of slider
		}
		
	}
	
	private void updatePanelSectionData(ArrayList <Element> elements){
		jbgSectionTypes.clearSelection();
		
		if(elements.size() == 1){
			Element e = elements.get(0);
			updatePanelRotButt(e);//this must go first
			if(e.section.rotated == false){
				textFSectionWidth.setText((int)e.section.beamWidth+"");
				textFSectionHeight.setText((int)e.section.beamHeight+"");
				sliderW.setValue((int)e.section.beamWidth/10);//because slider increases by 10
				sliderH.setValue((int)e.section.beamHeight/10);
			}else if(e.section.rotated == true){
				textFSectionWidth.setText((int)e.section.beamHeight+"");
				textFSectionHeight.setText((int)e.section.beamWidth+"");
				sliderW.setValue((int)e.section.beamHeight/10);//because slider increases by 10
				sliderH.setValue((int)e.section.beamWidth/10);
			}
			updateFromProfileType(e);
			
		}else{
			boolean sameWidth, sameHeight, sameType, sameRotated;
			sameWidth = sameHeight = sameType = sameRotated = true;
			for(int i=0;i<elements.size();i++){//JDM//here we check whether all the selected elements share the same section or not
				Element e = elements.get(i);
				if(i>0){
					Element enext = elements.get(i-1);
					if(e.section.beamWidth != enext.section.beamWidth)sameWidth = false;
					if(e.section.beamHeight != enext.section.beamHeight)sameHeight = false;
					if(!e.section.profileType.equals(enext.section.profileType))sameType = false;
					if(e.section.rotated != (enext.section.rotated))sameRotated = false;
				}
			}
			
			if(sameRotated){//this must go first
				//set toggle button selected or unselected accordingly
				Element e = elements.get(0);
				updatePanelRotButt(e);
			}else{
				buttonRotateProfile.setText("Profile rotation");
				buttonRotateProfile.setEnabled(false);
			}
			
			if(sameWidth){
				Element e = elements.get(0);
				if(e.section.rotated == false){
					textFSectionWidth.setText((int)e.section.beamWidth+"");
					sliderW.setValue((int)e.section.beamWidth/10);//because slider increases by 10
				}else if(e.section.rotated == true){//swap height and width
					textFSectionWidth.setText((int)e.section.beamHeight+"");
					sliderW.setValue((int)e.section.beamHeight/10);//because slider increases by 10
				}
			}else{
				textFSectionWidth.setText("-");
			}
			
			if(sameHeight){
				Element e = elements.get(0);
				if(e.section.rotated == false){
					textFSectionHeight.setText((int)e.section.beamHeight+"");
					sliderH.setValue((int)e.section.beamHeight/10);//because slider increases by 10
				}else if(e.section.rotated == true){//swap height and width
					textFSectionHeight.setText((int)e.section.beamWidth+"");
					sliderH.setValue((int)e.section.beamWidth/10);//because slider increases by 10
				}
			}else{
				textFSectionHeight.setText("-");
			}
			
			if(sameType){
				Element e = elements.get(0);
				updateFromProfileType(e);
			}
		}
	}
	
	void updatePanelRotButt(Element e){
		buttonRotateProfile.setEnabled(true);
		if(e.section.rotated == false){
			buttonRotateProfile.setSelected(false);
			buttonRotateProfile.setText("Rotate Profile");
		}else if(e.section.rotated == true){
			buttonRotateProfile.setSelected(true);
			buttonRotateProfile.setText("Rotate back");
		}
	}
	
	private void updateFromProfileType(Element e){
		if(e.section.profileType.equals("rect")){
			jbRectSection.setSelected(true);//set radio button selected
			enableWidthControl(true);
		}else if(e.section.profileType.equals("ipe")){
			jbIPESection.setSelected(true);
			enableWidthControl(false);
		}else if(e.section.profileType.equals("heb")){
			jbHEBSection.setSelected(true);
			enableWidthControl(false);
		}
	}
	
	private void loadProfilesHashMaps(){
		loadHEBHashMap();
		loadIPEHashMap();
	}
	
	private void loadHEBHashMap(){
		//important// [0]-->G, [1]-->h, [2]-->b, [3]-->A, [4]-->Iy, [5]-->Wel.y, [6]-->Avz, [7]-->Iz, [8]-->Wel.z, [9]-->It(torsion)
		float[] p100 = {20.4f, 100, 100, 26, 449.5f, 89.91f, 9.04f, 167.3f, 33.45f, 9.25f};
		hebProfiles.put("100", p100);
		float[] p120 = {26.7f, 120, 120, 34f, 864.4f, 144.1f, 10.96f, 317.5f, 52.92f, 13.84f};
		hebProfiles.put("120", p120);
		float[] p140 = {33.7f, 140f, 140f, 43f, 1509f, 215.6f, 13.08f, 549.7f, 78.52f, 20.06f};
		hebProfiles.put("140", p140);
		float[] p160 = {42.6f, 160f, 160f, 54.3f, 2492f, 311.5f, 17.59f, 889.2f, 111.2f, 31.24f};
		hebProfiles.put("160", p160);
		float[] p180 = {51.2f, 180f, 180f, 65.3f, 3831f, 425.7f, 20.24f, 1363f, 151.4f, 42.16f};
		hebProfiles.put("180", p180);
		float[] p200 = {61.3f, 200f, 200f, 78.1f, 5696f, 569.6f, 24.83f, 2003f, 200.3f, 59.28f};
		hebProfiles.put("200", p200);
		float[] p220 = {71.5f, 220f, 220f, 91f, 8091f, 735.5f, 27.92f, 2843f, 258.5f, 76.57f};
		hebProfiles.put("220", p220);
		float[] p240 = {83.2f, 240f, 240f, 106f, 11260f, 938.3f, 33.23f, 3923f, 326.9f, 102.7f};
		hebProfiles.put("240", p240);
		float[] p260 = {93f, 260f, 260f, 118.4f, 14920f, 1148f, 37.59f, 5135f, 395f, 123.8f};
		hebProfiles.put("260", p260);
		float[] p280 = {103f, 280f, 280f, 131.4f, 19270f, 1376f, 41.09f, 6595f, 471f, 143.7f};
		hebProfiles.put("280", p280);
		float[] p300 = {117f, 300f, 300f, 149.1f, 25170f, 1678f, 47.43f, 8563f, 570.9f, 185f};
		hebProfiles.put("300", p300);
		float[] p320 = {127f, 320f, 300f, 161.3f, 30820f, 1926f, 51.77f, 9239f, 615.9f, 225.1f};
		hebProfiles.put("320", p320);
		float[] p340 = {134f, 340f, 300f, 170.9f, 36660f, 2156f, 56.09f, 9690f, 646f, 257.2f};
		hebProfiles.put("340", p340);
		float[] p360 = {142f, 360f, 300f, 180.6f, 43190f, 2400f, 60.6f, 10140f, 676.1f, 292.5f};
		hebProfiles.put("360", p360);
		float[] p400 = {155f, 400f, 300f, 197.8f, 57680f, 2884f, 69.98f, 10820f, 721.3f, 355.7f};
		hebProfiles.put("400", p400);
		float[] p450 = {171f, 450f, 300f, 218f, 79890f, 3551f, 79.66f, 11720f, 781.4f, 440.5f};
		hebProfiles.put("450", p450);
		float[] p500 = {187f, 500f, 300f, 238.6f, 107200f, 4287f, 89.82f, 12620f, 841.6f, 538.4f};
		hebProfiles.put("500", p500);
		float[] p550 = {199f, 550f, 300f, 254.1f, 136700f, 4971f, 100.1f, 13080f, 871.8f, 600.3f};
		hebProfiles.put("550", p550);
		float[] p600 = {212f, 600f, 300f, 270f, 171000f, 5701f, 110.8f, 13530f, 902f, 667.2f};
		hebProfiles.put("600", p600);
		float[] p650 = {225f, 650f, 300f, 286.3f, 210600f, 6480f, 122f, 13980f, 932.3f, 739.2f};
		hebProfiles.put("650", p650);
		float[] p700 = {241f, 700f, 300f, 306.4f, 256900f, 7340f, 137.1f, 14440f, 962.7f, 830.9f};
		hebProfiles.put("700", p700);
		float[] p800 = {262f, 800f, 300f, 334.2f, 359100f, 8977f, 161.8f, 14900f, 993.6f, 946};
		hebProfiles.put("800", p800);
		float[] p900 = {291f, 900f, 300f, 371.3f, 494100f, 10980f, 188.8f, 15820f, 1054f, 1137};
		hebProfiles.put("900", p900);
		float[] p1000 = {314f, 1000f, 300f, 400f, 644700f, 12890f, 212.5f, 16280f, 1085f, 1254};
		hebProfiles.put("1000", p1000);
	}
	
	private void loadIPEHashMap(){
		//important// [0]-->G, [1]-->h, [2]-->b, [3]-->A, [4]-->Iy, [5]-->Wel.y, [6]-->Avz, [7]-->Iz, [8]-->Wel.z, [9]-->It(torsion)
		float[] p80 = {6f, 80f, 46f, 7.64f, 80.14f, 20.03f, 3.24f, 3.58f, 8.49f, 3.69f, 0.7f};
		ipeProfiles.put("80", p80);
		float[] p100 = {8.1f, 100f, 55f, 10.3f, 171f, 34.2f, 4.07f, 5.08f, 15.92f, 5.79f, 1.2f};
		ipeProfiles.put("100", p100);
		float[] p120 = {10.4f, 120f, 64f, 13.2f, 317.8f, 52.96f, 4.9f, 6.31f, 27.67f, 8.65f, 1.74f};
		ipeProfiles.put("120", p120);
		float[] p140 = {12.9f, 140f, 73f, 16.4f, 541.2f, 77.32f, 5.74f, 7.64f, 44.92f, 12.31f, 2.45f};
		ipeProfiles.put("140", p140);
		float[] p160 = {15.8f, 160f, 82f, 20.1f, 869.3f, 108.7f, 6.58f, 9.66f, 68.31f, 16.66f, 3.6f};
		ipeProfiles.put("160", p160);
		float[] p180 = {18.8f, 180f, 91f, 23.9f, 1317f, 146.3f, 7.42f, 11.25f, 100.9f, 22.16f, 4.79f};
		ipeProfiles.put("180", p180);
		float[] p200 = {22.4f, 200f, 100f, 28.5f, 1943f, 194.3f, 8.26f, 14f, 142.4f, 28.47f, 6.98f};
		ipeProfiles.put("200", p200);
		float[] p220 = {26.2f, 220f, 110f, 33.4f, 2772f, 252f, 9.11f, 15.88f, 204.9f, 37.25f, 9.07f};
		ipeProfiles.put("220", p220);
		float[] p240 = {30.7f, 240f, 120f, 39.1f, 3892f, 324.3f, 9.97f, 19.14f, 283.6f, 47.27f, 12.88f};
		ipeProfiles.put("240", p240);
		float[] p270 = {36.1f, 270f, 135f, 45.9f, 5790f, 428.9f, 11.23f, 22.14f, 419.9f, 62.2f, 15.94f};
		ipeProfiles.put("270", p270);
		float[] p300 = {42.2f, 300f, 150f, 53.8f, 8356f, 557.1f, 12.46f, 25.68f, 603.8f, 80.5f, 20.12f};
		ipeProfiles.put("300", p300);
		float[] p330 = {49.1f, 330f, 160f, 62.6f, 11770f, 713.1f, 13.71f, 30.81f, 788.1f, 98.52f, 28.15f};
		ipeProfiles.put("330", p330);
		float[] p360 = {57.1f, 360f, 170f, 72.7f, 16270f, 903.6f, 14.95f, 35.14f, 1043f, 122.8f, 37.32f};
		ipeProfiles.put("360", p360);
		float[] p400 = {66.3f, 400f, 180f, 84.5f, 23130f, 1156f, 16.55f, 42.69f, 1318f, 146.4f, 51.08f};
		ipeProfiles.put("400", p400);
		float[] p450 = {77.6f, 450f, 190f, 98.8f, 33740f, 1500f, 18.48f, 50.85f, 1676f, 176.4f, 66.87f};
		ipeProfiles.put("450", p450);
		float[] p500 = {90.7f, 500f, 200f, 116f, 48200f, 1928f, 20.43f, 59.87f, 2142f, 214.2f, 89.29f};
		ipeProfiles.put("500", p500);
		float[] p550 = {106f, 550f, 210f, 134f, 67120f, 2441f, 22.35f, 72.34f, 2668f, 254.1f, 123.2f};
		ipeProfiles.put("550", p550);
		float[] p600 = {122f, 600f, 220f, 156f, 92080f, 3069f, 24.3f, 83.78f, 3387f, 307.9f, 165.4f};
		ipeProfiles.put("600", p600);
	}
}
	
	






























	