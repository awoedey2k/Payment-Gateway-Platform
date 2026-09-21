package io.paymentgateway.core.extended.tenant.service.checkout;

import io.paymentgateway.core.domain.TenantDomain;
import io.paymentgateway.core.extended.tenant.domain.CheckoutConfig;
import io.paymentgateway.core.extended.tenant.repository.ExtendedTenantDomainRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom-domain (CNAME) tenant resolution and locale hydration for the checkout page (spec Chunk 2 §5.2).
 *
 * <p>Only verified domains resolve. The locale is chosen from the browser's {@code Accept-Language} against the tenant's
 * {@code supportedLocales}; when nothing matches it falls back to the domain's {@code defaultLocale}, then
 * {@code fallbackLocale}, then {@value #GLOBAL_FALLBACK}.
 */
@Service
public class CheckoutDomainService {

    static final String GLOBAL_FALLBACK = "en_US";

    private final ExtendedTenantDomainRepository domains;

    public CheckoutDomainService(ExtendedTenantDomainRepository domains) {
        this.domains = domains;
    }

    @Transactional(readOnly = true)
    public Optional<CheckoutConfig> resolve(String hostHeader, String acceptLanguage) {
        String host = normaliseHost(hostHeader);
        if (host.isEmpty()) {
            return Optional.empty();
        }
        return domains
            .findByHost(host)
            .filter(d -> Boolean.TRUE.equals(d.getIsVerified()))
            .map(d -> toConfig(d, acceptLanguage));
    }

    static String normaliseHost(String hostHeader) {
        if (hostHeader == null) {
            return "";
        }
        String host = hostHeader.trim().toLowerCase(Locale.ROOT);
        int colon = host.lastIndexOf(':');
        if (colon > 0 && host.indexOf(']') < colon) {
            host = host.substring(0, colon);
        }
        return host.endsWith(".") ? host.substring(0, host.length() - 1) : host;
    }

    private CheckoutConfig toConfig(TenantDomain domain, String acceptLanguage) {
        List<String> supported = parseList(domain.getSupportedLocales());
        return new CheckoutConfig(
            domain.getTenant().getId(),
            domain.getCustomDomain(),
            negotiate(domain, supported, acceptLanguage),
            supported
        );
    }

    static String negotiate(TenantDomain domain, List<String> supported, String acceptLanguage) {
        if (acceptLanguage != null && !acceptLanguage.isBlank() && !supported.isEmpty()) {
            try {
                List<Locale.LanguageRange> wanted = Locale.LanguageRange.parse(acceptLanguage);
                List<String> tags = supported
                    .stream()
                    .map(s -> s.replace('_', '-'))
                    .toList();
                String match = Locale.lookupTag(wanted, tags);
                if (match != null) {
                    return supported.get(tags.indexOf(match));
                }
                // No exact match (e.g. "fr-CA" against ["fr_SN"]): accept the same language in any region.
                for (Locale.LanguageRange range : wanted) {
                    String language = range.getRange().split("-")[0];
                    for (int i = 0; i < tags.size(); i++) {
                        if (
                            tags
                                .get(i)
                                .toLowerCase(Locale.ROOT)
                                .startsWith(language.toLowerCase(Locale.ROOT) + "-")
                        ) {
                            return supported.get(i);
                        }
                    }
                }
            } catch (IllegalArgumentException malformedHeader) {
                // A malformed Accept-Language header is ignored, not an error.
            }
        }
        return firstNonBlank(domain.getDefaultLocale(), domain.getFallbackLocale(), GLOBAL_FALLBACK);
    }

    private static List<String> parseList(String csv) {
        if (csv == null || csv.isBlank()) {
            return List.of();
        }
        return Arrays.stream(csv.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .toList();
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return GLOBAL_FALLBACK;
    }
}
