package cy.services.impl;

import cy.models.CreateAttendRequest;
import cy.dtos.CustomHandleException;
import cy.dtos.RequestAttendDto;
import cy.dtos.UserDto;
import cy.entities.RequestAttendEntity;
import cy.models.RequestAttendModel;
import cy.repositories.IUserRepository;
import cy.services.IRequestAttendService;
import cy.utils.FileUploadProvider;
import cy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class RequestAttendServiceImpl implements IRequestAttendService {
    @Autowired
    private FileUploadProvider fileUploadProvider;

    @Autowired
    private IUserRepository userRepository;

    @Override
    public List<RequestAttendDto> findAll() {
        return null;
    }

    @Override
    public Page<RequestAttendDto> findAll(Pageable page) {
        return null;
    }

    @Override
    public List<RequestAttendDto> findAll(Specification<RequestAttendEntity> specs) {
        return null;
    }

    @Override
    public Page<RequestAttendDto> filter(Pageable page, Specification<RequestAttendEntity> specs) {
        return null;
    }

    @Override
    public RequestAttendDto findById(Long id) {
        return null;
    }

    @Override
    public RequestAttendDto add(RequestAttendModel model) {
        return null;
    }

    @Override
    public List<RequestAttendDto> add(List<RequestAttendModel> model) {
        return null;
    }

    @Override
    public RequestAttendDto update(RequestAttendModel model) {
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        return false;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }

    public RequestAttendDto requestToDto(CreateAttendRequest request){
        final String folderName = "user/" + SecurityUtils.getCurrentUsername() + "/request_attend/";
        List<String> fileS3Urls = new ArrayList<>();
        if(request.getAttachedFiles() != null){ // Check if user has attached files
            for(MultipartFile file : request.getAttachedFiles()){
                try{
                    String s3Url = fileUploadProvider.uploadFile(folderName, file);
                    fileS3Urls.add(s3Url);
                }catch (Exception ex){
                    ex.printStackTrace();
                    fileS3Urls.add("Error code: 31");
                    throw new CustomHandleException(31);
                }
            }
        }

        // Check if requested user id exist
        userRepository.findById(request.getRequestUserId()).orElseThrow(() -> new CustomHandleException(32));

        // Check if assigned user id exist
        userRepository.findById(request.getAssignUserId()).orElseThrow(() -> new CustomHandleException(33));

        return RequestAttendDto.builder()
                .timeCheckIn(request.getTimeCheckIn())
                .timeCheckOut(request.getTimeCheckOut())
                .dateRequestAttend(request.getDateRequestAttend())
                .status(0)
                .reasonCancel("")
                .files(fileS3Urls)
                .createdBy(UserDto.builder().id(SecurityUtils.getCurrentUserId()).build())
                .build();
    }
}
