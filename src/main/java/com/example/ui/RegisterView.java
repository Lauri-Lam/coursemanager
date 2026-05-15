package com.example.ui;

import com.example.domain.AppUser;
import com.example.domain.Role;
import com.example.repository.AppUserRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.crypto.password.PasswordEncoder;

@Route("register")
@PageTitle("Rekisteröityminen")
public class RegisterView extends VerticalLayout {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    private final TextField username = new TextField("Käyttäjänimi");
    private final PasswordField password = new PasswordField("Salasana");
    private final PasswordField confirmPassword = new PasswordField("Vahvista salasana");

    public RegisterView(AppUserRepository appUserRepository,
                        PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;

        addClassName("register-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H1 title = new H1("Rekisteröidy");

        username.setWidth("300px");
        password.setWidth("300px");
        confirmPassword.setWidth("300px");

        Button registerButton = new Button("Rekisteröidy");
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.setWidth("300px");

        registerButton.addClickListener(event -> registerUser());

        Anchor loginLink = new Anchor("login", "Onko sinulla jo käyttäjä? Kirjaudu sisään");

        add(
                title,
                username,
                password,
                confirmPassword,
                registerButton,
                loginLink
        );
    }

    private void registerUser() {
        String usernameValue = username.getValue();
        String passwordValue = password.getValue();
        String confirmPasswordValue = confirmPassword.getValue();

        if (usernameValue == null || usernameValue.isBlank()) {
            Notification.show("Käyttäjänimi ei voi olla tyhjä");
            return;
        }

        if (passwordValue == null || passwordValue.length() < 6) {
            Notification.show("Salasanan pitää olla vähintään 6 merkkiä pitkä");
            return;
        }

        if (!passwordValue.equals(confirmPasswordValue)) {
            Notification.show("Salasanat eivät täsmää");
            return;
        }

        if (appUserRepository.existsByUsername(usernameValue)) {
            Notification.show("Käyttäjänimi on jo käytössä");
            return;
        }

        AppUser newUser = new AppUser(
                usernameValue,
                passwordEncoder.encode(passwordValue),
                Role.USER
        );

        appUserRepository.save(newUser);

        Notification.show("Rekisteröityminen onnistui");
        UI.getCurrent().navigate("login");
    }
}