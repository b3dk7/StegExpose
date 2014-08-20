/*
 * The famous Chi-Square attack on PoV, described in :
 * Attacks on Steganographic Systems
 * Andreas Westfeld and Andreas Pfitzmann
 * 
 * Only works for sequential embedding 
 * 
 * More info :
 * http://www.guillermito2.net/stegano/tools/index.html
 * http://cuneytcaliskan.blogspot.fr/2011/12/steganalysis-chi-square-attack-lsb.html
 * 
 * source: http://code.google.com/p/simple-steganalysis-suite/
 * author: Bastien Faure
 */

package fr.steganalysis;
import java.awt.Color;
import java.awt.image.BufferedImage;

import org.apache.commons.math3.stat.inference.ChiSquareTest;


public class ChiSquare {
	
	public static void chiSquareAttackTopToBottom(BufferedImage image, double[] x, double[] chi, int size)
	{
		int width = image.getWidth();
		int height = image.getHeight();
		int block = 0;
		int nbBytes = 1;
		int red, green, blue;
		int[] values = new int[256];
		double[] expectedValues = new double[128];
		long[] pov = new long[128];
		
		for(int i=0; i<values.length; i++)
		{
			values[i] = 1;
			x[i] = i;
		}

		for(int j=0; j<height; j++)
		{
			for(int i=0; i<width; i++)
			{
				if(block < chi.length)
				{	
					red = (new Color(image.getRGB(i, j))).getRed();
					values[red]++;
					nbBytes++;
					if(nbBytes > size)
					{
						for(int k=0; k<expectedValues.length; k++)
						{
							expectedValues[k] = (values[2*k]+values[2*k+1])/2;
							pov[k] = values[2*k];
						}
						chi[block] = new ChiSquareTest().chiSquareTest(expectedValues, pov);
						block++;
						nbBytes = 1;
						/*for(int m=0; m<values.length; m++)
						{
							values[m] = 1;
						}*/
					}
				}
				
				if(block < chi.length)
				{
					green = (new Color(image.getRGB(i, j))).getGreen();
					values[green]++;
					nbBytes++;
					if(nbBytes > size)
					{
						for(int k=0; k<expectedValues.length; k++)
						{
							expectedValues[k] = (values[2*k]+values[2*k+1])/2;
							pov[k] = values[2*k];
						}
						chi[block] = new ChiSquareTest().chiSquareTest(expectedValues, pov);
						block++;
						nbBytes = 1;
						/*for(int m=0; m<values.length; m++)
						{
							values[m] = 1;
						}*/
					}
				}

				if(block < chi.length)
				{
					blue = (new Color(image.getRGB(i, j))).getBlue();
					values[blue]++;			
					nbBytes++;
					if(nbBytes > size)
					{
						for(int k=0; k<expectedValues.length; k++)
						{
							expectedValues[k] = (values[2*k]+values[2*k+1])/2;
							pov[k] = values[2*k];
						}
						chi[block] = new ChiSquareTest().chiSquareTest(expectedValues, pov);
						block++;
						nbBytes = 1;
						/*for(int m=0; m<values.length; m++)
						{
							values[m] = 1;
						}*/
					}
				}
			}
		}
	}
	
	public static void chiSquareAttackLeftToRight(BufferedImage image, double[] x, double[] chi, int size)
	{
		int width = image.getWidth();
		int height = image.getHeight();
		int block = 0;
		int nbBytes = 1;
		int red, green, blue;
		int[] values = new int[256];
		double[] expectedValues = new double[128];
		long[] pov = new long[128];
		
		for(int i=0; i<values.length; i++)
		{
			values[i] = 1;
			x[i] = i;
		}

		for(int i=0; i<width; i++)
		{
			for(int j=0; j<height; j++)
			{
				if(block < chi.length)
				{	
					red = (new Color(image.getRGB(i, j))).getRed();
					values[red]++;
					nbBytes++;
					if(nbBytes > size)
					{
						for(int k=0; k<expectedValues.length; k++)
						{
							expectedValues[k] = (values[2*k]+values[2*k+1])/2;
							pov[k] = values[2*k];
						}
						chi[block] = new ChiSquareTest().chiSquareTest(expectedValues, pov);
						block++;
						nbBytes = 1;
						/*for(int m=0; m<values.length; m++)
						{
							values[m] = 1;
						}*/
					}
				}
				
				if(block < chi.length)
				{
					green = (new Color(image.getRGB(i, j))).getGreen();
					values[green]++;
					nbBytes++;
					if(nbBytes > size)
					{
						for(int k=0; k<expectedValues.length; k++)
						{
							expectedValues[k] = (values[2*k]+values[2*k+1])/2;
							pov[k] = values[2*k];
						}
						chi[block] = new ChiSquareTest().chiSquareTest(expectedValues, pov);
						block++;
						nbBytes = 1;
						/*for(int m=0; m<values.length; m++)
						{
							values[m] = 1;
						}*/
					}
				}

				if(block < chi.length)
				{
					blue = (new Color(image.getRGB(i, j))).getBlue();
					values[blue]++;			
					nbBytes++;
					if(nbBytes > size)
					{
						for(int k=0; k<expectedValues.length; k++)
						{
							expectedValues[k] = (values[2*k]+values[2*k+1])/2;
							pov[k] = values[2*k];
						}
						chi[block] = new ChiSquareTest().chiSquareTest(expectedValues, pov);
						block++;
						nbBytes = 1;
						/*for(int m=0; m<values.length; m++)
						{
							values[m] = 1;
						}*/
					}
				}
			}
		}
	}
	
