package ec.com.test.infrastructure.config;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Sistema de Gestión de Transferencias Interbancarias API",
                version = "1.0.0",
                description = "API REST desarrollada con Java Quarkus para la administración de cuentas bancarias y ejecución de transferencias interbancarias atómicas con detección de fraude.",
                contact = @Contact(
                        name = "Soporte Técnico Backend",
                        email = "soporte@banco.com.ec"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"
                )
        ),
        servers = {
                @Server(url = "/", description = "Servidor Actual")
        }
)
public class OpenAPIConfig extends Application {
}
