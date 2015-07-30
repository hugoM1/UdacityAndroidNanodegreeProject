package udacity.hugo.myappportfolio.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * Created by hugo on 6/22/15.
 */
public class StreamTrack implements Parcelable {
    private String trackPreviewUrl;
    private String trackName;
    private String albumName;
    private String albumImage;
    private String trackArtistName;
    private long trackDuration;

    public StreamTrack() {
    }

    public StreamTrack(Parcel in){
        trackName = in.readString();
        trackPreviewUrl = in.readString();
        albumName = in.readString();
        albumImage = in.readString();
        trackArtistName = in.readString();
        trackDuration = in.readLong();
    }

    public void setTrackPreviewUrl(String trackPreviewUrl) {
        this.trackPreviewUrl = trackPreviewUrl;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public void setAlbumImage(String albumImage) {
        this.albumImage = albumImage;
    }

    public void setTrackArtistName(String trackArtistName) {
        this.trackArtistName = trackArtistName;
    }

    public void setTrackDuration(long trackDuration) {
        this.trackDuration = trackDuration;
    }

    public String getTrackPreviewUrl() {
        return trackPreviewUrl;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getAlbumImage() {
        return albumImage;
    }

    public String getTrackArtistName() {
        return trackArtistName;
    }

    public long getTrackDuration() {
        return trackDuration;
    }

    public static final Parcelable.Creator<StreamTrack> CREATOR = new ClassLoaderCreator<StreamTrack>() {
        @Override
        public StreamTrack createFromParcel(Parcel source, ClassLoader loader) {
            return new StreamTrack(source);
        }

        @Override
        public StreamTrack createFromParcel(Parcel source) {
            return null;
        }

        @Override
        public StreamTrack[] newArray(int size) {
            return new StreamTrack[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trackName);
        dest.writeString(trackPreviewUrl);
        dest.writeString(albumName);
        dest.writeString(albumImage);
        dest.writeString(trackArtistName);
        dest.writeLong(trackDuration);
    }

}
