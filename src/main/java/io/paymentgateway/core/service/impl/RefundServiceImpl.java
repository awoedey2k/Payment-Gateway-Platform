package io.paymentgateway.core.service.impl;

import io.paymentgateway.core.domain.Refund;
import io.paymentgateway.core.repository.RefundRepository;
import io.paymentgateway.core.service.RefundService;
import io.paymentgateway.core.service.dto.RefundDTO;
import io.paymentgateway.core.service.mapper.RefundMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.paymentgateway.core.domain.Refund}.
 */
@Service
@Transactional
public class RefundServiceImpl implements RefundService {

    private static final Logger LOG = LoggerFactory.getLogger(RefundServiceImpl.class);

    private final RefundRepository refundRepository;

    private final RefundMapper refundMapper;

    public RefundServiceImpl(RefundRepository refundRepository, RefundMapper refundMapper) {
        this.refundRepository = refundRepository;
        this.refundMapper = refundMapper;
    }

    @Override
    public RefundDTO save(RefundDTO refundDTO) {
        LOG.debug("Request to save Refund : {}", refundDTO);
        Refund refund = refundMapper.toEntity(refundDTO);
        refund = refundRepository.save(refund);
        return refundMapper.toDto(refund);
    }

    @Override
    public RefundDTO update(RefundDTO refundDTO) {
        LOG.debug("Request to update Refund : {}", refundDTO);
        Refund refund = refundMapper.toEntity(refundDTO);
        refund = refundRepository.save(refund);
        return refundMapper.toDto(refund);
    }

    @Override
    public Optional<RefundDTO> partialUpdate(RefundDTO refundDTO) {
        LOG.debug("Request to partially update Refund : {}", refundDTO);

        return refundRepository
            .findById(refundDTO.getId())
            .map(existingRefund -> {
                refundMapper.partialUpdate(existingRefund, refundDTO);

                return existingRefund;
            })
            .map(refundRepository::save)
            .map(refundMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefundDTO> findOne(Long id) {
        LOG.debug("Request to get Refund : {}", id);
        return refundRepository.findById(id).map(refundMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Refund : {}", id);
        refundRepository.deleteById(id);
    }
}
