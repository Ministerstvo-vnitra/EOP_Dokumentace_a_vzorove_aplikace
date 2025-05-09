package com.aheaditec.sample.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aheaditec.wrapper.Reader;
import com.aheaditec.sample.R;

import java.util.List;

/**
 * Adapter for RecyclerView holding already paired readers to which we want to connect.
 */

public class ConnectReadersAdapter extends RecyclerView.Adapter<ConnectReadersAdapter.ConnectReaderViewHolder> {

    private List<Reader> readers;
    private ViewClickAdapter adapter;

    public ConnectReadersAdapter(List<Reader> readers, ViewClickAdapter adapter) {
        if (readers == null || adapter == null) {
            throw new IllegalArgumentException("Readers or adapter is null.");
        }
        this.readers = readers;
        this.adapter = adapter;
    }

    @Override
    public ConnectReaderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View readersView = inflater.inflate(R.layout.item_reader_scanned, parent, false);
        return new ConnectReaderViewHolder(readersView);
    }

    @Override
    public void onBindViewHolder(ConnectReaderViewHolder holder, int position) {
        Reader reader = readers.get(position);

        holder.readerName.setText(reader.getNickname());
        holder.connectButton.setText("Connect");
        holder.connectButton.setOnClickListener(view -> adapter.onConnectClicked(reader));
    }

    @Override
    public int getItemCount() {
        return readers.size();
    }

    class ConnectReaderViewHolder extends RecyclerView.ViewHolder {

        private TextView readerName;
        private Button connectButton;

        private ConnectReaderViewHolder(View itemView) {
            super(itemView);

            this.readerName = itemView.findViewById(R.id.txtItemReaderName);
            this.connectButton = itemView.findViewById(R.id.btnItemConnectPair);
        }
    }

    public interface ViewClickAdapter {
        void onConnectClicked(Reader reader);
    }
}
