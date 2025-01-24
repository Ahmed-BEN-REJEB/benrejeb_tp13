package comptoirs.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class Commande {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(nullable = false)
	@Setter(AccessLevel.NONE) // la clé est auto-générée par la BD, On ne veut pas de "setter"
	private Integer numero;

	@Basic(optional = false)
	@Column(nullable = false)
	@ToString.Exclude
	// Initialisée avec la date de création
	private LocalDate saisiele = LocalDate.now();

	@Basic(optional = true)
	@ToString.Exclude
	private LocalDate envoyeele = null;

    // BigDecimal est la classe Java recommandée pour les montants monétaires
	@Column(precision = 18, scale = 2)
    @Min(value = 0, message = "Le port ne peut pas être négatif")
	@ToString.Exclude
	private BigDecimal port = BigDecimal.ZERO;

	@Size(max = 40)
	@Column(length = 40)
	private String destinataire;

	@Embedded // AdressePostale est une classe "embarquée" dans Commande
	private AdressePostale adresseLivraison;

	@Basic(optional = false)
	@Column(nullable = false, precision = 10, scale = 2)
    @Min(value = 0, message = "La remise (%) ne peut pas être négative")
    @Max(value = 1, message = "La remise (%) ne peut pas être supérieure à 1")
	private BigDecimal remise = BigDecimal.ZERO;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "commande", orphanRemoval = true)
	@ToString.Exclude
    private List<Ligne> lignes = new LinkedList<>();

	@ManyToOne(optional = false)
	@NonNull
    @ToString.Exclude
	private Client client;

}
