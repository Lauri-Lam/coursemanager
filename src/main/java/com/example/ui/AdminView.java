package com.example.ui;

import com.example.ui.layout.MainLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "admin", layout = MainLayout.class)
@PageTitle("Admin")
public class AdminView extends VerticalLayout {

    public AdminView() {
        addClassName("admin-view");
        setPadding(true);
        setSpacing(true);

        add(
                new H1("Admin-sivu"),
                new Paragraph("Tämä sivu näkyy vain ADMIN-roolilla kirjautuneelle käyttäjälle.")
        );
    }
}