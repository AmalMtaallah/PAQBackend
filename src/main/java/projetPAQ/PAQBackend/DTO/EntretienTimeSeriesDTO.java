package projetPAQ.PAQBackend.DTO;

import java.util.Date;

import lombok.Data;

@Data
public class EntretienTimeSeriesDTO {
    private Date date;
    private String type;
    
    public EntretienTimeSeriesDTO(Date date, String type) {
        this.date = date;
        this.type = type;
    }
}
