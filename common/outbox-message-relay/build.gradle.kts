dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.kafka:spring-kafka")
    implementation(project(":common:snowflake"))
    implementation(project(":common:event"))
    runtimeOnly("com.mysql:mysql-connector-j")
}