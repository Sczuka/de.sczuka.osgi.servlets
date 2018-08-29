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

The OSGi build is done by [gradle](https://gradle.org/) and [bndtools](http://bndtools.org/).
