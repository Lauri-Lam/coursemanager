package com.example.ui;

import com.example.ui.layout.MainLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "user-super", layout = MainLayout.class)
@PageTitle("Super-sivu")
public class UserSuperView extends VerticalLayout {

    public UserSuperView() {
        addClassName("user-super-view");
        setPadding(true);
        setSpacing(true);

        add(
                new H1("Super-sivu"),
                new Paragraph("Tämä sivu näkyy vain SUPER-roolilla kirjautuneelle käyttäjälle.")
        );
    }
}