package image.processing.assignment;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MainPanel extends JPanel {

	private static final Font FONT_PLAIN = new Font("system", Font.PLAIN, 20);
	private static final Font FONT_BOLD = new Font("system", Font.BOLD, 20);

	private final ImagePanel originalImage;
	private final ImagePanel modifiedImage;
	private final JButton openButton;
	private final JButton saveButton;
	private final JButton applyButton;
	private final JLabel messageLabel;
	private final JCheckBox boxFilterCheck;
	private final JCheckBox gaussianFilterCheck;
	private final JCheckBox edgeDetectionCheck;
	private final JCheckBox grayscaleCheck;
	private final JSlider gammaSlider;

	public MainPanel() {
		// Instantiate member field
		this.originalImage = new ImagePanel();
		this.modifiedImage = new ImagePanel();
		this.openButton = new JButton();
		this.saveButton = new JButton();
		this.applyButton = new JButton();
		this.messageLabel = new JLabel();
		this.boxFilterCheck = new JCheckBox();
		this.gaussianFilterCheck = new JCheckBox();
		this.edgeDetectionCheck = new JCheckBox();
		this.grayscaleCheck = new JCheckBox();
		this.gammaSlider = new JSlider();

		initGUI();
	}

	/**
	 * Builds the GUI.
	 */
	private void initGUI() {
		setLayout(new GridBagLayout());

		messageLabel.setText("Image Processor");
		openButton.setText("Open");
		saveButton.setText("Save");
		applyButton.setText("Apply");

		openButton.addActionListener(ea -> openImage());
		saveButton.addActionListener(ea -> saveImage());
		applyButton.addActionListener(ea -> applyTransforms());
		messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		gammaSlider.setMinimum(0);
		gammaSlider.setMaximum(2000);
		gammaSlider.setValue(1000);

		JLabel boxLabel = new JLabel("Box Filter:");
		JLabel gaussianLabel = new JLabel("Gaussian Filter:");
		JLabel edgeLabel = new JLabel("Edge Detection Filter:");
		JLabel gammaLabel = new JLabel("Gamma Correction:");
		JLabel grayscaleLabel = new JLabel("Convert to Grayscale:");

		Insets ins = insets(5, 10, 5, 10);
		GridBagConstraints c1 = constr(0, 0, ins);
		GridBagConstraints c2 = constr(1, 0, ins);
		c1.weighty = 1;
		c2.weighty = 1;
		c1.fill = GridBagConstraints.BOTH;
		c2.fill = GridBagConstraints.BOTH;
		add(originalImage, c1);
		add(modifiedImage, c2);
		add(openButton, constr(0, 1, ins));
		add(saveButton, constr(1, 1, ins));
		add(messageLabel, constr(0, 2, ins, 2));
		add(boxLabel, constr(0, 3, ins));
		add(boxFilterCheck, constr(1, 3, ins));
		add(gaussianLabel, constr(0, 4, ins));
		add(gaussianFilterCheck, constr(1, 4, ins));
		add(edgeLabel, constr(0, 5, ins));
		add(edgeDetectionCheck, constr(1, 5, ins));
		add(gammaLabel, constr(0, 6, ins));
		add(gammaSlider, constr(1, 6, ins));
		add(grayscaleLabel, constr(0, 7, ins));
		add(grayscaleCheck, constr(1, 7, ins));
		add(applyButton, constr(0, 8, ins, 2));

		for (Component comp : this.getComponents()) {
			if (comp instanceof JLabel) {
				comp.setFont(FONT_PLAIN);
			} else {
				comp.setFont(FONT_BOLD);
			}
		}
	}

	private void openImage() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Open Image...");
		fc.setMultiSelectionEnabled(false);
		fc.setCurrentDirectory(new File("."));
		fc.setFileFilter(new FileNameExtensionFilter("Images", "png", "jpg", "jpeg", "bmp", "gif"));
		fc.setApproveButtonText("Open");

		int result = fc.showOpenDialog(null);
		if (result != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File file = fc.getSelectedFile();

		BufferedImage img;
		try {
			img = ImageIO.read(file);
		} catch (IOException ex) {
			messageLabel.setText("Could not open file '" + file.getPath() + "'");
			return;
		}

		originalImage.setImage(img);
		modifiedImage.setImage(img);
	}

	private void saveImage() {
		if (originalImage.getImage().equals(modifiedImage.getImage())) {
			messageLabel.setText("Images are the same!");
			return;
		}

		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Open Image...");
		fc.setMultiSelectionEnabled(false);
		fc.setCurrentDirectory(new File("."));
		fc.setFileFilter(new FileNameExtensionFilter("Images", "png", "jpg", "jpeg", "bmp", "gif"));
		fc.setApproveButtonText("Save");

		int result = fc.showSaveDialog(null);
		if (result != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File file = fc.getSelectedFile();
		try {
			ImageIO.write(modifiedImage.getImage(), "jpg", file);
			messageLabel.setText("Successfully saved to file");
		} catch (IOException ex) {
			messageLabel.setText("Could not save to file!");
		}

	}

	private void applyTransforms() {
		BufferedImage img = copyImage(originalImage.getImage());

		// 1. Apply box filter
		if (boxFilterCheck.isSelected()) {
			img = ImageProcessor.boxFilter(img, 3);
		}

		// 2. Apply gaussian filter
		if (gaussianFilterCheck.isSelected()) {
			img = ImageProcessor.gaussianFilter(img);
		}

		// 3. Edge Detection filter
		if (edgeDetectionCheck.isSelected()) {
			img = ImageProcessor.edgeDetectionFilter(img);
		}

		// 4. Gamma Correction
		img = ImageProcessor.gammaCorrectionFilter(gammaSlider.getValue() / 1000.0, img);

		// 5. Grayscale
		if (grayscaleCheck.isSelected()) {
			img = ImageProcessor.grayscaleFilter(img);
		}

		modifiedImage.setImage(img);
	}

	// ============================================================================================
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ex) {
			throw new RuntimeException(ex);
		}
		
		JFrame frame = new JFrame();
		frame.setTitle("Image Processor");
		frame.setSize(650, 600);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setContentPane(new MainPanel());
		frame.setVisible(true);
	}

	private static Insets insets(int top, int left, int bottom, int right) {
		return new Insets(top, left, bottom, right);
	}

	/**
	 * Generates a {@link GridBagConstraints} instance for the given
	 * coordinates.
	 *
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 * @param ins the insets of the cell.
	 * @return constraints to be applied to a component in a
	 * {@link GridBagLayout}.
	 */
	private static GridBagConstraints constr(int x, int y, Insets ins) {
		GridBagConstraints constr = new GridBagConstraints();
		constr.gridx = x;
		constr.gridy = y;
		constr.weightx = 1;
		constr.weighty = 0;
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.insets = ins;
		return constr;
	}

	/**
	 * Generates a {@link GridBagConstraints} instance for the given
	 * coordinates.
	 *
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 * @param ins the insets of the cell.
	 * @param widthx the number of columns to span.
	 * @return constraints to be applied to a component in a
	 * {@link GridBagLayout}.
	 */
	private static GridBagConstraints constr(int x, int y, Insets ins, int widthx) {
		GridBagConstraints constr = new GridBagConstraints();
		constr.gridx = x;
		constr.gridy = y;
		constr.gridwidth = widthx;
		constr.weightx = 1;
		constr.weighty = 0;
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.insets = ins;
		return constr;
	}
	
	/**
	 * Copies a {@link BufferedImage}.
	 * @param bi the image to be copied.
	 * @return the cloned image.
	 */
	public static BufferedImage copyImage(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(bi.getRaster().createCompatibleWritableRaster());
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
}
