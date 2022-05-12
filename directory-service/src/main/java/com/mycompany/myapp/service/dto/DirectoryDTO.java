package com.mycompany.myapp.service.dto;

import javax.validation.constraints.*;

public record DirectoryDTO(@NotNull String path) {
}
