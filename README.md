# weather rest app
## Requirements
Create a spring rest app that :
1. stores data in any rational DB (MySQL) and any NoSQL DB
2. consumes data from https://openweathermap.org/current (need free registration) and stores it in the NoSQL DB
3. exposes current REST calls:
    1. get an average temperature for the last 10 data calls (point 2)
    2. post that triggers the consuming of new data (secured). 
    3. get all data calls (point 2) and exposes a CSV file.
4. Users are authorised with spring security (looks in relational DB).

5. Only authorised users can access REST API

## Implementation
### Installation
1. Install latest JDK 10 (Oracle JDK is recommended).
    ```java
    $ java -version
    java version "10.0.2" 2018-07-17
    Java(TM) SE Runtime Environment 18.3 (build 10.0.2+13)
    Java HotSpot(TM) 64-Bit Server VM 18.3 (build 10.0.2+13, mixed mode)
    ```
2. Install MySQL CE [5.7](https://dev.mysql.com/downloads/mysql/5.7.html#downloads)

    2.1. Execute the following SQL scripts:
    ```sql
    $ mysql -u root -p < db/ddl.sql
    $ mysql -u root -p < db/data.sql
    ```
3. Install Elastic Search via docker
    ```sh
    # Start docker containers
    $ docker-compose up
    # Stop docker containers
    $ docker-compose down
    ```

    3.1. Fix Ubuntu issue on docker-compose down
    ```sh
    $ sudo killall docker-containerd-shim
    $ sudo docker-compose down
    ```
    3.2. Check whether ES works
    ```bash
    $ curl --request GET --url http://localhost:9200/_cat/health?v
    ```
    3.3. Check wheter Kibana works - [Open](http://localhost:5601) in browser
4. Start weather rest app - execute gradle task _bootRun_
5. Use latest PostMan version (_v6.2.5_) as a rest client

    5.1. Test weather rest app
    - Provider your App ID in src/main/java/resources/_application.properties_ - openweather.appid
    ```properties
    openweather.appid={please provide your App ID here}
    ```
    - import client/SpringRestApp.postman_collection.json
    - Use Authorizaiton=Basic Auth
    - User credentials are spring/spring

    5.2. Test openweather API - import client/OpenWeather.postman_collection.json
    - Replace _{{appIdVal}}_ with your App ID.