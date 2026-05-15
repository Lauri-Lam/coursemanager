package com.example.ui.layout;

import com.example.ui.AdminView;
import com.example.ui.AuthenticatedView;
import com.example.ui.CategoryDetailView;
import com.example.ui.CategoryView;
import com.example.ui.CourseSearchView;
import com.example.ui.CourseView;
import com.example.ui.DashboardView;
import com.example.ui.StudentView;
import com.example.ui.UserSuperView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        DrawerToggle toggle = new DrawerToggle();

        Span appName = new Span("Course Manager");
        appName.getStyle()
                .set("font-size", "24px")
                .set("font-weight", "800")
                .set("color", "white")
                .set("margin-right", "24px");

        Span userInfo = new Span("Käyttäjä: " + getCurrentUsername());
        userInfo.getStyle()
                .set("font-weight", "700")
                .set("color", "white")
                .set("margin-left", "auto");

        HorizontalLayout header = new HorizontalLayout();
        header.add(toggle, appName, userInfo);

        if (isLoggedIn()) {
            Button logoutButton = new Button("Kirjaudu ulos", VaadinIcon.SIGN_OUT.create());
            logoutButton.getStyle()
                    .set("border-radius", "12px")
                    .set("font-weight", "700");

            logoutButton.addClickListener(event -> logout());

            header.add(logoutButton);
        }

        header.setWidthFull();
        header.setAlignItems(HorizontalLayout.Alignment.CENTER);
        header.setPadding(true);
        header.setSpacing(true);
        header.getStyle()
                .set("background", "var(--app-color-dark)")
                .set("color", "white");

        addToNavbar(header);
    }

    private void createDrawer() {
        VerticalLayout navigation = new VerticalLayout();
        navigation.setPadding(true);
        navigation.setSpacing(false);
        navigation.getStyle().set("gap", "8px");

        navigation.add(createNavLink("Etusivu", VaadinIcon.HOME, DashboardView.class));

        if (isLoggedIn()) {
            navigation.add(createNavLink("Kategoriat", VaadinIcon.TAGS, CategoryView.class));
            navigation.add(createNavLink("Kategorian lisätiedot", VaadinIcon.INFO_CIRCLE, CategoryDetailView.class));
            navigation.add(createNavLink("Kurssit", VaadinIcon.BOOK, CourseView.class));
            navigation.add(createNavLink("Opiskelijat", VaadinIcon.USERS, StudentView.class));
            navigation.add(createNavLink("Kurssihaku", VaadinIcon.SEARCH, CourseSearchView.class));
            navigation.add(createNavLink("Kirjautuneen sivu", VaadinIcon.USER, AuthenticatedView.class));
        }

        if (hasRole("SUPER")) {
            navigation.add(createNavLink("Super-sivu", VaadinIcon.USERS, UserSuperView.class));
        }

        if (hasRole("ADMIN")) {
            navigation.add(createNavLink("Admin", VaadinIcon.SHIELD, AdminView.class));
        }

        Footer footer = new Footer();
        footer.add(new Span("© 2026 Course Manager"));
        footer.getStyle()
                .set("margin-top", "auto")
                .set("padding", "16px")
                .set("font-size", "13px")
                .set("color", "var(--lumo-secondary-text-color)");

        VerticalLayout drawerContent = new VerticalLayout(navigation, footer);
        drawerContent.setSizeFull();
        drawerContent.setPadding(false);
        drawerContent.setSpacing(false);

        addToDrawer(drawerContent);
    }

    private RouterLink createNavLink(String text, VaadinIcon icon, Class<? extends Component> navigationTarget) {
        RouterLink link = new RouterLink(text, navigationTarget);

        link.getElement().insertChild(0, icon.create().getElement());

        link.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "10px")
                .set("padding", "10px 14px")
                .set("border-radius", "12px")
                .set("text-decoration", "none")
                .set("color", "var(--app-color-dark)")
                .set("font-weight", "700");

        return link;
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return "Vierailija";
        }

        return authentication.getName();
    }

    private boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName());
    }

    private boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }

    private void logout() {
        UI.getCurrent().getPage().setLocation("/logout");
    }
}