package vrep;
import coppelia.remoteApi;

public class Vrep {

	public boolean scene;
	public int clientID;
	public remoteApi api;
	
	public Vrep()
	{
	      api = new remoteApi();
	      api.simxFinish(-1); // just in case, close all opened connections
	      clientID = api.simxStart("127.0.0.1",19997,true,true,5000,5);
	      //loadScene();
	      scene = false; //Scene not loaded
	      //api.simxStartSimulation(clientID, remoteApi.simx_opmode_oneshot);
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
}
