package helper;
import java.lang.Math;
import java.util.LinkedList;
import java.util.List;

public class Point3 {

	private float x;
	private float y;
	private float z;
	
	public static final Point3 ZERO_POINT = new Point3(0,0,0);
	
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
	
	public boolean equals(Point3 p)
	{
		return this.equals(p, 0.00001);
	}
	
	public boolean equals(Point3 p, double epsilon)
	{
		if (epsilon < 0)
			epsilon = -1*epsilon;
		
		Point3 r = this.sub(p);
		if (
				(-epsilon <= r.x && r.x <= epsilon) &&
				(-epsilon <= r.y && r.y <= epsilon) &&
				(-epsilon <= r.z && r.z <= epsilon)
			)
			return true;
		else
			return false;		
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
	
	public Point3 mul(double f)
	{
		return new Point3(f*x, f*y, f*z);
	}
	
	public Point3 cross(Point3 p1, Point3 p2)
	{
		Point3 c = new Point3();
		Point3 vec1 = p1.sub(this);
		Point3 vec2 = p2.sub(this);
		
		c.x = vec1.y*vec2.z - vec1.z*vec2.y;
		c.y = vec1.z*vec2.x - vec1.x*vec2.z;
		c.z = vec1.x*vec2.y - vec1.y*vec2.x;

		return this.add(c);
	}
	
	public Point3 dot(Point3 p1, Point3 p2)
	{
		return this.add(p1.sub(this)).add(p2.sub(this));
	}
	
	public Point3 bisect(Point3 p1, Point3 p2)
	{
		return bisect(p1, p2, 1);
	}
	
	public Point3 bisect(Point3 p1, Point3 p2, float length)
	{
		Point3 a = this.add(p1.sub(this).mul(this.distance(p2)));
		Point3 b = this.add(p2.sub(this).mul(this.distance(p1)));
		Point3 r = this.dot(a, b);
		r = this.add(r.sub(this).mul(length));
		return r;
	}
	
	public Point3 lerp(Point3 target, float mu)
	{
		mu = Math.max(0f, Math.min(1f, mu));
		return this.add(target.sub(this).mul(mu)); //start + (end - start)*step
	}
	
	public Point3 cosInt(Point3 target, float mu)
	{
		mu = Math.max(0f, Math.min(1f, mu));
		float mu2 = (float)(1-Math.cos(mu*Math.PI))/2;
		
		//Point3 p = new Point3(this.x*(1-mu)+target.x*mu, this.y*(1-mu2)+target.y*mu2, this.z*(1-mu)+target.z*mu);
		Point3 p = new Point3(this.x*(1-mu2)+target.x*mu2, this.y*(1-mu2)+target.y*mu2, this.z*(1-mu)+target.z*mu);
		
		return p;
	}
	
	public Point3 cubInt(Point3 prevKnot, Point3 nextKnot, Point3 nextKnot2, float mu)
	{
		float mu2 = mu*mu;
		
		//Point3 a0 = nextKnot2.sub(nextKnot).sub(prevKnot).add(this);
		//Point3 a1 = prevKnot.sub(this).sub(a0);
		//Point3 a2 = nextKnot.sub(prevKnot);
		//Point3 a3 = this;
		
		Point3 a0 = prevKnot.mul(-0.5f).add(this.mul(1.5f)).sub(nextKnot.mul(1.5f)).add(nextKnot2.mul(0.5f));
		Point3 a1 = prevKnot.sub(this.mul(2.5f)).add(nextKnot.mul(2f)).sub(nextKnot2.mul(0.5f));
		Point3 a2 = prevKnot.mul(-0.5f).add(nextKnot.mul(0.5f));
		Point3 a3 = this;
		
		return a0.mul(mu*mu2).add(a1.mul(mu2)).add(a2.mul(mu)).add(a3);
	}
	
	public Point3 hermiteInt(Point3 prevKnot, Point3 nextKnot, Point3 nextKnot2, float mu, float bias, float tension)
	{
		float mu2 = mu*mu;
		float mu3 = mu * mu2;
		
		Point3 m0 = (this.sub(prevKnot)).mul(1+bias).mul(1-tension/2);
		m0 = m0.add(nextKnot.sub(this)).mul(1-bias).mul(1-tension/2);
		
		Point3 m1 = (nextKnot.sub(this)).mul(1+bias).mul(1-tension/2);
		m1 = m1.add(nextKnot2.sub(nextKnot)).mul(1-bias).mul(1-tension/2);
		
		float a0 = 2*mu3 - 3*mu2 + 1;
		float a1 = mu3 - 2*mu2 + mu;
		float a2 = mu3 - mu2;
		float a3 = -2*mu3 + 3*mu2;
		
		return this.mul(a0).add(m0.mul(a1)).add(m1.mul(a2)).add(nextKnot.mul(a3));
	}
	
	public Point3 bezierCurve(Point3 target, List<Point3> bezierPoints, float mu)
	{
		Point3 b;
		if (bezierPoints.isEmpty())
			b = this.mul(1-mu).add(target.mul(mu));
		else
		{
			LinkedList<Point3> a1 = new LinkedList<>(bezierPoints);
			LinkedList<Point3> a2 = new LinkedList<>(bezierPoints);
			Point3 b1 = this.bezierCurve(a1.removeLast(), a1, mu);
			Point3 b2 = a2.removeFirst().bezierCurve(target, a2, mu);
			b = b1.mul(1-mu).add(b2.mul(mu));
		}
		return b;
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
