package com.example.artbookf.Adapter;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artbookf.Model.Art;
import com.example.artbookf.R;
import com.example.artbookf.Roomdb.ArtDao;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<Art> artList;
    private ArtDao artDao;

    // Yapıcıya ArtDao eklenmiş
    public MyAdapter(List<Art> artList, ArtDao artDao){
        this.artList = artList;
        this.artDao = artDao;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        Art currentArt = artList.get(position);

        holder.nameTextView.setText(currentArt.name);
        holder.textid.setText(String.valueOf(currentArt.id));

        if (currentArt.image != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(currentArt.image, 0, currentArt.image.length);
            holder.imageView.setImageBitmap(bitmap);
        } else {
            holder.imageView.setImageBitmap(null);
        }

        holder.butonSil.setOnClickListener(v -> {

            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Silme Onayı")
                    .setMessage("Bu öğeyi silmek istediğinizden emin misiniz? Bu işlem geri alınamaz.")
                    .setPositiveButton("Evet", (dialog, which) -> {
                        deleteArt(currentArt, holder.itemView);
                    })
                    .setNegativeButton("Hayır", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });
    }

    private void deleteArt(Art art, View itemView) {
        artDao.delete(art)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    // Başarılı silme işlemi sonrası yapılacak işlemler
                    int position = artList.indexOf(art);
                    artList.remove(art);
                    notifyItemRemoved(position);
                }, throwable -> {
                    // Hata durumunda yapılacak işlemler
                    throwable.printStackTrace();
                    Toast.makeText(itemView.getContext(), "Silme hatası", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return artList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        ImageView imageView;
        TextView textid;
        ImageButton butonSil;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView2);
            textid = itemView.findViewById(R.id.txtid);
            butonSil = itemView.findViewById(R.id.btnDelete);
        }
    }
}
