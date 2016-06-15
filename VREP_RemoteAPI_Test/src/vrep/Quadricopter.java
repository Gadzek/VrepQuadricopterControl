package vrep;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import coppelia.FloatW;
import coppelia.FloatWA;
import coppelia.IntW;
import coppelia.remoteApi;
import helper.Point3;


public class Quadricopter implements Runnable
{
	//TODO: Add LoadModel method (here?)
	
	//private	FloatWA dronePosition;
	//private	FloatWA targetPosition;
	private	int droneHandle;
	private	int targetHandle;
	private	int clientID;
	private float velocity;
	private	remoteApi vrep;
	
	private Point3 position;
	private Point3 positionOfTarget;
	
	public static ArrayList<Integer> loadedQuadricopters;
	private LinkedList<FloatWA> path;
	//private LinkedList<Command> commands;
	
	public final static int quad_path_linear = 1;
	public final static int quad_path_cosine = 2;
	public final static int quad_path_cubic = 3;
	public final static int quad_path_hermite = 4;
	public final static int quad_path_bezier = 5;
	
	@Override
	public void run()
	{
		executeCommands();
	}
	
	protected Quadricopter(int cid, remoteApi api)
	{
		this(cid, api, "Quadricopter");
	}
	
	protected Quadricopter(int cid, remoteApi api, String quadName)
	{
		this.clientID = cid;
		this.vrep = api;
		
		//TODO: Make Quadricopter search and loadedQuadricopters list in Vrep class rather than Quadricopter
		if (loadedQuadricopters == null)
			loadedQuadricopters = new ArrayList<>();
		path = new LinkedList<>();
		
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
		
		if (!loadedQuadricopters.contains(droneHandle))
			loadedQuadricopters.add(droneHandle);
		
		//dronePosition = new FloatWA(3);
		//targetPosition = new FloatWA(3);
		//getObjectPosition(targetHandle, targetPosition);
		//getObjectPosition(droneHandle, dronePosition);
		
		velocity = 0.2f;
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
	
	public void moveTo(Point3 target, float velocity) //TODO: Make sure object is not already moving
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
			//path.add(next);
			//path.add(next);
			//System.out.println(p);
		    while(vrep.simxSetObjectPosition(clientID, targetHandle, -1, next, remoteApi.simx_opmode_oneshot_wait) != remoteApi.simx_return_ok){}
		}
		//position = getObjectPosition(droneHandle);
		//positionOfTarget = getObjectPosition(targetHandle);
	}
	
	public void moveTo(float x, float y, float z)
	{
		pathAddLinearInterpolation(new Point3(x, y, z));
	}
	
	public void moveTo(float x, float y, float z, float velocity)
	{
		moveTo(new Point3(x, y, z), velocity);
	}
	
	public void moveTo(float x, float y, float z, int pattern)
	{
		moveTo(new Point3(x, y, z), pattern);
	}
	
	public void moveTo(Point3 target)
	{
		pathAddLinearInterpolation(target);
	}
	
	public void moveTo(Point3 target, int pattern)
	{
		moveTo(target, pattern, null);
	}
	
	public void moveTo(Point3 target, int pattern, List<Point3> bezierPoints)
	{
		switch(pattern)
		{
		case quad_path_linear:
			pathAddLinearInterpolation(target);
			break;
		case quad_path_cosine:
			pathAddCosineInterpolation(target);
			break;
		case quad_path_bezier:
			pathAddBezierCurve(target, bezierPoints);
			break;
		default:
			System.out.println("Incorrect algorithm integer. Using linear interpolation...");
			pathAddLinearInterpolation(target);
			break;
		}
	}
	
	public void addPath(List<Point3> knots, int algorithm)
	{
		addPath(knots, algorithm, 0f, 0f);
	}
	
	public void addPath(List<Point3> knots, int algorithm, float hermiteBias, float hermiteTension)
	{
		switch(algorithm)
		{
		case quad_path_linear:
			for(Point3 knot : knots)
			{
				pathAddLinearInterpolation(knot);
			}			
			break;
		case quad_path_cosine:
			for(Point3 knot : knots)
			{
				pathAddCosineInterpolation(knot);
			}
			break;
		case quad_path_cubic:
			pathAddCubicInterpolation(knots);
			break;
		case quad_path_hermite:
			pathAddHermiteInterpolation(knots, hermiteBias, hermiteTension);
			break;
		case quad_path_bezier:
			pathAddBezierPath(knots);
			break;
		default:
			System.out.println("Incorrect algorithm integer. Using linear interpolation...");
			for(Point3 knot : knots)
			{
				pathAddLinearInterpolation(knot);
			}	
			break;
		}
	}
	
	private void pathAddLinearInterpolation(Point3 target)
	{
		Point3 startPos;
		if (path.isEmpty())
			startPos = position;
		else
			startPos = new Point3(path.getLast().getArray());
		
		//float distance = startPos.distance(target);
		
		//FloatW dt = new FloatW(0);
		//vrep.simxGetFloatingParameter(clientID, remoteApi.sim_floatparam_simulation_time_step, dt, remoteApi.simx_opmode_oneshot_wait);
		//float ds = velocity*dt.getValue();
		
		for(float mu=0f; mu<1; mu+=0.01)
		{
			Point3 p = startPos.lerp(target, mu);
			FloatWA next = new FloatWA(p.toFloatArray());
		    path.add(next);
		}
	}
	
	private void pathAddCosineInterpolation(Point3 target)
	{
		Point3 startPos;
		if (path.isEmpty())
			startPos = position;
		else
			startPos = new Point3(path.getLast().getArray());
		
		//float distance = startPos.distance(target);
		
		//FloatW dt = new FloatW(0);
		//vrep.simxGetFloatingParameter(clientID, remoteApi.sim_floatparam_simulation_time_step, dt, remoteApi.simx_opmode_oneshot_wait);
		//float ds = velocity*dt.getValue();
		
		for(float mu=0f; mu<1; mu+=0.01)
		{
			Point3 p = startPos.cosInt(target, mu);
			FloatWA next = new FloatWA(p.toFloatArray());
		    path.add(next);
		}
	}
	
	private void pathAddCubicInterpolation(List<Point3> knots)
	{			
		if(knots == null || knots.isEmpty())
		{
			System.out.println("Not enough knots for interpolation!");
			return;
		}
		
		Point3 startPos;
		if (path.isEmpty())
			startPos = position;
		else
			startPos = new Point3(path.getLast().getArray());
		
		knots.add(0, startPos);
		
		for (int i=0; i<knots.size()-1; i++)
		{
			Point3 prevKnot;
			Point3 thisKnot = knots.get(i);
			Point3 nextKnot = knots.get(i+1);
			Point3 nextKnot2;
			
			if(i == 0)
			{
				prevKnot = thisKnot.sub(nextKnot.sub(thisKnot));				
			}
			else
			{
				prevKnot = knots.get(i-1);
			}
			if(i == knots.size()-2)
				nextKnot2 = nextKnot.add(nextKnot.sub(thisKnot));
			else
				nextKnot2 = knots.get(i+2);
			
			for(float mu=0f; mu<1; mu+=0.01)
			{
				Point3 p = thisKnot.cubInt(prevKnot, nextKnot, nextKnot2, mu);
				FloatWA next = new FloatWA(p.toFloatArray());
			    path.add(next);
			}

		}
	}
	
	private void pathAddHermiteInterpolation(List<Point3> knots, float bias, float tension)
	{
		if(knots == null || knots.isEmpty())
		{
			System.out.println("Not enough knots for interpolation!");
			return;
		}
		
		Point3 startPos;
		if (path.isEmpty())
			startPos = position;
		else
			startPos = new Point3(path.getLast().getArray());
		
		knots.add(0, startPos);
		
		for (int i=0; i<knots.size()-1; i++)
		{
			Point3 prevKnot;
			Point3 thisKnot = knots.get(i);
			Point3 nextKnot = knots.get(i+1);
			Point3 nextKnot2;
			
			if(i == 0)
			{
				prevKnot = thisKnot.sub(nextKnot.sub(thisKnot));				
			}
			else
			{
				prevKnot = knots.get(i-1);
			}
			if(i == knots.size()-2)
				nextKnot2 = nextKnot.add(nextKnot.sub(thisKnot));
			else
				nextKnot2 = knots.get(i+2);
			/*
			float distance = thisKnot.distance(nextKnot);
			FloatW dt = new FloatW(0);
			vrep.simxGetFloatingParameter(clientID, remoteApi.sim_floatparam_simulation_time_step, dt, remoteApi.simx_opmode_oneshot_wait);
			float ds = velocity*dt.getValue()/distance;
			*/
			for(float mu=0f; mu<1; mu+=0.01)
			{
				Point3 p = thisKnot.hermiteInt(prevKnot, nextKnot, nextKnot2, mu, bias, tension);
				FloatWA next = new FloatWA(p.toFloatArray());
			    path.add(next);
			}

		}
	}
	
	public void pathAddBezierCurve(Point3 target, List<Point3> bezierPoints)
	{
		if (bezierPoints == null || bezierPoints.isEmpty())
		{
			//set default values
		}
		
		Point3 startPos;
		if (path.isEmpty())
			startPos = position;
		else
			startPos = new Point3(path.getLast().getArray());
		
		//float distance = startPos.distance(target);
		
		//FloatW dt = new FloatW(0);
		//vrep.simxGetFloatingParameter(clientID, remoteApi.sim_floatparam_simulation_time_step, dt, remoteApi.simx_opmode_oneshot_wait);
		//float ds = velocity*dt.getValue();
		
		for(float mu=0f; mu<1; mu+=0.01)
		{
			Point3 p = startPos.bezierCurve(target, bezierPoints, mu);
			FloatWA next = new FloatWA(p.toFloatArray());
		    path.add(next);
		}
	}
	
	public void pathAddBezierPath(List<Point3> knots)
	{

		
		if(knots == null || knots.isEmpty())
		{
			System.out.println("Not enough knots for interpolation!");
			return;
		}
		
		Point3 startPos;
		if (path.isEmpty())
			startPos = position;
		else
			startPos = new Point3(path.getLast().getArray());
		
		if (!startPos.equals(knots.get(0), 0.01))
			knots.add(0, startPos);
		
		for (int i=0; i<knots.size()-1; i++)
		{
			Point3 thisKnot = knots.get(i);
			Point3 nextKnot = knots.get(i+1);
			
			Point3 bezier1;
			Point3 bezier2;
			
			Point3 normal = null;
			Point3 normal2 = null;
			
			if(i == 0)
			{
				bezier1 = thisKnot.add(nextKnot.sub(thisKnot).mul(0.3));
			}
			else
			{
				Point3 prevKnot = knots.get(i-1);
				normal = thisKnot.bisect(prevKnot, nextKnot);
				Point3 cross = thisKnot.cross(prevKnot, nextKnot);
				bezier1 = thisKnot.cross(normal, cross);
				
				float bezierLength = thisKnot.distance(bezier1);
				float segmentLength = thisKnot.distance(nextKnot);
				if (bezierLength > segmentLength/2)
					bezierLength = segmentLength/(2*bezierLength);
				
				bezier1 = thisKnot.add(thisKnot.sub(bezier1).mul(bezierLength));
			}
			
			if(i == knots.size()-2)
			{
				bezier2 = nextKnot.add(thisKnot.sub(nextKnot).mul(0.3));
			}
			else
			{
				Point3 nextKnot2 = knots.get(i+2);
				normal2 = nextKnot.bisect(thisKnot, nextKnot2);
				Point3 cross2 = nextKnot.cross(thisKnot, nextKnot2);
				bezier2 = nextKnot.cross(cross2, normal2);
				
				float bezierLength = nextKnot.distance(bezier2);
				float segmentLength = nextKnot.distance(nextKnot2);
				if (bezierLength > segmentLength/2)
					bezierLength = segmentLength/(2*bezierLength);
				
				bezier2 = nextKnot.add(nextKnot.sub(bezier2).mul(bezierLength));
			}
			
			List<Point3> bezierPoints = new LinkedList<>();
			bezierPoints.add(bezier1);
			bezierPoints.add(bezier2);
			System.out.println(normal + " | " + normal2 + " || " + bezier1 + " | " + bezier2);
			
			for(float mu=0f; mu<1; mu+=0.01)
			{
				Point3 p = thisKnot.bezierCurve(nextKnot, bezierPoints, mu);
				FloatWA next = new FloatWA(p.toFloatArray());
			    path.add(next);
			}

		}
		
	}
	
	public void wait(float ms)
	{
		FloatW dt = new FloatW(0);
		Point3 pos;
		
		if (path.isEmpty())
			pos = position;
		else
			pos = new Point3(path.getLast().getArray());
		
		vrep.simxGetFloatingParameter(clientID, remoteApi.sim_floatparam_simulation_time_step, dt, remoteApi.simx_opmode_oneshot_wait);
		
		for (float t=0; t<ms; t+=dt.getValue())
		{
			path.add(new FloatWA(pos.toFloatArray()));
		}
	}

	public void executeCommands()
	{
		while (!path.isEmpty())
		{
			while(vrep.simxSetObjectPosition(clientID, targetHandle, -1, path.removeFirst(), remoteApi.simx_opmode_oneshot_wait) != remoteApi.simx_return_ok){}
		}
	}
}
