package com.example.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class CategoryDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String responsiblePerson;

    @NotBlank
    @Column(nullable = false)
    private String targetAudience;

    @Column(length = 1000)
    private String extraInfo;

    @NotNull
    @OneToOne
    @JoinColumn(name = "category_id", nullable = false, unique = true)
    private Category category;

    public CategoryDetail() {
    }

    public CategoryDetail(String responsiblePerson, String targetAudience, String extraInfo, Category category) {
        this.responsiblePerson = responsiblePerson;
        this.targetAudience = targetAudience;
        this.extraInfo = extraInfo;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public String getResponsiblePerson() {
        return responsiblePerson;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public Category getCategory() {
        return category;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setResponsiblePerson(String responsiblePerson) {
        this.responsiblePerson = responsiblePerson;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}