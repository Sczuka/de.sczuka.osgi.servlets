package de.sczuka.osgi.servlets.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;
import org.osgi.service.component.runtime.dto.ReferenceDTO;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

@Component(property = { HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN + "=/sg/*" })
public class ServiceGraph extends HttpServlet implements Servlet {

	private static final String NEW_LINE = "\n";

	private static final long serialVersionUID = 1L;

	BundleContext context;

	@Reference
	ServiceComponentRuntime scr;

	@Activate
	void activate(BundleContext context) {
		this.context = context;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		if (request.getRequestURL().toString().contains("full.render.js")) {
			try (InputStream stream = getClass().getClassLoader().getResourceAsStream("resources/full.render.js")) {
				try (Scanner scanner = new Scanner(stream, "UTF-8")) {
					response.getWriter().print(scanner.useDelimiter("\\A").next());
					return;
				}
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}

		if (request.getRequestURL().toString().contains("viz.js")) {
			try (InputStream stream = getClass().getClassLoader().getResourceAsStream("resources/viz.js")) {
				try (Scanner scanner = new Scanner(stream, "UTF-8")) {
					response.getWriter().print(scanner.useDelimiter("\\A").next());
					return;
				}
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}

		Collection<ComponentDescriptionDTO> dtos = scr.getComponentDescriptionDTOs(context.getBundles());

		StringBuilder sbGraph = new StringBuilder();

		renderGraph(sbGraph, dtos);

		StringBuilder sb = new StringBuilder();

		String path = request.getRequestURL().toString();

		sb.append("<html>").append(NEW_LINE);
		sb.append("<head>").append(NEW_LINE);
		sb.append("<meta charset=\"utf-8\">").append(NEW_LINE);

		sb.append("<script src=\"" + path + "/viz.js\"></script>").append(NEW_LINE);
		sb.append("<script src=\"" + path + "/full.render.js\"></script>").append(NEW_LINE);

		sb.append("</head>").append(NEW_LINE);

		sb.append("<body>").append(NEW_LINE);

		sb.append("<div id=\"vgraph\"></div>").append(NEW_LINE);

		sb.append("<script>").append(NEW_LINE);
		sb.append("  var viz = new Viz();").append(NEW_LINE);
		sb.append("  var content = '").append(sbGraph.toString()).append("'").append(NEW_LINE);
		sb.append("  viz.renderSVGElement(content)").append(NEW_LINE);
		sb.append("    .then(function(element)").append(NEW_LINE);
		sb.append("    { document.getElementById('vgraph').appendChild(element); })").append(NEW_LINE);
		sb.append("    .catch(error => { console.error(error); });").append(NEW_LINE);
		sb.append("</script>").append(NEW_LINE);

		sb.append("</body>").append(NEW_LINE);
		sb.append("</html>").append(NEW_LINE);

		response.getWriter().println(sb.toString());
	}

	static class MappingEntry<T> {
		public final String name;
		public final T dto;

		public MappingEntry(String name, T dto) {
			this.name = name;
			this.dto = dto;
		}
	}

	private void renderGraph(StringBuilder sb, Collection<ComponentDescriptionDTO> dtos) {
		sb.append("digraph { ");

		sb.append("edge [arrowsize=2,arrowhead=onormal];");
		// Define components

		Map<String, MappingEntry<ComponentDescriptionDTO>> components = new HashMap<>();

		AtomicLong counter = new AtomicLong(0);
		dtos.forEach(
				dto -> components.put(dto.name, new MappingEntry<>("component_" + counter.incrementAndGet(), dto)));

		components.forEach((k, v) -> sb.append(v.name).append(" [label=\"").append(k)
				.append("\", shape=\"component\",style=\"filled\",fillcolor=\"yellow\"];"));

		// Define services

		Map<String, MappingEntry<ReferenceDTO>> services = new HashMap<>();

		counter.set(0);
		dtos.forEach(dto -> {
			for (ReferenceDTO ref : dto.references) {
				services.put(ref.interfaceName, new MappingEntry<>("service_" + counter.incrementAndGet(), ref));
			}

			for (String iface : dto.serviceInterfaces) {
				services.computeIfAbsent(iface, i -> {
					ReferenceDTO dtoNew = new ReferenceDTO();
					dtoNew.name = iface;
					return new MappingEntry<>("service_" + counter.incrementAndGet(), dtoNew);
				});
			}
		});

		services.forEach((k, v) -> sb.append(v.name).append(" [label=\"").append(k).append("\", shape=\"box\"];"));

		// Connect components and services

		components.entrySet().forEach(entry -> {
			for (ReferenceDTO ref : entry.getValue().dto.references) {
				sb.append(entry.getValue().name).append(" -> ").append(services.get(ref.interfaceName).name)
						.append("[label=\"<<uses>>\"];");
			}
			
			for (String iface : entry.getValue().dto.serviceInterfaces) {
				sb.append(entry.getValue().name).append(" -> ").append(services.get(iface).name)
				.append("[label=\"<<implements>>\",arrowhead=oinv];");
			}
		});

		sb.append("}");
	}

}