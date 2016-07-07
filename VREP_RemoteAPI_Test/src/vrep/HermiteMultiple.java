package vrep;

import java.util.LinkedList;
import java.util.List;

import coppelia.FloatWA;

public class HermiteMultiple implements MoveThorughPointsAlgorithm 
{
	private Quadricopter parent;
	private float bias;
	private float tension;
	
	public HermiteMultiple(Quadricopter parent, float bias, float tension)
	{
		this.parent = parent;
		this.bias = bias;
		this.tension = tension;
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
				Point3 p = thisKnot.hermiteInt(prevKnot, nextKnot, nextKnot2, mu, bias, tension);
				FloatWA next = new FloatWA(p.toFloatArray());
			    path.add(next);
			}
		}

		return path;
	}

}
