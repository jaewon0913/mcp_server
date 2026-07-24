package com.example.mcpserver.todo;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * H2 파일 DB에 영구 저장되는 TODO 리스트 툴.
 * 서버를 재시작해도 data/mcpdb.mv.db 파일에 데이터가 남아있습니다.
 */
@Service
public class TodoService {

    private final TodoRepository repository;

    public TodoService(TodoRepository repository) {
        this.repository = repository;
    }

    @Tool(description = "새로운 할 일을 추가합니다")
    @Transactional
    public String addTodo(@ToolParam(description = "할 일 내용") String content) {
        if (content == null || content.isBlank()) {
            return "할 일 내용이 비어 있어 추가할 수 없습니다.";
        }
        TodoItem saved = repository.save(new TodoItem(content.trim()));
        return "할 일이 추가되었습니다: " + saved;
    }

    @Tool(description = "전체 할 일 목록을 조회합니다 (완료 여부 포함)")
    public String listTodos() {
        List<TodoItem> all = repository.findAll();
        if (all.isEmpty()) {
            return "등록된 할 일이 없습니다.";
        }
        return all.stream()
                .map(TodoItem::toString)
                .collect(Collectors.joining("\n"));
    }

    @Tool(description = "아직 완료되지 않은 할 일만 조회합니다")
    public String listPendingTodos() {
        List<TodoItem> pending = repository.findByDoneFalse();
        if (pending.isEmpty()) {
            return "미완료 할 일이 없습니다.";
        }
        return pending.stream()
                .map(TodoItem::toString)
                .collect(Collectors.joining("\n"));
    }

    @Tool(description = "지정한 ID의 할 일을 완료 처리합니다")
    @Transactional
    public String completeTodo(@ToolParam(description = "완료 처리할 할 일의 ID") long id) {
        return repository.findById(id)
                .map(item -> {
                    item.setDone(true);
                    repository.save(item);
                    return "완료 처리되었습니다: " + item;
                })
                .orElse("ID " + id + "에 해당하는 할 일을 찾을 수 없습니다.");
    }

    @Tool(description = "지정한 ID의 할 일을 삭제합니다")
    @Transactional
    public String deleteTodo(@ToolParam(description = "삭제할 할 일의 ID") long id) {
        if (!repository.existsById(id)) {
            return "ID " + id + "에 해당하는 할 일을 찾을 수 없습니다.";
        }
        repository.deleteById(id);
        return "ID " + id + " 할 일이 삭제되었습니다.";
    }
}
