package vrep;

import coppelia.IntW;
import coppelia.remoteApi;

public class SimulationState implements Signal{

	private remoteApi vrepApi;
	private int clientID;
	
	public static final int pauseValue = 19;
	public static final int stopValue = 21;
	public int signalValue;
	
	public SimulationState(int clientID, remoteApi vrepApi)
	{
		this.clientID = clientID;
		this.vrepApi = vrepApi;
	}
	
	@Override
	public void readSignalValue()
	{
		IntW state = new IntW(0);
		//while(vrepApi.simxGetIntegerSignal(clientID, "simstate", state, remoteApi.simx_opmode_streaming) != remoteApi.simx_return_ok);		
		int r;
		while((r = vrepApi.simxGetIntegerSignal(clientID, "simstate", state, remoteApi.simx_opmode_streaming)) != remoteApi.simx_return_ok)
		{
		}
		/*for (int i=0; 
				r != remoteApi.simx_return_novalue_flag && r != remoteApi.simx_return_ok && i<100; 
				i++){
			r = vrepApi.simxGetIntegerSignal(clientID, "simstate", state, remoteApi.simx_opmode_streaming);
		}*/
		if (r != remoteApi.simx_return_novalue_flag && r != remoteApi.simx_return_ok)
			VrepErrorHandler.handleFunctionReturnValue(Thread.currentThread().getStackTrace()[1].toString(), r);
		//System.out.println(r);
		
		signalValue = state.getValue();
		//System.out.println(signalValue);
	}
	
	public boolean pauseCommands()
	{
		return signalValue == pauseValue;
	}
	
	public boolean stopCommands()
	{
		return signalValue == stopValue;
	}
	
	public boolean clearCommands()
	{
		return signalValue == stopValue;
	}
	
}
