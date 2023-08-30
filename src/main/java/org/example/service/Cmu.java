package org.example.service;

import org.example.model.Consultationn;
import org.example.model.DossierMedecin;

import javax.ws.rs.*;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

@ApplicationPath("/api")
public class Cmu extends Application{

    @POST
    @Path("/ajouterDossier/{isn}/{nom}/{prenom}/{numeroCmu}/{ville}/{age}/{masculin}/{feminin}/{enceinte}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void addDossierPatient(@PathParam("isn") int isn, @PathParam("nom") String nom, @PathParam("prenom") String prenom,
                                  @PathParam("numeroCmu") int numeroCmu, @PathParam("ville") String ville, @PathParam("age") int age,
                                  @PathParam("masculin") boolean masculin, @PathParam("feminin") boolean feminin, @PathParam("enceinte") boolean enceinte) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/cmu", "root", "");
             PreparedStatement statement = connection.prepareStatement("INSERT INTO dossierpatient (isn,nom,prenom,numeroCmu,ville," +
                     "age,masculin,feminin,enceinte) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)")) {

            statement.setInt(1, isn);
            statement.setString(2, nom);
            statement.setString(3, prenom);
            statement.setInt(4, numeroCmu);
            statement.setString(5, ville);
            statement.setInt(6, age);
            statement.setBoolean(7, masculin);
            statement.setBoolean(8, feminin);
            statement.setBoolean(9, enceinte);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @PUT
    @Path("/modifierDossierPat/{isn}/{nom}/{prenom}/{numeroCmu}/{ville}/{age}/{masculin}/{feminin}/{enceinte}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void modifierDossierPatient(@PathParam("isn") int isn, @PathParam("nom") String nom, @PathParam("prenom") String prenom,
                                       @PathParam("numeroCmu") int numeroCmu, @PathParam("ville") String ville, @PathParam("age") int age,
                                       @PathParam("masculin") boolean masculin, @PathParam("feminin") boolean feminin, @PathParam("enceinte") boolean enceinte) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/cmu", "root", "");
             PreparedStatement statement = connection.prepareStatement("UPDATE dossierpatient SET nom = ?, prenom = ?, numeroCmu = ?, ville = ?, " +
                     "age = ?, masculin = ?, feminin = ?, enceinte = ? WHERE isn = ?")) {

            statement.setString(1, nom);
            statement.setString(2, prenom);
            statement.setInt(3, numeroCmu);
            statement.setString(4, ville);
            statement.setInt(5, age);
            statement.setBoolean(6, masculin);
            statement.setBoolean(7, feminin);
            statement.setBoolean(8, enceinte);
            statement.setInt(9, isn);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @POST
    @Path("/consultation/{examenPhysique}/{discussionSymptomes}/{diagnostic}/{ordonnance}/{isn_dossierPatient}/{numeroCmu}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public int consultation(@PathParam("examenPhysique") String examenPhysique, @PathParam("discussionSymptomes") String discussionSymptomes,
                            @PathParam("diagnostic") String diagnostic, @PathParam("ordonnance") String ordonnance, @PathParam("isn_dossierPatient") int isn_dossierPatient, @PathParam("numeroCmu") int numeroCmu) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/cmu", "root", "");
             PreparedStatement statement = connection.prepareStatement("INSERT INTO consultation (examenPhysique,DiscussionSymptômes,diagnostic,ordonnance," +
                     "tauxReduction,code,isn_dossierPatient,numeroCmu) VALUES (?, ?, ?, ?, ?, ?, ?,?)")) {
            int tauxReduction = 70;
            String code = genererAlphanumerique(10);
            try(PreparedStatement statemente = connection.prepareStatement("SELECT * FROM dossierpatient WHERE isn = ?");) {
                statemente.setInt(1, isn_dossierPatient);
                ResultSet resultSet = statemente.executeQuery();
                if (resultSet.next()) {
                    Integer age = resultSet.getInt("age");
                    Boolean enceinte = resultSet.getBoolean("enceinte");
                    Integer numeroCm = resultSet.getInt("numeroCmu");

                    if (age <= 5 || enceinte) {
                        tauxReduction = 100;
                    }
                    if(numeroCm != numeroCmu){
                        return 1;
                    }
                }


            }catch (SQLException e) {
                e.printStackTrace();
            }

            statement.setString(1, examenPhysique);
            statement.setString(2, discussionSymptomes);
            statement.setString(3, diagnostic);
            statement.setString(4, ordonnance);
            statement.setInt(5, tauxReduction);
            statement.setString(6, code);
            statement.setInt(7, isn_dossierPatient);
            statement.setInt(8, numeroCmu);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @PUT
    @Path("/modifierdossier/{isn}/{antecedentsMedicaux}/{historiqueVaccination}/{resumesMedicaux}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void modifierDossie(@PathParam("isn")int isn,@PathParam("antecedentsMedicaux")String antecedentsMedicaux,@PathParam("historiqueVaccination")String historiqueVaccination,
                               @PathParam("resumesMedicaux")String resumesMedicaux) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/cmu", "root", "");
             PreparedStatement statement = connection.prepareStatement("UPDATE dossierpatient SET antecedentsMedicaux = ?, historiqueVaccination = ?, resumesMedicaux = ? WHERE isn = ?")) {

            statement.setString(1, antecedentsMedicaux);
            statement.setString(2, historiqueVaccination);
            statement.setString(3, resumesMedicaux);
            statement.setInt(4, isn);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


    @GET
    @Path("/afficherDossier/{isn}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DossierMedecin> AfficherDossieAPartirIsn(@PathParam("isn") int isn) {
        List<DossierMedecin> dossierMedecins = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/cmu", "root", "");
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM dossierpatient WHERE isn = ?");
        ) {
            statement.setInt(1, isn);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Integer isnValue = resultSet.getInt("isn");
                String nom = resultSet.getString("nom");
                String prenom = resultSet.getString("prenom");
                Integer numeroCmu = resultSet.getInt("numeroCmu");
                String ville = resultSet.getString("ville");
                String antecedentsMedicaux = resultSet.getString("antecedentsMedicaux");
                String historiqueVaccination = resultSet.getString("historiqueVaccination");
                String resumesMedicaux = resultSet.getString("resumesMedicaux");
                Integer age = resultSet.getInt("age");
                Boolean masculin = resultSet.getBoolean("masculin");
                Boolean feminin = resultSet.getBoolean("feminin");
                Boolean enceinte = resultSet.getBoolean("enceinte");

                DossierMedecin dossierMedecin = new DossierMedecin(isnValue, nom, prenom, numeroCmu, ville, antecedentsMedicaux, historiqueVaccination,
                        resumesMedicaux, age, masculin, feminin, enceinte);
                dossierMedecins.add(dossierMedecin);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dossierMedecins;
    }



    @GET
    @Path("/afficherConsultation/{isnDossierPatient}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Consultationn> AfficherConsultatio(@PathParam("isnDossierPatient") int isnDossierPatient) {
        List<Consultationn> consultations = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/cmu", "root", "");
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM consultation WHERE isn_dossierPatient = ?")) {

            statement.setInt(1, isnDossierPatient);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String examenPhysique = resultSet.getString("examenPhysique");
                    String DiscussionSymptomes = resultSet.getString("DiscussionSymptômes");
                    String diagnostic = resultSet.getString("diagnostic");
                    String ordonnance = resultSet.getString("ordonnance");
                    Integer tauxReduction = resultSet.getInt("tauxReduction");
                    String code = resultSet.getString("code");
                    Integer isnDossierPatientFromDB = resultSet.getInt("isn_dossierPatient");

                    Consultationn consultation = new Consultationn(examenPhysique, DiscussionSymptomes, diagnostic, ordonnance
                            , tauxReduction, code, isnDossierPatientFromDB);
                    consultations.add(consultation);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return consultations;
    }


    @DELETE
    @Path("/supprimerDossierPatientEtConsultations/{isnDossierPatient}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public int supprimerDossierPatientEtConsultations(@PathParam("isnDossierPatient") int isnDossierPatient) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/cmu", "root", "")) {

            Integer isnn = null;
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM consultation WHERE isn_dossierPatient = ?")) {
                statement.setInt(1, isnDossierPatient);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        isnn = resultSet.getInt("isn_dossierPatient");

                    }

                }
            }
            if (isnn != null){
                // Supprimer les consultations associées
                try (PreparedStatement deleteConsultations = connection.prepareStatement("DELETE FROM consultation WHERE isn_dossierPatient = ?")) {
                    deleteConsultations.setInt(1, isnDossierPatient);
                    deleteConsultations.executeUpdate();
                }
            }


            // Supprimer l'entrée du dossier patient
            try (PreparedStatement deleteDossierPatient = connection.prepareStatement("DELETE FROM dossierpatient WHERE isn = ?")) {
                deleteDossierPatient.setInt(1, isnDossierPatient);
                deleteDossierPatient.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    @POST
    @Path("/authentification/{code}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public int authentification(@PathParam("code") int code) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/cmu", "root", "")) {

            Integer isnn = null;
            boolean medecin = false;
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM utilisateur WHERE code = ?")) {
                statement.setInt(1, code);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        isnn = resultSet.getInt("code");
                        medecin = resultSet.getBoolean("medecin");
                        System.out.println("medecin "+medecin);

                    }
                    if(isnn != null && medecin){
                        return 1;
                    }


                }
            }

            Integer cod = null;
            boolean employerCmu = false;
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM utilisateur WHERE code = ?")) {
                statement.setInt(1, code);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        cod = resultSet.getInt("code");
                        employerCmu = resultSet.getBoolean("employerCmu");
                        System.out.println("employerCmu "+employerCmu);
                    }

                    if(cod != null && employerCmu){
                        return 2;
                    }

                }
            }

            Integer codee = null;
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM consultation WHERE isn_dossierPatient = ?")) {
                statement.setInt(1, code);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        codee = resultSet.getInt("isn_dossierPatient");

                    }
                    if(codee != null ){
                        return codee;
                    }

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 3;
    }

    private String genererAlphanumerique(int longueur) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(longueur);
        String CARACTERES = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        for (int i = 0; i < longueur; i++) {
            int index = random.nextInt(CARACTERES.length());
            sb.append(CARACTERES.charAt(index));
        }

        return sb.toString();
    }
}
