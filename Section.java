/**
 * (./) Section.java v0.1 05/09/2011
 * @author Enrique Ramos Melgar
 * http://www.esc-studio.com
 *
 * THIS LIBRARY IS RELEASED UNDER A CREATIVE COMMONS ATTRIBUTION 3.0 LICENSE
 * http://creativecommons.org/licenses/by/3.0/
 * http://www.processing.org/
 */
package BeamCalc;

import processing.core.PApplet;
import processing.core.PConstants;

/**
 * This class contains the geometric data of the section
 * @author Enrique Ramos
 *
 */
//TODO check how the updaeInertias function is working, only for rect section?
//TODO check how the are A is being updated, from table values or as a product of width*height, in rect section or in HEB and IPE?
//TODO Update material density and own-weight of beams from normalized profiles
public class Section {
	public String profileType;
	// Section Properties
	public float Ixz; // moment of inertia on the main axis (mm4)
	public float Ixy; // moment of inertia on the secondary axis(mm4)
	public float J; // moment of inertia in Torsion (mm4)
	public float A; // Area of Section (mm2)
	public float beamHeight; // mm
	public float beamWidth; // mm
	public boolean rotated;
	
	public Section() {
		//initial values of the section
		profileType="rect";
		this.beamWidth = 70; //mm
		this.beamHeight = 150; //mm
		this.Ixz = (float)(beamWidth*(Math.pow(beamHeight,3))/12.0); //mm4
		// this.Ixy = 3492000; // mm4 1338000
		this.Ixy = (float)(beamHeight*(Math.pow(beamWidth,3))/12.0); //mm4
		this.J = 52370000; // mm4 
		this.A = beamHeight*beamWidth; // mm2
		this.rotated = false;

	}
	
	/**
	 * This function is called from the elementUI, and rotates the section when rotate button is pressed.
	 */
	public void rotate(){
		if(this.rotated==false){
			this.rotated = true;
		}else if(this.rotated == true){
			this.rotated = false;
		}
		//width and height
		float oldBeamWidth = getBeamWidth();
		this.beamWidth = getBeamHeight();
		this.beamHeight = oldBeamWidth;
		
		//inertias
		float oldIxz = getIxz();
		this.Ixz = getIxy();
		this.Ixy = oldIxz;
	}

