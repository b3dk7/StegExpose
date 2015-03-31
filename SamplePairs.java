/*
 *    Digital Invisible Ink Toolkit
 *    Copyright (C) 2005  K. Hempstalk	
 *    Original C++ Code is copyright to Zhe Wang et al, McMaster University.
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *
 *		@author Kathryn Hempstalk
 */


import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;


/**
 * Sample pairs analysis for an image.
 * <P>
 * Sample pairs analysis is a technique for detecting
 * steganography in an image.  More information can be found
 * in the paper "Detection of LSB steganography via Sample Pair analysis".
 * This implementation is based off some C++ code kindly provided by 
 * the authors of the paper.
 *
 * @author Kathryn Hempstalk
 */
public class SamplePairs extends PixelBenchmark{
	
	//CONSTRUCTORS
	
	/**
	 * Creates a new sample pairs analysis.
	 */
	public SamplePairs(){
		super();
	}
	
	
	//FUNCTIONS
	
	/**
	 * Does sample pairs analysis on an image.
	 *
	 * @param image The image to analyse.
	 */
	public double doAnalysis(BufferedImage image, int colour){
		
		//get the images sizes
		int imgx = image.getWidth(), imgy = image.getHeight();
		
		int startx = 0, starty = 0;
		int apair[] = new int[2];
		int u, v;
		long P,X,Y,Z;
		long W;
		
		P = X = Y = Z = W = 0;
		
		//pairs across the image
		for(starty = 0; starty < imgy; starty++){
			for(startx = 0; startx < imgx; startx = startx + 2){
				//get the block of data (2 pixels)
				apair[0] = image.getRGB(startx, starty);
				apair[1] = image.getRGB(startx + 1, starty);
				
				u = getPixelColour(apair[0], colour);
				v = getPixelColour(apair[1], colour);
				

				//if the 7 msb are the same, but the 1 lsb are different
				if( (u>>1 == v>>1) && ((v & 0x1) != (u & 0x1)))
					W++;
				//if the pixels are the same
				if( u == v )
					Z++;
				//if lsb(v) = 0 & u < v OR lsb(v) = 1 & u > v
				if( (v==(v>>1)<<1)&&(u<v) || (v!=(v>>1)<<1)&&(u>v) )
					X++;
				//vice versa
				if( (v==(v>>1)<<1)&&(u>v) || (v!=(v>>1)<<1)&&(u<v) )
					Y++;
				P++;
			}			
		}
		
		//pairs down the image
		for(starty = 0; starty < imgy; starty = starty + 2){
			for(startx = 0; startx < imgx; startx++){
				
				//get the block of data (2 pixels)
				apair[0] = image.getRGB(startx, starty);
				apair[1] = image.getRGB(startx, starty + 1);
				
				u = getPixelColour(apair[0], colour);
				v = getPixelColour(apair[1], colour);
				
				//if the 7 msb are the same, but the 1 lsb are different
				if( (u>>1 == v>>1) && ((v & 0x1) != (u & 0x1)))
					W++;
				//the pixels are the same
				if( u==v )
					Z++;
				//if lsb(v) = 0 & u < v OR lsb(v) = 1 & u > v
				if( (v==(v>>1)<<1)&&(u<v) || (v!=(v>>1)<<1)&&(u>v) )
					X++;
				//vice versa
				if( (v==(v>>1)<<1)&&(u>v) || (v!=(v>>1)<<1)&&(u<v) )
					Y++;
				P++;
			}			
		}
		
		//solve the quadratic equation
		//in the form ax^2 + bx + c = 0
		double a = 0.5 * ( W + Z );
		double b = 2 * X - P;
		double c = Y - X;		
		
		//the result
		double x;
		
		//straight line
		if(a == 0)
			x = c / b;
		
		//curve
		//take it as a curve
		double discriminant = Math.pow(b,2) - (4 * a * c);
		
		if(discriminant >= 0){
			double rootpos = ((-1 * b) + Math.sqrt(discriminant)) / (2 * a);
			double rootneg = ((-1 * b) - Math.sqrt(discriminant)) / (2 * a);
			
			//return the root with the smallest absolute value (as per paper)
			if(Math.abs(rootpos) <= Math.abs(rootneg))
				x = rootpos;
			else
				x = rootneg;
		}else{
			x = c / b;
		}
		
		if(x == 0){
			//let's assume straight lines again, something is probably wrong
			x = c / b;
		}
		
		return x;
	}
	
	
	
	
	
	/**
	 * Gets the given colour value for this pixel.
	 * 
	 * @param pixel The pixel to get the colour of.
	 * @param colour The colour to get.
	 * @return The colour value of the given colour in the given pixel.
	 */
	public int getPixelColour(int pixel, int colour){
		if(colour == RSAnalysis.ANALYSIS_COLOUR_RED)
			return getRed(pixel);
		else if (colour == RSAnalysis.ANALYSIS_COLOUR_GREEN)
			return getGreen(pixel);
		else if (colour == RSAnalysis.ANALYSIS_COLOUR_BLUE)
			return getBlue(pixel);
		else
			return 0;		
	}
	
	
	/*
	 * A small main method that will print out the message length
	 * in percent of pixels.
	 *
	 */
	public static void main(String[] args){
		if(args.length != 1){
			System.out.println("Usage: invisibleinktoolkit.benchmark.SamplePairs <imagefilename>");
			System.exit(1);
		}
		try{
			System.out.println("\nSample Pairs Results");
			System.out.println("--------------------");
			SamplePairs sp = new SamplePairs();
			BufferedImage image = ImageIO.read(new File(args[0]));
			double average = 0;
			double results = sp.doAnalysis(image, SamplePairs.ANALYSIS_COLOUR_RED);
			System.out.println("Result from red: " + results);
			average += results;
			results = sp.doAnalysis(image, SamplePairs.ANALYSIS_COLOUR_GREEN);
			System.out.println("Result from green: " + results);
			average += results;
			results = sp.doAnalysis(image, SamplePairs.ANALYSIS_COLOUR_BLUE);
			System.out.println("Result from blue: " + results);
			average += results;
			average = average/3;
			System.out.println("Average result: " + average);
			System.out.println();
		}catch(Exception e){
			System.out.println("ERROR: Cannot process that image type, please try another image.");
			e.printStackTrace();
		}
	}
	
	
	//VARIABLES
	
	
	/**
	 * Denotes analysis to be done with red.
	 */
	public static final int ANALYSIS_COLOUR_RED = 0;
	
	/**
	 * Denotes analysis to be done with green.
	 */
	public static final int ANALYSIS_COLOUR_GREEN = 1;
	
	/**
	 * Denotes analysis to be done with blue.
	 */
	public static final int ANALYSIS_COLOUR_BLUE = 2;
	
	
}//end of class
