package coppelia;
public class Point3 {

	private float x;
	private float y;
	private float z;
	
	//float velocity?
	
	public Point3(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Point3(Point3 p)
	{
		x = p.x;
		y = p.y;
		z = p.z;
	}
	
	public Point3()
	{
		this(0f, 0f, 0f);
	}
	
	public Point3(float[] xyz)
	{
		if (xyz.length != 3)
			throw new IndexOutOfBoundsException("The array must have length of 3");
		else
		{
			this.x = xyz[0];
			this.y = xyz[1];
			this.z = xyz[2];
		}
	}
	
	public Point3(double x, double y, double z)
	{
		this((float)x, (float)y, (float)z);
	}
	
	public Point3(double[] xyz)
	{
		if (xyz.length != 3)
			throw new IndexOutOfBoundsException("The array must have length of 3");
		else
		{
			this.x = (float)xyz[0];
			this.y = (float)xyz[1];
			this.z = (float)xyz[2];
		}
	}
	
	public float distanceSquared(Point3 p)
	{
		return (x-p.x)*(x-p.x) + (y-p.y)*(y-p.y) + (z-p.z)*(z-p.z);
	}
	
	public float distance(Point3 p)
	{ 
		return (float)Math.sqrt(distanceSquared(p));
	}
	
	public Point3 add(Point3 p)
	{
		return new Point3(x+p.x, y+p.y, z+p.z);
	}
	
	public Point3 sub(Point3 p)
	{
		return new Point3(x-p.x, y-p.y, z-p.z);
	}
	
	public Point3 mul(float f)
	{
		return new Point3(f*x, f*y, f*z);
	}
	
	public Point3 lerp(Point3 target, float step)
	{
		step = Math.max(0f, Math.min(1f, step));
		return this.add(target.sub(this).mul(step)); //start + (end - start)*step
	}
	
	public float[] toFloatArray()
	{
		float[] f = {x, y, z};
		return f;
	}
	
	public String toString()
	{
		//return "x: "+ x +" y: "+ y + " z: " + z;
		return String.format("x: %f y: %f z: %f", x, y, z);
	}

}
