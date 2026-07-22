package com.novforge.api.users;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long user_id;

    @Column(name = "google_uid", nullable = false, unique = true, length = 128)
    private String googleUid;

    @Column(name = "user_name", nullable = false, length = 50)
    private String name;

    @Column(name = "user_email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "profile_image", length = 512)
    private String profileImage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    protected User() {}

    public User(String googleUid, String name, String email, String profileImage) {
        this.googleUid = googleUid;
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
    }

    @PrePersist
    void onCreate() { createdAt = Instant.now(); }

    @PreUpdate
    void onUpdate() { updatedAt = Instant.now(); }

    public void updateName(String name) { this.name = name; }
    public void updateProfileImage(String profileImage) { this.profileImage = profileImage; }

    public Long getId() { return user_id; }
    public String getGoogleUid() { return googleUid; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getProfileImage() { return profileImage; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
