/**
 * (./) DXFImporter.java v0.1 05/09/2011
 * @author Enrique Ramos Melgar
 * http://www.esc-studio.com
 *
 * THIS LIBRARY IS RELEASED UNDER A CREATIVE COMMONS ATTRIBUTION 3.0 LICENSE
 * http://creativecommons.org/licenses/by/3.0/
 * http://www.processing.org/
 * 
 * This implementation is based on the code from BNichols available at:
 * http://processing.org/discourse/yabb2/YaBB.pl?num=1142535442
 */

package BeamCalc;

import java.io.File;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;


/**
 * This class imports parses DXF files and converts the line
 * objects into element objects understandable by the solver
 * @author Jaime de Miguel 
 *
 */
public class importDXF_JDM {

	PApplet p;
	Structure structure;
	ArrayList <PVector[]> vertices;
	
	importDXF_JDM(PApplet p, Structure struc) {
		this.structure = struc;
		this.p = p;
	}
	
	void importDxf(File f) {
		vertices = new ArrayList <PVector[]> ();
		structure.reset();
		String[] dxf = PApplet.loadStrings(f);
		populateVerticesArray(dxf);
		createBeams();
		structure.draw(p);

    	
	}
	
	private void populateVerticesArray(String[] dxf){
		PVector coords1 = new PVector();
		PVector coords2 = new PVector();
		boolean inLineSection = false;
		
		for(int i=0;i<dxf.length;i++){
			String txt = dxf[i];
			txt = txt.replaceAll("\\s","");//some dxf's write a white space before the line's code, others don't
			
			if(txt.equals("LINE")){
				coords1 = new PVector();
				coords2 = new PVector();
				inLineSection = true;
			}
			if(txt.equals("10") && dxf[i+2].replaceAll("\\s","").equals("20")){
				coords1.x = Float.parseFloat(dxf[i+1]);
				coords1.y = Float.parseFloat(dxf[i+3]);
				if(dxf[i+4].replaceAll("\\s","").equals("30")){//because some dxf's are just 2D
					coords1.z = Float.parseFloat(dxf[i+5]);
				}else{
					coords1.z = 0.0f;
				}
			}
			if(txt.equals("11") && dxf[i+2].replaceAll("\\s","").equals("21")){
				coords2.x = Float.parseFloat(dxf[i+1]);
				coords2.y = Float.parseFloat(dxf[i+3]);
				if(dxf[i+4].replaceAll("\\s","").equals("31")){//because some dxf's are just 2D
					coords2.z = Float.parseFloat(dxf[i+5]);
				}else{
					coords2.z = 0.0f;
				}
				PVector[] pts = {coords1, coords2};
				vertices.add(pts);
			}
			
			if(inLineSection && txt.equals("ENDSEC")){
				inLineSection = false;
				break; //exit the loop when done reading line objects
			}
		}
	}
	
	private void createBeams(){
		for(PVector[] pts : vertices){
			PVector pt1 = pts[0];
			PVector pt2 = pts[1];
			
			int plane = 0;//see structure.addBeam for parameter values
			PVector beamVec = PVector.sub(pt2, pt1);
			if(beamVec.dot(new PVector(1,0,0)) == 0)plane = 1;
			
	    	if(pt1.z<100 && pt2.z<100){//if very close to the ground we assume its a clamped node
	    		structure.addBeam(pt1, pt2, 3, 3, plane);
	    	}else if(pt1.z<100){
	    		structure.addBeam(pt1, pt2, 3, 0, plane);
	    	}else if(pt2.z<100){
	    		structure.addBeam(pt1, pt2, 0, 3, plane);
	    	}else{
	    		structure.addBeam(pt1, pt2, 0, 0, plane);
	    	}
		}
	}
		
	
	
	
	
	
	
	
	
	
	
	
}
	
	