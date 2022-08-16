package cy.services.impl;

import cy.dtos.RequestDayOffDto;
import cy.dtos.ResponseDto;
import cy.entities.RequestDayOffEntity;
import cy.entities.UserEntity;
import cy.models.RequestDayOffModel;
import cy.repositories.IRequestDayOffRepository;
import cy.repositories.IUserRepository;
import cy.services.IRequestDayOffService;
import cy.utils.FileUploadProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.jaxb.PageAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class RequestDayOffServiceImpl implements IRequestDayOffService {
    @Autowired
    IRequestDayOffRepository iRequestDayOffRepository;
    @Autowired
    IUserRepository userRepository;
    @Autowired
    FileUploadProvider fileUploadProvider;
    @Override
    public RequestDayOffDto createOrUpdate(RequestDayOffModel requestDayOffModel) throws IOException {
        RequestDayOffEntity requestDayOff = null;
        if(requestDayOffModel.getId() != null){
            requestDayOff = iRequestDayOffRepository.findById(requestDayOffModel.getId()).orElse(null);
        }
        if(requestDayOff == null){
            requestDayOff = new RequestDayOffEntity();
        }
        if(requestDayOffModel.getAssignId() != null){
            UserEntity assignTo = userRepository.findById(requestDayOffModel.getAssignId()).orElse(null);
            if(assignTo != null)
                requestDayOff.setAssignTo(assignTo);
        }
        if(requestDayOffModel.getCreatedById() != null){
            UserEntity createdBy = userRepository.findById(requestDayOffModel.getCreatedById()).orElse(null);
            if(createdBy != null)
                requestDayOff.setCreateBy(createdBy);
        }
        requestDayOff.setReasonCancel(requestDayOffModel.getReasonCancel());
        requestDayOff.setStatus(requestDayOffModel.getStatus());
        if(requestDayOffModel.getFiles() != null && requestDayOffModel.getFiles().size() > 0){
            List<String> files = new ArrayList<>();
            for(MultipartFile fileMultipart : requestDayOffModel.getFiles()){
                if(!fileMultipart.isEmpty()){
                    String result = fileUploadProvider.uploadFile("requestDayOff",fileMultipart);
                    files.add(result);
                }
            }
            requestDayOff.setFiles(files.toString());
        }
        requestDayOff.setDateDayOff(new Date());
        requestDayOff = iRequestDayOffRepository.save(requestDayOff);
        return RequestDayOffDto.toDto(requestDayOff);
    }

    @Override
    public Page<RequestDayOffDto> getByPage(Integer pageIndex, Integer pageSize) {
        Pageable page = PageRequest.of(pageIndex, pageSize);
        Page<RequestDayOffEntity> findByPage = iRequestDayOffRepository.findBypage(page);
        return findByPage.map(x->RequestDayOffDto.toDto(x));
    }
}
