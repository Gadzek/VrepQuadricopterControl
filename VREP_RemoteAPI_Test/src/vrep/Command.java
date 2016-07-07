package vrep;

import java.util.LinkedList;
import java.util.List;

public class Command {

	public Point3 target;
	public int[] intParams;
	public float[] floatParams;
	public String[] stringParams;
	public LinkedList<Point3> point3Params;
	
	public Command()
	{
		target = new Point3();
		intParams = new int[0];
		floatParams = new float[0];
		stringParams = new String[0];
		point3Params = new LinkedList<Point3>();
	}
	
	public Command(Point3 p, int[] intParams, float[] floatParams, String[] stringParams, List<Point3> point3Params)
	{
		target = p;
		this.intParams = intParams;
		this.floatParams = floatParams;
		this.stringParams = stringParams;
		this.point3Params = new LinkedList<>(point3Params);
	}
	
	
	
}
