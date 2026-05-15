package com.example.ui;

import com.example.domain.Category;
import com.example.repository.CategoryRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.example.ui.layout.MainLayout;

@Route(value = "categories", layout = MainLayout.class)
@PageTitle("Kategoriat")
public class CategoryView extends VerticalLayout {

    private final CategoryRepository categoryRepository;

    private final Grid<Category> grid = new Grid<>(Category.class, false);

    private final TextField name = new TextField("Nimi");
    private final TextArea description = new TextArea("Kuvaus");

    private final Button saveButton = new Button("Tallenna");
    private final Button deleteButton = new Button("Poista");
    private final Button clearButton = new Button("Tyhjennä");

    private final Binder<Category> binder = new BeanValidationBinder<>(Category.class);

    private Category selectedCategory;

    public CategoryView(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;

        addClassName("category-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H1 title = new H1("Kategoriat");

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
        grid.addColumn(Category::getId)
                .setHeader("ID")
                .setAutoWidth(true);

        grid.addColumn(Category::getName)
                .setHeader("Nimi")
                .setAutoWidth(true);

        grid.addColumn(Category::getDescription)
                .setHeader("Kuvaus")
                .setAutoWidth(true);

        grid.addColumn(Category::getDescription)
                .setHeader("Kuvaus")
                .setAutoWidth(true);

        grid.addColumn(category -> category.getCategoryDetail() != null
                        ? category.getCategoryDetail().getResponsiblePerson()
                        : "-")
                .setHeader("Vastuuhenkilö")
                .setAutoWidth(true);

        grid.addColumn(category -> category.getCategoryDetail() != null
                        ? category.getCategoryDetail().getTargetAudience()
                        : "-")
                .setHeader("Kohderyhmä")
                .setAutoWidth(true);

        grid.setSizeFull();

        grid.asSingleSelect().addValueChangeListener(event -> {
            selectedCategory = event.getValue();

            if (selectedCategory != null) {
                binder.readBean(selectedCategory);
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

        description.setMinHeight("120px");

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        clearButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout buttons = new HorizontalLayout(saveButton, deleteButton, clearButton);

        formLayout.add(name, description, buttons);
        formLayout.setWidth("350px");
        formLayout.setPadding(true);
        formLayout.setSpacing(true);

        return formLayout;
    }

    private void configureForm() {
        binder.bindInstanceFields(this);

        saveButton.addClickListener(event -> saveCategory());
        deleteButton.addClickListener(event -> deleteCategory());
        clearButton.addClickListener(event -> clearForm());
    }

    private void saveCategory() {
        try {
            if (selectedCategory == null) {
                selectedCategory = new Category();
            }

            binder.writeBean(selectedCategory);
            categoryRepository.save(selectedCategory);

            Notification.show("Kategoria tallennettu");
            refreshGrid();
            clearForm();

        } catch (Exception e) {
            Notification.show("Tallennus epäonnistui: tarkista kentät");
        }
    }

    private void deleteCategory() {
        if (selectedCategory != null && selectedCategory.getId() != null) {
            categoryRepository.delete(selectedCategory);

            Notification.show("Kategoria poistettu");
            refreshGrid();
            clearForm();
        }
    }

    private void clearForm() {
        selectedCategory = null;
        binder.readBean(new Category());
        grid.asSingleSelect().clear();
        deleteButton.setEnabled(false);
    }

    private void refreshGrid() {
        grid.setItems(categoryRepository.findAll());
    }
}