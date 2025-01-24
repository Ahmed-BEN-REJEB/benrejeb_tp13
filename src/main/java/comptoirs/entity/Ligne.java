package comptoirs.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @RequiredArgsConstructor @ToString
@Table(uniqueConstraints = {
    // Une commande ne peut pas contenir deux fois le même produit
	@UniqueConstraint(columnNames = {"COMMANDE_NUMERO", "PRODUIT_REFERENCE"})
})
public class Ligne {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(nullable = false)
	@Setter(AccessLevel.NONE) // la clé est auto-générée par la BD, On ne veut pas de "setter"
	private Integer id;

	@JoinColumn(nullable = false)
	@ManyToOne(optional = false)
	@NonNull
	private Commande commande;

	@JoinColumn(nullable = false)
	@ManyToOne(optional = false)
	@NonNull
	private Produit produit;

	@Basic(optional = false)
	@Column(nullable = false)
	@NonNull
    @PositiveOrZero(message = "La quantité ne peut pas être négative")
	private Integer quantite;

}
