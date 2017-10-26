package com.louco.btproject;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class AdRVBtDevices extends RecyclerView.Adapter<AdRVBtDevices.ViewHolder> {

    private List<BluetoothDevice> devices = new ArrayList<>();
    private onClickRV onclickRV;

    AdRVBtDevices(onClickRV onclickRV) {
        this.onclickRV = onclickRV;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view  = inflater.inflate(R.layout.ad_rv_bt_devices, parent, false);
        return new ViewHolder(view, onclickRV);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(devices.get(position));
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    void addDevicesList(BluetoothDevice bluetoothDevice){
        devices.add(bluetoothDevice);
        notifyDataSetChanged();
    }
    void clearDevicesList(){
        devices.clear();
        notifyDataSetChanged();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_name_device)
        TextView NameDevice;
        BluetoothDevice lastDevice;

        ViewHolder(View itemView, onClickRV onclickRV) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> onclickRV.onClick(lastDevice));
        }

        private void bind(BluetoothDevice device){
            lastDevice = device;
            if(device.getName()==null) NameDevice.setText(device.getAddress());
            else NameDevice.setText(device.getName());
        }
    }

    interface onClickRV{
        void onClick(BluetoothDevice device);
    }
}
