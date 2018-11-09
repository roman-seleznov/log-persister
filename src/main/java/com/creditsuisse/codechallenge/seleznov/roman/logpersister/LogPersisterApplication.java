package com.creditsuisse.codechallenge.seleznov.roman.logpersister;

import com.creditsuisse.codechallenge.seleznov.roman.logpersister.domain.LogEntry;
import com.creditsuisse.codechallenge.seleznov.roman.logpersister.domain.ServerEvent;
import org.aopalliance.aop.Advice;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.*;
import org.springframework.integration.channel.interceptor.WireTap;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.file.splitter.FileSplitter;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.jpa.core.JpaExecutor;
import org.springframework.integration.jpa.outbound.JpaOutboundGateway;
import org.springframework.integration.jpa.support.PersistMode;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.MatchAlwaysTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableIntegration
@EntityScan(basePackageClasses = ServerEvent.class)
@EnableTransactionManagement
public class LogPersisterApplication {

    @Value("${file}")
    String file;

    @Value("${tail}")
    boolean tail;

    @Bean
    public MessageChannel fileInputChannel() {
        return MessageChannels
          .direct()
          .interceptor(new WireTap(logChannel()))
          .get();
    }

    @Bean
    public MessageChannel transformerChannel() {
        return MessageChannels.direct().get();
    }

    @Bean
    public MessageChannel aggregatorChannel() {
        return MessageChannels.direct().get();
    }

    @Bean
    public MessageChannel persistenceChannel() {
        return MessageChannels
          .direct()
          .interceptor(new WireTap(logChannel()))
          .get();
    }

    @Bean
    public MessageChannel logChannel() {
        return MessageChannels.direct().get();
    }

    @Bean
    @InboundChannelAdapter(value = "fileInputChannel", poller = @Poller(fixedDelay = "1000"))
    public MessageSource<File> fileReadingMessageSource() {
        FileReadingMessageSource sourceReader= new FileReadingMessageSource();

        File fileToRead = new File(file).getAbsoluteFile();
        sourceReader.setDirectory(fileToRead.getParentFile());

        CompositeFileListFilter<File> filter = new CompositeFileListFilter<>();
        filter.addFilter(new AcceptOnceFileListFilter<>());

        filter.addFilter(new SimplePatternFileListFilter(fileToRead.getName()));

        sourceReader.setFilter(filter);
        sourceReader.setAutoCreateDirectory(false);
        return sourceReader;
    }

    @Splitter(inputChannel="fileInputChannel")
    @Bean
    public MessageHandler fileSplitter() {
        FileSplitter splitter = new FileSplitter(true, false);
        splitter.setApplySequence(true);
        splitter.setOutputChannel(transformerChannel());
        return splitter;
    }

    @Bean
    @Transformer(inputChannel = "transformerChannel", outputChannel = "aggregatorChannel")
    public org.springframework.integration.transformer.Transformer transformer() {
        return Transformers.fromJson(LogEntry.class);
    }

    @Bean
    @ServiceActivator(inputChannel = "logChannel")
    public MessageHandler logger() {
        LoggingHandler loggingHandler =  new LoggingHandler(LoggingHandler.Level.INFO.name());
        loggingHandler.setLoggerName(getClass().getName());
        return loggingHandler;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JpaExecutor jpaExecutor() {
        JpaExecutor jpaExecutor = new JpaExecutor(this.entityManager);
        jpaExecutor.setEntityClass(ServerEvent.class);
        jpaExecutor.setPersistMode(PersistMode.PERSIST);
        return jpaExecutor;
    }

    @Bean
    @ServiceActivator(inputChannel="persistenceChannel")
    public JpaOutboundGateway save(JpaExecutor jpaExecutor, PlatformTransactionManager transactionManager) {

        DefaultTransactionAttribute dta = new DefaultTransactionAttribute();
        dta.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        dta.setReadOnly(false);

        MatchAlwaysTransactionAttributeSource matas = new MatchAlwaysTransactionAttributeSource();
        matas.setTransactionAttribute(dta);

        TransactionInterceptor ic = new TransactionInterceptor();
        ic.setTransactionManager(transactionManager);
        ic.setTransactionAttributeSource(matas);

        List<Advice> adviceChain = new ArrayList<>();
        adviceChain.add(ic);

        JpaOutboundGateway gateway = new JpaOutboundGateway(jpaExecutor);
        gateway.setAdviceChain(adviceChain);
        gateway.setProducesReply(false);

        return gateway;
    }

    public static void main(String[] args) {
        SpringApplication.run(LogPersisterApplication.class, args);
    }

}
