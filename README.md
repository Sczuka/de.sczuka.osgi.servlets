# de.sczuka.osgi.servlets

Servlets for monitoring the OSGi environment.

## de.sczuka.osgi.servlets.provider

| Name | Description |
|---|---|
| [ServiceGraph](https://github.com/Sczuka/de.sczuka.osgi.servlets#servicegraph) | OSGi component graph as graphics |


### ServiceGraph

The OSGi *ServiceScraph* servlet provides a graphical visualization of all running components, their service references and service implementations. It is rendered as [graphviz](https://graphviz.gitlab.io/) and made accessible through the http endpoint "http://localhost:8080/sg".

In order to run the servlet run the *launch.bndrun*.

The ServiceGraph servlet uses the *ServiceComponentRuntime* to query the components and generated a *dot* graph which then is rendered by the [viz-js](http://viz-js.com).


## Build setup

### Local build

For local builds use the gradle wrapper.

```shell
./gradlew clean jar
```

### Jenkins

The build instructions for Jenkins are stored in the _Jenkinsfile_. Configure your build job to be a *pipeline job* and point to the [git Servlets repository](https://github.com/Sczuka/de.sczuka.osgi.servlets.git).

Following Jenkins plugins are used:

* JUnitResultArchiver
* ArtifactArchiver

#### Setting up your Jenkins

Set up the following global Jenkins variables when accessing the internet through a proxy:

| Variable | Description |
|---|---|
| ftp_proxy | ftp proxy url |
| http_proxy | http proxy url |
| https_proxy | https proxy url |
| no_proxy | The proxy exceptions |
| GRADLE_OPTIONS | e.g.: "-Dhttp.proxyHost=myProxyHost -Dhttp.proxyPort=myProxyPort -Dhttps.proxyHost=myProxyHost -Dhttps.proxyPort=myProxyPort" |

You must provide an URL to you proxy certificate when you using a proxy that breaks the certificate chain.

e.g.:
```
http://any.local.url/my-sertificate.cer
```

The location of the proxy certificate is done by the variable *PROXY_CERTIFICATE_URL*.