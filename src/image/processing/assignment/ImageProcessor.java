package image.processing.assignment;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author Frankie
 */
public class ImageProcessor {

	ImageProcessor() {
	}

	private static int wrapHorizontalIndex(BufferedImage img, int i) {
		// This takes care of negative and positive indices beyond the image resolution
		return (i + img.getWidth()) % img.getWidth();
	}

	private static int wrapVerticalIndex(BufferedImage img, int i) {
		// This takes care of negative and positive indices beyond the image resolution
		return (i + img.getHeight()) % img.getHeight();
	}

	private static Color applyKernel(BufferedImage img, float[][] kernel, int row, int column) {
		float red = 0.0f;
		float green = 0.0f;
		float blue = 0.0f;

		int minIndexH = -(kernel.length / 2);
		int maxIndexH = minIndexH + kernel.length;
		int minIndexV = -(kernel[0].length / 2);
		int maxIndexV = minIndexV + kernel[0].length;

		for (int i = minIndexH; i < maxIndexH; ++i) {
			for (int j = minIndexV; j < maxIndexV; ++j) {
				int indexH = wrapHorizontalIndex(img, row + i);
				int indexV = wrapVerticalIndex(img, column + j);
				Color col = new Color(img.getRGB(indexH, indexV));

				red += col.getRed() * kernel[i - minIndexH][j - minIndexV];
				green += col.getGreen() * kernel[i - minIndexH][j - minIndexV];
				blue += col.getBlue() * kernel[i - minIndexH][j - minIndexV];
			}
		}

		red = Math.min(Math.max(red, 0.0f), 255.0f);
		green = Math.min(Math.max(green, 0.0f), 255.0f);
		blue = Math.min(Math.max(blue, 0.0f), 255.0f);

		return new Color((int) red, (int) green, (int) blue);
	}

	/**
	 * Default method that provides the base for all image processing methods
	 *
	 * @param img
	 * @param kernel
	 * @return
	 */
	public static BufferedImage applyConvolutionFilter(BufferedImage img, float[][] kernel) {
		BufferedImage output = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

		// Set output image from input image (img)
		for (int j = 0; j < img.getHeight(); ++j) {
			for (int i = 0; i < img.getWidth(); ++i) {
				// Calculate the gaussian blur
				Color pixelColor = applyKernel(img, kernel, i, j);
				output.setRGB(i, j, pixelColor.getRGB());
			}
		}

		return output;
	}

	public static BufferedImage boxFilter(BufferedImage img, int radius) {
		final float[][] boxKernel = new float[2 * radius + 1][2 * radius + 1];
		float value = 1.0f / ((2 * radius + 1) * (2 * radius + 1));

		for (int i = 0; i < 2 * radius + 1; i++) {
			for (int j = 0; j < 2 * radius + 1; j++) {
				boxKernel[i][j] = value;
			}
		}
		return applyConvolutionFilter(img, boxKernel);
	}

	public static BufferedImage gaussianFilter(BufferedImage img) {
		final float gaussianKernel[][] = new float[][]{
			{0.00000067f, 0.00002292f, 0.00019117f, 0.00038771f, 0.00019117f, 0.00002292f, 0.00000067f},
			{0.00002292f, 0.00078634f, 0.00655965f, 0.01330373f, 0.00655965f, 0.00078633f, 0.00002292f},
			{0.00019117f, 0.00655965f, 0.05472157f, 0.11098164f, 0.05472157f, 0.00655965f, 0.00019117f},
			{0.00038771f, 0.01330373f, 0.11098164f, 0.22508352f, 0.11098164f, 0.01330373f, 0.00038771f},
			{0.00019117f, 0.00655965f, 0.05472157f, 0.11098164f, 0.05472157f, 0.00655965f, 0.00019117f},
			{0.00002292f, 0.00078634f, 0.00655965f, 0.01330373f, 0.00655965f, 0.00078633f, 0.00002292f},
			{0.00000067f, 0.00002292f, 0.00019117f, 0.00038771f, 0.00019117f, 0.00002292f, 0.00000067f}
		};

		return applyConvolutionFilter(img, gaussianKernel);
	}

	public static BufferedImage edgeDetectionFilter(BufferedImage img) {
		final float edgeDetectionFilter[][] = new float[][]{
			{-1f, -1f, -1f},
			{-1f, 8f, -1f},
			{-1f, -1f, -1f}
		};
		return applyConvolutionFilter(img, edgeDetectionFilter);
	}

	/**
	 * Applies gamma filtering to an image. As the user slides the JSlider from
	 * left to right, the gamma intensifies
	 *
	 * @param gammaSlider
	 * @return
	 */
	public static BufferedImage gammaCorrectionFilter(double gammaSlider, BufferedImage img) {
		for (int x = 0; x < img.getWidth(); ++x) {
			for (int y = 0; y < img.getHeight(); ++y) {
				int color = img.getRGB(x, y);
				
				int red = (color >> 16) & 0xFF;
				int green = (color >> 8) & 0xFF;
				int blue = (color >> 0) & 0xFF;
				
				red = colorRange(Math.pow(red, gammaSlider));
				green =  colorRange(Math.pow(green, gammaSlider));
				blue =  colorRange(Math.pow(blue, gammaSlider));
				
				img.setRGB(x, y, (0xFF << 24) | (red << 16) | (green << 8) | blue);
			}
		}
		return img;
	}
	
	private static int colorRange(double x) {
		return (int) Math.max(0, Math.min(255, x));
	}

	public static BufferedImage grayscaleFilter(BufferedImage img) {
		for (int x = 0; x < img.getWidth(); ++x) {
			for (int y = 0; y < img.getHeight(); ++y) {
				int color = img.getRGB(x, y);
				
				int red = (color >> 16) & 0xFF;
				int green = (color >> 8) & 0xFF;
				int blue = (color >> 0) & 0xFF;
				
				int avg = (red + green + blue) / 3;
				img.setRGB(x, y, (avg << 16) | (avg << 8) | avg);
			}
		}
		return img;
	}
}
