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
			path.addAll(lerp.moveTo(knot));
		}
		return path;
	}

}
