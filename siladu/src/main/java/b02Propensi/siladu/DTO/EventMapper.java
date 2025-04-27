package b02Propensi.siladu.DTO;
import org.mapstruct.Mapper;
import b02Propensi.siladu.model.Event;
import b02Propensi.siladu.DTO.request.CreateEventRequestDTO;
import b02Propensi.siladu.DTO.request.UpdateEventRequestDTO;

@Mapper(componentModel = "spring")
public interface EventMapper {
    
    Event createEventRequestDTOToEvent(CreateEventRequestDTO createEventRequestDTO);
    UpdateEventRequestDTO eventToUpdateEventRequestDTO(Event event);
}
