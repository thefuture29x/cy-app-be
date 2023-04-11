package cy.services.common;

import cy.dtos.common.FileDto;
import cy.entities.common.FileEntity;
import cy.models.common.FileModel;
import cy.services.common.IBaseService;

public interface IFileService extends IBaseService<FileEntity, FileDto, FileModel, Long> {
    public FileEntity addEntity(FileModel model);
}
