package BeamCalc;

import processing.core.PVector;

public class FloatingLoad {
	PVector load;//the load itself
	float xpos;//x coordinate in local coord system of the load's  position
	
	FloatingLoad(PVector load, float xpos){
		this.load=load;
		this.xpos=xpos;
	}
	
	
}