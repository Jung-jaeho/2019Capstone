package ann.example.airpollutionmonitor.View;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import ann.example.airpollutionmonitor.AppManager;
import ann.example.airpollutionmonitor.BaseActivity;
import ann.example.airpollutionmonitor.Model.Location;
import ann.example.airpollutionmonitor.R;

public class RegisterActivity extends BaseActivity {
    ListView listView;
    LinearLayout noDeviceMessage;
    LocationAdapter locationAdapter;

    ArrayList<Location> locations = AppManager.getInstance().getLocations();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 액티비티 갱신
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void initView(){
        ImageView backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView title = findViewById(R.id.title);
        title.setText(R.string.menu_register);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 제품 추가 액티비티 띄움
                Intent intent = new Intent(getApplicationContext(), AddLocationActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        locationAdapter = new LocationAdapter(this, locations);
        listView = findViewById(R.id.listView);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(locationAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, final long l) {
                // 등록된 제품(장소) 삭제
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(RegisterActivity.this);
                alert_confirm
                        .setMessage("삭제하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                locations.remove(i);
                                locationAdapter.notifyDataSetChanged();
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                return;
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
                return false;
            }
        });

        noDeviceMessage = findViewById(R.id.message_no_device);
        if(locations.size() == 0){
            noDeviceMessage.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }else{
            noDeviceMessage.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

}

class LocationAdapter extends BaseAdapter{
    Context context = null;
    LayoutInflater layoutInflater = null;
    ArrayList<Location> locations;

    public LocationAdapter(Context context, ArrayList<Location> locations) {
        this.context = context;
        this.locations = locations;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return locations.size();
    }

    @Override
    public Object getItem(int i) {
        return locations.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = layoutInflater.inflate(R.layout.item_device, null);

        TextView name = v.findViewById(R.id.name);
        name.setText(locations.get(i).getName());

        TextView serialNumber = v.findViewById(R.id.serial_number);
        serialNumber.setText(locations.get(i).getSerialNumber());

        return v;
    }
}
