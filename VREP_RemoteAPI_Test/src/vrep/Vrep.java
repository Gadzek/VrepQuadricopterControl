package vrep;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import coppelia.remoteApi;

public class Vrep {

	//TODO: Add ErrorReporter
	public int clientID;
	public remoteApi api;
	public ArrayList<Quadricopter> drones;
	public GlobalSignalListener globalSignalListener;
	public Thread signals;
	
	public ExecutorService droneExecutor;
	
	//TODO: Add SignalListener and check interrupts
	
	public Vrep()
	{
		this("127.0.0.1");
	}
	
	public Vrep(String IP)
	{
	      api = new remoteApi();
	      api.simxFinish(-1); // just in case, close all opened connections
	      clientID = api.simxStart(IP,19997,true,true,5000,5);
	      drones = new ArrayList<Quadricopter>();
	      //globalSignalListener = new SignalListener();
	      //globalSignalListener.addSignal(simState);
	      
	      //loadScene();
	      //scene = false; //Scene not loaded
	      //api.simxStartSimulation(clientID, remoteApi.simx_opmode_oneshot);
	      
	      globalSignalListener = new GlobalSignalListener();
	      SimulationState simState = new SimulationState(clientID, api);
	      globalSignalListener.addSignal(simState);
	      signals = new Thread(globalSignalListener);
	      droneExecutor = Executors.newCachedThreadPool();
	      
	      if(clientID==-1)
	      {
	    	  System.out.println("Failed connecting to remote API server");
	      }
	      else
	      {
	    	  System.out.println("Connected to remote API server");
	      }
	}
	
	public Quadricopter getQuadricopter(String name)
	{
		Quadricopter q = new Quadricopter(clientID, api, name);
		for (Quadricopter quad : drones)
		{
			if (quad.getDroneHandle() == q.getDroneHandle())
			{
				return quad; //Do not add Quadricopter to list if it already exist there
			}
		}
		q.parentApp = this;
		q.setGlobalSignalListener(globalSignalListener);
		//q.setSignalListener(signalListener);
		drones.add(q);
		return q;
	}
	
	
	public int startSimulation()
	{
		int sim = api.simxStartSimulation(clientID, remoteApi.simx_opmode_oneshot);

		if(!globalSignalListener.pause)
		{
			//droneExecutor.execute(globalSignalListener);
			signals.start();
			for (Quadricopter quad : drones)
			{
				//quad.getDroneThread().start();
				droneExecutor.execute(quad.getDroneCommands());
			}
		}
		return sim;
	}
	
	public int stopSimulation() throws InterruptedException
	{
		return stopSimulation(true);
	}
	
	public int stopSimulation(boolean waitForDrones) throws InterruptedException
	{
		if(waitForDrones)
		{
			droneExecutor.shutdown();
			while (!droneExecutor.awaitTermination(24L, TimeUnit.HOURS)) {
			    System.out.println("Not yet. Still waiting for termination");
			}
			globalSignalListener.simRunning = false;
			return api.simxStopSimulation(clientID, remoteApi.simx_opmode_oneshot);
		}
		else
		{
			droneExecutor.shutdown();
			globalSignalListener.simRunning = false;
			return api.simxStopSimulation(clientID, remoteApi.simx_opmode_oneshot);
		}
	}
	
	public int pauseSimulation()
	{
		globalSignalListener.pause = true;
		return api.simxPauseSimulation(clientID, remoteApi.simx_opmode_oneshot);
	}
	
	public void disconnect()
	{
		api.simxFinish(clientID);
		System.out.println("Disconnected from remote API server");
	}
}
