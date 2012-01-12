package org.openmaji.implementation.rpc.security;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.ConfirmationCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class KeystoreCallbackHandler implements CallbackHandler
{
  private final char[] keyStorePassword;
  private final String username;
  private final String password;

  public KeystoreCallbackHandler(char[] keyStorePassword, String username, String password)
  {
    this.keyStorePassword = keyStorePassword;
    this.username = username;
    this.password = password;
  }

  /**
   * 
   */
  public void handle(Callback[] callbacks) throws UnsupportedCallbackException
  {
    for (int i = 0; i < callbacks.length; i++)
    {
      Callback callback = callbacks[i];

      //
      // a TextOutputCallback is simply used to provide the handler with
      // messages that may need to be displayed to the user.
      //
      if (callback instanceof TextOutputCallback) {
        TextOutputCallback toc = (TextOutputCallback) callbacks[i];
        switch (toc.getMessageType())
        {
          case TextOutputCallback.INFORMATION :
            System.out.println("INFORMATION: " + toc.getMessage());
            break;
          case TextOutputCallback.ERROR :
            System.out.println("ERROR: " + toc.getMessage());
            break;
          case TextOutputCallback.WARNING :
            System.out.println("WARNING: " + toc.getMessage());
            break;
          default :
            throw new UnsupportedCallbackException(callback, "Unsupported message type: " + toc.getMessageType());
        }
      }
      //
      // the NameCallback is used by the keystore login to get the alias
      // the key is stored in the keystore under. This would normally be
      // the user name
      //
      else if (callback instanceof NameCallback) {
          NameCallback nc = (NameCallback) callbacks[i];
          nc.setName(username);
        }
      //
      // PasswordCallback is used for requesting passwords - in this
      // case we have a password for the keystore and also a password
      // for the key.
      //
      else if (callback instanceof PasswordCallback)  {
          PasswordCallback pc = (PasswordCallback) callbacks[i];
          String prompt = pc.getPrompt();
          if (prompt.startsWith("Private key"))
          {
            pc.setPassword(password.toCharArray());
          }
          else if (prompt.startsWith("Keystore")) {
              pc.setPassword(keyStorePassword);
          }
          else {
              throw new UnsupportedCallbackException(callback, "Unrecognized password prompt: " + prompt);
          }
      }
      //
      // at the last stage of the login we get back a confirmation message (for information only)
      //
      else if (callback instanceof ConfirmationCallback) {
          // ignore
      }
      else {
          throw new UnsupportedCallbackException(callback, "Unrecognized Callback");
      }
    }
  }
}
