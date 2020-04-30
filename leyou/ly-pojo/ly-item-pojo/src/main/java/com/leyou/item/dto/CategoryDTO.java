package com.leyou.item.dto;

import lombok.Data;
import java.util.Date;

/**
 * 隐藏表结构：我们可以返回dto
 */
@Data
public class CategoryDTO {
	private Long id;
	private String name;
	private Long parentId;
	private Boolean isParent;
	private Integer sort;
}