package com.gopitch.GoPitch.service;

import com.gopitch.GoPitch.domain.ExtraService;
import com.gopitch.GoPitch.repository.ExtraServiceRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ExtraServiceService {
    private final ExtraServiceRepository extraServiceRepository;

    public ExtraServiceService(ExtraServiceRepository extraServiceRepository) {
        this.extraServiceRepository = extraServiceRepository;
    }

    public List<ExtraService> getServicesByClub(long clubId) {
        return extraServiceRepository.findByClubId(clubId);
    }

    public ExtraService createService(ExtraService service) {
        return extraServiceRepository.save(service);
    }
}
