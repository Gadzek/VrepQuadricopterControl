package vrep;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import coppelia.FloatWA;

public class BezierMultiple implements MoveThorughPointsAlgorithm
{
	private Quadricopter parent;
	
	public BezierMultiple(Quadricopter parent)
	{
		this.parent = parent;
	}
	
	@Override
	public LinkedList<FloatWA> moveThrough(List<Point3> knots) 
	{
		if(knots == null || knots.isEmpty())
		{
			System.out.println("Not enough knots for interpolation!");
			return null;
		}
		
		LinkedList<FloatWA> path = new LinkedList<>();		
		Point3 startPos;
		
		if (parent.getDroneCommands().path.isEmpty())
			startPos = parent.getPosition();
		else
			startPos = new Point3(parent.getDroneCommands().path.getLast().getArray());
		
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
		
		return path;
	}

}
