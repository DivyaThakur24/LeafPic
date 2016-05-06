package com.horaapps.leafpic.Base.deprecated;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.horaapps.leafpic.Base.AlbumSettings;
import com.horaapps.leafpic.Base.CustomAlbumsHandler;
import com.horaapps.leafpic.R;
import com.horaapps.leafpic.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by dnld on 12/11/15.
 */
@Deprecated
public class deprecatedAlbum implements Parcelable {

    public static final int FILTER_ALL = 45;
    public static final int FILTER_IMAGE = 55;
    public static final int FILTER_VIDEO = 75;
    public static final int FILTER_GIF = 555;
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<deprecatedAlbum> CREATOR = new Parcelable.Creator<deprecatedAlbum>() {
        @Override
        public deprecatedAlbum createFromParcel(Parcel in) {
            return new deprecatedAlbum(in);
        }

        @Override
        public deprecatedAlbum[] newArray(int size) {
            return new deprecatedAlbum[size];
        }
    };
    public String ID = null;
    public String DisplayName = null;
    public String Path = null;
    public AlbumSettings settings = new AlbumSettings();
    public AlbumMediaCount count = new AlbumMediaCount();
    public ArrayList<deprecatedMedia> deprecatedMedias = new ArrayList<deprecatedMedia>();
    public ArrayList<deprecatedMedia> selectedDeprecatedMedias = new ArrayList<deprecatedMedia>();
    Context context;
    private boolean selected = false;
    private int current = -1;
    private int filter_photos = FILTER_ALL;

    public deprecatedAlbum(String id, String name, AlbumMediaCount count) {
        ID = id;
        DisplayName = name;
        this.count = count;
    }

    public deprecatedAlbum(String id) {
        ID = id;
    }

    public deprecatedAlbum(String id, Context ctx) {
        ID = id;
        context = ctx;
    }

    public deprecatedAlbum(Context ctx) {
        context = ctx;
    }

    public deprecatedAlbum(Context ctx, String photoPath) {
        context = ctx;
        MediaStoreHandler as = new MediaStoreHandler(context);
        ID = as.getAlbumPhoto(photoPath);
        setSettings();
        updatePhotos();
        setCurrentPhoto(photoPath);
    }

    /**
     * parcellable
     */

    protected deprecatedAlbum(Parcel in) {
        ID = in.readString();
        DisplayName = in.readString();
        Path = in.readString();
        settings = (AlbumSettings) in.readValue(AlbumSettings.class.getClassLoader());
        count = (AlbumMediaCount) in.readValue(AlbumMediaCount.class.getClassLoader());
        if (in.readByte() == 0x01) {
            deprecatedMedias = new ArrayList<deprecatedMedia>();
            in.readList(deprecatedMedias, deprecatedMedia.class.getClassLoader());
        } else {
            deprecatedMedias = null;
        }
        current = in.readInt();
        filter_photos = in.readInt();
    }

    public void setContext(Context ctx) {
        context = ctx;
    }

    public void setSettings() {
        CustomAlbumsHandler h = new CustomAlbumsHandler(context);
        settings = h.getSettings(ID);
    }

    public void setDefaultSortingMode(String column) {
       // CustomAlbumsHandler h = new CustomAlbumsHandler(context);
       // h.setAlbumSortingMode(ID, column);
       // settings.columnSortingMode = column;
    }

    public void setDefaultSortingAscending(Boolean ascending) {
        CustomAlbumsHandler h = new CustomAlbumsHandler(context);
        h.setAlbumSortingAscending(ID, ascending);
        settings.ascending = ascending;
    }

    public String getSortingMode() {
        return null;//settings.getSQLSortingMode();
        /*if (settings.columnSortingMode != null) return settings.getSQLSortingMode();
        else return MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";*/
    }

    public String getContentDescdription(Context c) {
        if (count.photos > 0 && count.videos == 0)
            return count.photos == 1 ? c.getString(R.string.singular_photo) : c.getString(R.string.plural_photos);
        else if (count.photos == 0 && count.videos > 0) return c.getString(R.string.video);
        else return c.getString(R.string.media);
    }

    /**
     * media stuff
     */
   /* public boolean areFiltersActive() {
        return filter_photos != FILTER_ALL;
    }

    public void filterMedias(int filter) {
        filter_photos = filter;
        updatePhotos();
    }*/

    public void updatePhotos() {
        MediaStoreHandler as = new MediaStoreHandler(context);
        deprecatedMedias = as.getAlbumPhotos(ID, getSortingMode(), filter_photos);
    }

   /* public void clearSelectedPhotos() {
        for (Media media : medias) {
            media.setSelected(false);
        }
        selectedMedias.clear();
    }

    public int getSelectedCount() {
        return selectedMedias.size();
    }
    */

    public deprecatedMedia getCurrentPhoto() {
        return deprecatedMedias.get(getCurrentPhotoIndex());
    }

    public void setCurrentPhoto(String path) {
        setCurrentPhotoIndex(getPhotoIndex(path));
    }

    int getPhotoIndex(String path) {
        for (int i = 0; i < deprecatedMedias.size(); i++) {
            if (deprecatedMedias.get(i).Path.equals(path)) return i;
        }
        return -1;
    }

    public int getCurrentPhotoIndex() {
        return current;
    }

    public void setCurrentPhotoIndex(int n) {
        current = n;
    }


