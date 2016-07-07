package vrep;

public interface Signal {

	public boolean pauseCommands();
	public boolean stopCommands();
	//public boolean clearCommands();
	
	public void readSignalValue();
}
