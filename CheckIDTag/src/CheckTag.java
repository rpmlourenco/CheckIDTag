import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;

public class CheckTag {
	public static void main(String[] args) {

		Logger log = LogManager.getLogManager().getLogger("");
		for (Handler h : log.getHandlers()) {
			h.setLevel(Level.SEVERE);
		}

		File currentDir = new File("D:\\Music\\"); // current directory
		checkCovers(currentDir, false, true);
	}

	/**
	 * @param dir
	 *            root folder path
	 * @param missing
	 *            checks for missing covers
	 * @param size
	 *            checks for covers bigger than 600 pixels width or height
	 */
	public static void checkCovers(File dir, boolean missing, boolean size) {
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				// System.out.println("directory:" + file.getCanonicalPath());
				checkCovers(file, missing, size);
			} else {
				if (file.getName().toLowerCase().endsWith(".mp3") || file.getName().toLowerCase().endsWith(".m4a")) {

					AudioFile f;
					try {
						f = AudioFileIO.read(file);
						Tag tag = f.getTag();
						List<Artwork> existingArtworkList = tag.getArtworkList();

						if (existingArtworkList.size() > 0) {
							Artwork art = existingArtworkList.get(0);

							if (art != null && art.getBinaryData() != null) {
								BufferedImage img = ImageIO.read(
										ImageIO.createImageInputStream(new ByteArrayInputStream(art.getBinaryData())));
								if (size && (img.getWidth() > 1000 || img.getHeight() > 1000))
									System.out.println("FOUND COVER " + file.getCanonicalPath() + " "
											+ art.getMimeType() + " " + img.getWidth() + "x" + img.getHeight());
							} else {
								if (missing)
									System.out.println("MISSING COVER " + file.getCanonicalPath());
							}
						} else {
							if (missing)
								System.out.println("MISSING COVER " + file.getCanonicalPath());
						}

					} catch (Exception e) {
						System.out.println("ERROR: " + file.getName());
						e.printStackTrace();
					}
				}
			}
		}
	}

}
