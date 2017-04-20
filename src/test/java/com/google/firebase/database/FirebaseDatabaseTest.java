package com.google.firebase.database;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.TestOnlyImplFirebaseTrampolines;
import com.google.firebase.auth.FirebaseCredential;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.testing.ServiceAccount;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.junit.AfterClass;
import org.junit.Test;

public class FirebaseDatabaseTest {
  
  private static FirebaseOptions firebaseOptions =
      new FirebaseOptions.Builder()
          .setCredential(createCertificateCredential())
          .setDatabaseUrl("https://firebase-db-test.firebaseio.com")
          .build();

  @AfterClass
  public static void tearDownClass() {
    TestOnlyImplFirebaseTrampolines.clearInstancesForTest();
  }

  private static FirebaseCredential createCertificateCredential() {
    return FirebaseCredentials.fromCertificate(ServiceAccount.EDITOR.asStream());
  }

  @Test
  public void testGetInstance() throws ExecutionException, InterruptedException {
    FirebaseApp.initializeApp(firebaseOptions);
    FirebaseDatabase defaultDatabase = FirebaseDatabase.getInstance();
    assertNotNull(defaultDatabase);
    assertSame(defaultDatabase, FirebaseDatabase.getInstance());
  }

  @Test
  public void testGetInstanceForApp() throws ExecutionException, InterruptedException {
    FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "testGetInstanceForApp");
    FirebaseDatabase db = FirebaseDatabase.getInstance(app);
    assertNotNull(db);
    assertSame(db, FirebaseDatabase.getInstance(app));
  }

  @Test
  public void testAppDelete() throws ExecutionException, InterruptedException {
    FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "testAppDelete");
    FirebaseDatabase db = FirebaseDatabase.getInstance(app);
    assertNotNull(db);
    app.delete();

    try {
      db.getReference();
      fail("No error thrown when calling method on database after delete");
    } catch (IllegalStateException expected) {
      // ignore
    }

    try {
      FirebaseDatabase.getInstance(app);
      fail("No error thrown when getting db instance after deleting app");
    } catch (IllegalStateException expected) {
      // ignore
    }
  }

  @Test
  public void testInitAfterAppDelete() throws ExecutionException, InterruptedException,
      TimeoutException {
    FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "testInitAfterAppDelete");
    FirebaseDatabase db1 = FirebaseDatabase.getInstance(app);
    assertNotNull(db1);
    app.delete();

    app = FirebaseApp.initializeApp(firebaseOptions, "testInitAfterAppDelete");
    FirebaseDatabase db2 = FirebaseDatabase.getInstance(app);
    assertNotNull(db2);
    assertNotSame(db1, db2);
  }
}