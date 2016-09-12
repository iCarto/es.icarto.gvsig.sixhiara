package es.icarto.gvsig.sixhiara.forms;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// http://docs.oracle.com/javase/8/javafx/graphics-tutorial/image_ops.htm#JFXGR238
public class ChartToImage {
	private static final Logger logger = LoggerFactory
			.getLogger(ChartToImage.class);

	public ChartToImage() {
		// TODO Auto-generated constructor stub
	}

	public void snapshot(Scene scene) {
		WritableImage snapshot = scene.snapshot(null);
		File outFile = new File("/tmp/imageops-snapshot.png");
		try {
			BufferedImage bytes = SwingFXUtils.fromFXImage(snapshot, null);
			ImageIO.write(bytes, "png", outFile);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

	}

}
