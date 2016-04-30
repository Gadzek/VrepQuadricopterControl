package vrep;

import java.util.LinkedList;
import coppelia.FloatWA;
import coppelia.remoteApi;

public class CommandSender implements Runnable {

	public LinkedList<FloatWA> path;
	
	private int clientID;
	private int objectHandle;
	private remoteApi vrep;
	
	public CommandSender(int clientID, remoteApi api, int objectHandle)
	{
		this.clientID = clientID;
		this.vrep = api;
		this.objectHandle = objectHandle;
		this.path = new LinkedList<>();
	}
	
	@Override
	public void run() {
		
		while (!path.isEmpty()){
			while(vrep.simxSetObjectPosition(clientID, objectHandle, -1, path.removeFirst(), remoteApi.simx_opmode_oneshot_wait) != remoteApi.simx_return_ok){}
		}
		//notifyAll();
	}

}
