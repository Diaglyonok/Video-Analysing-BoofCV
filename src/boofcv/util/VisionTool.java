package boofcv.util;

import boofcv.alg.feature.detect.edge.CannyEdge;
import boofcv.alg.feature.detect.edge.EdgeContour;
import boofcv.alg.feature.detect.edge.EdgeSegment;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.shapes.ShapeFittingOps;
import boofcv.factory.feature.detect.edge.FactoryEdgeDetectors;
import boofcv.gui.feature.VisualizeShapes;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.ConnectRule;
import boofcv.struct.PointIndex_I32;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayU8;
import georegression.struct.shapes.Rectangle2D_I32;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class VisionTool implements ImageProcessor {

	@Override
	public void init() { }
	
	@Override
	public void setValue(int i) { }

	@Override
	public BufferedImage process(BufferedImage image) {
		if (image==null) { return null; }
		
		GrayF32 gray = ConvertBufferedImage.convertFrom(image, (GrayF32) null);

		Graphics2D g2 = image.createGraphics();

		fitCannyEdges(g2, gray);
		fitCannyBinary(g2, gray);
		
		return image;
	}

	private static double splitFraction = 0.05;
	private static double minimumSideFraction = 0.1;

	private static void fitCannyBinary(Graphics2D g2, GrayF32 input) {
		GrayU8 binary = new GrayU8(input.width, input.height);

		// Finds edges inside the image
		CannyEdge<GrayF32, GrayF32> canny = FactoryEdgeDetectors.canny(2, false, true, GrayF32.class, GrayF32.class);

		canny.process(input, 0.1f, 0.3f, binary);

		List<Contour> contours = BinaryImageOps.contour(binary, ConnectRule.EIGHT, null);
		g2.setStroke(new BasicStroke(2));

		// used to select colors for each line
		for (Contour c : contours) {
			// Only the external contours are relevant.
			List<PointIndex_I32> vertexes = ShapeFittingOps.fitPolygon(c.external, true, splitFraction,
					minimumSideFraction, 100);

			// Target should have 6 sides
			if (vertexes.size() < 6) {
				continue;
			}

			int xpasses = 0;
			int ypasses = 0;
			int minx = vertexes.get(0).x;
			int miny = vertexes.get(0).y;
			int maxx = vertexes.get(0).x;
			int maxy = vertexes.get(0).y;
			for (int i = 1; i < vertexes.size(); i++) {
				int x = vertexes.get(i).x;
				int y = vertexes.get(i).y;

				if (x > maxx) {
					maxx = x;
					xpasses++;
				}
				if (y > maxy) {
					maxy = y;
					ypasses++;
				}
				if (x < minx) {
					minx = x;
					xpasses++;
				}
				if (y < miny) {
					miny = y;
					ypasses++;
				}
			}
			if (xpasses > 2 || ypasses > 2)
				continue;

			g2.setColor(new Color(1.f, 0.f, 1.f));
			VisualizeShapes.drawPolygon(vertexes, true, g2);
			g2.setColor(new Color(0.f, 0.f, 1.f));
			VisualizeShapes.drawRectangle(new Rectangle2D_I32(minx, miny, maxx, maxy), g2);
		}
	}


	public static void fitCannyEdges(Graphics2D g2, GrayF32 input) {

		// Finds edges inside the image
		CannyEdge<GrayF32, GrayF32> canny = FactoryEdgeDetectors.canny(2, true, true, GrayF32.class, GrayF32.class);

		canny.process(input, 0.1f, 0.3f, null);
		List<EdgeContour> contours = canny.getContours();

		g2.setStroke(new BasicStroke(2));

		for (EdgeContour e : contours) {

			for (EdgeSegment s : e.segments) {
				// fit line segments to the point sequence. Note that loop is
				// false
				List<PointIndex_I32> vertexes = ShapeFittingOps.fitPolygon(s.points, false, splitFraction,
						minimumSideFraction, 100);

				g2.setColor(new Color(1.f, 1.f, 0.f));
				VisualizeShapes.drawPolygon(vertexes, false, g2);
			}
		}
	}

}
