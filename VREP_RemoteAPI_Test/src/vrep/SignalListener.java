package vrep;

import java.util.ArrayList;
import java.util.List;

public class SignalListener implements Runnable{
	
	//public boolean pause;
	//public boolean stop;
	//public boolean simRunning = true;
	
	private List<Signal> signals;
	
	protected SignalListener()
	{
		signals = new ArrayList<Signal>();
	}
	
	public void addSignal(Signal signal)
	{
		signals.add(signal);
	}
	
	protected List<Signal> getSignals()
	{
		return signals;
	}
	
	public void run()
	{

	}

}
