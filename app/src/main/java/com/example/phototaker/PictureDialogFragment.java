package com.example.phototaker;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.phototaker.api.PokeApi;
import com.example.phototaker.model.Pokemon;
import com.example.phototaker.model.PokemonResponse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Random;

public class PictureDialogFragment extends DialogFragment {

    private ImageView imageView;
    private TextView textView;
    private Bitmap takenImage;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // Get the image file path from the arguments
        String imageFilePath = getArguments().getString("imageFilePath");
        //Load the image from the file path
        takenImage = BitmapFactory.decodeFile(imageFilePath);

        // Inflate the layout for the dialog
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_dialog, null);
        dialog.setContentView(view);

        // Initialize the ImageView and TextView
        imageView = view.findViewById(R.id.imageView);
        textView = view.findViewById(R.id.TextName);

        // Set the image to the ImageView
        imageView.setImageBitmap(takenImage);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PokeApi pokeApi = retrofit.create(PokeApi.class);

        pokeApi.getAllPokemon().enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(Call<PokemonResponse> call, Response<PokemonResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Random random = new Random();
                    Pokemon randomPokemon = response.body().getResults().get(random.nextInt(response.body().getResults().size()));
                    textView.setText("You are a " + randomPokemon.getName());
                }
            }

            @Override
            public void onFailure(Call<PokemonResponse> call, Throwable t) {
                // Handle the error
            }
        });

        return dialog;
    }
}