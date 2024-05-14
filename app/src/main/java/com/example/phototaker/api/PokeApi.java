package com.example.phototaker.api;

import com.example.phototaker.model.Pokemon;
import com.example.phototaker.model.PokemonResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PokeApi {
    @GET("pokemon?limit=1200")
    Call<PokemonResponse> getAllPokemon();
}