package edu.kpi.testcourse.rest;

import edu.kpi.testcourse.logic.Logic;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationException;
import io.micronaut.security.authentication.AuthenticationFailed;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.UserDetails;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import java.util.ArrayList;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.reactivestreams.Publisher;

/**
 * Micronaut authentication bean that contains authorization logic: ensures that a user is
 * registered in the system and password is right.
 */
@Singleton
public class AuthenticationProviderUserPassword implements AuthenticationProvider {

  private final Logic logic;

  @Inject
  public AuthenticationProviderUserPassword(Logic logic) {
    this.logic = logic;
  }

  @Override
  public Publisher<AuthenticationResponse> authenticate(
      @Nullable HttpRequest<?> httpRequest,
      AuthenticationRequest<?, ?> authenticationRequest
  ) {
    return Flowable.create(emitter -> {
      String email = (String) authenticationRequest.getIdentity();
      String password = (String) authenticationRequest.getSecret();
      if (logic.isUserValid(email, password)) {
        emitter
          .onNext(new UserDetails(email, new ArrayList<>()));
        emitter.onComplete();
      } else {
        emitter.onError(new AuthenticationException(new AuthenticationFailed()));
      }
    }, BackpressureStrategy.ERROR);
  }
}
