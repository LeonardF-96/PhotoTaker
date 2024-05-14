package com.example.phototaker;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
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
        // Create a new dialog
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // Get the image file path from the arguments

        String imageFilePath = getArguments().getString("imageFilePath");
        //Load the image from the file path
        takenImage = BitmapFactory.decodeFile(imageFilePath);

        // Inflate the layout for the dialog
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_dialog, null);
        // Set the inflated layout as the content view for the dialog
        dialog.setContentView(view);

        // Initialize the ImageView and TextView from the inflated layout
        imageView = view.findViewById(R.id.imageView);
        textView = view.findViewById(R.id.TextName);

        // Set the loaded image to the ImageView
        imageView.setImageBitmap(takenImage);

        // Create a Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create a PokeApi instance from the Retrofit instance
        PokeApi pokeApi = retrofit.create(PokeApi.class);

        // Make an asynchronous request to get all Pokemon
        pokeApi.getAllPokemon().enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(Call<PokemonResponse> call, Response<PokemonResponse> response) {
                // This method is called when the request gets a response
                if (response.isSuccessful() && response.body() != null) {
                    // If the response is successful and contains a body
                    Random random = new Random();
                    // Select a random Pokemon from the response
                    Pokemon randomPokemon = response.body().getResults().get(random.nextInt(response.body().getResults().size()));
                    // Set the name of the random Pokemon to the TextView
                    textView.setText("You are a " + randomPokemon.getName());
                }
            }

            @Override
            public void onFailure(Call<PokemonResponse> call, Throwable t) {
                // Handle the error
                Log.e("PictureDialogFragment", "Request failed: " + t.getMessage(), t);
            }
        });

        // Return the created dialog
        return dialog;
    }
}