package io.paymentgateway.core.service.impl;

import io.paymentgateway.core.domain.CountryPaymentMethod;
import io.paymentgateway.core.repository.CountryPaymentMethodRepository;
import io.paymentgateway.core.service.CountryPaymentMethodService;
import io.paymentgateway.core.service.dto.CountryPaymentMethodDTO;
import io.paymentgateway.core.service.mapper.CountryPaymentMethodMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.paymentgateway.core.domain.CountryPaymentMethod}.
 */
@Service
@Transactional
public class CountryPaymentMethodServiceImpl implements CountryPaymentMethodService {

    private static final Logger LOG = LoggerFactory.getLogger(CountryPaymentMethodServiceImpl.class);

    private final CountryPaymentMethodRepository countryPaymentMethodRepository;

    private final CountryPaymentMethodMapper countryPaymentMethodMapper;

    public CountryPaymentMethodServiceImpl(
        CountryPaymentMethodRepository countryPaymentMethodRepository,
        CountryPaymentMethodMapper countryPaymentMethodMapper
    ) {
        this.countryPaymentMethodRepository = countryPaymentMethodRepository;
        this.countryPaymentMethodMapper = countryPaymentMethodMapper;
    }

    @Override
    public CountryPaymentMethodDTO save(CountryPaymentMethodDTO countryPaymentMethodDTO) {
        LOG.debug("Request to save CountryPaymentMethod : {}", countryPaymentMethodDTO);
        CountryPaymentMethod countryPaymentMethod = countryPaymentMethodMapper.toEntity(countryPaymentMethodDTO);
        countryPaymentMethod = countryPaymentMethodRepository.save(countryPaymentMethod);
        return countryPaymentMethodMapper.toDto(countryPaymentMethod);
    }

    @Override
    public CountryPaymentMethodDTO update(CountryPaymentMethodDTO countryPaymentMethodDTO) {
        LOG.debug("Request to update CountryPaymentMethod : {}", countryPaymentMethodDTO);
        CountryPaymentMethod countryPaymentMethod = countryPaymentMethodMapper.toEntity(countryPaymentMethodDTO);
        countryPaymentMethod = countryPaymentMethodRepository.save(countryPaymentMethod);
        return countryPaymentMethodMapper.toDto(countryPaymentMethod);
    }

    @Override
    public Optional<CountryPaymentMethodDTO> partialUpdate(CountryPaymentMethodDTO countryPaymentMethodDTO) {
        LOG.debug("Request to partially update CountryPaymentMethod : {}", countryPaymentMethodDTO);

        return countryPaymentMethodRepository
            .findById(countryPaymentMethodDTO.getId())
            .map(existingCountryPaymentMethod -> {
                countryPaymentMethodMapper.partialUpdate(existingCountryPaymentMethod, countryPaymentMethodDTO);

                return existingCountryPaymentMethod;
            })
            .map(countryPaymentMethodRepository::save)
            .map(countryPaymentMethodMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CountryPaymentMethodDTO> findOne(Long id) {
        LOG.debug("Request to get CountryPaymentMethod : {}", id);
        return countryPaymentMethodRepository.findById(id).map(countryPaymentMethodMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CountryPaymentMethod : {}", id);
        countryPaymentMethodRepository.deleteById(id);
    }
}
