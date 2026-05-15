package com.example.ui;

import com.example.domain.Category;
import com.example.domain.Course;
import com.example.repository.CategoryRepository;
import com.example.service.CourseSearchService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.dependency.CssImport;
import com.example.ui.layout.MainLayout;

@Route(value = "courses/search", layout = MainLayout.class)
@PageTitle("Kurssihaku")
@CssImport("./themes/coursemanager/views/course-search-view.css")
public class CourseSearchView extends VerticalLayout {

    private final CourseSearchService courseSearchService;
    private final CategoryRepository categoryRepository;

    private final Grid<Course> grid = new Grid<>(Course.class, false);

    private final TextField nameFilter = new TextField("Kurssin nimi");
    private final IntegerField minCreditsFilter = new IntegerField("Vähimmäisopintopisteet");
    private final Select<String> activeFilter = new Select<>();
    private final DatePicker startDateFromFilter = new DatePicker("Aloituspäivä alkaen");
    private final DatePicker startDateToFilter = new DatePicker("Aloituspäivä päättyen");
    private final ComboBox<Category> categoryFilter = new ComboBox<>("Kategoria");
    private final TextField studentLastNameFilter = new TextField("Opiskelijan sukunimi");

    private final Button searchButton = new Button("Hae");
    private final Button clearButton = new Button("Tyhjennä");

    public CourseSearchView(CourseSearchService courseSearchService,
                            CategoryRepository categoryRepository) {
        this.courseSearchService = courseSearchService;
        this.categoryRepository = categoryRepository;

        addClassName("course-search-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H1 title = new H1("Kurssihaku");
        title.addClassName("page-title");
        title.getStyle()
                .set("letter-spacing", "-0.5px")
                .set("margin-top", "0")
                .set("padding-left", "4px");

        configureFilters();
        configureGrid();

        HorizontalLayout filters = new HorizontalLayout(
                nameFilter,
                minCreditsFilter,
                activeFilter,
                startDateFromFilter,
                startDateToFilter,
                categoryFilter,
                studentLastNameFilter,
                searchButton,
                clearButton
        );

        filters.setAlignItems(Alignment.END);
        filters.setWidthFull();
        filters.setWrap(true);

        add(title, filters, grid);

        refreshGrid();
    }

    private void configureFilters() {
        nameFilter.addClassName("filter-field");
        minCreditsFilter.addClassName("filter-field");
        activeFilter.addClassName("filter-field");
        startDateFromFilter.addClassName("filter-field");
        startDateToFilter.addClassName("filter-field");
        categoryFilter.addClassName("filter-field");
        studentLastNameFilter.addClassName("filter-field");

        searchButton.addClassName("search-button");
        clearButton.addClassName("clear-button");

        nameFilter.setPlaceholder("Esim. Java");
        studentLastNameFilter.setPlaceholder("Esim. Lam");

        minCreditsFilter.setMin(1);
        minCreditsFilter.setStepButtonsVisible(true);

        activeFilter.setLabel("Aktiivisuus");
        activeFilter.setItems("Kaikki", "Aktiivinen", "Ei aktiivinen");
        activeFilter.setValue("Kaikki");

        categoryFilter.setItems(categoryRepository.findAll());
        categoryFilter.setItemLabelGenerator(Category::getName);

        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        clearButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        searchButton.getStyle()
                .set("min-width", "110px")
                .set("height", "42px")
                .set("border-radius", "14px");

        clearButton.getStyle()
                .set("min-width", "110px")
                .set("height", "42px")
                .set("border-radius", "14px");

        searchButton.addClickListener(event -> refreshGrid());

        clearButton.addClickListener(event -> {
            nameFilter.clear();
            minCreditsFilter.clear();
            activeFilter.setValue("Kaikki");
            startDateFromFilter.clear();
            startDateToFilter.clear();
            categoryFilter.clear();
            studentLastNameFilter.clear();
            refreshGrid();
        });
    }

    private void configureGrid() {
        grid.getStyle()
                .set("margin-top", "16px")
                .set("border", "10px solid #ddd6fe")
                .set("border-radius", "25px");

        grid.addColumn(Course::getId)
                .setHeader("ID")
                .setAutoWidth(true);

        grid.addColumn(Course::getName)
                .setHeader("Nimi")
                .setAutoWidth(true);

        grid.addColumn(course -> course.getCategory() != null ? course.getCategory().getName() : "-")
                .setHeader("Kategoria")
                .setAutoWidth(true);

        grid.addColumn(Course::getCredits)
                .setHeader("Opintopisteet")
                .setAutoWidth(true);

        grid.addColumn(course -> course.isActive() ? "Kyllä" : "Ei")
                .setHeader("Aktiivinen")
                .setAutoWidth(true);

        grid.addColumn(Course::getStartDate)
                .setHeader("Aloituspäivä")
                .setAutoWidth(true);

        grid.addColumn(Course::getEndDate)
                .setHeader("Päättymispäivä")
                .setAutoWidth(true);

        grid.setSizeFull();
    }

    private void refreshGrid() {
        Boolean active = null;

        if ("Aktiivinen".equals(activeFilter.getValue())) {
            active = true;
        } else if ("Ei aktiivinen".equals(activeFilter.getValue())) {
            active = false;
        }

        grid.setItems(courseSearchService.searchCourses(
                nameFilter.getValue(),
                minCreditsFilter.getValue(),
                active,
                startDateFromFilter.getValue(),
                startDateToFilter.getValue(),
                categoryFilter.getValue(),
                studentLastNameFilter.getValue()
        ));
    }
}