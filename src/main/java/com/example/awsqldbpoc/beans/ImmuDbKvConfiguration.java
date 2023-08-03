package com.example.awsqldbpoc.beans;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("immudbkv")
@Configuration
public class ImmuDbKvConfiguration extends ImmuDbConfiguration {

}
