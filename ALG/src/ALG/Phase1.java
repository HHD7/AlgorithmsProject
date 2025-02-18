package algoProject;import java.awt.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Phase1 {

    // حساب الطاقة للصورة
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

    // حساب طاقة البكسل بناءً على الجيران
    private static double calcPixelEnergy(BufferedImage image, int x, int y) {
        int width = image.getWidth();
        int height = image.getHeight();
        int left = (x > 0) ? image.getRGB(x - 1, y) : image.getRGB(x, y);
        int right = (x < width - 1) ? image.getRGB(x + 1, y) : image.getRGB(x, y);
        int up = (y > 0) ? image.getRGB(x, y - 1) : image.getRGB(x, y);
        int down = (y < height - 1) ? image.getRGB(x, y + 1) : image.getRGB(x, y);

        return Math.sqrt(gradient(left, right) + gradient(up, down));
    }

    // حساب التدرج بين البكسلات
    private static double gradient(int a, int b) {
        Color ca = new Color(a);
        Color cb = new Color(b);
        return Math.pow(ca.getRed() - cb.getRed(), 2) +
               Math.pow(ca.getGreen() - cb.getGreen(), 2) +
               Math.pow(ca.getBlue() - cb.getBlue(), 2);
    }

    // إيجاد السميز باستخدام البروت فورس
    public static int[] findSeamBruteForce(double[][] energy) {
        int height = energy.length;
        int width = energy[0].length;
        int[] seam = new int[height];
        
        // تحديد السميز في الصف الأول
        int minIndex = 0;
        for (int x = 1; x < width; x++) {
            if (energy[0][x] < energy[0][minIndex]) {
                minIndex = x;
            }
        }
        seam[0] = minIndex;

        // الانتقال عبر الصفوف واختيار الجار ذو أقل طاقة
        for (int y = 1; y < height; y++) {
            int prevX = seam[y - 1];
            minIndex = prevX;
            
            // اختيار الجار ذو الطاقة الأقل (يسار، وسط، يمين)
            if (prevX > 0 && energy[y][prevX - 1] < energy[y][minIndex]) {
                minIndex = prevX - 1;
            }
            if (prevX < width - 1 && energy[y][prevX + 1] < energy[y][minIndex]) {
                minIndex = prevX + 1;
            }
            
            seam[y] = minIndex;
        }

        return seam;
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
            // تحميل الصورة من المسار
            String imagePath = "./src/images/center.jpg";
            File inputFile = new File(imagePath);
            BufferedImage image = ImageIO.read(inputFile);

            int seamsToRemove = 50;
            for (int i = 0; i < seamsToRemove; i++) {
                // حساب الطاقة للصورة
                double[][] energy = calcEnergy(image);
                
                // إيجاد السميز باستخدام بروت فورس
                int[] seam = findSeamBruteForce(energy);
                
                // إزالة السميز من الصورة
                image = removeSeam(image, seam);
            }

            // حفظ الصورة الجديدة
            String outputImagePath = "/Users/samar43/Desktop/energy_cartoon.jpg";
            ImageIO.write(image, "jpg", new File(outputImagePath));

            System.out.println("✅ Seam carving completed. Image saved at: " + outputImagePath);
        } catch (IOException e) {
            System.out.println("❌ Error processing the image file.");
            e.printStackTrace();
        }
    }
}