    /*
     public void selectAllPhotos() {
        for (int i = 0; i < medias.size(); i++)
            if (!medias.get(i).isSelected()) {
                medias.get(i).setSelected(true);
                selectedMedias.add(medias.get(i));
            }
    }

    public int toggleSelectPhoto(int index) {
        if (medias.get(index) != null) {
            medias.get(index).setSelected(!medias.get(index).isSelected());
            if (medias.get(index).isSelected())
                selectedMedias.add(medias.get(index));
            else
                selectedMedias.remove(medias.get(index));
        }
        return index;
    }
    public void selectAllPhotosUpTo(int targetIndex, PhotosAdapter adapter) {
        int indexRightBeforeOrAfter = -1;
        int indexNow;
        for (Media sm : selectedMedias) {
            indexNow = medias.indexOf(sm);
//            Log.d("SELECT", String.format("checking: %d, indexRightBeforeOrAfter: %d targetIndex: %d", indexNow, indexRightBeforeOrAfter, targetIndex));
            if (indexRightBeforeOrAfter == -1) {
                indexRightBeforeOrAfter = indexNow;
            }

            if (indexNow > targetIndex) {
                break;
            }
            indexRightBeforeOrAfter = indexNow;
        }

        if (indexRightBeforeOrAfter == -1) {
            Log.wtf("Album", "indexRightBeforeOrAfter==-1 this should not happen.");
        } else {
            for (int index = Math.min(targetIndex, indexRightBeforeOrAfter); index <= Math.max(targetIndex, indexRightBeforeOrAfter); index++) {
//                Log.d("SELECT", String.format("Selecting: %d", index));
                if (medias.get(index) != null) {
                    if (!medias.get(index).isSelected()) {
                        medias.get(index).setSelected(true);
                        selectedMedias.add(medias.get(index));
                        adapter.notifyItemChanged(index);
                    }
                }
            }
        }
//        Log.d("SELECT", String.format("target: %d  indexRightBeforeOrAfter: %d", targetIndex, indexRightBeforeOrAfter));
    }*/

    public boolean isSelected() {
        return selected;
    }

    public void setSelcted(boolean value) {
        selected = value;
    }

    public boolean setPath() {
        if (deprecatedMedias.size() > 0) {
            Path = StringUtils.getBucketPathbyImagePath(deprecatedMedias.get(0).Path);
            return true;
        }
        return false;
    }

    public boolean hasCustomCover() {
        return settings.coverPath != null;
    }

    public String getPathCoverAlbum() {
        if (hasCustomCover()) return settings.coverPath;
        if (deprecatedMedias.size() > 0) return "file://" + deprecatedMedias.get(0).Path;
        else return "drawable://" + R.drawable.ic_empty;
    }

    public deprecatedMedia getCoverAlbum() {
        if (hasCustomCover())
            return new deprecatedMedia(settings.coverPath);
        if (deprecatedMedias.size() > 0)
            return deprecatedMedias.get(0); //return also image info like date, orientation...
        return new deprecatedMedia("drawable://" + R.drawable.ic_empty);
    }

    public void setCoverPath(String path) {
        settings.coverPath = path;
    }

    public void setSelectedPhotoAsPreview() {
        if (selectedDeprecatedMedias.size() > 0) {
            CustomAlbumsHandler h = new CustomAlbumsHandler(context);
            h.setAlbumPhotPreview(ID, selectedDeprecatedMedias.get(0).Path);
            settings.coverPath = selectedDeprecatedMedias.get(0).Path;
        }
    }

    public int getImagesCount() {
        return count.getTotal();
    }

    public void deleteCurrentPhoto() {
        deletePhoto(getCurrentPhoto());
    }

    public void moveCurentPhoto(String newFolderPath) {
        final Uri asd = getCurrentPhoto().getUri();
        File from = new File(getCurrentPhoto().Path);
        File to = new File(StringUtils.getPhotoPathMoved(getCurrentPhoto().Path, newFolderPath));
        if (from.renameTo(to)) {
            MediaScannerConnection.scanFile(
                    context,
                    new String[]{to.getAbsolutePath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            //deleteCurrentPhoto();
                            context.getContentResolver().delete(asd, null, null);
                        }
                    });
        }
    }

    /*public void copySelectedPhotos(String folderPath) {
        for (Media media : selectedMedias)
            copyPhoto(media.Path, folderPath);
    }

    public void copyPhoto(String olderPath, String folderPath) {
        try {
            File from = new File(olderPath);
            File to = new File(StringUtils.getPhotoPathMoved(olderPath, folderPath));

            InputStream in = new FileInputStream(from);
            OutputStream out = new FileOutputStream(to);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0)
                out.write(buf, 0, len);

            in.close();
            out.close();

            scanFile(new String[]{to.getAbsolutePath()});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

   /* public void movePhoto(String olderPath, String folderPath) {
        try {
            File from = new File(olderPath);
            File to = new File(StringUtils.getPhotoPathMoved(olderPath, folderPath));
            scanFile(new String[]{from.getAbsolutePath()});
            from.renameTo(to);
            scanFile(new String[]{to.getAbsolutePath()});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

public void moveSelectedPhotos(String paths, String folderPath) {
        for (String path : paths.split("ç"))
            movePhoto(path, folderPath);
    }
    */

    public ArrayList<deprecatedMedia> getSelectedDeprecatedMedias() {
        return selectedDeprecatedMedias;
    }

    public void deletePhoto(deprecatedMedia a) {
        context.getContentResolver().delete(a.getUri(), null, null);
        deprecatedMedias.remove(a);
    }

    public void scanFile(String[] path) {
        MediaScannerConnection.scanFile(context, path, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                System.out.println("SCAN COMPLETED: " + path);
            }
        });
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ID);
        dest.writeString(DisplayName);
        dest.writeString(Path);
        dest.writeValue(settings);
        dest.writeValue(count);
        if (deprecatedMedias == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(deprecatedMedias);
        }
        dest.writeInt(current);
        dest.writeInt(filter_photos);
    }
}