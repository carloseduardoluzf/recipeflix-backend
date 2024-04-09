package com.projecao.recipeflix.service;

import com.projecao.recipeflix.repository.RecipeRepository;
import org.springframework.stereotype.Service;

@Service
public class RecipeService {
    private final RecipeRepository repository;

    public RecipeService(RecipeRepository recipeRepository){
        this.repository = recipeRepository;
    }

}
