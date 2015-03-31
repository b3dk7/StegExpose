/*
 *    Digital Invisible Ink Toolkit
 *    Copyright (C) 2005  K. Hempstalk	
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
 *		@author Kathryn Hempstalk
 */


import java.awt.image.BufferedImage;
import java.util.Vector;
import java.util.Enumeration;
import javax.imageio.ImageIO;
import java.io.File;


/**
 * RS analysis for a stego-image.
 * <P>
 * RS analysis is a system for detecting LSB steganography proposed by
 * Dr. Fridrich at Binghamton University, NY.  You can visit her
 * webpage for more information - 
 * {@link http://www.ws.binghamton.edu/fridrich/} <BR>
 * Implemented as described in "Reliable detection of LSB steganography
 * in color and grayscale images" by J. Fridrich, M. Goljan and R. Du. 
 * <BR>
 * This code was produced with the aid of the authors and has been 
 * verified as a correct implementation of RS Analysis.  Their assistance
 * has proved invaluable.
 *
 * @author Kathryn Hempstalk
 */
public class RSAnalysis extends PixelBenchmark{
	
	//CONSTRUCTORS
	
	/**
	 * Creates a new RS analysis with a given mask size of m x n.
	 *
	 * Each alternating bit is set to 1.  Eg for a mask of size 2x2
	 * the resulting mask will be {1,0;0,1}.  Two masks are used - one is
	 * the inverse of the other.
	 *
	 * @param m The x mask size.
	 * @param n The y mask size.
	 */
	public RSAnalysis(int m, int n){
		//two masks
		mMask = new int[2][m * n];
		
		//iterate through them and set alternating bits
		int k = 0;
		for(int i = 0; i < n; i++){
			for(int j = 0; j < m; j++){
				if ( ((j % 2) == 0  && (i % 2) == 0 )
						|| ((j % 2) == 1 && (i % 2) == 1 )){
					mMask[0][k] = 1;
					mMask[1][k] = 0;
				}else{
					mMask[0][k] = 0;
					mMask[1][k] = 1;
				}
				k++;
			}
		}
		
		//set up the mask size.
		mM = m;
		mN = n;
	}
	
	
	//FUNCTIONS
	
