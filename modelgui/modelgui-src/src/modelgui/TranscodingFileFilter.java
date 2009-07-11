package modelgui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

import org.apache.batik.transcoder.AbstractTranscoder;

public class TranscodingFileFilter extends FileFilter {

	AbstractTranscoder transcoder;
	String description = null;
	String[] extensions = null;
	String name;

	public TranscodingFileFilter(AbstractTranscoder transcoder,
			String description, String[] extensions) {
		this.transcoder = transcoder;
		this.description = description;
		this.extensions = extensions;
		name = "[default name]";
	}

	public String[] getExtensions() {
		return extensions;
	}

	@Override
	public boolean accept(File pathname) {
		if (pathname.isDirectory())
			return true;
		else {
			for (int i = 0; i < extensions.length; i++) {
				if (pathname.isFile()
						&& (pathname.getPath().endsWith("." + extensions[i]) || pathname
								.isDirectory())) {
					return true;
				}
			}
			return false;
		}
	}

	public AbstractTranscoder getTranscoder() {
		return transcoder;
	}

	public void setName(String str) {
		this.name = str;
	}

	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

}
