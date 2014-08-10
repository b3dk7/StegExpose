/*
 * On Steganalysis of Random LSB Embedding in Continuous-tone Images
 * 
 * source: http://code.google.com/p/simple-steganalysis-suite/
 */

package fr.steganalysis;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class PrimarySets {
	
	private BufferedImage image;
	private double result;
	
	public PrimarySets(BufferedImage image)
	{
		this.image = image;
	}
	
	public void run()
	{
		int width = image.getWidth();
		int height = image.getHeight();
		int red, green, blue;
		int red2, green2, blue2;
		
		int P=0, X=0, Y=0, V=0, W=0, Z=0;
		double a, b, c, delta, p1, p2;
		
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
					
					P++;
					
					// (v is even and u < v)  or  (v is odd and u > v)
					if((((red2&0x01) == 0) && (red < red2) ) || ( ((red2&0x01) == 1) && (red > red2)))
					{
						X++;
					}
					// (v is even and u > v)  or  (v is odd and u < v)
					else if((((red2&0x01) == 0) && (red > red2) ) || ( ((red2&0x01) == 1) && (red < red2)))
					{
						Y++;
						// (u even and v odd) or (u odd and v even)
						if((((red&0x01) == 0) && ((red2&0x01) == 1)) || (((red&0x01) == 1) && ((red2&0x0) == 0))) 
						{
							W++;
						}
						else
						{
							V++;
						}
					}
					else if(red == red2)
					{
						Z++;
					}
				}
			}
		}
		
		/*System.out.println("--> Y = V + W = "+Y);
		System.out.println("--> V = "+V);
		System.out.println("--> W = "+W);
		System.out.println("--> V + W = "+(V+W));
		System.out.println("X = "+X);
		System.out.println("Z = "+Z);
		System.out.println("P = X + Y + Z = "+P);
		System.out.println("--> X + Y + Z = "+(X+Y+Z));*/
		
		a = 0.5*(W+Z);
		b = (2*X)-P;
		c = Y-X;
		
		delta = Math.pow(b, 2)-(4*a*c);
		
		p1 = (-b+Math.sqrt(delta))/(2*a);
		p2 = (-b-Math.sqrt(delta))/(2*a);
		
		if(p1 < p2)
		{
			result = p1;
		}
		else
		{
			result = p2;
		}				
	}
	
	public double getResult()
	{
		return result;
	}
}
