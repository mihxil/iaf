/*
 * $Log: SpringTxExecutorBase.java,v $
 * Revision 1.1  2007-10-09 15:54:43  europe\L190409
 * Direct copy from Ibis-EJB:
 * first version in HEAD of txSupport classes
 *
 */
package nl.nn.adapterframework.txsupport;

import nl.nn.adapterframework.util.LogUtil;

import org.apache.log4j.Logger;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Base class for excecutor implementations that use a transaction manager provided 
 * by the Spring framework to ensure the method is executed in the right transaction state.
 * 
 * @author  Gerrit van Brakel
 * @since   4.8
 * @version Id
 */
public class SpringTxExecutorBase {
	protected Logger log=LogUtil.getLogger(SpringTxExecutorBase.class);

    public final static TransactionDefinition TXREQUIRED = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);
    public final static TransactionDefinition TXSUPPORTS = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_SUPPORTS);
    public final static TransactionDefinition TXNEW = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    public final static TransactionDefinition TXNOTSUPPORTED = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);
    public final static TransactionDefinition TXMANDATORY = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_MANDATORY);
    public final static TransactionDefinition TXNEVER = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_NEVER);
    
    protected PlatformTransactionManager txManager;

    public void setTxManager(PlatformTransactionManager txManager) {
        this.txManager = txManager;
    }
	public PlatformTransactionManager getTxManager() {
		return txManager;
	}

}