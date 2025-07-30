package gift.member.entity;

import jakarta.persistence.*;

@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = true, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = true, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    private Role role;

    @Column(unique = true)
    private Long kakaoId;

    @Column(length = 1024)
    private String kakaoAccessToken;

    protected Member() {

    }

    public Member(Long id, String email, String password, Role role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Member(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Member(Long kakaoId) {
        this.email = null;
        this.password = null;
        this.role = Role.USER;
        this.kakaoId = kakaoId;
    }

    public Long getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public Role getRole() {
        return this.role;
    }

    public Long getKakaoId() {
        return this.kakaoId;
    }

    public String getKakaoAccessToken() {
        return this.kakaoAccessToken;
    }

    public void updateKakaoAccessToken(String kakaoAccessToken) {
        this.kakaoAccessToken = kakaoAccessToken;
    }
}
