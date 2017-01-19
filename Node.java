/**
 * (./) Node.java v0.1 05/09/2011
 * @author Enrique Ramos Melgar
 * http://www.esc-studio.com
 *
 * THIS LIBRARY IS RELEASED UNDER A CREATIVE COMMONS ATTRIBUTION 3.0 LICENSE
 * http://creativecommons.org/licenses/by/3.0/
 * http://www.processing.org/
 */
package BeamCalc;

import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

/**
 * This class contains the definitions of every node in the
 * structure, the integration algorithms and the equilibrium
 * equations. Node visualisation is processed here. 
 * @author Enrique Ramos
 */
public class Node {

	
	private PApplet p; 				// Parent Processing Application
	private Structure structure; 	// Parent Structure
	
	// Elements linked to the node
	ArrayList<Element> nodeBeams = new ArrayList<Element>();
	
	// ArrayLists for the graphs
	public ArrayList<PVector> graph = new ArrayList<PVector>();
	public ArrayList<Integer> graphTimes = new ArrayList<Integer>();
	
	private float dispScale = 0;

	// Node Position Vectors
	PVector initialPos;
	public PVector pos;
	protected PVector visualPos;
	
	// Rotation vectors
	public PVector initialRot;
	public PVector rot = new PVector();
	PVector visualRot = new PVector();

	public PVector forceVec = new PVector();
	public PVector momentVec = new PVector();
	public PVector loadVec = new PVector();
	private PVector result = new PVector();
	private PVector momentResult = new PVector();
	private PVector deltaPos = new PVector();
	private PVector deltaRot = new PVector();

	PVector dispVec = new PVector();
	float rotVec;
	float windLoad;
	PVector load;//testing JDM

	PVector prevPos = new PVector();
	PVector prevRot = new PVector();
	boolean switcher = false;

	public int id;
	public boolean linked = false;
	public boolean clamped = false;
	public boolean selected = false;
	private int activeGizmo = 3;

	private PVector posAcc = new PVector();
	private PVector rotAcc;
	private float posMass = 1;
	private float rotMass = 1;
	public boolean hover;

	float posStep = 0.00001f;
	private float rotStep = 0.0000000001f;
	private int startMillis;
	PVector[] damping = new PVector[2];
	PVector[] vel = new PVector[2];
	private int graphCount;

	Node(PApplet p, Structure structure, float x, float y, float z) {
		this.p = p;
		this.structure = structure;
		pos = new PVector(x, y, z);
		prevPos = new PVector(x, y, z);
		visualPos = new PVector();
		initPos();
		// loadVec.set(0, 0, -100);
	}

	Node(PApplet p, Structure structure, PVector iVec) {
		this.p = p;
		this.structure = structure;
		pos = new PVector(iVec.x, iVec.y, iVec.z);
		prevPos = new PVector(iVec.x, iVec.y, iVec.z);
		visualPos = new PVector();
		// loadVec.set(0, 0, -100);
		initPos();
		startMillis = p.millis();
		vel[0] = new PVector();
		vel[1] = new PVector();
		load=new PVector();//testing JDM
		
	}


	public void euler(float h1, float h2) {

		PVector[] k1 = f(pos, rot);
		k1[0].mult(h1);
		k1[1].mult(h2);

		if (!linked)
			pos.add(k1[0]);
		if (!clamped)
			rot.add(k1[1]);

		dispVec = PVector.sub(pos, initialPos);
		graph.add(dispVec);
		graphTimes.add(p.millis() - startMillis);
	}

	public void verlet(float h1, float h2) {
		structure.fout.println("");
		structure.fout.println("//////node id="+this.id);
		PVector[] k1 = f(pos, rot, vel, h1, h2);

		if (!linked) {
			pos.add(PVector.mult(k1[0], h1));
			// vel[0] = k1[0].get();
		}
		if (!clamped) {
			structure.fout.println("node.rot before mult by K1 ="+rot);
			structure.fout.println("K1 ="+k1[1]);
			rot.add(PVector.mult(k1[1], h2));
			// vel[1] = k1[1].get();
			structure.fout.println("node.rot after mult by K1 ="+rot);
		}

		dispVec = PVector.sub(pos, initialPos);
		if (graphCount++ % 100 == 0) {
			graph.add(dispVec);
			graphTimes.add(p.millis() - startMillis);
		}
		
		
	}


