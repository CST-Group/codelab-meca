/**
 * 
 */
package br.unicamp.MECA_Demo.codelets.perceptual;

import java.util.ArrayList;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.cst.representation.owrl.AbstractObject;
import br.unicamp.cst.representation.owrl.Property;
import br.unicamp.cst.representation.owrl.QualityDimension;
import br.unicamp.meca.system1.codelets.PerceptualCodelet;
import java.util.List;

/**
 * @author andre
 *
 */
public class SmartCarPerceptualCodelet extends PerceptualCodelet {
	
	private ArrayList<Memory> sensoryMemories = new ArrayList<Memory>();

    private Memory smartCarsInfo;

	public SmartCarPerceptualCodelet(String id, ArrayList<String> sensoryCodeletsIds) {
		super(id, sensoryCodeletsIds);
	}

	/* (non-Javadoc)
	 * @see br.unicamp.cst.core.entities.Codelet#accessMemoryObjects()
	 */
	@Override
	public void accessMemoryObjects() {
		
		int index = 0;

        if (smartCarsInfo == null)
            smartCarsInfo = this.getOutput(id, index);

        if (sensoryMemories == null || sensoryMemories.size() == 0) {
            if (sensoryCodeletsIds != null) {

                for (String sensoryCodeletsId : sensoryCodeletsIds) {
                    sensoryMemories.add(this.getInput(sensoryCodeletsId, index));
                }

            }

        }

	}

	/* (non-Javadoc)
	 * @see br.unicamp.cst.core.entities.Codelet#calculateActivation()
	 */
	@Override
	public void calculateActivation() {
		
		try{

			setActivation(0.0d);

		} catch (CodeletActivationBoundsException e) {

			e.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see br.unicamp.cst.core.entities.Codelet#proc()
	 */
	@Override
	public void proc() {
		
		try{
			
			if(sensoryMemories!=null && sensoryMemories.size()>0){
				
				String smartCarsInfoString = "";
				
				for(Memory sensoryMemory : sensoryMemories) {
					
					try{
						
						String smartCarInfoString = (String) sensoryMemory.getI();
						if(smartCarInfoString!=null){
							if(smartCarsInfoString.length()==0)
								smartCarsInfoString+=smartCarInfoString;
							else
								smartCarsInfoString+="/"+smartCarInfoString;
						}						
							
					}catch(Exception e){}					
					
				}
				
				if(smartCarsInfoString.length()>0){
                                    //splits infos from multiple cars
                                    String[] splitSmartInfo = smartCarsInfoString.split("[/]");
                                    
                                    //creates a array that will be used to store smartcars individual infos
                                    List<AbstractObject> smartCarsInfoArray = new ArrayList<>();
                                    
                                    //info writen as AbstractObject
                                    for(String smartCarInfoSolo : splitSmartInfo){
                                        //adds to the list
                                        smartCarsInfoArray.add(fromStringToOWRL(smartCarInfoSolo));
                                    }
                                    
                                    //the new MO's "I" is the array
                                    smartCarsInfo.setI(smartCarsInfoArray);
					
                                    //System.out.println(smartCarsInfoString);		
					
				}else {
					
					smartCarsInfo.setI(null);
					
				}
				
			}else{
				
				smartCarsInfo.setI(null);
				
			}
			
			
		}catch(Exception e){
			
			smartCarsInfo.setI(null);
			
		}

	}

        private AbstractObject fromStringToOWRL(String info){
        List<Property> properties = new ArrayList();
        String[] splitInfo = info.split("[;]");
        
         Property smartID = new Property("CAR_ID");
        smartID.addQualityDimension(new QualityDimension("CAR_ID", splitInfo[0]));
        
        Property position = new Property("POSITION");
        position.addQualityDimension(new QualityDimension("X", splitInfo[1]));
        position.addQualityDimension(new QualityDimension("Y", splitInfo[2]));
        
        Property speed = new Property("SPEED");
        speed.addQualityDimension(new QualityDimension("SPEED",splitInfo[3]));
        
        Property laneID = new Property("LANE_ID");
        laneID.addQualityDimension(new QualityDimension("LANE_ID",splitInfo[4]));
        
        Property distanceBetweenCarAndLight = new Property("DISTANCE_UNTIL_LIGHT");
        distanceBetweenCarAndLight.addQualityDimension(new QualityDimension("DISTANCE_UNTIL_LIGHT",splitInfo[5]));
        
        //new.
        //Property currentPhase = new Property("CURRENT_PHASE");
        //currentPhase.addQualityDimension(new QualityDimension("PHASE",splitInfo[6]));
        
        Property estimatedArrival = new Property("ESTIMATED_ARRIVAL");
        estimatedArrival.addQualityDimension(new QualityDimension("ESTIMATED_ARRIVAL",Double.parseDouble(splitInfo[5])/Double.parseDouble(splitInfo[3])));
        
        properties.add(position);
        properties.add(speed);
        properties.add(laneID);
        properties.add(distanceBetweenCarAndLight);
        properties.add(estimatedArrival);
        //properties.add(currentPhase);
        
        AbstractObject smart = new AbstractObject("SMARTCAR_INFO", properties);
        //AbstractObject smart = new AbstractObject("SMARTCAR_INFO_" + splitInfo[0], properties);
        
        return smart;
    }
        
}
