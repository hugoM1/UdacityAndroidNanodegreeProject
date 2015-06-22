package udacity.hugo.myappportfolio.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * Created by hugo on 6/22/15.
 */
public class StreamTrack implements Parcelable {
    private String trackPreviewUrl;
    private String trackName;

    public StreamTrack() {
    }

    public StreamTrack(Parcel in){
        trackName = in.readString();
        trackPreviewUrl = in.readString();
    }

    public void setTrackPreviewUrl(String trackPreviewUrl) {
        this.trackPreviewUrl = trackPreviewUrl;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getTrackPreviewUrl() {
        return trackPreviewUrl;
    }

    public String getTrackName() {
        return trackName;
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
    }

}
