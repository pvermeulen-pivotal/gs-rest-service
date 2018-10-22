package io.pivotal.geode.beacon.rest;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.client.PoolFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.xml.GemfireConstants;
import org.springframework.data.gemfire.support.ConnectionEndpoint;

@SpringBootApplication
@PropertySource("classpath:application.properties")
@ClientCacheApplication
public class Application {

	@Bean
	ClientCacheFactoryBean gemfireCache(@Value("${gemfire.auto.serialzer.classes}") String classes) {
		ClientCacheFactoryBean gemfireCache = new ClientCacheFactoryBean();
		gemfireCache.setPdxSerializer(new ReflectionBasedAutoSerializer(classes));
		gemfireCache.setClose(true);
		return gemfireCache;
	}

	@Bean(name = GemfireConstants.DEFAULT_GEMFIRE_POOL_NAME)
	PoolFactoryBean gemfirePool(@Value("${gemfire.locator.host:localhost}") String host,
			@Value("${gemfire.locator.port:10334}") int port,
			@Value("${gemfire.max.connections:800}") int maxConnections,
			@Value("${gemfire.single.hop.enabled:true}") boolean singleHop,
			@Value("${gemfire.read.timeout:10000}") int readTimeout,
			@Value("${gemfire.retry.attempts:-1}") int retryAttempts,
			@Value("${gemfire.subscription.enabled:false}") boolean subscriptionEnabled) {
		PoolFactoryBean gemfirePool = new PoolFactoryBean();
		gemfirePool.setKeepAlive(false);
		gemfirePool.setMaxConnections(800);
		gemfirePool.setPrSingleHopEnabled(true);
		gemfirePool.setReadTimeout(15000);
		gemfirePool.setRetryAttempts(-1);
		gemfirePool.setSubscriptionEnabled(false);
		gemfirePool.setThreadLocalConnections(false);
		gemfirePool.addLocators(new ConnectionEndpoint(host, port));
		return gemfirePool;
	}

	@Bean(name = "beacon")
	ClientRegionFactoryBean<String, Object> myRegion(@Value("${gemfire.region.name}") String regionName,
			GemFireCache gemfireCache, Pool gemfirePool) {
		ClientRegionFactoryBean<String, Object> inboundMessageQueue = new ClientRegionFactoryBean<>();
		inboundMessageQueue.setCache(gemfireCache);
		inboundMessageQueue.setName(regionName);
		inboundMessageQueue.setPool(gemfirePool);
		inboundMessageQueue.setShortcut(ClientRegionShortcut.PROXY);
		return inboundMessageQueue;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
