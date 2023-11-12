package eu.vxbank.api;

import eu.vxbank.api.endpoints.response.PingResponse;
import eu.vxbank.api.utils.components.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import vxbank.datastore.VxBankDatastore;
import vxbank.datastore.data.models.VxDataTestEntity;
import vxbank.datastore.data.service.VxdTestEntityService;

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
		VxDataTestEntity testEntity = new VxDataTestEntity();
		testEntity.objectId = "01";
		testEntity.message = "Test from vxbank api run 2";

		VxdTestEntityService.persist(testEntity,datastore);

		return "Managed to persist new VxBankDatastore";
	}



}