	/**
	 * Does an RS analysis of a given image.  
	 * <P>
	 * The analysis data returned is specified by name in
	 * the getResultNames() method.
	 *
	 * @param image The image to analyse.
	 * @param colour The colour to analyse.
	 * @param overlap Whether the blocks should overlap or not.
	 * @return The analysis information.
	 */
	public double[] doAnalysis(BufferedImage image, int colour, boolean overlap){
		
		//get the images sizes
		int imgx = image.getWidth(), imgy = image.getHeight();
		
		int startx = 0, starty = 0;
		int block[] = new int[mM * mN];
		double numregular = 0, numsingular = 0;
		double numnegreg = 0, numnegsing = 0;
		double numunusable = 0, numnegunusable = 0;
		double variationB, variationP, variationN;
		
		while(startx < imgx && starty < imgy){
			//this is done once for each mask...
			for(int m = 0; m < 2; m++){
				//get the block of data	
				int k = 0;
				for(int i = 0; i < mN; i++){
					for(int j = 0; j < mM; j++){
						block[k] = image.getRGB(startx + j, starty + i);
						k++;
					}
				}
				
				//get the variation the block
				variationB = getVariation(block, colour);
				
				//now flip according to the mask
				block = flipBlock(block, mMask[m]);
				variationP = getVariation(block, colour);
				//flip it back
				block = flipBlock(block, mMask[m]);
				
				//negative mask
				mMask[m] = this.invertMask(mMask[m]);
				variationN = getNegativeVariation(block, colour, mMask[m]);
				mMask[m] = this.invertMask(mMask[m]);				
				
				//now we need to work out which group each belongs to
				
				//positive groupings
				if(variationP > variationB)
					numregular++;
				if(variationP < variationB)
					numsingular++;
				if(variationP == variationB)
					numunusable++;
				
				//negative mask groupings
				if(variationN > variationB)
					numnegreg++;
				if(variationN < variationB)
					numnegsing++;
				if(variationN == variationB)
					numnegunusable++;
				
				//now we keep going...
			}
			//get the next position
			if(overlap)
				startx += 1;
			else
				startx += mM;
			
			if(startx >= (imgx - 1)){
				startx = 0;
				if(overlap)
					starty += 1;
				else
					starty += mN;
			}
			if(starty >= (imgy - 1))
				break;
		}
		
		//get all the details needed to derive x...
		double totalgroups = numregular + numsingular + numunusable;
		double allpixels[] = this.getAllPixelFlips(image, colour, overlap);
		double x = getX(numregular, numnegreg, allpixels[0], allpixels[2],
				numsingular, numnegsing, allpixels[1], allpixels[3]);
		
		//calculate the estimated percent of flipped pixels and message length
		double epf, ml;
		if( 2 * (x - 1) == 0)
			epf = 0;
		else
			epf = Math.abs(x / (2 * (x - 1)));
		
		if(x - 0.5 == 0)
			ml = 0;
		else
			ml = Math.abs(x / (x - 0.5));
		
		//now we have the number of regular and singular groups...
		double results[] = new double[28];
		
		//save them all...
		
		//these results
		results[0] = numregular;
		results[1] = numsingular;
		results[2] = numnegreg;
		results[3] = numnegsing;
		results[4] = Math.abs(numregular - numnegreg);
		results[5] = Math.abs(numsingular - numnegsing);
		results[6] = (numregular / totalgroups) * 100;
		results[7] = (numsingular / totalgroups) * 100;
		results[8] = (numnegreg / totalgroups) * 100;
		results[9] = (numnegsing / totalgroups) * 100;
		results[10] = (results[4] / totalgroups) * 100;
		results[11] = (results[5] / totalgroups) * 100;
		
		//all pixel results
		results[12] = allpixels[0];
		results[13] = allpixels[1];
		results[14] = allpixels[2];
		results[15] = allpixels[3];
		results[16] = Math.abs(allpixels[0] - allpixels[1]);
		results[17] = Math.abs(allpixels[2] - allpixels[3]);
		results[18] = (allpixels[0] / totalgroups) * 100;
		results[19] = (allpixels[1] / totalgroups) * 100;
		results[20] = (allpixels[2] / totalgroups) * 100;
		results[21] = (allpixels[3] / totalgroups) * 100;
		results[22] = (results[16] / totalgroups) * 100;
		results[23] = (results[17] / totalgroups) * 100;
		
		//overall results
		results[24] = totalgroups;
		results[25] = epf;
		results[26] = ml;
		results[27] = ((imgx * imgy * 3) * ml) / 8;
		
		return results;
	}
	
	/**
	 * Gets the x value for the p=x(x/2) RS equation. See the paper for
	 * more details.
	 *
	 * @param r The value of Rm(p/2).
	 * @param rm The value of R-m(p/2).
	 * @param r1 The value of Rm(1-p/2).
	 * @param rm1 The value of R-m(1-p/2).
	 * @param s The value of Sm(p/2).
	 * @param sm The value of S-m(p/2).
	 * @param s1 The value of Sm(1-p/2).
	 * @param sm1 The value of S-m(1-p/2).
	 * @return The value of x.
	 */
	private double getX(double r, double rm, double r1, double rm1,
			double s, double sm, double s1, double sm1){
		
		double x = 0; //the cross point.
		
		double dzero = r - s; // d0 = Rm(p/2) - Sm(p/2)
		double dminuszero = rm - sm; // d-0 = R-m(p/2) - S-m(p/2)
		double done = r1 - s1; // d1 = Rm(1-p/2) - Sm(1-p/2)
		double dminusone = rm1 - sm1; // d-1 = R-m(1-p/2) - S-m(1-p/2)
		
		//get x as the root of the equation 
		//2(d1 + d0)x^2 + (d-0 - d-1 - d1 - 3d0)x + d0 - d-0 = 0
		//x = (-b +or- sqrt(b^2-4ac))/2a
		//where ax^2 + bx + c = 0 and this is the form of the equation
		
		//thanks to a good friend in Dunedin, NZ for helping with maths
		//and to Miroslav Goljan's fantastic Matlab code
		
		double a = 2 * (done + dzero);
		double b = dminuszero - dminusone - done - (3 * dzero);
		double c = dzero - dminuszero;
		
		if(a == 0)
		    //take it as a straight line
		    x = c / b;
		  		
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
			//maybe it's not the curve we think (straight line)
			double cr = (rm - r) / (r1 - r + rm - rm1);
			double cs = (sm - s) / (s1 -s + sm - sm1);
			x = (cr + cs) / 2;
		}
		