	public PVector[] f(PVector currentPos, PVector currentRot) {

		PVector posTemp = this.pos.get();
		PVector rotTemp = this.rot.get();

		PVector[] result = new PVector[2];
		result[0] = new PVector();
		result[1] = new PVector();

		forceVec = new PVector();
		momentVec = new PVector();

		pos = currentPos.get();
		rot = currentRot.get();

		// TODO Synchronous??
		for (int i = 0; i < nodeBeams.size(); i++) {
			//nodeBeams.get(i).eval();
			forceVec.add(nodeBeams.get(i).getReaction(this));
			momentVec.add(nodeBeams.get(i).getMoment(this));
		}
		
		// TODO
		posMass = nodeBeams.size();
		result[0] = PVector.mult(forceVec.get(), 1 / posMass);

		rotMass = nodeBeams.size();
		result[1] = PVector.mult(momentVec.get(), 1 / rotMass);

		pos = posTemp.get();
		rot = rotTemp.get();

		return result;
	}

	public PVector[] f(PVector currentPos, PVector currentRot, PVector[] cVel,
			float h1, float h2) {//current used function

		PVector posTemp = this.pos.get();
		PVector rotTemp = this.rot.get();

		PVector[] acc = new PVector[2];
		acc[0] = new PVector();
		acc[1] = new PVector();

		forceVec = new PVector();
		momentVec = new PVector();

		pos = currentPos.get();
		rot = currentRot.get();

		float mass = 0;
		float rotMass = 0;

		for (int i = 0; i < nodeBeams.size(); i++) {
			//nodeBeams.get(i).eval();
			forceVec.add(nodeBeams.get(i).getReaction(this));
			//PApplet.println("forceVec="+forceVec);
			momentVec.add(nodeBeams.get(i).getMoment(this));
			forceVec.add(load);//JDM user input loads on nodes
			mass += nodeBeams.get(i).getMass() / 2;//TODO check if this mass function is correct on the element side
			rotMass += nodeBeams.get(i).getMass()
					* Math.pow(nodeBeams.get(i).L, 2) / 105;
		}
		structure.fout.println("momentVec="+momentVec);
		
		damping[0] = PVector.mult(cVel[0], -100);
		forceVec.add(damping[0]);
		damping[1] = PVector.mult(cVel[1], -100000);
		momentVec.add(damping[1]);
		
		structure.fout.println("momentVec after damping="+momentVec);
		
		acc[0] = PVector.mult(forceVec.get(), 1 / mass);
		acc[1] = PVector.mult(momentVec.get(), 1 / mass * 100f);
		
		structure.fout.println("acc[1]="+acc[1]);
		
		if (!linked)
			cVel[0].add(PVector.mult(acc[0], h1));
		if (!clamped)
			cVel[1].add(PVector.mult(acc[1], h2));
		
		structure.fout.println("cVel[1]="+cVel[1]);
				
		pos = posTemp.get();
		rot = rotTemp.get();

		return cVel;
	}

/**
 * This is superseded by euler and verlet functions
 * @deprecated
 */
	private void getForces() {

		for (int i = 0; i < nodeBeams.size(); i++) {
			nodeBeams.get(i).eval(0);//0 added by JDM // unnecessary
		}

		forceVec = new PVector();
		momentVec = new PVector();

		for (int i = 0; i < nodeBeams.size(); i++) {
			momentVec.add(nodeBeams.get(i).getMoment(this));
			forceVec.add(nodeBeams.get(i).getReaction(this));
		}
		// Add concentrated loads and moments on nodes
		// forceVec.add(loadVec);
		// momentVec.add(concMomentVec);

	}

