package com.aheaditec.sample.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aheaditec.wrapper.Reader;
import com.aheaditec.sample.R;

import java.util.List;

/**
 * Adapter for RecyclerView holding already paired readers.
 */

public class ScanReadersAdapter extends RecyclerView.Adapter<ScanReadersAdapter.ScanReaderViewHolder> {

    private List<Reader> readers;
    private ViewClickAdapter adapter;

    public ScanReadersAdapter(List<Reader> readers, ViewClickAdapter adapter) {
        if (readers == null || adapter == null) {
            throw new IllegalArgumentException("Readers or adapter is null.");
        }
        this.readers = readers;
        this.adapter = adapter;
    }

    @Override
    public ScanReaderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View readersView = inflater.inflate(R.layout.item_reader_scanned, parent, false);
        return new ScanReaderViewHolder(readersView);
    }

    @Override
    public void onBindViewHolder(ScanReaderViewHolder holder, int position) {
        Reader reader = readers.get(position);

        holder.readerName.setText(reader.getNickname());
        holder.scanButton.setText("Pair");
        holder.scanButton.setOnClickListener(view -> adapter.onPairClicked(reader));
    }

    @Override
    public int getItemCount() {
        return readers.size();
    }

    class ScanReaderViewHolder extends RecyclerView.ViewHolder {

        private TextView readerName;
        private Button scanButton;

        private ScanReaderViewHolder(View itemView) {
            super(itemView);

            this.readerName = itemView.findViewById(R.id.txtItemReaderName);
            this.scanButton = itemView.findViewById(R.id.btnItemConnectPair);
        }
    }

    public interface ViewClickAdapter {
        void onPairClicked(Reader reader);
    }
}
