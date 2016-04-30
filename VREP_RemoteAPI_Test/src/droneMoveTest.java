import coppelia.FloatWA;

import coppelia.IntW;
import coppelia.IntWA;
import coppelia.Point3;
import coppelia.StringW;
import coppelia.CharWA;
import coppelia.remoteApi;
import vrep.Quadricopter;

public class droneMoveTest {
	
	public static void main(String[] args)
	{    	      
     
	      System.out.println("Program started");
	      remoteApi vrep = new remoteApi();
	      vrep.simxFinish(-1); // just in case, close all opened connections
	      int clientID = vrep.simxStart("127.0.0.1",19997,true,true,5000,5); //simplify
	      if (clientID!=-1)
	      {

	         System.out.println("Connected to remote API server");
	         Quadricopter quad = new Quadricopter(clientID, vrep);
	         Quadricopter quad2 = new Quadricopter(clientID, vrep, "Quadricopter#0");
	         vrep.simxStartSimulation(clientID, remoteApi.simx_opmode_oneshot);
	         //quad.printPosition();
	         //System.out.println(quad.getPosition());
	         quad.moveTo(new Point3(0f, 1f, 0.5f), 1f);
	         quad2.moveTo(-2f, -1f, 1f, 0.5f);
	         //System.out.println(quad.getPosition());
	         //quad.wait(5000f);
	         //quad.moveTo(0.5f, 0.5f, 2f);
	         //System.out.println(quad.getPosition());
	         //quad.moveTo(0.5f, 2f, 2f);
	         //vrep.simxStopSimulation(clientID, remoteApi.simx_opmode_oneshot);
	      }   
	      else
	         System.out.println("Failed connecting to remote API server");
	      System.out.println("Program ended");
	}
}
