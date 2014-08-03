/*
 * RELIABLE DETECTION OF LSB STEGANOGRAPHY BASED ON THE DIFFERENCE IMAGE HISTOGRAM
 * 2003
 * 
 * ABSTRACT 
 * A new steganalytic technique based on the difference image histogram aimed at LSB steganography is proposed. 
 * Translation coefficients between difference image histograms are defined as a measure of the weak correlation 
 * between the least significant bit (LSB) plane and the remaining bit planes, and then used to construct a 
 * classifier to discriminate the stego-image from the carrier-image. The algorithm can not only detect the existence 
 * of hidden messages embedded using sequential or random LSB replacement in images reliably, but also estimate 
 * the amount of hidden messages exactly. Experimental results show that for raw lossless compressed images the new 
 * algorithm has a better performance than the RS analysis method and improves the computation speed significantly.
 * 
 * http://mips.changwon.ac.kr/research/sem_data/Reliable_Detection_of_LSB_Steganography_Based.pdf
 * 
 * My implementation does not seem to work properly
 * The resulting p-value seems strange
 * Need to be modified
 */

package fr.steganalysis;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class DifferenceHistogramAttack {
	
	private BufferedImage image;
	private int width;
	private int height;
	
	private int[] g = new int[7]; //histogram g from -3 to +3
	private int[] h = new int[7]; //histogram h from -3 to +3
	
	private double result;
	
	public DifferenceHistogramAttack(BufferedImage image)
	{
		this.image = image;
		this.width = image.getWidth();
		this.height = image.getHeight();
	}
	
	/*
	 * Compute Difference Histograms
	 */
	private void computeHistograms()
	{
		int[] g2 = new int[511]; //histogram g from -255 to +255
		int[] h2 = new int[511]; //histogram h from -255 to +255
		int red, green, blue;
		int red2, green2, blue2;
		int redDiff, greenDiff, blueDiff;
		
		//Initialize
		for(int i=0; i<h2.length; i++)
		{
			g2[i] = 0;
			h2[i] = 0;
		}
		
		for(int j=0; j<height; j++)
		{
			for(int i=0; i<width; i+=2)
			{
				if( (i+1) < width)
				{
					red = new Color(image.getRGB(i,j)).getRed();
					/*green = new Color(image.getRGB(i,j)).getGreen();
					blue = new Color(image.getRGB(i,j)).getBlue();*/
					
					red2 = new Color(image.getRGB(i+1,j)).getRed();
					/*green2 = new Color(image.getRGB(i+1,j)).getGreen();
					blue2 = new Color(image.getRGB(i+1,j)).getBlue();*/
					
					redDiff = red - red2;
					/*greenDiff = green - green2;
					blueDiff = blue - blue2;*/
					
					h2[redDiff+255]++;
					/*h2[greenDiff+255]++;
					h2[blueDiff+255]++;*/
					
					red = new Color(image.getRGB(i,j)).getRed() & 0xFE;
					/*reen = new Color(image.getRGB(i,j)).getGreen() & 0xFE;
					blue = new Color(image.getRGB(i,j)).getBlue() & 0xFE;*/
					
					red2 = new Color(image.getRGB(i+1,j)).getRed() & 0xFE;
					/*green2 = new Color(image.getRGB(i+1,j)).getGreen() & 0xFE;
					blue2 = new Color(image.getRGB(i+1,j)).getBlue() & 0xFE;*/
					
					redDiff = red - red2;
					/*greenDiff = green - green2;
					blueDiff = blue - blue2;*/
					
					g2[redDiff+255]++;
					/*g2[greenDiff+255]++;
					g2[blueDiff+255]++;*/
					
				}
			}
		}

		//Preprocess histograms
		h[0] = h2[255];
		g[0] = g2[255];
		for(int i=1; i<7; i++)
		{
			h[i] = (h2[255+i] + h2[255-i])/2;
			g[i] = (g2[255+i] + g2[255-i])/2;
		}
		
	}

	/*
	 * Calculate p value
	 */
	private void computeCoefficients()
	{
		double p1, p2;
		double result0, result1, result2;
		double a, b, c;
		double d1, d2, d3;
		double alpha, beta, gamma, delta;
		double a01, a21, a22, a23, a43, a44, a45, a65, a66, a67;
		
		a01 = ((double)g[0] - (double)h[0])/(2.0*g[0]);
		a21 = ((double)h[1] - a01*g[0])/(double)g[2];
		a22 = (double)h[2]/g[2];
		a23 = 1.0 - a22 - a21;
		a43 = ((double)h[3] - a23*g[2])/(double)g[4];
		a44 = (double)h[4]/g[4];
		a45 = 1 - a44 - a43;
		a65 = ((double)h[5] - a45*g[4])/(double)g[6];
		a66 = (double)h[6]/g[6];
		a67 = 1 - a66 - a65;

		//Result for i=0
		alpha = (double)a21/a01;
		beta = (double)a23/a01;
		gamma = (double)g[0]/g[2];
		
		d1 = 1 - alpha;
		d2 = alpha - gamma;
		d3 = beta - gamma;
		
		a = 2*d1;
		b = d3-(4*d1)-d2;
		c = 2*d2;
		
		delta = Math.pow(b, 2) - (4*a*c);
		if(delta < 0)
		{
			result0 = 1;
		}
		else if(delta == 0)
		{
			result0 = (double)-b/(2*a);
		}
		else
		{
			p1 = (double)(-b+Math.sqrt(delta))/(2*a);
			p2 = (double)(-b-Math.sqrt(delta))/(2*a);
			
			if(Math.abs(p1) < Math.abs(p2))
			{
				result0 = p1;
			}
			else
			{
				result0 = p2;
			}
		}
		
		//Result for i=1
		alpha = (double)a43/a23;
		beta = (double)a45/a21;
		gamma = (double)g[2]/g[4];
		
		d1 = 1 - alpha;
		d2 = alpha - gamma;
		d3 = beta - gamma;
		
		a = 2*d1;
		b = d3-(4*d1)-d2;
		c = 2*d2;
		
		delta = Math.pow(b, 2) - (4*a*c);
		
		if(delta < 0)
		{
			result1 = 1;
		}
		else if(delta == 0)
		{
			result1 = (double)-b/(2*a);
		}
		else
		{
			p1 = (double)(-b+Math.sqrt(delta))/(2*a);
			p2 = (double)(-b-Math.sqrt(delta))/(2*a);
			
			if(Math.abs(p1) < Math.abs(p2))
			{
				result1 = p1;
			}
			else
			{
				result1 = p2;
			}
		}
		
		
		//Result for i=2
		alpha = (double)a65/a45;
		beta = (double)a67/a23;
		gamma = (double)g[4]/g[6];
		
		d1 = 1 - alpha;
		d2 = alpha - gamma;
		d3 = beta - gamma;
		
		a = 2*d1;
		b = d3-(4*d1)-d2;
		c = 2*d2;
		
		delta = Math.pow(b, 2) - (4*a*c);
		
		if(delta < 0)
		{
			result2 = 1;
		}
		else if(delta == 0)
		{
			result2 = (double)-b/(2*a);
		}
		else
		{
			p1 = (double)(-b+Math.sqrt(delta))/(2*a);
			p2 = (double)(-b-Math.sqrt(delta))/(2*a);
			
			if(Math.abs(p1) < Math.abs(p2))
			{
				result2 = p1;
			}
			else
			{
				result2 = p2;
			}
		}
		
		//Final result
		result = (double)(result0 + result1 + result2)/3.0;
	}
	
	public double getResult()
	{
		return result;
	}
	
	public void run()
	{
		computeHistograms();
		computeCoefficients();
	}
}
