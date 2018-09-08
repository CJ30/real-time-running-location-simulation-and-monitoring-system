package demo.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Embeddable;

@Data
@Embeddable
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MedicalInfo {
    // blood flow rate
    private long bfr;

    // first-aid medical index
    private long fmi;

    public MedicalInfo(){
    }

    public MedicalInfo(long bfr, long fmi){
        this.bfr = bfr;
        this.fmi = fmi;
    }

}
