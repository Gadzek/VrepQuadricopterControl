package vrep;

import java.util.LinkedList;
import java.util.List;

import coppelia.FloatWA;

public class CosineMultiple implements MoveThorughPointsAlgorithm 
{

	private Quadricopter parent;
	
	public CosineMultiple(Quadricopter parent)
	{
		this.parent = parent;
	}
	
	@Override
	public LinkedList<FloatWA> moveThrough(List<Point3> knots) 
	{
		Cosine cos = new Cosine(parent);
		LinkedList<FloatWA> path = new LinkedList<>();
		
		for(Point3 knot : knots)
		{
			path.addAll(cos.moveTo(knot));
		}
		return path;
	}

}
