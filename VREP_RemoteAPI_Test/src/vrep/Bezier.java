package vrep;

import java.util.LinkedList;
import java.util.List;
import coppelia.FloatWA;

public class Bezier implements MoveToPointAlgorithm 
{
	private Quadricopter parent;
	private List<Point3> bezierPoints;
	
	public Bezier(Quadricopter parent, List<Point3> bezierPoints)
	{
		this.parent = parent;
		this.bezierPoints = new LinkedList<Point3>(bezierPoints);
	}

	@Override
	public LinkedList<FloatWA> moveTo(Point3 target) 
	{
		if (bezierPoints == null || bezierPoints.isEmpty())
		{
			//set default values
		}
		
		Point3 startPos;
		LinkedList<FloatWA> path = new LinkedList<>();
		
		if (parent.getDroneCommands().path.isEmpty())
			startPos = parent.getPosition();
		else
			startPos = new Point3(parent.getDroneCommands().path.getLast().getArray());
		
		for(float mu=0f; mu<1; mu+=0.01)
		{
			Point3 p = startPos.bezierCurve(target, bezierPoints, mu);
			FloatWA next = new FloatWA(p.toFloatArray());
		    path.add(next);
		}
		
		return path;
	}
	
}
