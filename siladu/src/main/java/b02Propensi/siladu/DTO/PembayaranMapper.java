package b02Propensi.siladu.DTO;
import org.mapstruct.Mapper;

import b02Propensi.siladu.DTO.request.CreatePembayaranRequestDTO;
import b02Propensi.siladu.DTO.request.UpdatePembayaranRequestDTO;
import b02Propensi.siladu.model.PaketLayanan;
import b02Propensi.siladu.model.Pembayaran;

@Mapper(componentModel = "spring")
public interface PembayaranMapper {
      Pembayaran createPembayaranRequestDTOtoPembayaran(CreatePembayaranRequestDTO createPembayaranRequestDTO);
      Pembayaran updatePembayaranRequestDTOtoPembayaran(UpdatePembayaranRequestDTO updatePembayaranRequestDTO);
      UpdatePembayaranRequestDTO pembayaranToUpdatePembayaranRequestDTO(Pembayaran pembayaran);
}
