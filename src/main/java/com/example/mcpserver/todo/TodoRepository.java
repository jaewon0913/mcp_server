package com.example.mcpserver.todo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<TodoItem, Long> {

    List<TodoItem> findByDoneFalse();

    List<TodoItem> findByDoneTrue();
}