	/**
	 * This is superseded by euler and verlet functions
	 * @deprecated
	 */
	private void move() {

		if (linked) {
			result = PVector.mult(forceVec.get(), -1);
			forceVec.add(result);
		} else {

			posMass = nodeBeams.size();
			posAcc = PVector.mult(forceVec.get(), 1 / posMass);
			deltaPos = PVector.mult(posAcc, posStep);
			pos.add(deltaPos);

		}

	}

	/**
	 * This is superseded by euler and verlet functions
	 * @deprecated
	 */
	private void rotate() {

		if (clamped) {
			momentResult = PVector.mult(momentVec.get(), -1);
			momentVec.add(momentResult);
		} else {

			rotMass = nodeBeams.size();
			rotAcc = PVector.mult(momentVec.get(), 1 / posMass);
			deltaRot = PVector.mult(rotAcc.get(), rotStep);
			rot.add(deltaRot);
		}

	}

	/**
	 * When an element is created, it calls this function to be added
	 * in the nodeBeams arrayList in each of the nodes.
	 * @param BeamIn
	 */
	public void addBeam(Element BeamIn) {
		nodeBeams.add(BeamIn);
	}

	/**
	 * Isolates the current node
	 */
	public void unLink() {
		nodeBeams = new ArrayList<Element>();
	}
	
	//by JDM, testing ...
	public void getBoundingBox(){
		float maxX = 0;
		float maxY = 0;
		float maxZ = 0;
		
		for(Element beam : this.nodeBeams){
			float w = beam.section.getBeamWidth();
			float h = beam.section.getBeamHeight();
			PVector xAxis = beam.getInitCS().xAxis;
			PVector yAxis = beam.getInitCS().yAxis;
			PVector zAxis = beam.getInitCS().zAxis;
		
			//PApplet.println(xAxis);
			//these two cases correspond to X and Y being on the ground
			if(Math.abs(xAxis.x) == 1 && Math.abs(yAxis.y) == 1){
				if(w>maxY)maxY=w;
				if(h>maxZ)maxZ=h;
			}else if(Math.abs(xAxis.y) == 1 && Math.abs(yAxis.x) == 1){
				if(w>maxX)maxX=w;
				if(h>maxZ)maxZ=h;
			}
			//these two other cases correspond to X and Z being on the ground
			else if(Math.abs(xAxis.x) == 1 && Math.abs(zAxis.y) == 1){
				if(w>maxZ)maxZ=w;
				if(h>maxY)maxY=h;
			}else if(Math.abs(xAxis.y) == 1 && Math.abs(zAxis.x) == 1){
				if(w>maxZ)maxZ=w;
				if(h>maxX)maxX=h;
			}
			//these two other cases correspond to Y and Z being on the ground
			else if(Math.abs(yAxis.x) == 1 && Math.abs(zAxis.y) == 1){
				if(w>maxX)maxX=w;
				if(h>maxY)maxY=h;
			}else if(Math.abs(yAxis.y) == 1 && Math.abs(zAxis.x) == 1){
				if(w>maxY)maxY=w;
				if(h>maxX)maxX=h;
			}
		}
		
		for(Element beam : this.nodeBeams){
			PVector xAxis = beam.getInitCS().xAxis;
			if(Math.abs(xAxis.x) == 1){
				if(this.id == beam.a.id){
					beam.nx[0]=maxX/2;
				}else{
					beam.nx[1]=maxX/2;
				}
			}else if(Math.abs(xAxis.y) == 1){
				if(this.id == beam.a.id){
					beam.nx[0]=maxY/2;
				}else{
					beam.nx[1]=maxY/2;
				}
			}else if(Math.abs(xAxis.z) == 1){
				if(this.id == beam.a.id){
					beam.nx[0]=maxZ/2;
				}else{
					beam.nx[1]=maxZ/2;
				}
			}
		}
		
		//PApplet.println(maxX+", "+maxY+", "+maxZ);
		p.fill(255, 200);
		p.stroke(100);
		p.pushMatrix();
		p.translate(this.visualPos.x, this.visualPos.y, this.visualPos.z);
		p.rotateX(-this.visualRot.x);
		p.rotateY(-this.visualRot.y);
		p.rotateZ(-this.visualRot.z);
		p.box(maxX, maxY, maxZ);
		p.popMatrix();
		
		
	}
	
	
	
/**
 * Draws the current node to the PApplet passed on as a parameter
 * @param window
 */
	public void draw(PApplet window) {

		// Get the visualisation scale from the toolbox
		dispScale = structure.toolbox.getDispScale();

		// This computes the current displacement of the node from its original position
		visualPos = new PVector(initialPos.x + dispVec.x * dispScale,
				initialPos.y + dispVec.y * dispScale, initialPos.z + dispVec.z
						* dispScale);

		// This computes the current rotation of the node from its original rotation
		visualRot = new PVector(dispScale * rot.x, dispScale * rot.y, dispScale
				* rot.z);

		// Draw Rotation Lines
		window.pushMatrix();
		window.translate(visualPos.x, visualPos.y, visualPos.z);
		
		if (selected){
			//displayData();disabled by JDM, this just draws a 3d text with the id of the node
			drawLoad();//before coord system rotation because loads on nodes do not rotate
		}
		
		Element beam=nodeBeams.get(0);//we assume support nodes only have 1 beam -- this might need revision
		// Select the colour of the node
		if (clamped) {
			drawClampedLink(window, beam, 2, 6);//float determines the scale, integer the hatch density
			window.fill(255, 0, 0);
			window.stroke(255, 0, 0);
			
		} else {
			if (linked) {
				drawHingeLink(window, beam, 2, 6);//float determines the scale, integer the hatch density
				window.fill(0, 0, 255);
				window.stroke(0, 0, 255);
				
			} else {
				window.fill(0, 255, 0);
				window.stroke(0, 255, 0);
			}
		}
		if (hover)
			window.stroke(255, 200, 0);
		
		window.rotateX(-visualRot.x);//i dont think this is being used for anyth but would be useful to draw stuff aligned to node rotation
		window.rotateY(-visualRot.y);
		window.rotateZ(-visualRot.z);
		
		// Draw a point
		window.strokeWeight(10);
		if(!structure.rendering)window.point(0, 0, 0);
		window.strokeWeight(1);
		window.popMatrix();

		// drawVec(forceVec, p, 1, 0);
		// drawVec(damping[0], p, 1000, 1);

		if (selected)
			drawGizmo(window.g, 500, 1, activeGizmo);//JDM change stroke weight factor to 1 instead of 3 (careful, later it is mult by 2...)
			//drawGizmo(window.g, 500, 3, activeGizmo);//disabled by JDM
			
	}

