import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gopitch.GoPitch.domain.ImageClub;
import com.gopitch.GoPitch.repository.ImageClubRepository;

@Service
public class ImageClubService {
    private final ClubService clubService;
    private final ImageClubRepository imageClubRepository;

    public ImageClubService(ClubService clubService, ImageClubRepository imageClubRepository) {
        this.clubService = clubService;
        this.imageClubRepository = imageClubRepository;
    }

}
