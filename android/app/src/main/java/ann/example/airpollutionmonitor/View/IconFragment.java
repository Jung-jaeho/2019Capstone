package ann.example.airpollutionmonitor.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import ann.example.airpollutionmonitor.R;

public class IconFragment extends Fragment {
    public static final int level1 = 1;
    public static final int level2 = 2;
    public static final int level3 = 3;
    public static final int level4 = 4;
    public static final int level5 = 5;
    public static final int level6 = 6;

    private String name;
    private int level;

    public static IconFragment newInstance(String name, int level){
        IconFragment fragment = new IconFragment();
        Bundle args = new Bundle();
        args.putString("name", name);   // 오염 물질 이름
        args.putInt("level", level);    // 현재 등급
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        name = getArguments().getString("name");
        level = getArguments().getInt("level", 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_icon, container, false);

        TextView textView = view.findViewById(R.id.name);
        textView.setText(name);

        ImageView imageView = view.findViewById(R.id.icon);
        switch (level){
            case level1:
                Glide.with(view)
                        .load(R.drawable.level1)
                        .into(imageView);
                break;
            case level2:
                Glide.with(view)
                        .load(R.drawable.level2)
                        .into(imageView);
                break;
            case level3:
                Glide.with(view)
                        .load(R.drawable.level3)
                        .into(imageView);
                break;
            case level4:
                Glide.with(view)
                        .load(R.drawable.level4)
                        .into(imageView);
                break;
            case level5:
                Glide.with(view)
                        .load(R.drawable.level5)
                        .into(imageView);
                imageView.setImageResource(R.drawable.level5);
                break;
            case level6:
                Glide.with(view)
                        .load(R.drawable.level6)
                        .into(imageView);
                break;
        }

        return view;
    }
}
