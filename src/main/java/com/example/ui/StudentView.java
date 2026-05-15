package com.example.ui;

import com.example.domain.Course;
import com.example.domain.Student;
import com.example.repository.CourseRepository;
import com.example.repository.StudentRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.stream.Collectors;
import com.example.ui.layout.MainLayout;

@Route(value = "students", layout = MainLayout.class)
@PageTitle("Opiskelijat")
public class StudentView extends VerticalLayout {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    private final Grid<Student> grid = new Grid<>(Student.class, false);

    private final TextField firstName = new TextField("Etunimi");
    private final TextField lastName = new TextField("Sukunimi");
    private final EmailField email = new EmailField("Sähköposti");
    private final MultiSelectComboBox<Course> courses = new MultiSelectComboBox<>("Kurssit");

    private final Button saveButton = new Button("Tallenna");
    private final Button deleteButton = new Button("Poista");
    private final Button clearButton = new Button("Tyhjennä");

    private final Binder<Student> binder = new BeanValidationBinder<>(Student.class);

    private Student selectedStudent;

    public StudentView(StudentRepository studentRepository, CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;

        addClassName("student-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H1 title = new H1("Opiskelijat");

        configureGrid();
        configureForm();

        VerticalLayout formLayout = createFormLayout();

        HorizontalLayout content = new HorizontalLayout(grid, formLayout);
        content.setSizeFull();
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, formLayout);

        add(title, content);

        refreshGrid();
        clearForm();
    }

    private void configureGrid() {
        grid.addColumn(Student::getId)
                .setHeader("ID")
                .setAutoWidth(true);

        grid.addColumn(Student::getFirstName)
                .setHeader("Etunimi")
                .setAutoWidth(true);

        grid.addColumn(Student::getLastName)
                .setHeader("Sukunimi")
                .setAutoWidth(true);

        grid.addColumn(Student::getEmail)
                .setHeader("Sähköposti")
                .setAutoWidth(true);

        grid.addColumn(student -> student.getCourses().stream()
                        .map(Course::getName)
                        .collect(Collectors.joining(", ")))
                .setHeader("Kurssit")
                .setAutoWidth(true);

        grid.setSizeFull();

        grid.asSingleSelect().addValueChangeListener(event -> {
            selectedStudent = event.getValue();

            if (selectedStudent != null) {
                binder.readBean(selectedStudent);
                courses.setValue(selectedStudent.getCourses());
                deleteButton.setEnabled(true);
            } else {
                clearForm();
            }
        });
    }

    private VerticalLayout createFormLayout() {
        VerticalLayout formLayout = new VerticalLayout();

        firstName.setWidthFull();
        lastName.setWidthFull();
        email.setWidthFull();
        courses.setWidthFull();

        courses.setItemLabelGenerator(Course::getName);

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        clearButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout buttons = new HorizontalLayout(saveButton, deleteButton, clearButton);

        formLayout.add(firstName, lastName, email, courses, buttons);
        formLayout.setWidth("400px");
        formLayout.setPadding(true);
        formLayout.setSpacing(true);

        return formLayout;
    }

    private void configureForm() {
        binder.bindInstanceFields(this);

        saveButton.addClickListener(event -> saveStudent());
        deleteButton.addClickListener(event -> deleteStudent());
        clearButton.addClickListener(event -> clearForm());
    }

    private void saveStudent() {
        try {
            if (selectedStudent == null) {
                selectedStudent = new Student();
            }

            binder.writeBean(selectedStudent);
            selectedStudent.setCourses(courses.getValue());
            studentRepository.save(selectedStudent);

            Notification.show("Opiskelija tallennettu");
            refreshGrid();
            clearForm();

        } catch (Exception e) {
            Notification.show("Tallennus epäonnistui: tarkista kentät");
        }
    }

    private void deleteStudent() {
        if (selectedStudent != null && selectedStudent.getId() != null) {
            studentRepository.delete(selectedStudent);

            Notification.show("Opiskelija poistettu");
            refreshGrid();
            clearForm();
        }
    }

    private void clearForm() {
        selectedStudent = null;
        refreshCourseComboBox();
        binder.readBean(new Student());
        courses.clear();
        grid.asSingleSelect().clear();
        deleteButton.setEnabled(false);
    }

    private void refreshGrid() {
        grid.setItems(studentRepository.findAll());
    }

    private void refreshCourseComboBox() {
        courses.setItems(courseRepository.findAll());
    }
}