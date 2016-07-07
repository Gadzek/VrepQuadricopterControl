package vrep;

import java.util.BitSet;
import coppelia.remoteApi;

public class VrepErrorHandler 
{	
	private static final BitSet return_ok = convert(remoteApi.simx_return_ok);
	private static final BitSet return_novalue_flag = convert(remoteApi.simx_return_novalue_flag);
	private static final BitSet return_timeout_flag = convert(remoteApi.simx_return_timeout_flag);
	private static final BitSet return_illegal_opmode_flag = convert(remoteApi.simx_return_illegal_opmode_flag);
	private static final BitSet return_remote_error_flag = convert(remoteApi.simx_return_remote_error_flag);
	private static final BitSet return_split_progress_flag = convert(remoteApi.simx_return_split_progress_flag);
	private static final BitSet return_local_error_flag = convert(remoteApi.simx_return_local_error_flag);
	private static final BitSet return_initialize_error_flag = convert(remoteApi.simx_return_initialize_error_flag);
	
	public static StringBuilder logger;
	
	
	public static void handleFunctionReturnValue(String functionName, int returnValue)
	{
		BitSet retVal = convert(returnValue);
		if(retVal.intersects(return_novalue_flag))
			System.out.println(functionName + ": Function returns no value flag");
		if(retVal.intersects(return_timeout_flag))
			System.out.println(functionName + ": Function timed out");
		if(retVal.intersects(return_illegal_opmode_flag))
			System.out.println(functionName + ": Operation mode not supported for given function");
		if(retVal.intersects(return_remote_error_flag))
			System.out.println(functionName + ": Function caused an error on the server side (e.g. invalid handle was given)");
		if(retVal.intersects(return_split_progress_flag))
			System.out.println(functionName + ": The communication thread is still processing previous split command of the same type");
		if(retVal.intersects(return_local_error_flag))
			System.out.println(functionName + ": Function caused an error on the client side");
		if(retVal.intersects(return_initialize_error_flag))
			System.out.println(functionName + ": simxStart function has not been called or Vrep class object has not been created");
	}
	

	public static BitSet convert(long value) 
	{
	    BitSet bits = new BitSet(7);
	    int index = 0;
	    while (value != 0L) 
	    {
	      if (value % 2L != 0) 
	      {
	        bits.set(index);
	      }
	      ++index;
	      value = value >>> 1;
	    }
	    return bits;
	  }
}
