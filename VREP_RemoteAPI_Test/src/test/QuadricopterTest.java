package test;

import vrep.Quadricopter;
import vrep.Vrep;

public class QuadricopterTest {

	public static void main(String[] args) 
	{
		ConstructorsTest();

	}
	
	public static void ConstructorsTest()
	{
		Vrep vrep = new Vrep();
		System.out.println("1");
		Quadricopter quad1 = vrep.getQuadricopter("Quadricopter");
		System.out.println("2");
		Quadricopter quad2 = vrep.getQuadricopter("Quadricopter#0");

		System.out.println(quad1.getPosition());
		System.out.println(quad2.getPosition());
		quad1.moveTo(0f, 0f, 1);
		quad2.moveTo(2f, 4f, 2f);
	}

}
