package com.projecao.recipeflix.controller;

import com.projecao.recipeflix.dto.RecipeDTO;
import com.projecao.recipeflix.model.Recipe;
import com.projecao.recipeflix.model.User;
import com.projecao.recipeflix.repository.RecipeRepository;
import com.projecao.recipeflix.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class RecipeController {
    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    UserRepository userRepository;

    @PostMapping("/recipe/add")
    public ResponseEntity<?> createRecipe(@RequestBody RecipeDTO recipeDTO) {
        Long id  = getCurrentUserId();

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }

        Recipe recipe = new Recipe(recipeDTO.title(), recipeDTO.description(), recipeDTO.image(), recipeDTO.instructions(), userOptional.get());
        userOptional.get().getRecipes().add(recipe);
        Recipe savedRecipe = recipeRepository.save(recipe);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedRecipe);
    }

    @GetMapping("/recipe/all")
    public List<RecipeDTO> getAllRecipes() {
        List<Recipe> recipes = recipeRepository.findAllWithUser();
        return recipes.stream()
                .map(recipe -> new RecipeDTO(recipe.getTitle(), recipe.getDescription(), recipe.getImage(), recipe.getInstructions(), recipe.getUser().getAuthorName()))
                .collect(Collectors.toList());
    }

   /* @GetMapping("/recipe/all")
    public List<RecipeDTO> getAllRecipes(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Recipe> recipesPage = recipeRepository.findAllWithUser(pageable);
        List<Recipe> recipes = recipesPage.getContent();
        return recipes.stream()
                .map(recipe -> new RecipeDTO(recipe.getTitle(), recipe.getDescription(), recipe.getImage(), recipe.getInstructions(), recipe.getUser().getAuthorName()))
                .collect(Collectors.toList());
    }*/

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Obtém o nome de usuário do Authentication
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return user.getId();
        } else {
            // Se o usuário não for encontrado, você pode lidar com isso de acordo com a sua lógica de negócios.
            throw new UsernameNotFoundException("Usuário não encontrado com o nome: " + username);
        }
    }

}
