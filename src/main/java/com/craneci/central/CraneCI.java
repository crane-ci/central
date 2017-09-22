package com.craneci.central;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;

@SpringBootApplication
@EnableScheduling
@RestController
public class CraneCI {

	public static void main(String[] args) {
		SpringApplication.run(CraneCI.class, args);
	}
	
	@Bean
	public HazelcastInstance hazelcastInstance() {
		Config config = new Config();
        return Hazelcast.newHazelcastInstance(config);
	}
	
	
	@Autowired
	HazelcastInstance hz;
	
	List<String> result = new ArrayList<>();
	
	@Scheduled(fixedDelay=1)
	public void response() throws Exception {
		IQueue<String> q = hz.getQueue("message");
		result.add(q.take());
	}
	
	@RequestMapping("/cmd/{cmd}")
	public String index(@PathVariable String cmd) throws Exception {
		IQueue<String> q = hz.getQueue("command");
		q.put(cmd);
		Thread.sleep(1000);		
		return String.join("\n", result);
	}
	
}
