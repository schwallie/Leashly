package ly.leash.Leashly.imageCapture;

import java.io.File;

import android.os.Environment;

import ly.leash.Leashly.imageCapture.AlbumStorageDirFactory;

public final class FroyoAlbumDirFactory extends AlbumStorageDirFactory {

	@Override
	public File getAlbumStorageDir(String albumName) {
		// TODO Auto-generated method stub
		return new File(
		  Environment.getExternalStoragePublicDirectory(
		    Environment.DIRECTORY_PICTURES
		  ), 
		  albumName
		);
	}
}
