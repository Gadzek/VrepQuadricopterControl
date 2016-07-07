package vrep;

import java.util.LinkedList;
import coppelia.FloatWA;

public class Cosine implements MoveToPointAlgorithm 
{

	private Quadricopter parent;
	
	public Cosine(Quadricopter parent)
	{
		this.parent = parent;
	}
	
	@Override
	public LinkedList<FloatWA> moveTo(Point3 target) 
	{
		Point3 startPos;
		LinkedList<FloatWA> path = new LinkedList<>();
		
		if (parent.getDroneCommands().path.isEmpty())
			startPos = parent.getPosition();
		else
			startPos = new Point3(parent.getDroneCommands().path.getLast().getArray());
		
		for(float mu=0f; mu<1; mu+=0.01)
		{
			Point3 p = startPos.cosInt(target, mu);
			FloatWA next = new FloatWA(p.toFloatArray());
		    path.add(next);
		}
		
		return path;
	}

}
