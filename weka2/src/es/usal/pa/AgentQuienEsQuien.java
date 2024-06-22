package es.usal.pa;

import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgentQuienEsQuien extends Agent {
	public void setup(){
		
		DFAgentDescription dfd=new DFAgentDescription();
		dfd.setName(getAID());
		
		ServiceDescription sd = new ServiceDescription();
		sd.setName("QEQ");
		sd.setType("qeq");
		sd.addOntologies("ontologia");
		sd.addLanguages(new SLCodec().getName());
		
		dfd.addServices(sd);
		//Registramos el servicio
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		
		System.out.println("--------BIENVENIDO A QUIEN ES QUIEN--------");
		ThreadedBehaviourFactory creadorDeHilos;
		//Creamos una instancia del creador de hilos
		creadorDeHilos=new ThreadedBehaviourFactory();
		
		//CREAMOS EL COMPOTAMIENTO PARALELO Y LE AÑADIMOS 5 HILOS DE COMPORTAMIENTO CÍCLICO
		ParallelBehaviour pb=new ParallelBehaviour(ParallelBehaviour.WHEN_ALL);
		Behaviour ciclicPregunta;
		ciclicPregunta=new CyclicBehaviourPreguntas(this);
		pb.addSubBehaviour(creadorDeHilos.wrap(ciclicPregunta));
		
		ciclicPregunta=new CyclicBehaviourPreguntas(this);
		creadorDeHilos=new ThreadedBehaviourFactory();
		pb.addSubBehaviour(creadorDeHilos.wrap(ciclicPregunta));
		
		ciclicPregunta=new CyclicBehaviourPreguntas(this);
		creadorDeHilos=new ThreadedBehaviourFactory();
		pb.addSubBehaviour(creadorDeHilos.wrap(ciclicPregunta));
		
		ciclicPregunta=new CyclicBehaviourPreguntas(this);
		creadorDeHilos=new ThreadedBehaviourFactory();
		pb.addSubBehaviour(creadorDeHilos.wrap(ciclicPregunta));
		
		ciclicPregunta=new CyclicBehaviourPreguntas(this);
		creadorDeHilos=new ThreadedBehaviourFactory();
		pb.addSubBehaviour(creadorDeHilos.wrap(ciclicPregunta));
		addBehaviour(pb);
	}
}
