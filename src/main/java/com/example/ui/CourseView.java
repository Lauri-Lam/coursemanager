package com.example.ui;

import com.example.domain.Category;
import com.example.domain.Course;
import com.example.repository.CategoryRepository;
import com.example.repository.CourseRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.example.ui.layout.MainLayout;

@Route(value = "courses", layout = MainLayout.class)
@PageTitle("Kurssit")
public class CourseView extends VerticalLayout {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;

    private final Grid<Course> grid = new Grid<>(Course.class, false);

    private final TextField name = new TextField("Nimi");
    private final TextArea description = new TextArea("Kuvaus");
    private final DatePicker startDate = new DatePicker("Aloituspäivä");
    private final DatePicker endDate = new DatePicker("Päättymispäivä");
    private final IntegerField credits = new IntegerField("Opintopisteet");
    private final Checkbox active = new Checkbox("Aktiivinen");
    private final ComboBox<Category> category = new ComboBox<>("Kategoria");

    private final Button saveButton = new Button("Tallenna");
    private final Button deleteButton = new Button("Poista");
    private final Button clearButton = new Button("Tyhjennä");

    private final Binder<Course> binder = new BeanValidationBinder<>(Course.class);

    private Course selectedCourse;

    public CourseView(CourseRepository courseRepository, CategoryRepository categoryRepository) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;

        addClassName("course-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H1 title = new H1("Kurssit");

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
        grid.addColumn(Course::getId)
                .setHeader("ID")
                .setAutoWidth(true);

        grid.addColumn(Course::getName)
                .setHeader("Nimi")
                .setAutoWidth(true);

        grid.addColumn(course -> course.getCategory() != null ? course.getCategory().getName() : "-")
                .setHeader("Kategoria")
                .setAutoWidth(true);

        grid.addColumn(Course::getStartDate)
                .setHeader("Aloituspäivä")
                .setAutoWidth(true);

        grid.addColumn(Course::getEndDate)
                .setHeader("Päättymispäivä")
                .setAutoWidth(true);

        grid.addColumn(Course::getCredits)
                .setHeader("Opintopisteet")
                .setAutoWidth(true);

        grid.addColumn(course -> course.isActive() ? "Kyllä" : "Ei")
                .setHeader("Aktiivinen")
                .setAutoWidth(true);

        grid.setSizeFull();

        grid.asSingleSelect().addValueChangeListener(event -> {
            selectedCourse = event.getValue();

            if (selectedCourse != null) {
                binder.readBean(selectedCourse);
                deleteButton.setEnabled(true);
            } else {
                clearForm();
            }
        });
    }

    private VerticalLayout createFormLayout() {
        VerticalLayout formLayout = new VerticalLayout();

        name.setWidthFull();
        description.setWidthFull();
        description.setMinHeight("100px");
        startDate.setWidthFull();
        endDate.setWidthFull();
        credits.setWidthFull();
        category.setWidthFull();

        category.setItemLabelGenerator(Category::getName);

        credits.setMin(1);
        credits.setStepButtonsVisible(true);

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        clearButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout buttons = new HorizontalLayout(saveButton, deleteButton, clearButton);

        formLayout.add(
                name,
                description,
                startDate,
                endDate,
                credits,
                active,
                category,
                buttons
        );

        formLayout.setWidth("400px");
        formLayout.setPadding(true);
        formLayout.setSpacing(true);

        return formLayout;
    }

    private void configureForm() {
        binder.bindInstanceFields(this);

        saveButton.addClickListener(event -> saveCourse());
        deleteButton.addClickListener(event -> deleteCourse());
        clearButton.addClickListener(event -> clearForm());
    }

    private void saveCourse() {
        try {
            if (selectedCourse == null) {
                selectedCourse = new Course();
            }

            binder.writeBean(selectedCourse);
            courseRepository.save(selectedCourse);

            Notification.show("Kurssi tallennettu");
            refreshGrid();
            clearForm();

        } catch (Exception e) {
            Notification.show("Tallennus epäonnistui: tarkista kentät");
        }
    }

    private void deleteCourse() {
        if (selectedCourse != null && selectedCourse.getId() != null) {
            courseRepository.delete(selectedCourse);

            Notification.show("Kurssi poistettu");
            refreshGrid();
            clearForm();
        }
    }

    private void clearForm() {
        selectedCourse = null;
        refreshCategoryComboBox();
        binder.readBean(new Course());
        grid.asSingleSelect().clear();
        deleteButton.setEnabled(false);
    }

    private void refreshGrid() {
        grid.setItems(courseRepository.findAll());
    }

    private void refreshCategoryComboBox() {
        category.setItems(categoryRepository.findAll());
    }
}