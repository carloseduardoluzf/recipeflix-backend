package com.projecao.recipeflix.repository;

import com.projecao.recipeflix.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Query("SELECT r FROM Recipe r JOIN FETCH r.user u")
    List<Recipe> findAllWithUser();
}
