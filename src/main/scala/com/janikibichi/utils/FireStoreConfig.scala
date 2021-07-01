package com.janikibichi.utils

import java.io.InputStream

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.storage.{Bucket, Storage, StorageOptions}
import com.google.firebase.{FirebaseApp, FirebaseOptions}
import com.google.firebase.cloud.{FirestoreClient, StorageClient}
import com.typesafe.config.ConfigFactory

object FireStoreConfig{

  // SET UP ADMIN AUTH MAP
  val auth: java.util.Map[String, Object] = new java.util.HashMap[String, Object]()
  auth.put("uid", "Complicated-Auth-Token")

  // USE A SERVICE ACCOUNT WITH CREDENTIALS AS AN ENV VARIABLE IN K8 SECRETS VOLUME MOUNT
  val options: FirebaseOptions = FirebaseOptions
    .builder()
    .setCredentials(GoogleCredentials.getApplicationDefault())
    .setStorageBucket("your-globally-unique-bucket-name.appspot.com")
    .setDatabaseAuthVariableOverride(auth) // ADD AUTH TO RE REQUEST AS PER SET PROD FIRESTORE DB RULES
    .build()

  FirebaseApp.initializeApp(options)

  // MAKE THE DATABASE AVAILABLE
  val database: Firestore = FirestoreClient.getFirestore()

  // MAKE BUCKET AVAILABLE
  val bucket: Bucket = StorageClient.getInstance().bucket()

  // MAKE STORAGE AVAILABLE
  val storage: Storage = StorageOptions.getDefaultInstance.getService

}
