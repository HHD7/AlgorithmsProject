package ALG;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
    
    //for checking
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


    public static int[] findSeamBruteForce(double[][] energy) {
    	
        int height = energy.length;
        int width = energy[0].length;
        
        int[] bestSeam = new int[height];
        double minEnergy = Double.POSITIVE_INFINITY;

        // Try every possible starting position in the first row
        for (int startX = 0; startX < width; startX++) {
            int[] seam = new int[height];
            double seamEnergy = findSeam(energy, startX, 0, seam);
            
         // if the current seam has lower energy than the best found so far update bestSeam
            if (seamEnergy < minEnergy) {
                minEnergy = seamEnergy;
                System.arraycopy(seam, 0, bestSeam, 0, height); 
            }
        }

        return bestSeam;
    }

    // Recursively finds the lowest-energy seam starting from (x, y)
    private static double findSeam(double[][] energy, int x, int y, int[] seam) {
        int height = energy.length;
        int width = energy[0].length;

        seam[y] = x; // Store current position

        // Base case: If we reached the last row, return its energy
        if (y == height - 1) {
            return energy[y][x];
        }

        // Recursively find the minimum-energy seam in the next row
        double minEnergy = Double.POSITIVE_INFINITY;

        for (int dx = -1; dx <= 1; dx++) { // Left, middle, right
            int nextX = x + dx;
            
            // ensure within bounds
            if (nextX >= 0 && nextX < width) {
                int[] tempSeam = new int[height]; // Temporary seam storage
                System.arraycopy(seam, 0, tempSeam, 0, height);

                double nextEnergy = findSeam(energy, nextX, y + 1, tempSeam); // Recursively find the seam energy for the next row

                if (nextEnergy < minEnergy) {
                    minEnergy = nextEnergy;
                    System.arraycopy(tempSeam, 0, seam, 0, height); // Store best seam found so far
                }
            }
        }

        return energy[y][x] + minEnergy;
    }

    // إزالة السميز من الصورة
    public static BufferedImage removeSeam(BufferedImage image, int[] seam) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage newImage = new BufferedImage(width - 1, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            int newX = 0;
            for (int x = 0; x < width; x++) {
                if (x != seam[y]) {
                    newImage.setRGB(newX, y, image.getRGB(x, y));
                    newX++;
                }
            }
        }
        return newImage;
    }

    public static void main(String[] args) {
        try {

        	String imagePath = "C:\\Users\\Hessa\\Downloads\\testimage1.jpg"; //load image
            File inputFile = new File(imagePath);
            BufferedImage image = ImageIO.read(inputFile);

            int seamsToRemove = 50;
            for (int i = 0; i < seamsToRemove; i++) {
            	
                double[][] energy = calcEnergy(image);

                int[] seam = findSeamBruteForce(energy);

                image = removeSeam(image, seam);
            }

            // Save the output image
            String outputImagePath = "C:\\Users\\Hessa\\Downloads\\newimage.jpg";
            ImageIO.write(image, "jpg", new File(outputImagePath));

            System.out.println("✅ Seam carving completed. Image saved at: " + outputImagePath);
            
        } catch (IOException e) {
            System.out.println("❌ Error processing the image file.");
            e.printStackTrace();
        }
    }
}