package vrep;

import java.util.LinkedList;
import java.util.List;

import coppelia.FloatWA;

public class LinearMultiple implements MoveThorughPointsAlgorithm {

	private Quadricopter parent;
	
	public LinearMultiple(Quadricopter parent)
	{
		this.parent = parent;
	}
	
	public LinkedList<FloatWA> moveThrough(List<Point3> knots) 
	{
		Linear lerp = new Linear(parent);
		LinkedList<FloatWA> path = new LinkedList<>();
		
		for(Point3 knot : knots)
		{
			//path.addAll(lerp.moveTo(knot));
			Point3 startPos;
			if (path.isEmpty())
				startPos = parent.getPosition();
			else
				startPos = new Point3(path.getLast().getArray());
			//System.out.println(startPos);
			
			for(float mu=0f; mu<1; mu+=0.01)
			{
				Point3 p = startPos.lerp(knot, mu);
				FloatWA next = new FloatWA(p.toFloatArray());
			    path.add(next);
			}
		}
		return path;
	}

}
