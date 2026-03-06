package projetPAQ.PAQBackend.repository;


import java.util.Date;

import projetPAQ.PAQBackend.entity.Collaborateur;

public interface BaseEntretien {
    boolean isArchive();
    void setArchive(boolean archive);
    Date getDateArchivage();
    void setDateArchivage(Date dateArchivage);
    Collaborateur getCollaborateur();
    Date getDate();
    boolean isDeleted();
}
