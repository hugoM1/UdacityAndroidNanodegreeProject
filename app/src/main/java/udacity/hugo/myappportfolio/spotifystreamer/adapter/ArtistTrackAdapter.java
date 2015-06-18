package udacity.hugo.myappportfolio.spotifystreamer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;
import kaaes.spotify.webapi.android.models.Track;
import udacity.hugo.myappportfolio.R;

/**
 * Created by hugo on 6/18/15.
 */
public class ArtistTrackAdapter extends BaseAdapter {

    private List<Track> tracks;
    private Context context;

    public ArtistTrackAdapter(List<Track> tracks, Context context) {
        this.tracks = tracks;
        this.context = context;
    }

    @Override
    public int getCount() {
        return tracks.size();
    }

    @Override
    public Object getItem(int position) {
        return tracks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateList(List<Track> newTracksList){
        this.tracks = newTracksList;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        Track track = (Track) getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_track_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.trackImage = (ImageView) convertView.findViewById(R.id.image_view_track);
            viewHolder.trackName = (TextView) convertView.findViewById(R.id.text_view_track_name);
            viewHolder.albumName = (TextView) convertView.findViewById(R.id.text_view_album_name);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.albumName.setText(String.valueOf(track.album.name));
        viewHolder.trackName.setText(String.valueOf(track.name));

        // Check if has images
        if (track.album.images.size() > 0) {
            Picasso.with(context)
                    .load(track.album.images.get(1).url)
                    .into(viewHolder.trackImage);
        }

        return convertView;
    }

    private class ViewHolder{
        ImageView trackImage;
        TextView trackName, albumName;
    }
}
