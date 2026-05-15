package com.example.ui;

import com.example.ui.layout.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "push", layout = MainLayout.class)
@PageTitle("Server Push")
public class PushView extends VerticalLayout {

    private final Span statusText = new Span("Push-toimintoa ei ole vielä käynnistetty.");

    public PushView() {
        addClassName("push-view");
        setPadding(true);
        setSpacing(true);

        H1 title = new H1("Vaadin Server Push");
        title.addClassName("page-title");

        Paragraph description = new Paragraph(
                "Tässä näkymässä käyttöliittymä päivittyy taustaprosessin avulla ilman sivun uudelleenlatausta."
        );

        Button startButton = new Button("Käynnistä taustapäivitys");
        startButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        startButton.addClickListener(event -> startBackgroundTask());

        add(title, description, statusText, startButton);
    }

    private void startBackgroundTask() {
        UI ui = UI.getCurrent();

        statusText.setText("Taustaprosessi käynnissä...");

        new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    int progress = i;

                    Thread.sleep(1000);

                    ui.access(() -> {
                        statusText.setText("Taustaprosessin vaihe " + progress + "/5 valmis.");
                    });
                }

                ui.access(() -> {
                    statusText.setText("Taustaprosessi valmis. Näkymä päivittyi Server Pushin avulla.");
                });

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                ui.access(() -> {
                    statusText.setText("Taustaprosessi keskeytyi.");
                });
            }
        }).start();
    }
}