	private void drawForces(PApplet window, float scale) {

		window.strokeWeight(5);
		window.stroke(255, 0, 0);
		window.line(visualPos.x, visualPos.y, visualPos.z, visualPos.x
				+ forceVec.x * scale, visualPos.y + forceVec.y * scale,
				visualPos.z + forceVec.z * scale);
		window.strokeWeight(1);

	}

	private void drawReaction(PApplet window, float scale) {

		// Draw reaction force
		window.stroke(255, 0, 255);
		window.line(visualPos.x, visualPos.y, visualPos.z, visualPos.x
				+ result.x * scale, visualPos.y + result.y * scale, visualPos.z
				+ result.z * scale);
	}

	private void drawVec(PVector vec, PApplet window, float scale, int col) {

		// Draw reaction force
		window.stroke(col * 255, 0, 0);
		window.line(visualPos.x, visualPos.y, visualPos.z, visualPos.x + vec.x
				* scale, visualPos.y + vec.y * scale, visualPos.z + vec.z
				* scale);
	}	
	
	/**
	 * JDM TESTING / Draws the external load of the node
	 */
	private void drawLoad() {
		
		//p.scale(1, -1, 1);
		p.stroke(255,0,0);
		p.strokeWeight(2);
		
		PVector ref=new PVector(0,1,0);
		if(this.load.x==0 && this.load.z==0 && this.load.y!=0)ref = new PVector(1,0,0);//if load is in y we change the ref vector to x
		PVector cross=ref.cross(this.load);
		cross.normalize();
		
		PVector arrow1=this.load.get();
		arrow1.normalize();
		PVector arrow2=arrow1.get();
		arrow2.mult(-1);
		
		arrow1.add(cross);
		arrow1.mult(-200);
		arrow2.add(cross);
		arrow2.mult(200);
		
		//p.line(load.x, load.y, load.z, load.x+arrow1.x, load.y+arrow1.y, load.z+arrow1.z);//departing from node
		//p.line(load.x, load.y, load.z, load.x+arrow2.x, load.y+arrow2.y, load.z+arrow2.z);//departing from node
		p.line(0, 0, 0, arrow1.x, arrow1.y, arrow1.z);//arrow arrives at node
		p.line(0, 0, 0, arrow2.x, arrow2.y, arrow2.z);//arrow arrives at node
		p.line(0, 0, 0, -load.x, -load.y, -load.z);
		//p.line(this.pos.x, this.pos.y, this.pos.z,loadEndPt.x, loadEndPt.y, loadEndPt.z);
		//p.text("Node " + id, 10, 10);
		//p.scale(1, -1, 1);

	}
	