	/**
	 * This function is called from the element's draw() function, and renders the surface of the current
	 * section accurately, following the element's deformation state.
	 * @param p
	 * @param elm
	 * @param scale
	 */
	public void renderSrfs(PApplet p, Element elm, float scale) {
		
		float tempAngleXY = 0;
		float tempAngleXZ = 0;
		float tempAngleX = 0;
		
		p.noStroke();
		p.fill(255, 200);
		//draws one side of the beam
		p.beginShape(PConstants.QUAD_STRIP);
		for (int i = 0; i < elm.numPoints; i++) {
			float[] values = getSectionDrawingValues(elm, scale, tempAngleXZ, tempAngleXY, i);
			
			tempAngleXZ = values[0];
			tempAngleXY = values[1];
			float xi = values[2];
			float yi = values[3];
			float zi = values[4];
			
			int factorWidth=0;
			if(this.profileType.equals("rect") || this.rotated == true)factorWidth=1;
			
			
			p.vertex(
					xi * elm.dispLScale + beamHeight * PApplet.sin(tempAngleXZ) + beamWidth * PApplet.sin(tempAngleXY),
					yi * scale	- 0.5f*beamWidth * PApplet.cos(tempAngleXY)*factorWidth, 
					zi	* scale - 0.5f*beamHeight * PApplet.cos(tempAngleXZ));

			p.vertex(
					xi * elm.dispLScale - beamHeight * PApplet.sin(tempAngleXZ) + beamWidth * PApplet.sin(tempAngleXY), 
					yi * scale - 0.5f*beamWidth * PApplet.cos(tempAngleXY)*factorWidth, 
					zi	* scale + 0.5f*beamHeight * PApplet.cos(tempAngleXZ));

		}
		p.endShape(PConstants.CLOSE);
		
		if(this.profileType.equals("rect") || this.rotated == true){
			//draws other side of the beam if profile type is rectangular
			p.beginShape(PConstants.QUAD_STRIP);
			for (int i = 0; i < elm.numPoints; i++) {
				
				float[] values = getSectionDrawingValues(elm, scale, tempAngleXZ, tempAngleXY, i);
				
				tempAngleXZ = values[0];
				tempAngleXY = values[1];
				float xi = values[2];
				float yi = values[3];
				float zi = values[4];
				
				p.vertex(
						xi * elm.dispLScale + beamHeight * PApplet.sin(tempAngleXZ) - beamWidth * PApplet.sin(tempAngleXY), 
						yi * scale + 0.5f*beamWidth * PApplet.cos(tempAngleXY), 
						zi * scale - 0.5f*beamHeight * PApplet.cos(tempAngleXZ));
				p.vertex(
						xi * elm.dispLScale - beamHeight * PApplet.sin(tempAngleXZ) - beamWidth * PApplet.sin(tempAngleXY), 
						yi * scale + 0.5f*beamWidth * PApplet.cos(tempAngleXY), 
						zi * scale + 0.5f*beamHeight * PApplet.cos(tempAngleXZ));
	
			}
			p.endShape(PConstants.CLOSE);
		}
		
		p.beginShape(PConstants.QUAD_STRIP);
		//draws bottom side of the beam
		for (int i = 0; i < elm.numPoints; i++) {
			
			float[] values = getSectionDrawingValues(elm, scale, tempAngleXZ, tempAngleXY, i);
			
			tempAngleXZ = values[0];
			tempAngleXY = values[1];
			float xi = values[2];
			float yi = values[3];
			float zi = values[4];
			
			int factorHeight = 0;
			if(this.profileType.equals("rect") || this.rotated == false)factorHeight=1;
			
			p.vertex(
					xi * elm.dispLScale + beamHeight * PApplet.sin(tempAngleXZ) + beamWidth * PApplet.sin(tempAngleXY), 
					yi * scale - 0.5f*beamWidth * PApplet.cos(tempAngleXY), 
					zi * scale - 0.5f*beamHeight * PApplet.cos(tempAngleXZ)*factorHeight);
			p.vertex(
					xi * elm.dispLScale + beamHeight * PApplet.sin(tempAngleXZ) - beamWidth * PApplet.sin(tempAngleXY), 
					yi * scale	+ 0.5f*beamWidth * PApplet.cos(tempAngleXY), 
					zi * scale - 0.5f*beamHeight * PApplet.cos(tempAngleXZ)*factorHeight);

		}
		p.endShape(PConstants.CLOSE);

		p.beginShape(PConstants.QUAD_STRIP);
		//draws top side of the beam
		if(this.profileType.equals("rect") || this.rotated == false){
			for (int i = 0; i < elm.numPoints; i++) {
				
				float[] values = getSectionDrawingValues(elm, scale, tempAngleXZ, tempAngleXY, i);
				
				tempAngleXZ = values[0];
				tempAngleXY = values[1];
				float xi = values[2];
				float yi = values[3];
				float zi = values[4];
				
				p.vertex(
						xi * elm.dispLScale - beamHeight * PApplet.sin(tempAngleXZ) + beamWidth * PApplet.sin(tempAngleXY), 
						yi * scale - 0.5f*beamWidth * PApplet.cos(tempAngleXY), 
						zi	* scale + 0.5f*beamHeight * PApplet.cos(tempAngleXZ));
				p.vertex(
						xi * elm.dispLScale - beamHeight * PApplet.sin(tempAngleXZ) - beamWidth * PApplet.sin(tempAngleXY), 
						yi * scale + 0.5f*beamWidth * PApplet.cos(tempAngleXY), 
						zi	* scale + 0.5f*beamHeight * PApplet.cos(tempAngleXZ));
	
			}
			p.endShape(PConstants.CLOSE);
		}
	}
	
