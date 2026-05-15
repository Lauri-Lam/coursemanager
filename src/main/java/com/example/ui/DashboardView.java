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
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Etusivu")
public class DashboardView extends VerticalLayout {

    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

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

        H1 title = new H1("Etusivu");
        title.addClassName("page-title");

        Paragraph intro = new Paragraph("Tämä näkymä kokoaa sovelluksen tärkeimmät tiedot yhteen paikkaan.");

        HorizontalLayout cards = new HorizontalLayout(
                createStatCard("Kategoriat", categoryRepository.count(), "Kurssien pääryhmät"),
                createStatCard("Kurssit", courseRepository.count(), "Tallennetut kurssit"),
                createStatCard("Opiskelijat", studentRepository.count(), "Kurssien opiskelijat")
        );
        cards.setWidthFull();
        cards.setSpacing(true);

        HorizontalLayout quickLinks = new HorizontalLayout(
                createNavigationButton("Kategoriat", "categories"),
                createNavigationButton("Kurssit", "courses"),
                createNavigationButton("Kurssihaku", "courses/search")
        );
        quickLinks.setSpacing(true);

        add(title, intro, cards, quickLinks);
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