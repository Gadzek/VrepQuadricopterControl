package vrep;

import java.util.ArrayList;
import java.util.List;

public class GlobalSignalListener extends SignalListener {
	
	public boolean pause;
	//public boolean stop;
	public boolean simRunning;
	
	private List<Signal> signals;
	
	protected GlobalSignalListener() 
	{
		simRunning = true;
		pause = false;
		signals = new ArrayList<Signal>();
	}
	
	@Override
	public void addSignal(Signal signal)
	{
		signals.add(signal);
	}
	
	@Override
	protected List<Signal> getSignals()
	{
		return signals;
	}
	
	@Override
	public void run()
	{
		simRunning = true;
		while(simRunning)
		{				
				int pauses = 0;
				int stops = 0;
	
				for (Signal signal : signals)
				{
					signal.readSignalValue();
					if(signal.pauseCommands())
						pauses++;
					if(signal.stopCommands())
						stops++;
				}
				//synchronized (this) 
				//{
					if(pauses==0)
						pause = false;
						//this.notifyAll();
					else
						pause = true;
					if(stops==0)
						simRunning = true;
					else
						simRunning = false;			
				//}
		}
	}

}
