V basic Spring Boot/Thymeleaf web app with ELO Ranking logic for both singles and doubles.

For doubles, the ranking change is calculated using the average rating of the two players in the pair.

Deployed on CF (app eloranker in org pivotallabs / space Beach-London). To deploy, run:

./gradlew build && cf push eloranker -p build/libs/<name-of-jar>


TODO List:

 - Integrate with Meeple (https://meeple-api.cfapps.io/api.html) for Pivotal
 - Some basic error-catching
 - Allow edit of user
 - Allow different spaces so each Labs project/office can have their own
 - Other screens with lists of games played for each player
 - Do some styling
 - Adjust algorithm so it takes fewer games to become representative. ATM, it's around 10 I think (you can currently beat only the lowest-ranked person many times and be top by miles).
