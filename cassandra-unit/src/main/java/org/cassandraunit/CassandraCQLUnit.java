package org.cassandraunit;

import org.cassandraunit.dataset.CQLDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.RetryPolicy;
/**
 * @author Marcin Szymaniuk
 * @author Jeremy Sevellec
 */
public class CassandraCQLUnit extends BaseCassandraUnit {
    private CQLDataSet dataSet;

    private static final Logger log = LoggerFactory.getLogger(CassandraCQLUnit.class);
    private String hostIp = "127.0.0.1";
    private int port = 9142;
    private RetryPolicy retryPolicy;
    public Session session;
    public Cluster cluster;


    public CassandraCQLUnit(CQLDataSet dataSet) {
        this.dataSet = dataSet;
    }

    public CassandraCQLUnit(CQLDataSet dataSet, String configurationFileName) {
        this(dataSet);
        this.configurationFileName = configurationFileName;
    }

    public CassandraCQLUnit(CQLDataSet dataSet, String configurationFileName, String hostIp, int port) {
        this(dataSet);
        this.configurationFileName = configurationFileName;
        this.hostIp = hostIp;
        this.port = port;
    }

    public CassandraCQLUnit(CQLDataSet dataSet, String configurationFileName, String hostIp, int port, long startUpTimeout) {
        super(startUpTimeout);
        this.dataSet = dataSet;
        this.configurationFileName = configurationFileName;
        this.hostIp = hostIp;
        this.port = port;
    }

    public CassandraCQLUnit(CQLDataSet dataSet, String configurationFileName, String hostIp, int port, long startUpTimeout, RetryPolicy retryPolicy) {
        this(dataSet, configurationFileName, hostIp, port, startUpTimeout);
		this.retryPolicy = retryPolicy;
    }

    protected void load() {
        cluster = cluster();
        session = cluster.connect();
        CQLDataLoader dataLoader = new CQLDataLoader(session);
        dataLoader.load(dataSet);
//        session = dataLoader.getSession();
    }

	private Cluster cluster() {
		Cluster.Builder builder = new Cluster.Builder()
			.addContactPoints(hostIp)
			.withPort(port)
			.withQueryOptions(new QueryOptions()
			.setConsistencyLevel(ConsistencyLevel.QUORUM));
		
		if (retryPolicy != null) {
			builder.withRetryPolicy(retryPolicy);
		}
		
		return builder.build();
	}

}
