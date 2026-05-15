package com.example.ui;

import com.example.ui.layout.MainLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "authenticated", layout = MainLayout.class)
@PageTitle("Kirjautuneen sivu")
public class AuthenticatedView extends VerticalLayout {

    public AuthenticatedView() {
        addClassName("authenticated-view");
        setPadding(true);
        setSpacing(true);

        add(
                new H1("Kirjautuneen käyttäjän sivu"),
                new Paragraph("Tämä sivu näkyy kaikille kirjautuneille käyttäjille roolista riippumatta.")
        );
    }
}