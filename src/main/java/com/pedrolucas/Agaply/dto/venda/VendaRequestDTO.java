package com.pedrolucas.Agaply.dto.venda;

import com.pedrolucas.Agaply.dto.itemvenda.ItemVendaRequestDTO;
import com.pedrolucas.Agaply.model.ItemVenda;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public record VendaRequestDTO (

        @NotEmpty(message = "A venda deve ter pelo menos um item")
        List<ItemVendaRequestDTO> itens,

        LocalDateTime data


){
}
