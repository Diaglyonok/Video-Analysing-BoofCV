package boofcv.util;

import java.awt.image.BufferedImage;

public interface ImageProcessor {
	
	/**
	 * Initialize processor.  
	 * This will be called once when program starts up.
	 */
	void init();
	
	/**
	 * Transform one image into another.
	 * This method may be called many times.
	 */
	BufferedImage process(BufferedImage image);
	
	/**
	 * Change some value within the process.
	 * It is optional that the processor do anything with this.
	 * @param i Value, typically between 0 and 9.
	 */
	void setValue(int i);
}
