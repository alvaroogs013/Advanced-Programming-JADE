package es.usal.pa;

import java.util.Scanner;

import weka.classifiers.trees.J48;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class Main
{
	public static void main(String args[]) throws Exception
	{
		
		 DataSource source = new DataSource("famosos.csv");
		 Instances dataEntrenamiento = source.getDataSet();
		 
		 
		 //indicar el atributo con la categoría a clasificar
		 if (dataEntrenamiento.classIndex() == -1)
			 dataEntrenamiento.setClassIndex(0);

		 
		 J48 j48 = new J48();
		 j48.setOptions(new String[] {"-C", "0.25", "-M", "1"});
		 j48.setUnpruned(true);
		 j48.buildClassifier(dataEntrenamiento);
		 
		 //procecidimiento habitual para la clasificación de instancias
		 double clasePredicha=j48.classifyInstance(dataEntrenamiento.lastInstance());
		 System.out.println("Clase "+dataEntrenamiento.classAttribute().value((int)clasePredicha));
		 
		 
		 //para el ejemplo vamos a hacer algo diferente al procedimiento habitual de clasificar
		 Scanner scanner=new Scanner(System.in);
		 Pregunta pregunta=new Pregunta(j48);
		 
		 while(!pregunta.esNodoFinal())
		 { 
			 String temp=pregunta.obtenerPreguntaNodo();
			 System.out.println(temp);
			 String respuesta=scanner.nextLine();
			 pregunta.navegarNodoRespuesta(respuesta);
		 }
		 
		 System.out.println("Es "+pregunta.obtenerPreguntaNodo());
	}
	
}
