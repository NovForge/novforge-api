package com.novforge.api.users;

import com.novforge.api.mybuild.MyBuild;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

    @Column(name = "user_nickname", nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(name = "user_email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "profile_image", length = 512)
    private String profileImage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private final Set<MyBuild> myBuilds = new LinkedHashSet<>();

    protected User() {}

    public User(String googleUid, String name, String nickname, String email, String profileImage) {
        this.googleUid = googleUid;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.profileImage = profileImage;
    }

    @PrePersist
    void onCreate() { createdAt = Instant.now(); }

    @PreUpdate
    void onUpdate() { updatedAt = Instant.now(); }

    public void updateNickname(String nickname) { this.nickname = nickname; }
    public void updateProfileImage(String profileImage) { this.profileImage = profileImage; }

    public Long getId() { return user_id; }
    public String getGoogleUid() { return googleUid; }
    public String getName() { return name; }
    public String getNickname() { return nickname; }
    public String getEmail() { return email; }
    public String getProfileImage() { return profileImage; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
