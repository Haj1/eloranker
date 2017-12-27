V basic Spring Boot/Thymeleaf web app with ELO Ranking logic for both singles and doubles.

For doubles, the ranking change is calculated using the average rating of the two players in the pair.

Deployed on CF (app eloranker in org pivotallabs / space Beach-London). To deploy, run:

./gradlew build && cf push eloranker -p build/libs/<name-of-jar>
