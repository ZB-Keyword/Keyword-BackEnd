package DevHeaven.keyword.domain.member.entity;

import DevHeaven.keyword.common.entity.BaseTimeEntity;
import DevHeaven.keyword.domain.member.type.MemberRole;
import DevHeaven.keyword.domain.member.type.MemberStatus;
import javax.swing.JFileChooser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    private String profileImageFileName;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    public Member modifyStatus(final MemberStatus status) {
        this.status = status;
        return this;
    }

    public Member modifyPassword(final String password) {
        this.password = password;
        return this;
    }

    public Member modifyProfileImageFileName(final String fileName) {
        this.profileImageFileName = fileName;
        return this;
    }
}