	/**
	 * This function is called from the element's draw() function, and renders the edges of the current
	 * section accurately, following the element's deformation state.
	 * @param p
	 * @param elm
	 * @param scale
	 */
	public void renderEdges(PApplet p, Element elm, float scale) {
		p.pushMatrix();
		p.translate(elm.x[0], elm.y[0][0], elm.y[1][0]);
		p.ellipse(elm.nx[0], 0, 6, 6);
		p.popMatrix();
		
		float tempAngleXY = 0;
		float tempAngleXZ = 0;
		float tempAngleX = 0;
		
		p.noFill();
		p.stroke(100);
		int factorWidth=0;
		if(this.profileType.equals("rect") || this.rotated == true)factorWidth=1;
		
		p.beginShape();
		for (int i = 0; i < elm.numPoints; i++) {
			
			float[] values = getSectionDrawingValues(elm, scale, tempAngleXZ, tempAngleXY, i);
			
			tempAngleXZ = values[0];
			tempAngleXY = values[1];
			float xi = values[2];
			float yi = values[3];
			float zi = values[4];
			
			
			PApplet.println("displScale="+elm.dispLScale);
			p.vertex(
					xi * elm.dispLScale + beamHeight * PApplet.sin(tempAngleXZ) + beamWidth * PApplet.sin(tempAngleXY),
					yi * scale	- 0.5f*beamWidth * PApplet.cos(tempAngleXY)*factorWidth, 
					zi	* scale - 0.5f*beamHeight * PApplet.cos(tempAngleXZ));

		}
		for (int i = elm.numPoints - 1; i >= 0; i--) {
			
			float[] values = getSectionDrawingValues(elm, scale, tempAngleXZ, tempAngleXY, i);
			
			tempAngleXZ = values[0];
			tempAngleXY = values[1];
			float xi = values[2];
			float yi = values[3];
			float zi = values[4];
			
			p.vertex(
					xi * elm.dispLScale - beamHeight * PApplet.sin(tempAngleXZ) + beamWidth * PApplet.sin(tempAngleXY), 
					yi * scale - 0.5f*beamWidth * PApplet.cos(tempAngleXY)*factorWidth, 
					zi	* scale + 0.5f*beamHeight * PApplet.cos(tempAngleXZ));

		}
		p.endShape(PConstants.CLOSE);
		
		if(this.profileType.equals("rect") || this.rotated == true){

			p.beginShape();
			for (int i = 0; i < elm.numPoints; i++) {
				
				float[] values = getSectionDrawingValues(elm, scale, tempAngleXZ, tempAngleXY, i);
				
				tempAngleXZ = values[0];
				tempAngleXY = values[1];
				float xi = values[2];
				float yi = values[3];
				float zi = values[4];
				
				p.vertex(
						xi * elm.dispLScale + beamHeight * PApplet.sin(tempAngleXZ) - beamWidth * PApplet.sin(tempAngleXY), 
						yi * scale + 0.5f*beamWidth * PApplet.cos(tempAngleXY), 
						zi * scale - 0.5f*beamHeight * PApplet.cos(tempAngleXZ));
			}
			for (int i = elm.numPoints - 1; i >= 0; i--) {
				
				float[] values = getSectionDrawingValues(elm, scale, tempAngleXZ, tempAngleXY, i);
				
				tempAngleXZ = values[0];
				tempAngleXY = values[1];
				float xi = values[2];
				float yi = values[3];
				float zi = values[4];
				p.vertex(
						xi * elm.dispLScale - beamHeight * PApplet.sin(tempAngleXZ) - beamWidth * PApplet.sin(tempAngleXY), 
						yi * scale + 0.5f*beamWidth * PApplet.cos(tempAngleXY), 
						zi * scale + 0.5f*beamHeight * PApplet.cos(tempAngleXZ));
	
			}
			p.endShape(PConstants.CLOSE);
		}
		

		int factorHeight = 0;
		if(this.profileType.equals("rect") || this.rotated == false)factorHeight=1;
		p.beginShape();
		for (int i = 0; i < elm.numPoints; i++) {
			
			float[] values = getSectionDrawingValues(elm, scale, tempAngleXZ, tempAngleXY, i);
			
			tempAngleXZ = values[0];
			tempAngleXY = values[1];
			float xi = values[2];
			float yi = values[3];
			float zi = values[4];
			
			p.vertex(
					xi * elm.dispLScale + beamHeight * PApplet.sin(tempAngleXZ) + beamWidth * PApplet.sin(tempAngleXY), 
					yi * scale - 0.5f*beamWidth * PApplet.cos(tempAngleXY), 
					zi * scale - 0.5f*beamHeight * PApplet.cos(tempAngleXZ)*factorHeight);
		}
		
		for (int i = elm.numPoints - 1; i >= 0; i--) {
			
			float[] values = getSectionDrawingValues(elm, scale, tempAngleXZ, tempAngleXY, i);
			
			tempAngleXZ = values[0];
			tempAngleXY = values[1];
			float xi = values[2];
			float yi = values[3];
			float zi = values[4];
			
			p.vertex(
					xi * elm.dispLScale + beamHeight * PApplet.sin(tempAngleXZ) - beamWidth * PApplet.sin(tempAngleXY), 
					yi * scale	+ 0.5f*beamWidth * PApplet.cos(tempAngleXY), 
					zi * scale - 0.5f*beamHeight * PApplet.cos(tempAngleXZ)*factorHeight);

		}
		p.endShape(PConstants.CLOSE);

		p.beginShape();

		if(this.profileType.equals("rect") || this.rotated == false){
			
			for (int i = 0; i < elm.numPoints; i++) {
				
				float[] values = getSectionDrawingValues(elm, scale, tempAngleXZ, tempAngleXY, i);
				
				tempAngleXZ = values[0];
				tempAngleXY = values[1];
				float xi = values[2];
				float yi = values[3];
				float zi = values[4];
				
				p.vertex(
						xi * elm.dispLScale - beamHeight * PApplet.sin(tempAngleXZ) + beamWidth * PApplet.sin(tempAngleXY), 
						yi * scale - 0.5f*beamWidth * PApplet.cos(tempAngleXY), 
						zi	* scale + 0.5f*beamHeight * PApplet.cos(tempAngleXZ));
			}
			
			for (int i = elm.numPoints-1; i >= 0; i--) {
				
				float[] values = getSectionDrawingValues(elm, scale, tempAngleXZ, tempAngleXY, i);
				
				tempAngleXZ = values[0];
				tempAngleXY = values[1];
				float xi = values[2];
				float yi = values[3];
				float zi = values[4];
				
				p.vertex(
						xi * elm.dispLScale - beamHeight * PApplet.sin(tempAngleXZ) - beamWidth * PApplet.sin(tempAngleXY), 
						yi * scale + 0.5f*beamWidth * PApplet.cos(tempAngleXY), 
						zi	* scale + 0.5f*beamHeight * PApplet.cos(tempAngleXZ));
	
			}
			
			p.endShape(PConstants.CLOSE);
		}
	}
	
