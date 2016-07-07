package vrep;

import java.util.LinkedList;
import java.util.List;
import coppelia.FloatWA;

public interface MoveThorughPointsAlgorithm 
{
	public LinkedList<FloatWA> moveThrough(List<Point3> knots);
}
