package com.example.ui;

import com.example.domain.Category;
import com.example.domain.CategoryDetail;
import com.example.repository.CategoryDetailRepository;
import com.example.repository.CategoryRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
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

import java.util.Optional;

@Route(value = "category-details", layout = MainLayout.class)
@PageTitle("Kategorian lisätiedot")
public class CategoryDetailView extends VerticalLayout {

    private final CategoryDetailRepository categoryDetailRepository;
    private final CategoryRepository categoryRepository;

    private final Grid<CategoryDetail> grid = new Grid<>(CategoryDetail.class, false);

    private final ComboBox<Category> category = new ComboBox<>("Kategoria");
    private final TextField responsiblePerson = new TextField("Vastuuhenkilö");
    private final TextField targetAudience = new TextField("Kohderyhmä");
    private final TextArea extraInfo = new TextArea("Lisätiedot");

    private final Button saveButton = new Button("Tallenna");
    private final Button deleteButton = new Button("Poista");
    private final Button clearButton = new Button("Tyhjennä");

    private final Binder<CategoryDetail> binder = new BeanValidationBinder<>(CategoryDetail.class);

    private CategoryDetail selectedCategoryDetail;

    public CategoryDetailView(CategoryDetailRepository categoryDetailRepository,
                              CategoryRepository categoryRepository) {
        this.categoryDetailRepository = categoryDetailRepository;
        this.categoryRepository = categoryRepository;

        addClassName("category-detail-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H1 title = new H1("Kategorian lisätiedot");

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
        grid.addColumn(CategoryDetail::getId)
                .setHeader("ID")
                .setAutoWidth(true);

        grid.addColumn(detail -> detail.getCategory() != null ? detail.getCategory().getName() : "-")
                .setHeader("Kategoria")
                .setAutoWidth(true);

        grid.addColumn(CategoryDetail::getResponsiblePerson)
                .setHeader("Vastuuhenkilö")
                .setAutoWidth(true);

        grid.addColumn(CategoryDetail::getTargetAudience)
                .setHeader("Kohderyhmä")
                .setAutoWidth(true);

        grid.addColumn(CategoryDetail::getExtraInfo)
                .setHeader("Lisätiedot")
                .setAutoWidth(true);

        grid.setSizeFull();

        grid.asSingleSelect().addValueChangeListener(event -> {
            selectedCategoryDetail = event.getValue();

            if (selectedCategoryDetail != null) {
                binder.readBean(selectedCategoryDetail);
                deleteButton.setEnabled(true);
            } else {
                clearForm();
            }
        });
    }

    private VerticalLayout createFormLayout() {
        VerticalLayout formLayout = new VerticalLayout();

        category.setWidthFull();
        responsiblePerson.setWidthFull();
        targetAudience.setWidthFull();
        extraInfo.setWidthFull();
        extraInfo.setMinHeight("120px");

        category.setItemLabelGenerator(Category::getName);

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        clearButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout buttons = new HorizontalLayout(saveButton, deleteButton, clearButton);

        formLayout.add(category, responsiblePerson, targetAudience, extraInfo, buttons);
        formLayout.setWidth("400px");
        formLayout.setPadding(true);
        formLayout.setSpacing(true);

        return formLayout;
    }

    private void configureForm() {
        binder.bindInstanceFields(this);

        saveButton.addClickListener(event -> saveCategoryDetail());
        deleteButton.addClickListener(event -> deleteCategoryDetail());
        clearButton.addClickListener(event -> clearForm());
    }

    private void saveCategoryDetail() {
        try {
            if (selectedCategoryDetail == null) {
                selectedCategoryDetail = new CategoryDetail();
            }

            Category selectedCategory = category.getValue();

            if (selectedCategory == null) {
                Notification.show("Valitse kategoria");
                return;
            }

            Optional<CategoryDetail> existingDetail = categoryDetailRepository.findByCategory(selectedCategory);

            if (existingDetail.isPresent()
                    && selectedCategoryDetail.getId() == null) {
                Notification.show("Tällä kategorialla on jo lisätiedot");
                return;
            }

            if (existingDetail.isPresent()
                    && selectedCategoryDetail.getId() != null
                    && !existingDetail.get().getId().equals(selectedCategoryDetail.getId())) {
                Notification.show("Tällä kategorialla on jo lisätiedot");
                return;
            }

            binder.writeBean(selectedCategoryDetail);
            categoryDetailRepository.save(selectedCategoryDetail);

            Notification.show("Lisätiedot tallennettu");
            refreshGrid();
            clearForm();

        } catch (Exception e) {
            Notification.show("Tallennus epäonnistui: tarkista kentät");
        }
    }

    private void deleteCategoryDetail() {
        if (selectedCategoryDetail != null && selectedCategoryDetail.getId() != null) {
            categoryDetailRepository.delete(selectedCategoryDetail);

            Notification.show("Lisätiedot poistettu");
            refreshGrid();
            clearForm();
        }
    }

    private void clearForm() {
        selectedCategoryDetail = null;
        refreshCategoryComboBox();
        binder.readBean(new CategoryDetail());
        grid.asSingleSelect().clear();
        deleteButton.setEnabled(false);
    }

    private void refreshGrid() {
        grid.setItems(categoryDetailRepository.findAll());
    }

    private void refreshCategoryComboBox() {
        category.setItems(categoryRepository.findAll());
    }
}