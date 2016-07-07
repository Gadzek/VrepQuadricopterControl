package vrep;
import coppelia.FloatWA;
import java.util.LinkedList;

public interface MoveToPointAlgorithm 
{
	public LinkedList<FloatWA> moveTo(Point3 target);
}
