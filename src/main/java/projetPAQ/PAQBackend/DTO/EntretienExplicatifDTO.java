package projetPAQ.PAQBackend.DTO;

import java.sql.Date;

public class EntretienExplicatifDTO {
    private Integer id;
    private Date date;
    private String typeErreur;
    private String details;
    private String decision;
    private Integer collaborateurId;
    private String collaborateurNom;
    private String collaborateurPrenom;
    private Long userId;
    private String userNom;
    private String userPrenom;
    private Boolean validated;
    private Long validatedByUserId;
    private String validatedByUserNom;
    private String validatedByUserPrenom;
    private Date validationDate;
    private Date dateArchivage; 
    
    public Date getDateArchivage() {
        return dateArchivage;
    }

    public void setDateArchivage(Date dateArchivage) {
        this.dateArchivage = dateArchivage;
    }
    public Long getValidatedByUserId() {
		return validatedByUserId;
	}

	public void setValidatedByUserId(Long validatedByUserId) {
		this.validatedByUserId = validatedByUserId;
	}

	public String getValidatedByUserNom() {
		return validatedByUserNom;
	}

	public void setValidatedByUserNom(String validatedByUserNom) {
		this.validatedByUserNom = validatedByUserNom;
	}

	public String getValidatedByUserPrenom() {
		return validatedByUserPrenom;
	}

	public void setValidatedByUserPrenom(String validatedByUserPrenom) {
		this.validatedByUserPrenom = validatedByUserPrenom;
	}

	public Date getValidationDate() {
		return validationDate;
	}

	public void setValidationDate(Date validationDate) {
		this.validationDate = validationDate;
	}

	// Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTypeErreur() {
        return typeErreur;
    }

    public void setTypeErreur(String typeErreur) {
        this.typeErreur = typeErreur;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public Integer getCollaborateurId() {
        return collaborateurId;
    }

    public void setCollaborateurId(Integer collaborateurId) {
        this.collaborateurId = collaborateurId;
    }

    public String getCollaborateurNom() {
        return collaborateurNom;
    }

    public void setCollaborateurNom(String collaborateurNom) {
        this.collaborateurNom = collaborateurNom;
    }

    public String getCollaborateurPrenom() {
        return collaborateurPrenom;
    }

    public void setCollaborateurPrenom(String collaborateurPrenom) {
        this.collaborateurPrenom = collaborateurPrenom;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserNom() {
        return userNom;
    }

    public void setUserNom(String userNom) {
        this.userNom = userNom;
    }

    public String getUserPrenom() {
        return userPrenom;
    }

    public void setUserPrenom(String userPrenom) {
        this.userPrenom = userPrenom;
    }

    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }
}