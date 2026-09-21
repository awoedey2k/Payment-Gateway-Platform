package io.paymentgateway.core.config;

import com.github.benmanes.caffeine.jcache.configuration.CaffeineConfiguration;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.boot.cache.autoconfigure.JCacheManagerCustomizer;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.jhipster.config.JHipsterProperties;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Caffeine caffeine = jHipsterProperties.getCache().getCaffeine();

        CaffeineConfiguration<Object, Object> caffeineConfiguration = new CaffeineConfiguration<>();
        caffeineConfiguration.setMaximumSize(OptionalLong.of(caffeine.getMaxEntries()));
        caffeineConfiguration.setExpireAfterWrite(OptionalLong.of(TimeUnit.SECONDS.toNanos(caffeine.getTimeToLiveSeconds())));
        caffeineConfiguration.setStatisticsEnabled(true);
        jcacheConfiguration = caffeineConfiguration;
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, io.paymentgateway.core.repository.UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, io.paymentgateway.core.repository.UserRepository.USERS_BY_EMAIL_CACHE);
            createCache(cm, io.paymentgateway.core.domain.User.class.getName());
            createCache(cm, io.paymentgateway.core.domain.Authority.class.getName());
            createCache(cm, io.paymentgateway.core.domain.User.class.getName() + ".authorities");
            createCache(cm, io.paymentgateway.core.domain.CorporateTenant.class.getName());
            createCache(cm, io.paymentgateway.core.domain.CorporateTenant.class.getName() + ".tenantDirectors");
            createCache(cm, io.paymentgateway.core.domain.CorporateTenant.class.getName() + ".apiKeys");
            createCache(cm, io.paymentgateway.core.domain.CorporateTenant.class.getName() + ".tenantDomains");
            createCache(cm, io.paymentgateway.core.domain.TenantDirector.class.getName());
            createCache(cm, io.paymentgateway.core.domain.ApiKey.class.getName());
            createCache(cm, io.paymentgateway.core.domain.TenantDomain.class.getName());
            createCache(cm, io.paymentgateway.core.domain.Currency.class.getName());
            createCache(cm, io.paymentgateway.core.domain.Country.class.getName());
            createCache(cm, io.paymentgateway.core.domain.PaymentMethod.class.getName());
            createCache(cm, io.paymentgateway.core.domain.CountryPaymentMethod.class.getName());
            createCache(cm, io.paymentgateway.core.domain.ForexRate.class.getName());
            createCache(cm, io.paymentgateway.core.domain.RoutingRule.class.getName());
            createCache(cm, io.paymentgateway.core.domain.Transaction.class.getName());
            createCache(cm, io.paymentgateway.core.domain.Transaction.class.getName() + ".refunds");
            createCache(cm, io.paymentgateway.core.domain.Refund.class.getName());
            createCache(cm, io.paymentgateway.core.domain.TenantWallet.class.getName());
            createCache(cm, io.paymentgateway.core.domain.LedgerAccount.class.getName());
            createCache(cm, io.paymentgateway.core.domain.JournalEntry.class.getName());
            createCache(cm, io.paymentgateway.core.domain.JournalEntry.class.getName() + ".journalLines");
            createCache(cm, io.paymentgateway.core.domain.JournalLine.class.getName());
            createCache(cm, io.paymentgateway.core.domain.TenantFeeConfig.class.getName());
            createCache(cm, io.paymentgateway.core.domain.PayoutSchedule.class.getName());
            createCache(cm, io.paymentgateway.core.domain.SettlementBatch.class.getName());
            createCache(cm, io.paymentgateway.core.domain.Dispute.class.getName());
            createCache(cm, io.paymentgateway.core.domain.DisputeEvidence.class.getName());
            createCache(cm, io.paymentgateway.core.domain.AmlCheck.class.getName());
            createCache(cm, io.paymentgateway.core.domain.AuditLogEntry.class.getName());
            createCache(cm, io.paymentgateway.core.domain.WebhookSubscription.class.getName());
            createCache(cm, io.paymentgateway.core.domain.WebhookDeliveryAttempt.class.getName());
            // jhipster-needle-caffeine-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }
}
