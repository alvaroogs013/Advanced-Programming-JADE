package es.usal.pa;
import java.io.IOException;
import java.io.Serializable;

import java.util.List;
import java.util.Scanner;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import jade.core.Agent;

public class AgentCliente extends Agent {
	public void setup() {
		
		

		DFAgentDescription dfd=new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd= new ServiceDescription();
		
		sd.setName("Agente"+getAID());
		sd.setType("responder");
		sd.addOntologies("ontologia");
		sd.addLanguages(new SLCodec().getName());

		dfd.addServices(sd);
		
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Se pordujo un error al registrar el servicio"
								+ "en el agente " +getAID());
		}
		
		
		
		//MANDAMOS EL MENSAJE QUE AVISA DE QUE SE HA CREADO ESTE AGENTE JUNTO CON EL AID
		//ESTE ES EL MENSAJE QUE DESBLOQUEARÁ UN COMPORTAMIENTO CÍCLICO DE LOS QUE ESTÁN
		//ESPERANDO EN EL IF DE INICIALIZACIÓN
		//COMO SE VE, HACEMOS MSG.SETCONVERSTATIONID Y SE LE METE EL AID DE ESTE AGENTE
		//QUE SERÁ EL VALOR QUE RECOGERÁ DESPUES EL COMPORTAMIENTO CÍCLICO Y QUE METE
		//EN LA VARIABLE CONVID
		
		DFAgentDescription[] dfd2;
		dfd2=Utils.buscarAgentes(this, "qeq");
			
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		
		for(int i=0;i<dfd2.length;i++)
			msg.addReceiver(dfd2[i].getName());
		msg.setOntology("ontologia");
		msg.setLanguage(new SLCodec().getName());
		msg.setEnvelope(new Envelope());
		msg.getEnvelope().setPayloadEncoding("ISO8859_1");
		Object objeto="patata";
		msg.setConversationId(""+getAID());
		try {
			msg.setContentObject((Serializable)objeto);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			send(msg);	
		
		System.out.println("------------------ JUGADOR: "+getLocalName()+" ------------------");
		//CREAMOS UN COMPORTAMIENTO CÍCLICO QUE SE ENCARGARÁ DE RESPONDER Y RECIBIR LAS PREGUNTAS DEL COMPORTAMIENTO CÍCLICO
		addBehaviour(new CyclicBehaviour(this){
		private static final long serialVersionUID = 1L;	
		public void action()
	       {
			
			
			//Estoy probando si espera que le llegue un mensaje con ese AID que es el suyo
			//HASTA AQUI HABRÍA AVISADO DE QUE SE HA CREADO Y QUIEN ES QUIEN CREA EL CICLICO Y ESPERA QUE EL CICLICO ENVIE MENSAJE DE PREGUNTA
			MessageTemplate mt=MessageTemplate.MatchConversationId(""+getAID());
			ACLMessage msg=new ACLMessage(ACLMessage.CONFIRM);
		    msg=blockingReceive(mt);	
			
				
			
			//MOSTRAMOS LA PREGUNTA DEL MENSAJE
			String pregunta = null;
			
			//OBTENEMOS LA PREGUNTA DEL MENSAJE
			try {
				pregunta = (String)msg.getContentObject();
			} catch (UnreadableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	System.out.println(pregunta);
	    	
	    	
	    	//COMPROBAMOS SI LA PREGUNTA QUE VIENE EMPIEZA POR ES PARA VER SI ES LA SOLUCIÓN
	    	//PARA ESTO EN UNA VARIABLE DE CONTROL INTRODUCIMOS LOS DOS PRIMEROS CARACTERES DE LA PREGUNTA
	    	char[] control = new char[2];
	    	pregunta.getChars(0,2,control,0);

	    	if(control[0]=='E' && control[1]=='S') {
	    		Scanner scan=new Scanner(System.in);
	    		String respuesta=scan.nextLine();
	    		doDelete();
	    	}
	    	else {
			//Permitimos la respuesta a la pregunta
			Scanner scan=new Scanner(System.in);
			String respuesta=scan.nextLine();
			
			
			DFAgentDescription[] dfd;
			dfd=Utils.buscarAgentes(this.myAgent, "qeq");
			
			//RESPONDEMOS AL COMPORTAMIENTO CORRESPONDIENTE
			//PARA ESTO USAMOS CREATEREPLY PARA QUE NO HAYA PROBLEMAS 
			ACLMessage rp=new ACLMessage(ACLMessage.CONFIRM);
			Object objeto;
			objeto=respuesta;
			rp=msg.createReply();
			msg.setConversationId(""+getAID());
			try {
				rp.setContentObject((Serializable)objeto);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				
				send(rp);	
	    	}//Fin del else
	        }//fin action
			
		});
		
		
	        }
		protected void takeDown() {
	    	System.out.println("Gracias por jugar "+getLocalName());
	    	}
}
