package ads.autservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "roles",
        schema = "public",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_roles_name", columnNames = "role_name")
        },
        indexes = {
                @Index(name = "idx_roles_name", columnList = "role_name")
        }
)
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Role {

    @Id
    @EqualsAndHashCode.Include
    private Integer id;   //  fixed: 1=ADMIN, 2=USER

    @Column(name = "role_name", nullable = false, length = 25)
    @ToString.Include
    private String roleName;

    @Column(name = "role_description", length = 255)
    private String roleDescription;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    //  Hindari include di toString dan equals ,Kalau tidak exclude → bisa infinite recursion
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<User> users = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}