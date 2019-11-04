/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kojak.com.sample;

import java.io.IOException;

//import sun.misc.BASE64Encoder;
//import sun.misc.BASE64Decoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

public class ImageUtils {

    /**
     * Decode string to image
     *
     * @param imageString The string to decode
     * @return decoded image
     */
    public static BufferedImage decodeToImage(String imageString) {

        BufferedImage image = null;
        byte[] imageByte;
        try {
            Decoder decoder = Base64.getDecoder();
            imageByte = decoder.decode(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * Encode image to string
     *
     * @param image The image to encode
     * @param type jpeg, bmp, ...
     * @return encoded string
     */
    public static String encodeToString(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();

            Encoder decoder = Base64.getEncoder();
            imageString = decoder.encodeToString(imageBytes);
            imageString = imageString.replaceAll("(?:\\n|\\r)", ""); //Kojak device image has carriage returns

            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }

    public static byte[] encodeToBytes(BufferedImage image, String type) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] imageBytes = null;
        try {
            ImageIO.write(image, type, bos);
            imageBytes = bos.toByteArray();

            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageBytes;
    }

    public static void main(String args[]) throws IOException {
        System.out.println("Date:: "+(new java.util.Date().getYear()+1900));
        /* Test image to string and string to image start */
//        BufferedImage img = ImageIO.read(new File("D:\\Projects\\Equity\\KOJAK\\Kojak\\sample.bmp"));
//        BufferedImage newImg;
//        String imgstr;
//        imgstr = encodeToString(img, "bmp");
//        System.out.println(imgstr);
//        newImg = decodeToImage(imgstr);
//        ImageIO.write(newImg, "png", new File("files/img/CopyOfTestImage.png"));
        /* Test image to string and string to image finish */
    }
}
