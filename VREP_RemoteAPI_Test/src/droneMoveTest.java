import java.util.LinkedList;
import java.util.List;

import helper.Point3;
import vrep.Quadricopter;
import vrep.Vrep;

public class droneMoveTest {
	
	public static void main(String[] args) throws InterruptedException
	{    	      
     
		 System.out.println("Program started");
		  
		 Vrep vrep = new Vrep();
		 Quadricopter quad1 = vrep.getQuadricopter("Quadricopter");
		 //Quadricopter quad2 = vrep.getQuadricopter("Quadricopter#0");
	     
		 vrep.startSimulation();
		 //quad.printPosition();
		 //System.out.println(quad.getPosition());
		 Thread t1 = new Thread(quad1);
		 //Thread t2 = new Thread(quad2);
		 //quad1.moveTo(0f, 1f, 0.5f, Quadricopter.quad_path_linear);
		 //quad1.moveTo(0f, 1f, 2f, Quadricopter.quad_path_linear);
		 //quad1.moveTo(1f,  2f, 3f, Quadricopter.quad_path_cosine);
		 //quad1.moveTo(2f,  3f, 2f, Quadricopter.quad_path_cosine);
		 //quad1.moveTo(new Point3(0, 0, 1));
		 List<Point3> path = new LinkedList<>();
		 path.add(new Point3(0, 0, 1));
		 path.add(new Point3(1f, 2f, 1f));
		 path.add(new Point3(2f, 1f, 1f));
		 path.add(new Point3(3f, -1f, 1f));
		 path.add(new Point3(4f, 0f, 1f));
		 path.add(new Point3(5f, 2f, 1f));
		 path.add(new Point3(0f, -1f, 1f));
		 path.add(new Point3(2f, -2f, 3f));
		 //path.add(new Point3(0f, 0f, 1f));
		 
		 List<Point3> bezier = new LinkedList<>();
		 bezier.add(new Point3(0.5f, 1f, 1f));
		 bezier.add(new Point3(1f, 2f, 1f));
		 bezier.add(new Point3(1.5f, 1f, 1f));
		 //quad1.moveTo(new Point3(1,0,1));
		 //quad1.moveTo(new Point3(2,0,1), Quadricopter.quad_path_bezier, bezier);
		 //Point3 a = new Point3(0,0,1);
		 //Point3 b = new Point3(1,1,1);
		 //Point3 c = new Point3(-1,-1,1);
		 //Point3 d = a.cross(b, c);
		 //System.out.println(d);
		 quad1.addPath(path, Quadricopter.quad_path_hermite, 0.5f, 0.2f);
		 
		 //quad2.moveTo(-2f, -1f, 1f, 0.5f);
		 t1.start();
		 //t2.start();
		 
		 t1.join();
		 //t2.join();
	     
	     vrep.disconnect();
         
	     System.out.println("Program ended");
	}
}
