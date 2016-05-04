import vrep.Quadricopter;
import vrep.Vrep;

public class droneMoveTest {
	
	public static void main(String[] args) throws InterruptedException
	{    	      
     
		 System.out.println("Program started");
		  
		 Vrep vrep = new Vrep();
		 Quadricopter quad1 = vrep.getQuadricopter("Quadricopter");
		 Quadricopter quad2 = vrep.getQuadricopter("Quadricopter#0");
	      
		 vrep.startSimulation();
		 //quad.printPosition();
		 //System.out.println(quad.getPosition());
		 Thread t1 = new Thread(quad1);
		 Thread t2 = new Thread(quad2);
		 //quad1.moveTo(0f, 1f, 0.5f, 1f);
		 //quad2.moveTo(-2f, -1f, 1f, 0.5f);
		 quad1.addCmd(0f, 1f, 0.5f, 1f);
		 quad2.addCmd(-2f, -1f, 1f, 0.5f);
		 t1.start();
		 t2.start();
		 
		 t1.join();
		 t2.join();
	     
	     vrep.disconnect();
         
	     System.out.println("Program ended");
	}
}
