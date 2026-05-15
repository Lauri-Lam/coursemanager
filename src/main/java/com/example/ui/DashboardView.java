package com.example.ui;

import com.example.repository.CategoryRepository;
import com.example.repository.CourseRepository;
import com.example.repository.StudentRepository;
import com.example.ui.layout.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard")
public class DashboardView extends VerticalLayout {

    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    private final H1 title = new H1();
    private final Paragraph intro = new Paragraph();

    private final HorizontalLayout cards = new HorizontalLayout();
    private final HorizontalLayout quickLinks = new HorizontalLayout();

    private String language = "fi";

    public DashboardView(CategoryRepository categoryRepository,
                         CourseRepository courseRepository,
                         StudentRepository studentRepository) {
        this.categoryRepository = categoryRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            UI.getCurrent().navigate("login");
            return;
        }

        addClassName("dashboard-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        title.addClassName("page-title");

        RadioButtonGroup<String> languageSelector = new RadioButtonGroup<>();
        languageSelector.setLabel("Kieli / Language");
        languageSelector.setItems("Suomi", "English");
        languageSelector.setValue("Suomi");

        languageSelector.addValueChangeListener(event -> {
            if ("English".equals(event.getValue())) {
                language = "en";
            } else {
                language = "fi";
            }

            updateTexts();
        });

        cards.setWidthFull();
        cards.setSpacing(true);

        quickLinks.setSpacing(true);

        add(languageSelector, title, intro, cards, quickLinks);

        updateTexts();
    }

    private void updateTexts() {
        cards.removeAll();
        quickLinks.removeAll();

        if ("en".equals(language)) {
            title.setText("Dashboard");
            intro.setText("This view gathers the most important information about the application in one place.");

            cards.add(
                    createStatCard("Categories", categoryRepository.count(), "Main groups of courses"),
                    createStatCard("Courses", courseRepository.count(), "Saved courses"),
                    createStatCard("Students", studentRepository.count(), "Students enrolled in courses")
            );

            quickLinks.add(
                    createNavigationButton("Categories", "categories"),
                    createNavigationButton("Courses", "courses"),
                    createNavigationButton("Course search", "courses/search")
            );
        } else {
            title.setText("Etusivu");
            intro.setText("Tämä näkymä kokoaa sovelluksen tärkeimmät tiedot yhteen paikkaan.");

            cards.add(
                    createStatCard("Kategoriat", categoryRepository.count(), "Kurssien pääryhmät"),
                    createStatCard("Kurssit", courseRepository.count(), "Tallennetut kurssit"),
                    createStatCard("Opiskelijat", studentRepository.count(), "Kurssien opiskelijat")
            );

            quickLinks.add(
                    createNavigationButton("Kategoriat", "categories"),
                    createNavigationButton("Kurssit", "courses"),
                    createNavigationButton("Kurssihaku", "courses/search")
            );
        }
    }

    private Div createStatCard(String title, long value, String description) {
        Div card = new Div();
        card.addClassName("dashboard-card");

        Span valueText = new Span(String.valueOf(value));
        valueText.addClassName("dashboard-card-value");

        Span titleText = new Span(title);
        titleText.addClassName("dashboard-card-title");

        Paragraph descriptionText = new Paragraph(description);
        descriptionText.addClassName("dashboard-card-description");

        card.add(valueText, titleText, descriptionText);

        return card;
    }

    private Button createNavigationButton(String text, String route) {
        Button button = new Button(text);
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        button.addClickListener(event ->
                button.getUI().ifPresent(ui -> ui.navigate(route))
        );

        return button;
    }
}