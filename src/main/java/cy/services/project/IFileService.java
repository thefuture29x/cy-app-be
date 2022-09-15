package cy.services.project;

import cy.dtos.project.FileDto;
import cy.entities.project.FileEntity;
import cy.models.project.FileModel;
import cy.services.IBaseService;

public interface IFileService extends IBaseService<FileEntity, FileModel, FileDto, Long> {

}
