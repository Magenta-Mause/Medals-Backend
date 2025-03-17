package com.medals.medalsbackend;

import com.medals.medalsbackend.entity.medals.MedalCollection;
import com.medals.medalsbackend.entity.medals.MedalType;
import com.medals.medalsbackend.entity.performancerecording.Discipline;
import com.medals.medalsbackend.entity.performancerecording.DisciplineCategory;
import com.medals.medalsbackend.entity.performancerecording.DisciplineRatingMetric;
import com.medals.medalsbackend.entity.performancerecording.RatingMetric;
import com.medals.medalsbackend.entity.users.Athlete;
import com.medals.medalsbackend.entity.users.Trainer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DummyData {

    public static final List<Trainer> TRAINERS = List.of(
            Trainer.builder().firstName("Paul").lastName("Reiter").email("test@example.org").build(),
            Trainer.builder().firstName("Lisa").lastName("Schmidt").email("lise@example.org").build(),
            Trainer.builder().firstName("Michael").lastName("Fischer").email("michael@example.org").build()
            /* Trainer.builder().firstName("Anna").lastName("Meyer").email("anna@web.de").build(),
            Trainer.builder().firstName("Thomas").lastName("Wagner").email("thomas@hotmail.com").build(),
            Trainer.builder().firstName("Sophie").lastName("Becker").email("sophie@outlook.com").build(),
            Trainer.builder().firstName("Daniel").lastName("Koch").email("daniel@gmx.net").build(),
            Trainer.builder().firstName("Laura").lastName("Schulz").email("laura@gmail.com").build(),
            Trainer.builder().firstName("Felix").lastName("Bauer").email("felix@yahoo.com").build(),
            Trainer.builder().firstName("Julia").lastName("Hoffmann").email("julia@web.de").build(),
            Trainer.builder().firstName("Markus").lastName("Lehmann").email("markus@hotmail.com").build(),
            Trainer.builder().firstName("Nina").lastName("Krause").email("nina@outlook.com").build(),
            Trainer.builder().firstName("Stefan").lastName("Maier").email("stefan@gmx.net").build(),
            Trainer.builder().firstName("Clara").lastName("Huber").email("clara@gmail.com").build(),
            Trainer.builder().firstName("David").lastName("Schneider").email("david@yahoo.com").build() */
    );

    public static final List<Athlete> ATHLETES = List.of(
            Athlete.builder().email("johnreiter@example.org").firstName("John").lastName("Reiter").birthdate(LocalDate.of(2010, 3, 7)).gender(Athlete.Gender.MALE).totalMedal(MedalType.BRONZE).medalCollection(MedalCollection.builder().medalEndurance(MedalType.SILVER).medalCoordination(MedalType.BRONZE).medalStrength(MedalType.GOLD).medalSpeed(MedalType.SILVER).build()).build(),
            Athlete.builder().email("paul@example.org").firstName("Jane").lastName("Reiter").birthdate(LocalDate.of(2009, 4, 10)).gender(Athlete.Gender.FEMALE).totalMedal(MedalType.GOLD).medalCollection(MedalCollection.builder().medalEndurance(MedalType.BRONZE).medalCoordination(MedalType.SILVER).medalStrength(MedalType.GOLD).medalSpeed(MedalType.BRONZE).build()).build(),
            Athlete.builder().email("emily@example.org").firstName("Emily").lastName("Johnson").birthdate(LocalDate.of(2008, 5, 13)).gender(Athlete.Gender.DIVERSE).totalMedal(MedalType.SILVER).medalCollection(MedalCollection.builder().medalEndurance(MedalType.GOLD).medalCoordination(MedalType.BRONZE).medalStrength(MedalType.SILVER).medalSpeed(MedalType.GOLD).build()).build()
            /* Athlete.builder().email("test@gmx.de").firstName("Sarah").lastName("Davis").birthdate(LocalDate.of(2002, 2, 14)).gender(Athlete.Gender.FEMALE).totalMedal(MedalType.GOLD).medalCollection(MedalCollection.builder().medalEndurance(MedalType.BRONZE).medalCoordination(MedalType.SILVER).medalStrength(MedalType.GOLD).medalSpeed(MedalType.SILVER).build()).build(),
            Athlete.builder().email("test@gmx.de").firstName("Chris").lastName("Taylor").birthdate(LocalDate.of(2003, 11, 3)).gender(Athlete.Gender.MALE).totalMedal(MedalType.SILVER).medalCollection(MedalCollection.builder().medalEndurance(MedalType.GOLD).medalCoordination(MedalType.BRONZE).medalStrength(MedalType.SILVER).medalSpeed(MedalType.GOLD).build()).build(),
            Athlete.builder().email("test@gmx.de").firstName("Jessica").lastName("Wilson").birthdate(LocalDate.of(1999, 6, 24)).gender(Athlete.Gender.FEMALE).totalMedal(MedalType.BRONZE).medalCollection(MedalCollection.builder().medalEndurance(MedalType.SILVER).medalCoordination(MedalType.GOLD).medalStrength(MedalType.BRONZE).medalSpeed(MedalType.BRONZE).build()).build(),
            Athlete.builder().email("test@gmx.de").firstName("David").lastName("White").birthdate(LocalDate.of(2001, 9, 18)).gender(Athlete.Gender.MALE).totalMedal(MedalType.GOLD).medalCollection(MedalCollection.builder().medalEndurance(MedalType.BRONZE).medalCoordination(MedalType.SILVER).medalStrength(MedalType.GOLD).medalSpeed(MedalType.SILVER).build()).build(),
            Athlete.builder().email("test@gmx.de").firstName("Ashley").lastName("Martin").birthdate(LocalDate.of(2000, 12, 25)).gender(Athlete.Gender.FEMALE).totalMedal(MedalType.SILVER).medalCollection(MedalCollection.builder().medalEndurance(MedalType.GOLD).medalCoordination(MedalType.BRONZE).medalStrength(MedalType.SILVER).medalSpeed(MedalType.GOLD).build()).build(),
            Athlete.builder().email("test@gmx.de").firstName("James").lastName("Garcia").birthdate(LocalDate.of(1998, 8, 8)).gender(Athlete.Gender.MALE).totalMedal(MedalType.BRONZE).medalCollection(MedalCollection.builder().medalEndurance(MedalType.SILVER).medalCoordination(MedalType.GOLD).medalStrength(MedalType.BRONZE).medalSpeed(MedalType.BRONZE).build()).build(),
            Athlete.builder().email("test@gmx.de").firstName("Olivia").lastName("Martinez").birthdate(LocalDate.of(2004, 1, 31)).gender(Athlete.Gender.FEMALE).totalMedal(MedalType.GOLD).medalCollection(MedalCollection.builder().medalEndurance(MedalType.BRONZE).medalCoordination(MedalType.SILVER).medalStrength(MedalType.GOLD).medalSpeed(MedalType.SILVER).build()).build(),
            Athlete.builder().email("test@gmx.de").firstName("Daniel").lastName("Hernandez").birthdate(LocalDate.of(2005, 5, 5)).gender(Athlete.Gender.MALE).totalMedal(MedalType.SILVER).medalCollection(MedalCollection.builder().medalEndurance(MedalType.GOLD).medalCoordination(MedalType.BRONZE).medalStrength(MedalType.SILVER).medalSpeed(MedalType.GOLD).build()).build(),
            Athlete.builder().email("test@gmx.de").firstName("Sophia").lastName("Lopez").birthdate(LocalDate.of(2002, 3, 17)).gender(Athlete.Gender.FEMALE).totalMedal(MedalType.BRONZE).medalCollection(MedalCollection.builder().medalEndurance(MedalType.SILVER).medalCoordination(MedalType.GOLD).medalStrength(MedalType.BRONZE).medalSpeed(MedalType.BRONZE).build()).build(),
            Athlete.builder().email("test@gmx.de").firstName("Ethan").lastName("Gonzalez").birthdate(LocalDate.of(1999, 4, 29)).gender(Athlete.Gender.MALE).totalMedal(MedalType.GOLD).medalCollection(MedalCollection.builder().medalEndurance(MedalType.BRONZE).medalCoordination(MedalType.SILVER).medalStrength(MedalType.GOLD).medalSpeed(MedalType.SILVER).build()).build(),
            Athlete.builder().email("test@gmx.de").firstName("Isabella").lastName("Rodriguez").birthdate(LocalDate.of(2001, 10, 12)).gender(Athlete.Gender.FEMALE).totalMedal(MedalType.SILVER).medalCollection(MedalCollection.builder().medalEndurance(MedalType.GOLD).medalCoordination(MedalType.BRONZE).medalStrength(MedalType.SILVER).medalSpeed(MedalType.GOLD).build()).build(),
            Athlete.builder().email("test@gmx.de").firstName("Alexander").lastName("Perez").birthdate(LocalDate.of(2003, 6, 2)).gender(Athlete.Gender.MALE).totalMedal(MedalType.BRONZE).medalCollection(MedalCollection.builder().medalEndurance(MedalType.SILVER).medalCoordination(MedalType.GOLD).medalStrength(MedalType.BRONZE).medalSpeed(MedalType.BRONZE).build()).build(),
            Athlete.builder().email("test@gmx.de").firstName("Mia").lastName("Thomas").birthdate(LocalDate.of(2000, 9, 15)).gender(Athlete.Gender.FEMALE).totalMedal(MedalType.GOLD).medalCollection(MedalCollection.builder().medalEndurance(MedalType.BRONZE).medalCoordination(MedalType.SILVER).medalStrength(MedalType.GOLD).medalSpeed(MedalType.SILVER).build()).build(),
            Athlete.builder().email("test@gmx.de").firstName("Benjamin").lastName("Walker").birthdate(LocalDate.of(1998, 12, 1)).gender(Athlete.Gender.MALE).totalMedal(MedalType.SILVER).medalCollection(MedalCollection.builder().medalEndurance(MedalType.GOLD).medalCoordination(MedalType.BRONZE).medalStrength(MedalType.SILVER).medalSpeed(MedalType.GOLD).build()).build(),
            Athlete.builder().email("test@gmx.de").firstName("Charlotte").lastName("Hall").birthdate(LocalDate.of(2004, 11, 20)).gender(Athlete.Gender.FEMALE).totalMedal(MedalType.BRONZE).medalCollection(MedalCollection.builder().medalEndurance(MedalType.SILVER).medalCoordination(MedalType.GOLD).medalStrength(MedalType.BRONZE).medalSpeed(MedalType.BRONZE).build()).build(),
            Athlete.builder().email("test@gmx.de").firstName("Jacob").lastName("Young").birthdate(LocalDate.of(2001, 8, 6)).gender(Athlete.Gender.MALE).totalMedal(MedalType.GOLD).medalCollection(MedalCollection.builder().medalEndurance(MedalType.BRONZE).medalCoordination(MedalType.SILVER).medalStrength(MedalType.GOLD).medalSpeed(MedalType.SILVER).build()).build(),
            Athlete.builder().email("test@gmx.de").firstName("Liam").lastName("Morgan").birthdate(LocalDate.of(2002, 5, 22)).gender(Athlete.Gender.MALE).totalMedal(MedalType.BRONZE).medalCollection(MedalCollection.builder().medalEndurance(MedalType.SILVER).medalCoordination(MedalType.GOLD).medalStrength(MedalType.BRONZE).medalSpeed(MedalType.SILVER).build()).build(),
            Athlete.builder().email("test@gmx.de").firstName("Emma").lastName("Harris").birthdate(LocalDate.of(1999, 11, 11)).gender(Athlete.Gender.FEMALE).totalMedal(MedalType.GOLD).medalCollection(MedalCollection.builder().medalEndurance(MedalType.BRONZE).medalCoordination(MedalType.SILVER).medalStrength(MedalType.GOLD).medalSpeed(MedalType.BRONZE).build()).build(),
            Athlete.builder().email("test@gmx.de").firstName("Noah").lastName("Clark").birthdate(LocalDate.of(2001, 3, 15)).gender(Athlete.Gender.MALE).totalMedal(MedalType.SILVER).medalCollection(MedalCollection.builder().medalEndurance(MedalType.GOLD).medalCoordination(MedalType.BRONZE).medalStrength(MedalType.SILVER).medalSpeed(MedalType.GOLD).build()).build(),
            Athlete.builder().email("test@gmx.de").firstName("Ava").lastName("Lee").birthdate(LocalDate.of(2000, 6, 5)).gender(Athlete.Gender.FEMALE).totalMedal(MedalType.BRONZE).medalCollection(MedalCollection.builder().medalEndurance(MedalType.SILVER).medalCoordination(MedalType.GOLD).medalStrength(MedalType.BRONZE).medalSpeed(MedalType.SILVER).build()).build(),
            Athlete.builder().email("test@gmx.de").firstName("Lucas").lastName("Scott").birthdate(LocalDate.of(2003, 4, 27)).gender(Athlete.Gender.MALE).totalMedal(MedalType.GOLD).medalCollection(MedalCollection.builder().medalEndurance(MedalType.BRONZE).medalCoordination(MedalType.SILVER).medalStrength(MedalType.GOLD).medalSpeed(MedalType.BRONZE).build()).build(),
            Athlete.builder().email("test@gmx.de").firstName("Sophia").lastName("Adams").birthdate(LocalDate.of(1998, 9, 30)).gender(Athlete.Gender.FEMALE).totalMedal(MedalType.SILVER).medalCollection(MedalCollection.builder().medalEndurance(MedalType.GOLD).medalCoordination(MedalType.BRONZE).medalStrength(MedalType.SILVER).medalSpeed(MedalType.GOLD).build()).build(),
            Athlete.builder().email("test@gmx.de").firstName("William").lastName("Robinson").birthdate(LocalDate.of(2004, 12, 19)).gender(Athlete.Gender.MALE).totalMedal(MedalType.BRONZE).medalCollection(MedalCollection.builder().medalEndurance(MedalType.SILVER).medalCoordination(MedalType.GOLD).medalStrength(MedalType.BRONZE).medalSpeed(MedalType.SILVER).build()).build(),
            Athlete.builder().email("test@gmx.de").firstName("Isabella").lastName("Walker").birthdate(LocalDate.of(2001, 2, 10)).gender(Athlete.Gender.FEMALE).totalMedal(MedalType.GOLD).medalCollection(MedalCollection.builder().medalEndurance(MedalType.BRONZE).medalCoordination(MedalType.SILVER).medalStrength(MedalType.GOLD).medalSpeed(MedalType.BRONZE).build()).build(),
            Athlete.builder().email("test@gmx.de").firstName("James").lastName("Young").birthdate(LocalDate.of(1997, 7, 8)).gender(Athlete.Gender.MALE).totalMedal(MedalType.SILVER).medalCollection(MedalCollection.builder().medalEndurance(MedalType.GOLD).medalCoordination(MedalType.BRONZE).medalStrength(MedalType.SILVER).medalSpeed(MedalType.GOLD).build()).build(), Athlete.builder().email("test@gmx.de").firstName("Mia").lastName("Hall").birthdate(LocalDate.of(1996, 8, 3)).gender(Athlete.Gender.FEMALE).totalMedal(MedalType.BRONZE).medalCollection(MedalCollection.builder().medalEndurance(MedalType.SILVER).medalCoordination(MedalType.GOLD).medalStrength(MedalType.BRONZE).medalSpeed(MedalType.SILVER).build()).build() */
    );

    private static double convertTime(int minutes, int seconds) {
        return minutes * 60 + seconds;
    }

    public static Collection<DisciplineRatingMetric> getDisciplineRatingMetric() {
        Collection<DisciplineRatingMetric> ratings = new ArrayList<>();
        RatingMetric maleRatingMetric = RatingMetric.builder()
                .bronzeRating(convertTime(5, 40))
                .silverRating(convertTime(5, 0))
                .goldRating(convertTime(4, 15))
                .build();

        RatingMetric femaleRatingMetric = RatingMetric.builder()
                .bronzeRating(convertTime(6, 40))
                .silverRating(convertTime(6, 0))
                .goldRating(convertTime(5, 15))
                .build();

        Discipline run800 = Discipline.builder()
                .name("800m Lauf")
                .unit(Discipline.Unit.seconds)
                .category(DisciplineCategory.ENDURANCE)
                .isMoreBetter(false)
                .build();

        Discipline throwing = Discipline.builder()
                .name("Werfen")
                .unit(Discipline.Unit.meters)
                .category(DisciplineCategory.STRENGTH)
                .isMoreBetter(false)
                .build();


        for (int validIn = 2020; validIn <= 2026; validIn++) {
            ratings.add(DisciplineRatingMetric.builder()
                    .discipline(run800)
                    .startAge(6)
                    .endAge(7)
                    .ratingMale(maleRatingMetric)
                    .ratingFemale(femaleRatingMetric)
                    .validIn(validIn)
                    .build());

            ratings.add(DisciplineRatingMetric.builder()
                    .discipline(run800)
                    .startAge(8)
                    .endAge(9)
                    .ratingMale(maleRatingMetric)
                    .ratingFemale(femaleRatingMetric)
                    .validIn(validIn)
                    .build());

            ratings.add(DisciplineRatingMetric.builder()
                    .discipline(run800)
                    .startAge(10)
                    .endAge(11)
                    .ratingMale(maleRatingMetric)
                    .ratingFemale(femaleRatingMetric)
                    .validIn(validIn)
                    .build());

            ratings.add(DisciplineRatingMetric.builder()
                    .discipline(run800)
                    .startAge(12)
                    .endAge(13)
                    .ratingMale(maleRatingMetric)
                    .ratingFemale(femaleRatingMetric)
                    .validIn(validIn)
                    .build());

            ratings.add(DisciplineRatingMetric.builder()
              .discipline(run800)
              .startAge(14)
              .endAge(15)
              .ratingMale(maleRatingMetric)
              .ratingFemale(femaleRatingMetric)
              .validIn(validIn)
              .build());

            ratings.add(DisciplineRatingMetric.builder()
              .discipline(run800)
              .startAge(16)
              .endAge(17)
              .ratingMale(maleRatingMetric)
              .ratingFemale(femaleRatingMetric)
              .validIn(validIn)
              .build());

            ratings.add(DisciplineRatingMetric.builder()
                    .discipline(throwing)
                    .startAge(6)
                    .endAge(7)
                    .ratingMale(maleRatingMetric)
                    .ratingFemale(femaleRatingMetric)
                    .validIn(validIn)
                    .build());

            ratings.add(DisciplineRatingMetric.builder()
                    .discipline(throwing)
                    .startAge(8)
                    .endAge(9)
                    .ratingMale(maleRatingMetric)
                    .ratingFemale(femaleRatingMetric)
                    .validIn(validIn)
                    .build());

            ratings.add(DisciplineRatingMetric.builder()
                    .discipline(throwing)
                    .startAge(10)
                    .endAge(11)
                    .ratingMale(maleRatingMetric)
                    .ratingFemale(femaleRatingMetric)
                    .validIn(validIn)
                    .build());

            ratings.add(DisciplineRatingMetric.builder()
                    .discipline(throwing)
                    .startAge(12)
                    .endAge(13)
                    .ratingMale(maleRatingMetric)
                    .ratingFemale(femaleRatingMetric)
                    .validIn(validIn)
                    .build());

            ratings.add(DisciplineRatingMetric.builder()
                    .discipline(throwing)
                    .startAge(14)
                    .endAge(15)
                    .ratingMale(maleRatingMetric)
                    .ratingFemale(femaleRatingMetric)
                    .validIn(validIn)
                    .build());

            ratings.add(DisciplineRatingMetric.builder()
                    .discipline(throwing)
                    .startAge(16)
                    .endAge(17)
                    .ratingMale(maleRatingMetric)
                    .ratingFemale(femaleRatingMetric)
                    .validIn(validIn)
                    .build());



        }
        return ratings;
    }
}
