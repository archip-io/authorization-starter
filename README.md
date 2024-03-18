# Библиотека аутентификации и авторизации

Добро пожаловать в репозиторий Common Auth! Common Auth - это зависимость, которая содержит общие классы для
аутентификации и авторизации микросервисов

## Технологии

- Java 17
- Spring Security 6
- JWT
- Maven

# Использование

1. Необходимо получить токен персонального доступа (классический) как минимум с `read:packages` возможностью;
2. Отредактируйте `~/.m2/settings.xml`, заменив USERNAME и TOKEN на свои имя пользователя и токен персонального доступа
   соответственно:

   ```xml
   <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">
       <activeProfiles>
           <activeProfile>github</activeProfile>
       </activeProfiles>
   
       <profiles>
           <profile>
               <id>github</id>
               <repositories>
                   <repository>
                       <id>central</id>
                       <url>https://repo1.maven.org/maven2</url>
                   </repository>
                   <repository>
                       <id>github</id>
                       <url>https://maven.pkg.github.com/archip-io/common-auth</url>
                       <snapshots>
                           <enabled>true</enabled>
                       </snapshots>
                   </repository>
               </repositories>
           </profile>
       </profiles>
   
       <servers>
           <server>
               <id>github</id>
               <username>USERNAME</username>
               <password>TOKEN</password>
           </server>
       </servers>
   </settings>
   ```

3. Добавьте зависимость в `pom.xml`:

   ```xml
   <dependencies>
      <dependency>
         <groupId>com.archipio</groupId>
         <artifactId>common-auth</artifactId>
         <version>0.0.0</version>
      </dependency>
      <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-security</artifactId>
      </dependency>
   </dependencies>
   ```
   
4. Установите зависимость `com.archipio:common-auth:0.0.0`:

   ```shell
   mvn install
   ```
   
5. Настройте конфигурацию безопасности приложения:

   ```java
   @Configuration
   @EnableMethodSecurity
   @EnableWebSecurity
   public class SecurityConfig {
   
      @Bean
      public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         return http.csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(request -> request.anyRequest().permitAll())
            .addFilterBefore(new JwtVerifierFilter(new JwtTokenProvider()), UsernamePasswordAuthenticationFilter.class) // Добавление фильтра проверки JWT токена и аутентификации
            .build();
      }
      
      // Если нужна более детальная настройка CORS, удалите этот Bean и используйте аннотации @CrossOrigin на контроллерах и его мето 
      @Bean
      public CorsConfigurationSource corsConfigurationSource() {
         CorsConfiguration configuration = new CorsConfiguration();
         configuration.setAllowedOrigins(List.of("*")); // Можно исправить на конкретные домены 
         configuration.setAllowedHeaders(List.of("*")); // Можно исправить на конкретные хедеры
         configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE")); // Можно исправить на конкретные методы
         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
         source.registerCorsConfiguration("/**", configuration);
         return source;
      }
   
      // Заглушка
      @Bean
      public UserDetailsService userDetailsService() {
         return new InMemoryUserDetailsManager();
      }
   }
   ```
   
6. Защитите ресурсы при помощи аннотаций `@PreAuthorize`

    ```java
    @PreAuthorize("hasAuthority('READ')")
    @PostMapping("/")
    public ResponseEntity<Void> read(@AuthenticationPrincipal UserDetailsImpl user) {
       System.out.println(user);
       return ResponseEntity.status(OK).build();
    }
    ```

## Вклад в проект

Если вы хотите внести свой вклад в проект, вы можете следовать этим шагам:

1. Создайте форк этого репозитория.
2. Внесите необходимые изменения.
3. Создайте pull request, описывая ваши изменения.