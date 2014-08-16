package stegexpose;

import java.util.ArrayList;
/**
 * StegExpose
 * 
 * @author Benedikt Boehm
 * @version 0.1
 */
public class Fuse {
	

	

	
	public static double se(ArrayList<Double> input){
		double se;
		//int num = 0;
		double sum =0;
		for(Double detector : input){
			//System.out.println(detector);
			
			
			sum+=detector;
			
			
		}
		se = sum/input.size();
		//System.out.println("size "+input.size());
		//System.out.println("result "+se);
		return se;
	}
	
	
	public static double seQ(double se, double fileLength){
		return se*fileLength/3;
	}
	

}
