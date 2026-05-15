package com.example.ui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.Anchor;

@Route("login")
@PageTitle("Kirjautuminen")
public class LoginView extends VerticalLayout {

    public LoginView() {
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H1 title = new H1("Course Manager");

        LoginForm loginForm = new LoginForm();
        loginForm.setAction("login");

        Anchor registerLink = new Anchor("register", "Ei käyttäjää? Rekisteröidy");

        add(title, loginForm, registerLink);
    }
}