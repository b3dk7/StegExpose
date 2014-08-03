package stegexpose;
//import fr.steganalysis;
import invisibleinktoolkit.benchmark.RSAnalysis;
import invisibleinktoolkit.benchmark.SamplePairs;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


//import fr.steganalysis.AverageLsb;
import fr.steganalysis.ChiSquare;
import fr.steganalysis.DifferenceHistogramAttack;
import fr.steganalysis.ImageFileManager;
import fr.steganalysis.PrimarySets;






public class RunStegExpose {
	
	//size of chi square blocks 
	private static int size = 1024;
	
	public static void main(String[] args){
		
		//obtaining all files to be steganalysed
		File folder = new File(args[0]);
		File[] listOfFiles = folder.listFiles();
		
		//create output file
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(args[1], "UTF-8");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		writer.println();
		writer.println("file name,file size,image width,image height,ps,dh,sp,rs,cs,spQuant,rsQuant,csQuant");
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	writer.print(file.getName()+","+file.length()+",");
		        
		    	BufferedImage image = ImageFileManager.loadImage(file);
		    	//image routine
		        if(image != null){
		        	
			    	//loading image from command line
					
			        //BufferedImage image;
			        //BufferedImage image = ImageFileManager.loadImage(file);
					writer.print(image.getWidth()+","+image.getHeight()+",");
					//computing primary set
					String psS;
					try{
						PrimarySets ps = new PrimarySets(image);
						ps.run();
						psS = Double.toString(ps.getResult());
					}
					catch(Exception e){
						psS = "fail";
					}
					
					//computing difference histrogram
					String dhS;
					try{
						DifferenceHistogramAttack dh = new DifferenceHistogramAttack(image);
						dh.run();
						dhS = Double.toString(dh.getResult());
					}
					catch(Exception e){
						dhS = "fail";
					}
					
					
					
					//computing chi-square distribution of PoV
					String csQuantS;
					String csAvgS;
					try{
						int nbBlocks = ((3*image.getWidth()*image.getHeight())/size) - 1;
						double[] x = new double[nbBlocks];
						double[] chi = new double[nbBlocks];
						ChiSquare.chiSquareAttackTopToBottom(image, x, chi, size);
						double csQuant = 0;
						for(double csVal : chi)
							csQuant += csVal;
						double csAvg = csQuant/chi.length;
						csQuantS = Double.toString(csQuant*size);
						csAvgS = Double.toString(csAvg);
						
					}
					catch(Exception e){
						csQuantS = "fail";
						csAvgS = "fail";
					}
					
					
					//computing Sample Pairs average
					String spAverageValS;
					String spAverageLengthS;
					try{
						SamplePairs sp = new SamplePairs();
						double spAverageVal = (sp.doAnalysis(image, 0) + sp.doAnalysis(image, 1) + sp.doAnalysis(image, 2))/3;
						double spAverageLength = ((image.getHeight() * image.getWidth() * 3)/8)* spAverageVal;
						spAverageValS=Double.toString(spAverageVal);
						spAverageLengthS=Double.toString(spAverageLength);
					}
					catch(Exception e){
						spAverageValS = "fail";
						spAverageLengthS = "fail";
					}
					
					
					
					//computing RS Analysis average
					String rsAverageValS;
					String rsAverageLengthS;
					try{
						RSAnalysis rsa = new RSAnalysis(2,2);
						
						//RS analysis for overlapping groups
						double rsAverageOverlappingVal = (rsa.doAnalysis(image, 0, true)[26] + rsa.doAnalysis(image, 1, true)[26] + rsa.doAnalysis(image, 2, true)[26])/3;
						double rsAverageOverlappingLength = (rsa.doAnalysis(image, 0, true)[27] + rsa.doAnalysis(image, 1, true)[27] + rsa.doAnalysis(image, 2, true)[27])/3;
						
						
						//RS analysis for non-overlapping groups
						double rsAverageNonOverlappingVal = (rsa.doAnalysis(image, 0, false)[26] + rsa.doAnalysis(image, 1, false)[26] + rsa.doAnalysis(image, 2, false)[26])/3;
						double rsAverageNonOverlappingLength = (rsa.doAnalysis(image, 0, false)[27] + rsa.doAnalysis(image, 1, false)[27] + rsa.doAnalysis(image, 2, false)[27])/3;
						
						
						rsAverageValS = Double.toString((rsAverageOverlappingVal+rsAverageNonOverlappingVal)/2);
						rsAverageLengthS = Double.toString((rsAverageOverlappingLength+rsAverageNonOverlappingLength)/2);
					}
					catch(Exception e){
						rsAverageValS = "fail";
						rsAverageLengthS = "fail";
					}
					
					//write detector results to file
					
					writer.println(psS+","+dhS+","+spAverageValS+","+rsAverageValS+","+csAvgS+","+spAverageLengthS+","+rsAverageLengthS+","+csQuantS);
					writer.flush();
					
		        }
	        
		    }
		}
		writer.close();
	}
}
