package boofcv.util;

import boofcv.gui.image.ImagePanel;
import boofcv.gui.image.ShowImages;
import boofcv.io.webcamcapture.UtilWebcamCapture;
import com.github.sarxos.webcam.Webcam;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;


public class VideoApp {

	private String deviceName;
	final private int width;
	final private int height;
	private Webcam webcam = null;
	private ImagePanel gui = null;
	private JFrame guiWindow = null;
	private Character keyPressed = null;
	private final ImageProcessor imageProcessor;


	public VideoApp(ImageProcessor processor, String deviceName, int width, int height) {
		this.imageProcessor = processor;
		this.deviceName = deviceName;
		this.width = width;
		this.height = height;
	}

	public VideoApp(ImageProcessor processor, int width, int height) {
		this.imageProcessor = processor;
		this.width = width;
		this.height = height;
	}

	public void runVideo(File input) throws FrameGrabber.Exception {

		FFmpegFrameGrabber g = new FFmpegFrameGrabber(input);
		Java2DFrameConverter jfc = new Java2DFrameConverter();

		initVideo();
		g.start();

		gui = new ImagePanel();
		gui.setPreferredSize(new Dimension(g.getImageWidth(), g.getImageHeight()));

		guiWindow = ShowImages.showWindow(gui, "Title", true);
		guiWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		for (int i = 0; i < 50000; i++) {
			BufferedImage image = jfc.convert(g.grab());
			image = imageProcessor.process(image);
			gui.setBufferedImageSafe(image);
		}
		g.stop();
		guiWindow.removeAll();
	}

	public void run() {
		init();

		gui = new ImagePanel();
		gui.setPreferredSize(webcam != null ? webcam.getViewSize() : (new Dimension(width, height)));

		guiWindow = ShowImages.showWindow(gui, getTitle(), true);
		guiWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		long time = System.currentTimeMillis() + 1000L;
		boolean running = true;
		while (running) {
			BufferedImage image = readImage();
			if (image == null) {
				break;
			}

			if (keyPressed == null) {
				int lastDigitPressed = 5;
				imageProcessor.setValue(lastDigitPressed);
				image = imageProcessor.process(image);
			}
			gui.setBufferedImageSafe(image);
			if (System.currentTimeMillis() > time) {
				time = System.currentTimeMillis() + 1000L;
			}
		}
	}

	private void init() {
		webcam = UtilWebcamCapture.openDevice(deviceName, width, height);
		imageProcessor.init();
	}

	private void initVideo() {
		imageProcessor.init();
	}

	private BufferedImage readImage() {
		return webcam != null ? webcam.getImage() : null;
	}

	private String getTitle() {
		return deviceName;
	}

}