		if(x == 0){
			double ar = ((rm1 - r1 + r - rm) + (rm - r) / x) / (x - 1);
			double as = ((sm1 - s1 + s - sm) + (sm - s) / x) / (x - 1);
			if(as > 0 | ar < 0){
				//let's assume straight lines again...
				double cr = (rm - r) / (r1 - r + rm - rm1);
				double cs = (sm - s) / (s1 -s + sm - sm1);
				x = (cr + cs) / 2; 
			}
		}
		return x;
	}
	
	
	/**
	 * Gets the RS analysis results for flipping performed on all
	 * pixels.
	 *
	 * @param image The image to analyse.
	 * @param colour The colour to analyse.
	 * @param overlap Whether the blocks should overlap.
	 * @return The analysis information for all flipped pixels.
	 */
	private double[] getAllPixelFlips(BufferedImage image, int colour, boolean overlap){
		
		//setup the mask for everything...
		int[] allmask = new int[mM * mN];
		for(int i = 0; i < allmask.length; i++){
			allmask[i] = 1;
		}
		
		//now do the same as the doAnalysis() method
		
		//get the images sizes
		int imgx = image.getWidth(), imgy = image.getHeight();
		
		int startx = 0, starty = 0;
		int block[] = new int[mM * mN];
		double numregular = 0, numsingular = 0;
		double numnegreg = 0, numnegsing = 0;
		double numunusable = 0, numnegunusable = 0;
		double variationB, variationP, variationN;
		
		while(startx < imgx && starty < imgy){
			//done once for each mask
			for(int m = 0; m < 2; m++){
				//get the block of data
				int k = 0;
				for(int i = 0; i < mN; i++){
					for(int j = 0; j < mM; j++){
						block[k] = image.getRGB(startx + j, starty + i);
						k++;
					}
				}
				
				//flip all the pixels in the block (NOTE: THIS IS WHAT'S DIFFERENT
				//TO THE OTHER doAnalysis() METHOD)
				block = flipBlock(block, allmask);
				
				//get the variation the block
				variationB = getVariation(block, colour);
				
				//now flip according to the mask
				block = flipBlock(block, mMask[m]);
				variationP = getVariation(block, colour);
				//flip it back
				block = flipBlock(block, mMask[m]);
				
				//negative mask
				mMask[m] = this.invertMask(mMask[m]);
				variationN = getNegativeVariation(block, colour, mMask[m]);
				mMask[m] = this.invertMask(mMask[m]);
				
				//now we need to work out which group each belongs to
				
				//positive groupings
				if(variationP > variationB)
					numregular++;
				if(variationP < variationB)
					numsingular++;
				if(variationP == variationB)
					numunusable++;
				
				//negative mask groupings
				if(variationN > variationB)
					numnegreg++;
				if(variationN < variationB)
					numnegsing++;
				if(variationN == variationB)
					numnegunusable++;
				
				//now we keep going...
			}
			//get the next position
			if(overlap)
				startx += 1;
			else
				startx += mM;
			
			if(startx >= (imgx - 1)){
				startx = 0;
				if(overlap)
					starty += 1;
				else
					starty += mN;
			}
			if(starty >= (imgy - 1))
				break;
		}
		
		//save all the results (same order as before)
		double results[] = new double[4];
		
		results[0] = numregular;
		results[1] = numsingular;
		results[2] = numnegreg;
		results[3] = numnegsing;
		
		return results;
	}
	
	
	/**
	 * Returns an enumeration of all the result names.
	 *
	 * @return The names of all the results.
	 */
	public Enumeration getResultNames(){
		Vector names = new Vector(28);
		names.add("Number of regular groups (positive)");
		names.add("Number of singular groups (positive)");
		names.add("Number of regular groups (negative)");
		names.add("Number of singular groups (negative)");
		names.add("Difference for regular groups");
		names.add("Difference for singular groups");
		names.add("Percentage of regular groups (positive)");
		names.add("Percentage of singular groups (positive)");
		names.add("Percentage of regular groups (negative)");
		names.add("Percentage of singular groups (negative)");
		names.add("Difference for regular groups %");
		names.add("Difference for singular groups %");
		names.add("Number of regular groups (positive for all flipped)");
		names.add("Number of singular groups (positive for all flipped)");
		names.add("Number of regular groups (negative for all flipped)");
		names.add("Number of singular groups (negative for all flipped)");
		names.add("Difference for regular groups (all flipped)");
		names.add("Difference for singular groups (all flipped)");
		names.add("Percentage of regular groups (positive for all flipped)");
		names.add("Percentage of singular groups (positive for all flipped)");
		names.add("Percentage of regular groups (negative for all flipped)");
		names.add("Percentage of singular groups (negative for all flipped)");
		names.add("Difference for regular groups (all flipped) %");
		names.add("Difference for singular groups (all flipped) %");
		names.add("Total number of groups");
		names.add("Estimated percent of flipped pixels");
		names.add("Estimated message length (in percent of pixels)(p)");
		names.add("Estimated message length (in bytes)");
		return names.elements();
	}
	
	
	/**
	 * Gets the variation of the blocks of data. Uses
	 * the formula f(x) = |x0 - x1| + |x1 + x3| + |x3 - x2| + |x2 - x0|;
	 * However, if the block is not in the shape 2x2 or 4x1, this will be
	 * applied as many times as the block can be broken up into 4 (without
	 * overlaps).
	 *
	 * @param block The block of data (in 24 bit colour).
	 * @param colour The colour to get the variation of.
	 * @return The variation in the block.
	 */
	private double getVariation(int[] block, int colour){
		double var = 0;
		int colour1, colour2;
		for(int i = 0; i < block.length; i = i + 4){
			colour1 = getPixelColour(block[0 + i], colour);
			colour2 = getPixelColour(block[1 + i], colour);
			var += Math.abs(colour1 - colour2);
			colour1 = getPixelColour(block[3 + i], colour);
			colour2 = getPixelColour(block[2 + i], colour);
			var += Math.abs(colour1 - colour2);
			colour1 = getPixelColour(block[1 + i], colour);
			colour2 = getPixelColour(block[3 + i], colour);
			var += Math.abs(colour1 - colour2);
			colour1 = getPixelColour(block[2 + i], colour);
			colour2 = getPixelColour(block[0 + i], colour);
			var += Math.abs(colour1 - colour2);
		}
		return var;
	}
	
	
	/**
	 * Gets the negative variation of the blocks of data. Uses
	 * the formula f(x) = |x0 - x1| + |x1 + x3| + |x3 - x2| + |x2 - x0|;
	 * However, if the block is not in the shape 2x2 or 4x1, this will be
	 * applied as many times as the block can be broken up into 4 (without
	 * overlaps).
	 *
	 * @param block The block of data (in 24 bit colour).
	 * @param colour The colour to get the variation of.
	 * @param mask The negative mask.
	 * @return The variation in the block.
	 */
	private double getNegativeVariation(int[] block, int colour, int[] mask){
		double var = 0;
		int colour1, colour2;
		for(int i = 0; i < block.length; i = i + 4){
			colour1 = getPixelColour(block[0 + i], colour);
			colour2 = getPixelColour(block[1 + i], colour);
			if(mask[0 + i] == -1)
				colour1 = invertLSB(colour1);
			if(mask[1 + i] == -1)
				colour2 = invertLSB(colour2);
			var += Math.abs(colour1 - colour2);
			
			colour1 = getPixelColour(block[1 + i], colour);
			colour2 = getPixelColour(block[3 + i], colour);
			if(mask[1 + i] == -1)
				colour1 = invertLSB(colour1);
			if(mask[3 + i] == -1)
				colour2 = invertLSB(colour2);
			var += Math.abs(colour1 - colour2);
			
			colour1 = getPixelColour(block[3 + i], colour);
			colour2 = getPixelColour(block[2 + i], colour);
			if(mask[3 + i] == -1)
				colour1 = invertLSB(colour1);
			if(mask[2 + i] == -1)
				colour2 = invertLSB(colour2);
			var += Math.abs(colour1 - colour2);
			
			colour1 = getPixelColour(block[2 + i], colour);
			colour2 = getPixelColour(block[0 + i], colour);
			if(mask[2 + i] == -1)
				colour1 = invertLSB(colour1);
			if(mask[0 + i] == -1)
				colour2 = invertLSB(colour2);
			var += Math.abs(colour1 - colour2);
		}
		return var;
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
	
	
	/**
	 * Flips a block of pixels.
	 *
	 * @param block The block to flip.
	 * @param mask The mask to use for flipping.
	 * @return The flipped block.
	 */
	private int[] flipBlock(int[] block, int[] mask){
		//if the mask is true, negate every LSB
		for(int i = 0; i < block.length; i++){
			if( (mask[i] == 1)){
				//get the colour
				int red = getRed(block[i]), green = getGreen(block[i]),
				blue = getBlue(block[i]);
				
				//negate their LSBs
				red = negateLSB(red);
				green = negateLSB(green);
				blue = negateLSB(blue);
				
				//build a new pixel
				int newpixel = (0xff << 24) | ((red  & 0xff) << 16)
				| ((green & 0xff) << 8) | ((blue & 0xff));
				
				//change the block pixel
				block[i] = newpixel;
			}else if (mask[i] == -1){
				//get the colour
				int red = getRed(block[i]), green = getGreen(block[i]),
				blue = getBlue(block[i]);
				
				//negate their LSBs
				red = invertLSB(red);
				green = invertLSB(green);
				blue = invertLSB(blue);
				
				//build a new pixel
				int newpixel = (0xff << 24) | ((red  & 0xff) << 16)
				| ((green & 0xff) << 8) | ((blue & 0xff));
				
				//change the block pixel
				block[i] = newpixel;
			}
		}
		return block;
	}
	
	
	/**
	 * Negates the LSB of a given byte (stored in an int).
	 *
	 * @param abyte The byte to negate the LSB of.
	 * @return The byte with negated LSB.
	 */
	private int negateLSB(int abyte){
		int temp = abyte & 0xfe;
		if(temp == abyte)
			return abyte | 0x1;
		else
			return temp;
	}
	
	
	/**
	 * Inverts the LSB of a given byte (stored in an int).
	 * 
	 * @param abyte The byte to flip.
	 * @return The byte with the flipped LSB.
	 */
	private int invertLSB(int abyte){
		if(abyte == 255)
			return 256;
		if(abyte == 256)
			return 255;
		return (negateLSB(abyte + 1) - 1);
	}
	
	
	/**
	 * Inverts a mask.
	 *
	 * @param mask The mask to invert.
	 * @return The flipped mask.
	 */
	private int[] invertMask(int[] mask){
		for(int i = 0; i < mask.length; i++){
			mask[i] = mask[i] * -1;
		}
		return mask;
	}
	
	
	/**
	 * A small main method that will print out the message length
	 * in percent of pixels.
	 *
	 */
	public static void main(String[] args){
		if(args.length != 1){
			System.out.println("Usage: invisibleinktoolkit.benchmark.RSAnalysis <imagefilename>");
			System.exit(1);
		}
		try{
			System.out.println("\nRS Analysis results");
			System.out.println("-------------------");
			RSAnalysis rsa = new RSAnalysis(2,2);
			BufferedImage image = ImageIO.read(new File(args[0]));
			double average = 0;
			double[] results = rsa.doAnalysis(image, RSAnalysis.ANALYSIS_COLOUR_RED, true);
			System.out.println("Result from red: " + results[26]);
			average += results[26];
			results = rsa.doAnalysis(image, RSAnalysis.ANALYSIS_COLOUR_GREEN, true);
			System.out.println("Result from green: " + results[26]);
			average += results[26];
			results = rsa.doAnalysis(image, RSAnalysis.ANALYSIS_COLOUR_BLUE, true);
			System.out.println("Result from blue: " + results[26]);
			average += results[26];
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
	
	/**
	 * The mask to be used for the pixel groups.
	 */
	private int[][] mMask;
	
	/**
	 * The x length of the mask.
	 */
	private int mM;
	
	/**
	 * The y length of the mask.
	 */
	private int mN;
	
}
//end of class
