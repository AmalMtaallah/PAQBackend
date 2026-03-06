package projetPAQ.PAQBackend.DTO;

import lombok.Data;
import java.util.List;

@Data
public class SglWithSlDTO {
    private Long sglId;
    private String sglFirstName;
    private String sglLastName;
    private List<UserDTO> slList;

    public SglWithSlDTO(Long sglId, String sglFirstName, String sglLastName, List<UserDTO> slList) {
        this.sglId = sglId;
        this.sglFirstName = sglFirstName;
        this.sglLastName = sglLastName;
        this.slList = slList;
    }
}
