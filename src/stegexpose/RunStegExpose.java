

package stegexpose;
//import fr.steganalysis;
import invisibleinktoolkit.benchmark.RSAnalysis;
import invisibleinktoolkit.benchmark.SamplePairs;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;


//import fr.steganalysis.AverageLsb;
import fr.steganalysis.ChiSquare;
import fr.steganalysis.ImageFileManager;
import fr.steganalysis.PrimarySets;



/**
 * StegExpose
 * 
 * @author Benedikt Boehm
 * @version 0.1
 */


public class RunStegExpose {
	
	//size of chi square blocks 
	private static int csSize = 1024;
	//threshold to be applied to stegexpose indicator
	private static double threshold = 0.2;
	//setting percentage points for fast mode
	//private static double speedThreshold = 0.1;
	//private static double cleanThreshold;

	private static boolean fast = false;
	private static boolean csvMode = false;
	
	private static double minProb = 0;
	private static double maxProb = 1;
	
	private static int RED =0;
	private static int GREEN =1;
	private static int BLUE =2;
	
	private static String fileName;
	
	
	//prepare csv file file
	private static PrintWriter writer;
	
	
	private static ArrayList<Double> stegExposeInput;
	private static double fileSize;
	
	
	

	private static Double ps = null;
	private static Double cs=null;
	private static Double sp = null;
	private static Double rs = null;
	private static Double fusion=null;
	private static Long fusionQ=null;
	
	
	
	public static void main(String[] args){
		
		//obtaining all files to be steganalysed
		File[] listOfFiles;
		if(args.length>0){
			File folder = new File(args[0]);
			listOfFiles = folder.listFiles();
		}
		else{
			System.out.println("please provide StegExpose with directory of files to be scanned");
			return;
		}
		
		//setting speed mode (optional parameter)
		if(args.length>1)
			if(args[1].equals("fast"))
				fast = true;
		
		
		//setting user defined threshold threshold (optional parameter)
		if(args.length>2){
			try{
				double userDefinedThreshold = Double.valueOf(args[2]);
				if(userDefinedThreshold>=minProb&&userDefinedThreshold<=maxProb)
					threshold = userDefinedThreshold;
			}
			catch(Exception e){}
			
		}
		
		//now that thresholds are set, we can setup thresholds for fast decisions
		//cleanThreshold = threshold - speedThreshold;
		//double stegoThreshold = threshold + speedThreshold;
		
		
		//creating a file for csv output providing full steganalytic report (optional parameter)
		if(args.length>3)
			csvMode =true;
		if(csvMode){
			try {
			writer = new PrintWriter(args[3], "UTF-8");
			}
			catch (Exception e) {}
			writer.println();
			writer.println("File name,Above stego threshold?,Secret message size in bytes (ignore for clean files),Primary Sets,Chi Square,Sample Pairs,RS analysis,Fusion (mean)");
		}

		
		
		
		
		for (File file : listOfFiles) {
		    //reset all detectors
			ps = null;
			cs = null;
			sp = null;
			rs = null;
			fusion = null;
			fusionQ = null;
			if (file.isFile()) {
		    	BufferedImage image = ImageFileManager.loadImage(file);
		    	
		    	//routine (currently only for images)
		        if(image != null){
		        	fileSize = file.length();
		        	fileName=file.getName();
		        	
					stegExposeInput = new ArrayList<Double>();
		        	
		        	//computing primary set
					try{
						PrimarySets pso = new PrimarySets(image);
						pso.run();
						ps = steralize(pso.getResult());
						add(ps);
					}
					catch(Exception e){
					}
					
					
					//looking for fast break
					if(isClean()){
						continue;
					}
					
					
					//computing Sample Pairs average
					try{
						SamplePairs spo = new SamplePairs();
						sp = steralize((spo.doAnalysis(image, RED) + spo.doAnalysis(image, GREEN) + spo.doAnalysis(image, BLUE))/3);
						add(sp);
					}
					catch(Exception e){
					}
					
					
					//looking for fast break
					if(isClean()){
						continue;
					}
					
					
					//computing chi square attack
					try{
						int nbBlocks = ((3*image.getWidth()*image.getHeight())/csSize) - 1;
						double[] x = new double[nbBlocks];
						double[] chi = new double[nbBlocks];
						ChiSquare.chiSquareAttackTopToBottom(image, x, chi, csSize);
						double csQuant = 0;
						for(double csVal : chi)
							csQuant += csVal;
						cs = steralize(csQuant/chi.length);
						add(cs);
					}
					catch(Exception e){
						
					}
					
					//looking for fast break
					if(isClean()){
						continue;
					}
					
					
					//computing RS Analysis average
					try{
						RSAnalysis rso = new RSAnalysis(2,2);
						//RS analysis for overlapping groups
						double rsAverageOverlappingVal = (rso.doAnalysis(image, RED, true)[26] + rso.doAnalysis(image, GREEN, true)[26] + rso.doAnalysis(image, BLUE, true)[26])/3;
						//RS analysis for non-overlapping groups
						double rsAverageNonOverlappingVal = (rso.doAnalysis(image, RED, false)[26] + rso.doAnalysis(image, GREEN, false)[26] + rso.doAnalysis(image, BLUE, false)[26])/3;
						
						rs = steralize((rsAverageOverlappingVal+rsAverageNonOverlappingVal)/2);
						add(rs);
					}
					catch(Exception e){
					}
					printResults();
					
		        }
	        
		    }
		}
		if(csvMode)
			writer.close();
	}
	private static double steralize(double x){
		x=Math.abs(x);
		if(x>1)
			return 1;
		return x;
	}
	private static void printResults(){
		//setting up stegexpose and quantitative stegexpose detector
		fusion = Fuse.se(stegExposeInput);
		fusionQ = Math.round(Fuse.seQ(fusion, fileSize));
		//determine is a file is a stego or clean file
		boolean stego;
		if(fusion>threshold)
			stego = true;
		else
			stego = false;
		
		if(csvMode){
			writer.println(fileName+","+stego+","+fusionQ+","+ps+","+cs+","+sp+","+rs+","+fusion);
			writer.flush();
		}
		else
			if(stego)
				System.out.println(fileName + " is suspicious. Approximate amount of hidden data is "+fusionQ+" bytes.");
    
	}
	
	private static void add(Double x){
		if(x.isNaN()==false){
			stegExposeInput.add(x);
		}
	}
	

	/**
	 * used by fast mode to check if it is save to pass a file off as clean
	 * 
	 * @param abyte The byte to flip.
	 * @return The byte with the flipped LSB.
	 */
	private static boolean isClean(){
		if(fast){
			if(Fuse.se(stegExposeInput)<threshold){
				printResults();
				return true;
			}
				
			
		}
		return false;
			
	}
}