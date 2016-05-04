package vrep;
import coppelia.remoteApi;

public class Vrep {

	public boolean scene;
	public int clientID;
	public remoteApi api;
	
	public Vrep()
	{
		this("127.0.0.1");
	}
	
	public Vrep(String IP)
	{
	      api = new remoteApi();
	      api.simxFinish(-1); // just in case, close all opened connections
	      clientID = api.simxStart(IP,19997,true,true,5000,5);
	      //loadScene();
	      scene = false; //Scene not loaded
	      //api.simxStartSimulation(clientID, remoteApi.simx_opmode_oneshot);
	      
	      if(clientID==-1)
	      {
	    	  System.out.println("Failed connecting to remote API server");
	      }
	      else
	      {
	    	  System.out.println("Connected to remote API server");
	      }
	}
	
	public int loadScene()
	{
		//int out = api.simxLoadScene(clientID, "H:\\Praca magisterska\\V-REP\\scenes\\QuadTrial.ttt", 1, remoteApi.simx_opmode_oneshot_wait);
		//int out;
		while (api.simxLoadScene(clientID, "H:\\Praca magisterska\\V-REP\\scenes\\QuadTrial.ttt", 1, remoteApi.simx_opmode_oneshot_wait) != remoteApi.simx_return_ok)
		{
			
		}
		//System.out.println(out);
		scene = true;
		return 0;
	}
	
	public Quadricopter getQuadricopter(String name)
	{
		return new Quadricopter(clientID, api, name);
	}
	
	public int startSimulation()
	{
		return api.simxStartSimulation(clientID, remoteApi.simx_opmode_oneshot);
	}
	
	public void disconnect()
	{
		api.simxFinish(clientID);
		System.out.println("Disconnected from remote API server");
	}
	
	/*
	protected void finalize() throws Throwable
	{
		super.finalize();
		api.simxFinish(clientID);
		System.out.println("Disconnected from remote API server");
	}
	*/
}
