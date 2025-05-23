package ma.emsi.charitywebapp.utils;

import ma.emsi.charitywebapp.entities.ActionCharite;
import ma.emsi.charitywebapp.entities.Notification;
import ma.emsi.charitywebapp.entities.Utilisateur;
import ma.emsi.charitywebapp.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Classe utilitaire pour faciliter la création des notifications dans l'application
 */
@Component
public class NotificationUtils {

    private static NotificationService notificationService;

    @Autowired
    public NotificationUtils(NotificationService notificationService) {
        NotificationUtils.notificationService = notificationService;
    }

    /**
     * Crée une notification pour un utilisateur
     *
     * @param utilisateur L'utilisateur destinataire
     * @param titre Titre de la notification
     * @param contenu Contenu détaillé de la notification
     * @return La notification créée et persistée
     */
    public static Notification creerNotification(Utilisateur utilisateur, String titre, String contenu) {
        return creerNotification(utilisateur, titre, contenu, null);
    }

    /**
     * Crée une notification pour un utilisateur liée à une action
     *
     * @param utilisateur L'utilisateur destinataire
     * @param titre Titre de la notification
     * @param contenu Contenu détaillé de la notification
     * @param action L'action liée à la notification
     * @return La notification créée et persistée
     */
    public static Notification creerNotification(Utilisateur utilisateur, String titre, String contenu, ActionCharite action) {
        if (notificationService == null) {
            throw new IllegalStateException("NotificationService n'a pas été initialisé");
        }

        Notification notification = new Notification();
        notification.setUtilisateur(utilisateur);
        notification.setTitre(titre);
        notification.setContenu(contenu);
        notification.setAction(action);
        notification.setDateCreation(LocalDateTime.now());
        notification.setLu(false);

        return notificationService.saveNotification(notification);
    }

    /**
     * Crée une notification pour un don reçu
     *
     * @param utilisateur L'administrateur de l'organisation
     * @param action L'action concernée
     * @param montant Le montant du don
     * @param nomDonateur Le nom du donateur
     * @return La notification créée
     */
    public static Notification notifierNouveauDon(Utilisateur utilisateur, ActionCharite action, double montant, String nomDonateur) {
        String titre = "Nouveau don reçu pour " + action.getTitre();
        String contenu = String.format(
                "Un don de %.2f € a été effectué par %s pour votre action \"%s\".",
                montant, nomDonateur, action.getTitre());
        return creerNotification(utilisateur, titre, contenu, action);
    }

    /**
     * Crée une notification pour une nouvelle participation
     *
     * @param utilisateur L'administrateur de l'organisation
     * @param action L'action concernée
     * @param nomParticipant Le nom du participant
     * @return La notification créée
     */
    public static Notification notifierNouvelleParticipation(Utilisateur utilisateur, ActionCharite action, String nomParticipant) {
        String titre = "Nouveau participant pour " + action.getTitre();
        String contenu = String.format(
                "%s s'est inscrit(e) à votre action \"%s\".",
                nomParticipant, action.getTitre());
        return creerNotification(utilisateur, titre, contenu, action);
    }

    /**
     * Crée une notification d'objectif atteint
     *
     * @param utilisateur L'administrateur de l'organisation
     * @param action L'action qui a atteint son objectif
     * @return La notification créée
     */
    public static Notification notifierObjectifAtteint(Utilisateur utilisateur, ActionCharite action) {
        String titre = "Objectif atteint pour " + action.getTitre();
        String contenu = String.format(
                "Félicitations ! Votre action \"%s\" a atteint son objectif de collecte de %.2f €.",
                action.getTitre(), action.getObjectifCollecte());
        return creerNotification(utilisateur, titre, contenu, action);
    }

    /**
     * Crée une notification administrative
     *
     * @param utilisateur L'administrateur de l'organisation
     * @param titre Titre de la notification
     * @param contenu Contenu de la notification
     * @return La notification créée
     */
    public static Notification notifierMessageAdmin(Utilisateur utilisateur, String titre, String contenu) {
        return creerNotification(utilisateur, "Message administratif: " + titre, contenu);
    }
} 