	public static void chiSquareAttackBottomToTop(BufferedImage image, double[] x, double[] chi, int size)
	{
		int width = image.getWidth();
		int height = image.getHeight();
		int block = 0;
		int nbBytes = 1;
		int red, green, blue;
		int[] values = new int[256];
		double[] expectedValues = new double[128];
		long[] pov = new long[128];
		
		for(int i=0; i<values.length; i++)
		{
			values[i] = 1;
			x[i] = i;
		}

		for(int j=height-1; j>=0; j--)
		{
			for(int i=width-1; i>=0; i--)
			{
				if(block < chi.length)
				{	
					red = (new Color(image.getRGB(i, j))).getRed();
					values[red]++;
					nbBytes++;
					if(nbBytes > size)
					{
						for(int k=0; k<expectedValues.length; k++)
						{
							expectedValues[k] = (values[2*k]+values[2*k+1])/2;
							pov[k] = values[2*k];
						}
						chi[block] = new ChiSquareTest().chiSquareTest(expectedValues, pov);
						block++;
						nbBytes = 1;
						/*for(int m=0; m<values.length; m++)
						{
							values[m] = 1;
						}*/
					}
				}
				
				if(block < chi.length)
				{
					green = (new Color(image.getRGB(i, j))).getGreen();
					values[green]++;
					nbBytes++;
					if(nbBytes > size)
					{
						for(int k=0; k<expectedValues.length; k++)
						{
							expectedValues[k] = (values[2*k]+values[2*k+1])/2;
							pov[k] = values[2*k];
						}
						chi[block] = new ChiSquareTest().chiSquareTest(expectedValues, pov);
						block++;
						nbBytes = 1;
						/*for(int m=0; m<values.length; m++)
						{
							values[m] = 1;
						}*/
					}
				}

				if(block < chi.length)
				{
					blue = (new Color(image.getRGB(i, j))).getBlue();
					values[blue]++;			
					nbBytes++;
					if(nbBytes > size)
					{
						for(int k=0; k<expectedValues.length; k++)
						{
							expectedValues[k] = (values[2*k]+values[2*k+1])/2;
							pov[k] = values[2*k];
						}
						chi[block] = new ChiSquareTest().chiSquareTest(expectedValues, pov);
						block++;
						nbBytes = 1;
						/*for(int m=0; m<values.length; m++)
						{
							values[m] = 1;
						}*/
					}
				}
			}
		}
	}
	
	public static void chiSquareAttackRightToLeft(BufferedImage image, double[] x, double[] chi, int size)
	{
		int width = image.getWidth();
		int height = image.getHeight();
		int block = 0;
		int nbBytes = 1;
		int red, green, blue;
		int[] values = new int[256];
		double[] expectedValues = new double[128];
		long[] pov = new long[128];
		
		for(int i=0; i<values.length; i++)
		{
			values[i] = 1;
			x[i] = i;
		}

		for(int i=width-1; i>=0; i--)
		{
			for(int j=0; j<height; j++)
			{
				if(block < chi.length)
				{	
					red = (new Color(image.getRGB(i, j))).getRed();
					values[red]++;
					nbBytes++;
					if(nbBytes > size)
					{
						for(int k=0; k<expectedValues.length; k++)
						{
							expectedValues[k] = (values[2*k]+values[2*k+1])/2;
							pov[k] = values[2*k];
						}
						chi[block] = new ChiSquareTest().chiSquareTest(expectedValues, pov);
						block++;
						nbBytes = 1;
						/*for(int m=0; m<values.length; m++)
						{
							values[m] = 1;
						}*/
					}
				}
				
				if(block < chi.length)
				{
					green = (new Color(image.getRGB(i, j))).getGreen();
					values[green]++;
					nbBytes++;
					if(nbBytes > size)
					{
						for(int k=0; k<expectedValues.length; k++)
						{
							expectedValues[k] = (values[2*k]+values[2*k+1])/2;
							pov[k] = values[2*k];
						}
						chi[block] = new ChiSquareTest().chiSquareTest(expectedValues, pov);
						block++;
						nbBytes = 1;
						/*for(int m=0; m<values.length; m++)
						{
							values[m] = 1;
						}*/
					}
				}

				if(block < chi.length)
				{
					blue = (new Color(image.getRGB(i, j))).getBlue();
					values[blue]++;			
					nbBytes++;
					if(nbBytes > size)
					{
						for(int k=0; k<expectedValues.length; k++)
						{
							expectedValues[k] = (values[2*k]+values[2*k+1])/2;
							pov[k] = values[2*k];
						}
						chi[block] = new ChiSquareTest().chiSquareTest(expectedValues, pov);
						block++;
						nbBytes = 1;
						/*for(int m=0; m<values.length; m++)
						{
							values[m] = 1;
						}*/
					}
				}
			}
		}
	}
}
