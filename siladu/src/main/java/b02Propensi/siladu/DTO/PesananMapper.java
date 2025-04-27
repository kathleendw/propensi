package b02Propensi.siladu.DTO;
import org.mapstruct.Mapper;
import b02Propensi.siladu.DTO.request.CreatePesananRequestDTO;
import b02Propensi.siladu.model.Pesanan;

@Mapper(componentModel = "spring")
public interface PesananMapper {
     Pesanan createPesananRequestDTOtoPesanan (CreatePesananRequestDTO createPesananRequestDTO);
    
}
