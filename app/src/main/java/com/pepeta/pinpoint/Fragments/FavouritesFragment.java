package com.pepeta.pinpoint.Fragments;

import static com.pepeta.pinpoint.FunctionalUtil.showMessageErrorSnackBar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pepeta.pinpoint.BuildConfig;
import com.pepeta.pinpoint.Constants;
import com.pepeta.pinpoint.FavouritesRecyclerViewAdapter;
import com.pepeta.pinpoint.Model.PlaceDetails.DetailsModel;
import com.pepeta.pinpoint.R;
import com.pepeta.pinpoint.User;
import com.pepeta.pinpoint.WebServices.RetrofitAPI;
import com.pepeta.pinpoint.WebServices.RetrofitClient;
import com.pepeta.pinpoint.databinding.FavouritePlaceRowBinding;
import com.pepeta.pinpoint.databinding.FragmentFavouritesBinding;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavouritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavouritesFragment extends Fragment {

    private final CompositeDisposable compositeDisposable;
    private final RetrofitAPI googleMapsService;
    FragmentFavouritesBinding binding;
    FavouritesRecyclerViewAdapter favouritesRecyclerViewAdapter;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference dbReference;
    private DatabaseReference dbFavourites;
    private final List<String> placeIdList= new ArrayList<>();
    private final List<DetailsModel> placesList = new ArrayList<>();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER_ID = "userID";

    // TODO: Rename and change types of parameters
    private String userID;
    private User user;

    public FavouritesFragment() {
        // Required empty public constructor
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        googleMapsService = retrofit.create(RetrofitAPI.class);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userID
     * @return A new instance of fragment FavouritesFragment.
     */
    public static FavouritesFragment newInstance(String userID) {
        FavouritesFragment fragment = new FavouritesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userID = getArguments().getString(ARG_USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFavouritesBinding.inflate(getLayoutInflater());
        favouritesRecyclerViewAdapter = new FavouritesRecyclerViewAdapter();
        binding.rvFavouritePlaces.setAdapter(favouritesRecyclerViewAdapter);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvFavouritePlaces);
        if (!userID.isEmpty()){
            mAuth = FirebaseAuth.getInstance();
            database = FirebaseDatabase.getInstance();
            dbReference = database.getReference();
            dbFavourites= dbReference.child(Constants.NODE_FAVOURITES);
            setFavouritePlacesIDList();
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
    private void initFavouritesList(){
        setFavouritePlacesIDList();
    }

    private void getUserFavouritePlaces() {
        if (placeIdList.size()>0){
            if (placesList.size()<=0){
                for (String placeId : placeIdList) {

                    compositeDisposable.add(
                            googleMapsService.getPlaceDetails(
                                    placeId,
                                    BuildConfig.MAPS_API_KEY)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            placeDetailsRootModel -> {
                                                placesList.add(placeDetailsRootModel.getDetails());
                                                favouritesRecyclerViewAdapter.updateFavouritePlacesList(placesList);
                                            },
                                            throwable -> Log.d("MYERROR", "accept: " + throwable.getMessage())
                                    )
                    );
                }
            }
        }
    }

    private void setFavouritePlacesIDList() {
        if (placeIdList.size()<=0){
            dbFavourites.child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            String placeID = dataSnapshot.getKey();
                            if (!placeIdList.contains(placeID)) placeIdList.add(placeID);
                        }
                        getUserFavouritePlaces();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            /*DetailsModel place = placesList.get(position);
            removeFromFavourites(place,position);*/
            dbFavourites.child(userID).child(placesList.get(position).getPlaceId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        placeIdList.remove(position);
                        placesList.remove(position);
//                    favouritesRecyclerViewAdapter.notifyItemRemoved(position);
                        favouritesRecyclerViewAdapter.updateFavouritePlacesList(placesList);
                        showMessageErrorSnackBar(binding.favouritesFragmentLayout,"Place successfully removed from Favourites",false);
                    }else{
                        showMessageErrorSnackBar(binding.favouritesFragmentLayout,task.getException().getMessage(),true);
                    }
                }
            });
        }
    };

/*    private void removeFromFavourites(DetailsModel details,int position) {
        dbFavourites.child(userID).child(details.getPlaceId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    placeIdList.remove(position);
                    placesList.remove(position);
//                    favouritesRecyclerViewAdapter.notifyItemRemoved(position);
                    favouritesRecyclerViewAdapter.updateFavouritePlacesList(placesList);
                    showMessageErrorSnackBar(binding.favouritesFragmentLayout,"Place successfully removed from Favourites",false);
                }else{
                    showMessageErrorSnackBar(binding.favouritesFragmentLayout,task.getException().getMessage(),true);
                }
            }
        });
    }*/

}