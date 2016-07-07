package vrep;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import coppelia.FloatWA;

public class DroneCommands implements Runnable{

	//TODO: Add non-global SignalListener execution
	public LinkedList<FloatWA> path;
	private Quadricopter parent;
	
	protected DroneCommands(Quadricopter parent)
	{
		this.parent = parent;
		path = new LinkedList<FloatWA>();
	}	
	
	@Override
	public void run()
	{		
		while (!path.isEmpty() && parent.getGlobalSignalListener().simRunning)
		{
			if(!parent.getGlobalSignalListener().pause)
			//synchronized(parent.getGlobalSignalListener())
			{
				parent.setTargetPosition(path.removeFirst());
				
				try{
					//parent.getGlobalSignalListener().wait();
					Thread.sleep(15);
				} catch (InterruptedException e){
					e.printStackTrace();
				}
			}
		}
		if (!parent.getGlobalSignalListener().simRunning)
		{
			path.clear();
		}
	}
	
	public void moveTo(Point3 target)
	{
		moveTo(target, Quadricopter.quad_path_linear);
	}
	
	public void moveTo(Point3 target, int pattern)
	{
		moveTo(target, pattern, new LinkedList<Point3>());
	}
	
	public void moveTo(Point3 target, MoveToPointAlgorithm a)
	{
		path.addAll(a.moveTo(target));
	}
	
	public void moveThrough(List<Point3> knots, MoveThorughPointsAlgorithm a)
	{
		path.addAll(a.moveThrough(knots));
	}
	
	public void moveTo(Point3 target, int pattern, List<Point3> bezierPoints)
	{
		switch(pattern)
		{
		case Quadricopter.quad_path_linear:
			pathAddLinearInterpolation(target);
			break;
		case Quadricopter.quad_path_cosine:
			pathAddCosineInterpolation(target);
			break;
		case Quadricopter.quad_path_bezier:
			pathAddBezierCurve(target, bezierPoints);
			break;
		default:
			System.out.println("Incorrect algorithm integer. Using linear interpolation...");
			pathAddLinearInterpolation(target);
			break;
		}
	}
	
	public void addPath(List<Point3> knots)
	{
		addPath(knots, Quadricopter.quad_path_linear);
	}
	
	public void addPath(List<Point3> knots, int algorithm)
	{
		addPath(knots, algorithm, 0f, 0f);
	}
	
	public void addPath(List<Point3> knots, int algorithm, float hermiteBias, float hermiteTension)
	{
		switch(algorithm)
		{
		case Quadricopter.quad_path_linear:
			for(Point3 knot : knots)
			{
				pathAddLinearInterpolation(knot);
			}			
			break;
		case Quadricopter.quad_path_cosine:
			for(Point3 knot : knots)
			{
				pathAddCosineInterpolation(knot);
			}
			break;
		case Quadricopter.quad_path_cubic:
			pathAddCubicInterpolation(knots);
			break;
		case Quadricopter.quad_path_hermite:
			pathAddHermiteInterpolation(knots, hermiteBias, hermiteTension);
			break;
		case Quadricopter.quad_path_bezier:
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
			startPos = parent.getPosition();
		else
			startPos = new Point3(path.getLast().getArray());
		//System.out.println(startPos);
		
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
			startPos = parent.getPosition();
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
			startPos = parent.getPosition();
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
			startPos = parent.getPosition();
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
			startPos = parent.getPosition();
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
			startPos = parent.getPosition();
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
			//System.out.println(normal + " | " + normal2 + " || " + bezier1 + " | " + bezier2);
			
			Point3 prevPoint = thisKnot;
			float arcLength = 0;
			Map<Float, Float> arcIntegral = new HashMap<>();
			//Map<Float, Float> arcDerivative = new HashMap<>();
			
			for(float mu=0f; mu<1; mu+=0.01f)
			{
				Point3 p = thisKnot.bezierCurve(nextKnot, bezierPoints, mu);
				float ds = prevPoint.distance(p);
				arcLength += ds;
				arcIntegral.put(mu, arcLength);
			    prevPoint = p;
			}
			
			for(float mu=0f; mu<1; mu+=0.01f)
			{
				float ds = arcIntegral.get(mu)/arcLength;
				Point3 p = thisKnot.bezierCurve(nextKnot, bezierPoints, ds);
				
				FloatWA next = new FloatWA(p.toFloatArray());
			    path.add(next);
			}

		}	
	}

	
	
}
