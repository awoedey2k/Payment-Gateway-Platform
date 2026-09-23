package io.paymentgateway.core.service.impl;

import io.paymentgateway.core.domain.RoutingRule;
import io.paymentgateway.core.repository.RoutingRuleRepository;
import io.paymentgateway.core.service.RoutingRuleService;
import io.paymentgateway.core.service.dto.RoutingRuleDTO;
import io.paymentgateway.core.service.mapper.RoutingRuleMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.paymentgateway.core.domain.RoutingRule}.
 */
@Service
@Transactional
public class RoutingRuleServiceImpl implements RoutingRuleService {

    private static final Logger LOG = LoggerFactory.getLogger(RoutingRuleServiceImpl.class);

    private final RoutingRuleRepository routingRuleRepository;

    private final RoutingRuleMapper routingRuleMapper;

    public RoutingRuleServiceImpl(RoutingRuleRepository routingRuleRepository, RoutingRuleMapper routingRuleMapper) {
        this.routingRuleRepository = routingRuleRepository;
        this.routingRuleMapper = routingRuleMapper;
    }

    @Override
    public RoutingRuleDTO save(RoutingRuleDTO routingRuleDTO) {
        LOG.debug("Request to save RoutingRule : {}", routingRuleDTO);
        RoutingRule routingRule = routingRuleMapper.toEntity(routingRuleDTO);
        routingRule = routingRuleRepository.save(routingRule);
        return routingRuleMapper.toDto(routingRule);
    }

    @Override
    public RoutingRuleDTO update(RoutingRuleDTO routingRuleDTO) {
        LOG.debug("Request to update RoutingRule : {}", routingRuleDTO);
        RoutingRule routingRule = routingRuleMapper.toEntity(routingRuleDTO);
        routingRule = routingRuleRepository.save(routingRule);
        return routingRuleMapper.toDto(routingRule);
    }

    @Override
    public Optional<RoutingRuleDTO> partialUpdate(RoutingRuleDTO routingRuleDTO) {
        LOG.debug("Request to partially update RoutingRule : {}", routingRuleDTO);

        return routingRuleRepository
            .findById(routingRuleDTO.getId())
            .map(existingRoutingRule -> {
                routingRuleMapper.partialUpdate(existingRoutingRule, routingRuleDTO);

                return existingRoutingRule;
            })
            .map(routingRuleRepository::save)
            .map(routingRuleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RoutingRuleDTO> findOne(Long id) {
        LOG.debug("Request to get RoutingRule : {}", id);
        return routingRuleRepository.findById(id).map(routingRuleMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete RoutingRule : {}", id);
        routingRuleRepository.deleteById(id);
    }
}
