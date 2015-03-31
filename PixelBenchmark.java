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
 *    
 *		@author Kathryn Hempstalk
 */


/**
 * A convience class to provide all the base methods
 * for benchmarkers to use.
 *
 * Provides pixel functions commonly used in benchmarking.
 *
 * @author Kathryn Hempstalk.
 */
public class PixelBenchmark{
	
	/**
	 * Gets the red content of a pixel.
	 *
	 * @param pixel The pixel to get the red content of.
	 * @return The red content of the pixel.
	 */
	public int getRed(int pixel){
		return ((pixel >> 16) & 0xff);
	}
	
	/**
	 * Gets the green content of a pixel.
	 *
	 * @param pixel The pixel to get the green content of.
	 * @return The green content of the pixel.
	 */
	public int getGreen(int pixel){
		return ((pixel >> 8) & 0xff);
	}
	
	/**
	 * Gets the blue content of a pixel.
	 *
	 * @param pixel The pixel to get the blue content of.
	 * @return The blue content of the pixel.
	 */
	public int getBlue(int pixel){
		return (pixel & 0xff);
	}
	
}
//end of class.				   
