import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

public class Halles {

    static ArrayList<String> imageFolderUrls = new ArrayList<String>();
    static String destinationFolder = "img/";
    static String fileType = "png";
    static String fileFormat = "%d-%d." + fileType;

    public static void main(String[] args) throws Exception {

        String stairs[] = {"0", "-1", "-2", "-3", "-4"};

        for (String stair : stairs) {

            imageFolderUrls.clear();
            imageFolderUrls.add("https://mapmanager-unibail.visioglobe.com/public/956d0783fcf40934/content/tiles/map.f" + stair + "l2/5/");

            destinationFolder = "img" + stair + "/";
            File destination = new File(destinationFolder);
            destination.mkdirs();

            int rows = 30; // we assume the no. of rows and cols are known and each chunk has equal width and height
            int cols = 30;

            /* Download images */
            
            for (String imageFolderUrl : imageFolderUrls) {
                for (int i = 0; i <= rows; i++) {
                    for (int j = 0; j <= cols; j++) {
                        String nameFile = String.format(fileFormat, i, j);
                        try {
                            saveImage(imageFolderUrl + nameFile, destinationFolder + nameFile);
                        } catch (Exception e) {
                            System.out.println(nameFile + " does not exist");
                        }
                    }
                }
            }

            /* Merge images together */
            
            int chunkWidth, chunkHeight;
            int type;
            // fetching image files
            File[][] imgFiles = new File[rows][cols];
            for (int i = 0; i < rows; i++)
                for (int j = 0; j < cols; j++)
                    imgFiles[i][j] = new File(destinationFolder + String.format(fileFormat, i, j));

            // creating a bufferd image array from image files
            int correctRow = -1;
            int correctColumn = -1;
            BufferedImage[][] buffImages = new BufferedImage[rows][cols];
            for (int i = 0; i < rows; i++)
                for (int j = 0; j < cols; j++)
                    if (imgFiles[i][j].exists()) {
                        buffImages[i][j] = ImageIO.read(imgFiles[i][j]);
                        if (buffImages[i][j].getWidth() > 10 && correctRow == -1) {
                            correctRow = i;
                            correctColumn = j;
                        }
                    }
            if (correctRow != -1) {
                type = buffImages[correctRow][correctColumn].getType();
                chunkWidth = buffImages[correctRow][correctColumn].getWidth();
                chunkHeight = buffImages[correctRow][correctColumn].getHeight();

                // Initializing the final image
                BufferedImage finalImg = new BufferedImage(chunkWidth * cols, chunkHeight * rows, type);

                for (int i = 0; i < rows; i++)
                    for (int j = 0; j < cols; j++)
                        if (imgFiles[i][j].exists())
                            finalImg.createGraphics().drawImage(buffImages[i][j], chunkWidth * i, chunkHeight * j, null);

                System.out.println("Image concatenated.....");
                ImageIO.write(finalImg, fileType, new File("stair " + stair + "." + fileType));
            }
        }
    }

    public static void saveImage(String imageUrl, String destinationFile) throws IOException {
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(destinationFile);

        byte[] b = new byte[2048];
        int length;

        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }

        is.close();
        os.close();
    }
}
