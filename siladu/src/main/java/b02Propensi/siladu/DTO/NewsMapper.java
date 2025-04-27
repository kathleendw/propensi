package b02Propensi.siladu.DTO;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import b02Propensi.siladu.DTO.request.AddNewsRequestDTO;
import b02Propensi.siladu.DTO.request.EditNewsRequestDTO;
import b02Propensi.siladu.DTO.response.ReadNewsResponseDTO;
import b02Propensi.siladu.model.News;

@Mapper(componentModel = "spring")
public interface NewsMapper {
    
    News addNewsRequestDTOToNews(AddNewsRequestDTO addNewsRequestDTO);
    
    ReadNewsResponseDTO newsToReadNewsResponseDTO(News news);

    News editNewsRequestDTOToNews(EditNewsRequestDTO editNewsRequestDTO);

    EditNewsRequestDTO newsToEditNewsRequestDTO(News news);
}
