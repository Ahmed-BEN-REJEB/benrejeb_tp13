package comptoirs.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class Client {

    @Id
    @Basic(optional = false)
    @NonNull
    @Size(min = 1, max = 5)
    @Column(nullable = false, length = 5)
    private String code;

    @Basic(optional = false)
    @NonNull
    @Size(min = 1, max = 40)
    @Column(nullable = false, length = 40)
    private String societe;

    @Size(max = 30)
    @Column(length = 30)
    private String contact;

    @Size(max = 30)
    @Column(length = 30)
    @ToString.Exclude
    private String fonction;

    @Embedded // AdressePostale est une classe "embarquée" dans Client
    @ToString.Exclude
    private AdressePostale adresse;

    @Size(max = 24)
    @Column(length = 24)
    @ToString.Exclude
    private String telephone;

    // @Pattern(regexp="^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$",
    // message="Invalid phone/fax format, should be as xxx-xxx-xxxx")//if the field
    // contains phone or fax number consider using this annotation to enforce field
    // validation
    @Size(max = 24)
    @Column(length = 24)
    @ToString.Exclude
    private String fax;

    // Si on supprime un client, on supprime aussi ses commandes
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "client")
    @ToString.Exclude
    private List<Commande> commandes = new ArrayList<>();

}