	/**
	 * Displays the id of the node
	 */
	public void displayData() {

		p.scale(1, -1, 1);
		p.fill(0);
		p.textAlign(PConstants.LEFT);
		p.text("Node " + id, 10, 10);
		p.scale(1, -1, 1);

	}
	
	/**
	 * JDM / Draws the external link type of the node
	 */
	private void drawClampedLink(PApplet window, Element beam, float f, int n) {
		window.stroke(120);
		PVector plane=beam.planeVec.get();
		PVector beamV=beam.beamVec.get();
		plane.normalize();
		beamV.normalize();
		
		PVector h=plane.cross(beamV);
		h.mult(100*f);
		PVector v=PVector.mult(beamV.get(), 100*f);
		
		if(this.id==beam.a.id){//for proper orientation
			//h.mult(-1);
			v.mult(-1);
		}
		PVector d=PVector.add(v, h);
		d.mult((float)(0.3));
		window.line(-h.x, -h.y, -h.z, h.x, h.y, h.z);
		
		PVector org=new PVector(-h.x, -h.y, -h.z);	
		float length=h.mag();
		h.normalize();
		
		for(int i=0;i<n+1;i++){
			window.line(org.x, org.y, org.z, org.x+d.x, org.y+d.y, org.z+d.z);
			PVector step=PVector.mult(h.get(), 2*length/((float)(n)));
			org.add(step);
		}
	}
	
	/**
	 * JDM/ Draws the external link type of the node
	 */
	private void drawHingeLink(PApplet window, Element beam, float f, int n) {
		window.stroke(120);
		PVector plane=beam.planeVec.get();
		PVector beamV=beam.beamVec.get();
		plane.normalize();
		beamV.normalize();
		
		PVector h=plane.cross(beamV);
		h.mult(100*f);
		PVector v=PVector.mult(beamV.get(), 100*f);
		
		if(this.id==beam.a.id){//for proper orientation
			//h.mult(-1);
			v.mult(-1);
		}
		
		PVector d1=PVector.add(h, v);
		h.mult(-1);
		PVector d2=PVector.add(h, v);
		window.line(d1.x, d1.y, d1.z, d2.x, d2.y, d2.z);
		window.line(0, 0, 0, d1.x, d1.y, d1.z);
		window.line(0, 0, 0, d2.x, d2.y, d2.z);
		
		
		
		PVector org=new PVector(-h.x, -h.y, -h.z);	
		org.add(v);
		float length=h.mag();
		h.normalize();
		d1.mult((float)(0.3));
		
		for(int i=0;i<n+1;i++){
			window.line(org.x, org.y, org.z, org.x+d1.x, org.y+d1.y, org.z+d1.z);
			PVector step=PVector.mult(h.get(), 2*length/((float)(n)));
			org.add(step);
		}
		
	}
	
