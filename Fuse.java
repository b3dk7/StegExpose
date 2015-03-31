import java.util.ArrayList;
/**
 * Fuse
 * 
 * Provides static methods used by RunStegExpose in order to combine detector results 
 * 
 * @author Benedikt Boehm
 * @version 0.1
 */
public class Fuse {

	/**
	 * Outputs the mean value of all members in a List
	 * 
	 * @param input		List of detector results to be used in fusion
	 * @return 			Mean result			
	 */
	public static double se(ArrayList<Double> input){
		double se;
		double sum =0;
		for(Double detector : input){
			sum+=detector;
		}
		se = sum/input.size();
		return se;
	}
	
	/**
	 * Provides quantitative steganalysis
	 * 
	 * @param se			steganalytic input (percentage value)
	 * @param fileLength	size of the file being analysed
	 * @return 				Quantitative steganalytic output
	 */
	public static double seQ(double se, double fileLength){
		return se*fileLength/3;
	}
	

}
