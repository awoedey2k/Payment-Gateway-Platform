package io.paymentgateway.core.service.impl;

import io.paymentgateway.core.domain.LedgerAccount;
import io.paymentgateway.core.repository.LedgerAccountRepository;
import io.paymentgateway.core.service.LedgerAccountService;
import io.paymentgateway.core.service.dto.LedgerAccountDTO;
import io.paymentgateway.core.service.mapper.LedgerAccountMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.paymentgateway.core.domain.LedgerAccount}.
 */
@Service
@Transactional
public class LedgerAccountServiceImpl implements LedgerAccountService {

    private static final Logger LOG = LoggerFactory.getLogger(LedgerAccountServiceImpl.class);

    private final LedgerAccountRepository ledgerAccountRepository;

    private final LedgerAccountMapper ledgerAccountMapper;

    public LedgerAccountServiceImpl(LedgerAccountRepository ledgerAccountRepository, LedgerAccountMapper ledgerAccountMapper) {
        this.ledgerAccountRepository = ledgerAccountRepository;
        this.ledgerAccountMapper = ledgerAccountMapper;
    }

    @Override
    public LedgerAccountDTO save(LedgerAccountDTO ledgerAccountDTO) {
        LOG.debug("Request to save LedgerAccount : {}", ledgerAccountDTO);
        LedgerAccount ledgerAccount = ledgerAccountMapper.toEntity(ledgerAccountDTO);
        ledgerAccount = ledgerAccountRepository.save(ledgerAccount);
        return ledgerAccountMapper.toDto(ledgerAccount);
    }

    @Override
    public LedgerAccountDTO update(LedgerAccountDTO ledgerAccountDTO) {
        LOG.debug("Request to update LedgerAccount : {}", ledgerAccountDTO);
        LedgerAccount ledgerAccount = ledgerAccountMapper.toEntity(ledgerAccountDTO);
        ledgerAccount = ledgerAccountRepository.save(ledgerAccount);
        return ledgerAccountMapper.toDto(ledgerAccount);
    }

    @Override
    public Optional<LedgerAccountDTO> partialUpdate(LedgerAccountDTO ledgerAccountDTO) {
        LOG.debug("Request to partially update LedgerAccount : {}", ledgerAccountDTO);

        return ledgerAccountRepository
            .findById(ledgerAccountDTO.getId())
            .map(existingLedgerAccount -> {
                ledgerAccountMapper.partialUpdate(existingLedgerAccount, ledgerAccountDTO);

                return existingLedgerAccount;
            })
            .map(ledgerAccountRepository::save)
            .map(ledgerAccountMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LedgerAccountDTO> findOne(Long id) {
        LOG.debug("Request to get LedgerAccount : {}", id);
        return ledgerAccountRepository.findById(id).map(ledgerAccountMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete LedgerAccount : {}", id);
        ledgerAccountRepository.deleteById(id);
    }
}
