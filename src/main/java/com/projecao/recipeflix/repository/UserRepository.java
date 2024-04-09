package com.projecao.recipeflix.repository;

import com.projecao.recipeflix.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByAuthorName(String authorName);

    User findByUsername(String username);

}