	private float[] getSectionDrawingValues(Element elm, float scale, float tempAngleXZ, float tempAngleXY, int i){
		
		
		float xi = elm.x[i];
		float yi = elm.y[1][i];
		float zi = elm.y[0][i];
		
		if (i < elm.numPoints - 1) {
			tempAngleXZ = (float) (Math.atan2(elm.y[0][i + 1] * scale - elm.y[0][i] * scale, elm.x[i + 1] * elm.dispLScale - elm.x[i] * elm.dispLScale));
			tempAngleXY = (float) (Math.atan2(elm.y[1][i + 1] * scale - elm.y[1][i] * scale, elm.x[i + 1] * elm.dispLScale - elm.x[i] * elm.dispLScale));
		}
		
		if(i==0){
			xi = elm.nx[0];
			//yi = elm.ny[1][0];
			//zi = elm.ny[0][0];
			//tempAngleXZ = (float) (Math.atan2(elm.y[0][i + 1] * scale - yi * scale, elm.x[i + 1] * elm.dispLScale - xi * elm.dispLScale));
			//tempAngleXY = (float) (Math.atan2(elm.y[1][i + 1] * scale - zi * scale, elm.x[i + 1] * elm.dispLScale - xi * elm.dispLScale));
		}
		if(i==elm.numPoints-1){
			xi=elm.x[elm.numPoints-1]-elm.nx[1];
			//yi = elm.ny[1][1];
			//zi = elm.ny[0][1];
		}
		if(i==elm.numPoints-2){
			//tempAngleXZ = (float) (Math.atan2(elm.ny[1][1] * scale - elm.y[0][i] * scale, (elm.x[elm.numPoints-1]-elm.nx[1]) * elm.dispLScale - elm.x[i] * elm.dispLScale));
			//tempAngleXY = (float) (Math.atan2(elm.ny[0][1] * scale - elm.y[1][i] * scale, (elm.x[elm.numPoints-1]-elm.nx[1]) * elm.dispLScale - elm.x[i] * elm.dispLScale));
		}
		
		float[] values = {tempAngleXZ, tempAngleXY, xi, yi, zi};
		
		return values;
	}

	private float[] getSectionDrawingValues_OLD(Element elm, float scale, float tempAngleXZ, float tempAngleXY, int i){
		
		
		float xi = elm.x[i];
		float yi = elm.y[1][i];
		float zi = elm.y[0][i];
		
		if (i < elm.numPoints - 1) {
			tempAngleXZ = (float) (Math.atan2(elm.y[0][i + 1] * scale - elm.y[0][i] * scale, elm.x[i + 1] * elm.dispLScale - elm.x[i] * elm.dispLScale));
			tempAngleXY = (float) (Math.atan2(elm.y[1][i + 1] * scale - elm.y[1][i] * scale, elm.x[i + 1] * elm.dispLScale - elm.x[i] * elm.dispLScale));
		}
		
		if(i==0){
			xi = elm.nx[0];
			yi = elm.ny[1][0];
			zi = elm.ny[0][0];
			tempAngleXZ = (float) (Math.atan2(elm.y[0][i + 1] * scale - yi * scale, elm.x[i + 1] * elm.dispLScale - xi * elm.dispLScale));
			tempAngleXY = (float) (Math.atan2(elm.y[1][i + 1] * scale - zi * scale, elm.x[i + 1] * elm.dispLScale - xi * elm.dispLScale));
		}
		if(i==elm.numPoints-1){
			xi=elm.x[elm.numPoints-1]-elm.nx[1];
			yi = elm.ny[1][1];
			zi = elm.ny[0][1];
		}
		if(i==elm.numPoints-2){
			tempAngleXZ = (float) (Math.atan2(elm.ny[1][1] * scale - elm.y[0][i] * scale, (elm.x[elm.numPoints-1]-elm.nx[1]) * elm.dispLScale - elm.x[i] * elm.dispLScale));
			tempAngleXY = (float) (Math.atan2(elm.ny[0][1] * scale - elm.y[1][i] * scale, (elm.x[elm.numPoints-1]-elm.nx[1]) * elm.dispLScale - elm.x[i] * elm.dispLScale));
		}
		
		float[] values = {tempAngleXZ, tempAngleXY, xi, yi, zi};
		
		return values;
	}
	
