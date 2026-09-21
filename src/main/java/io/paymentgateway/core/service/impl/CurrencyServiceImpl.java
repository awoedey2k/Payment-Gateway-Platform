package io.paymentgateway.core.service.impl;

import io.paymentgateway.core.domain.Currency;
import io.paymentgateway.core.repository.CurrencyRepository;
import io.paymentgateway.core.service.CurrencyService;
import io.paymentgateway.core.service.dto.CurrencyDTO;
import io.paymentgateway.core.service.mapper.CurrencyMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.paymentgateway.core.domain.Currency}.
 */
@Service
@Transactional
public class CurrencyServiceImpl implements CurrencyService {

    private static final Logger LOG = LoggerFactory.getLogger(CurrencyServiceImpl.class);

    private final CurrencyRepository currencyRepository;

    private final CurrencyMapper currencyMapper;

    public CurrencyServiceImpl(CurrencyRepository currencyRepository, CurrencyMapper currencyMapper) {
        this.currencyRepository = currencyRepository;
        this.currencyMapper = currencyMapper;
    }

    @Override
    public CurrencyDTO save(CurrencyDTO currencyDTO) {
        LOG.debug("Request to save Currency : {}", currencyDTO);
        Currency currency = currencyMapper.toEntity(currencyDTO);
        currency = currencyRepository.save(currency);
        return currencyMapper.toDto(currency);
    }

    @Override
    public CurrencyDTO update(CurrencyDTO currencyDTO) {
        LOG.debug("Request to update Currency : {}", currencyDTO);
        Currency currency = currencyMapper.toEntity(currencyDTO);
        currency = currencyRepository.save(currency);
        return currencyMapper.toDto(currency);
    }

    @Override
    public Optional<CurrencyDTO> partialUpdate(CurrencyDTO currencyDTO) {
        LOG.debug("Request to partially update Currency : {}", currencyDTO);

        return currencyRepository
            .findById(currencyDTO.getId())
            .map(existingCurrency -> {
                currencyMapper.partialUpdate(existingCurrency, currencyDTO);

                return existingCurrency;
            })
            .map(currencyRepository::save)
            .map(currencyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CurrencyDTO> findOne(Long id) {
        LOG.debug("Request to get Currency : {}", id);
        return currencyRepository.findById(id).map(currencyMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Currency : {}", id);
        currencyRepository.deleteById(id);
    }
}
