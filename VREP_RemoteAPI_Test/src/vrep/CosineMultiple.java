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
			Point3 startPos;
			if (path.isEmpty())
				startPos = parent.getPosition();
			else
				startPos = new Point3(path.getLast().getArray());
			
			for(float mu=0f; mu<1; mu+=0.01)
			{
				Point3 p = startPos.cosInt(knot, mu);
				FloatWA next = new FloatWA(p.toFloatArray());
			    path.add(next);
			}
		}
		return path;
	}

}
