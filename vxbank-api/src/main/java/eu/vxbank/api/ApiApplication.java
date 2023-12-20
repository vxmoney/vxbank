package eu.vxbank.api;

import eu.vxbank.api.utils.components.SystemService;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxExampleModel;
import vxbank.datastore.data.service.VxDsService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;

@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")})
@RestController
@SpringBootApplication
public class ApiApplication {


	@Autowired
	SystemService systemService;



	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

	@GetMapping("/")
	public String hello() {
		return "Hello world!\n";
	}

	@GetMapping("/testDatastore")
	public String testDatastore(){
		VxBankDatastore datastore = systemService.getVxBankDatastore();
		VxExampleModel exampleModel = new VxExampleModel();
		exampleModel.id = 1L;
		exampleModel.description = "Test from vxbank api run 2";

		VxBankDatastore ds = systemService.getVxBankDatastore();
		VxDsService.persist(exampleModel,ds,VxExampleModel.class);


		return "Managed to persist new VxBankDatastore";
	}



}
