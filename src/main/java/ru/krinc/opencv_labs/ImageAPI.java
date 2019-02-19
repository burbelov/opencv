package ru.krinc.opencv_labs;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Arrays;
import java.util.Locale;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import ru.krinc.opencv_labs.utils.ConfigurationUtil;


/**
 *
 * @author maxburbelov
 */
public class ImageAPI {
    private static final Logger logger = LogManager.getLogger(ImageAPI.class);
    private final String destDirPath = "/home/maxburbelov/opencv_images/res/";
    private final String dirPath = "/home/maxburbelov/opencv_images/";


    public ImageAPI() throws Exception {
        logger.info("Checking OS.....");
        switch (getOperatingSystemType()) {            
            case LINUX:
                System.load(ConfigurationUtil.getConfigurationEntry(Constants.PATH_TO_NATIVE_LIB_LINUX));
                logger.info("OpenCV loaded, bitch");
                break;
            case WINDOWS:
                throw new Exception("Windows does not support!!!!!!!!");
            case MACOS:
                throw new Exception("Mac OS does not support!!!!!!!!");
             case OTHER:                
                 throw new Exception("Current OS does not support!!!!!");
             default:                
                 throw new Exception("Your OS does not support!!!");
        }
    }
    
    public Constants.OSType getOperatingSystemType() {        
        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            if ((OS.contains("mac")) || (OS.contains("darwin"))) {            
                return Constants.OSType.MACOS;
            } else if (OS.contains("win")) {            
                return Constants.OSType.WINDOWS;
            } else if (OS.contains("nux")) {            
                return Constants.OSType.LINUX;
            } else {            
                return Constants.OSType.OTHER;
            }
    }

     
    public void showImage(Mat m){    
        int type = BufferedImage.TYPE_BYTE_GRAY;    
        if (m.channels() > 1) {        
            type = BufferedImage.TYPE_3BYTE_BGR;    
        }    
        int bufferSize = m.channels()*m.cols()*m.rows();    
        byte [] b = new byte[bufferSize];    
        m.get(0, 0, b);     
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);    
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();    
        System.arraycopy(b, 0, targetPixels, 0, b.length);      
        ImageIcon icon = new ImageIcon(image);    
        JFrame frame = new JFrame();    
        frame.setLayout(new FlowLayout());            
        frame.setSize(image.getWidth(null) + 50, image.getHeight(null) + 50);         
        JLabel lbl = new JLabel();    
        lbl.setIcon(icon);    
        frame.add(lbl);    
        frame.setVisible(true);    
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public void morfologyTest() {        
        try {            
            String fileName = "numpl5.jpg";
            String prfName = "mrf_";            
            Mat src = Imgcodecs.imread(dirPath + fileName, Imgcodecs.IMREAD_COLOR);            
            Mat dst = src.clone();            
            Mat element_10 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10, 10));            
            Mat element_01 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 1));            
            Mat element_05 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
            
            Imgproc.erode(src, dst, element_10);            
            Imgcodecs.imwrite(destDirPath + prfName + "erode_10_" + fileName, dst);            
            showImage(dst);            
            
            dst = src.clone();
            Imgproc.erode(src, dst, element_01);            
            Imgcodecs.imwrite(destDirPath + prfName + "erode_01_" + fileName, dst);            
            showImage(dst);          
            
            dst = src.clone();            
            Imgproc.erode(src, dst, element_05);            
            Imgcodecs.imwrite(destDirPath + prfName + "erode_05_" + fileName, dst);            
            showImage(dst);        
            
            dst = src.clone();            
            Imgproc.dilate(src, dst, element_10);            
            Imgcodecs.imwrite(destDirPath + prfName + "dilate_10_" + fileName, dst);            
            showImage(dst);     
            
            dst = src.clone();            
            Imgproc.dilate(src, dst, element_01);            
            Imgcodecs.imwrite(destDirPath + prfName + "dilate_01_" + fileName, dst);            
            showImage(dst);            
            
            dst = src.clone();            
            Imgproc.dilate(src, dst, element_05);            
            Imgcodecs.imwrite(destDirPath + prfName + "dilate_05_" + fileName, dst);            
            showImage(dst);        
        } catch (Exception ex) {            
            logger.catching(ex);
        }    
    }
    
    public Mat zerosChannel(Mat srcImage, int channelNumber) {
        int totalBytes = (int) (srcImage.total() * srcImage.elemSize());
        byte buffer[] = new byte[totalBytes];
        srcImage.get(0, 0, buffer);
        for (int i = 0; i < totalBytes; i++) {
            if (i % channelNumber == 0) {
                buffer[i] = 0;
            }
        }
        srcImage.put(0, 0, buffer);
        return srcImage;
    }
    
    public static void main(String[] args) {
        
        if (System.getProperty(Constants.SYS_PROP_CONF_PATH) != null) {
            ConfigurationUtil.setConfigPath(System.getProperty(Constants.SYS_PROP_CONF_PATH));
        }
        
        ImageAPI imageAPI;
        try {
            imageAPI = new ImageAPI();
        } catch (Exception ex) {
            logger.catching(ex);
            return;
        }
        
        Mat srcImage = Imgcodecs.imread(imageAPI.dirPath + "numpl5.jpg");
        imageAPI.showImage(imageAPI.zerosChannel(srcImage, 3));
        
        imageAPI.morfologyTest();
    }
    
    
}
