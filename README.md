# SigningConfigBug

Sample project demonstrating a bug in AGP 3.3+ where a task meant to update the signing config stopped working.

The config that this task is meant to set is probably possible just via `signingConfig` blocks, but in our actual app, the task calls out to a command line tool which gets out signing passwords. Also, it seems to be a known issue that configuring based on product flavors is not well supported, and this task makes that simpler.

### AGP 3.2.1 behavior/expected behavior
```
$ ./gradlew clean assembleNonproductionRelease && jarsigner -verify -verbose -certs ./app/build/outputs/apk/nonproduction/release/app-nonproduction-release.apk | grep "Signed by"

...

BUILD SUCCESSFUL in 6s
33 actionable tasks: 32 executed, 1 up-to-date
- Signed by "CN=Sha Sha NonProd, OU=Pinterest, O=Pinterest, L=San Francisco, ST=CA, C=US"
```

```
$ ./gradlew clean assembleProductionRelease && jarsigner -verify -verbose -certs ./app/build/outputs/apk/production/release/app-production-release.apk | grep "Signed by"

...

BUILD SUCCESSFUL in 7s
33 actionable tasks: 32 executed, 1 up-to-date
- Signed by "CN=Sha Sha Chu, OU=Pinterest, O=Pinterest, L=San Francisco, ST=CA, C=US"
```

### AGP 3.3.2+ behavior/broken behavior

(Tested in 3.3.2, 3.4.0, and 3.5.0-alpha13)

```
$ ./gradlew clean assembleNonproductionRelease
> Task :app:packageNonproductionRelease FAILED

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':app:packageNonproductionRelease'.
> 1 exception was raised by workers:
  java.lang.RuntimeException: java.lang.RuntimeException: com.android.ide.common.signing.KeytoolException: Failed to read key mykey from store "/Users/shasha/code/shashachu/SigningConfigBug/app/production.keystore": Keystore was tampered with, or password was incorrect
```

```
$ cat ./app/build/intermediates/signing_config/nonproductionRelease/out/signing-config.json
{"mName":"release","mStoreFile":"/Users/shasha/code/shashachu/SigningConfigBug/app/production.keystore","mStorePassword":"","mKeyAlias":"mykey","mKeyPassword":"","mStoreType":"jks","mV1SigningEnabled":true,"mV2SigningEnabled":true}
```

### Things already tried
* Changing the dependent task of the `updateXXXSigningConfig` tasks
  * Having it run before validateSigning and signingConfigWriter tasks
* Having the `updateXXXSigningConfig` tasks depend on `cleanSigningConfigWriterXXXRelease`