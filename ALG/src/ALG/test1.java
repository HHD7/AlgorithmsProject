package ALG;

import java.awt.Color;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;

public class test1 {
	
	
	public static double[][] calcEnergy(BufferedImage image) {
		
		 int width = image.getWidth();
	     int height = image.getHeight();
	     
	     double[][] energy = new double[height][width];
	     
	     for (int y = 0; y < height; y++) {
	            for (int x = 0; x < width; x++) {
	                energy[y][x] = calcPixelEnergy(image, x, y);
	            }
	        }
	     
	     return energy;
        
	}
	 
	private static double calcPixelEnergy(BufferedImage image, int x, int y) {
		
		int[][] brightness = new int[3][3];
		
		
		for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
            	
            	int newX = x + i;
            	int newY = y + j;
            	
            	
            	//out of bounds assume brightness = 0
            	 if (newX < 0 || newX >= image.getWidth() || newY < 0 || newY >= image.getHeight()) {
                     brightness[i + 1][j + 1] = 0;
                 } else {

                Color color = new Color(image.getRGB(newX, newY));
                brightness[i + 1][j + 1] = color.getRed() + color.getGreen() + color.getBlue(); //sum of RGB values
                 }
            }
        }
		
		//energy formula
		int xEnergy = brightness[0][0] + 2 * brightness[1][0] + brightness[2][0] - brightness[0][2] - 2 * brightness[1][2] - brightness[2][2];
		int yEnergy = brightness[0][0] + 2 * brightness[0][1] + brightness[0][2] - brightness[2][0] - 2 * brightness[2][1] - brightness[2][2];
		
		double pixelEnergy = Math.sqrt(xEnergy * xEnergy + yEnergy * yEnergy);
		
		return pixelEnergy;
	}
	
	
	public static BufferedImage generateEnergyImage(double[][] energy) {
		
		int height = energy.length;
        int width = energy[0].length;
        
        BufferedImage energyImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        double minEnergy = Double.MAX_VALUE;
        double maxEnergy = Double.MIN_VALUE;
        
       //find min and max energy
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                minEnergy = Math.min(minEnergy, energy[y][x]);
                maxEnergy = Math.max(maxEnergy, energy[y][x]);
            }
        }
        
        // normalize energy -> scale to RGB -> grayscale
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
            	
                double normalizedEnergy = (energy[y][x] - minEnergy) / (maxEnergy - minEnergy); //normalize between 0 and 1
               
                int intensity = (int) (normalizedEnergy * 255); // 0-255 for RGB
                
                Color color = new Color(intensity, intensity, intensity); // grayscale
           
                energyImage.setRGB(x, y, color.getRGB());
            }
        }

        return energyImage;

	}
	

	public static void main(String[] args) {
		
		try {
           
            File inputFile = new File("C:\\Users\\Hessa\\Downloads\\testimage1.jpg"); //image path
            BufferedImage image = ImageIO.read(inputFile);
 
            double[][] energy = calcEnergy(image);

            /* print the energy map 
              System.out.println("Energy Map:");
            for (int y = 0; y < energy.length; y++) {
                for (int x = 0; x < energy[y].length; x++) {
                    System.out.print(energy[y][x] + " ");
                }
                System.out.println();
            } */
            
            BufferedImage image2 = generateEnergyImage(energy);
            ImageIO.write(image2, "jpg", new File("C:\\Users\\Hessa\\Downloads\\energy_image.jpg"));
            
		} catch (IOException e) {
            e.printStackTrace();
        }
		
	}

}