	/**
	 * This function is called from the element's draw() function, and renders the surface of the current
	 * section accurately, following the element's deformation state.
	 * @param p
	 * @param elm
	 * @param scale
	 */
	public void renderSrfsOld(PApplet p, Element elm, float scale) {
		
		float tempAngleXY = 0;
		float tempAngleXZ = 0;
		float tempAngleX = 0;
		
		p.noStroke();
		p.fill(255, 200);
		//draws one side of the beam
		p.beginShape(PConstants.QUAD_STRIP);
		for (int i = 0; i < elm.numPoints; i++) {
			if (i < elm.numPoints - 1) {
				tempAngleXZ = (float) (Math.atan2(elm.y[0][i + 1] * scale - elm.y[0][i] * scale, elm.x[i + 1] * elm.dispLScale - elm.x[i] * elm.dispLScale));
				tempAngleXY = (float) (Math.atan2(elm.y[1][i + 1] * scale - elm.y[1][i] * scale, elm.x[i + 1] * elm.dispLScale - elm.x[i] * elm.dispLScale));
				// TODO Include Torsion!!
				tempAngleX = (elm.bAngles.x * elm.L / elm.x[i] - elm.aAngles.x);
			}
			
			int factorWidth=0;
			if(this.profileType.equals("rect") || this.rotated == true)factorWidth=1;
			
			
			p.vertex(
					elm.x[i] * elm.dispLScale + beamHeight * PApplet.sin(tempAngleXZ) + beamWidth * PApplet.sin(tempAngleXY),
					elm.y[1][i] * scale	- 0.5f*beamWidth * PApplet.cos(tempAngleXY)*factorWidth, 
					elm.y[0][i]	* scale - 0.5f*beamHeight * PApplet.cos(tempAngleXZ));

			p.vertex(
					elm.x[i] * elm.dispLScale - beamHeight * PApplet.sin(tempAngleXZ) + beamWidth * PApplet.sin(tempAngleXY), 
					elm.y[1][i] * scale - 0.5f*beamWidth * PApplet.cos(tempAngleXY)*factorWidth, 
					elm.y[0][i]	* scale + 0.5f*beamHeight * PApplet.cos(tempAngleXZ));

		}
		p.endShape(PConstants.CLOSE);
		
		if(this.profileType.equals("rect") || this.rotated == true){
			//draws other side of the beam if profile type is rectangular
			p.beginShape(PConstants.QUAD_STRIP);
			for (int i = 0; i < elm.numPoints; i++) {
				if (i < elm.numPoints - 1) {
					tempAngleXZ = (float) (Math.atan2(elm.y[0][i + 1] * scale - elm.y[0][i] * scale, elm.x[i + 1] * elm.dispLScale - elm.x[i] * elm.dispLScale));
					tempAngleXY = (float) (Math.atan2(elm.y[1][i + 1] * scale - elm.y[1][i] * scale, elm.x[i + 1] * elm.dispLScale
							- elm.x[i] * elm.dispLScale));
				}
				
				p.vertex(
						elm.x[i] * elm.dispLScale + beamHeight * PApplet.sin(tempAngleXZ) - beamWidth * PApplet.sin(tempAngleXY), 
						elm.y[1][i] * scale + 0.5f*beamWidth * PApplet.cos(tempAngleXY), 
						elm.y[0][i] * scale - 0.5f*beamHeight * PApplet.cos(tempAngleXZ));
				p.vertex(
						elm.x[i] * elm.dispLScale - beamHeight * PApplet.sin(tempAngleXZ) - beamWidth * PApplet.sin(tempAngleXY), 
						elm.y[1][i] * scale + 0.5f*beamWidth * PApplet.cos(tempAngleXY), 
						elm.y[0][i] * scale + 0.5f*beamHeight * PApplet.cos(tempAngleXZ));
	
			}
			p.endShape(PConstants.CLOSE);
		}
		
		p.beginShape(PConstants.QUAD_STRIP);
		//draws bottom side of the beam
		for (int i = 0; i < elm.numPoints; i++) {
			int factorHeight = 0;
			if(this.profileType.equals("rect") || this.rotated == false)factorHeight=1;
			
			if (i < elm.numPoints - 1) {
				tempAngleXZ = (float) (Math.atan2(elm.y[0][i + 1] * scale - elm.y[0][i] * scale, elm.x[i + 1] * elm.dispLScale - elm.x[i] * elm.dispLScale));
				tempAngleXY = (float) (Math.atan2(elm.y[1][i + 1] * scale - elm.y[1][i] * scale, elm.x[i + 1] * elm.dispLScale - elm.x[i] * elm.dispLScale));

			}
			p.vertex(
					elm.x[i] * elm.dispLScale + beamHeight * PApplet.sin(tempAngleXZ) + beamWidth * PApplet.sin(tempAngleXY), 
					elm.y[1][i] * scale - 0.5f*beamWidth * PApplet.cos(tempAngleXY), 
					elm.y[0][i] * scale - 0.5f*beamHeight * PApplet.cos(tempAngleXZ)*factorHeight);
			p.vertex(
					elm.x[i] * elm.dispLScale + beamHeight * PApplet.sin(tempAngleXZ) - beamWidth * PApplet.sin(tempAngleXY), 
					elm.y[1][i] * scale	+ 0.5f*beamWidth * PApplet.cos(tempAngleXY), 
					elm.y[0][i] * scale - 0.5f*beamHeight * PApplet.cos(tempAngleXZ)*factorHeight);

		}
		p.endShape(PConstants.CLOSE);

		p.beginShape(PConstants.QUAD_STRIP);
		//draws top side of the beam
		if(this.profileType.equals("rect") || this.rotated == false){
			for (int i = 0; i < elm.numPoints; i++) {
				if (i < elm.numPoints - 1) {
					tempAngleXZ = (float) (Math.atan2(elm.y[0][i + 1] * scale - elm.y[0][i] * scale, elm.x[i + 1] * elm.dispLScale - elm.x[i] * elm.dispLScale));
					tempAngleXY = (float) (Math.atan2(elm.y[1][i + 1] * scale - elm.y[1][i] * scale, elm.x[i + 1] * elm.dispLScale - elm.x[i] * elm.dispLScale));
	
				}
				p.vertex(
						elm.x[i] * elm.dispLScale - beamHeight * PApplet.sin(tempAngleXZ) + beamWidth * PApplet.sin(tempAngleXY), 
						elm.y[1][i] * scale - 0.5f*beamWidth * PApplet.cos(tempAngleXY), 
						elm.y[0][i]	* scale + 0.5f*beamHeight * PApplet.cos(tempAngleXZ));
				p.vertex(
						elm.x[i] * elm.dispLScale - beamHeight * PApplet.sin(tempAngleXZ) - beamWidth * PApplet.sin(tempAngleXY), 
						elm.y[1][i] * scale + 0.5f*beamWidth * PApplet.cos(tempAngleXY), 
						elm.y[0][i]	* scale + 0.5f*beamHeight * PApplet.cos(tempAngleXZ));
	
			}
			p.endShape(PConstants.CLOSE);
		}
	}
	
