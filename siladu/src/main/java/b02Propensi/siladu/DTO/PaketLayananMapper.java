package b02Propensi.siladu.DTO;
import org.mapstruct.Mapper;

import b02Propensi.siladu.DTO.request.CreatePaketLayananRequestDTO;
import b02Propensi.siladu.DTO.request.UpdatePaketLayananRequestDTO;
import b02Propensi.siladu.DTO.response.ReadPaketLayananResponseDTO;
import b02Propensi.siladu.model.PaketLayanan;
@Mapper(componentModel = "spring")
public interface PaketLayananMapper {
    PaketLayanan createPaketLayananRequestDTOtoPaketLayanan(CreatePaketLayananRequestDTO createPaketLayananRequestDTO);
    PaketLayanan updatePaketLayananRequestDTOToPaketLayanan(UpdatePaketLayananRequestDTO updatePaketLayananRequestDTO);
    UpdatePaketLayananRequestDTO paketLayananToUpdatePaketLayananRequestDTO(PaketLayanan paketLayanan);
    ReadPaketLayananResponseDTO paketLayananToReadPaketLayananResponseDTO(PaketLayanan paketLayanan);
}
