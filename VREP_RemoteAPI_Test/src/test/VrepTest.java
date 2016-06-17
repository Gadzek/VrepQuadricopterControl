package test;
import coppelia.IntW;
import coppelia.remoteApi;
import vrep.Vrep;

public class VrepTest {

	public static void main(String[] args)
	{
		ConstructorsTest();
	}
	
	public static void ConstructorsTest()
	{
		Vrep vrep = new Vrep();
		//assert vrep.scene == false;
		assert vrep.clientID != -1;
		System.out.println(vrep.clientID);
		
		IntW targetH = new IntW(0);
		int out = vrep.api.simxGetObjectHandle(vrep.clientID, "Quad", targetH, vrep.api.simx_opmode_oneshot_wait);
		System.out.println(out);
	}
}
