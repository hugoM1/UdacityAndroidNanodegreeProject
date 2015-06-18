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
import kaaes.spotify.webapi.android.models.Artist;
import udacity.hugo.myappportfolio.R;

/**
 * Created by hugo on 6/16/15.
 */
public class ArtistAdapter extends BaseAdapter {
    private List<Artist> artists;
    private Context context;

    public ArtistAdapter(List<Artist> artists, Context context) {
        this.artists = artists;
        this.context = context;
    }

    @Override
    public int getCount() {
        return artists.size();
    }

    @Override
    public Object getItem(int position) {
        return artists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateList(List<Artist> newArtist){
        this.artists = newArtist;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        Artist artist = (Artist) getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageArtist = (ImageView) convertView.findViewById(R.id.image_view_artist);
            viewHolder.nameArtist = (TextView) convertView.findViewById(R.id.text_view_artist_name);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.nameArtist.setText(String.valueOf(artist.name));

        if (artist.images.size()>0) {
            Picasso.with(context)
                    .load(artist.images.get(1).url)
                    .fit()

                    .into(viewHolder.imageArtist);
        }

        return convertView;
    }

    private class ViewHolder{
        ImageView imageArtist;
        TextView nameArtist;
    }
}
