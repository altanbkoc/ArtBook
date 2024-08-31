package com.example.artbookf.View;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.artbookf.Adapter.MyAdapter;
import com.example.artbookf.Model.Art;
import com.example.artbookf.R;
import com.example.artbookf.Roomdb.ArtDao;
import com.example.artbookf.Roomdb.ArtDatabase;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ListFragment extends Fragment {

    ArtDatabase db;
    ArtDao artDao;
    MyAdapter myAdapter;
    RecyclerView recyclerView;
    CompositeDisposable compositeDisposable = new CompositeDisposable(); // Bellek sızıntısını önlemek için

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        db= Room.databaseBuilder(getContext(),ArtDatabase.class,"Art").build();
        artDao = db.artDao();

        compositeDisposable.add(artDao.getArt()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setAdapter)
        );

        return view;
    }

    private void setAdapter(List<Art> artList) {
        myAdapter = new MyAdapter(artList,artDao);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(myAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
