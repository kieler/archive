package modelgui;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import org.apache.batik.swing.svg.JSVGComponent;
import org.apache.batik.transcoder.AbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.image.TIFFTranscoder;
import org.apache.fop.svg.PDFTranscoder;

public class ExportManager {

	JFileChooser fileChooser;
	JSVGComponent svgCanvas;

	FileFilter pdfFilter;
	FileFilter pngFilter;
	FileFilter tiffFilter;
	FileFilter svgFilter;
	SVGApplication app;

	public ExportManager(SVGApplication app) {
		this.svgCanvas = app.svgCanvas;
		this.app = app;
		fileChooser = new JFileChooser(".");

		tiffFilter = new TranscodingFileFilter(new TIFFTranscoder(),
				"Tagged Image File Format (*.tiff|*.tif)", new String[] {
						"tiff", "tif" });
		pngFilter = new TranscodingFileFilter(new PNGTranscoder(),
				"Portable Network Graphics (*.png)", new String[] { "png" });
		pdfFilter = new TranscodingFileFilter(new PDFTranscoder(),
				"Portable Document Format (*.pdf)", new String[] { "pdf" });
		// svgFilter = new TranscodingFileFilter(new SVGTranscoder(), "Scalable
		// Vector Format (*.svg)", new String[]{"svg"});

		fileChooser.addChoosableFileFilter(tiffFilter);
		fileChooser.addChoosableFileFilter(pngFilter);
		fileChooser.addChoosableFileFilter(pdfFilter);
		// fileChooser.addChoosableFileFilter(svgFilter);

		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
	}

	public void cancelSelection() {

	}

	public void showSaveDialog(Component parent) {
		int returnValue = fileChooser.showSaveDialog(parent);

		File f = fileChooser.getSelectedFile();
		if (f == null)
			return;

		FileFilter filter = fileChooser.getFileFilter();
		if (returnValue == JFileChooser.CANCEL_OPTION)
			return;
		if (!filter.accept(f)) {
			// if file does not contain a correct extension, just add the right
			// one
			f = new File(f.getPath() + "."
					+ ((TranscodingFileFilter) filter).getExtensions()[0]);
		}
		if (filter instanceof TranscodingFileFilter) {
			AbstractTranscoder transcoder = ((TranscodingFileFilter) filter)
					.getTranscoder();
			FileOutputStream fo;
			try {
				fo = new FileOutputStream(f);
				transcoder.transcode(new TranscoderInput(svgCanvas
						.getSVGDocument()), new TranscoderOutput(fo));
				JOptionPane.showMessageDialog(parent,
						"File successfully saved!", "Success!",
						JOptionPane.INFORMATION_MESSAGE);
				// TODO: due to a bug in batik, we have to restart the
				// application, here. Otherwise
				// a NullPointerException will occur.
				app.restart();
			}
			catch (TranscoderException e) {
				new ModelguiErrorDialog(e, "Could not export to image!");
			}
			catch (FileNotFoundException e) {
				new ModelguiErrorDialog("File " + f.getPath() + " not found!");
			}
		}
		else {
			// TODO: try to parse pathname to get the correct extension for
			// transcoding
		}
	}
}
