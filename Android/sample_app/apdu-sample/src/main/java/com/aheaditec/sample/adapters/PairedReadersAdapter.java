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

public class PairedReadersAdapter extends RecyclerView.Adapter<PairedReadersAdapter.PairedReadersViewHolder> {

    private List<Reader> readers;
    private ViewClickAdapter adapter;

    public PairedReadersAdapter(List<Reader> readers, ViewClickAdapter adapter) {
        if (readers == null || adapter == null) {
            throw new IllegalArgumentException("Readers or adapter is null.");
        }
        this.readers = readers;
        this.adapter = adapter;
    }

    @Override
    public PairedReadersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View readersView = inflater.inflate(R.layout.item_reader_paired, parent, false);
        return new PairedReadersViewHolder(readersView);
    }

    @Override
    public void onBindViewHolder(PairedReadersViewHolder holder, int position) {
        Reader reader = readers.get(position);

        holder.readerName.setText(reader.getNickname());
        holder.unpairButton.setOnClickListener(view -> adapter.onUnpairClick(reader));
        holder.renameButton.setOnClickListener(view -> adapter.onRenameClick(reader));
    }

    @Override
    public int getItemCount() {
        return readers.size();
    }

    class PairedReadersViewHolder extends RecyclerView.ViewHolder {

        private TextView readerName;
        private Button renameButton;
        private Button unpairButton;

        private PairedReadersViewHolder(View itemView) {
            super(itemView);

            this.readerName = itemView.findViewById(R.id.txtItemReaderName);
            this.renameButton = itemView.findViewById(R.id.btnItemRename);
            this.unpairButton = itemView.findViewById(R.id.btnItemUnpair);
        }
    }

    public interface ViewClickAdapter {

        void onUnpairClick(Reader reader);

        void onRenameClick(Reader reader);
    }
}
