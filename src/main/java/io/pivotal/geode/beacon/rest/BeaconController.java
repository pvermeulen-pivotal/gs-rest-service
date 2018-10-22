package io.pivotal.geode.beacon.rest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.execute.ResultCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.pivotal.gemfire.domain.BeaconRequest;
import io.pivotal.gemfire.domain.BeaconResponse;

@RestController
public class BeaconController {

	private static final String SUCCESS_STATUS = "success";
	private static final String ERROR_STATUS = "error";
	private static final int CODE_SUCCESS = 200;
	private static final int BAD_REQUEST = 400;
	private static final String FUNCTION_ID = "GetBeaconResponse";

	private final Logger log = LoggerFactory.getLogger(BeaconController.class);

	private ObjectMapper mapper = new ObjectMapper();

	@Resource
	Region<String, Object> beacon;

	@RequestMapping(value = "/receiveEvent", method = RequestMethod.PUT)
	public Response beacon(@RequestBody BeaconRequest request) {
		Response baseResponse = new Response();
		if ((request.getCustomerId() != null) && (request.getDeviceId() != null) && (request.getUuid() != null)
				&& (request.getMajor() > 0) && (request.getMinor() > 0) && (request.getSignalPower() != 0)) {
			baseResponse.setStatus(SUCCESS_STATUS);
			baseResponse.setCode(CODE_SUCCESS);
			sendBeaconToGemFire(request, baseResponse);
		} else {
			baseResponse.setPayload("Invalid beacon request");
			baseResponse.setStatus(ERROR_STATUS);
			baseResponse.setCode(BAD_REQUEST);
		}

		return baseResponse;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void sendBeaconToGemFire(BeaconRequest request, Response baseResponse) {
		String msg = null;
		try {
			Set<String> keys = new HashSet<String>();
			keys.add(request.getKey());
			Execution execution = FunctionService.onRegion(beacon).withFilter(keys).setArguments(request);
			ResultCollector results = execution.execute(FUNCTION_ID);
			if (results != null && results.getResult() != null) {
				List<BeaconResponse> resultList = (List<BeaconResponse>) results.getResult();
				baseResponse.setPayload(mapper.writeValueAsString(resultList.get(0)));
				baseResponse.setStatus(SUCCESS_STATUS);
				baseResponse.setCode(CODE_SUCCESS);
				return;
			}
		} catch (Exception e) {
			msg = "Exception executing function " + FUNCTION_ID + " exception: " + e.getMessage();
			log.error(msg);
		}
		if (msg != null) {
			baseResponse.setPayload(msg);
		} else {
			baseResponse.setPayload("No response to beacon request");
		}
		baseResponse.setStatus(ERROR_STATUS);
		baseResponse.setCode(BAD_REQUEST);
	}
}
