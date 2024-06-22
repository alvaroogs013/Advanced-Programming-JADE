package es.usal.pa;

import java.io.FileNotFoundException;


import java.io.IOException;
import java.io.Serializable;
import jade.content.lang.sl.SLCodec;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;


public class CyclicBehaviourPreguntas extends CyclicBehaviour {

	public String convID;
	public int i=0;
	private int inicializado=0;
	private Pregunta pregunta=null;
	ACLMessage msg;
	
	//Contructor con agente
	public CyclicBehaviourPreguntas(AgentQuienEsQuien agentQuienEsQuien) {
		super(agentQuienEsQuien);
		
	}
	@Override
	public void action() {
		// TODO Auto-generated method stub
		//ESTA PARTE DE INICIALIZADO SOLO SE EJECUTARÁ CUANDO SE CREE EL COMPORTAMIENTO CÍCLICO
				//TODOS LOS COMPORTAMIENTOS CREADOS SE QUEDARÁN ESPERANDO EN EL ACLMESSAGE A QUE UN CLIENTE
				//LOS DESBLOQUEE, SOLO UNO DE ESTOS COMPORTAMIENTOS RECIBIRÁ UN MENSAJE DE UN CLIENTE POR LO
				//TANTO SE IRÁN DESBLOQUEANDO UNO A UNO. 
				//ALMACENAMOS EN CONVID EL ID DEL AGENTE CLIENTE CORRESPONDIENTE MEDIANTE MSG.GETCONVERSATIONID
				//DE ESTAFORMA EN CADA ENVIO Y RECEPCIÓN ESTABLECEMOS CUAL ES EL CONVERSATION ID QUE TIENE QUE TENER
				//EL MENSAJE QUE SE ENVÍA O EL QUE TIENE QUE RECIBIRSE
				//EL RESTO DE COSAS QUE HAY EN ESTE IF NO ES IMPORTANTE, ES UN COPIA Y PEGA DEL MAIN QUE NOS DIERON
				if(inicializado==0) {
					
					
				msg=this.myAgent.blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
				convID=msg.getConversationId();
				System.out.println("SeDesbloquea");
				
				
				//AQUI EMPIEZA LA PARTE DE INICIALIZAR LA PARTE DE ABRIR EL FICHERO DEL QUE SE VA A LEER
				DataSource source = null;
				try {
					source = new DataSource("famosos.csv");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				 Instances dataEntrenamiento = null;
				try {
					dataEntrenamiento = source.getDataSet();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
				 
				 //indicar el atributo con la categoría a clasificar
				 if (dataEntrenamiento.classIndex() == -1)
					 dataEntrenamiento.setClassIndex(0);

				 
				 J48 j48 = new J48();
				 try {
					j48.setOptions(new String[] {"-C", "0.25", "-M", "1"});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 j48.setUnpruned(true);
				 try {
					j48.buildClassifier(dataEntrenamiento);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
				 //procecidimiento habitual para la clasificación de instancias
				 double clasePredicha = 0;
				try {
					clasePredicha = j48.classifyInstance(dataEntrenamiento.lastInstance());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 System.out.println("Clase "+dataEntrenamiento.classAttribute().value((int)clasePredicha));
				 
				 
				 //para el ejemplo vamos a hacer algo diferente al procedimiento habitual de clasificar
				 try {
				 pregunta=new Pregunta(j48);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//AQUI ACABA LA PARTE DE INICIALIZAR EL FICHERO DEL QUE SE LEERÁ
				
				inicializado=1;
				}
				
				
				i++;
				String temp=null;
				
				//AHORA, UNA VEZ DESBLOQUEADO EL COMPORTAMIENTO VAMOS A OBTENER UNA PREGUNTA DEL FICHERO Y VAMOS A ENVIARLA
				//AL AGENTE CLIENTE POR MENSAJE. USAMOS UN ACLMESSAGE.CONFIRM PARA QUE NO HAYA PROBLEMAS CON LOS COMPORTAMIENTOS
				//QUE ESTAN ESPERANDO Y QUE ESPERAN UN MENSAJE ACLMESSAGE.REQUEST
				//Si todavia no es la ultima pregunta
				 if(!pregunta.esNodoFinal())
				 { 
					  temp=pregunta.obtenerPreguntaNodo();
				
				//Vamos a enviarle un mensaje al agentecliente
				//PARA ESTO USAMOS CREATEREPLY PARA QUE NO HAYA PROBLEMAS 
			    //IMPORTANTE ESTABLECERLE A rp LA PERFORMATIVA CONFIRM
				ACLMessage rp=new ACLMessage(ACLMessage.CONFIRM);
				rp=msg.createReply();
				rp.setPerformative(ACLMessage.CONFIRM);
				Object objeto="PREGUNTA: "+temp;
				try {
					rp.setContentObject((Serializable)objeto);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				this.myAgent.send(rp);	
				
				
				//HASTA AQUI LE HABRÁ HECHO LA PREGUNTA Y SE QUEDA ESPERANDO A QUE AGENTE CLIENTE LA RESPONDA.
				//CON MATCHCONVERSATIONID INFORMAMOS QUE EL EMNSAJE QUE TIENE QUE DESBLOQUEARLO TIENE QUE TENER
				//EL ID DE CONVERSACIÓN CONVID QUE ES EL ID DEL CLIENTE CON EL QUE SE TIENE QUE COMUNICAR ESTE COMPORTAMIENTO
				msg=new ACLMessage(ACLMessage.CONFIRM);
				MessageTemplate mt=MessageTemplate.MatchConversationId(convID);
				msg=this.myAgent.blockingReceive(mt);
				
				//OBTENEMOS EL VALOR DELA RESPUESTA
				String respuesta = new String();
				
				//OBTENEMOS LA PREGUNTA DEL MENSAJE
				try {
					respuesta = (String)msg.getContentObject();
				} catch (UnreadableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	pregunta.navegarNodoRespuesta(respuesta);
		    	
				 }
				 
				 //EL COMPORTAMIENTO DEL ELSE ES IGUAL QUE EL DEL IF, PERO PARA LA SITUACUÓN EN QUE LA PREGUNTA QUE VA A HACER
				 //ES LA PREGUNTA DE SOLUCIÓN.
				 else {
					 temp=pregunta.obtenerPreguntaNodo();
						
						//Vamos a enviarle un mensaje al agentecliente
					 	//PARA ESTO USAMOS CREATEREPLY PARA QUE NO HAYA PROBLEMAS 
					    //IMPORTANTE ESTABLECERLE A rp LA PERFORMATIVA CONFIRM
						 ACLMessage rp = new ACLMessage(ACLMessage.CONFIRM);
						 rp=msg.createReply();	
						 rp.setPerformative(ACLMessage.CONFIRM);
						Object objeto="ES: "+temp;
						msg.setConversationId(convID);
						try {
							rp.setContentObject((Serializable)objeto);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						this.myAgent.send(rp);	
						
						//Ponemos el flag de inicializado a 0 para que vuelva a quedarse esperando a que un nuevo jugador se conecte
						//por lo tanto cuando el comportamiento cíclico vuelva a repetirse, volverá a entrar en el if de inicialización
						inicializado=0;
					 }	

					 }

	}

