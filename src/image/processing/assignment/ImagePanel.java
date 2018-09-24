package image.processing.assignment;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {

	/**
	 * The image being displayed.
	 */
	private BufferedImage image;

	/**
	 * Instantiates a panel with no image.
	 */
	public ImagePanel() {
	}

	/**
	 * Instantiates a panel with a given image.
	 *
	 * @param image the image to display. Can be {@code null}.
	 */
	public ImagePanel(BufferedImage image) {
		this.image = image;
	}

	/**
	 * @return the image being displayed. Can be {@code null}.
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * Sets the image to be displayed. This also requests a repaint.
	 *
	 * @param image the image to be displayed.
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
		this.repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (image != null) {
			int xOffset = 0;
			int yOffset = 0;

			int w = getWidth();
			int h = getHeight();

			float frameAspect = getWidth() / (float) getHeight();
			float imageAspect = image.getWidth() / (float) image.getHeight();

			if (imageAspect < frameAspect) {
				float mult = getHeight() / (float) image.getHeight();
				w = Math.round(mult * image.getWidth());

				xOffset = Math.round((getWidth() - w) * 0.5f);
			} else {
				float mult = getWidth() / (float) image.getWidth();
				h = Math.round(mult * image.getHeight());

				yOffset = Math.round((getHeight() - h) * 0.5f);
			}

			g.drawImage(image, xOffset, yOffset, w, h, null);
		}
	}

}
