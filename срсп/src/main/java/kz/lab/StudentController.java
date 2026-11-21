package kz.lab;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentController {
    private final Student student = new Student(1, "Aruzhan", "aruzhan@example.kz", 20);

    @GetMapping("/api/student")
    public Student getStudent() { return student; }

    @QueryMapping
    public Student student(@Argument int id) {
        return id == student.id() ? student : null;
    }
}
