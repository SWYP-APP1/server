package com.swyp.futsal.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import java.io.FileInputStream;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {

  @Value("${firebase.credentials.path}")
  private String firebaseCredentialsPath;

  @Bean
  public FirebaseAuth firebaseAuth() throws IOException {
    FileInputStream serviceAccount = new FileInputStream(firebaseCredentialsPath);

    FirebaseOptions options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build();

    FirebaseApp.initializeApp(options);
    return FirebaseAuth.getInstance();
  }
}

