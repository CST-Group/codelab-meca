/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.MECA_Demo.codelets.system2;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;
import br.unicamp.cst.representation.owrl.AbstractObject;
import br.unicamp.cst.representation.owrl.Property;
import br.unicamp.cst.representation.owrl.QualityDimension;
import br.unicamp.jtraci.entities.Lane;
import br.unicamp.jtraci.entities.Link;
import br.unicamp.jtraci.entities.Phase;
import br.unicamp.jtraci.entities.TrafficLight;
import br.unicamp.jtraci.simulation.SumoSimulation;

import java.util.ArrayList;
import java.util.List;
//import br.unicamp.meca.system1.codelets.AttentionCodelet;

/**
 *
 * @author Wandemberg Gibaut
 */
public class SmartCarAttentionCodelet extends br.unicamp.meca.system1.codelets.AttentionCodelet{
    
    private static final Double MIN_PHASE_TIME = 2.000d;
    private static final Double MAX_PHASE_TIME = 12.0000d;
    
    private TrafficLight trafficLight;
    private ArrayList<Phase> phases;
    
   
    private int initialTime = SumoSimulation.getInstance().getCurrentStep();
    private boolean startFlag = false;
    private boolean endFlag = false;
    
    public SmartCarAttentionCodelet(String id, ArrayList<String> perceptualCodeletsIds, TrafficLight trafficLight, ArrayList<Phase> phases) {
		super(id, perceptualCodeletsIds); 
                this.trafficLight = trafficLight;
                this.phases = phases;
	}
    
    @Override
    public void accessMemoryObjects(){

        if(getInputPerceptsMO() == null){
            setInputPercepts(new ArrayList<AbstractObject>());
            for (Memory perceptualMO : this.getInputs()) {
                if(perceptualMO.getI()!= null){
                    if(perceptualMO.getI().getClass().getName().equals("java.lang.String")){
                    //phaseTime vem como string n como AbstractObject
                        String info = (String) perceptualMO.getI();             
                    //passo essa info como o nome de um AO soh pra facilitar
                        getInputPercepts().add(new AbstractObject(info.split("[;]")[1]) );
                    }
                    else{
                        //getInputPercepts().add((AbstractObject) perceptualMO.getI());
                        //new. 
                        getInputPercepts().addAll((List<AbstractObject>) perceptualMO.getI());
                    }
                } 
            }
        }

        if(getOutputFilteredPerceptsMO() == null){
            setOutputFilteredPerceptsMO(getOutput(getId()));
        }

    }
    
    @Override
    public void proc() {
        if(getInputPercepts().size() == 2){
            setOutputFilteredPercepts(generateFilteredPercepts(getInputPercepts()));
            getOutputFilteredPerceptsMO().setI(getOutputFilteredPercepts());

            startFlag = true;
        }
        else {
            AbstractObject configuration = new AbstractObject("CONFIGURATION");
            configuration.addProperty(new Property("NO_SMARTCAR", new QualityDimension("NO_SMARTCAR","TRUE")));
            getOutputFilteredPerceptsMO().setI(configuration);

            if(startFlag && !endFlag){
                int time = (SumoSimulation.getInstance().getCurrentStep()-initialTime);
                System.out.println("Total time took by the SmartCar: " + time + " steps");
                endFlag = true;
            }
        }
    }
    
    
    @Override
    public AbstractObject generateFilteredPercepts(List<AbstractObject> inputPercepts){
        
        //novo. pra ser compativel com a nova implementacao. Pela logica o input com o tempo eh o ultimo da lista
        String elapsedTime = inputPercepts.get(inputPercepts.size()-1).getName();
        //gerar as infos da traffic_light
        // no traffic light as "fases" sao indicies do conjunto de fases
        //usa o primeiro smart da lista pq a info de fase n muda de um smart pra outro
        AbstractObject trafficPercept = createTrafficLightStruct(elapsedTime, inputPercepts.get(0));
        
        //padrao em caixa alta pra passar pro SOAR e diferenciar estruturas passadas via input-link daquelas preexistentes (ex: state)
        AbstractObject configuration = new AbstractObject("CONFIGURATION");
        configuration.addCompositePart(trafficPercept);
        
        //todos os smartPercepts se chamam SMARTCAR_INFO. A estrutura do OWRL permite isso (diferente do JSON, por exemplo). Isso tera q ser levando em conta no Script em SOAR para evitar "impasse"
        for(int i = 0; i < inputPercepts.size()-1; i++){
            AbstractObject smartPercept = inputPercepts.get(i);
            configuration.addCompositePart(smartPercept);
        }
        
       //presume-se ate então um unico smart car. Na existencia de multiplos, o elemento unico do inputPercepts deverá ser uma lista com 
       //infos de CADA smart. Dai então o nó central "configuration" será essencial (poderia ser dispensado para 1 unico smart).
       
        return configuration;
    } 
    