	/**
	 * This function is called from the element's draw() function, and renders the edges of the current
	 * section accurately, following the element's deformation state.
	 * @param p
	 * @param elm
	 * @param scale
	 */
	public void renderEdgesOld(PApplet p, Element elm, float scale) {
		
		float tempAngleXY = 0;
		float tempAngleXZ = 0;
		float tempAngleX = 0;
		p.noFill();
		p.stroke(100);
		int factorWidth=0;
		if(this.profileType.equals("rect") || this.rotated == true)factorWidth=1;
		
		p.beginShape();
		for (int i = 0; i < elm.numPoints; i++) {
			if (i < elm.numPoints - 1) {
				tempAngleXZ = (float) (Math.atan2(elm.y[0][i + 1] * scale - elm.y[0][i] * scale, elm.x[i + 1] * elm.dispLScale - elm.x[i] * elm.dispLScale));
				tempAngleXY = (float) (Math.atan2(elm.y[1][i + 1] * scale - elm.y[1][i] * scale, elm.x[i + 1] * elm.dispLScale - elm.x[i] * elm.dispLScale));
				// TODO Include Torsion!!
				tempAngleX = (elm.bAngles.x * elm.L / elm.x[i] - elm.aAngles.x);
			}
			
			p.vertex(
					elm.x[i] * elm.dispLScale + beamHeight * PApplet.sin(tempAngleXZ) + beamWidth * PApplet.sin(tempAngleXY),
					elm.y[1][i] * scale	- 0.5f*beamWidth * PApplet.cos(tempAngleXY)*factorWidth, 
					elm.y[0][i]	* scale - 0.5f*beamHeight * PApplet.cos(tempAngleXZ));

		}
		for (int i = elm.numPoints - 1; i >= 0; i--) {
			if (i < elm.numPoints - 1) {
				tempAngleXZ = (float) (Math.atan2(elm.y[0][i + 1] * scale - elm.y[0][i] * scale, elm.x[i + 1] * elm.dispLScale - elm.x[i] * elm.dispLScale));
				tempAngleXY = (float) (Math.atan2(elm.y[1][i + 1] * scale - elm.y[1][i] * scale, elm.x[i + 1] * elm.dispLScale - elm.x[i] * elm.dispLScale));
				// TODO Include Torsion!!
				tempAngleX = (elm.bAngles.x * elm.L / elm.x[i] - elm.aAngles.x);
			}

			p.vertex(
					elm.x[i] * elm.dispLScale - beamHeight * PApplet.sin(tempAngleXZ) + beamWidth * PApplet.sin(tempAngleXY), 
					elm.y[1][i] * scale - 0.5f*beamWidth * PApplet.cos(tempAngleXY)*factorWidth, 
					elm.y[0][i]	* scale + 0.5f*beamHeight * PApplet.cos(tempAngleXZ));

		}
		p.endShape(PConstants.CLOSE);
		
		if(this.profileType.equals("rect") || this.rotated == true){

			p.beginShape();
			for (int i = 0; i < elm.numPoints; i++) {
				if (i < elm.numPoints - 1) {
					tempAngleXZ = (float) (Math.atan2(elm.y[0][i + 1] * scale - elm.y[0][i] * scale, elm.x[i + 1] * elm.dispLScale - elm.x[i] * elm.dispLScale));
					tempAngleXY = (float) (Math.atan2(elm.y[1][i + 1] * scale - elm.y[1][i] * scale, elm.x[i + 1] * elm.dispLScale - elm.x[i] * elm.dispLScale));
				}
				
				p.vertex(
						elm.x[i] * elm.dispLScale + beamHeight * PApplet.sin(tempAngleXZ) - beamWidth * PApplet.sin(tempAngleXY), 
						elm.y[1][i] * scale + 0.5f*beamWidth * PApplet.cos(tempAngleXY), 
						elm.y[0][i] * scale - 0.5f*beamHeight * PApplet.cos(tempAngleXZ));
			}
			for (int i = elm.numPoints - 1; i >= 0; i--) {
				if (i < elm.numPoints - 1) {
					tempAngleXZ = (float) (Math.atan2(elm.y[0][i + 1] * scale - elm.y[0][i] * scale, elm.x[i + 1] * elm.dispLScale - elm.x[i] * elm.dispLScale));
					tempAngleXY = (float) (Math.atan2(elm.y[1][i + 1] * scale - elm.y[1][i] * scale, elm.x[i + 1] * elm.dispLScale - elm.x[i] * elm.dispLScale));
				}
	
				p.vertex(
						elm.x[i] * elm.dispLScale - beamHeight * PApplet.sin(tempAngleXZ) - beamWidth * PApplet.sin(tempAngleXY), 
						elm.y[1][i] * scale + 0.5f*beamWidth * PApplet.cos(tempAngleXY), 
						elm.y[0][i] * scale + 0.5f*beamHeight * PApplet.cos(tempAngleXZ));
	
			}
			p.endShape(PConstants.CLOSE);
		}
		

		int factorHeight = 0;
		if(this.profileType.equals("rect") || this.rotated == false)factorHeight=1;
		p.beginShape();
		for (int i = 0; i < elm.numPoints; i++) {
			
			if (i < elm.numPoints - 1) {
				tempAngleXZ = (float) (Math.atan2(elm.y[0][i + 1] * scale - elm.y[0][i] * scale, elm.x[i + 1] * elm.dispLScale - elm.x[i] * elm.dispLScale));
				tempAngleXY = (float) (Math.atan2(elm.y[1][i + 1] * scale - elm.y[1][i] * scale, elm.x[i + 1] * elm.dispLScale - elm.x[i] * elm.dispLScale));

			}
			p.vertex(
					elm.x[i] * elm.dispLScale + beamHeight * PApplet.sin(tempAngleXZ) + beamWidth * PApplet.sin(tempAngleXY), 
					elm.y[1][i] * scale - 0.5f*beamWidth * PApplet.cos(tempAngleXY), 
					elm.y[0][i] * scale - 0.5f*beamHeight * PApplet.cos(tempAngleXZ)*factorHeight);
		}
		
		for (int i = elm.numPoints - 1; i >= 0; i--) {
			
			if (i < elm.numPoints - 1) {
				tempAngleXZ = (float) (Math.atan2(elm.y[0][i + 1] * scale - elm.y[0][i] * scale, elm.x[i + 1] * elm.dispLScale - elm.x[i] * elm.dispLScale));
				tempAngleXY = (float) (Math.atan2(elm.y[1][i + 1] * scale - elm.y[1][i] * scale, elm.x[i + 1] * elm.dispLScale - elm.x[i] * elm.dispLScale));

			}
			p.vertex(
					elm.x[i] * elm.dispLScale + beamHeight * PApplet.sin(tempAngleXZ) - beamWidth * PApplet.sin(tempAngleXY), 
					elm.y[1][i] * scale	+ 0.5f*beamWidth * PApplet.cos(tempAngleXY), 
					elm.y[0][i] * scale - 0.5f*beamHeight * PApplet.cos(tempAngleXZ)*factorHeight);

		}
		p.endShape(PConstants.CLOSE);

		p.beginShape();

		if(this.profileType.equals("rect") || this.rotated == false){
			for (int i = 0; i < elm.numPoints; i++) {
				if (i < elm.numPoints - 1) {
					tempAngleXZ = (float) (Math.atan2(elm.y[0][i + 1] * scale - elm.y[0][i] * scale, elm.x[i + 1] * elm.dispLScale - elm.x[i] * elm.dispLScale));
					tempAngleXY = (float) (Math.atan2(elm.y[1][i + 1] * scale - elm.y[1][i] * scale, elm.x[i + 1] * elm.dispLScale - elm.x[i] * elm.dispLScale));
	
				}
				p.vertex(
						elm.x[i] * elm.dispLScale - beamHeight * PApplet.sin(tempAngleXZ) + beamWidth * PApplet.sin(tempAngleXY), 
						elm.y[1][i] * scale - 0.5f*beamWidth * PApplet.cos(tempAngleXY), 
						elm.y[0][i]	* scale + 0.5f*beamHeight * PApplet.cos(tempAngleXZ));
			}
			for (int i = elm.numPoints-1; i >= 0; i--) {
				if (i < elm.numPoints - 1) {
					tempAngleXZ = (float) (Math.atan2(elm.y[0][i + 1] * scale - elm.y[0][i] * scale, elm.x[i + 1] * elm.dispLScale - elm.x[i] * elm.dispLScale));
					tempAngleXY = (float) (Math.atan2(elm.y[1][i + 1] * scale - elm.y[1][i] * scale, elm.x[i + 1] * elm.dispLScale - elm.x[i] * elm.dispLScale));
	
				}
					
				p.vertex(
						elm.x[i] * elm.dispLScale - beamHeight * PApplet.sin(tempAngleXZ) - beamWidth * PApplet.sin(tempAngleXY), 
						elm.y[1][i] * scale + 0.5f*beamWidth * PApplet.cos(tempAngleXY), 
						elm.y[0][i]	* scale + 0.5f*beamHeight * PApplet.cos(tempAngleXZ));
	
			}
			
			p.endShape(PConstants.CLOSE);
		}
	}
	
	public void updateInertias(){
		A = beamWidth*beamHeight;
		Ixz = (float)(beamWidth*(Math.pow(beamHeight,3))/12.0);
		Ixy = (float)(beamHeight*(Math.pow(beamWidth,3))/12.0);
		J = J;//TODO update Torsion Inertia
	}
	
	public float getIxz() {
		return Ixz;
	}

	public void setIxz(float ixz) {
		Ixz = ixz;
	}

	public float getIxy() {
		return Ixy;
	}

	public void setIxy(float ixy) {
		Ixy = ixy;
	}

	public float getJ() {
		return J;
	}

	public void setJ(float j) {
		J = j;
	}

	public float getA() {
		return A;
	}

	public void setA(float a) {
		A = a;
	}

	public float getBeamHeight() {
		return beamHeight;
	}

	public void setBeamHeight(float beamHeight) {
		this.beamHeight = beamHeight;
	}

	public float getBeamWidth() {
		return beamWidth;
	}

	public void setBeamWidth(float beamWidth) {
		this.beamWidth = beamWidth;
	}

}
