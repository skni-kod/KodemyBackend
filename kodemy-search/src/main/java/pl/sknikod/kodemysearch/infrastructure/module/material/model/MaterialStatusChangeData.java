package pl.sknikod.kodemysearch.infrastructure.module.material.model;

import lombok.Data;

@Data
public class MaterialStatusChangeData {
	private Long id;
	private MaterialStatus status;
}
