package cy.services.impl;

import cy.dtos.CustomHandleException;
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
import org.springframework.data.jpa.domain.Specification;
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
    public List<RequestDayOffDto> findAll() {
        return null;
    }

    @Override
    public Page<RequestDayOffDto> findAll(Pageable page) {
        Page<RequestDayOffEntity> findByPage = iRequestDayOffRepository.findBypage(page);
        return findByPage.map(x -> RequestDayOffDto.toDto(x));
    }

    @Override
    public List<RequestDayOffDto> findAll(Specification<RequestDayOffEntity> specs) {
        return null;
    }

    @Override
    public Page<RequestDayOffDto> filter(Pageable page, Specification<RequestDayOffEntity> specs) {
        return null;
    }

    @Override
    public RequestDayOffDto findById(Long id) {
        return null;
    }

    @Override
    public RequestDayOffEntity getById(Long id) {
        return this.iRequestDayOffRepository.findById(id).orElseThrow(() -> new CustomHandleException(99999));
    }

    @Override
    public RequestDayOffDto add(RequestDayOffModel requestDayOffModel) {
        RequestDayOffEntity requestDayOff = null;
        if (requestDayOffModel.getId() != null) {
            requestDayOff = iRequestDayOffRepository.findById(requestDayOffModel.getId()).orElse(null);
        }
        if (requestDayOff == null) {
            requestDayOff = new RequestDayOffEntity();
        }
        if (requestDayOffModel.getAssignId() != null) {
            UserEntity assignTo = userRepository.findById(requestDayOffModel.getAssignId()).orElse(null);
            if (assignTo != null)
                requestDayOff.setAssignTo(assignTo);
        }
        if (requestDayOffModel.getCreatedById() != null) {
            UserEntity createdBy = userRepository.findById(requestDayOffModel.getCreatedById()).orElse(null);
            if (createdBy != null)
                requestDayOff.setCreateBy(createdBy);
        }
        requestDayOff.setReasonCancel(requestDayOffModel.getReasonCancel());
        requestDayOff.setStatus(requestDayOffModel.getStatus());
        if (requestDayOffModel.getFiles() != null && requestDayOffModel.getFiles().size() > 0) {
            List<String> files = new ArrayList<>();
            for (MultipartFile fileMultipart : requestDayOffModel.getFiles()) {
                if (!fileMultipart.isEmpty()) {
                    try {
                        String result = fileUploadProvider.uploadFile("requestDayOff", fileMultipart);
                        files.add(result);
                    } catch (Exception e) {
                        System.out.println("upload file failed");
                    }
                }
            }
            requestDayOff.setFiles(files.toString());
        }
        requestDayOff.setDateDayOff(new Date());
        requestDayOff = iRequestDayOffRepository.save(requestDayOff);
        return RequestDayOffDto.toDto(requestDayOff);
    }

    @Override
    public List<RequestDayOffDto> add(List<RequestDayOffModel> model) {
        return null;
    }

    @Override
    public RequestDayOffDto update(RequestDayOffModel requestDayOffModel) {
        RequestDayOffEntity requestDayOff = null;
        if (requestDayOffModel.getId() != null) {
            requestDayOff = iRequestDayOffRepository.findById(requestDayOffModel.getId()).orElse(null);
        }
        if (requestDayOff == null) {
            requestDayOff = new RequestDayOffEntity();
        }
        if (requestDayOffModel.getAssignId() != null) {
            UserEntity assignTo = userRepository.findById(requestDayOffModel.getAssignId()).orElse(null);
            if (assignTo != null)
                requestDayOff.setAssignTo(assignTo);
        }
        if (requestDayOffModel.getCreatedById() != null) {
            UserEntity createdBy = userRepository.findById(requestDayOffModel.getCreatedById()).orElse(null);
            if (createdBy != null)
                requestDayOff.setCreateBy(createdBy);
        }
        requestDayOff.setReasonCancel(requestDayOffModel.getReasonCancel());
        requestDayOff.setStatus(requestDayOffModel.getStatus());
        if (requestDayOffModel.getFiles() != null && requestDayOffModel.getFiles().size() > 0) {
            List<String> files = new ArrayList<>();
            for (MultipartFile fileMultipart : requestDayOffModel.getFiles()) {
                if (!fileMultipart.isEmpty()) {
                    try {
                        String result = fileUploadProvider.uploadFile("requestDayOff", fileMultipart);
                        files.add(result);
                    } catch (Exception e) {
                        System.out.println("upload file failed");
                    }
                }
            }
            requestDayOff.setFiles(files.toString());
        }
        requestDayOff.setDateDayOff(new Date());
        requestDayOff = iRequestDayOffRepository.save(requestDayOff);
        return RequestDayOffDto.toDto(requestDayOff);
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            iRequestDayOffRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }
}