    public AbstractObject createTrafficLightStruct(String time, AbstractObject smartcar){
        List<Property> properties = new ArrayList();
        
        
        Property currentPhase = new Property("CURRENT_PHASE");
        currentPhase.addQualityDimension(new QualityDimension("PHASE", currentStateName(trafficLight.getState(),getSmartCarLaneIndex(smartcar,getControlledLanesIDs()))));
        currentPhase.addQualityDimension(new QualityDimension("ELAPSED_TIME", time));
        
        //to match the new implementation. Only needs to add the elapsed time.
        //think that may cause future problems
        //getPropertyStruct(smartcar, "CURRENT_PHASE").addQualityDimension(new QualityDimension("ELAPSED_TIME", time));
        
        Property bounds = new Property("BOUNDS");
        bounds.addQualityDimension(new QualityDimension("MAX_TIME", MAX_PHASE_TIME));
        bounds.addQualityDimension(new QualityDimension("MIN_TIME", MIN_PHASE_TIME));
        
        
        properties.add(currentPhase);
        properties.add(bounds);
        
        AbstractObject traffic = new AbstractObject("TRAFFIC_LIGHT", properties);
        return traffic;
    }
    
    public String currentStateName(String state, int smartcarIndex){
        String light = "";
        if(state.charAt(smartcarIndex) == 'r' || state.charAt(smartcarIndex) == 'R' || state.charAt(smartcarIndex) == 'y' || state.charAt(smartcarIndex) == 'Y'){
            light = "RED";
        }
        else if(state.charAt(smartcarIndex) == 'g' || state.charAt(smartcarIndex) == 'G'){
            light = "GREEN";
        }
        return light;
    }
    
    public int getSmartCarLaneIndex(AbstractObject smartcar,ArrayList<String> controlledLanesIds){
        String smartLaneID = (String)getPropertyStruct(smartcar, "LANE_ID").getQualityDimensions().get(0).getValue();
        return controlledLanesIds.indexOf(smartLaneID);
    }
    
    
    public Property getPropertyStruct(final AbstractObject obj, final String name){
        return obj.getProperties().stream().filter(o -> o.getName().equals(name)).findFirst().get();
    }
    
    public ArrayList<String> getControlledLanesIDs(){
        /*
	 * Controlled Lanes
	 */							
	//ArrayList<Lane> controlledIncomingLanes = new ArrayList<Lane>();
	ArrayList<String> controlledIncomingLanesIDs = new ArrayList<String>();
	//ArrayList<Lane> controlledOutgoingLanes = new ArrayList<Lane>();
	//ArrayList<String> controlledOutgoingLanesIDs = new ArrayList<String>();
	Link[][] controlledLinks = trafficLight.getControlledLinks().getLinks();
	for(int i=0;i<controlledLinks.length;i++){											
            for(int j=0;j<controlledLinks[i].length;j++){
		Link controlledLink = controlledLinks[i][j];
		
                Lane incomingLane = controlledLink.getIncomingLane();	
		//controlledIncomingLanes.add(incomingLane);	
		controlledIncomingLanesIDs.add(incomingLane.getID());
		//Lane outgoingLane = controlledLink.getOutgoingLane();
		//controlledOutgoingLanes.add(outgoingLane);		
		//controlledOutgoingLanesIDs.add(outgoingLane.getID());											
            }						
	}
        return controlledIncomingLanesIDs;
    }
    
     
    @Override
	public void calculateActivation() {
		
		try{

			setActivation(0.0d);

		} catch (CodeletActivationBoundsException e) {

			e.printStackTrace();
		}

	}
}