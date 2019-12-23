package org.sonar.samples.java;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

/**
 * @author Anton Khitrov
 * @since 19 дек. 2019 г.
 */
public class UsafeApiInvokeTest {

@Test
public void test(){
    JavaCheckVerifier.verify("src/test/files/UnsafeApiClassCheck.java", new DetectUnsafeApiRule());
}

}
