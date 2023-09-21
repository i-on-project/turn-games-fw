# How to Test

1. Have database runing with [sql Schema](../code/jvm/turngamesfw/sql/create-schema.sql) or run ./gradlew dbTestUp [here](../code/jvm/turngamesfw/) in terminal with docker runing to start database with schema.

2. Set database url in [application](../code/jvm/turngamesfw/application-module/src/main/kotlin/pt/isel/application/Application.kt).

3. Start application.

4. Run npm start in [js](../code/js/turngamesfw/).