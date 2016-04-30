package vrep;

import java.util.ArrayList;

import coppelia.FloatW;
import coppelia.FloatWA;
import coppelia.IntW;
import coppelia.Point3;
import coppelia.remoteApi;


public class Quadricopter
{
	//TODO: Add LoadModel method (here?)
	
	//private	FloatWA dronePosition;
	//private	FloatWA targetPosition;
	private	int droneHandle;
	private	int targetHandle;
	private	int clientID;
	private	remoteApi vrep;
	private CommandSender cmdSender;
	
	private Point3 position;
	private Point3 positionOfTarget;
	
	public static ArrayList<Integer> loadedQuadricopters;
	
	public Quadricopter(int cid, remoteApi api)
	{
		this(cid, api, "Quadricopter");
	}
	
	public Quadricopter(int cid, remoteApi api, String quadName)
	{
		this.clientID = cid;
		this.vrep = api;
		
		//TODO: Make Quadricopter search and loadedQuadricopters list in Vrep class rather than Quadricopter
		if (loadedQuadricopters == null)
			loadedQuadricopters = new ArrayList<>();
		
		String quadNum = "";
		if(quadName.contains("#"))
		{
			String[] s = quadName.split("#");
			quadName = s[0];
			quadNum = "#"+s[1];
		}
		
		IntW targetH = new IntW(0);
		vrep.simxGetObjectHandle(clientID, quadName+"_target"+quadNum, targetH, remoteApi.simx_opmode_oneshot_wait);
		targetHandle = targetH.getValue();
		vrep.simxGetObjectHandle(clientID, quadName+"_base"+quadNum, targetH, remoteApi.simx_opmode_oneshot_wait);
		droneHandle = targetH.getValue();
		
		cmdSender = new CommandSender(cid, api, targetHandle);
		
		if (!loadedQuadricopters.contains(droneHandle))
			loadedQuadricopters.add(droneHandle);
		
		//dronePosition = new FloatWA(3);
		//targetPosition = new FloatWA(3);
		//getObjectPosition(targetHandle, targetPosition);
		//getObjectPosition(droneHandle, dronePosition);
		
		position = getObjectPosition(droneHandle);
		positionOfTarget = getObjectPosition(targetHandle);
	}
	
	public Point3 getPosition()
	{
		//getObjectPosition(droneHandle, position);
		position = getObjectPosition(droneHandle);
		return position;
	}
	
	public Point3 getTargetPosition()
	{
		positionOfTarget = getObjectPosition(targetHandle);
		return position;
	}
	
	public remoteApi getAPI()
	{
		return vrep;
	}
	
	private void getObjectPosition(int handle, FloatWA out)
	{
        while(vrep.simxGetObjectPosition(clientID, handle, -1, out, remoteApi.simx_opmode_streaming) != remoteApi.simx_return_ok)
        {
       	 //waiting for data
        }
	}
	
	private Point3 getObjectPosition(int handle)
	{
		FloatWA pos = new FloatWA(3);
        while(vrep.simxGetObjectPosition(clientID, handle, -1, pos, remoteApi.simx_opmode_streaming) != remoteApi.simx_return_ok)
        {
       	 //waiting for data
        }
        
        return new Point3(pos.getArray());
	}
	
	public void moveTo(Point3 target, float velocity)
	{
		//FloatWA t = new FloatWA(new float[]{1f, 1f, 1f});
		//while(vrep.simxSetObjectPosition(clientID, targetHandle, -1, t, remoteApi.simx_opmode_oneshot_wait) != remoteApi.simx_return_ok){}
		float distance = position.distance(target);
		
		FloatW dt = new FloatW(0);
		vrep.simxGetFloatingParameter(clientID, remoteApi.sim_floatparam_simulation_time_step, dt, remoteApi.simx_opmode_oneshot_wait);
		float ds = velocity*dt.getValue();
		
		for(float i=0f; i<distance; i+=ds)
		{
			Point3 p = position.lerp(target, i);
			FloatWA next = new FloatWA(p.toFloatArray());
			cmdSender.path.add(next);
			//System.out.println(p);
		    //while(vrep.simxSetObjectPosition(clientID, targetHandle, -1, next, remoteApi.simx_opmode_oneshot_wait) != remoteApi.simx_return_ok){}
		}
		//cmdSender.run();
		Thread t = new Thread(cmdSender);
		t.start();
		
		//position = getObjectPosition(droneHandle);
		//positionOfTarget = getObjectPosition(targetHandle);
	}
	
	public void moveTo(Point3 target)
	{
		moveTo(target, 0.5f);
	}
	
	public void moveTo(float x, float y, float z)
	{
		moveTo(new Point3(x, y, z));
	}
	
	public void moveTo(float x, float y, float z, float velocity)
	{
		moveTo(new Point3(x, y, z), velocity);
	}
	
	public void wait(float ms)
	{
		FloatW dt = new FloatW(0);
		vrep.simxGetFloatingParameter(clientID, remoteApi.sim_floatparam_simulation_time_step, dt, remoteApi.simx_opmode_oneshot_wait);
		System.out.println(dt.getValue());
		for (float t=0; t<ms; t+=50)
		{
			moveTo(position);
		}
	}
}
