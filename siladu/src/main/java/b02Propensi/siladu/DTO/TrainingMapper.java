package b02Propensi.siladu.DTO;
import org.mapstruct.Mapper;

import b02Propensi.siladu.DTO.request.CreateTrainingRequestDTO;
import b02Propensi.siladu.DTO.request.UpdateTrainingRequestDTO;
import b02Propensi.siladu.DTO.response.ReadTrainingResponseDTO;
import b02Propensi.siladu.model.Training;
@Mapper(componentModel = "spring")
public interface TrainingMapper {
    Training createTrainingRequestDTOtoTraining(CreateTrainingRequestDTO createTrainingRequestDTO);
    Training updateTrainingRequestDTOtoTraining(UpdateTrainingRequestDTO updateTrainingRequestDTO);
    UpdateTrainingRequestDTO trainingToUpdateTrainingRequestDTO(Training training);
    ReadTrainingResponseDTO trainingToReadTrainingResponseDTO(Training training);

}
