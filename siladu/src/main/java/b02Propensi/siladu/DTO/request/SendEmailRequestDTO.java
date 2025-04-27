package b02Propensi.siladu.DTO.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class SendEmailRequestDTO {
    public String email;
    public String pesan;
    
}
