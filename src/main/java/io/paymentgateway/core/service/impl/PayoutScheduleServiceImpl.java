package io.paymentgateway.core.service.impl;

import io.paymentgateway.core.domain.PayoutSchedule;
import io.paymentgateway.core.repository.PayoutScheduleRepository;
import io.paymentgateway.core.service.PayoutScheduleService;
import io.paymentgateway.core.service.dto.PayoutScheduleDTO;
import io.paymentgateway.core.service.mapper.PayoutScheduleMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.paymentgateway.core.domain.PayoutSchedule}.
 */
@Service
@Transactional
public class PayoutScheduleServiceImpl implements PayoutScheduleService {

    private static final Logger LOG = LoggerFactory.getLogger(PayoutScheduleServiceImpl.class);

    private final PayoutScheduleRepository payoutScheduleRepository;

    private final PayoutScheduleMapper payoutScheduleMapper;

    public PayoutScheduleServiceImpl(PayoutScheduleRepository payoutScheduleRepository, PayoutScheduleMapper payoutScheduleMapper) {
        this.payoutScheduleRepository = payoutScheduleRepository;
        this.payoutScheduleMapper = payoutScheduleMapper;
    }

    @Override
    public PayoutScheduleDTO save(PayoutScheduleDTO payoutScheduleDTO) {
        LOG.debug("Request to save PayoutSchedule : {}", payoutScheduleDTO);
        PayoutSchedule payoutSchedule = payoutScheduleMapper.toEntity(payoutScheduleDTO);
        payoutSchedule = payoutScheduleRepository.save(payoutSchedule);
        return payoutScheduleMapper.toDto(payoutSchedule);
    }

    @Override
    public PayoutScheduleDTO update(PayoutScheduleDTO payoutScheduleDTO) {
        LOG.debug("Request to update PayoutSchedule : {}", payoutScheduleDTO);
        PayoutSchedule payoutSchedule = payoutScheduleMapper.toEntity(payoutScheduleDTO);
        payoutSchedule = payoutScheduleRepository.save(payoutSchedule);
        return payoutScheduleMapper.toDto(payoutSchedule);
    }

    @Override
    public Optional<PayoutScheduleDTO> partialUpdate(PayoutScheduleDTO payoutScheduleDTO) {
        LOG.debug("Request to partially update PayoutSchedule : {}", payoutScheduleDTO);

        return payoutScheduleRepository
            .findById(payoutScheduleDTO.getId())
            .map(existingPayoutSchedule -> {
                payoutScheduleMapper.partialUpdate(existingPayoutSchedule, payoutScheduleDTO);

                return existingPayoutSchedule;
            })
            .map(payoutScheduleRepository::save)
            .map(payoutScheduleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PayoutScheduleDTO> findOne(Long id) {
        LOG.debug("Request to get PayoutSchedule : {}", id);
        return payoutScheduleRepository.findById(id).map(payoutScheduleMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete PayoutSchedule : {}", id);
        payoutScheduleRepository.deleteById(id);
    }
}
