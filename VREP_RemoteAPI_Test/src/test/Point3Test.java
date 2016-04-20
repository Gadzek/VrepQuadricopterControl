package test;
import java.util.Arrays;

import coppelia.Point3;

public class Point3Test 
{
	public static void main(String[] args)
	{
		ConstructorsTest();
		OperationsTest();
		distanceTest();
		lerpTest();
		//System.out.println(1f - 0.6f);
	}
	
	public static void ConstructorsTest()
	{
		System.out.println("Constructors: ");
		
		Point3 p = new Point3();
		assert Arrays.equals(p.toFloatArray(), new float[]{0f, 0f, 0f});
		System.out.println(p.toString());
		
		p = new Point3(1f, -1f, 1f);
		assert Arrays.equals(p.toFloatArray(), new float[]{1f, -1f, 1f});
		System.out.println(p.toString());
		
		float[] f = new float[]{0.5f, 0.5f, 0.5f};
		p = new Point3(f);
		assert Arrays.equals(p.toFloatArray(), f);
		System.out.println(p.toString());
		
		p = new Point3(1, -1, 1);
		assert Arrays.equals(p.toFloatArray(), new float[]{1f, -1f, 1f});
		System.out.println(p.toString());
		
		double[] d = new double[]{0.5, 0.5, 0.5};
		p = new Point3(d);
		assert Arrays.equals(p.toFloatArray(), new float[]{0.5f, 0.5f, 0.5f});
		System.out.println(p.toString());
		
		try
		{
			p = new Point3(new float[2]);
		}
		catch(IndexOutOfBoundsException e)
		{
			System.out.println(e.getMessage());
		}
		
		try
		{
			p = new Point3(new float[4]);
		}
		catch(IndexOutOfBoundsException e)
		{
			System.out.println(e.getMessage());
		}
		
	}
	
	public static void OperationsTest()
	{
		System.out.println("\nOperations: ");
		
		Point3 a = new Point3(1f, 0.9f, 0.2f);
		Point3 b = new Point3(0.4f, 0.5f, 0.78f);
		Point3 c = a.add(b);
		assert Arrays.equals(c.toFloatArray(), new float[]{1f+0.4f, 0.9f+0.5f, 0.2f+0.78f});
		System.out.println(c.toString());
		
		c = a.sub(b);
		assert Arrays.equals(c.toFloatArray(), new float[]{1f-0.4f, 0.9f-0.5f, 0.2f-0.78f});
		System.out.println(c.toString());
		
		c = a.mul(3.56f);
		assert Arrays.equals(c.toFloatArray(), new float[]{1f*3.56f, 0.9f*3.56f, 0.2f*3.56f});
		System.out.println(c.toString());
		c = a.mul(-1.09f);
		assert Arrays.equals(c.toFloatArray(), new float[]{1f*-1.09f, 0.9f*-1.09f, 0.2f*-1.09f});
		System.out.println(c);
	}
	
	public static void distanceTest()
	{
		System.out.println("\nDistance: ");
		
		Point3 a = new Point3(1f, 1.5f, 0f);
		Point3 b = new Point3(0.4f, 0.5f, 0.3f);
		float d = a.distanceSquared(b);
		assert d == (a.toFloatArray()[0]-b.toFloatArray()[0])*(a.toFloatArray()[0]-b.toFloatArray()[0]) 
				+ (a.toFloatArray()[1]-b.toFloatArray()[1])*(a.toFloatArray()[1]-b.toFloatArray()[1])
				+ (a.toFloatArray()[2]-b.toFloatArray()[2])*(a.toFloatArray()[2]-b.toFloatArray()[2]);
		assert a.distanceSquared(b) == b.distanceSquared(a);
		System.out.println(d);
		
		float d2 = a.distance(b);
		assert d2 == (float)Math.sqrt(d);
		assert a.distance(b) == b.distance(a);
		System.out.println(d2);
	}
	
	public static void lerpTest()
	{
		System.out.println("\nLerp: ");
		
		float step = 0.8f;
		step = Math.max(0f, Math.min(1f, step));
		Point3 a = new Point3(1f, 1f, 1f);
		Point3 b = new Point3(0.5f, 0.5f, 0.5f);
		Point3 c = a.lerp(b, step);
		assert Arrays.equals(c.toFloatArray(), new float[]{1f+(0.5f-1f)*step, 1f+(0.5f-1f)*step, 1f+(0.5f-1f)*step});
		System.out.println(c);
	}
}
