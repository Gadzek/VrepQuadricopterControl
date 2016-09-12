package vrep;

import java.util.LinkedList;
import java.util.List;

import coppelia.FloatWA;
import coppelia.IntW;
import coppelia.remoteApi;


public class Quadricopter
{
	//TODO: Add LoadModel method (here?)
	
	//private	FloatWA dronePosition;
	//private	FloatWA targetPosition;
	public Vrep parentApp;
	private	int droneHandle;
	private	int targetHandle;
	private	int clientID;
	//private float velocity;
	private	remoteApi vrepApi;
	
	private Point3 position;
	private Point3 positionOfTarget;
	
	//private Thread commandThread;
	//private LinkedList<FloatWA> path;
	private DroneCommands commands;
	private SignalListener signalListener;
	private GlobalSignalListener globalSignalListener;
	
	public final static int quad_path_linear = 1;
	public final static int quad_path_cosine = 2;
	public final static int quad_path_cubic = 3;
	public final static int quad_path_hermite = 4;
	public final static int quad_path_bezier = 5;
	
	protected Quadricopter(int clientID, remoteApi api)
	{
		this(clientID, api, "Quadricopter");
	}
	
	protected Quadricopter(int clientID, remoteApi api, String quadName)
	{
		this.clientID = clientID;
		this.vrepApi = api;
		
		String quadNum = "";
		if(quadName.contains("#"))
		{
			String[] s = quadName.split("#");
			quadName = s[0];
			quadNum = "#"+s[1];
		}
		
		IntW targetH = new IntW(0);
		int r = vrepApi.simxGetObjectHandle(clientID, quadName+"_target"+quadNum, targetH, remoteApi.simx_opmode_oneshot_wait);
		if (r != remoteApi.simx_return_ok)
			VrepErrorHandler.handleFunctionReturnValue(Thread.currentThread().getStackTrace()[1].toString(), r);
		targetHandle = targetH.getValue();

		
		r = vrepApi.simxGetObjectHandle(clientID, quadName+"_base"+quadNum, targetH, remoteApi.simx_opmode_oneshot_wait);
		if (r != remoteApi.simx_return_ok)
			VrepErrorHandler.handleFunctionReturnValue(Thread.currentThread().getStackTrace()[1].toString(), r);
		droneHandle = targetH.getValue();
		
		//dronePosition = new FloatWA(3);
		//targetPosition = new FloatWA(3);
		//getObjectPosition(targetHandle, targetPosition);
		//getObjectPosition(droneHandle, dronePosition);
		
		//velocity = 0.2f;
		position = getObjectPosition(droneHandle);
		System.out.println("Pos: " + position);
		positionOfTarget = getObjectPosition(targetHandle);
		System.out.println("Target: " + positionOfTarget);
		
		commands = new DroneCommands(this);
		//path = new LinkedList<>();
		//commandThread = new Thread(commands);
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
		return positionOfTarget;
	}
	
	protected void setTargetPosition(FloatWA target)
	{
		int r;
		int timeout = 0;
		while((r = vrepApi.simxSetObjectPosition(clientID, targetHandle, -1, target, remoteApi.simx_opmode_oneshot_wait)) != remoteApi.simx_return_ok && timeout < 500)
		{
			timeout++;
		}
		/*for (int i=0; 
				r != remoteApi.simx_return_ok && i<100; 
				i++){
			if(!getGlobalSignalListener().simRunning || getGlobalSignalListener().pause)
				break;
			r =vrepApi.simxSetObjectPosition(clientID, targetHandle, -1, target, remoteApi.simx_opmode_oneshot_wait);
		}*/
		if (r != remoteApi.simx_return_ok)
			VrepErrorHandler.handleFunctionReturnValue(Thread.currentThread().getStackTrace()[1].toString(), r);
	}
	
	protected void setSignalListener(SignalListener listener)
	{
		this.signalListener = listener;
	}
	
	
	protected SignalListener getSignalListener()
	{
		return this.signalListener;
	}
	
	protected void setGlobalSignalListener(GlobalSignalListener listener)
	{
		this.globalSignalListener = listener;
	}
	
	protected GlobalSignalListener getGlobalSignalListener()
	{
		return this.globalSignalListener;
	}
	
	public remoteApi getAPI()
	{
		return vrepApi;
	}
	
	public int getDroneHandle()
	{
		return droneHandle;
	}
	
	private Point3 getObjectPosition(int handle)
	{
		FloatWA pos = new FloatWA(3);
		int r = -1;
		int timeout = 0;
        while((r = vrepApi.simxGetObjectPosition(clientID, handle, -1, pos, remoteApi.simx_opmode_streaming)) != remoteApi.simx_return_ok)
        {
       	 //waiting for data
        	timeout++;
        }
		
		/*
		int r = vrepApi.simxGetObjectPosition(clientID, handle, -1, pos, remoteApi.simx_opmode_streaming);
		for (int i=0; 
				r != remoteApi.simx_return_ok && i<100; 
				i++){
			r = vrepApi.simxGetObjectPosition(clientID, handle, -1, pos, remoteApi.simx_opmode_streaming);
			//System.out.println(i + ": " + r);
		}*/
        
		//System.out.println(r);
		if (r != remoteApi.simx_return_novalue_flag && r != remoteApi.simx_return_ok)
			VrepErrorHandler.handleFunctionReturnValue(Thread.currentThread().getStackTrace()[1].toString(), r);
		
        return new Point3(pos.getArray());
	}
	
	/*
	protected void setDroneThread()
	{
		commandThread = new Thread(commands);
	}
	
	public Thread getDroneThread()
	{
		return commandThread;
	}
	*/
	
	public DroneCommands getDroneCommands()
	{
		return commands;
	}
	
	public void moveTo(Point3 target, MoveToPointAlgorithm a)
	{
		commands.moveTo(target, a);
	}
	
	/*
	public void moveTo(float x, float y, float z)
	{
		moveTo(new Point3(x,y,z));
	}
	
	public void moveTo(float x, float y, float z, int pattern)
	{
		moveTo(new Point3(x,y,z), pattern);
	}
	
	public void moveTo(float x, float y, float z, int pattern, List<Point3> bezierPoints)
	{
		moveTo(new Point3(x,y,z), pattern, bezierPoints);
	}
	
	public void moveTo(Point3 target)
	{
		commands.moveTo(target, quad_path_linear);
	}
	
	public void moveTo(Point3 target, int pattern)
	{
		commands.moveTo(target, pattern, new LinkedList<Point3>());
	}
	
	public void moveTo(Point3 target, int pattern, List<Point3> bezierPoints)
	{
		commands.moveTo(target, pattern, bezierPoints);
	}
	
	public void addPath(List<Point3> knots)
	{
		addPath(knots, quad_path_linear);
	}
	
	public void addPath(List<Point3> knots, int algorithm)
	{
		addPath(knots, algorithm, 0f, 0f);
	}
	
	public void addPath(List<Point3> knots, int algorithm, float hermiteBias, float hermiteTension)
	{
		commands.addPath(knots, algorithm, hermiteBias, hermiteTension);
	}
	*/
	
	public void moveThrough(List<Point3> knots, MoveThorughPointsAlgorithm a)
	{
		commands.moveThrough(knots, a);
	}
}