	/**
	 * Returns the connection state of the node
	 * @return
	 */
	public boolean isDisconnected() {
		if (nodeBeams.size() < 1)
			return true;
		return false;
	}

	/**
	 * Returns the displacement vector of the node
	 * @return
	 */
	public float getDisp() {
		float disp;
		disp = dispVec.mag();
		return disp;
	}

	/**
	 * Returns the node to its initial position
	 */
	private void initPos() {
		initialPos = new PVector(pos.x, pos.y, pos.z);
	}

	/**
	 * Draws a gizmo to allow the user to modify the position and
	 * rotation of the selected node
	 * @param window
	 * @param scale
	 * @param weight
	 * @param active
	 */
	public void drawGizmo(PGraphics window, float scale, int weight, int active) {

		PVector gizmoX = new PVector(1, 0, 0);
		PVector gizmoY = new PVector(0, 1, 0);
		PVector gizmoZ = new PVector(0, 0, 1);

		if (active == 0) {
			window.stroke(230, 200, 30);
			window.strokeWeight(2 * weight);
		} else {
			window.stroke(255, 0, 0);
			window.strokeWeight(weight);
		}
		window.line(visualPos.x, visualPos.y, visualPos.z, visualPos.x
				+ gizmoX.x * scale, visualPos.y + gizmoX.y * scale, visualPos.z
				+ gizmoX.z * scale);
		window.strokeWeight(4 * weight);
		window.point(visualPos.x + gizmoX.x * scale, visualPos.y + gizmoX.y
				* scale, visualPos.z + gizmoX.z * scale);

		if (active == 1) {
			window.stroke(230, 200, 30);
			window.strokeWeight(2 * weight);
		} else {
			window.stroke(0, 255, 0);
			window.strokeWeight(weight);
		}
		window.line(visualPos.x, visualPos.y, visualPos.z, visualPos.x
				+ gizmoY.x * scale, visualPos.y + gizmoY.y * scale, visualPos.z
				+ gizmoY.z * scale);
		window.strokeWeight(4 * weight);
		window.point(visualPos.x + gizmoY.x * scale, visualPos.y + gizmoY.y
				* scale, visualPos.z + gizmoY.z * scale);

		if (active == 2) {
			window.stroke(230, 200, 30);
			window.strokeWeight(2 * weight);
		} else {
			window.stroke(0, 0, 255);
			window.strokeWeight(weight);
		}
		window.line(visualPos.x, visualPos.y, visualPos.z, visualPos.x
				+ gizmoZ.x * scale, visualPos.y + gizmoZ.y * scale, visualPos.z
				+ gizmoZ.z * scale);
		window.strokeWeight(4 * weight);
		window.point(visualPos.x + gizmoZ.x * scale, visualPos.y + gizmoZ.y
				* scale, visualPos.z + gizmoZ.z * scale);
		window.strokeWeight(1);

	}

	public void activeGizmo(int i) {
		this.activeGizmo = i;
	}

	public void modify(float dist, int mode) {

		if (mode == 0) {
			if (activeGizmo == 0)
				this.pos.x += dist / 10;
			if (activeGizmo == 1)
				this.pos.y += dist / 10;
			if (activeGizmo == 2)
				this.pos.z += dist / 10;
		} else {
			if (activeGizmo == 0)
				this.rot.x += dist / 100000;
			if (activeGizmo == 1)
				this.rot.y += dist / 100000;
			if (activeGizmo == 2)
				this.rot.z += dist / 100000;
		}

	}

	public float getYscaleGraph() {

		float maxDisp = 0;
		for (int i = 0; i < graph.size(); i++) {
			if (Math.abs(graph.get(i).z) > maxDisp)
				maxDisp = Math.abs(graph.get(i).z);
		}
		return maxDisp;
	}

	public float getXscaleGraph() {

		float maxDisp = 0;
		for (int i = 0; i < graphTimes.size(); i++) {
			if (Math.abs(graphTimes.get(i)) > maxDisp)
				maxDisp = Math.abs(graphTimes.get(i));
		}
		return maxDisp;
	}
}
