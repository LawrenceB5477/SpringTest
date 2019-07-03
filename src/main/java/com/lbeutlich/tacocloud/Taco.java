package com.lbeutlich.tacocloud;

import com.fasterxml.jackson.annotation.JsonTypeId;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

//add java's bean validation api! hibernate implements it too
@Data
public class Taco {

    private Long id;
    private Date createdAt;

    @NotNull
    @Size(min=5, message = "The name for your taco must be at least 5 characters.")
    private String name;
    @Size(min=1, message = "Select at least one ingredient")
    private List<String> ingredients;
}
